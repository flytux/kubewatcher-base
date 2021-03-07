package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class TestApp {

    private static final String beforeDateStr = "2020-10-20T02:05:00Z";
    private static final DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();

    public static void main(String[] args) {

        DateTime current = new DateTime();
        System.out.println("current = " + current);

        DateTime before = formatter.parseDateTime(beforeDateStr);
        System.out.println("before = " + before);

        Years years = Years.yearsBetween(before, current);
        System.out.println("years = " + years);

        Duration duration = new Duration(current.toInstant().getMillis(), before.toInstant().getMillis());
        System.out.println("duration = " + duration);
        System.out.println("duration Days = " + duration.toStandardDays());
        System.out.println("duration Hours = " + duration.toStandardHours());
        System.out.println("duration Minutes = " + duration.toStandardMinutes());
        System.out.println("duration Seconds = " + duration.toStandardSeconds());

        Period period = duration.toPeriod();
        System.out.println("duration.toPeriod() = " + period);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .appendMillis().appendSuffix("ms")
            .printZeroNever()
            .toFormatter();
        System.out.println("period.toStandardDays() = " + period.toString(formatter));
        System.out.println("period.toStandardDays() = " + ExternalConstants.getBetweenPeriod(before.toInstant().getMillis(), current.toInstant().getMillis()));
        System.out.println("period.toStandardDays() = " + ExternalConstants.getCurrentBetweenPeriod(before.toInstant().getMillis()));
        System.out.println("period.toStandardDays() = " + getDurationAsString(current.toInstant().getMillis() - before.toInstant().getMillis()));
    }

    private static String getDurationAsString(long durationInMillis) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .appendMillis().appendSuffix("ms")
            .printZeroNever()
            .toFormatter();
        Period period = new Period(durationInMillis);
        return formatter.print(period);
    }
}
