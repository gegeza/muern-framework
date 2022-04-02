package com.muern.framework.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * @author gegeza
 * @date 2019-11-26 5:31 PM
 */
public final class DateUtil {
    public static final String DATETIME = "yyyyMMddHHmmss";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIMESTAMP = "yyyyMMddHHmmssSSSS";
    /** yyyy年MM月dd日 HH:mm:ss */
    public static final String YYYYMMDDHHMMSS_CN = "yyyy年MM月dd日 HH:mm:ss";

    public static LocalDateTime MIN = LocalDateTime.parse("1970-01-01T00:00:00");


    /** 格式化当前时间 */
    public static String formatNowDateTime() {
        return formatNow(DATE_TIME);
    }
    
    /** 格式化当前时间 */
    public static String formatNowTimeStamp() {
        return formatNow(DATETIMESTAMP);
    }

    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    public static String format(TemporalAccessor temporalAccessor, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporalAccessor);
    }

    /**
     * 字符串日期格式转换
     */
    public static String format(String date, String sourcePattern, String targetPattern) {
        return format(toLocalDateTime(date, sourcePattern), targetPattern);
    }

    /**
     * 格式化为日期
     */
    public static LocalDateTime toLocalDateTime(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * String 转 LocalDate
     */
    public static LocalDate toLocalDate(String date, String pattern) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 时间戳转LocalDateTime
     * @param seconds
     * @return
     */
    public static LocalDateTime secondsToLocalDateTime(long seconds) {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8));
    }

    /**
     * 返回较大的时间
     */
    public static LocalDateTime max(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        return localDateTime1.compareTo(localDateTime2) > 0 ? localDateTime1 : localDateTime2;
    }

    /**
     * 返回较小的时间
     */
    public static LocalDateTime min(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        return localDateTime1.compareTo(localDateTime2) > 0 ? localDateTime2 : localDateTime1;
    }

    /**
     * 获取某天最小的时间点：yyyyMMdd 00:00:00
     */
    public static LocalDateTime getDayStart(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取某天最后的时间点：yyyyMMdd 23:59:59
     */
    public static LocalDateTime getDayEnd(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 取得两个日期的间隔天数
     */
    public static long getDiffDays(LocalDateTime day1, LocalDateTime day2) {
        return day1.toLocalDate().toEpochDay() - day2.toLocalDate().toEpochDay();
    }


    /**
     * 获取所在周的星期一的日期
     */
    public static LocalDate getFirstDayOfWeek(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");//设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        return LocalDate.parse(sdf.format(cal.getTime()), dtf);
    }

}
