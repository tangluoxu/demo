

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtils {

//    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>(){
		@Override      
	       protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("yyyy-MM-dd");     
	      } 
	};
	public static final String FORMATYYYYMMMDD = "yyyy-MM-dd||yyyy.MM.dd||yyyy/MM/dd";
	public static final String FORMATYYYYMMDD_Z = "yyyy年MM月dd日";
	public static final String FORMATYYYYMMDDHHMMSS = "yyyy.MM.dd HH:mm:ss||yyyy/MM/dd HH:mm:ss||yyyy-MM-dd HH:mm:ss";
	public static final String FORMATYYYYMMDDHHMMSS_Z = "yyyy年MM月dd日 HH时mm分ss秒";
	public static final String FORMATUNCR = "格式不符合要求";
	
	
    public static String getCurDate() {
        return sdf.get().format(new Date());
    }

    /**
     * 描述：格式化时间为yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     * @author djc
     * @version 1.0
     */
    public static String getDateTime(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.format(date).toString();
    }


    /**
     * 根据特定的格式化规格来对传来的时间进行格式化
     *
     * @param date   需要格式化的时间
     * @param format 格式化的规格
     * @return the string
     */
    public static String getForMatTime(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getForMatTimeWithLocate(Date date, String format, Locale locate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, locate);
        return simpleDateFormat.format(date);
    }

    /**
     * 求出两个日期的月份差
     *
     * @param endTime
     * @param startTime
     * @return
     */
    public static int getMonths(String endTime, String startTime) {
        String endYear = endTime.substring(0, 4);
        String startYear = startTime.substring(0, 4);
        String endMonth = endTime.substring(5, 7);
        String startMonth = startTime.substring(5, 7);
        int len = (Integer.valueOf(endYear) - Integer.valueOf(startYear)) * 12 + (Integer.valueOf(endMonth) - Integer.valueOf(startMonth)) + 1;
        return len;
    }


    public static int getYears(Date startTime, Date endTime) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(endTime);
        int endYear = c1.get(Calendar.YEAR);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(startTime);
        int startYear = c2.get(Calendar.YEAR);

        return endYear - startYear;
    }

    /**
     * 日期加减月份
     *
     * @param date
     * @param months
     * @return
     */
    public static Date calDateMonth(Date date, int months) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, months);
        return cl.getTime();
    }

    /**
     * 日期加减年份
     *
     * @param date
     * @param years
     * @return
     */
    public static Date calDateYear(Date date, int years) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.YEAR, years);
        return cl.getTime();
    }


    /**
     * 日期加减日份
     *
     * @param date 要加减的日期
     * @param day  要加减的天数
     * @return 返回结果
     */
    public static Date calDateDay(Date date, int day) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DAY_OF_WEEK, day);
        return cl.getTime();
    }

    /**
     * 两个日期之间的日期差
     *
     * @param beginDate 开始
     * @param endDate   结束
     * @return
     */
    public static Long subDate(Date beginDate, Date endDate) {
        return (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24);
    }


    /**
     * 获取传入的时间的上周一
     *
     * @param date the date
     * @return the previous weekday
     */
    public static Date getPreviousWeekday(Date date) {
        Date a = org.apache.commons.lang3.time.DateUtils.addDays(date, -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(a);
        cal.add(Calendar.WEEK_OF_YEAR, -1);//一周
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
        return cal.getTime();
//		return DateFormatUtils.format(cal.getTime(),DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    }


    /**
     * 获取传入的时间的上周日
     *
     * @param date the date
     * @return the previous week sunday
     */
    public static Date getPreviousWeekSunday(Date date) {
        Date a = org.apache.commons.lang3.time.DateUtils.addDays(date, -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(a);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
//		return DateFormatUtils.format(cal.getTime(),DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    }


    /**
     * 获取传入时间的周一.
     *
     * @param date the date
     * @return the string
     */
    public static Date getNowWeekMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);//解决周日会出现并到周一的问题
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
        return calendar.getTime();
//		return DateFormatUtils.format(calendar.getTime(),DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    }


    /**
     * 获取传人时间的周日
     *
     * @param date the date
     * @return the string
     */
    public static Date getNowWeekSunday(Date date) {
        Date a = org.apache.commons.lang3.time.DateUtils.addDays(date, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(a);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return calendar.getTime();
//		return DateFormatUtils.format(calendar.getTime(),DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    }


    /**
     * 解析字符串为 时间类型
     *
     * @param date     the date
     * @param patterns the patterns 字符串模式
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date format(String date, String patterns) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(patterns);
        return sdf.parse(date);
    }

    /**
     * 解析字符串为 时间类型 带地区
     *
     * @param date     the date
     * @param patterns the patterns 字符串模式
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date format(String date, String patterns, Locale locale) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(patterns, locale);
        return sdf.parse(date);
    }


    /**
     * 生成指定的日期时间（例：传进来的date为2016-10-27，addMonth为0，monthDay为15,那么返回为2016-10-15
     *
     * @param date     the date
     * @param addMonth the add month
     * @param monthDay the month day
     * @return the date
     */
    public static Date genericSpecdate(Date date, int addMonth, int monthDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, addMonth);
        cal.set(Calendar.DAY_OF_MONTH, monthDay);
        return cal.getTime();
    }


    /**
     * 查找本年开始时间
     *
     * @return the current year start time
     */
    public static Date getCurrentYearStartTime() {
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus);
        Date date = currentDate.getTime();
        date = org.apache.commons.lang3.time.DateUtils.setHours(date, 0);
        date = org.apache.commons.lang3.time.DateUtils.setMinutes(date, 0);
        date = org.apache.commons.lang3.time.DateUtils.setSeconds(date, 0);
        return date;
    }
    /**
     * 每年的一月一号
     * @return
     */
    public static String getCurrentYearStartTimeStr() {
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus);
        Date date = currentDate.getTime();
        date = org.apache.commons.lang3.time.DateUtils.setHours(date, 0);
        date = org.apache.commons.lang3.time.DateUtils.setMinutes(date, 0);
        date = org.apache.commons.lang3.time.DateUtils.setSeconds(date, 0);
        return sdf.get().format(date);
    }

    private static int getYearPlus() {
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天
        cd.set(Calendar.DAY_OF_YEAR, 1);//把日期设为当年第一天
        cd.roll(Calendar.DAY_OF_YEAR, -1);//把日期回滚一天。
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        if (yearOfNumber == 1) {
            return -MaxYear;
        } else {
            return 1 - yearOfNumber;
        }
    }


    /**
     * 查找本年结束时间.
     *
     * @return the current year end time
     */
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }


    /**
     * 查找本月末的时间
     *
     * @return the times monthnight
     */
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }
    
    /**
     * 查找本月初的时间
     *
     * @return the times monthnight
     */
    public static Date getTimesMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    
    /**
     * 查找下一年当前月月末的时间
     */
    public static Date getNextYearTimesMonthLast() {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, 1); 
		//获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
         //设置日历中月份的最大天数  
        cal.set(Calendar.DAY_OF_MONTH, lastDay); 
        return cal.getTime();
    }

    public static Date getValidDate(String dateStr, String dateFormat) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            date = format.parse(dateStr);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }


    /**
     * 获得本月第一天的时间
     *
     * @return
     */
    public static Date getMonthFirst() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }


    /**
     * 获得本季度第一天
     */
    public static Date getQuarterFirstDay() {
        Calendar cal = Calendar.getInstance();
        int calmonth = cal.get(Calendar.MONTH);
        int month;

        if (calmonth >= 0 && calmonth <= 2)
            month = -1;
        else if (calmonth >= 2 && calmonth <= 5)
            month = 2;
        else if (calmonth >= 6 && calmonth <= 8)
            month = 5;
        else
            month = 8;
        cal.set(cal.get(Calendar.YEAR), month, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }


    /**
     * 获得下个季度的第一天
     *
     * @return
     */
    public static Date getNextQuarterFirstDay() {
        Calendar cal = Calendar.getInstance();
        int calmonth = cal.get(Calendar.MONTH);
        int month;

        if (calmonth >= 0 && calmonth <= 2)
            month = -1;
        else if (calmonth >= 2 && calmonth <= 5)
            month = 2;
        else if (calmonth >= 6 && calmonth <= 8)
            month = 5;
        else
            month = 8;
        cal.set(cal.get(Calendar.YEAR), month + 3, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }


    /**
     * 获取当天0点的时间
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 判断传进来的日期是否含有小时，还是只是单纯的日期
     *
     * @param date 要判断的日期
     * @return
     */
    public static Boolean isHaveHour(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int hour = cl.get(Calendar.HOUR_OF_DAY);
        return hour != 0;
    }

    /**
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {
        int season = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }


    /**
     * 取得季度已过天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfSeason(Date date) {
        int day = 0;


        Date[] seasonDates = getSeasonDate(date);


        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);


        if (month == Calendar.JANUARY || month == Calendar.APRIL
                || month == Calendar.JULY || month == Calendar.OCTOBER) {// 季度第一个月
            day = getPassDayOfMonth(date);
        } else if (month == Calendar.FEBRUARY || month == Calendar.MAY
                || month == Calendar.AUGUST || month == Calendar.NOVEMBER) {// 季度第二个月
            day = getDayOfMonth(seasonDates[0])
                    + getPassDayOfMonth(date);
        } else if (month == Calendar.MARCH || month == Calendar.JUNE
                || month == Calendar.SEPTEMBER || month == Calendar.DECEMBER) {// 季度第三个月
            day = getDayOfMonth(seasonDates[0]) + getDayOfMonth(seasonDates[1])
                    + getPassDayOfMonth(date);
        }
        return day;
    }


    /**
     * 取得季度天数
     *
     * @param date
     * @return
     */
    public static int getDayOfSeason(Date date) {
        int day = 0;
        Date[] seasonDates = getSeasonDate(date);
        for (Date date2 : seasonDates) {
            day += getDayOfMonth(date2);
        }
        return day;
    }

    /**
     * 取得月已经过的天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得月天数
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得年天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfYear(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 取得季度月
     *
     * @param date
     * @return
     */
    public static Date[] getSeasonDate(Date date) {
        Date[] season = new Date[3];


        Calendar c = Calendar.getInstance();
        c.setTime(date);


        int nSeason = getSeason(date);
        c.set(Calendar.DAY_OF_MONTH, 1);  //设置日期
        if (nSeason == 1) {// 第一季度
            c.set(Calendar.MONTH, Calendar.JANUARY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.FEBRUARY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MARCH);
            season[2] = c.getTime();
        } else if (nSeason == 2) {// 第二季度
            c.set(Calendar.MONTH, Calendar.APRIL);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MAY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.JUNE);
            season[2] = c.getTime();
        } else if (nSeason == 3) {// 第三季度
            c.set(Calendar.MONTH, Calendar.JULY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.AUGUST);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.SEPTEMBER);
            season[2] = c.getTime();
        } else if (nSeason == 4) {// 第四季度
            c.set(Calendar.MONTH, Calendar.OCTOBER);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.NOVEMBER);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.DECEMBER);
            season[2] = c.getTime();
        }
        return season;
    }

    /**
     * 获取date月份
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH)+ 1;
    }
    /**
     * 获取date年份
     */
    public static int getYear(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.YEAR);
    }
    
    /**
     * 获取date年月(yyyy-MM-dd)
     */
    public static String getYearAndMonth(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year = c1.get(Calendar.YEAR);
        int month = c1.get(Calendar.MONTH)+ 1;
        if(month<10){
        	return year+"-"+"0"+month;
        }else{
        	return year+"-"+month;
        }
        
    }
    
    /**
     * 获取date该年的开始时间：2017-01-01 00:00:00
     */
    public static Date getCurYearStartTime(Date date,Integer cycleOffsetDay) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(date);
    	c1.set(Calendar.MONTH, 0);//设置第一个月01
    	c1.set(Calendar.DAY_OF_MONTH, 1); //设置日期第一天01
    	c1.set(Calendar.HOUR_OF_DAY, 0);
    	c1.set(Calendar.MINUTE, 0);
    	c1.set(Calendar.SECOND, 0);
    	//日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
        return c1.getTime();
    }
    
    /**
     * 获取date该年的结束时间：2017-12-31 23:59:59
     */
    public static Date getCurYearEndTime(Date date,Integer cycleOffsetDay) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(date);
    	c1.set(Calendar.MONTH, 11);//设置第一个月01
    	c1.set(Calendar.DAY_OF_MONTH, 31); //设置日期第一天01
    	c1.set(Calendar.HOUR_OF_DAY, 23);
    	c1.set(Calendar.MINUTE, 59);
    	c1.set(Calendar.SECOND, 59);
    	//日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
        return c1.getTime();
    }
    
    /**
     * 获取date所在季度的开始时间：2017-10-01 00:00:00
     * 1-3,4-6,7-9,10-12
     */
    public static Date getQuarterFirstDay(Date date,Integer cycleOffsetDay) {
        Calendar         c1 = Calendar.getInstance();
        c1.setTime(date);
        int calmonth = c1.get(Calendar.MONTH)+1;
        int month;
        if (calmonth >= 1 && calmonth <= 3){//第1季度：开始于1月
        	month = 1;
        }else if (calmonth >= 4 && calmonth <= 6){//第2季度：开始于3月
            month = 4;
        }else if (calmonth >= 7 && calmonth <= 9){//第3季度：开始于7月
            month = 7;
        }else{//第4季度
        	month = 10;
        }
        c1.set(Calendar.MONTH, (month-1));
    	c1.set(Calendar.DAY_OF_MONTH, 1); //设置日期第一天01
    	c1.set(Calendar.HOUR_OF_DAY, 0);
    	c1.set(Calendar.MINUTE, 0);
    	c1.set(Calendar.SECOND, 0);
    	//日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
        return c1.getTime();
    }
    
    /**
     * 获取date所在季度的结束时间：2017-12-31 23:59:59
     * 1-3,4-6,7-9,10-12
     */
    public static Date getQuarterEndDay(Date date,Integer cycleOffsetDay) {
    	Calendar         c1 = Calendar.getInstance();
    	c1.setTime(date);
    	int calmonth = c1.get(Calendar.MONTH)+1;
    	int month;
    	if (calmonth >= 1 && calmonth <= 3){//第1季度：开始于1月
    		month = 3;
    	}else if (calmonth >= 4 && calmonth <= 6){//第2季度：开始于3月
    		month = 6;
    	}else if (calmonth >= 7 && calmonth <= 9){//第3季度：开始于7月
    		month = 9;
    	}else{//第4季度
    		month = 12;
    	}
    	c1.set(Calendar.MONTH, (month-1));
    	c1.set(Calendar.DAY_OF_MONTH, 31); //设置日期31
    	c1.set(Calendar.HOUR_OF_DAY, 23);
    	c1.set(Calendar.MINUTE, 59);
    	c1.set(Calendar.SECOND, 59);
    	//日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
    	return c1.getTime();
    }
    
    /**
     * 获取该date的月份第一天：2017-01-01 00:00:00
     */
    public static Date getMonthFirst(Date date,Integer cycleOffsetDay) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
    	c1.set(Calendar.DAY_OF_MONTH, 1); //设置日期第一天为01
    	c1.set(Calendar.HOUR_OF_DAY, 0);
    	c1.set(Calendar.MINUTE, 0);
    	c1.set(Calendar.SECOND, 0);
        //日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
        return c1.getTime();
    }
    
    /**
     * 获取该date的月份第一天：2017-02-28 23:59:59
     */
    public static Date getMonthEnd(Date date,Integer cycleOffsetDay) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTime(date);
    	//先月份加1并且设置第一天为1，然后再日期减1天，得到当前月最后一天时间
    	c1.add(Calendar.MONTH, 1); 
    	c1.set(Calendar.DAY_OF_MONTH, 1); //设置日期第一天为01
    	c1.add(Calendar.DATE, -1);//
    	
    	c1.set(Calendar.HOUR_OF_DAY, 23);
    	c1.set(Calendar.MINUTE, 59);
    	c1.set(Calendar.SECOND, 59);
    	//日期偏移量cycleOffsetDay
    	if(cycleOffsetDay!=null&&cycleOffsetDay!=0){
    		c1.add(Calendar.DATE, cycleOffsetDay);
    	}
    	return c1.getTime();
    }
    
    /**
     * 计算日期并设置时分秒
     */
    public static Date calDateDay(Date date, int day,int... hms) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DATE, day);
        if(hms!=null&&hms.length>0){
        	int len=hms.length;
        	if(len>0){
        		cl.set(Calendar.HOUR_OF_DAY, hms[0]);
        	}
        	if(len>1){
        		cl.set(Calendar.MINUTE, hms[1]);
        	}
        	if(len>2){
        		cl.set(Calendar.SECOND, hms[2]);
        	}
        }
        return cl.getTime();
    }
    
    /**
	 * 将不同格式的日期字符串转换为日期对象
	 */
	public static Date convertStringtoDate(String time) throws ParseException{
		Date date = null;
		SimpleDateFormat dateFormat =null;
		String format = judgeFormat(time);
		if(FORMATYYYYMMMDD.equals(format)||FORMATYYYYMMDD_Z.equals(format)){
			dateFormat =new SimpleDateFormat("yyyy-MM-dd");
		}else if(FORMATYYYYMMDDHHMMSS.equals(format)||FORMATYYYYMMDDHHMMSS_Z.equals(format)){
			dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else{
			dateFormat =new SimpleDateFormat("yyyy-MM-dd");
		}
		// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
		dateFormat.setLenient(false);
		time = convertString(format,time);//将不同格式日期转换为-格式
		date = dateFormat.parse(time);
		return date;
	}
	
	/**
	 * 判断字符串日期的格式
	 */
	private static String judgeFormat(String time){
		String regex1 = "^\\s*\\d{4}(-|/|\\.){1}\\d{2}(-|/|\\.){1}\\d{2}\\s*";
		String regex2 = "^\\s*\\d{4}(-|/|\\.){1}\\d{2}(-|/|\\.){1}\\d{2}\\s+(\\d{2}:){2}\\d{2}\\s*";
		String regex3 = "^\\s*\\d{4}([\u4e00-\u9fa5]){1}\\d{2}([\u4e00-\u9fa5]){1}\\d{2}([\u4e00-\u9fa5]){1}\\s*";
		String regex4 = "^\\s*\\d{4}([\u4e00-\u9fa5]){1}(\\d{2}([\u4e00-\u9fa5]){1}){2}\\s*(\\d{2}([\u4e00-\u9fa5]){1}){3}\\s*";
		Pattern p1 = Pattern.compile(regex1);
		Matcher m1 = p1.matcher(time);
		if(m1.matches()){
			return FORMATYYYYMMMDD;
		}
		Pattern p2 = Pattern.compile(regex2);
		Matcher m2 = p2.matcher(time);
		if(m2.matches()){
			return FORMATYYYYMMDDHHMMSS;
		}
		Pattern p3 = Pattern.compile(regex3);
		Matcher m3 = p3.matcher(time);
		if(m3.matches()){
			return FORMATYYYYMMDD_Z;
		}
		Pattern p4 = Pattern.compile(regex4);
		Matcher m4 = p4.matcher(time);
		if(m4.matches()){
			return FORMATYYYYMMDDHHMMSS_Z;
		}
		return FORMATUNCR;
	}
	
	/**
	 * 将不同格式日期转换为-格式
	 */
	private static String convertString(String format,String time){
		if(FORMATYYYYMMMDD.equals(format)||FORMATYYYYMMDDHHMMSS.equals(format)){
			time = time.replaceAll("/|\\.", "-");
		}else if(FORMATYYYYMMDD_Z.equals(format)){
			time = time.replaceAll("[\u4e00-\u9fa5]", "-");
			time = time.substring(0, time.length() - 1);
		}else if(FORMATYYYYMMDDHHMMSS_Z.equals(format)){
			time = time.replaceAll("[\u4e00-\u9fa5]", "-");
			String[] timeArray = time.split("-");
			time = timeArray[0] + "-" + timeArray[1] + "-" + timeArray[2] + " " + timeArray[3] + ":" + timeArray[4] +":"+ timeArray[5];
		}
		return time;
	}
	
	
	public static List<Integer> getYearsList(Date time,int interval) {
    	List<Integer> yearList=new ArrayList<Integer>();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(time);
        int year = c1.get(Calendar.YEAR);
        yearList.add(year);
        for(int i=1;i<=interval;i++){
        	yearList.add(year+i);
        }
        return yearList;
    }
    
    public static boolean isLegalDate(String dateStr,SimpleDateFormat formater){
    	boolean isLegal=false;
    	try {
			formater.setLenient(false);
			formater.parse(dateStr);
			isLegal=true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return isLegal;
    }
    
    public static String monthIndexName(int monthIndex){
    	switch (monthIndex) {
		case 1:
			return "one";
		case 2:
			return "two";
		case 3:
			return "three";
		case 4:
			return "thour";
		case 5:
			return "five";
		case 6:
			return "six";
		case 7:
			return "seven";
		case 8:
			return "eight";
		case 9:
			return "nine";
		case 10:
			return "ten";
		case 11:
			return "eleven";
		case 12:
			return "twelve";
		}
    	return null;
    }
    
    /**
     * 判断当前时间是否在某个时间段内(yyyy-MM-dd)
     * @param startTime
     * @param endTime
     * @param nowTime
     * @return
     * @throws ParseException 
     */
    public static boolean belongCalendar(String startTime,String endTime,String nowTime) throws ParseException{
    	//设置当前时间
    	 Calendar date = Calendar.getInstance();
    	 date.setTime(sdf.get().parse(nowTime));
    
    	 Calendar begin = Calendar.getInstance();
    	 begin.setTime(sdf.get().parse(startTime));
    
    	 Calendar end = Calendar.getInstance();
    	 end.setTime(sdf.get().parse(endTime));
    	
    	 if (date.after(begin) && date.before(end)) {
    		 return true;
    	} else {
    		return false;
    	}

    }
    
    /**
     * 获取12个月份字符串列表
     * @return
     */
    public static List<String> getMonthList(){
    	List<String> list=new ArrayList<String>();
    	for(int i=1;i<=12;i++){
    		list.add(i>9?String.valueOf(i):("0")+i);
    	}
    	return list;
    }
    
}
