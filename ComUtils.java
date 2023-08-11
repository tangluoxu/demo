

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共方法类
 * @author 作者 hwb
 * @version 版本号 2015-6-21
 */
public class ComUtils {

//	public static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	public static final ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override      
	       protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("yyyy-MM-dd");     
	      } 
	};
	
//	public static SimpleDateFormat yearMonthFormat=new SimpleDateFormat("yyyy-MM");
	public static final ThreadLocal<SimpleDateFormat> yearMonthFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override      
	       protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("yyyy-MM");     
	      } 
	};
	
//	public static SimpleDateFormat allDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final ThreadLocal<SimpleDateFormat> allDateFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override      
	       protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
	      } 
	};
	
//	public static SimpleDateFormat hmFormat=new SimpleDateFormat("HH:mm");
	public static final ThreadLocal<SimpleDateFormat> hmFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override      
	       protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("HH:mm");     
	      } 
	};
	
	public static final ThreadLocal<SimpleDateFormat> yearFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override      
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy");     
		} 
	};
	
	public final static String SEPARATOR="/";
	
	//月份的英文简称
	public final static String[] monthSimEnglish=new String[]{"","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
	//星期的英文简称
	public final static String[] weekSimEnglish=new String[]{"","SUN","MON","TUE","WED","THU","FRI","SAT"};
	public final static String[] weekSimChinese=new String[]{"","星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
	public final static String[] moneyChinese=new String[]{"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
	
	/*
	 * 获取月份的英文简称
	 */
	public static String getMonthSimEnglish(int month){
		return monthSimEnglish[month];
	}
	
	
	/*
	 * 获取星期的英文简称
	 * //dayOfWeek：1周日 2星期一 3星期二 4星期三 5星期四 6星期五 7星期六
	 */
	public static String getWeekSimEnglish(int dayOfWeek){
		dayOfWeek=dayOfWeek%7;
		if(dayOfWeek==0){
			dayOfWeek=7;//周六
		}
		return weekSimEnglish[dayOfWeek];
//		五  六   日   一   二    三   四    五   六    日       一     二      三     四      五       六     日
//		6     7     8     9     10   11   12    13    14     15      16      17      18     19       20       21     22
//		1     2     3     4     5     6     7      8      9       10      11      12      13      14      15      16      17      18 19 20 21 22 23 24 25 26 27 28 
	}
	
	
	/*
	 * 获取某年某月有多少天
	 */
	public static int getDays(int year,int month){
		int days=28;
		if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){//31天
			days=31;
		}else if(month==4||month==6||month==9||month==11){//30天
			days=30;
		}else if(month==2&&(year%4==0&&year%100!=0||year%400==0)){//闰年、2月有29天
			days=29;
		}
		return days;
	}
	
	public static  List<String> getArrayList(int start,int end){
		List<String> list=new ArrayList<String>();
		for(int i=start;i<=end;i++){
			list.add(String.valueOf(i));
		}
		return list;
	}
	
	
	
	/**
	 * 获取分页数量
	 */
	public static int getPageNum(int total,int pageSize){
		int count=total/pageSize;
		if(total%pageSize!=0){
			count+=1;
		}
		return count;
	}
	
	/*
	 * 获取日期的年份、月份、日等值
	 * ComUtils.getYearMonthDay(new Date(), new int[]{Calendar.YEAR,Calendar.MONTH})
	 */
	public static int[] getYearMonthDay(Date date,int... params){
		Calendar time =Calendar.getInstance();
		time.setTime(date);
		int len=params.length;
		int[] ret=new int[len];
		for(int i=0;i<len;i++){
			if(Calendar.MONTH==params[i]){
				ret[i]=time.get(params[i])+1;//月份需要加1
			}else{
				ret[i]=time.get(params[i]);
			}
			
		}
		return ret;
	}
	
	
	
	
	/*
	 * 获取某年某月的星期分布【7X5的分布格局】
	 */
	public static List<List<String>> getMonthDayOfWeek(int year,int month) {
		List<List<String>> allList=new ArrayList<List<String>>();
		try {
			int days=28;
			if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){//31天
				days=31;
			}else if(month==4||month==6||month==9||month==11){//30天
				days=30;
			}else if(month==2&&(year%4==0&&year%100!=0||year%400==0)){//闰年、2月有29天
				days=29;
			}
			Calendar time =Calendar.getInstance();
			String s=year+"-"+month+"-01";
			time.setTime(simpleDateFormat.get().parse(s));

			int day=time.get(Calendar.DAY_OF_WEEK);//1周日 2星期一 3星期二 4星期三 5星期四 6星期五 7星期六
			int index=day-1;
			
			List<String> list=new ArrayList<String>();
			for(int i=0,len=index;i<len;i++){
				list.add("");
			}
			
			for(int i=0;i<days;i++){
				index++;
				list.add(String.valueOf(i+1));
				if(index%7==0){
					allList.add(list);
					list=new ArrayList<String>();
				}
			}
			
			for(int i=0,len=35-index;i<len;i++){
				index++;
				list.add("");
				if(index%7==0){
					allList.add(list);
					list=new ArrayList<String>();
				}
			}
		} catch (Exception e) {
		}
		return allList;
	}
	
	
	
    /*
     * 日期的计算
     */
    public static Date calDayOfMonth(Date date,int n){
    	Calendar rightNow = Calendar.getInstance();
    	rightNow.setTime(date);
    	rightNow.add(Calendar.DAY_OF_MONTH,n);
    	return rightNow.getTime();
    }
    
    public static Date calMonth(Date date,int n){
    	Calendar rightNow = Calendar.getInstance();
    	rightNow.setTime(date);
    	rightNow.add(Calendar.MONTH,n);
    	return rightNow.getTime();
    }
    
    public static Date calYear(Date date,int n){
    	Calendar rightNow = Calendar.getInstance();
    	rightNow.setTime(date);
    	rightNow.add(Calendar.YEAR,n);
    	return rightNow.getTime();
    }
	
    /*
     * 判断是否包含中文
     */
    public static final boolean isChinese(String strName) {  
        char[] ch = strName.toCharArray();  
        for (int i = 0; i < ch.length; i++) {  
            char c = ch[i];  
            if (isChinese(c)) {  
                return true;  
            }  
        }  
        return false;  
    }  
    
 // GENERAL_PUNCTUATION 判断中文的“号  
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号  
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号  
    private static final boolean isChinese(char c) {  
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }  
    
    /*
     * 日期格式转换为字符串并获取星期几
     */
    public static String dateToStr(Date nowDate,String format){
    	String weekStr="";
    	if(StringUtils.isNotEmpty(format)&&format.contains("E")){
    		Calendar time =Calendar.getInstance();
    		time.setTime(nowDate);
    		int dayOfWeek=time.get(Calendar.DAY_OF_WEEK);
    		dayOfWeek=dayOfWeek%7;
    		if(dayOfWeek==0){
    			dayOfWeek=7;//周六
    		}
    		weekStr=weekSimChinese[dayOfWeek];
    		format= StringUtils.replace(format, "E", "");
    	}
    	SimpleDateFormat df=new SimpleDateFormat(format);
    	return df.format(nowDate)+weekStr;
    }
    

    public static List<String> getMoneyChinese(String money,int weishu){
    	List<String> list=new ArrayList<String>();
    	if(StringUtils.isNotEmpty(money)){
    		int index=money.indexOf(".");
    		if(index>0){
    			money=money.substring(0,index);
    		}
    		money= String.format("%0"+weishu+"d", Integer.valueOf(money));  
    		char[] numArray=money.toCharArray();
    		for(int i=0,len=numArray.length;i<len;i++){
    			list.add(moneyChinese[Integer.valueOf(String.valueOf(numArray[i]))]);
    		}
    	}
    	return list;
    }
    
    /*
     * 获取门票的座位
     */
    public static String[] getRowColNum(String info){
		String[] ret=new String[]{"0","0"};
		if(StringUtils.isNotEmpty(info)){//S区2排29座
			info= StringUtils.replace(info, " ", "");
			info= StringUtils.trim(info);
			String[] ss=info.split("区");
			if(ss.length>1){
				ss=ss[1].split("排");
				if(ss.length>1){
					ret[0]=ss[0];
					ss=ss[1].split("座");
					ret[1]=ss[0];
				}
			}
		}
		return ret;
	}
    
    
    /*
     * 获取文件的相对路径，并且开头不能以斜线
     */
    public static String getFileVirPath(String virPath,String fileName){
    	fileName=virPath+SEPARATOR+fileName;
		if(fileName.startsWith(SEPARATOR)){
			fileName=fileName.substring(1);
		}
		return fileName;
    }
    
    public static String reDealTextArea(String content){
    	if(StringUtils.isNotEmpty(content)){
    		content= StringUtils.replace(content, "\r", "");
    		content= StringUtils.replace(content, "\n", "");
    	}
    	return content;
    }
    
    
    
    public static List<Byte> getByteByStr(String strArry){
		List<Byte> list=null;
		if(StringUtils.isNotEmpty(strArry)){
			list=new ArrayList<Byte>();
			String[] array=strArry.split(",");
			for(String s:array){
				list.add(Byte.valueOf(s));
			}
		}
		return list;
	}
	
    public static List<String> getByStr(String strArry){
		List<String> list=null;
		if(StringUtils.isNotEmpty(strArry)){
			list=new ArrayList<String>();
			String[] array=strArry.split(",");
			for(String s:array){
				list.add(s);
			}
		}
		return list;
	}
    
    public static List<String> getByStr(String strArry,String splitStr){
    	List<String> list=null;
    	if(StringUtils.isNotEmpty(strArry)){
    		list=new ArrayList<String>();
    		String[] array=strArry.split(splitStr);
    		for(String s:array){
    			if(StringUtils.isNotBlank(s)){
    				list.add(s);
    			}
    		}
    	}
    	return list;
    }
    
    
    public static List<String> getTextAreaList(String strArry){
    	List<String> list=new ArrayList<String>();
    	if(StringUtils.isNotEmpty(strArry)){
    		strArry= StringUtils.replace(strArry, "\r\n", "@&Temp123");
    		String[] array=strArry.split("@&Temp123");
    		for(String s:array){
    			list.add(s);
    		}
    	}
    	return list;
    }
    
    
    public static List<Long> getLongByStr(String strArry){
    	List<Long> list=null;
    	if(StringUtils.isNotEmpty(strArry)){
    		list=new ArrayList<Long>();
    		String[] array=strArry.split(",");
    		for(String s:array){
    			if(StringUtils.isNotEmpty(s)&& StringUtils.isNumeric(s)){
    				list.add(Long.valueOf(s));
    			}
    		}
    	}
    	return list;
    }
    
    /**
     * 多选选择，去除空的字符串
     * @param strArry
     * @param splitStr
     * @return
     */
    public static String strArryTrim(String strArry,String splitStr){
    	if(StringUtils.isNotEmpty(strArry)){
    		String[] tempAry=strArry.split(splitStr);
			StringBuffer buf=new StringBuffer();
			for(String temp:tempAry){
				if(StringUtils.isNotBlank(temp)){
					buf.append(splitStr).append(temp);
				}
			}
			return buf.substring(1);
    	}
    	return strArry;
    }
    
    
    public static Map<Long,Long> getMapByStr(String strArry){
    	Map<Long,Long> map=null;
    	if(StringUtils.isNotEmpty(strArry)){
    		map=new HashMap<Long,Long>();
    		String[] array=strArry.split(",");
    		for(String s:array){
    			if(StringUtils.isNotEmpty(s)&& StringUtils.isNumeric(s)){
    				map.put(Long.valueOf(s), Long.valueOf(s));
    			}
    		}
    	}
    	return map;
    }
    
    
    
    /**
	 * 用户备份目录的根目录
	 * @param userId
	 * @return
	 */
	public static String getUserPath(String basePath,String userId) {
	    int len = userId.length();

        StringBuilder userDir = new StringBuilder(basePath);
        for (int i = 0; i < len; i += 2) {
            int twoAfterI = i + 2;
            userDir.append(userId.substring(i, (twoAfterI < len ? twoAfterI : len)));
            userDir.append("/");
        }
        return userDir.toString();
    }
	
	public static String getDateHmStr(Date date){
		if(date!=null){
			return hmFormat.get().format(date);
		}
		return "";
	}
	
	
	public static String listToStr(List<Long> list){
		String tempString="";
		if(list!=null&&list.size()>0){
			for(Long id:list){
				tempString+=","+id;
			}
			if(StringUtils.isNotEmpty(tempString)){
				tempString=tempString.substring(1);
			}
		}
		return tempString;
	}
	
	
	
	/*
	 * 字符串分割：exportProject=username_用户名称,sex_性别
	 */
	public static String[] splitStr(String exportProject,String firstSplit,String secondSplit){
		String[] exportProjectArray=exportProject.split(firstSplit);
		String projectName="";
		String projectDesc="";
		for(String temp:exportProjectArray){
			String[] arr=temp.split(secondSplit);
			projectName+=","+arr[0];
			projectDesc+=","+arr[1];
		}
		if(StringUtils.isNotEmpty(projectName)){
			projectName=projectName.substring(1);
		}
		if(StringUtils.isNotEmpty(projectDesc)){
			projectDesc=projectDesc.substring(1);
		}
		return new String[]{projectName,projectDesc};
	}
	
	
	/*
	 * 将组织的字符串转换【0/2343/3432/】转换为List集合
	 */
	public static List<Long> getOrganizationIdByStr(String strArry){
    	List<Long> list=null;
    	if(StringUtils.isNotEmpty(strArry)&&!"null".equals(strArry)){
    		list=new ArrayList<Long>();
    		String[] array=strArry.split("/");
    		for(String s:array){
    			if(StringUtils.isNotEmpty(s)&&!"0".equals(s)){
    				list.add(Long.valueOf(s));
    			}
    		}
    	}
    	return list;
    }
	
	
	public static String getHttpStartUrl(String url,String prefixHttp){
		if(StringUtils.isNotEmpty(url)&&!StringUtils.startsWithIgnoreCase(url, "http")){
			return prefixHttp+"/"+url;
		}
		return url;
	}
	
	/*
	 * 其中imei是15位、每位由1-9中的数字组成。
		meid是14位。MEID由14个十六进制字符标识，，第15位为校验位，不参与空中传输。             手机只能获得14位
	 */
	public static boolean isImei(String temp){
		if(StringUtils.isNotEmpty(temp)&& StringUtils.isNumeric(temp)){
			return true;
		}
		return false;
	}
	
	/*
	 * 统计key在str字符串出现的次数
	 */
	public static int  countSubStr(String str,String key){
        int count = 0;
        int index = 0;
        while((index=str.indexOf(key,index))!=-1){
            index = index+key.length();
            count++;
        }
        return count;
    }
	
	/**
	 * 截取str中key出现needCount次的字符串
	 * @param str
	 * @param key
	 * @param needCount
	 * @return
	 */
	public static String subStr(String str,String key,int needCount){
		if(StringUtils.isNotBlank(str)){
			int count = 0;
			int index = 0;
			while((index=str.indexOf(key,index))!=-1){
				index = index+key.length();
				count++;
				if(needCount==count){
					return str.substring(0, index);
				}
			}
		}
		return str;
	}

	
	public static int getLevel(String parentIds){
		int count = 0;
		if(StringUtils.isNoneBlank(parentIds)){
			String separate="/";
			int index = 0;
			while((index=parentIds.indexOf(separate,index))!=-1){
				index = index+separate.length();
				count++;
			}
		}
		return count;
	}
	
	/*
	 * 号码去除空字符串和横线
	 */
	public static String dealPhoneNum(String phoneNum){
		if(StringUtils.isNotBlank(phoneNum)){
			phoneNum= StringUtils.trim(phoneNum);
			phoneNum= StringUtils.remove(phoneNum, "-");
			phoneNum= StringUtils.remove(phoneNum, " ");
			phoneNum= StringUtils.remove(phoneNum, "+");
			if(phoneNum.startsWith("86")){
				phoneNum=phoneNum.substring(2);
			}else if(phoneNum.startsWith("+86")){
				phoneNum=phoneNum.substring(3);
			}else if(phoneNum.startsWith("0086")){
				phoneNum=phoneNum.substring(4);
			}
			return phoneNum;
		}
		return null;
	}
	
	
	/*
	 * 获取链接中/出现count次数前面的字符串
	 */
	public static String getUrlPrefix(String currentUrl,int count){
		String url="";
		String[] temp=currentUrl.split("/");//排除添加、修改、删除等链接
		int i=0;
		for(String s:temp){
			if(i>=count){
				break;
			}
			if(StringUtils.isNotBlank(s)){
				url+="/"+s;
				i++;
			}
		}
		return url;
	}
	
	
	public static Date getFormatDate(String dateStr){
		if(StringUtils.isNotBlank(dateStr)){
			SimpleDateFormat allDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return allDateFormat.parse(dateStr);
			} catch (ParseException e) {
			}
		}
		return null;
	}
	
	/**
	 * 是否手机号码
	 */
	public static boolean isPhoneNum(String phone){
		boolean isLegal=false;
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0-1,5-9]))\\d{8}$";
		if(phone.length() == 11){
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phone);
			boolean isMatch = m.matches();
			if(isMatch){
				isLegal=true;
			} 
		}
		return isLegal;
	}

	/*
	 * 获取当前年月上一个月
	 */
	public static String getLastMonth(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        String yearMonth = yearMonthFormat.get().format(cal.getTime());
		return yearMonth;
	}
	
	
	/**
	 * 根据身份证获取年龄（虚岁）
	 * @param args
	 * @throws Exception 
	 * @throws ParseException 
	 */
	
	public static int ageVal(String ctIdentityNumber){
		String dataOfBirth= ctIdentityNumber.substring(6, 14); 
		int year = Integer.valueOf(dataOfBirth.substring(0, 4));
		int age = getAge(year);
		return age;
	}
	
	/**
     * 数字验证
     *
     * @param val
     * @return 提取的数字。
     */
    public static boolean isNum(String val) {
        return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
    }
	
  //由出生日期获得年龄
    public static int getAge(Date birthDay)  {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow  >= dayOfMonthBirth){
              	  age++;
                }
            }
        }else{
      	  age++;
        }
        return age;
    }
    
  /**
   * 当前年的年龄
   * @return
   */
    public static int getAge(int year)  {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int age = yearNow-year;
        return age;
    }
    
    
    /**
	 * 根据身份证获取当前年的年龄（虚岁）
	 * @return
	 */
	public static int ageValueWithYearStartTime(String ctIdentityNumber){
		return ageVal(ctIdentityNumber);
	}

	
    public static int getAgeWithCurTime(Date birthDay)  {
        Calendar cal = Calendar.getInstance();
        Date date =DateUtils.getCurrentYearStartTime();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        cal.setTime(date);
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow  >= dayOfMonthBirth){
              	  age++;
                }
            }
        }else{
      	  age++;
        }
        return age;
    }
	
}
