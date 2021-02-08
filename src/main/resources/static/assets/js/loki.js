
$("#searchBtn").click(function(){
    var sDate = document.getElementById('startDate').value; //날짜
    var eDate = document.getElementById('endDate').value; //날짜
    var stime = document.getElementById('startTime').value; //시작시간
    var etime = document.getElementById('endTime').value; //종료시간

    var sDT = sDate +" "+ stime;
    var eDT = eDate +" "+ etime;

    var startT = new Date(sDT).getTime();
    var endT = new Date(eDT).getTime();

    var startTime = startT.toString();
    var endTime = endT.toString();

    startT = startTime.padEnd(19,"0");
    endT = endTime.padEnd(19,"0");

    //=======================================
    if(targetId == undefined){
        alert("error");
    }
    var uri = "/loki/api/v1/query_range?direction=BACKWARD&query={app=~"+ '"' + targetId +'"' + "} |= " +'"' +"error"+ '"';
    obj.chartQueries[0].apiQuery = uri;

    lokiJs.getDataByPanel(obj, true,startT,endT)
        .then(value => lokiJs.createTable(obj, value))

});

var logAttr;
$(document).on("click", ".extendlog", function(){

    var timeStamp = $(this)[0].id; //


});


var obj = {},targetId;
$(document).on("click", ".errtd", function(){ //에러 버튼 클릭
    var typeColId= $(this).children()[0].id; //renderTable 함수에서 ID값 부여
    var uri = "/loki/api/v1/query_range?direction=BACKWARD&query={app=~"+ '"' + typeColId +'"' + "} |= " +'"' +"error"+ '"';
    //var uri = "/loki/api/v1/query_range?direction=BACKWARD&query={app=~"+ '"' + typeColId +'"' + ",stream!="+'""'  +"} |= " +'"' +"error"+ '"';
    obj.chartQueries[0].apiQuery = uri; //panel과 container를 전역변수.

    targetId = typeColId; //에러로그 modal 버튼 클릭시 사용.

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
    var app = "";


    $.each(tdArr,function(index,item){ //쿼리문 파라미터로 넘기기위한 추출
        if($(item).attr("name") == "pod"){
            pod = $(item).text();
        }
        if($(item).attr("name") == "container"){
            container = $(item).text();
        }
        if($(item).attr("name") == "app"){
            app = $(item).text();
        }
    });

    /*조회조건 시간값 가져오기.*/
    var sDate = document.getElementById('startDate').value; //날짜
    var eDate = document.getElementById('endDate').value; //날짜
    var stime = document.getElementById('startTime').value; //시작시간
    var etime = document.getElementById('endTime').value; //종료시간

    var sDT = sDate +" "+ stime;
    var eDT = eDate +" "+ etime;

    var startT = new Date(sDT).getTime();
    var endT = new Date(eDT).getTime();

    var startTime = startT.toString();
    var endTime = endT.toString();

    startT = startTime.padEnd(19,"0");
    endT = endTime.padEnd(19,"0");


    var uriStart = "/loki/api/v1/query_range?direction=BACKWARD&limit=1000&query={";
    var uriEnd = "} |="+'"'+"error"+'"'+"&start="+startT+"&end="+endT+"&step=60"; //&start="+start+"&end="+end+"&step=60"
    var uri = uriStart +"app=" +'"' + app + '"' + ",container=" +'"' + container + '"' + ",pod=" +'"' + pod + '"' + ",stream!="+'""' + uriEnd;
    //var response = lokiJs.getFetchRequest("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"));
    fetch("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"))
        .then((response) => response.json())
        .then((data) => logModalTable(data.data));

});




function logModalTable(tableData){
      $('#logModal').modal(); //step1 : modal 호출.
      var data = tableData.result;

      $('#serviceName').text(data[0].stream.pod);
      var dataArray = [];
      var dataArrayAll = [];
//      for(let i = 0; i<data.values.length; i++){
        //dataArray.push(data[i].values);
//      }
    for(let i = 0; i<data.length; i++){
       for(let j=0; j<data[i].values.length; j++){
            dataArray.push(data[i].values[j]);
       }
    }


    if(dataArray === undefined){
        $('#logModalTable')
            .html('<thead><tr><th>No Result</th></tr></thead>')
        return ;
    }

    const tableBodyHtml = String.prototype.concat('<tbody>',
        dataArray.map(item => {
            let trAppend = '';
            for(let i = 0; i<dataArray.length; i++){
                trAppend += '<td class="extendlog" id='+ '"'+ item[0] +'"' +'>' + item[1]  + '</td>';
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
                    if(header == "Log"){
                        trAppend += '<td>' + '<input type="button" class="logbtn btn btn-md btn-outline-white" value="Log" id="'+ item.pod  +'" >' + '</td>';
                    }else{
                        trAppend += '<td name="'+ header +'">' + item[header]  + '</td>';
                    }
                }
                return String.prototype.concat('<tr class="logLabel">', trAppend, '</tr>');

            }), '</tbody>');
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

            let totalCount = 0;
            for(let i =0; i<dataArray.length; i++){
                totalCount = Number(dataArray[i].총건수);
                errorCount = Number(dataArray[i].에러);
                if(isNaN(totalCount)){
                    totalCount = 0;
                }
                if(isNaN(errorCount)){
                    errorCount = 0;
                }
                dataArray[i].정상 = totalCount - errorCount;
                nomalPercent = 100 * ((totalCount - errorCount) / totalCount);
                errorPercent = 100 * (errorCount / totalCount);

                dataArray[i].정상율 = parseFloat(nomalPercent).toFixed(2);
                dataArray[i].에러율 = parseFloat(errorPercent).toFixed(2);
            }

            let totalSum =0, nomalSum =0, errorSum =0, nomalAvg=0, errorAvg=0 ; //집계값
            let rowCount = dataArray.length;

            const tableHeaderHtml = String.prototype.concat('<thead><tr>',
                headers.map(value => '<th>' + value + '</th>').join(''), //모든요소들을 연결해 하나의 문자열로 만듬.
                '</tr></thead>');
            const tableBodyHtml = String.prototype.concat('<tbody>',
                dataArray.map(item => {
                    let trAppend = '';

                    for (let header of headers) {
                        if(item[header] == undefined){ //undefined 처리.
                            item[header] = 0;
                        }
                        if(header == "총건수"){
                            trAppend += '<td>' + item[header] + '</td>';
                            totalSum += Number(item[header]);
                        }else if(header == "정상"){
                            trAppend += '<td>' + item[header] + '</td>';
                            nomalSum += item[header];
                        }else if(header == "정상율"){
                            trAppend += (item[header] >= 95 ) ? '<td><span class="success_percent_green">' + item[header] +" %"+ '</span></td>' :
                                (item[header] >= 90) ? '<td><span class="success_percent_yellow">' + item[header] +" %"+ '</span></td>' :
                                 '<td><span class="success_percent_red">' + item[header] +" %"+ '</span></td>';
                                 nomalAvg += Number(item[header]);
                        }else if(header == "에러"){
                            trAppend += '<td class="errtd">' + item[header] + '<button class="errbtn btn btn-md btn-outline-white" id='+ item.app +'><i class="feather icon-search "></i></button> </td>';
                            errorSum += Number(item[header]);
                        }else if(header == "에러율"){
                            trAppend += '<td>' + item[header] +" %"+'</td>';
                            errorAvg += Number(item[header]);
                        }else{
                            trAppend += '<td>' + item[header] + '</td>';
                        }
                    }
                    return String.prototype.concat('<tr>', trAppend, '</tr>');

                }), '</tbody>');
                nomalAvg = parseFloat(nomalAvg / rowCount).toFixed(1) ;
                errorAvg = parseFloat(errorAvg / rowCount).toFixed(1) ;
                const tableFootHtml = String.prototype.concat('<tfoot><tr>'
                    + '<th>집계</th>' + '<th>'+ totalSum +'</th>' + '<th>'+nomalSum+'</th>' + '<th>'+nomalAvg+" %"+'</th>' + '<th>'+errorSum+'</th>' + '<th>'+errorAvg+" %"+'</th>' +
                    '</tr></tfoot>');
            $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml + tableFootHtml);
        }
    

     function logConvertTableData(data) { // header 와 data 분리.
         if (data === undefined || data.length === 0) {
             return undefined;
         }

         let result = {};
         if (!Array.isArray(data)) {
             data = [data];
         }

         result.headers = ["pod","container","job","app","Log"]; //TODO response로 받아오는 label 값이 달라서 공통으로 들어있는것들만 뽑아서 하드코딩. pod가 serviceId 개념.
         result.data = data.map(value => value);

         return result;
     }

     function convertTableData(data) {
        console.log(data)
        if (data === undefined || data.length === 0) {
            return undefined;
        }

        let result = {};
        if (!Array.isArray(data)) {
            data = [data];
        }

        typeCol = Object.keys(data[0])[0]; //기준 항목
        /*
        //TODO 쿼리의 결과값으로 타겟이 존재하지않는 상황이 발생하기때문에 넘어오는 타겟의 수가 틀릴경우가 있다.
            그렇기때문에 총건수 ~ 에러율을 고정값으로 넣어준상태.
            ex) {app=~"loki|grafana|prometheus"} |= "error" 호출할 경우 loki에 error난 로그가 없을시 결과값에 항목이없이 넘어온다.
        */
        colList = ["총건수","정상", "정상율","에러", "에러율"];
        colList.unshift(typeCol);
        result.headers = colList
 
        result.data = data.map(value => value); //원본

        return result;


     }
     function appValueCall(){
        var uri = "/loki/api/v1/label/app/values";
     }

    return {
        createPanel: function (panel) {
                //appValueCall();
                if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                    panel.refreshIntervalMillis = defaultIntervalMillis;
                }
                panel.readyTimestamp = readyTimestamp;
                const panelType = panel.panelType;

                if(panelType == "LOG_METRIC_TABLE"){
                    obj = panel;
                }

                switch (panelType) {
                    case "METRIC_TABLE":
                        this.getDataByPanel(panel, true)
                            .then(value => this.createTable(panel, value))
                            .then(panel => scheduleMap.set(panel.panelId,
                                setTimeout(lokiJs.refreshFunction, panel.refreshIntervalMillis, panel))
                            )
                        break;
                    case "LOG_METRIC_TABLE":
                        this.getDataByPanel(panel, true)
                            .then(value => this.createTable(panel, value))
                            .then(panel => scheduleMap.set(panel.panelId,panel)
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
            }
        },
        getDataByPanel: function (panel, isCreate,startT,endT) {

            return Promise.all(panel.chartQueries.map(chartQuery => {
                const convertApiQuery = commonVariablesJs.convertVariableApiQuery(chartQuery.apiQuery);

                var start = new Date().setHours(0,0,0,0,0,0,0,0); //자정의 시간
                var end = new Date().setHours(23,59,59,0,0,0,0,0);
                var startTime = start.toString();
                var endTime = end.toString();
                startTime = startTime.padEnd(19,"0");
                endTime = endTime.padEnd(19,"0");

                if (chartQuery.queryType.indexOf("METRIC") > -1) {
                    if(startT != undefined && endT != undefined){
                        startTime = startT;
                        endTime = endT;
                    }
                    let uri = convertApiQuery + this.getQueryRangeTimeNStep(chartQuery, startTime, endTime)
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
            return String.prototype.concat("&start=", start , "&end=", end , "&step=", queryStep);
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
                    if(dataArray[i].status == 404){
                       logrenderTable(panel);
                       break;
                    }
                    let item = dataArray[i];
                    item.data.result.forEach(value => {
                        const key = Object.keys(value.stream);
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
                    tableData = logConvertTableData([...data.values()]);
                    logrenderTable(panel, tableData);
            } else if (panel.panelType === 'METRIC_TABLE'){
                let data = new Map();
                for (let i = 0; i < dataArray.length; i++) {
                    let item = dataArray[i];
                    item.data.result.forEach(value => {
                    const key = Object.values(value.metric).toString();
                    if(key ==""){ // value.metric 값이 없는 항목일경우  return;
                        return;
                    }
                    const legend = panel.chartQueries[i].legend;

                    let element = data.get(key);
                    if (element === undefined) {
                        element = {};
                        for (const [key, entry] of Object.entries(value.metric)) { // key값 setting
                            element[key] = entry;
                        }
                    }

                    let valueCount = 0;
                    for(let j=0; j< Object.values(value.values).length; j++){ //value sum
                        count = Number(Object.values(value.values)[j][1]);
                        valueCount += count;
                    }
                    //console.log("valueCount:",valueCount);
                    element[legend] = parseFloat(valueCount).toFixed(1) - 0; //parseFloat 부동소수점 실수로 반환. -> 소수점 처리?
                    //element[legend] = this.convertValue(parseFloat(valueCount).toFixed(1) - 0, panel.yaxisUnit); //parseFloat 부동소수점 실수로 반환. -> 소수점 처리?
                    data.set(key, element);
                    });
                    console.log("2 :",data);
                }
                tableData = convertTableData([...data.values()]);
                renderTable(panel, tableData);

            } else if (panel.panelType === 'HTML_TABLE'){
                  tableData = dataArray[0];
              }
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
