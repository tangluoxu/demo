package com.eshore.hb.btsp114busiservice.product.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: RelecfClassUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2012-12-25 上午9:58:48
 *
 */
public class ReflectClassUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ReflectClassUtils.class);
	/**
	 * 
	* @Title: getAnnotationVvalue
	* @Description: 
	* @param @param portal  不能带‘/’
	* @param @param classStr  like  'cn.nd.superunner.admin.controller.ActivityBasicnumController'
	* @param @return
	* @param @throws ClassNotFoundException    设定文件
	* @return Map<String,String>    返回类型
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getAnnoRequestMappingValue(String portal,String classStr) throws ClassNotFoundException{
		Class clazz = null;
		try{
			clazz = Class.forName(classStr);
		}catch (Exception e) {
			logger.error("class地址错误："+classStr);
			return null;
		}
		String clazzHeader = getAnnoRequestMappingValue(clazz.getAnnotations());
		if(clazzHeader==null){
			return null;
		}else{
			String headValue = "";
			if(StringUtils.isNotBlank(portal)){
				headValue+=portal;
			}
			if(!clazzHeader.startsWith("/")){
				headValue+="/";
			}
			headValue+=clazzHeader;
			List<String> list = new ArrayList<String>();
			for(Method method:clazz.getMethods()){
				String methodUrl = "";
				String methodDirections = "";
				String permissions="";
				for(Annotation a:method.getAnnotations()){
					 String astr = a.toString();
					 if(astr!=null && astr.startsWith("@org.springframework.web.bind.annotation.RequestMapping")){
						// String regex = ".*value=(\\[(.*)\\], produces=\\[(.*)\\], method=\\[(.*)\\], params).*";
						 String regex = ".*value=(\\[(.*)\\],).*";
						 Pattern pattern=Pattern.compile(regex);
						 Matcher matcher=pattern.matcher(astr); 
						 while(matcher.find()){ 
							 String tempUrl = matcher.group(2);
							 if(tempUrl!=null){
								 String[] strs1 = tempUrl.split("]");
								 tempUrl = strs1[0];
								 if(!tempUrl.startsWith("/")){
									 if("".equals(tempUrl)){
										 tempUrl = headValue;
									 }else{
										 tempUrl = headValue+"/"+tempUrl;
									 }
								}else{
									tempUrl = headValue+tempUrl;
								}
							 }
							 
							 String methodregex = ".*method=\\[(.*)\\],.*";
							 Pattern pattern2=Pattern.compile(methodregex);
							 Matcher matcher2=pattern2.matcher(astr); 
							 String postType = "";
							 while(matcher2.find()){ 
								 postType = matcher2.group(1);
							 }
							 if(postType==null ||"".equals(postType)){
								 postType="GET";
							 }
							 String[] strs2 = postType.split("]");
							 postType = strs2[0];
							 if(postType==null ||"".equals(postType)){
								 postType="GET";
							 }
							 methodUrl=tempUrl+"σ"+postType;
						 }
					 }
					 if(astr!=null && astr.startsWith("@cn.nd.superunner.common.utils.MethodDirections")){
						 String regex = ".*value=(.*)\\)";
						 Pattern pattern=Pattern.compile(regex);
						 Matcher matcher=pattern.matcher(astr); 
						 while(matcher.find()){ 
							 methodDirections = matcher.group(1);
						 }
					 }
					 if(astr!=null && astr.startsWith("@org.apache.shiro.authz.annotation.RequiresPermissions")){
						 String regex = ".*value=(\\[(.*)\\])";
						 Pattern pattern=Pattern.compile(regex);
						 Matcher matcher=pattern.matcher(astr); 
						 while(matcher.find()){ 
							 permissions = matcher.group(1);
							 if(StringUtils.isNotBlank(permissions)){
								 permissions=permissions.substring(1, permissions.length()-1);
							 }
						 }
					 }
				}
				if(methodUrl!=null && !"".equals(methodUrl)){
					list.add(methodUrl+"σ"+methodDirections+"σ"+permissions);
				}
			}
			return list;
		}
	}
	
	public static String getAnnoRequestMappingValue(Annotation[] annotations){
		 for(Annotation a:annotations){
			 String astr = a.toString();
			 if(astr!=null && astr.startsWith("@org.springframework.web.bind.annotation.RequestMapping")){
				// String regex = ".*value=(\\[(.*)\\], produces=\\[(.*)\\], method).*";
				 //String regex = ".*value=(\\[(.*)\\], produces=\\[(.*)\\], method=\\[(.*)\\], params).*";
				 String regex = ".*value=(\\[(.*)\\],).*";
				 Pattern pattern=Pattern.compile(regex);
				 Matcher matcher=pattern.matcher(astr); 
				 while(matcher.find()){ 
					 String str = matcher.group(2); 
					 if(StringUtils.isNotBlank(str)){
						 String[] strs1 = str.split("]");
						 str = strs1[0];
					 }
					 return str;
				 }
			 }
		}
		return null;
	}
	
	public static String getAnnoRequestMappingMethod(Annotation[] annotations){
		 for(Annotation a:annotations){
			 String astr = a.toString();
			 if(astr!=null && astr.startsWith("@org.springframework.web.bind.annotation.RequestMapping")){
				 String regex = ".*method=(\\[(.*)\\],).*";
				 Pattern pattern=Pattern.compile(regex);
				 Matcher matcher=pattern.matcher(astr); 
				 while(matcher.find()){ 
					 String str = matcher.group(2); 
					 if(StringUtils.isNotBlank(str)){
						 String[] strs1 = str.split("]");
						 str = strs1[0];
					 }
					 return str;
				 }
			 }
		}
		return null;
	}
	
	public static String getMethodDirections(Annotation[] annotations){
		 for(Annotation a:annotations){
			 String astr = a.toString();
			 if(astr!=null && astr.startsWith("@cn.eshore.welfare.common.util.MethodDirections")){
				 String regex = ".*value=(.*)\\).*";
				 Pattern pattern=Pattern.compile(regex);
				 Matcher matcher=pattern.matcher(astr); 
				 while(matcher.find()){ 
					 return matcher.group(1); 
				 }
			 }
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static String getAnnoRequestMappingHeaderValue(String portal,String classStr) throws ClassNotFoundException{
		Class clazz = null;
		try{
			clazz = Class.forName(classStr);
		}catch (Exception e) {
			logger.error("class地址错误："+classStr);
			return null;
		}
		String headValue = "";
		if(StringUtils.isNotBlank(portal)){
			headValue+=portal;
		}
		String clazzHeader = getAnnoRequestMappingValue(clazz.getAnnotations());
		if(clazzHeader==null){
			return null;
		}else{
			if(!clazzHeader.startsWith("/")){
				headValue+="/";
			}
			headValue+=clazzHeader;
		}
		return headValue;
	}
	
	/** 
     * 从包package中获取所有的Class 
     *  
     * @param pack 
     * @return 
     */  
    public static Set<Class<?>> getClasses(String pack) {  
  
        // 第一个class类的集合  
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();  
        // 是否循环迭代  
        boolean recursive = true;  
        // 获取包的名字 并进行替换  
        String packageName = pack;  
        String packageDirName = packageName.replace('.', '/');  
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things  
        Enumeration<URL> dirs;  
        try {  
            dirs = Thread.currentThread().getContextClassLoader().getResources(  
                    packageDirName);  
            // 循环迭代下去  
            while (dirs.hasMoreElements()) {  
                // 获取下一个元素  
                URL url = dirs.nextElement();  
                // 得到协议的名称  
                String protocol = url.getProtocol();  
                // 如果是以文件的形式保存在服务器上  
                if ("file".equals(protocol)) {  
                    logger.debug(" file类型的扫描");  
                    // 获取包的物理路径  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    // 以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(packageName, filePath,  
                            recursive, classes);  
                } else if ("jar".equals(protocol)) {  
                    // 如果是jar包文件  
                    // 定义一个JarFile  
                    //System.err.println("jar类型的扫描");  
                    JarFile jar;  
                    try {  
                        // 获取jar  
                        jar = ((JarURLConnection) url.openConnection())  
                                .getJarFile();  
                        // 从此jar包 得到一个枚举类  
                        Enumeration<JarEntry> entries = jar.entries();  
                        // 同样的进行循环迭代  
                        while (entries.hasMoreElements()) {  
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
                            JarEntry entry = entries.nextElement();  
                            String name = entry.getName();  
                            // 如果是以/开头的  
                            if (name.charAt(0) == '/') {  
                                // 获取后面的字符串  
                                name = name.substring(1);  
                            }  
                            // 如果前半部分和定义的包名相同  
                            if (name.startsWith(packageDirName)) {  
                                int idx = name.lastIndexOf('/');  
                                // 如果以"/"结尾 是一个包  
                                if (idx != -1) {  
                                    // 获取包名 把"/"替换成"."  
                                    packageName = name.substring(0, idx)  
                                            .replace('/', '.');  
                                }  
                                // 如果可以迭代下去 并且是一个包  
                                if ((idx != -1) || recursive) {  
                                    // 如果是一个.class文件 而且不是目录  
                                    if (name.endsWith(".class")  
                                            && !entry.isDirectory()) {  
                                        // 去掉后面的".class" 获取真正的类名  
                                        String className = name.substring(  
                                                packageName.length() + 1, name  
                                                        .length() - 6);  
                                        try {  
                                            // 添加到classes  
                                            classes.add(Class  
                                                    .forName(packageName + '.'  
                                                            + className));  
                                        } catch (ClassNotFoundException e) {  
                                            // log  
                                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");  
                                            e.printStackTrace();  
                                        }  
                                    }  
                                }  
                            }  
                        }  
                    } catch (IOException e) {  
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");  
                        e.printStackTrace();  
                    }  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return classes;  
    }
    
    /** 
     * 以文件的形式来获取包下的所有Class 
     *  
     * @param packageName 
     * @param packagePath 
     * @param recursive 
     * @param classes 
     */  
    public static void findAndAddClassesInPackageByFile(String packageName,  
            String packagePath, final boolean recursive, Set<Class<?>> classes) {  
        // 获取此包的目录 建立一个File  
        File dir = new File(packagePath);  
        // 如果不存在或者 也不是目录就直接返回  
        if (!dir.exists() || !dir.isDirectory()) {  
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");  
            return;  
        }  
        // 如果存在 就获取包下的所有文件 包括目录  
        File[] dirfiles = dir.listFiles(new FileFilter() {  
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File file) {  
                return (recursive && file.isDirectory())  
                        || (file.getName().endsWith(".class"));  
            }  
        });  
        // 循环所有文件  
        for (File file : dirfiles) {  
            // 如果是目录 则继续扫描  
            if (file.isDirectory()) {  
                findAndAddClassesInPackageByFile(packageName + "."  
                        + file.getName(), file.getAbsolutePath(), recursive,  
                        classes);  
            } else {  
                // 如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0,  
                        file.getName().length() - 6);  
                try {  
                    // 添加到集合中去  
                    //classes.add(Class.forName(packageName + '.' + className));  
                                         //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净  
                                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));    
                                } catch (ClassNotFoundException e) {  
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");  
                    e.printStackTrace();  
                }  
            }  
        }  
    } 
    
    @SuppressWarnings({ "rawtypes" })
	public static String getMethodDesc() {
    	try {
    		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (int i = 0; i < stack.length; i++) {
                StackTraceElement ste = stack[i];
                String className = ste.getClassName();
                String methodName = ste.getMethodName();
                if(ste.getClassName().endsWith("Controller")){
					Class clazz = Class.forName(className);
					Method[] methods = clazz.getDeclaredMethods();
					for(Method method:methods){
						String methodNameV = method.getName();
						if(methodNameV.equals(methodName)){
							Annotation[] an = method.getAnnotations();
							String methodDesc = ReflectClassUtils .getMethodDirections(an);
							return methodDesc;
						}
					}
                }
            }
    	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }


    
    /**
     * 调用Getter方法.
     */
    public static Object invokeGetterMethod(Object target, String propertyName) {
    	String getterMethodName = null;
    	//先判断单位扩展表跟员工扩展表中。。可用福利跟可用点赞中的字段问题
    	if(propertyName.substring(0, 2).equals("wV") || propertyName.substring(0, 2).equals("wP")){
    		getterMethodName = "get" +propertyName;
    	}else{
    		getterMethodName = "get" + StringUtils.capitalize(propertyName);
    	}
		Object obj = invokeMethod(target, getterMethodName, new Class[] {},
				new Object[] {});
        return obj;
    }
    
    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     */
    public static Object invokeMethod(final Object object,
            final String methodName, final Class<?>[] parameterTypes,
            final Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method ["
                    + methodName + "] parameterType " + parameterTypes
                    + " on target [" + object + "]");
        }

        method.setAccessible(true);

        try {
            return method.invoke(object, parameters);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
        
    }
    
    /**
     * 循环向上转型, 获取对象的DeclaredMethod.
     * 
     * 如向上转型到Object仍无法找到, 返回null.
     */
    protected static Method getDeclaredMethod(Object object, String methodName,
            Class<?>[] parameterTypes) {
        Assert.notNull(object, "object不能为空");

        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {// NOSONAR
                                                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }
    
    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(
            Exception e) {
        return convertReflectionExceptionToUnchecked(null, e);
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(
            String desc, Exception e) {
        desc = (desc == null) ? "Unexpected Checked Exception." : desc;
        if (e instanceof IllegalAccessException
                || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(desc, e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(desc,
                    ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(desc, e);
    }
    
    /**
     * 获取属性的类型
     */
    public static Class<?> getFieldTypeClass(Object obj,String fieldName){
		try {
			Class<?> clazz=obj.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			if(field!=null){
				return field.getType();
	    	}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 调用Setter方法.
     */
    public static Object invokeSetterMethod(Object target, String propertyName,Class<?>[] parameterTypes,Object[] parameters) {
    	String setterMethodName = "set" + StringUtils.capitalize(propertyName);
    	Object obj = invokeMethod(target, setterMethodName, parameterTypes,parameters);
    	return obj;
    }
    
    
}
