let commonChartsJs = (function () {
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
            if (item.data.data.resultType === 'matrix') {
                console.warn("unsupported result type=" + item.data.data.resultType);
                return 0;
            }
            return item.data.data.result.map(resultItem =>
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
                    if (Array.isArray(panel.chartQueries) && panel.chartQueries.length > 0) {
                        this.getDataByPanel(panel)
                            .then(value => this.createCharts(panel, value));
                    }
                    break;
                case "TABLE":
                    this.getDataByPanel(panel)
                        .then(value => this.createTable(panel, value))
                        .then(panel => {
                            setInterval(function () {
                                commonChartsJs.getDataByPanel(panel)
                                    .then(value => commonChartsJs.createTable(panel, value));
                            }, panel.refreshIntervalMillis);
                        });
                    break;
                case "BADGE":
                    this.getDataByPanel(panel)
                        .then(value => this.createBadge(panel, value))
                        .then(panel => {
                            setInterval(function () {
                                commonChartsJs.getDataByPanel(panel)
                                    .then(value => commonChartsJs.createBadge(panel, value));
                            }, panel.refreshIntervalMillis);
                        });
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
                if (item.data.data.resultType === 'matrix') {
                    console.warn("unsupported result type=" + item.data.data.resultType);
                } else {
                    item.data.data.result.forEach(value => {
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

        createCharts: function (panel, chartQueries) {
            let series = [];
            for (let i = 0; i < chartQueries.length; i++) {
                const item = chartQueries[i];
                let chartData = chartQueries[i].data;
                item.data = undefined;
                item.intervalMillis = panel.refreshIntervalMillis;

                if (chartData.data.resultType === 'matrix') {
                    chartData.data.result.forEach(value => {
                        series.push({
                            "name": Mustache.render(chartQueries[i].legend, value.metric),
                            "data": value.values.map(arr => [arr[0] * 1000, parseFloat(arr[1]).toFixed(2) - 0]),
                            "custom": item
                        })
                    })
                } else {
                    chartData.data.result.forEach(value => {
                        series.push({
                            "name": Mustache.render(chartQueries[i].legend, value.metric),
                            "data": [parseFloat(value.value[1]).toFixed(2) - 0],
                            "custom": item
                        });
                    })
                }
            }
            const chartData = this.getChartData(panel, series, panel.refreshIntervalMillis);
            if (chartData === undefined) {
                console.warn("empty chartData");
                return;
            }
            this.renderChart(panel.panelId, chartData);
        },


        getChartData: function (panel, series, refreshIntervalMillis) {
            const type = panel.chartType;
            let data;
            switch (type) {
                case "line":
                    data = this.getLineChartData(panel, series, refreshIntervalMillis);
                    break;
                case "solidgauge":
                    data = this.getGaugeChartData(panel, series, refreshIntervalMillis);
                    break;
                default:
                    console.warn("unsupported chart type");
            }
            return data;
        },
        getLineChartData: function (panel, series, refreshIntervalMillis) {
            return {
                chart: {
                    type: panel.chartType.toLowerCase(),
                    styledMode: false,
                    events: {
                        load: function () {
                            const series = this.series;
                            setInterval(function () {
                                commonChartsJs.refreshChart(series);
                            }, refreshIntervalMillis);
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
        getGaugeChartData: function (panel, series, refreshIntervalMillis) {
            series.forEach(item => {
                item.dataLabels = {
                    format:
                        '<div style="text-align:center">' +
                        '<span style="font-size:25px">{y}</span><br/>' +
                        '<span style="font-size:12px;opacity:0.4">' +
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
                            const series = this.series;
                            setInterval(function () {
                                commonChartsJs.refreshChart(series);
                            }, refreshIntervalMillis);
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
        renderChart: function (id, chartData) {
            const type = chartData.chart.type;
            switch (type) {
                case "line":
                    this.createLineHighChart(id, chartData);
                    break;
                case "solidgauge":
                    this.createGaugeHighChart(id, chartData);
                    break;
                default:
                    console.warn("unsupported chart type");
            }
        },
        createLineHighChart: function (id, chartData) {
            return new Highcharts.chart('container-' + id, chartData);
        },
        createGaugeHighChart: function (id, chartData) {
            return new Highcharts.chart('container-' + id, Highcharts.merge(gaugeOptions, chartData));
        },
        getDataByPanel: function (panel) {
            return Promise.all(panel.chartQueries.map((chartQuery) => {
                let request;
                chartQuery.timesptamp = panel.readyTimestamp - (60 * 60) * 1000;
                if (chartQuery.apiQuery.indexOf("query_range") > 0) {
                    request = this.getRequest(chartQuery.apiQuery + this.getQueryRangeTimeNStep(chartQuery, (60 * 60) * 1000));
                } else {
                    request = this.getRequest(chartQuery.apiQuery);
                }
                return request
                    .then(response => {
                        chartQuery.data = response.data;
                        return chartQuery;
                    })
                    .catch(error => {
                        console.error(panel.panelId + error);
                    });
            }));
        },

        refreshChart: function (series) {
            Promise.all(series.map((item) => {
                const chartQuery = item.options.custom;
                let request;
                if (chartQuery.apiQuery.indexOf("query_range") > 0) {
                    request = this.getRequest(chartQuery.apiQuery + this.getQueryRangeTimeNStep(chartQuery, chartQuery.intervalMillis));
                } else {
                    request = this.getRequest(chartQuery.apiQuery);
                }
                return request
                    .then(response => {
                        const chartData = response.data;
                        const resultType = chartData.data.resultType;
                        if (item.chart.types.includes('solidgauge')) {
                            chartData.data.result.forEach(cItem => loadSolidGaugeChartData(item, cItem));
                        } else if (item.chart.types.includes('line')) {
                            chartData.data.result.forEach(cItem => loadLineChartData(item, chartQuery.legend, resultType, cItem));
                        } else {
                            console.error('unsupported chart type // type=' + item.chart.types);
                        }
                    })
                    .catch(error => {
                        console.error(item.name, error);
                    });
            }))
        },

        getRequest: function (uri) {
            return axios.get(apiHost + uri.replace(/\+/g, "%2B"));
        },

        getQueryRangeTimeNStep: function (chartQuery, intervalMillis) {
            const start = chartQuery.timesptamp;
            const end = start + intervalMillis;
            chartQuery.timesptamp = end;
            const queryStep = chartQuery.queryStep === undefined || chartQuery.queryStep === ''
                ? 15 : chartQuery.queryStep;
            return String.prototype.concat("&start=", start / 1000, "&end=", end / 1000, "&step=", queryStep);
        },

    }
}());


