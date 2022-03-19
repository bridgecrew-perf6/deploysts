package utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * @class DateUtils
 * @brief 날짜 관련 유틸을 제공합니다.
 * @version 0.1
 * @author Kim Donghyeong
 */

@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * TODO : Should set these static final variables into Common table or Constant Variables Class Set Static final variables :
     * BASE_DATE_FORMAT.
     */
    private static final String BASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String BIGINT_DATE_FORMAT = "yyyyMMddHHmmssSSS";

    public static final String DATE_FORMAT14 = "yyyyMMddHHmmss";


    /**
     * get Current DateTime with BASE_DATE_FORMAT.
     * 
     * @return the String
     */
    public static String getDate() {

        return getDate(DateUtils.BASE_DATE_FORMAT);
    }


    /**
     * <pre>
     * get Current DateTime with specific Date Format.
     * </pre>
     *
     * @param dateFormat
     *        dateFormat
     * @return String
     */
    public static String getDate(String dateFormat) {

        return DateUtils.convertDateFormat(new Date(), new SimpleDateFormat(dateFormat));
    }


    /**
     * <pre>
     * nextDays일 이후의 일자정보를 얻는다.
     * </pre>
     *
     * @param nextDays
     *        nextDays
     * @param dateFormat
     *        dateFormat
     * @return String
     */
    public static String getNextDate(Calendar cal, int nextDays, String dateFormat) {

        if (cal == null) {
            cal = Calendar.getInstance();
        }
        cal.add(Calendar.DATE, nextDays);
        return DateUtils.convertDateFormat(cal.getTime(), new SimpleDateFormat(dateFormat));
    }


    /**
     * <pre>
     * nextDays일 이후의 일자정보를 얻는다.
     * </pre>
     *
     * @param nextDays
     *        nextDays
     * @param dateFormat
     *        dateFormat
     * @return String
     */
    public static String getNextDate(int nextDays, String dateFormat) {

        return getNextDate(Calendar.getInstance(), nextDays, dateFormat);
    }


    /**
     * <pre>
     * srcDateFormat 형으로 지정된 일자 정보를 trgDateFormat형태로 변경.
     * </pre>
     * 
     * @param sourceString
     *        sourceString
     * @param srcDateFormat
     *        srcDateFormat
     * @param trgDateFormat
     *        trgDateFormat
     * @return the String
     */
    public static String convertDateFormat(String sourceString, String srcDateFormat, String trgDateFormat) {

        if (sourceString == null || StringUtils.isEmpty(sourceString) || sourceString.startsWith("0000")
                || sourceString.startsWith("00:00")) {
            return "";
        }

        Date sourceDate = null;
        try {
            sourceDate = new SimpleDateFormat(srcDateFormat).parse(sourceString);
        } catch (ParseException e) {
            return "";
        }
        return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(trgDateFormat));
    }


    public static String convertDateFormat(String sourceString, String srcDateFormat, String trgDateFormat, Locale locale) {

        if (sourceString == null || StringUtils.isEmpty(sourceString) || sourceString.startsWith("0000")
                || sourceString.startsWith("00:00")) {
            return "";
        }

        Date sourceDate = null;
        try {
            sourceDate = new SimpleDateFormat(srcDateFormat).parse(sourceString);
        } catch (ParseException e) {
            return "";
        }
        return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(trgDateFormat, locale));
    }


    /**
     * <pre>
     * 일자 정보(yyyyMMddHHmm)를 지정된 포멧으로 변경.
     * </pre>
     * 
     * @param sourceString
     *        sourceString
     * @param dateFormat
     *        dateFormat
     * @return the String
     */
    public static String convertDateFormat(String sourceString, String dateFormat) {

        Date sourceDate = null;

        try {
            sourceDate = new SimpleDateFormat("yyyyMMddHHmm").parse(sourceString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(dateFormat));
    }


    /**
     * <pre>
     * 일자 정보(yyyyMMddHHmmss)를 지정된 포멧으로 변경.
     * </pre>
     * 
     * @param sourceDate
     *        sourceDate
     * @param dateFormat
     *        dateFormat
     * @return the String
     */
    public static String convertDateFormat(Date sourceDate, String dateFormat) {

        return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(dateFormat));
    }


    /**
     * <pre>
     * get format converted Date string with specific Date Format.
     * </pre>
     * 
     * @param sourceDate
     *        sourceDate
     * @param dateForm
     *        dateForm
     * @return the String
     */
    public static String convertDateFormat(Date sourceDate, SimpleDateFormat dateForm) {

        return dateForm.format(sourceDate);
    }


    /**
     * <pre>
     * get time stamp.
     * </pre>
     *
     * @return the String
     * @author Choo Kyoungil
     */
    public static String getTimeStamp() {

        return Long.toString(System.currentTimeMillis());
    }


    /**
     * getDayOfTheWeek.
     *
     * @return
     * @author Kim Donghyeong
     */
    public static int getDayOfTheWeek() {

        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * getDayOfTheWeekString.
     *
     * @return
     */
    public static String getDayOfTheWeekStringSet(int day) {

        System.out.println("day :::" + day);

        String korDayOfWeek = "";
        Calendar cal = Calendar.getInstance();
        if (day != 0) {
            // cal.add(Calendar.DATE, day);
            cal.set(Calendar.DATE, day);
        }

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                korDayOfWeek = "일";
                break;

            case 2:
                korDayOfWeek = "월";
                break;

            case 3:
                korDayOfWeek = "화";
                break;

            case 4:
                korDayOfWeek = "수";
                break;

            case 5:
                korDayOfWeek = "목";
                break;

            case 6:
                korDayOfWeek = "금";
                break;

            case 7:
                korDayOfWeek = "토";
                break;
        }

        return korDayOfWeek;
    }


    /**
     * getDayOfTheWeekString.
     *
     * @return
     */
    public static String getDayOfTheWeekString(int day) {

        System.out.println("day :::" + day);

        String korDayOfWeek = "";
        Calendar cal = Calendar.getInstance();
        if (day != 0) {
            cal.add(Calendar.DATE, day);
            // cal.set(Calendar.DATE, day);
        }

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                korDayOfWeek = "일";
                break;

            case 2:
                korDayOfWeek = "월";
                break;

            case 3:
                korDayOfWeek = "화";
                break;

            case 4:
                korDayOfWeek = "수";
                break;

            case 5:
                korDayOfWeek = "목";
                break;

            case 6:
                korDayOfWeek = "금";
                break;

            case 7:
                korDayOfWeek = "토";
                break;
        }

        return korDayOfWeek;
    }


    /**
     * 
     * changeTimeStamp
     *
     * @return
     * @author kim doo
     */
    public static String changeTimeStamp(String getDate) {

        SimpleDateFormat curFormat = null;

        if (getDate.trim().length() == 14) {
            curFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        } else {
            curFormat = new SimpleDateFormat("yyyyMMddHHmm");
        }

        long lMsec = 0;

        try {
            Date date = curFormat.parse(getDate);
            lMsec = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lMsec + "";
    }


    /**
     * GMT 시간을 Locale 시간으로 변경
     *
     * @param date
     *        GMT시간
     * @param hour
     * @param minute
     * @param dateFormat
     * @return
     * @author wonju choi
     */
    public static String convertToDispalyTimeZone(String date, int hour, int minute, String dateFormat) {

        Calendar calendar = Calendar.getInstance();
        try {
            Date sourceDate = new SimpleDateFormat(dateFormat).parse(date);
            calendar.setTime(sourceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minute);

        return convertDateFormat(calendar.getTime(), new SimpleDateFormat(dateFormat));
    }


    /**
     * 기준데이터를 받아 오늘날짜와 비교
     *
     * @param defDate
     *        : ex)"2018-02-01 00:00:00"
     * @return
     * @author yong
     */
    public static int compareNowDate(String defStringDate) {

        int ret = 0;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(BASE_DATE_FORMAT);
        Date defDate = new Date(); // 변경기준날짜
        Date nowDate = new Date(); // 오늘날짜

        try {
            if (defStringDate != null && !"".equals(defStringDate)) {
                defDate = df.parse(defStringDate);

                ret = (int) (nowDate.getTime() - defDate.getTime());
            }
        } catch (ParseException e) {
            log.error(e.toString());
        }

        return ret;
    }


    public static long diffOfDate(String begin, String end) {


        long diffDays = 0;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date beginDate = formatter.parse(begin);
            Date endDate = formatter.parse(end);

            long diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return diffDays;
    }


    public static String getYesterDay(String date) {

        long chStart = 0;
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

        if (date != "") {
            date = date.replaceAll("-", ""); // 사이사이의 -를 없애고 다 붙인다
            try {
                chStart = df.parse(date).getTime(); // 스트링형 date를 long형의 함수로 컨버트하고
                chStart -= 86400000; // 24*60*60*1000 하루치의 숫자를 빼준다

                // 일 시 분
                Date aa = new Date(chStart); // 이것을 다시 날짜형태로 바꿔주고
                date = df.format(aa); // 바꿔준 날짜를 yyyyMMdd형으로 바꾼후

                // 스트링으로 다시 형변환을해서 date에 대입
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date; // date를 반환하면 하루 전날이 스트링형으로 반환됨
    }

}
