package com.kubeworks.watcher.cloud.monitoring.controller;


import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.AllArgsConstructor;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;


@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class LogController {

    private final ProxyApiService proxyApiService;
    private final MonitoringRestController monitoringRestController;

    private final MonitoringProperties monitoringProperties;

    @GetMapping(value = "/monitoring/logging", produces = MediaType.TEXT_HTML_VALUE)
    public String logging(Model model) {
        Map<String, Object> response = monitoringRestController.logging();
        model.addAllAttributes(response);
        return "monitoring/logging/logging";
    }

    @RequestMapping(value = "/monitoring/apiCall") //TODO Rest API로 server에서 ErrorCount 구하기
    public @ResponseBody String lokiApiCall(@RequestParam(value = "param")String param) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
        long localTime = currentTime.getTime(); //local Timestamp

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR,-1);
        long sTime = cal.getTimeInMillis(); //1시간 전 timestamp
        String endTime = String.valueOf(localTime);
        String startTime = String.valueOf(sTime);
        String forNanoSeconds = "000000";
        endTime = endTime.concat(forNanoSeconds);
        startTime = startTime.concat(forNanoSeconds);

        param = param.replace(",","|");

        String uri = "";
        String apiHost = monitoringProperties.getDefaultLokiUrl() + "/loki/api/v1/query_range?query={uri}";
        if("http://loki.do".equals(monitoringProperties.getDefaultLokiUrl())){
            uri = "sum(count_over_time({app=~"+param+"} |="+"\"error\""+"[1m])) by (app)"; //local 환경
        } else {
            uri = "sum(count_over_time({app=~"+param+",marker="+"\"FRT.TX_END\""+"} |="+"\"TX END : [1]\""+"[1m])) by (app)"; //TODO Caas환경용
        }

        String uriEnd = "&start="+startTime+"&end="+endTime+"&step=60"; //시간기준 nanosecond로 설정해야함.

        String url = apiHost + uriEnd;
        String resultData = restTemplate.getForObject(url, String.class,uri); // getForObject : 객체로 결과를 반환받는다.

        return resultData;
    }

}
