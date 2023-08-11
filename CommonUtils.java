

import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
	
	/**
	 * 排除的请求参数
	 * @param request
	 * @param params
	 * @param debarParams
	 */
	public static void setHttpRequestParams(HttpServletRequest request, Map<String, Object> params, String... debarParams){
		//排除的请求参数
		Map<String, String> debarParamsMap=new HashMap<String, String>();
		debarParamsMap.put("_","_");
		if(debarParams!=null&&debarParams.length>0){
			for(String s:debarParams){
				debarParamsMap.put(s, s);
			}
		}
		String propertyVal=null;
		String paramName=null;
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {  
			paramName = paramNames.nextElement();
			if(!debarParamsMap.containsKey(paramName)){
				propertyVal=request.getParameter(paramName);
				if(StringUtils.isNotBlank(propertyVal)){
					params.put(paramName, propertyVal);
				}
			}
		}
	}
	
	/**
	 * 需要的参数方式
	 * @param request
	 * @param params
	 * @param needParams
	 */
	public static void setHttpRequestParamsV2(HttpServletRequest request, Map<String, Object> params, String... needParams){
		if(needParams!=null&&needParams.length>0){
			for(String s:needParams){
				String d  =request.getParameter(s);
				if(StringUtils.isNotBlank(d)&&!"null".equalsIgnoreCase(d)){
					params.put(s, d);
				}
			}
		}
	}
	
	
	public static void setHttpRequestParamsV3(Map<String, String> requestParams,Map<String, Object> params,String... needParams){
		if(needParams!=null&&needParams.length>0){
			for(String s:needParams){
				String val  =requestParams.get(s);
				if(StringUtils.isNotBlank(val)&&!"null".equalsIgnoreCase(val)){
					params.put(s, val);
				}
			}
		}
	}
	
	
	/**?>=startTime and ?<=endTime
	 * 设置开始时间和结束时间
	 * @param params
	 * @param request
	 */
	public static void setStartEndTime(HttpServletRequest request, Map<String,Object> params){
		//1今天、2最近一月、3最近3月、4最近一年
		try {
			String curType=request.getParameter("curType");
			String startTime=request.getParameter("startTime");
			String endTime=request.getParameter("endTime");
			if(StringUtils.isNotBlank(curType)){
				Date nowDate=new Date();
				if("1".equals(curType)){
					String curTimeStr=ComUtils.simpleDateFormat.get().format(nowDate);
					params.put("startTime", ComUtils.allDateFormat.get().parse(curTimeStr+" 00:00:00"));
				}else if("2".equals(curType)){
					params.put("startTime", DateUtils.calDateMonth(nowDate, -1));
				}else if("3".equals(curType)){
					params.put("startTime", DateUtils.calDateMonth(nowDate, -3));
				}else if("4".equals(curType)){
					params.put("startTime", DateUtils.getCurrentYearStartTime());
				}
				params.put("endTime", nowDate);
			}else{
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(StringUtils.isNotBlank(startTime)){
					params.put("startTime", dateFormat.parse(startTime+" 00:00:00"));
				}
				if(StringUtils.isNotBlank(endTime)){
					params.put("endTime", dateFormat.parse(endTime+" 23:59:59"));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置今年开始时间和今年结束时间
	 * @param request
	 * @param params
	 * @param propertyName
	 */
	public static void setStartEndYearTime(HttpServletRequest request, Map<String, Object> params, String propertyName){
		String year = request.getParameter(propertyName);
		if (StringUtils.isNotBlank(year)) {
			try {
				params.put("beginTime", DateUtils.format(year+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
				params.put("endTime", DateUtils.format(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *  ?>=startTime and ?<=endTime
	 */
	public static void setStartEndTime(HttpServletRequest request, Map<String,Object> params, String startPropertyName, String endPropertyName){
		try {
			String startTime=request.getParameter(startPropertyName);
			String endTime=request.getParameter(endPropertyName);
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isNotBlank(startTime)){
				params.put(startPropertyName, dateFormat.parse(startTime+" 00:00:00"));
			}
			if(StringUtils.isNotBlank(endTime)){
				params.put(endPropertyName, dateFormat.parse(endTime+" 23:59:59"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ?>=startTime and ?<endTime
	 */
	public static void setStartEndMonthTime(HttpServletRequest request, Map<String,Object> params, String startPropertyName, String endPropertyName){
		try {
			String startTime=request.getParameter(startPropertyName);
			String endTime=request.getParameter(endPropertyName);
			if(StringUtils.isNotBlank(startTime)){
				params.put(startPropertyName, ComUtils.allDateFormat.get().parse(startTime+"-01 00:00:00"));
			}
			if(StringUtils.isNotBlank(endTime)){
				Date date=ComUtils.allDateFormat.get().parse(endTime+"-01 00:00:00");
				params.put(endPropertyName,ComUtils.calMonth(date, 1) );
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
