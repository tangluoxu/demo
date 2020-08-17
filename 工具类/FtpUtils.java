package com.eshore.hb.btsp114busiservice.product.util;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUtils {
	private static Logger logger = LoggerFactory.getLogger(FtpUtils.class);

	//上传文件
	public static void uploadFile(String ip,int port,String username, String password, String savePath,String filename, InputStream inputStream) {
		try{
			FTPClient ftpClient = new FTPClient();
	        ftpClient.setControlEncoding("utf-8");
	        logger.info("connecting...ftp服务器:"+ip+":"+port); 
	        ftpClient.connect(ip, port); //连接ftp服务器
	        ftpClient.login(username, password); //登录ftp服务器
	        int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
	        if(!FTPReply.isPositiveCompletion(replyCode)){
	        	logger.info("connect failed...ftp服务器:"+ip+":"+port); 
	        }else{
	        	logger.info("connect successfu...ftp服务器:"+ip+":"+port); 
	        }
	        ftpClient.setControlEncoding("UTF-8"); // 中文支持
	        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.changeWorkingDirectory(savePath);
	         //上传ftp
	        ftpClient.storeFile(filename, inputStream);
	        inputStream.close();
	        ftpClient.logout();
		} catch (Exception e) {
			  logger.error("在上传文件时，出现异常：{}", e);
	    }
	}
	
	//下载文件
	public static InputStream downLoadFile(String ip,int port,String username, String password, String path,String filename) {
		InputStream inputStream =null;
		try{
			FTPClient ftpClient = new FTPClient();
	        ftpClient.setControlEncoding("utf-8");
	        logger.info("connecting...ftp服务器:"+ip+":"+port); 
	        ftpClient.connect(ip, port); //连接ftp服务器
	        ftpClient.login(username, password); //登录ftp服务器
	        int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
	        if(!FTPReply.isPositiveCompletion(replyCode)){
	        	logger.info("connect failed...ftp服务器:"+ip+":"+port); 
	        }else{
	        	logger.info("connect successfu...ftp服务器:"+ip+":"+port); 
	        }
	        ftpClient.setControlEncoding("UTF-8"); // 中文支持
	        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.changeWorkingDirectory(path);
	         //下载ftp
	        inputStream = ftpClient.retrieveFileStream(filename); 
	        ftpClient.logout();
		} catch (Exception e) {
			  logger.error("在下载文件时，出现异常：{}", e);
	    }
		return inputStream;
	}
}
