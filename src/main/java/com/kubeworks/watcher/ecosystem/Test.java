package com.kubeworks.watcher.ecosystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.ContainerMetrics;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.PodMetrics;
import com.kubeworks.watcher.ecosystem.kubernetes.serdes.CustomQuantityFormatter;
import io.kubernetes.client.custom.Quantity;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class Test {

    public class Test6 {

        public static void main(String[] args) {
            LocalDateTime localDateTime = LocalDateTime.now();
            System.out.println(localDateTime);


            long epochMilli = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            System.out.println("epochMilli = " + epochMilli);

            // LocalDateTime -> Timestamp로 변경
            Timestamp timestamp2 = Timestamp.valueOf(localDateTime);
            System.out.println(timestamp2);

            // 1970/01/01 00:00:00 GMT 부터 millisecond로 계산한 시간 출력
            System.out.println(timestamp2.getTime());

            // Timestamp -> LocalDateTime으로 변경
            LocalDateTime localDateTime1 = timestamp2.toLocalDateTime();
            System.out.println(localDateTime1);
        }

    }


    public class Test5 {
        public static void main(String[] args) {
//            BigDecimal bigDecimal = new BigDecimal("123_456_789_012_345_678.000000000".replaceAll("_", ""));
//            Quantity quantity = new Quantity(bigDecimal, Quantity.Format.DECIMAL_SI);
//            System.out.println("quantity = " + ExternalConstants.toStringQuantityViaK8s(quantity));
//            System.out.println("quantity = " + ExternalConstants.toStringQuantity(quantity));
//
//            BigDecimal bigDecimal1 = new BigDecimal("123_456_789_012_345_678".replaceAll("_", ""));
//            Quantity quantity1 = new Quantity(bigDecimal1, Quantity.Format.BINARY_SI);
//            System.out.println("quantity = " + ExternalConstants.toStringQuantityViaK8s(quantity1));
//            System.out.println("quantity = " + ExternalConstants.toStringQuantity(quantity1));
        }
    }

    public class Test4 {
        public static void main(String[] args) {

            String visitsPodName = "visits-5d5b44768-sbkbv";
            String apiGatewayPodName = "api-gateway-7df5bb9486-zbldg";


            String[] split = StringUtils.split(visitsPodName, "-");
            String visitName = Arrays.stream(split, 0, split.length - 2).collect(Collectors.joining("-"));
            System.out.println("visitName = " + visitName);

        }
    }

    public class Test3 {
        static final String json = "{\"kind\":\"PodMetrics\",\"apiVersion\":\"metrics.k8s.io/v1beta1\",\"metadata\":{\"name\":\"prometheus-cluster-monitoring-0\",\"namespace\":\"cattle-prometheus\",\"selfLink\":\"/apis/metrics.k8s.io/v1beta1/namespaces/cattle-prometheus/pods/prometheus-cluster-monitoring-0\",\"creationTimestamp\":\"2021-01-21T06:30:33Z\"},\"timestamp\":\"2021-01-21T06:30:15Z\",\"window\":\"30s\",\"containers\":[{\"name\":\"rules-configmap-reloader\",\"usage\":{\"cpu\":\"3770n\",\"memory\":\"4700Ki\"}},{\"name\":\"prometheus\",\"usage\":{\"cpu\":\"28608822n\",\"memory\":\"640852Ki\"}},{\"name\":\"prometheus-config-reloader\",\"usage\":{\"cpu\":\"0\",\"memory\":\"5780Ki\"}},{\"name\":\"prometheus-proxy\",\"usage\":{\"cpu\":\"3338n\",\"memory\":\"5908Ki\"}},{\"name\":\"prometheus-agent\",\"usage\":{\"cpu\":\"178816n\",\"memory\":\"14260Ki\"}}]}";
        static final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
            .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .failOnUnknownProperties(false)
            .defaultViewInclusion(false)
            .modules(new JavaTimeModule())
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .dateFormat(new StdDateFormat())
            .build();

        @SneakyThrows
        public static void main(String[] args) {

            PodMetrics item = objectMapper.readValue(json, new TypeReference<PodMetrics>() {});
            List<ContainerMetrics> containers = item.getContainers();
            Map<String, Quantity> stringQuantityMap = containers.stream().map(ContainerMetrics::getUsage)
                .reduce((map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, (quantity1, quantity2) -> {
                        Quantity.Format format1 = quantity1.getFormat();
                        Quantity.Format format2 = quantity2.getFormat();

                        if (format1 != format2) {
                            System.out.println("notEquals // format1 = " + format1 + ", format2 = " + format2);
                        }

                        BigDecimal add = quantity1.getNumber().add(quantity2.getNumber());
                        Quantity addQuantity = new Quantity(add, format1);
//                        System.out.println("addQuantity = " + addQuantity);
                        return addQuantity;
                    }));
                    return map1;
                }).orElse(Collections.emptyMap());

//            System.out.println(item.getMetadata().getName() + " :  stringQuantityMap = " + stringQuantityMap);

            stringQuantityMap.forEach((s, quantity) -> {
                System.out.println("name = " + s + ", quantity = " + ExternalConstants.toStringQuantity(quantity));
            });

        }

    }

    public class Test2 {
        public static void main(String[] args) {
            Duration duration = new Duration(180 * 1000 + 30 * 1000);
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix("d ")
                .appendHours()
                .appendSuffix("h ")
                .appendMinutes()
                .appendSuffix("m ")
                .appendSeconds()
                .appendSuffix("s")
                .toFormatter();

            Period period = duration.toPeriod();
            Period dayTimePeriod = period.normalizedStandard(PeriodType.dayTime());
            String formattedString = formatter.print(dayTimePeriod);
            System.out.println(formattedString);
            System.out.println(ExternalConstants.getFormatDuration(duration));
        }
    }

    public class Test1 {
        public static void main(String[] args) {
            CustomQuantityFormatter customQuantityFormatter = new CustomQuantityFormatter();

            BigDecimal kiloBigDecimal = new BigDecimal(1024);


            System.out.println("decimalQuantity2 = " + new BigDecimal("0.13") + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal("0.13"), Quantity.Format.DECIMAL_SI)));
            System.out.println("binaryQuantity1 = " + kiloBigDecimal + " / "
                + customQuantityFormatter.format(new Quantity(kiloBigDecimal, Quantity.Format.BINARY_SI)));
            System.out.println("decimalQuantity1 = " + kiloBigDecimal + " / "
                + customQuantityFormatter.format(new Quantity(kiloBigDecimal, Quantity.Format.DECIMAL_SI)));

            System.out.println("binaryQuantity2 = " + new BigDecimal(1024*1024).multiply(new BigDecimal(111)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024).multiply(new BigDecimal(111)), Quantity.Format.BINARY_SI)));
            System.out.println("decimalQuantity2 = " + new BigDecimal(1024*1024).multiply(new BigDecimal(234)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024).multiply(new BigDecimal(234)), Quantity.Format.DECIMAL_SI)));

            System.out.println("binaryQuantity3 = " + new BigDecimal(1024*1024*1024).multiply(new BigDecimal(564)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024*1024).multiply(new BigDecimal(564)), Quantity.Format.BINARY_SI)));
            System.out.println("decimalQuantity3 = " + new BigDecimal(1024*1024*1024).multiply(new BigDecimal(6667)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024*1024).multiply(new BigDecimal(6667)), Quantity.Format.DECIMAL_SI)));

            System.out.println("binaryQuantity4 = " + new BigDecimal(1024*1024*1024).multiply(new BigDecimal(34534)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024*1024).multiply(new BigDecimal(34534)), Quantity.Format.BINARY_SI)));
            System.out.println("decimalQuantity4 = " + new BigDecimal(1024*1024*1024).multiply(new BigDecimal(332)) + " / "
                + customQuantityFormatter.format(new Quantity(new BigDecimal(1024*1024*1024).multiply(new BigDecimal(332)), Quantity.Format.DECIMAL_SI)));

        }
    }


}
