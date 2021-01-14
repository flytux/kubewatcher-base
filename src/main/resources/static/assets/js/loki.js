



let commonChartsJs = (function () {
    let chartMap = new Map();
        let scheduleMap = new Map();
        let readyTimestamp = new Date().getTime();
        let defaultIntervalMillis = 60 * 1000;

        /*ServiceMonitorByLokiDaoImpl 참고*/
//        let data_day = 24 * 60 * 60 * 1000 * 1000000;
//        let date_90day = 90 * DATE_DAY;
//        let date_5m = 5 * 60 * 1000 * 1000000;
//
//        //let req_time_term = 1000l; // 1000l ???
//        let max_query_limit = 5000;

    function renderTable(panel, tableData) {
        //console.log("3. renderTable tabledata :", tableData); //undefined 발생.
        if (tableData === undefined) {
            $('#container-' + panel.panelId)
                .html('<thead><tr><th>No Result</th></tr></thead>');
            return;
        }

        if (panel.panelType === 'HTML_TABLE') {
            const tableHtml = tableData.substring(tableData.indexOf('<thead>'),
                tableData.indexOf('</tbody>') + 8);
            $('#container-' + panel.panelId)
                .html(tableHtml);
            return;
        }

        const headers = tableData.headers;
        const dataArray = tableData.data;

        const tableHeaderHtml = String.prototype.concat('<thead><tr>',
            headers.map(value => '<th>' + value + '</th>').join(''),
            '</tr></thead>');
        const tableBodyHtml = String.prototype.concat('<tbody>',
            dataArray.map(item => {
                //console.log(item);
                let trAppend = '';
                for(let i = 0; i<dataArray.length; i++){
                    trAppend += '<td>' + item  + '</td>';
                    return String.prototype.concat('<tr>', trAppend, '</tr>');
                }

            }), '</tbody>');
          $('#container-' + panel.panelId).html(tableBodyHtml);
        //$('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
    }

     function convertTableData(data) { // header 와 data 분리.
         if (data === undefined || data.length === 0) {
             return undefined;
         }

         let result = {};
         if (!Array.isArray(data)) {
             data = [data];
         }
         //console.log("2 convertTableData data :",data);
         let valueList = [];
         for(let i=0; i<data.length; i++){ //Object 의 array.length
            for(let j=0; j < Object.keys(data[i]).length; j++){
                //console.log(Object.values(data[i])[j]);
                valueList = valueList.concat(Object.values(data[i])[j][1]); //log 데이터만 추출.
            }

         }


         result.headers = Object.keys(valueList);
         result.data = valueList.map(value => value);

         //result.headers = Object.keys(data);
         //result.data = data.map(value => value);

         return result;
     }

     //스파크라인 차트 최초 생성시 마지막 데이터값 그려주는 함수
     function drawOneLabel(chart, series, unit) {
         let render = chart.renderer;
         let v = series[0].points[series[0].points.length - 1];
         let lastVal = addSparkUnitFormat(v.y, unit);
         chart.customText = render.label(lastVal, chart.plotWidth / 2, chart.plotHeight / 1.5).css({
             color: '#FFFFFF',
             fontSize: 'calc(' + chart.plotWidth + '* 0.01em)'
         }).attr({
             zIndex: 5,
             align: 'center'
         }).add();
     }

     //스파크라인 차트용 포맷
     function addSparkUnitFormat(str, unit) {
         str = String(str);
         if (unit == '%') {
             var cFloat = parseFloat(str);
             cFloat = cFloat.toFixed(1);
             str = cFloat+'%';
         } else if (unit == 'float'){
             var minus = str.substring(0, 1);
             str = str.replace(/[^\d]+/g, '');
             str = str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
             if (minus == "-") str = "-" + str;
         } else if (unit == 'bytes') {
             var bFloat = parseFloat(str);
             var mbValue = bFloat / 1024 / 1024;
             mbValue = mbValue.toFixed(1);
             str = mbValue+'MB';
         } else if (unit == 'sec') {
             var sFloat = parseFloat(str);
             sFloat = sFloat.toFixed(1);
             str = sFloat+' s'
         } else {
             str = str + unit;
         }
         return str;
     }

    return {
        createPanel: function (panel) {
                if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                    panel.refreshIntervalMillis = defaultIntervalMillis;
                }
                panel.readyTimestamp = readyTimestamp;
                const panelType = panel.panelType;
                switch (panelType) {
                    case "METRIC_TABLE":
                    case "HTML_TABLE":
                        this.getDataByPanel(panel, true)
                            .then(value => this.createTable(panel, value))
                            .then(panel => scheduleMap.set(panel.panelId,
                                setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel))
                            )
                        break;
                    case "CHART":
                        if (panel.chartQueries.length > 0) {
                            this.getDataByPanel(panel, true)
                                .then(responses => this.convertSeries(panel, responses))
                                .then(series => this.getChartData(panel, series))
                                .then(chartOptions => this.renderChart(panel, chartOptions));
                        }
                        break;

                }
            },
        refreshFunction: function (panel) {
            const panelType = panel.panelType;
            panel.readyTimestamp = panel.readyTimestamp + panel.refreshIntervalMillis;
            let timerId = setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel);
            scheduleMap.set(panel.panelId, timerId);
            switch (panelType) {

                case "METRIC_TABLE":
                case "HTML_TABLE":
                    commonChartsJs.getDataByPanel(panel)
                        .then(value => commonChartsJs.createTable(panel, value));
                    break;
                case "CHART":
                    if (panel.chartQueries.length > 0) {
                        this.getDataByPanel(panel, true)
                            .then(responses => this.convertSeries(panel, responses))
                            .then(series => this.getChartData(panel, series))
                            .then(chartOptions => this.renderChart(panel, chartOptions));
                    }
                    break;
            }
        },
        getDataByPanel: function (panel, isCreate) {
            return Promise.all(panel.chartQueries.map(chartQuery => {
                const convertApiQuery = commonVariablesJs.convertVariableApiQuery(chartQuery.apiQuery); //return값으로 api url 받아옴.

                const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000
                    : panel.readyTimestamp - panel.refreshIntervalMillis;

                const end = isCreate ? start + (60 * 60) * 1000
                    : start + panel.refreshIntervalMillis;
                if (chartQuery.queryType === 'API') { //TODO loki 에서도 query 와 query_range 구분처리를해야하는지?
                    let uri = chartQuery.apiQuery.indexOf("query_range") > 0
                        ? convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end)
                        : convertApiQuery;
                      return this.getFetchRequest(uri.replace(/\+/g, "%2B"));
//                    return this.getFetchRequest(apiHost + uri.replace(/\+/g, "%2B"));
                } else {
                    return this.getFetchRequest(convertApiQuery.replace(/\+/g, "%2B")); //loki API일때도 query range를 사용하기위해 star, end 추가
                }
            }));
        },
        getQueryRangeTimeNStep: function (chartQuery, start, end) {
            const queryStep = chartQuery.queryStep === undefined || chartQuery.queryStep === ''
                ? 15 : chartQuery.queryStep;
            return String.prototype.concat("&start=", start / 1000, "&end=", end / 1000, "&step=", queryStep);
        },

        getFetchRequest: function (url) {
            let apirul = encodeURI(url); //request header 오류 발생하여 encodeURI 처리.
            //console.log("getFetchRequest 호출 apirul :",apirul);
            return fetch(apirul).then(response => {
                const contentType = response.headers.get("Content-Type");
                if (contentType.indexOf("text/html") >= 0) {
                    return response.text();
                }
                return response.json() //http api => response값
            });
        },
        createTable: function (panel, dataArray) {
            let tableData;
            if (panel.panelType === 'METRIC_TABLE') {
                let data = new Map();
                for (let i = 0; i < dataArray.length; i++) {
                    let item = dataArray[i];
                        //console.log("1. item:", item);
                    if (item.data.resultType === 'matrix') {
                        console.warn("unsupported result type=" + item.data.data.resultType);
                    } else {
                        item.data.result.forEach(value => {
                            const key = Object.values(value.stream).toString();
                            const legend = panel.chartQueries[i].legend;
                            let element = data.get(key);
                            let logdate;
                            if (element === undefined) {
                                element = {};
                                elementValue = {};
                                for (const [key, entry] of Object.entries(value.values)) { //Object.entries는 for in의 순서로 객체자체의 속성 key, value배열을 반환한다.
                                    element[key] = entry; //배얼 1번째 value만 넘기기
                                }
                            }
                            //element[legend] = parseFloat(value.value[1]).toFixed(1) - 0;
                            data.set(key, element);
                        });
                    }
                }
                tableData = convertTableData([...data.values()]);

            } else if (panel.panelType === 'HTML_TABLE'){
                tableData = dataArray[0];
            }


            renderTable(panel, tableData);

            return panel;
        },

        /*Total Count*/
        convertSeries: function (panel, responses) {
            //console.log("responses",responses);
            if (panel.chartType === 'counttilemap') {
                return this.convertCountTileMapChartSeries(panel, responses);
            }

            const chartQueries = panel.chartQueries;
            const series = chartQueries.flatMap(function (chartQuery, index) {
                const responseElement = responses[index];
                if (responseElement.status === 'error' || !responseElement.data.result.length) {
                    return panel.chartType.indexOf('gauge') > 0 ? {
                        "name": "N/A",
                        "data": [0, 0]
                    } : undefined;
                }
                chartQuery.resultType = responseElement.data.resultType;
                return responseElement.data.result.flatMap(value => {
                    return {
                        "name": Mustache.render(chartQuery.legend, value.metric),
                        "data": chartQuery.resultType === 'matrix'
                            ? value.values.map(arr => [arr[0] * 1000, parseFloat(arr[1]).toFixed(1) - 0])
                            : [parseFloat(value.value[1]).toFixed(1) - 0]
                    };
                });
            }).filter(value => value !== undefined);
            return series;
        },
        getChartData: function (panel, series) { //TODO 현재 panelType : sparkline 인데 이건 prometheus 이고... loki의 response값을 어떻게 가공해야 보여줄수있을지?
            const type = panel.chartType;
            //console.log(type);
            let data;
            switch (type) {
                case "area":
                case "line":
                    data = this.getLineChartData(panel, series);
                    break;
                case "sparkline":
                    data = this.getSparkLineChartData(panel, series);
                    break;
                case "solidgauge":
                    data = this.getGaugeChartData(panel, series);
                    break;
                case "multiplegauge":
                    data = this.getMultipleGaugeChartData(panel, series);
                    break;
                case "bar":
                    data = this.getBarChartData(panel, series);
                    break;
                case "scatter":
                    data = this.getScatterChartData(panel, series);
                    break;
                case "counttilemap":
                    data = this.getTileMapChartData(panel, series);
                    break;
                default:
                    console.warn(type, "unsupported chart type");
            }
            return data;
        },
        //스파크라인 차트 --> 차트 타입은 기본형인 'area'
        getSparkLineChartData: function (panel, series) {
            //console.log("getSparkLineChartData:",panel ,series)
            return {
                chart: {
                    type: 'area',
                    styledMode: false,
                    backgroundColor: 'transparent',
                    events: {
                        load: function () {
                            const chart = this;
                            const series = this.series;
                            //현재 값을 스파크라인 판넬 중앙에 표시하고, 리프레시할때 마다 업데이트하기 위해 customText renderer 사용
                            //loadSparkLineChartData 메소드에서 customText트 변수 업데이트
                            drawOneLabel(chart, series, panel.yaxisUnit);
                            scheduleMap.set(panel.panelId,
                                setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel));
                        }
                    },
                    margin: [40, 0, 0, 0],
                    style: {
                        overflow: 'visible'
                    },
                    skipClone: true
                },
                title: {
                    text: panel.title,
                    style: {
                        color: '#bebebe',
                        fontWeight: 'bold'
                    }
                },
                credits: {
                    enabled: false
                },
                xAxis: {
                    labels: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    startOnTick: false,
                    endOnTick: false,
                    tickPositions: []
                },
                yAxis: {
                    min: 0,
                    max: 100,
                    endOnTick: false,
                    startOnTick: false,
                    labels: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    tickPositions: []
                },
                plotOptions: {
                    series: {
                        enableMouseTracking: false,
                        animation: false,
                        lineWidth: 2,
                        shadow: false,
                        color: '#256eb0',
                        marker: {
                            enabled: false
                        },
                        showInNavigator: true,
                        fillOpacity: 0.2
                    }
                },
                tooltip: {
                    style: {
                        display: "none",
                    }
                },
                legend: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                series: series
            }
        },
        renderChart: function (panel, chartData) {
            console.log(chartData);
            if (chartData === undefined) {
                console.log(panel.panelId, panel.title);
                return;
            }
            // const type = chartData.chart.type;
            const type = panel.chartType;
            switch (type) {
                case "area":
                case "line":
                    this.renderLineHighChart(panel, chartData);
                    break;
                case "multiplegauge":
                case "solidgauge":
                    this.renderGaugeHighChart(panel, chartData);
                    break;
                case "scatter":
                case "counttilemap":
                case "tilemap":
                case "bar":
                case "sparkline":
                    this.renderCommonHighChart(panel, chartData);
                    break;
                default:
                    console.warn(type, "unsupported chart type");
            }
        },
        renderCommonHighChart: function (panel, chartData) {
            const panelId = panel.panelId;
            const chart = new Highcharts.chart('container-' + panelId, chartData);
            chartMap.set(panelId, chart);
        },
    }

}());
