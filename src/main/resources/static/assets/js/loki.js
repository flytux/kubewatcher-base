
$("#searchBtn").click(function(){

    var sDate = document.getElementById('startDate').value; //날짜
    var stime = document.getElementById('startTime').value; //시작시간
    var etime = document.getElementById('endTime').value; //종료시간
    console.log("sDate :",sDate, "stime :",stime );
    var sDT = sDate +" "+ stime;

    var startT = new Date(sDT).getTime();

    sDate = sDate.split("-");
    var smsg = new Date(sDate).getTime(stime); //오늘날짜의 start시간
    var emsg = new Date(sDate).getTime(etime); //오늘날짜의 end시간
//    const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000
//                        : panel.readyTimestamp - panel.refreshIntervalMillis;
//
//    const end = isCreate ? start + (60 * 60) * 1000
//                        : start + panel.refreshIntervalMillis;
});

var obj = {};
var loglist = {};

//direction=BACKWARD&limit=1000&query={container_name="", pod_name=~"", stream=~"stderr|stdout"} |~ "(?i)error"&start=1611300200.412&end=1611303800.412&step=15
$(document).on("click", ".errtd", function(){

    var containerId= $(this).children()[0].id; //renderTable 함수에서 ID값 부여
    var uri = "/loki/api/v1/query_range?direction=BACKWARD&query={container=~"+ '"' + containerId +'"' + "} |= " +'"' +"error"+ '"';

    obj.chartQueries[0].apiQuery = uri;

    switch (obj.panelType) {
        case "LOG_METRIC_TABLE":
            lokiJs.getDataByPanel(obj, true)
                .then(value => lokiJs.createTable(obj, value))
            break;
    }
});

$(document).on("click", ".logbtn", function(){ //.logbtn     modal log 버튼 클릭.

    var tdArr = $(this).parent().parent().children();
    var total = $(this).parent().parent().children().length;
    let element = {};
    var pod = "";
    var container = "";
//    var s = ",";

    $.each(tdArr,function(index,item){
        var key = $(item).attr("name");
        var value = $(item).text();
//        if(key != undefined){ //전체의 값이 필요할때 사용.
//            element[key]= value;
//        }
        if($(item).attr("name") == "pod"){
            pod = $(item).text();
        }
        if($(item).attr("name") == "container"){
            container = $(item).text();
        }

//        if(key == undefined){
//
//        }else if(index != total -1 ){
//            element[key]= value;
//            test += key + "=" +'"'+ value + '"'+s;
//        }else if(index == total -1){
//            test += key + "=" +'"'+ value + '"'
//        }

        /*if(index == total -1){
            test += key + "=" +'"'+ value + '"'
        }*/

    });
    var start = new Date().setHours(0,0,0,0,0,0,0,0); //자정의 시간
    var end = new Date().setHours(23,59,59,0,0,0,0,0);
    var startTime = start.toString();
    var endTime = end.toString();
    startTime = startTime.padEnd(19,"0");
    endTime = endTime.padEnd(19,"0"); //loki query start, end 시간기준을 nanoseconds로 잡아야지 결과값이 출력됨..

    var uriStart = "/loki/api/v1/query_range?direction=BACKWARD&query={";
    var uriEnd = "} |="+'"'+"error"+'"'+"&start="+startTime+"&end="+endTime+"&step=60"; //&start="+start+"&end="+end+"&step=60"
    var uri = uriStart + "container=" +'"' + container + '"' + ",pod=" +'"' + pod + '"' + uriEnd;

    //var response = lokiJs.getFetchRequest("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"));
    fetch("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"))
        .then((response) => response.json())
        .then((data) => logModalTable(data.data));



});

function requestQuery(){


}

function logModalTable(tableData){
    $('#logModal').modal(); //step1 : modal 호출.

    var data = tableData.result[0];
    console.log("data :",data);
    $('#serviceName').text(data.stream.pod);
    var dataArray = [];
    for(let i = 0; i<data.values.length; i++){
        console.log("valuelist :",data.values[i][1]);
        dataArray.push(data.values[i][1]);
    }
    console.log(dataArray);

    if(dataArray === undefined){
        $('#logModalTable')
            .html('<thead><tr><th>No Result</th></tr></thead>')
        return ;
    }
    /*const tableHeaderHtml = String.prototype.concat('<thead><tr>',
        headers.map(value => '<th>' + value + '</th>').join(''), //header 값은 service id값으로.
        '</tr></thead>');*/

    const tableBodyHtml = String.prototype.concat('<tbody>',
        dataArray.map(item => {
            let trAppend = '';
            for(let i = 0; i<dataArray.length; i++){
                trAppend += '<td>' + item  + '</td>';
                return String.prototype.concat('<tr>', trAppend, '</tr>');
            }
        }), '</tbody>');

    $('#logModalTable').html(tableBodyHtml);
}

let lokiJs = (function () {
    let chartMap = new Map();
        let scheduleMap = new Map();
        let readyTimestamp = new Date().getTime();
        let defaultIntervalMillis = 60 * 1000;

    function logrenderTable(panel, tableData) {
        //console.log("logrenderTable :",tableData);
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
        const dataArray = tableData.data; //TODO dataarray값을 log버튼 클릭시 query문에 사용하여 로그 값을 받아오게끔?

        const tableHeaderHtml = String.prototype.concat('<thead><tr>',
            headers.map(value => '<th>' + value + '</th>').join(''),
            '</tr></thead>');
        const tableBodyHtml = String.prototype.concat('<tbody>',
            dataArray.map(item => {
                let trAppend = '';
                for (let header of headers) {
                    if(header == "Log"){
                        trAppend += '<td>' + '<input type="button" class="logbtn btn btn-md btn-outline-white" value="Log" id="'+ item.pod  +'" >' + '</td>';
                    }else{
                        trAppend += '<td name="'+ header +'">' + item[header]  + '</td>';
                    }
                }
                return String.prototype.concat('<tr class="logLabel">', trAppend, '</tr>');
//                for(let i = 0; i<dataArray.length; i++){
//                    trAppend += '<td>' + item  + '</td>' + '<td class="logbtn">' + '<input type="button" class="btn btn-md btn-outline-white" value="Log" >' + '</td>'; //log를 하나의 테이블루 뿌릴때사용
//                    return String.prototype.concat('<tr>', trAppend, '</tr>'); //로그를 하나의 테이블로 보여줄때 사용.
//                }

            }), '</tbody>');
        //$('#container-' + panel.panelId).html(tableBodyHtml);
        $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
    }
    function renderTable(panel, tableData) {
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
                headers.map(value => '<th>' + value + '</th>').join(''), //모든요소들을 연결해 하나의 문자열로 만듬.
                '</tr></thead>');
            const tableBodyHtml = String.prototype.concat('<tbody>', //TODO id or class 부여해서 클릭하면 에러리스트를 호출해야함.
                dataArray.map(item => {
                    let trAppend = '';
                    for (let header of headers) {
                        if(header == "에러"){
                            trAppend += '<td class="errtd">' + item[header] + '<button class="errbtn btn btn-md btn-outline-white" id='+ item.container +'><i class="feather icon-search "></i></button> </td>';
                        }else {
                            trAppend += '<td>' + item[header] + '</td>';
                        }

                    }
                    return String.prototype.concat('<tr>', trAppend, '</tr>');
                }), '</tbody>');
            $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
        }
    

     function logConvertTableData(data) { // header 와 data 분리.
         //console.log("logConvertTableData :",data);
         if (data === undefined || data.length === 0) {
             return undefined;
         }

         let result = {};
         if (!Array.isArray(data)) {
             data = [data];
         }

         result.headers = ["pod","container","job","namespace","Log"]; //TODO response로 받아오는 label 값이 달라서 공통으로 들어있는것들만 뽑아서 하드코딩. pod가 serviceId 개념..
         //result.headers = Object.keys(data[0]);
         //result.headers.push("Log");
         result.data = data.map(value => value);
         /*로그 하나의 배열로 변환 - log 들을 하나의 table로 보여줄때 사용*/
         /*let valueList = [];
         for(let i=0; i<data.length; i++){ //Object 의 array.length
            for(let j=0; j < Object.keys(data[i]).length; j++){
                valueList = valueList.concat(Object.values(data[i])[j][1]); //log 데이터만 추출.
            }

         }
         result.headers = Object.keys(valueList);
         result.data = valueList.map(value => value);*/

         return result;
     }

     function convertTableData(data) {
        if (data === undefined || data.length === 0) {
            return undefined;
        }

        let result = {};
        if (!Array.isArray(data)) {
            data = [data];
        }

        //result.headers = header;
        result.headers = Object.keys(data[0]); //TODO header 고정으로 받게끔.... 이값이 고정으로 들어온다면 수정할필요없겠지만..........아니라면 헤더값을 고정으로 넣어줘야한다?
        result.data = data.map(value => value);
        return result;

//         let result = {};
//         let header = [];
//         let dataList = [];
//         element = {};
//
//         for(let [key,value] of data){
//            header.push(key);
//            element[key]= value[1];
//         }
//
//         dataList.push(element);
//         //result.headers = Object.keys(data[0]); //원본 객체의 (data[0]) key 값들을 배열로 리턴.
//         //result.data = data.map(value => value);
//         result.headers = header;
//         result.data = dataList;
//
//         console.log("convertTableData result : ",result);
//         return result;
     }

    return {
        createPanel: function (panel) {
                if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                    panel.refreshIntervalMillis = defaultIntervalMillis;
                }
                panel.readyTimestamp = readyTimestamp;
                const panelType = panel.panelType;

                if(panelType == "LOG_METRIC_TABLE"){ //에러로그 추출을 위한 obj 전역변수 생성 후 데이터 대입.
                    obj = panel;
                }

                switch (panelType) {
                    case "METRIC_TABLE":
                    case "LOG_METRIC_TABLE":
                        this.getDataByPanel(panel, true)
                            .then(value => this.createTable(panel, value))
                            .then(panel => scheduleMap.set(panel.panelId,
                                setTimeout(lokiJs.refreshFunction, panel.refreshIntervalMillis, panel))
                            )
                        break;
                }
            },
        refreshFunction: function (panel) {
            const panelType = panel.panelType;
            panel.readyTimestamp = panel.readyTimestamp + panel.refreshIntervalMillis;
            let timerId = setTimeout(lokiJs.refreshFunction, panel.refreshIntervalMillis, panel);
            scheduleMap.set(panel.panelId, timerId);
            switch (panelType) {
                case "METRIC_TABLE":
                case "LOG_METRIC_TABLE":
                    lokiJs.getDataByPanel(panel)
                        .then(value => lokiJs.createTable(panel, value));
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

//                const start = isCreate ? panel.readyTimestamp - (60 * 60) * 1000
//                    : panel.readyTimestamp - panel.refreshIntervalMillis;
//
//                const end = isCreate ? start + (60 * 60) * 1000
//                    : start + panel.refreshIntervalMillis;

                const start = new Date().setHours(0,0,0,0) //TODO 얘네도 나노세컨즈로 맞춰야할까? 값은 정상적으로 나온다.
                const end = new Date().getTime()

                if (chartQuery.queryType.indexOf("METRIC") > -1) {
                      let uri = convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end) //모두 start , end 부여.
//                    let uri = chartQuery.apiQuery.indexOf("query_range") > -1 //query_range로 시작하는 쿼리문은 뒤에 start , end를 붙이고 아니면 붙이지 않는다.
//                        ? convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, start, end)
//                        : convertApiQuery;
                    return chartQuery.queryType === "PROXY_METRIC"
                        ? this.getFetchRequest("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"))
                        : this.getFetchRequest(apiHost + encodeURI(uri).replace(/\+/g, "%2B"));
                } else {
                    return this.getFetchRequest(encodeURI(convertApiQuery).replace(/\+/g, "%2B"));
                }
            }));
        },
        getQueryRangeTimeNStep: function (chartQuery, start, end) {
            const queryStep = chartQuery.queryStep === undefined || chartQuery.queryStep === ''
                ? 15 : chartQuery.queryStep;
            return String.prototype.concat("&start=", start / 1000, "&end=", end / 1000, "&step=", queryStep);
        },

        getFetchRequest: function (url) {
            return fetch(url).then(response => { //TODO 여기서 http api를 호출하기때문에 url을 완성시켜야한다. & 파라미터 정의 해야하는지?
                const contentType = response.headers.get("Content-Type");
                if (contentType.indexOf("text/html") >= 0) {
                    return response.text();
                }
                return response.json()
            });
        },
        createTable: function (panel, dataArray) {
            let tableData;
            if (panel.panelType === 'LOG_METRIC_TABLE') {
                let data = new Map();
                for (let i = 0; i < dataArray.length; i++) {
                    let item = dataArray[i];
                    if (item.data.resultType === 'matrix') {
                        console.warn("unsupported result type=" + item.data.data.resultType);
                    } else {
                        console.log("item.data.result : ", item.data.result);
                        item.data.result.forEach(value => {
                            const key = Object.keys(value.stream);
//                            const key = Object.values(value)[0].container;
//                            const legend = panel.chartQueries[i].legend;
                            let element = data.get(key);
                            if (element === undefined) {
                                element = {}; //value.stream
                                  for (const [key, entry] of Object.entries(value.stream)) {
                                    element[key] = entry;
                                  }
                            }
                            data.set(key, element);

                        });
                    }
                }
                    tableData = logConvertTableData([...data.values()]);
                    logrenderTable(panel, tableData);
            } else if (panel.panelType === 'METRIC_TABLE'){
                //console.log("api 반환 dataArray :",dataArray);
                let data = new Map();
                for (let i = 0; i < dataArray.length; i++) {
                    let item = dataArray[i];
                    if (item.data.resultType === 'matrix') {
                        console.warn("unsupported result type=" + item.data.data.resultType);
                    } else {
                        item.data.result.forEach(value => {
                            const key = Object.values(value.metric).toString(); //각 쿼리문의 결과값에서 value.metric 값이 모두 넘어오지 않을경우 어떻게 해야하는지
                            const legend = panel.chartQueries[i].legend;
                            let element = data.get(key);
                            if (element === undefined) {
                                element = {};
                                for (const [key, entry] of Object.entries(value.metric)) { //TODO 이렇게 반복해서 key, value를 넣게되면 값이 없는 상태에서는 생략된다.
                                    element[key] = entry;
                                }
                            }
                            element[legend] = this.convertValue(parseFloat(value.value[1]).toFixed(1) - 0, panel.yaxisUnit); //parseFloat 부동소수점 실수로 반환. -> 소수점 처리?
                            //console.log("element[legend] :",element[legend]) ;

                            data.set(key, element);
                            //console.log("data :",data);
                        });
                    }
                }

                tableData = convertTableData([...data.values()]);
                renderTable(panel, tableData);

            } else if (panel.panelType === 'HTML_TABLE'){
                  tableData = dataArray[0];
              }
                  //renderTable(panel, tableData);
            return panel;
        },
        convertValue: function (value, unit) {
            if (unit !== undefined && unit.toLowerCase() === "float") {
                unit = "";
            }

            if (unit !== undefined && unit.toLowerCase() === "count") {
                return this.thousandsSeparators(value);
            }

            let kilo = unit !== undefined && unit.toLowerCase().indexOf('byte') > -1  ? 1024 : 1000;
            let convertUnit = unit !== undefined && unit.toLowerCase().indexOf('byte') > -1  ? "iB" : unit;
            return Math.abs(value) > 1000000000
                ? Highcharts.numberFormat(value / kilo / kilo / kilo, 0) + " G" + convertUnit
                : Math.abs(value) > 1000000
                    ? Highcharts.numberFormat(value / kilo / kilo, 0) + " M" + convertUnit
                    : Math.abs(value) > 1000
                        ? Highcharts.numberFormat(value / kilo, 0) + " K" + convertUnit
                        : value + ' ' + unit;
        },



    }

}());
