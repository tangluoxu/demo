/**
 * 
 */
package com.eshore.hb.btsp114busiservice.product.util;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算， 这个工具类提供精确的浮点数运算，包括加减乘除和四舍五入。
 * 
 */
public final class MathUtil {
	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 2;
	//默认保留2位小数
//	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = new ThreadLocal<DecimalFormat>(){
		@Override      
	       protected DecimalFormat initialValue() {
	        return new DecimalFormat("#.##");     
	      } 
	};

	// 这个类不能实例化
	private MathUtil() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */
	public static String add(String v1, String v2) {
		if (StringUtils.isBlank(v1)) {
			v1 = "0";
		}
		if (StringUtils.isBlank(v2)) {
			v2 = "0";
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).toString();
	}
    
	/**
	 * 提供精确的加法运算，批量多个相加
	 * @param strings 加数
	 * @return 多个参数的和
	 */
	public static String addMore(String...strings){
		BigDecimal bigDecimal = new BigDecimal("0");
		for (String str : strings){
			if(StringUtils.isNotBlank(str)) {
				BigDecimal b = new BigDecimal(str);
				bigDecimal = bigDecimal.add(b);
			}
		}
		return DECIMAL_FORMAT.get().format(bigDecimal);
	}
	
	public static double add(BigDecimal...ary){
		BigDecimal bigDecimal = new BigDecimal("0");
		for (BigDecimal temp : ary){
			bigDecimal = bigDecimal.add(temp);
		}
		return bigDecimal.doubleValue();
	}
	
	/**
	 * 提供精确的加法运算，批量多个相加
	 * @param strings 加数
	 * @return 多个参数的和
	 */
	public static double add(String...strings){
		BigDecimal bigDecimal = new BigDecimal("0");
		for (String str : strings){
			if(StringUtils.isNotBlank(str)) {
				BigDecimal b = new BigDecimal(str);
				bigDecimal = bigDecimal.add(b);
			}
		}
		return bigDecimal.doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差:v1-v2
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	
	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 * 
	 * @param v1 被除数
	 * @param v2 除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double div(BigDecimal b1, Long v2, int scale) {
		if(v2==null||v2.compareTo(0L)==0){
			return 0;
		}
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static double round(BigDecimal bigDecimal, int scale) {
		if(bigDecimal==null){
			return 0D;
		}
		BigDecimal one = new BigDecimal("1");
		return bigDecimal.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 返回大于0，则第一个数大 返回小于0，则第二数大 等于0，一样大
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static int compare(Double val1, Double val2) {
		BigDecimal val11 = new BigDecimal(val1);
		BigDecimal val12 = new BigDecimal(val2);
		return val11.compareTo(val12);
	}

	/**
	 * 获取d1除以d2后，得到其商+余数是否大于0【是则加1，否则加0】
	 * 如：3.5/3=1.166======>返回2
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getSurplusInt(double d1 ,double d2){
		double d=div(d1, d2, 8);//小数位设置为8，主要尽量保证其不可四舍五入到个数
		return (int)Math.ceil(d);//向上取整
	}
	
	public static int getMulInt(double d1 ,double d2){
		double d=mul(d1, d2);
		String[] ss=String.valueOf(d).split("\\.");
		return Integer.valueOf(ss[0])+(Integer.valueOf(ss[1]).intValue()>0?1:0);
	}
	
	
	
	/**
	 * @param maxNum
	 * @param limit
	 * @param lastIndexStr
	 * @return
	 */
	public static int[] createRandomIndex(int maxNum, int limit) {
		int num = 0;// 产生0至maxNum-1的随机数字
		int[] random = new int[limit];
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			for (int i = 0; i < limit; i++) {
				for (int k = 0, count = 5; k < count; k++) {
					num = (int) (secureRandom.nextDouble() * maxNum);// 产生0至maxNum-1的随机数字
					int j = 0;
					for (; j < i; j++) {
						if (random[j] == num)
							break;
					}
					if (i == j) {// 不存在重复
						random[i] = num;
						break;
					} else {
						if (k == 4) {
							random[i] = (int) (secureRandom.nextDouble() * maxNum);
						}
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
		}
		Arrays.sort(random);
		return random;

	}

	/**
	 * 判断是否是数字 描述：方法说明
	 * 
	 * @author 作者 hwb
	 * @version 版本号 2014-8-25
	 * @param str
	 * @return
	 * @return boolean 返回值：注释出失败、错误、异常时的返回情况
	 * @exception 异常：注释出什么条件下会引发什么样的异常
	 * @see 参考的JavaDoc
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}


	/**
	 * 判断是否为浮点数，包括double和float
	 * @param str 传入的字符串
	 * @return 是浮点数返回true，否则返回false
	 */
	public static boolean isDouble(String str){
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}


	/**
	 * 保留小数点后几位
	 * @param val 需要保留的数据
	 * @param scale 保留的后几位
	 * @return 返回的数值
	 */
	public static String reserveDecimal(String val, int scale){
		if(StringUtils.isNotBlank(val)) {
			BigDecimal bd = new BigDecimal(val);
			bd = bd.setScale(scale, RoundingMode.HALF_UP);
			return bd.toString();
		}else{
			return null;
		}
	}

	/**
	 * 返回大于0，则第一个数大 返回小于0，则第二数大 等于0，一样大
	 *
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static int compare(String val1, String val2) {
		BigDecimal val11 = new BigDecimal(val1);
		BigDecimal val12 = new BigDecimal(val2);
		return val11.compareTo(val12);
	}

	/**
	 * 单独清理数字的尾部的,因为jdk7中bigDecimal不能去掉0.00000的尾部
	 * @param bigDecimal
	 * @return
	 */
	public static String clearTailZero(BigDecimal bigDecimal) {
		BigDecimal zero = BigDecimal.ZERO;
		if (bigDecimal.compareTo(zero) == 0) {
			return "0";
		} else {
			return bigDecimal.stripTrailingZeros().toPlainString();
		}
	}
	
	public static String clearTailZero(String bigDecimalStr) {
		BigDecimal bigDecimal=new BigDecimal(bigDecimalStr);
		BigDecimal zero = BigDecimal.ZERO;
		if (bigDecimal.compareTo(zero) == 0) {
			return "0";
		} else {
			return bigDecimal.stripTrailingZeros().toPlainString();
		}
	}
	
	/**
	 * 比较b1和b2的绝对差是否在scale内
	 * @param b1
	 * @param b2
	 * @param scale
	 * @return
	 */
	public static boolean isEqualABS(BigDecimal b1, BigDecimal b2, String scale){
		BigDecimal rc = b1.subtract(b2).abs();
	    BigDecimal compare = new BigDecimal(scale);
	    if (rc.compareTo(compare) > 0) {
	    	return false;
	    }
	    return true;
	}
	
	/**
	 * 保留小数点后几位
	 * @param val 需要保留的数据
	 * @param scale 保留的后几位
	 * @return 返回的数值
	 */
	public static String reserveDecimal(Double val, int scale){
		if(val != null) {
			BigDecimal bd = new BigDecimal(val);
			bd = bd.setScale(scale, RoundingMode.HALF_UP);
			return bd.toString();
		}else{
			return null;
		}
	}
	
	/**
	 * 1.假如被除数为0，判断defaultVal是否非空，是则返回defaultVal，否则返回0
	 * 2.假如被除数非0，判断相除结果是否为0并且defaultVal非空，是则返回defaultVal，否则返回相除所得的avg值
	 */
	public static Object divDefault(double v1, BigDecimal b2, int scale,Object defaultVal) {
		if(b2==null||b2.compareTo(new BigDecimal(0))==0){
			if(defaultVal!=null){
				return defaultVal;
			}
			return 0;
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal avg = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
		if(defaultVal!=null&&avg.compareTo(new BigDecimal(0))==0){
			return defaultVal;
		}
		return avg;
	}
	
	public static Object divDefault(Long v1, Long v2, int scale,Object defaultVal) {
		if(v2==null||v2.compareTo(0L)==0){
			if(defaultVal!=null){
				return defaultVal;
			}
			return 0;
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		BigDecimal avg = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
		if(defaultVal!=null&&avg.compareTo(new BigDecimal(0))==0){
			return defaultVal;
		}
		return avg;
	}
	
	/**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @param roundingMode 模式 BigDecimal.ROUND_HALF_UP
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divV2(double v1, double v2, int scale,int roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, roundingMode);
    }
}
