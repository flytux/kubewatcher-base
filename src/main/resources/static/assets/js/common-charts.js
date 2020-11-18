let commonChartsJs = (function () {
    let chartMap = new Map();
    let scheduleMap = new Map();
    let readyTimestamp = new Date().getTime();
    let defaultIntervalMillis = 60 * 1000;
    let gaugeOptions = {
        chart: {
            type: 'solidgauge'
        },
        pane: {
            center: ['50%', '85%'],
            size: '150%',
            startAngle: -90,
            endAngle: 90,
            background: {
                backgroundColor:
                    Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                innerRadius: '60%',
                outerRadius: '100%',
                shape: 'arc'
            }
        },

        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        },
        tooltip: {
            enabled: false
        },
        // the value axis
        yAxis: {
            stops: [
                [0.1, '#55BF3B'], // green
                [0.5, '#DDDF0D'], // yellow
                [0.9, '#DF5353'] // red
            ],
            lineWidth: 0,
            tickWidth: 0,
            minorTickInterval: null,
            tickAmount: 2,
            title: {
                y: -70
            },
            labels: {
                y: 16
            }
        },

        plotOptions: {
            solidgauge: {
                dataLabels: {
                    y: 5,
                    borderWidth: 0,
                    useHTML: true
                }
            }
        }
    }

    function loadSolidGaugeChartData(serise, dataItem) {
        const value = dataItem.value;
        if (value === undefined || value.length < 2) {
            console.warn("failed chart reload // empty reload data")
            return;
        }
        serise.points[0].update(parseFloat(value[1]).toFixed(2) - 0);
    }

    function loadLineChartData(serise, tagetLegend, resultType, dataItem) {
        if (dataItem === undefined || dataItem.length < 2) {
            console.error("failed chart reload // empty reload data")
            return;
        }
        if (Mustache.render(tagetLegend, dataItem.metric) === serise.name) {
            if (resultType === 'matrix') {
                dataItem.values.forEach(arr => {
                    serise.addPoint([
                        arr[0] * 1000,
                        parseFloat(arr[1]).toFixed(2) - 0
                    ], true, true);
                })
            } else {
                dataItem.value.forEach(arr => {
                    serise.addPoint([
                        arr[0] * 1000,
                        parseFloat(arr[1]).toFixed(2) - 0
                    ], true, true);
                })
            }
        }
    }

    function convertSumBadgeData(dataArray) {
        return dataArray.map(item => {
            if (item.data.resultType === 'matrix') {
                console.warn("unsupported result type=" + item.data.data.resultType);
                return 0;
            }
            return item.data.result.map(resultItem =>
                parseInt(resultItem.value[1]));
        }).reduce((value1, value2) => value1 + value2)[0];
    }

    function renderTable(panel, tableData) {
        if (tableData === undefined) {
            $('#container-' + panel.panelId)
                .html('<thead><tr><th>No Result</th></tr></thead>');
            return;
        }

        const headers = tableData.headers;
        const dataArray = tableData.data;
        const tableHeaderHtml = String.prototype.concat('<thead><tr>',
            headers.map(value => '<th>' + value + '</th>').join(''),
            '</tr></thead>');
        const tableBodyHtml = String.prototype.concat('<tbody>',
            dataArray.map(item => {
                let trAppend = '';
                for (let header of headers) {
                    trAppend += '<td>' + item[header] + '</td>';
                }
                return String.prototype.concat('<tr>', trAppend, '</tr>');
            }), '</tbody>');
        $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
    }

    function convertTableData(data) {
        if (data === undefined || data.length === 0) {
            return undefined;
        }

        let result = {};
        if (!Array.isArray(data)) {
            data = [data];
        }
        result.headers = Object.keys(data[0]);
        result.data = data.map(value => value);
        return result;
    }


    return {
        createPanel: function (panel) {
            if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                panel.refreshIntervalMillis = defaultIntervalMillis;
            }
            panel.readyTimestamp = readyTimestamp;
            const panelType = panel.panelType;
            switch (panelType) {
                case "CHART":
                    if (panel.chartQueries.length > 0) {
                        this.getDataByPanel(panel, true)
                            .then(responses => this.convertSeries(panel, responses))
                            .then(series => this.getChartData(panel, series))
                            .then(chartOptions => this.renderChart(panel, chartOptions));
                    }
                    break;
                case "TABLE":
                    this.getDataByPanel(panel, true)
                        .then(value => this.createTable(panel, value))
                        .then(panel => scheduleMap.set(panel.panelId,
                            setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel))
                        )
                    break;
                case "BADGE":
                    this.getDataByPanel(panel, true)
                        .then(value => this.createBadge(panel, value))
                        .then(panel => scheduleMap.set(panel.panelId,
                            setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel))
                        );
                    break;
                default:
                    console.warn("unsupported panel type");
            }
        },

        createBadge: function (panel, dataArray) {
            const badgeData = convertSumBadgeData(dataArray);
            $('#container-' + panel.panelId).text(badgeData);
            return panel;
        },
        createTable: function (panel, dataArray) {
            let data = new Map();
            for (let i = 0; i < dataArray.length; i++) {
                let item = dataArray[i];
                if (item.data.resultType === 'matrix') {
                    console.warn("unsupported result type=" + item.data.data.resultType);
                } else {
                    item.data.result.forEach(value => {
                        const key = Object.values(value.metric).toString();
                        const legend = item.legend;
                        let element = data.get(key);
                        if (element === undefined) {
                            element = {};
                            for (const [key, entry] of Object.entries(value.metric)) {
                                element[key] = entry;
                            }
                        }
                        element[legend] = parseFloat(value.value[1]).toFixed(2) - 0;
                        data.set(key, element);
                    });
                }
            }
            const tableData = convertTableData([...data.values()]);
            renderTable(panel, tableData);
            return panel;
        },

        refreshFunction: function (panel) {
            const panelType = panel.panelType;
            panel.readyTimestamp = panel.readyTimestamp + panel.refreshIntervalMillis;
            let timerId = setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel);
            scheduleMap.set(panel.panelId, timerId);
            switch (panelType) {
                case "CHART":
                    commonChartsJs.refreshChart(panel);
                    break;
                case "TABLE":
                    commonChartsJs.getDataByPanel(panel)
                        .then(value => commonChartsJs.createTable(panel, value));
                    break;
                case "BADGE":
                    commonChartsJs.getDataByPanel(panel)
                        .then(value => commonChartsJs.createBadge(panel, value));
                    break;
                default:
                    console.warn("unsupported panel type");
            }
        },

        refreshChart: function (panel) {
            const chart = chartMap.get(panel.panelId);
            const chartSeries = chart.series;
            const panelType = panel.panelType;
            if (panel.chartQueries.length > 0) {
                this.getDataByPanel(panel, false)
                    .then(responses => this.convertSeries(panel, responses))
                    .then(series => {
                        const start = new Date().getTime();
                        switch (panel.chartType) {
                            case "line":
                                const seriesMap = new Map(series.map(i => [i.name, i]));
                                console.log("refreshChart seriesMap", panel.panelId, panel.title, (new Date().getTime()) - start);
                                for (let chartSeriesItem of chartSeries) {
                                    const seriesData = seriesMap.get(chartSeriesItem.name);
                                    // const start = new Date().getTime();
                                    if (seriesData.data.length) {
                                        seriesData.data.forEach((arr, index) => {
                                            chartSeriesItem.addPoint(arr, index === seriesData.data.length - 1, true);
                                        })
                                    }
                                    // console.log("refreshChart addPoint", panel.panelId, seriesData.data, panel.title, (new Date().getTime()) - start);
                                    seriesMap.delete(chartSeriesItem.name);
                                }
                                if (seriesMap.size > 0) {
                                    seriesMap.forEach((value, key) => chart.addSeries(value));
                                }
                                break;
                            case "solidgauge":
                                if (series[0].data.lengrh) {
                                    chartSeries[0].points[0].update(series[0].data[0]);
                                }
                                break;
                            default:
                                console.warn("unsupported chart type");
                        }
                        console.log("refreshChart completed", panel.panelId, panel.title, (new Date().getTime()) - start);
                    });
            }
        },

        getDataByPanel: function (panel, isCreate) {
            return Promise.all(panel.chartQueries.map(chartQuery => {
                const convertApiQuery = commonVariablesJs.convertVariableApiQuery(chartQuery.apiQuery);

                const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000
                    : panel.readyTimestamp - panel.refreshIntervalMillis;

                const end = isCreate ? start + (60 * 60) * 1000
                    : start + panel.refreshIntervalMillis;

                if (chartQuery.apiQuery.indexOf("query_range") > 0) {
                    return request = this.getFetchRequest(convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end));
                } else {
                    return request = this.getFetchRequest(convertApiQuery);
                }
            }));
        },

        convertSeries: function (panel, responses) {
            const chartQueries = panel.chartQueries;
            const series = chartQueries.flatMap(function(chartQuery, index) {
                const responseElement = responses[index];
                if (responseElement.status === 'error') {
                    return {
                        "name": "N/A",
                        "data": [0, 0]
                    };
                }
                chartQuery.resultType = responseElement.data.resultType;
                return responseElement.data.result.flatMap(value => {
                    return {
                        "name": Mustache.render(chartQuery.legend, value.metric),
                        "data": chartQuery.resultType === 'matrix'
                            ? value.values.map(arr => [arr[0] * 1000, parseFloat(arr[1]).toFixed(2) - 0])
                            : [parseFloat(value.value[1]).toFixed(2) - 0]
                    };
                });
            });
            return series;
        },

        getChartData: function (panel, series) {
            const type = panel.chartType;
            let data;
            switch (type) {
                case "line":
                    data = this.getLineChartData(panel, series);
                    break;
                case "solidgauge":
                    data = this.getGaugeChartData(panel, series);
                    break;
                default:
                    console.warn("unsupported chart type");
            }
            return data;
        },
        getLineChartData: function (panel, series) {
            return {
                chart: {
                    type: panel.chartType.toLowerCase(),
                    styledMode: false,
                    events: {
                        load: function () {
                            scheduleMap.set(panel.panelId,
                                setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel));
                        }
                    }
                },
                credits: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                yAxis: {
                    // min: panel.yaxisMin,
                    // max: panel.yaxisMax,
                    title: {
                        text: panel.yaxisLabel
                    },
                    labels: {
                        // format: '{value} '+ panel.yaxisUnit,
                        formatter: function () {
                            if (Math.abs(this.value) > 1000) {
                                return Highcharts.numberFormat(this.value / 1024, 0) + " K" + panel.yaxisUnit;
                            }
                            return this.value + ' ' + panel.yaxisUnit;
                        }
                    }
                },
                xAxis: {
                    type: panel.xaxisMode
                },
                title: null,
                series: series
            }
        },
        getGaugeChartData: function (panel, series) {
            series.forEach(item => {
                const valueHtml = item.name === 'N/A' ? '<span style="font-size:25px">' + item.name + '</span><br/>'
                    : '<span style="font-size:25px">{y}</span><br/>';

                item.dataLabels = {
                    format:
                        '<div style="text-align:center">' +
                        valueHtml +
                        '<span style="font-size:10px;opacity:0.4">' +
                        panel.title + ' [' + panel.yaxisUnit + ']' +
                        '</span>' +
                        '</div>'
                };
                item.tooltip = {
                    valueSuffix: ' ' + panel.yaxisUnit
                };
            })

            return {
                chart: {
                    type: panel.chartType.toLowerCase(),
                    styledMode: false,
                    events: {
                        load: function () {
                            scheduleMap.set(panel.panelId,
                                setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel));
                        }
                    }
                },
                title: panel.title,
                yAxis: {
                    min: parseFloat(panel.yaxisMin),
                    max: parseFloat(panel.yaxisMax),
                    title: {
                        text: panel.yaxisLabel
                    }
                },
                series: series
            }
        },

        renderChart: function (panel, chartData) {
            const type = chartData.chart.type;
            switch (type) {
                case "line":
                    this.renderLineHighChart(panel, chartData);
                    break;
                case "solidgauge":
                    this.renderGaugeHighChart(panel, chartData);
                    break;
                default:
                    console.warn("unsupported chart type");
            }
        },
        renderLineHighChart: function (panel, chartData) {
            const panelId = panel.panelId;
            const chart = new Highcharts.chart('container-' + panelId, chartData);
            chartMap.set(panelId, chart);
        },
        renderGaugeHighChart: function (panel, chartData) {
            const panelId = panel.panelId;
            const chart = new Highcharts.chart('container-' + panelId, Highcharts.merge(gaugeOptions, chartData));
            chartMap.set(panelId, chart);
        },

        getFetchRequest: function (uri) {
            return fetch(apiHost + uri.replace(/\+/g, "%2B")).then(response => response.json());
        },

        getQueryRangeTimeNStep: function (chartQuery, start, end) {
            const queryStep = chartQuery.queryStep === undefined || chartQuery.queryStep === ''
                ? 15 : chartQuery.queryStep;
            return String.prototype.concat("&start=", start / 1000, "&end=", end / 1000, "&step=", queryStep);
        },
        getChart: function (id) {
            return chartMap.get(id);
        },
        removeChart: function (panel) {
            const id = panel.panelId;
            const scheduleId = scheduleMap.get(id);
            clearTimeout(scheduleId);
            scheduleMap.delete(id)
            if (panel.panelType === 'CHART') {
                const chart = chartMap.get(id);
                chart.destroy();
                chartMap.delete(id);
            }
        },
        resetReadyTimestamp: function () {
            readyTimestamp = new Date().getTime();
        },


    }
}());


