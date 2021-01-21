

$("#searchBtn").click(function(){

    var sDate = document.getElementById('startDate').value; //날짜
    var stime = document.getElementById('startTime').value; //시작시간
    var etime = document.getElementById('endTime').value; //종료시간
    console.log(sDate,stime,etime);
    var sDT = sDate +" "+ stime;
    console.log(sDT);
    var startT = new Date(sDT).getTime();
    console.log(startT);

    sDate = sDate.split("-");
    var smsg = new Date(sDate).getTime(stime); //오늘날짜의 start시간
    var emsg = new Date(sDate).getTime(etime); //오늘날짜의 end시간

//    const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000
//                        : panel.readyTimestamp - panel.refreshIntervalMillis;
//
//    const end = isCreate ? start + (60 * 60) * 1000
//                        : start + panel.refreshIntervalMillis;
});

let commonChartsJs = (function () {
    let chartMap = new Map();
        let scheduleMap = new Map();
        let readyTimestamp = new Date().getTime();
        let defaultIntervalMillis = 60 * 1000;

    function logrenderTable(panel, tableData) {
        //console.log("renderTable :",tableData);
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
                let trAppend = '';
                for(let i = 0; i<dataArray.length; i++){
                    //trAppend += '<td>' + item  + '</td>' + '<td class="logbtn">' + '<input type="button" class="btn btn-md btn-outline-white" value="Log" id=' + "" + '>' + '</td>'; //id값 test.
                    trAppend += '<td>' + item  + '</td>' + '<td class="logbtn">' + '<input type="button" class="btn btn-md btn-outline-white" value="Log" >' + '</td>';
                    return String.prototype.concat('<tr>', trAppend, '</tr>');
                }

            }), '</tbody>');
          $('#container-' + panel.panelId).html(tableBodyHtml);
        //$('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
    }
    function renderTable(panel, tableData) {
            console.log("renderTable tabledata :", tableData);
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
                    let trAppend = '';
                    for (let header of headers) {
                        trAppend += '<td>' + item[header] + '</td>';
                    }
                    return String.prototype.concat('<tr>', trAppend, '</tr>');
                }), '</tbody>');
            $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
        }
    

     function logConvertTableData(data) { // header 와 data 분리.

         if (data === undefined || data.length === 0) {
             return undefined;
         }

         let result = {};
         if (!Array.isArray(data)) {
             data = [data];
         }
         /*로그 하나의 배열로 변환*/
         let valueList = [];
         for(let i=0; i<data.length; i++){ //Object 의 array.length
            for(let j=0; j < Object.keys(data[i]).length; j++){
                valueList = valueList.concat(Object.values(data[i])[j][1]); //log 데이터만 추출.
            }

         }


         result.headers = Object.keys(valueList);
         result.data = valueList.map(value => value);

         return result;
     }

     function convertTableData(data) {
//        if (data === undefined || data.length === 0) {
//            return undefined;
//        }
//
//        let result = {};
//        if (!Array.isArray(data)) {
//            data = [data];
//        }
//        result.headers = Object.keys(data[0]);
//        result.data = data.map(value => value);
//        return result;

         let result = {};
         let header = [];
         let dataList = [];
         element = {};

         for(let [key,value] of data){
            console.log(key);
            console.log(value[1]);
            header.push(key);
            element[key]= value[1];
         }

         dataList.push(element);
         //result.headers = Object.keys(data[0]); //원본 객체의 (data[0]) key 값들을 배열로 리턴.
         //result.data = data.map(value => value);
         result.headers = header;
         result.data = dataList;

         console.log("convertTableData result : ",result);
         return result;
     }

    return {
        createPanel: function (panel) {
                //console.log("panel : ",panel );
                if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                    panel.refreshIntervalMillis = defaultIntervalMillis;
                }
                panel.readyTimestamp = readyTimestamp;
                const panelType = panel.panelType;
                switch (panelType) {
                    case "METRIC_TABLE":
                    case "LOG_METRIC_TABLE":
                        this.getDataByPanel(panel, true)
                            .then(value => this.createTable(panel, value))
                            .then(panel => scheduleMap.set(panel.panelId,
                                setTimeout(commonChartsJs.refreshFunction, panel.refreshIntervalMillis, panel))
                            )
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
                case "LOG_METRIC_TABLE":
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

                const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000 //TODO 조회기준 값으로 변경필요..
                    : panel.readyTimestamp - panel.refreshIntervalMillis;

                const end = isCreate ? start + (60 * 60) * 1000
                    : start + panel.refreshIntervalMillis;
                    //console.log(start,end);

                if (chartQuery.queryType.indexOf("METRIC") > -1) {
                    let uri = chartQuery.apiQuery.indexOf("query_range") > -1
                        ? convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end)
                        : convertApiQuery;
                    return chartQuery.queryType === "PROXY_METRIC"
                        ? this.getFetchRequest("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"))
                        : this.getFetchRequest(apiHost + encodeURI(uri).replace(/\+/g, "%2B"));
                } else {
                    return this.getFetchRequest(encodeURI(convertApiQuery).replace(/\+/g, "%2B"));
                }

//                if (chartQuery.queryType === 'API') {
//                    let uri = chartQuery.apiQuery.indexOf("query_range") > 0
//                        ? convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end)
//                        : convertApiQuery;
//                      return this.getFetchRequest(uri.replace(/\+/g, "%2B"));
////                    return this.getFetchRequest(apiHost + uri.replace(/\+/g, "%2B"));
//                } else {
//                    return this.getFetchRequest(convertApiQuery.replace(/\+/g, "%2B")); //loki API일때도 query range를 사용하기위해 star, end 추가
//                }
            }));
        },
        getQueryRangeTimeNStep: function (chartQuery, start, end) {
            const queryStep = chartQuery.queryStep === undefined || chartQuery.queryStep === ''
                ? 15 : chartQuery.queryStep;
            return String.prototype.concat("&start=", start / 1000, "&end=", end / 1000, "&step=", queryStep);
        },

        getFetchRequest: function (url) {
//            let apirul = encodeURI(url); //request header 오류 발생하여 encodeURI 처리.
            //console.log("getFetchRequest 호출 apirul :",apirul);
            return fetch(url).then(response => { //TODO 여기서 http api를 호출하기때문에 url을 완성시켜야한다. & 파라미터 정의 해야하는지?
                const contentType = response.headers.get("Content-Type");
                if (contentType.indexOf("text/html") >= 0) {
                    return response.text();
                }
                return response.json() //http api => response값
            });
        },
        createTable: function (panel, dataArray) {
            let tableData;
            if (panel.panelType === 'LOG_METRIC_TABLE') {
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
                            if (element === undefined) {
                                element = {};
                                elementValue = {};
                                for (const [key, entry] of Object.entries(value.values)) { //Object.entries는 for in의 순서로 객체자체의 속성 key, value배열을 반환한다.
                                    element[key] = entry; //배얼 1번째 value만 넘기기
                                }
                            }

                            //element[legend] = this.convertValue(parseFloat(value.value[1]).toFixed(1) - 0, panel.yaxisUnit);
                            data.set(key, element);
                        });
                    }
                }
                tableData = logConvertTableData([...data.values()]);
                logrenderTable(panel, tableData);
            } else if (panel.panelType === 'METRIC_TABLE'){
                console.log("api 반환 dataArray :",dataArray);
                let data = new Map();
                    for (let i = 0; i < dataArray.length; i++) {
                        let item = dataArray[i];
                        if (item.data.resultType === 'matrix') {
                            console.warn("unsupported result type=" + item.data.data.resultType);
                        } else {
                            item.data.result.forEach(value => {
                                //const key = Object.values(value.metric).toString();
                                console.log("Object: ",Object.values(value.value));
                                const legend = panel.chartQueries[i].legend;
                                const key = legend;
                                let element = data.get(key);
                                if (element === undefined) {
                                    element = {};
                                    for (const [key, entry] of Object.entries(value.value)) {
                                        element[key] = entry;
                                    }
                                }
                                //element[legend] = this.convertValue(parseFloat(value.value[1]).toFixed(1) - 0, panel.yaxisUnit);
                                console.log("element: ",element);
                                data.set(key, element);
                                console.log("data: ",data);
                            });
                        }
                    }
                    console.log("data : ",data);
                    //tableData = convertTableData([...data.values()]);   //value값만 파라미터로 넘어간다.
                    tableData = convertTableData(data);
                    renderTable(panel, tableData);

            } else if (panel.panelType === 'HTML_TABLE'){
                  tableData = dataArray[0];
              }
            //renderTable(panel, tableData);
            return panel;
        },




    }

}());
