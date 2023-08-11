

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /*
     * 以文件的MD5作为文件名，文件假如存在则直接返回文件名，否则才保存文件
     */
    public static String doTransmitResources(CommonsMultipartFile multipartFile, String fileFolder) {
    	InputStream is=null;
        try {
        	is=multipartFile.getInputStream();
            String fileMd5 = DigestUtils.md5Hex(is);
            String fileName = fileMd5 + getFileExtension(multipartFile.getOriginalFilename());
            File screenshotFile = new File(fileFolder + fileName);
            if (!screenshotFile.exists()) {
                File f_FilePath = new File(fileFolder);
                if (!f_FilePath.exists()) {
                    f_FilePath.mkdirs();
                }
                multipartFile.transferTo(screenshotFile);
                screenshotFile.setWritable(true);
                screenshotFile.setReadable(true);
            }
            return fileName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
			IOUtils.closeQuietly(is);
		}
        return null;
    }


    public static String doTransmitResources(MultipartFile multipartFile, String fileFolder) {
    	InputStream is=null;
        try {
        	is=multipartFile.getInputStream();
            String fileMd5 = DigestUtils.md5Hex(is);
            String fileName = fileMd5 + getFileExtension(multipartFile.getOriginalFilename());
            File screenshotFile = new File(fileFolder + fileName);
            if (!screenshotFile.exists()) {
                File f_FilePath = new File(fileFolder);
                if (!f_FilePath.exists()) {
                    f_FilePath.mkdirs();
                }
                multipartFile.transferTo(screenshotFile);
            }
            return fileName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	IOUtils.closeQuietly(is);
        }
        return null;
    }
    
    public static String doTransmitResourcesWithType(String typeValue,MultipartFile multipartFile, String fileFolder) {
    	OutputStream outputStream = null;
    	FileInputStream inputStream = null;
    	Workbook wb = null;
    	try {
            String fileMd5 = DigestUtils.md5Hex(multipartFile.getInputStream());
            fileFolder += typeValue+ ComUtils.SEPARATOR + fileMd5;
            String fileName = multipartFile.getOriginalFilename();
            File screenshotFile = new File(fileFolder+ComUtils.SEPARATOR  + fileName);
            boolean isExcel = false;
            //如果是excel文件，则进行列宽度自适处理
            if(".xlsx".equals(getFileExtension(fileName)) || ".xls".equals(getFileExtension(fileName))){
            	isExcel= true;
            }
            if (!screenshotFile.exists()) {
                File f_FilePath = new File(fileFolder);
                if (!f_FilePath.exists()) {
                    f_FilePath.mkdirs();
                }
                multipartFile.transferTo(screenshotFile);
                if(isExcel){
	                 inputStream = new FileInputStream(screenshotFile);
	                wb = WorkbookFactory.create(inputStream);
	    			//添加列自适应宽度
	    			Sheet sheet = wb.getSheetAt(0);
	    			if(sheet!=null){
	    				int colNum = sheet.getRow(0).getPhysicalNumberOfCells();
	    				setSizeColumn(sheet,colNum);
	    			}
	    			
	    			outputStream = new FileOutputStream(screenshotFile);
	    			wb.write(outputStream);
	    			//关闭流
	    		  
                }
            }
            String fileUrl = typeValue+ ComUtils.SEPARATOR +fileMd5+ComUtils.SEPARATOR+fileName;
            return fileUrl;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(outputStream!=null) {
        		try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(inputStream!=null) {
        		try {
        			inputStream.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
//        	IOUtils.closeQuietly(outputStream);
//        	IOUtils.closeQuietly(inputStream);
        	//IOUtils.closeQuietly(wb);
        	if(wb!=null) {
        		try {
        			wb.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }
        return null;
    }
    
    public static void setSizeColumn(Sheet sheet, int colNum) {
		for (int columnNum = 0; columnNum < colNum; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            int num = sheet.getPhysicalNumberOfRows();
            for (int rowNum = 0; rowNum < num; rowNum++) {
                Row currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING){
                        int length = (currentCell.getStringCellValue().getBytes().length+currentCell.getStringCellValue().length())/2;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
                
            }
            sheet.setColumnWidth(columnNum, (columnWidth+1) * 256+200);
        }

		
	}

    /*
     * 保存上传文件
     */
    public static String doTransmitResources(CommonsMultipartFile multipartFile, String fileFolder, String fileName) {
        if (fileName == null || "".equals(fileName)) {
            fileName = multipartFile.getOriginalFilename();
        } else {
            fileName = fileName + getFileExtension(multipartFile.getOriginalFilename());
        }
        File f_FilePath = new File(fileFolder);
        if (!f_FilePath.exists()) {
            f_FilePath.mkdirs();
        }
        File screenshotFile = new File(fileFolder + fileName);
        try {
            multipartFile.transferTo(screenshotFile);
            return fileName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * 获取文件的后缀名
     */
    public static String getFileExtension(String url) {
        if (url == null)
            return null;
        int indx = url.lastIndexOf('.');
        if (indx < 0)
            return null;
        return url.substring(indx).toLowerCase(Locale.ENGLISH);
    }
    /**
     * 获取文件名
     * @param url
     * @return
     */
    public static String getFileName(String url) {
    	if (url == null)
    		return null;
    	int indx = url.lastIndexOf('.');
    	if (indx < 0)
    		return url;
    	return url.substring(0,indx);
    }

    
    /*
     * 为文件添加年、月的子目录
     */
    public static String addYearMonthFolder(String folderName) {
        Date nowDate = new Date();
        int[] yearMonth = ComUtils.getYearMonthDay(nowDate, new int[]{Calendar.YEAR, Calendar.MONTH});
        if (folderName.lastIndexOf("/") < (folderName.length() - 1)) {
            folderName += File.separator;
        }
        folderName += yearMonth[0] + File.separator + yearMonth[1] + File.separator;
        return folderName;
    }

    /*
     * 获取图片的长宽
     */
    public static int[] getImgFileWidthHeight(InputStream inputStream) throws IOException {
        BufferedImage src = javax.imageio.ImageIO.read(inputStream);
        if (src != null) {
            return new int[]{src.getWidth(null), src.getHeight(null)}; // 得到源图高
        }
        return new int[]{0, 0};//非图片资源
    }


    /*
     * 读取文件到字符串
     */
    public static String readFromFile(File src) {
        BufferedReader bufferedReader = null;
        FileReader fileReader=null;
        try {
        	fileReader=new FileReader(src);
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	if(bufferedReader!=null) {
        		try {
        			bufferedReader.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
//            IOUtils.closeQuietly(bufferedReader);
            //IOUtils.closeQuietly(fileReader);
        	if(fileReader!=null) {
        		try {
        			fileReader.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }

    }


    /**
     * 写文件
     *
     * @param fileName      完整文件名(类似：/usr/a/b/c/d.txt)
     * @param contentBytes  文件内容的字节数组
     * @param autoCreateDir 目录不存在时，是否自动创建(多级)目录
     * @param autoOverWrite 目标文件存在时，是否自动覆盖
     * @return
     * @throws IOException
     */
    public static boolean write(String fileName, byte[] contentBytes,
                                boolean autoCreateDir, boolean autoOverwrite) throws IOException {
        boolean result = false;
        FileOutputStream fs=null;
        try {
			if (autoCreateDir) {
			    createDirs(fileName);
			}
			if (autoOverwrite) {
			    delete(fileName);
			}
			File f = new File(fileName);
			fs = new FileOutputStream(f);
			fs.write(contentBytes);
			fs.flush();
			fs.close();
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fs!=null) {
        		try {
        			fs.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
//			IOUtils.closeQuietly(fs);
		}
        return result;
    }
    
    /**
     * 写文件
     *
     * @param fileName      完整文件名(类似：/usr/a/b/c/d.txt)
     * @param contentBytes  文件内容的字节数组
     * @param autoCreateDir 目录不存在时，是否自动创建(多级)目录
     * @param autoOverWrite 目标文件存在时，是否自动覆盖
     * @return
     * @throws IOException
     */
    public static File writeFile(String fileName, byte[] contentBytes,
                                boolean autoCreateDir, boolean autoOverwrite) throws IOException {
    	FileOutputStream fs = null;
    	File f=null;
		try {
			if (autoCreateDir) {
			    createDirs(fileName);
			}
			if (autoOverwrite) {
			    delete(fileName);
			}
			f = new File(fileName);
			fs = new FileOutputStream(f);
			fs.write(contentBytes);
			fs.flush();
			fs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fs!=null) {
        		try {
        			fs.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
//			IOUtils.closeQuietly(fs);
		}
        return f;
    }

    /**
     * 删除文件
     *
     * @param fileName 待删除的完整文件名
     * @return
     */
    public static boolean delete(String fileName) {
        boolean result = false;
        File f = new File(fileName);
        if (f.exists()) {
            result = f.delete();

        } else {
            result = true;
        }
        return result;
    }

    /**
     * 创建(多级)目录
     *
     * @param filePath 完整的文件名(类似：/usr/a/b/c/d.xml)
     */
    public static void createDirs(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

    }
	
	/**
	 * 检测文件类型
	 * @param originalFilename
	 * @param allowFileTypes
	 * @return
	 */
	public static boolean checkFileType(String originalFilename,String[] allowFileTypes){
		String fileExtension=getFileExtension(originalFilename);
		for(String s:allowFileTypes){
			if(s.equalsIgnoreCase(fileExtension)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检测文件大小
	 * @param fileSize
	 * @param allowMaxSize
	 * @return
	 */
	public static boolean checkFileSize(long fileSize,long allowMaxSize){
		if(fileSize>0&&fileSize<=allowMaxSize){
			return true;
		}
		return false;
	}
	
	/**
	 * 下载绝对路径文件
	 */
	public static boolean downloadAbsolutePathFile(String filePath,String newFileName,HttpServletRequest request, HttpServletResponse response ){
		return downloadFile(1, filePath, newFileName, request, response);
	}
	
	/**
	 * 下载相对路径文件
	 */
	public static boolean downloadRelativePathFile(String filePath,String newFileName,HttpServletRequest request, HttpServletResponse response ){
		return downloadFile(0, filePath, newFileName, request, response);
	}
	
	/**
	 * 下载文件
	 * @param filePathName 要下载的文件路径
	 * @param newFileName 新文件名
	 * @param filePathType 文件路径类型：0相对路径、1绝对路径
	 */
	private static boolean downloadFile(Integer filePathType,String filePath,String newFileName,HttpServletRequest request, HttpServletResponse response ) {
		boolean isSuc=false;
		OutputStream toClient = null;
		InputStream fis = null;
		FileInputStream fileInputStream = null;
		//打开文件输入流 和 servlet输出流
		try {
			if (StringUtils.isNotBlank(filePath)) {
				if(filePathType==null||filePathType==0){
					filePath = request.getSession().getServletContext().getRealPath(filePath);
				}
				// 需要下载的文件
				File myfile = new File(filePath);
				if(!myfile.exists()){
					logger.error("文件：{}不存在"+filePath);
					return isSuc;
				}
				if(StringUtils.isBlank(newFileName)){
					newFileName=myfile.getName();
				}else if(newFileName.lastIndexOf(".")<1){
					newFileName+=FileUtils.getFileExtension(myfile.getName());//补上文件后缀名
				}
				// 清空response
				response.reset();
				// 设置response的Header
				response.addHeader("Content-Disposition", "attachment;filename=" +java.net.URLEncoder.encode( newFileName, "UTF-8"));
				response.addHeader("Content-Length", "" + myfile.length());
				response.setContentType("application/octet-stream");
				
				toClient = new BufferedOutputStream(response.getOutputStream());
				fileInputStream=new FileInputStream(myfile);
				fis = new BufferedInputStream(fileInputStream);
				//通过ioutil 对接输入输出流，实现文件下载
				IOUtils.copy(fis, toClient);//,"UTF-8"
				toClient.flush();
				 isSuc=true;
			}
		} catch (Exception e) {
			logger.error("【文件下载失败】", e);
		} finally {
			//关闭流
			if(fis!=null) {
        		try {
        			fis.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
//			IOUtils.closeQuietly(fis);
			//IOUtils.closeQuietly(fileInputStream);
			//IOUtils.closeQuietly(toClient);
			if(fileInputStream!=null) {
        		try {
        			fileInputStream.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
			if(toClient!=null) {
        		try {
        			toClient.close();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
		}
		return isSuc;
	}
	
	/**
	 * 将文件进行加密保存
	 * @param inputStream
	 * @param folder
	 * @param newFileName
	 * @param pwd
	 */
	public static void saveFilePwd(InputStream inputStream,String folder,String newFileName,String pwd){
		OPCPackage opc=null;
		FileOutputStream fos=null;
		try {
			long startTime=System.currentTimeMillis();
			logger.info("加密保存文件开始");
			
			//创建POIFS文件系统  加密文件
			POIFSFileSystem fs = new POIFSFileSystem();
			EncryptionInfo info = new EncryptionInfo(EncryptionMode.standard);
			Encryptor enc = info.getEncryptor();
			enc.confirmPassword(pwd);
			
			//然后把字节输入到输入流，然后输入到OPC包里面
			opc = OPCPackage.open(inputStream);
			OutputStream os = enc.getDataStream(fs);
			opc.save(os);
			opc.close();
			
			File folderFile=new File(folder);
			if(!folderFile.exists()){
				folderFile.mkdirs();
			}
			String filePath=folder+newFileName;
			fos = new FileOutputStream(filePath);
			fs.writeFilesystem(fos);
			fos.close();
			long endTime=System.currentTimeMillis();
			logger.info("加密保存文件结束:{}，消耗:{} mills",filePath,(endTime-startTime));
		} catch (Exception e) {
			logger.error("加密保存文件时，出现异常：{}",e);
		}finally{
			//IOUtils.closeQuietly(opc);
			//IOUtils.closeQuietly(fos);
			if(opc != null) {
				try {
					opc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 过滤文件名的非法字符串
	 * @param fileName
	 * @return
	 */
	public static String filterFileNameIllegalStr(String fileName){
		Pattern pattern=Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher=pattern.matcher(fileName);
		fileName=matcher.replaceAll("");
		return fileName;
	}
	
	
}

