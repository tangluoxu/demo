


import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.csource.common.IniFileReader;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

/**
 * FastDFS上传文件工具类
 * 访问URL：http://14.17.102.23:9999/group1/M00/00/00/wKgACFwsk4yALHh6ACMKQgPZpWs031.png
 * 图片存放路径：/data/fastdfs/fastdfs_storage_data/data/00/00
 * @author quanb
 *
 */
public class FastDFSClient {


	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;

	public FastDFSClient(String conf) throws Exception {
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
		}
		ClientGlobal.init(conf);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}

	/*
		linux打成jar包读不到conf配置文件参数时，使用properties文件配置参数，并用此方法上传
	 */
	public FastDFSClient() throws Exception {
		Properties properties = new Properties();
		// 使用ClassLoader加载properties配置文件生成对应的输入流
		InputStream in = new ClassPathResource("fdfs_client.properties").getInputStream();
		//InputStream in = FastDFSClient.class.getResourceAsStream("fdfs_client.properties");
		// 使用properties对象加载输入流
		properties.load(in);

		ClientGlobal.initByProperties(properties);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}
	/**
     * 上传文件方法
     * @param fileName	文件全路径
     * @param extName	文件扩展名，不包含（.）
     * @param metas		文件扩展信息
     * @return
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileName, extName, metas);
        return result;
    }
    /**
     * 上传文件方法
     * @param fileName	文件全路径
     * @return
     */
    public String uploadFile(String fileName) throws Exception {
        return uploadFile(fileName, null, null);
    }

    /**
     * 上传文件方法
     * @param fileName	文件全路径
     * @param extName	文件扩展名，不包含（.）
     * @return
     */
    public String uploadFile(String fileName, String extName) throws Exception {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * @param fileContent	文件的内容，字节数组
     * @param extName		文件扩展名
     * @param metas			文件扩展信息
     * @return
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {
        String result = storageClient.upload_file1(fileContent, extName, metas);
        return result;
    }

    /**
     * 上传文件方法
     * @param fileContent	文件的内容，字节数组
     * @return
     */
    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    /**
     * 上传文件方法
     * @param fileContent	文件的内容，字节数组
     * @param extName		文件扩展名
     * @return
     */
	public String uploadFile(byte[] fileContent, String extName) throws Exception {
	   return uploadFile(fileContent, extName, null);
	}

	/**
	 * 下载文件方法
	 * @param fileId  文件id
	 * @return
	 */
	public byte[] downloadFile(String fileId) throws Exception {
		 byte[] bytes = storageClient.download_file1(fileId);
		 return bytes;
	}

	/**
	 * 下载文件方法
	 * @param fileId  文件id
	 * @param path    保存文件路径（不包含文件名，文件名称默认）
	 * @return
	 */
	public void downloadFile(String fileId, String path) throws Exception {
		downloadFile(fileId, path, null);
	}

	/**
	 * 下载文件方法
	 * @param fileId  文件id
	 * @param path    保存文件路径（不包含文件名）
	 * @param filename 文件名称
	 * @return
	 */
	public void downloadFile(String fileId, String path, String filename) throws Exception {
		 byte[] bytes = downloadFile(fileId);
		 if (bytes.length < 3 || bytes.equals(null)) return;//判断输入的byte是否为空
		 FileOutputStream fos = null;
		 try {
		     File file = new File(path);
		     if (!file.exists() && !file.isDirectory()) {
	    		 file.mkdir();//生成目录
		     }
		     if(filename==null || "".equals(filename.trim())) {
		    	 filename = fileId.substring(fileId.lastIndexOf("/")+1, fileId.length());
		     }
		     File fileall = new File(path+File.separator+filename);
		     if(fileall.exists()) {
		    	 if(filename.contains(".")) {
		    		 String name = filename.substring(0, filename.lastIndexOf("."));
			    	 String ext = filename.substring(filename.lastIndexOf("."), filename.length());
			    	 filename = name+"_"+System.currentTimeMillis()+ext;
		    	 } else {
			    	 String ext = fileId.substring(fileId.lastIndexOf("."), fileId.length());
			    	 filename = filename+"_"+System.currentTimeMillis()+ext;
		    	 }
		     }
		     fos = new FileOutputStream(path+File.separator+filename);
			 IOUtils.write(bytes, fos);
		 } catch (Exception e) {
			 e.printStackTrace();
		 } finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 获取图片访问地址根目录
	 * fdfs_client.conf
	 * @return
	 */
	public static String getHttpBasePath() {
		IniFileReader iniReader;
		String value = "";
		try {
			iniReader = new IniFileReader("fdfs_client.conf");
			value = iniReader.getStrValue("http.nginx_http_path");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return value;
	}

	public static void main(String[] args) {

		try {
//			ClassPathResource cpr = new ClassPathResource("fdfs_client.conf");
//			ClientGlobal.init(cpr.getClassLoader().getResource("fdfs_client.conf").getPath());
//			//FastDFSClient client = new FastDFSClient("src\\main\\resources\\fdfs_client.conf");
//			FastDFSClient client = new FastDFSClient(cpr.getClassLoader().getResource("fdfs_client.conf").getPath());

			FastDFSClient client = new FastDFSClient();
			String uploadFile = client.uploadFile("D:/动漫图片/74c4864bc5eb9d46.jpg", "jpg");

//			for(int i=0; i<20; i++) {
//				client.downloadFile("group1/M00/00/08/wKgAHF3XOMSAPphRAAbzcmHLynU38.xlsx", "D:/image/test1", "aa.xlsx");
//			}

			System.out.println(uploadFile);//返回的是文件的
			System.out.println("图片根目录地址："+FastDFSClient.getHttpBasePath() + uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
