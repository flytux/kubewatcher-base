    var MAX_QUERY_LIMIT = 5000; //api query limit
    //var LOG_ATTR;
    var TARGET_OBJ = {},TARGET_ID; // 에러로그 대상 app , appId
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

    if(TARGET_ID == undefined){
        alert("대상 어플리케이션 선택 필요");
        return;
    }else{
        var uri = "/loki/api/v1/query_range?direction=BACKWARD&limit="+MAX_QUERY_LIMIT+"&query={app=~"+ '"' + TARGET_ID +'"' + "} |= " +'"' +"error"+ '"'; //local
        //var uri = "/loki/api/v1/query_range?direction=BACKWARD&limit="+MAX_QUERY_LIMIT+"&query={app=~"+ '"' + TARGET_ID +'"' + ",marker=" + '"'+ "FRT.TX_END" +'"'+"} |=" +'"' +"TX END : [1]"+ '"'; //Caas
        console.log("조회 :",uri)
        TARGET_OBJ.chartQueries[0].apiQuery = uri;

        lokiJs.getDataByPanel(TARGET_OBJ, true,startT,endT)
            .then(value => lokiJs.createTable(TARGET_OBJ, value))
    }
});

$(document).on("click", ".errtd", function(){ //에러 버튼 클릭
    var typeColId= $(this).children()[0].id; //renderTable 함수에서 ID값 부여
    var uri = "/loki/api/v1/query_range?direction=BACKWARD&limit="+MAX_QUERY_LIMIT+"&query={app=~"+ '"' + typeColId +'"' + "} |= " +'"' +"error"+ '"'; //local
    //var uri = "/loki/api/v1/query_range?direction=BACKWARD&limit="+MAX_QUERY_LIMIT+"&query={app=~"+ '"' + typeColId +'"' + ",marker=" + '"'+ "FRT.TX_END" +'"'+"} |=" +'"' +"TX END : [1]"+ '"'; // TODO Caas
    console.log("에러버튼 :",uri)
    TARGET_OBJ.chartQueries[0].apiQuery = uri; //panel과 container를 전역변수에 대입
    TARGET_ID = typeColId; //에러로그를 조회할 app

   switch (TARGET_OBJ.panelType) {
        case "LOG_METRIC_TABLE":
            lokiJs.getDataByPanel(TARGET_OBJ, true)
                .then(value => lokiJs.createTable(TARGET_OBJ, value))
            break;
    }
});

$(document).on("click", ".logbtn", function(){ //.logbtn     modal log 버튼 클릭.

    var tdArr = $(this).parent().parent().children();
    var total = $(this).parent().parent().children().length;
    let element = {};
    var serviceId = "";
    var clientIp = "";
    var uniqueId ="";  //고유값으로 설정하여 이값을 기준으로 로그 불러오게끔 만들기.
    //TODO Caas환경에서는 표시될 컬럼은 ServiceID , ClientIP ,RequestTime, ElpsedTime 여기서 파라미터값으로 사용할 컬럼 체크하여 로그리스트를 불러오는 쿼리 만들어야함.
    $.each(tdArr,function(index,item){ //쿼리문 파라미터로 넘기기위한 추출
        console.log("로그버튼 :",item)
        if($(item).attr("name") == "ServiceId"){
            serviceId = $(item).text();
        }
        if($(item).attr("name") == "ClientIP"){
            clientIp = $(item).text();
        }
        if($(item).attr("name") == "app"){
            app = $(item).text();
        }
        if($(item).attr("name") == "uniqueId"){
            uniqueId = item.id;
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


    var uriStart = "/loki/api/v1/query_range?direction=BACKWARD&limit="+MAX_QUERY_LIMIT+"&query={";
    //var uriEnd = "} |="+'"'+"error"+'"'+"&start="+startT+"&end="+endT+"&step=60"; //&start="+start+"&end="+end+"&step=60"  => local용
    var uriEnd = ",marker=" + '"'+ "FRT.TX_END" +'"'+"} |=" + '"'+ "TX END : [1]"+'"' + "&start="+startT+"&end="+endT+"&step=60"; //TODO - Caas용
    //",marker=" + '"'+ "FRT.EXEC_SVC" +'"'+"}
    //var uri = uriStart +"serviceId=" +'"' + serviceId + '"' + ",container=" +'"' + container + '"' + ",pod=" +'"' + pod + '"' + ",stream="+'"'+stream+'"' + uriEnd;
    console.log("logbtn 클릭 :",uri)
    fetch("/proxy/loki" + encodeURI(uri).replace(/\+/g, "%2B"))
        .then((response) => response.json())
        .then((data) => logModalTable(data.data));

});

$(document).on("click", ".extendlog", function(){
    var timeStamp = $(this)[0].id;
});

function logModalTable(tableData){
      $('#logModal').modal(); //step1 : modal 호출.
      var data = tableData.result;

      $('#serviceName').text(data[0].stream.pod);
      var dataArray = [];
      var dataArrayAll = [];

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
                        trAppend += '<td name="uniqueId" id="'+ item.uniqueId  +'">' + '<input type="button" class="logbtn btn btn-md btn-outline-white" value="Log">' + '</td>';
                    }else{
                        trAppend += '<td name="'+ header +'">' + item[header]  + '</td>';
                    }
                }
                return String.prototype.concat('<tr class="logLabel">', trAppend, '</tr>');

            }), '</tbody>');
        $('#container-' + panel.panelId).html(tableHeaderHtml + tableBodyHtml);
    }
    function renderTable(panel, tableData) {
            //console.log("renderTable tableData :",tableData)
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
                successCount = Number(dataArray[i].정상);
                errorCount = Number(dataArray[i].에러);
                //elapsedTime = Number(dataArray[i].응답시간);
                if(isNaN(totalCount)){
                    totalCount = 0;
                }
                if(isNaN(errorCount)){
                    errorCount = 0;
                }
                if(isNaN(successCount)){
                    successCount = 0;
                }

                //dataArray[i].정상 = totalCount - errorCount; //TODO 이렇게 구하는것과 successCount가 같은지 확인하고 같다면 successCount사용.
                nomalPercent = 100 * ((totalCount - errorCount) / totalCount);
                errorPercent = 100 * (errorCount / totalCount);
                dataArray[i].정상율 = parseFloat(nomalPercent).toFixed(2);
                dataArray[i].에러율 = parseFloat(errorPercent).toFixed(2);
            }

            let totalSum =0, nomalSum =0, errorSum =0, nomalAvg=0, errorAvg=0, elapsedAvg=0 ; //집계값
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
                        }
                        else if(header =="응답시간"){
                            trAppend += '<td>' + item[header] +" ms"+'</td>';
                            elapsedAvg += Number(item[header]);
                        }
                        else{
                            trAppend += '<td>' + item[header] + '</td>';
                        }
                    }
                    return String.prototype.concat('<tr>', trAppend, '</tr>');

                }), '</tbody>');
                nomalAvg = parseFloat(nomalAvg / rowCount).toFixed(1) ;
                errorAvg = parseFloat(errorAvg / rowCount).toFixed(1) ;
                //elapsedAvg = parseFloat(elapsedAvg / rowCount).toFixed(1) ;
                const tableFootHtml = String.prototype.concat('<tfoot><tr>'
                    + '<th>집계</th>' + '<th>'+ totalSum +'</th>' + '<th>'+nomalSum+'</th>' + '<th>'+nomalAvg+" %"+'</th>' + '<th>'+errorSum+'</th>' + '<th>'+errorAvg+" %"+'</th>' +
                    '</tr></tfoot>'); //'<th>'+elapsedAvg+" ms"+'</th>' +
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
         //result.headers = ["pod","app","job","container","stream","Log"]; //TODO local test용 - api 결과값의 stream 값을 기준으로 설정했었다.
         result.headers = ["ServiceId","ClientIP","RequestTime","ElapsedTime","Log"]; //TODO Caas 환경에서 표시할 항목값들만 선언..
         result.data = data.map(value => value);
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

        typeCol = Object.keys(data[0])[0]; //기준 항목
        /*
        //TODO 쿼리의 결과값으로 타겟이 존재하지않는 상황이 발생하기때문에 넘어오는 타겟의 수가 틀릴경우가 있다.
            그렇기때문에 총건수 ~ 에러율을 고정값으로 넣어준상태.
            ex) {app=~"loki|grafana|prometheus"} |= "error" 호출할 경우 loki에 error난 로그가 없을시 결과값에 항목이없이 넘어온다.
            ..
            + 응답시간(elapsedTime) api-Query문 작성 및 검증 필요
        */
        colList = ["총건수","정상", "정상율","에러", "에러율"];
        colList.unshift(typeCol);
        result.headers = colList

        result.data = data.map(value => value); //원본

        return result;
     }

     function convertSumBadgeData(dataArray) {
        //console.log("1. dataArray :",dataArray)
            return dataArray.map(value =>{
                let obj = {};
                const key = Object.values(value.metric).toString();
                if(key !== "" || undefined){
                    let valueCount = 0; //value 로 넘어오는 count 모두 sum
                    for(let j=0; j< Object.values(value.values).length; j++){ //value sum
                        count = Number(Object.values(value.values)[j][1]);
                        valueCount += count;
                    }
                    obj = {
                        [key + ''] : valueCount
                    };
                    return obj;
                }else{
                    obj = {
                        0 : 0
                    };
                    return obj;
                }
            })
            /*return dataArray.map(item => {
                return item.data.result.map(value => {
                    let obj = {};
                    const key = Object.values(value.metric).toString();
                    if(key !== "" || undefined){
                        let valueCount = 0; //value 로 넘어오는 count 모두 sum
                        for(let j=0; j< Object.values(value.values).length; j++){ //value sum
                            count = Number(Object.values(value.values)[j][1]);
                            valueCount += count;
                        }
                        obj = {
                            [key + ''] : valueCount
                        };
                        return obj;

                        //return valueCount;
                    }
                });
            })*/
//         return dataArray.map(item => {
//             console.log(item);
//             return item.data.result.flatMap(resultItem =>
//                 parseInt(resultItem.value[1]));
//         }).reduce((value1, value2) => value1 + value2)[0];
     }

    return {
        createPanel: function (panel, serviceMap) {

                if (panel.refreshIntervalMillis === undefined || panel.refreshIntervalMillis <= 0) {
                    panel.refreshIntervalMillis = defaultIntervalMillis;
                }
                panel.readyTimestamp = readyTimestamp;
                const panelType = panel.panelType;

                if(panelType == "LOG_METRIC_TABLE" && panelType !== null){
                    TARGET_OBJ = panel;
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
                    case "BADGE":
                       // panel = lokiJs.getErrorCount(panel,serviceMap) //TODO BADGE 데이터를 만드는 과정 검증필요..
                        this.getDataByPanel(panel, true)
                            .then(value => this.createBadge(panel, value,serviceMap))
                            .then(panel => scheduleMap.set(panel.panelId,
                                setTimeout(lokiJs.refreshFunction, panel.refreshIntervalMillis, panel))
                        );
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
                case "BADGE":
                    lokiJs.getDataByPanel(panel)
                        .then(value => lokiJs.createBadge(panel, value));
                    break;
            }
        },

//        getErrorCount : function(panel, serviceMap){
//            console.log("getErrorCount panel :",panel)
//            //var uriErrorEnd = "} |=" +'"'+"error"+'"'+"[1m])) by (app)";
//            var uriErrorEnd = ",marker=" + '"'+ "FRT.TX_END" +'"'+"} |=" +'"'+"TX END : [1]"+'"'+"[1m])) by (app)";
//            for(let i =0; i<panel.chartQueries.length; i++){
//                 var uri = "";
//                 var copyArr = [];
//                const convertApiQuery = commonVariablesJs.convertVariableApiQuery(panel.chartQueries[i].apiQuery);
//                 for(let j=0; j<serviceMap.length; j++){
//                    const clone = JSON.parse(JSON.stringify(panel.chartQueries[i]))
//                    uri = convertApiQuery + '"' +serviceMap[j]+ '"'+ uriErrorEnd;
//                    clone.apiQuery = uri
//                    copyArr.push(clone);
//                 }
//            }
//            panel.chartQueries = copyArr; //servicemap 만큼의 쿼리문 동적 생성 후 panel에 반환.
//            return panel;
//        },
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
            return fetch(url).then(response => {
                const contentType = response.headers.get("Content-Type");
                if (contentType.indexOf("text/html") >= 0) {
                    return response.text();
                }
                return response.json()
            });
        },
        createTable: function (panel, dataArray) {
            let tableData;

            if (panel.panelType === 'LOG_METRIC_TABLE') { //TODO Cass 환경
                let data = new Map();
                for (let i = 0; i < dataArray.length; i++) {
                    if(dataArray[i].status == 404){
                      logrenderTable(panel);
                       break;
                    }else if(dataArray[i].data.result == ""){ // 값이 없을경우
                       logrenderTable(panel);
                       break;
                    }
                    let item = dataArray[i];
                    //const serviceName = item.data.result[0].stream.pod;
                    let values = item.data.result[i].values;

                    var requestTime;
                    for(let j=0; j<values.length; j++){
                        element = {};
                        myDate = new Date(values[j][0]/1000000);
                        requestTime =myDate.getFullYear() +'-'+('0' + (myDate.getMonth()+1)).slice(-2)+ '-' +  ('0' + myDate.getDate()).slice(-2) + ' '+myDate.getHours()+ ':'+('0' + (myDate.getMinutes())).slice(-2)+ ':'+myDate.getSeconds();  //TODO Caas환경: RequestTime
                        splitWord = values[j][1].split(" ");

                        uniqueId = splitWord[4]; //local
                        serviceId = splitWord[3]; //local
                        clientIP = splitWord[5]; //local
                        elpasedTime = splitWord.pop(); //local


//                       uniqueId = splitWord[8] // 유니크아이디로 설정하여 이 값으로 로그 값 추출하는 쿼리 만들기.
//                       uniqueId = uniqueid.replace(/\[/," ");
//                       uniqueId = uniqueid.replace(/\]/," ");

//                       serviceId = splitWord[9]; //TODO Caas환경: serviceId
//                       serviceId = serviceId.replace(/\[/," ");
//                       serviceId = serviceId.replace(/\]/," ");

//                       clientIP = splitWord[10]; //TODO Caas환경: clientIP
//                       clientIP = clientIP.replace(/\[/," ");
//                       clientIP = clientIP.replace(/\]/," ");

//                       elpasedTime = splitWord.pop(); //TODO Caas환경: ElpsedTime 값

//                       console.log(uniqueId,serviceId,clientIP,requestTime,elpasedTime)

                        element["RequestTime"] = requestTime;
                        element["ServiceId"] = serviceId;
                        element["ClientIP"] = clientIP;
                        element["ElapsedTime"] = elpasedTime;
                        element["uniqueId"] = uniqueId;

                        data.set(j,element);
                    }
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
                    let valueCount = 0; //value 로 넘어오는 count 모두 sum
                    for(let j=0; j< Object.values(value.values).length; j++){ //value sum
                        count = Number(Object.values(value.values)[j][1]);
                        valueCount += count;
                    }

                    element[legend] = parseFloat(valueCount).toFixed(1) - 0;

                    data.set(key, element);
                    });
                    //console.log(data);
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
        thousandsSeparators: function(value) {
            let values = value.toString().split(".");
            values[0] = values[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            return values.join(".");
        },
        createBadge: function (panel, dataArray, serviceMap) { //todo serviceMap은 caas환경

            if (panel.chartType === 'text') {
               // $('#container-' + panel.panelId).text((badgeData) + panel.yaxisUnit);
               $('#container-' + panel.panelId).text(this.convertValue(convertSumBadgeData(dataArray), panel.yaxisUnit));
            } else if (panel.chartType === 'date') {
               $('#container-' + panel.panelId).text(moment(new Date(badgeData)).format('YYYY-MM-DD hh:mm:ss'));
            } else if (panel.chartType === 'age') {
               let duration = moment.duration(moment(new Date()).diff(new Date(badgeData)));
               let age = duration.asDays() > 1 ? parseInt(duration.asDays()) + ' Day'
                   : duration.asHours() > 1 ? parseInt(duration.asHours()) + ' Hour'
                       : duration.asMinutes() > 1 ? parseInt(duration.asMinutes()) + ' Min.'
                           : duration.asSeconds() > 1 ? parseInt(duration.asSeconds()) + ' Sec.' : 'N/A';
               $('#container-' + panel.panelId).text(age);
            } else if(dataArray[0].data.result.length === 0) {
               $('#container-' + panel.panelId).text('N/A');
            } else {
               //$('#container-' + panel.panelId).text(this.convertValue(convertSumBadgeData(dataArray), panel.yaxisUnit)); //하나의 컨테이너에 모든 에러카운트가 들어가게됨..
                let tableBottomHtml ;
                var appKey , errCount;
                for(let i=0; i<dataArray.length; i++){
                    valueCount = convertSumBadgeData(dataArray[i].data.result);

                    if(valueCount !== "" || null || undefined){
                        appKey = Object.keys(valueCount[0]).toString();
                        errCount =Object.values(valueCount[0]);
                        errCount = this.convertValue(errCount ,panel.yaxisUnit);
                        //console.log(appKey, errCount)

                        tableBottomHtml += '<div class="box_chart_red row col-xs-4">';
                        tableBottomHtml += '<div class="txt text-center">'+ appKey + '</div>';
                        tableBottomHtml += '<div class="t_chart_md text-center">' + errCount +'</div>' ;
                        tableBottomHtml += '</div>';
                    }else{
                        return;
                    }
                    $('#container-' + panel.panelId).html(tableBottomHtml);
                }

//               var appKey , errCount;
//               let tableBottomHtml ;
//                for(let i=0; i<badgeData.length; i++){
//                    if(badgeData[i][0] == undefined){
//                        badgeData[i][0] = {0 :0}; //TODO 값이 없을때 빈값으로 넘어옴..변경필요..
//                    }
//                    appKey = Object.keys(badgeData[i][0]);
//                    errCount =Object.values(badgeData[i][0]);
//                    errCount = this.convertValue(errCount ,panel.yaxisUnit);
//                   tableBottomHtml += '<div class="box_chart_red row col-xs-4">';
//                   tableBottomHtml += '<div class="txt text-center">'+ appKey + '</div>';
//                   tableBottomHtml += '<div class="t_chart_md text-center">' + errCount +'</div>' ;
//                   tableBottomHtml += '</div>';
//                }
//                $('#container-' + panel.panelId).html(tableBottomHtml);
            }
            return panel;
        },
    }

}());
