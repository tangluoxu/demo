

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


public class ExcelUtil {
    //保证线程安全
    private ExcelUtil() {
    }

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    private static final ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>(){
		@Override      
	       protected DecimalFormat initialValue() {
	        return new DecimalFormat("#");     
	      } 
	};
	private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = new ThreadLocal<DecimalFormat>(){
		@Override      
		protected DecimalFormat initialValue() {
			return new DecimalFormat("#0.00");     
		} 
	};

    private static XSSFWorkbook WB;

    private static CellStyle HEADSTYLE; // 表头行样式
    private static Font HEADFONT; // 表头行字体
    private static CellStyle CONTENTSTYLE; // 内容行样式
    private static Font CONTENTFONT; // 内容行字体
    
    public static final String GETEXCELVALTYPE_ONE= "1";
    public static final String GETEXCELVALTYPE_TWO= "2";

    public static Sheet setSheetHeadRowData(Workbook wb, String sheetLabelName, String[] headTitleArray) {
        Sheet sheet = wb.createSheet(sheetLabelName);
        Row headRow = sheet.createRow(0);
        CellStyle headStyle = wb.createCellStyle();
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        headStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        headStyle.setWrapText(true); // 设置单元格内容是否自动换行
        headStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Cell cell = null;
        int preLen = headTitleArray.length;
        for (int i = 0; i < preLen; i++) {
            sheet.setColumnWidth(i, 6000);
            cell = headRow.createCell(i);
            cell.setCellValue(headTitleArray[i]);
            cell.setCellStyle(headStyle);
        }
        return sheet;
    }
    
    /**
     * 获取workbook
     * @param fileName
     * @param is
     * @return
     */
    public static Workbook getExcelWb(String fileName, InputStream is) {
    	Workbook wb = null;
    	try {
			if (StringUtils.isNotBlank(fileName)) {
				if (fileName.endsWith("xls")) {
					wb = new HSSFWorkbook(is);//解析xls格式
				} else if (fileName.endsWith("xlsx")) {
					wb = new XSSFWorkbook(is);//解析xlsx格式
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return wb;
    }
    
    public static boolean isExcelFile(String fileName){
    	boolean isSuc=false;
    	if (StringUtils.isNotBlank(fileName)) {
			if (fileName.endsWith("xls")) {
				isSuc=true;
			} else if (fileName.endsWith("xlsx")) {
				isSuc=true;
			}
		}
    	return isSuc;
    }

    /**
     * 关闭workbook.
     * @param wb
     */
    public static void closeWB(Workbook wb) {
        try{
            if(wb != null){
                wb.close();
            }
        }catch (Exception e){
            logger.error("关闭excel中的workbook失败，原因是：{}",e);
        }
    }

    /**
     * 获取excel工作簿中的sheet数据
     * @param wb        工作簿
     * @param sheetAt   第几个sheet
     * @return
     */
    public static Sheet getSheet(Workbook wb, int sheetAt){
        if(wb != null){
            return wb.getSheetAt(sheetAt);
        }else{
            return null;
        }
    }


    public static void addRowDatas(Workbook wb, Sheet sheet, CellStyle cellStyle, String[] colNames, List<Map<String, Object>> datas) {
        if (datas != null && datas.size() > 0) {
            int rowIndex = 0;
            Row row = null;
            Cell cell = null;
            if (cellStyle == null && wb != null) {
                cellStyle = wb.createCellStyle();
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
                cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
                cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
            }
            for (Map<String, Object> temp : datas) {
                rowIndex++;
                row = sheet.createRow(rowIndex);
                int cellIndex = 0;
                for (String colName : colNames) {
                    cell = row.createCell(cellIndex);
                    Object object = temp.get(colName);

                    if (colName.equalsIgnoreCase("meid") && (object == null || StringUtils.isBlank(String.valueOf(object)))) {
                        object = temp.get("imei");
                    }
                    if (object != null) {
                        if (object instanceof Date) {
                            cell.setCellValue(ComUtils.allDateFormat.get().format(object));
                        } else {
                            cell.setCellValue(String.valueOf(object));
                        }
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(cellStyle);
                    cellIndex++;
                }
            }
        }
    }

    
    public static void addRowDatas(Workbook wb, Sheet sheet, List<?> dataList, String[] fieldNames){
    	if(dataList!=null&&dataList.size()>0&&fieldNames!=null&&fieldNames.length>0){
    		int rowIndex=0;
    		Row row=null;
    		Cell cell =null;
    		CellStyle cellStyle = wb.createCellStyle();
    		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
    		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
    		cellStyle.setWrapText(true); // 设置单元格内容是否自动换行  
    		int fieldLen=fieldNames.length;
    		Object value;
    		for(Object obj:dataList){
    			rowIndex++;
    			row = sheet.createRow(rowIndex);
    			for(int cellIndex=0;cellIndex<fieldLen;cellIndex++){
    				cell = row.createCell(cellIndex);  
    				if ("loadSequence".equals(fieldNames[cellIndex])) {
    		            value = rowIndex + "";
    		        } else if ("".equals(fieldNames[cellIndex])) {
    		            value = "";
    		        } else {
	    				//通过反射获取值
	    				value = ReflectClassUtils.invokeGetterMethod(obj,fieldNames[cellIndex]);
	                    //判断是否日期类，如果是日期类就格式化为yyyy-mm-dd hh:mm:ss
	                    if (value instanceof Date) {
	                        value = DateUtils.getDateTime((Date) value);
	                    }
    		        }
                    cell.setCellValue(value==null?"":String.valueOf(value));
    				cell.setCellStyle(cellStyle);
    			}
    		}
    	}
    }
    

    /*
     * 设置excel头部单元格
     */
    public static void setHeadRowData(Sheet sheet, Row row, CellStyle style, String[] datas) {
        Cell cell = null;
        int preLen = datas.length;
        for (int i = 0; i < preLen; i++) {
            sheet.setColumnWidth(i, 6000);
            cell = row.createCell(i);
            cell.setCellValue(datas[i]);
            cell.setCellStyle(style);
        }
    }


    public static <T> List<T> getSheetDates(Sheet sheet, String[] objAttrName, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        List<T> list = new LinkedList<T>();
        Row row = null;
        Cell cell = null;
        List<String> head = new ArrayList<String>();
        row = sheet.getRow(0);
        for(int i =0;i<row.getPhysicalNumberOfCells();i++){
            head.add(getCellValue(row,i));
        }
        for (int rowNum = 1, rows = sheet.getPhysicalNumberOfRows(); rowNum < rows; rowNum++) {
            row = sheet.getRow(rowNum);
            String uniCode = getCellValue(row,0);
            if (row != null && row.getLastCellNum() > 0) {
                if ((row.getCell(0) == null || StringUtils.isBlank(getCellValue(row.getCell(0))))) {
                    //如果第一个字段为空，则跳过该行，
                    continue;
                }
                for (int cellNum = 3; cellNum < head.size(); cellNum++) {
                    T obj = clazz.newInstance();
                    cell = row.getCell(cellNum);
                    Object value;
                    if (cell == null || StringUtils.isBlank(getCellValueV2(cell))) {
                        value = null;
                    } else {
                        value = getCellValueV2(cell);
                    }
                    //如果是0就跳过
                    if(value ==null || "0.00".equals(value) || StringUtils.isBlank(String.valueOf(value))){
                        continue;
                    }
                    //字段类型为String才能用
                    Reflections.invokeSetter(obj, objAttrName[0], uniCode);
                    Reflections.invokeSetter(obj, objAttrName[1], head.get(cellNum));
                    Reflections.invokeSetter(obj, objAttrName[2], value);
                    list.add(obj);
                }
            }
        }

        return list;
    }


    /*
     * 读取excel文件的内容到List集合
     */
    public static List<String[]> getExcelDatas(MultipartFile file) {
        BufferedReader br = null;
        List<String[]> fileAllStrList = new ArrayList<String[]>();
        InputStream is=null;
		try { 
			Workbook wb = null;
			 is=file.getInputStream();
            String fileName = file.getOriginalFilename();
            if (fileName.endsWith("csv")) {
                br = new BufferedReader(new InputStreamReader(is, "GBK"));
                fileAllStrList = new ArrayList<String[]>();
                String line = "";
                while ((line = br.readLine()) != null) {
                    fileAllStrList.add(line.split(","));
                }
            } else {
                if (fileName.endsWith("xls")) {
                    wb = new HSSFWorkbook(is);//解析xls格式
                } else if (fileName.endsWith("xlsx")) {
                    wb = new XSSFWorkbook(is);//解析xlsx格式
                }
                if (wb != null) {
                    Sheet sheet = wb.getSheetAt(0);//第一个工作表
                    Row row = null;
                    Cell cell = null;
                    for (int rowNum = 0, rows = sheet.getPhysicalNumberOfRows(); rowNum < rows; rowNum++) {
                        row = sheet.getRow(rowNum);
                        if (row != null && row.getLastCellNum() > 0) {
                            int cells = row.getLastCellNum();
                            String[] tempArr = new String[cells];
                            for (int cellNum = 0; cellNum < cells; cellNum++) {
                                cell = row.getCell(cellNum);
                                if (cell != null) {
                                    tempArr[cellNum] = getCellValue(cell);
                                } else {
                                    tempArr[cellNum] = "";
                                }
                            }
                            fileAllStrList.add(tempArr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            fileAllStrList = null;
            logger.error("在读取excel文件时，出现异常：{}", e);
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(is);
        }
        return fileAllStrList;
    }

    public static String getCellValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return ComUtils.allDateFormat.get().format(cell.getDateCellValue());
            }
            return decimalFormat.get().format(cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            try {
                return decimalFormat.get().format(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                return String.valueOf(cell.getRichStringCellValue());
            }
        } else {
            return String.valueOf(cell.getStringCellValue()).trim();
        }
    }
    
    /**
     * 格式化数字值
     * @param cell
     * @return
     */
    public static String getFormatCellValue(Cell cell) {
    	if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
    		return String.valueOf(cell.getBooleanCellValue());
    	} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
    		if (HSSFDateUtil.isCellDateFormatted(cell)) {
    			return ComUtils.allDateFormat.get().format(cell.getDateCellValue());
    		}
    		return  DECIMAL_FORMAT.get().format(cell.getNumericCellValue());
    	} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
    		try {
    			return DECIMAL_FORMAT.get().format(cell.getNumericCellValue());
    		} catch (IllegalStateException e) {
    			return String.valueOf(cell.getRichStringCellValue());
    		}
    	} else {
    		return String.valueOf(cell.getStringCellValue()).trim();
    	}
    }


    /**
     * 获取单元格中的数值，其中，数字不含有任何格式
     * @param cell 单元格
     * @return 返回单元格内容
     */
    public static String getCellValueV3(Cell cell) {
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING: //字符串
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_FORMULA: //公式
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BLANK: //空值
                return "";
            case Cell.CELL_TYPE_BOOLEAN: //布尔值
                return  String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_ERROR: //错误值
                return "";
            default:
                return "未知类型";
        }
    }


    /**
     * 获取cell中的值，如果是数字类型，保留到小数点后两位
     *
     * @param cell
     * @return
     */
    public static String getCellValueV2(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return ComUtils.allDateFormat.get().format(cell.getDateCellValue());
            }
            return new DecimalFormat("0.00").format(cell.getNumericCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            try {
                return new DecimalFormat("0.00").format(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                return String.valueOf(cell.getRichStringCellValue());
            }
        } else {
            if("0".equals(cell.getStringCellValue())){
                return "0.00";
            }else {
                return String.valueOf(cell.getStringCellValue());
            }
        }
    }
    
    //对数字不做任何处理。
    public static String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
        	cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        }
        return null;
    }


    public static String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
            return getCellValue(cell);
        }
        return null;
    }
    
    public static String getCellValue(Row row, int cellIndex, DecimalFormat decimalFormat) {
  	  if (row != null) {
  		  Cell cell = row.getCell(cellIndex);
  		  if (cell != null) {
  			  String temp=getCellValue(cell,decimalFormat);
  			  return StringUtils.isNotBlank(temp)?temp.trim(): null;
  		  }
  		  return null;
  	  } else {
  		  return null;
  	  }
    }
    
    public static String getCellValue(Cell cell, DecimalFormat decimalFormat) {
  	  try {
  		  if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
  			  return String.valueOf(cell.getBooleanCellValue());
  		  } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
  			  if (HSSFDateUtil.isCellDateFormatted(cell)) {
  				  return ComUtils.allDateFormat.get().format(cell.getDateCellValue());
  			  }
  			  return decimalFormat.format(cell.getNumericCellValue());
  		  } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
  			  try {
  				  return decimalFormat.format(cell.getNumericCellValue());
  			  } catch (IllegalStateException e) {
  				  return String.valueOf(cell.getRichStringCellValue());
  			  }
  		  } else {
  			  return String.valueOf(cell.getStringCellValue());
  		  }
  	  } catch (Exception e) {
  		  logger.error("单元格={}获取数据异常，原因是：{}", cell, e);
  		  return null;
  	  }
    }

    //如果是数字类型，就保留小数点后两位
    public static String getCellValueV2(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null) {
            return getCellValueV2(cell);
        }
        return null;
    }

    /**
     * 对所有的数据都不做处理，内容实际是什么，就是什么。
     * @param row 行
     * @param cellIndex 第几列
     * @return 返回对应的单元格的数据
     */
    public static String getCellValueV3(Row row, int cellIndex) {
        if(row != null) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null) {
                return getCellValueV3(cell);
            }
            return null;
        }else {
            return null;
        }
    }
    
    /**
     * 校验格式是否非法
     * @param row 行
     * @param cellIndex 第几列
     * @param type 初始化格式 1：getCellValue ， 2：getCellValueV2
     * @return 返回对应的单元格的数据
     */
    public static String getCellValueV4(Row row, int cellIndex, String type) {
    	if(row != null) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null) {
                String value =  getCellValueV3(cell);
                if(StringUtils.isNotBlank(value)){
                	if(GETEXCELVALTYPE_ONE.equals(type)){
                		return getCellValue(row,cellIndex);
                	}else if(GETEXCELVALTYPE_TWO.equals(type)){
                		return getCellValueV2(row,cellIndex);
                	}
                }
            }
            return null;
        }else {
            return null;
        }
    }

    /**
     * 根据sheet获取每个单元格的值.
     * @param sheet
     * @param rowNum
     * @param cellIndex
     * @return
     */
    public static String getCellValueBySheet(Sheet sheet, int rowNum, int cellIndex) {
        if (sheet != null) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                return ExcelUtil.getCellValueV3(row, cellIndex);
            }
        }
        return null;
    }
    
    public static String getCellValueBySheet(Sheet sheet, int rowNum, int cellIndex, int type) {
        if (sheet != null) {
            Row row = sheet.getRow(rowNum);
            
            if (row != null) {
        		if(type==1){
        			return ExcelUtil.getCellValue(row, cellIndex);
                }else{
                	return ExcelUtil.getCellValueV3(row, cellIndex);
                }
            }
        }
        return null;
    }

    public static Sheet getExcelSheet(CommonsMultipartFile impFile) {
    	InputStream is=null;
    	try {
    		Workbook wb = null;
            if (impFile != null && !impFile.isEmpty()) {
            	is=impFile.getInputStream();
                String fileName = impFile.getOriginalFilename();
                if (fileName.endsWith("xls")) {
                    wb = new HSSFWorkbook(is);//解析xls格式
                } else if (fileName.endsWith("xlsx")) {
                    wb = new XSSFWorkbook(is);//解析xlsx格式
                }
                if (wb != null) {
                    return wb.getSheetAt(0);//第一个工作表
                }
            }
        } catch (IOException e) {
            logger.error("在获取Excel文件的Sheet时，出现异常：{}", e);
        }finally{
        	IOUtils.closeQuietly(is);
        }
        return null;
    }

    /**
     * 导出到byte数组
     *
     * @param setInfo
     * @return
     * @throws IOException
     */
    public static byte[] export2ByteArray(ExcelExportData setInfo)
            throws IOException {
        return export2Stream(setInfo).toByteArray();
    }



    /**
     * 导出到流
     *
     * @param setInfo
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static ByteArrayOutputStream export2Stream(ExcelExportData setInfo) throws IOException {
        init();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Set<Entry<String, List<?>>> set = setInfo.getDataMap().entrySet();
        String[] sheetNames = new String[setInfo.getDataMap().size()];
        int sheetNameNum = 0;
        for (Entry<String, List<?>> entry : set) {
            sheetNames[sheetNameNum] = entry.getKey();
            sheetNameNum++;
        }
        XSSFSheet[] sheets = getSheets(setInfo.getDataMap().size(), sheetNames);
        int sheetNum = 0;
        XSSFCellStyle contextstyle = WB.createCellStyle();
        for (Entry<String, List<?>> entry : set) {
            // Sheet
            List<?> objs = entry.getValue();

            // 表头
            creatTableHeadRow(setInfo, sheets, sheetNum);

            // 表体
            String[] fieldNames = setInfo.getFieldNames().get(sheetNum);

            //表格类型
            String[] cellTypes = null;
            List<String[]> cellTypeList = setInfo.getCellTypes();
            if(cellTypeList!= null && !cellTypeList.isEmpty()){
                if(cellTypeList.size()<= sheetNum + 1){
                    cellTypes = cellTypeList.get(sheetNum);
                }
            }

            int rowNum = 1;
            for (Object obj : objs) {
                XSSFRow contentRow = sheets[sheetNum].createRow(rowNum);
                contentRow.setHeight((short) 300);
                XSSFCell[] cells = getCells(contentRow, setInfo.getFieldNames().get(sheetNum).length);
                int cellNum = 0; //
                if (fieldNames != null) {
                    for (int num = 0; num < fieldNames.length; num++) {

                        Object value = ReflectClassUtils.invokeGetterMethod(obj, fieldNames[num]);
                        //判断是否日期类，如果是日期类,判断该日期是否含有时分秒，如果没有，则格式化为年月日，否则，格式化为年月日时分秒
                        if (value instanceof Date) {
                            if(DateUtils.isHaveHour((Date)value)) {
                                value = DateUtils.getDateTime((Date) value);
                            }else{
                                value = DateUtils.getForMatTime((Date) value, "yyyy-MM-dd");
                            }
                        }
                        cells[cellNum].setCellValue(value == null ? "" : value.toString());
                        if(value != null && StringUtils.isNotBlank(String.valueOf(value))) {
                            if (cellTypes != null) {
                                setCellStyle(cellTypes[num], cells[cellNum], contextstyle);
                            }
                        }
                        cellNum++;
                    }
                }
                rowNum++;
            }
            adjustColumnSize(sheets, sheetNum, fieldNames); // 自动调整列宽
            sheetNum++;
        }
        WB.write(outputStream);
        return outputStream;
    }


    /**
     * 设置单元格格式
     */
    private static void setCellStyle(String type, XSSFCell cell, XSSFCellStyle cellStyle){
        cellStyle = WB.createCellStyle();
        XSSFDataFormat dataFormat = WB.createDataFormat();
        if("decimal".equalsIgnoreCase(type)){
            //小数格式,保留小数点后两位
            cellStyle.setDataFormat(dataFormat.getFormat("0.00"));
            cell.setCellValue(Double.parseDouble(cell.getStringCellValue()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        }else if("text".equalsIgnoreCase(type)){
            //文本格式
            cellStyle.setDataFormat(dataFormat.getFormat("@"));
        }else if("currency".equalsIgnoreCase(type)){
            //货币格式
            cellStyle.setDataFormat(dataFormat.getFormat("¥#,##0.00"));
        }else if("percent".equalsIgnoreCase(type)){
            //百分比格式
            cellStyle.setDataFormat(dataFormat.getFormat("0.00%"));
        }
            cell.setCellStyle(cellStyle);
    }

    /**
     * @Description: 初始化
     */
    private static void init() {
        WB = new XSSFWorkbook();

        HEADSTYLE = WB.createCellStyle();
        HEADFONT = WB.createFont();
        CONTENTSTYLE = WB.createCellStyle();
        CONTENTFONT = WB.createFont();

        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
    }

    /**
     * @Description: 自动调整列宽
     */
    private static void adjustColumnSize(XSSFSheet[] sheets, int sheetNum,
                                         String[] fieldNames) {
        for (int i = 0; i < fieldNames.length + 1; i++) {
            sheets[sheetNum].autoSizeColumn(i, true);
        }
    }


    /**
     * @Description: 创建表头行
     */
    private static void creatTableHeadRow(ExcelExportData setInfo,
                                          XSSFSheet[] sheets, int sheetNum) {
        // 表头
        XSSFRow headRow = sheets[sheetNum].createRow(0);
        headRow.setHeight((short) 350);
        // 列头名称
        for (int num = 0, len = setInfo.getColumnNames().get(sheetNum).length; num < len; num++) {
            XSSFCell headCell = headRow.createCell(num);
            headCell.setCellStyle(HEADSTYLE);
            headCell.setCellValue(setInfo.getColumnNames().get(sheetNum)[num]);
            //设置列头的宽度
            sheets[sheetNum].setColumnWidth(num,setInfo.getColumnNames().get(sheetNum)[num].getBytes().length*256);
        }
    }

    /**
     * @Description: 创建所有的Sheet
     */
    private static XSSFSheet[] getSheets(int num, String[] names) {
        XSSFSheet[] sheets = new XSSFSheet[num];
        for (int i = 0; i < num; i++) {
            sheets[i] = WB.createSheet(names[i]);
        }
        return sheets;
    }

    /**
     * @Description: 创建内容行的每一列
     */
    private static XSSFCell[] getCells(XSSFRow contentRow, int num) {
        XSSFCell[] cells = new XSSFCell[num];

        for (int i = 0, len = cells.length; i < len; i++) {
            cells[i] = contentRow.createCell(i);
            cells[i].setCellStyle(CONTENTSTYLE);
        }

        return cells;
    }

    /**
     * @Description: 初始化表头行样式
     */
    private static void initHeadCellStyle() {
        HEADSTYLE.setAlignment(CellStyle.ALIGN_CENTER);
        HEADSTYLE.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HEADSTYLE.setFont(HEADFONT);
        HEADSTYLE.setBorderTop(CellStyle.BORDER_MEDIUM);
        HEADSTYLE.setBorderBottom(CellStyle.BORDER_THIN);
        HEADSTYLE.setBorderLeft(CellStyle.BORDER_THIN);
        HEADSTYLE.setBorderRight(CellStyle.BORDER_THIN);
        HEADSTYLE.setTopBorderColor(IndexedColors.BLUE.index);
        HEADSTYLE.setBottomBorderColor(IndexedColors.BLUE.index);
        HEADSTYLE.setLeftBorderColor(IndexedColors.BLUE.index);
        HEADSTYLE.setRightBorderColor(IndexedColors.BLUE.index);
        HEADSTYLE.setFillForegroundColor(IndexedColors.GOLD.index);
        HEADSTYLE.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }

    /**
     * @Description: 初始化内容行样式
     */
    private static void initContentCellStyle() {
        CONTENTSTYLE.setAlignment(CellStyle.ALIGN_CENTER);
        CONTENTSTYLE.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        CONTENTSTYLE.setFont(CONTENTFONT);
        CONTENTSTYLE.setBorderTop(CellStyle.BORDER_THIN);
        CONTENTSTYLE.setBorderBottom(CellStyle.BORDER_THIN);
        CONTENTSTYLE.setBorderLeft(CellStyle.BORDER_THIN);
        CONTENTSTYLE.setBorderRight(CellStyle.BORDER_THIN);
        CONTENTSTYLE.setTopBorderColor(IndexedColors.BLUE.index);
        CONTENTSTYLE.setBottomBorderColor(IndexedColors.BLUE.index);
        CONTENTSTYLE.setLeftBorderColor(IndexedColors.BLUE.index);
        CONTENTSTYLE.setRightBorderColor(IndexedColors.BLUE.index);
        CONTENTSTYLE.setWrapText(true); // 字段换行
    }


    /**
     * @Description: 初始化表头行字体
     */
    private static void initHeadFont() {
        HEADFONT.setFontName("宋体");
        HEADFONT.setFontHeightInPoints((short) 10);
        HEADFONT.setBoldweight(Font.BOLDWEIGHT_BOLD);
        HEADFONT.setCharSet(Font.DEFAULT_CHARSET);
        HEADFONT.setColor(IndexedColors.BLACK.index);
    }

    /**
     * @Description: 初始化内容行字体
     */
    private static void initContentFont() {
        CONTENTFONT.setFontName("宋体");
        CONTENTFONT.setFontHeightInPoints((short) 10);
        CONTENTFONT.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        CONTENTFONT.setCharSet(Font.DEFAULT_CHARSET);
        CONTENTFONT.setColor(IndexedColors.BLUE_GREY.index);
    }


    /**
     * Excel导出数据类 可以导出同一个excel中多个sheet，多个数据
     *
     * @author djc
     */
    public static class ExcelExportData {

        /**
         * 导出数据 key:String 表示每个Sheet的名称 value:List<?> 表示每个Sheet里的所有数据行
         */
        private LinkedHashMap<String, List<?>> dataMap;

        /**
         * 单个sheet里的数据列标题
         */
        private List<String[]> columnNames;

        /**
         * 单个sheet里每行数据的列对应的对象属性名称
         */
        private List<String[]> fieldNames;

        /**
         * 单个sheet里每行数据的列对应的类型
         */
        private List<String[]> cellTypes;

        public List<String[]> getFieldNames() {
            return fieldNames;
        }

        public void setFieldNames(List<String[]> fieldNames) {
            this.fieldNames = fieldNames;
        }

        public List<String[]> getColumnNames() {
            return columnNames;
        }

        public void setColumnNames(List<String[]> columnNames) {
            this.columnNames = columnNames;
        }

        public LinkedHashMap<String, List<?>> getDataMap() {
            return dataMap;
        }

        public void setDataMap(LinkedHashMap<String, List<?>> dataMap) {
            this.dataMap = dataMap;
        }

        public List<String[]> getCellTypes() {
            return cellTypes;
        }

        public void setCellTypes(List<String[]> cellTypes) {
            this.cellTypes = cellTypes;
        }
    }
    
    /**
     * 复制row的单元格的标题，创建新的sheet
     */
    public static Sheet createErrSheet(Workbook errorWb, Row row, String sheetName){
  	  int totalCellNum=row.getLastCellNum();//总的标题单位格数，在最后一列添加错误描述
  	  String[] displayNamesArr=new String[totalCellNum+1];
  	  for(int i=0;i<totalCellNum;i++){
  		  displayNamesArr[i]=getCellValue(row,i);
  	  }
  	  displayNamesArr[totalCellNum]="错误描述";
  	  return setSheetHeadRowData(errorWb,sheetName, displayNamesArr); 
    }
    

    public static int createErrRow(Workbook errorWb , Sheet errorSheet, int errorRowIndex, Row row, int totalCellNum, String errorDesc){
    	errorRowIndex++;
    	Row errorRow=errorSheet.createRow(errorRowIndex);
    	PoiUtils.copyRow(errorWb, row, errorRow, true, false);
    	Cell errorCell =errorRow.createCell(totalCellNum);
    	errorCell.setCellValue( errorDesc);
    	return errorRowIndex;
    }
    
    public static void addRowDatas(Workbook wb, Sheet sheet, int rowIndex, CellStyle cellStyle, String... valAry) {
  	  if(cellStyle==null){
  		  cellStyle = wb.createCellStyle();
  		  cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
  		  cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
  		  cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
  	  }
  	  Row row= sheet.createRow(rowIndex);
  	  Cell cell;
  	  for(int i=0,len=valAry.length;i<len;i++){
  		  sheet.setColumnWidth(i, 6000);
  		  cell = row.createCell(i);
  		  cell.setCellValue(valAry[i]);
  		  cell.setCellStyle(cellStyle);
  	  }
    }
    
    public static void addRowDatas(Workbook wb, Sheet sheet, int rowIndex, List<?> valAry, CellStyle cellStyle, boolean isNeedBorder) {
  	  if(cellStyle==null){
  		  cellStyle = wb.createCellStyle();
  		  cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
  		  cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
  		  cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
  		  if(isNeedBorder){
  			  cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
  			  cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
  			  cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
  			  cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
  		  }
  	  }
  	  
  	  Row row= sheet.createRow(rowIndex);
  	  Cell cell;
  	  for(int i=0,len=valAry.size();i<len;i++){
  		  sheet.setColumnWidth(i, 6000);
  		  cell = row.createCell(i);
  		  cell.setCellValue(String.valueOf(valAry.get(i)));
  		  cell.setCellStyle(cellStyle);
  	  }
    }
    
    public static void addRowDatas(Workbook wb, Sheet sheet, int rowIndex, List<List<?>> rowDatas) {
    	CellStyle cellStyle = wb.createCellStyle();
    	cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
    	cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
    	cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
    	
    	if(rowDatas!=null&&rowDatas.size()>0){
    		Row row= null;
    		Cell cell;
    		for(List<?> valAry:rowDatas){
    			row= sheet.createRow(rowIndex);
    			for(int i=0,len=valAry.size();i<len;i++){
    	    		sheet.setColumnWidth(i, 6000);
    	    		cell = row.createCell(i);
    	    		cell.setCellValue(String.valueOf(valAry.get(i)));
    	    		cell.setCellStyle(cellStyle);
    	    	}
    			rowIndex++;
    		}
    	}
    }
    
    public static CellStyle getCellStyle(Workbook wb, Short alignment, Short fillForegroundColor, boolean isNeedBorder){
  	  if(alignment==null){
  		  alignment= HSSFCellStyle.ALIGN_CENTER;// 创建一个居中格式
  	  }
  	  if(fillForegroundColor==null){
  		  fillForegroundColor= IndexedColors.GOLD.getIndex();//背景色
  	  }
  	  CellStyle headStyle = wb.createCellStyle();
  	  headStyle.setAlignment(alignment); 
  	  headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
  	  headStyle.setWrapText(true); // 设置单元格内容是否自动换行
  	  headStyle.setFillForegroundColor(fillForegroundColor);
  	  headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
  	  if(isNeedBorder){
  		  headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
  		  headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
  		  headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
  		  headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
  	  }
  	  
  	  return headStyle;
    }
    
    public static void addRowDatas(Workbook wb, Sheet sheet, Integer rowIndex, List<?> dataList, String[] fieldNames, Map<String,Map<String,String>> typeCodeNameMap) {
    	addRowDatas(wb, sheet, rowIndex, dataList, fieldNames, typeCodeNameMap, null);
    }
    
    /**
     * 将数据设置到excel中
     */
    public static void addRowDatas(Workbook wb, Sheet sheet, Integer rowIndex, List<?> dataList, String[] fieldNames, Map<String,Map<String,String>> typeCodeNameMap, List<String> needFormatFieldNames) {
  	  if (dataList != null && dataList.size() > 0 && fieldNames != null && fieldNames.length > 0) {
  		  Row row = null;
  		  Cell cell = null;
  		  CellStyle cellStyle = wb.createCellStyle();
  		  cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
  		  cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
  		  cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
  		  
  		  CellStyle formatCellStyle = null;
  		  boolean isNeedFormatFieldNames=false;
  		  if(needFormatFieldNames!=null&&needFormatFieldNames.size()>0){
  			isNeedFormatFieldNames=true;
  			formatCellStyle = wb.createCellStyle();
  			formatCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
  			formatCellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
  			formatCellStyle.setWrapText(true); // 设置单元格内容是否自动换行

  			DataFormat df = wb.createDataFormat(); // 此处设置数据格式
  			formatCellStyle.setDataFormat(df.getFormat("#0.00"));
  		  }
  		  
  		  int fieldLen = fieldNames.length;
  		  Object value;
  		  SimpleDateFormat dateFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		  Map<String,String> tempMap;
  		  rowIndex=rowIndex!=null?rowIndex:1;//为1是因为要排除第一行的标题
  		  int dataIndex=0;
  		  String val=null;
  		  for (Object obj : dataList) {
  			  dataIndex++;
  			  row = sheet.createRow(rowIndex);
  			  for (int cellIndex = 0; cellIndex < fieldLen; cellIndex++) {
  				  cell = row.createCell(cellIndex);
  				 if(isNeedFormatFieldNames&&needFormatFieldNames.contains(fieldNames[cellIndex])){
  					cell.setCellStyle(formatCellStyle);
  				 }else{
  					 cell.setCellStyle(cellStyle);
  				 }
  				  if ("loadSequence".equals(fieldNames[cellIndex])) {
  					  value = rowIndex + "";
  				  }  if ("dataIndex".equals(fieldNames[cellIndex])) {
  					  value = dataIndex + "";
  				  } else if ("".equals(fieldNames[cellIndex])) {
  					  value = "";
  				  } else {
  					  if(obj instanceof Map){
  						  //通过反射获取值
  						  value = ((Map<?,?>) obj).get(fieldNames[cellIndex]);
  					  }else{
  						  //通过反射获取值
  						  value = ReflectClassUtils.invokeGetterMethod(obj, fieldNames[cellIndex]);
  						  
  						  if(typeCodeNameMap!=null&&typeCodeNameMap.containsKey(fieldNames[cellIndex])){
  							  tempMap= typeCodeNameMap.get(fieldNames[cellIndex]);
  							  value=tempMap.get(String.valueOf(value));
  						  }
  					  }
  					  //判断是否日期类，如果是日期类就格式化为yyyy-mm-dd hh:mm:ss
  					  if (value instanceof Date) {
  						  value = dateFmt.format(value);
  					  }
  				  }
  				if(isNeedFormatFieldNames&&needFormatFieldNames.contains(fieldNames[cellIndex])){
  					val=value!=null?String.valueOf(value):"";
  					if(StringUtils.isNotBlank(val)&&MathUtil.isDouble(val)){
  						cell.setCellValue(Double.parseDouble(val));
  					}else{
  						cell.setCellValue(val);
  					}
  				}else{
  					cell.setCellValue(value == null ? "" : String.valueOf(value));
  				}
  			  }
  			  rowIndex++;
  		  }
  	  }
    }
    
    /**
     * 获取行数据集合
     * @param row
     * @return
     */
    public static List<String> getRowVals(Row row){
    	List<String> list=null;
    	int cellNum=row!=null?row.getLastCellNum():0;
    	if(cellNum>0){
    		list=new ArrayList<String>();
    		for(int i=0;i<cellNum;i++){
    			list.add(getCellValue(row.getCell(i)));
    		}
    	}
    	return list;
    }
    
    /**
     * 根据字母获取其相应的excel列索引
     * A-Z 0-25
     * AA-AZ 26-51===>25*1+1,25*1+1+25
     * BA-BZ 52-77====>25*2+2,25*2+2+25
     * CA-CZ 78-103====>25*3+3,25*3+3+25
     * ZA-ZZ 676-701====>25*26+26,25*26+26+25
     * 
     * 二：CA
     * (C索引+1)*26+(A索引)
     * 三：AAA
     * (A索引+1)*26*26+(A索引+1)*26+(A索引)
     * @param colEnglishName
     * @return
     */
    public static int getColIndex(String colEnglishName){
    	int colIndex=0;
    	char[] charAry= StringUtils.isNotBlank(colEnglishName)?colEnglishName.trim().toCharArray():null;
    	if(charAry!=null&&charAry.length>0){
    		String english="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    		int weiShuIndex=charAry.length;
    		for(char charTemp:charAry){
    			//该英文字符在26字母中的索引
    			int indexTemp=english.indexOf(String.valueOf(charTemp).toUpperCase());
    			if(weiShuIndex>1){
    				colIndex+=(indexTemp+1)*Math.pow(26, (weiShuIndex-1));
    			}else{
    				colIndex+=(indexTemp);
    			}
    			weiShuIndex--;
    		}
    	}
    	return colIndex;
    }


    public static void writeXSSFWorkbook(HttpServletResponse response, Workbook wb, String fileName){
        OutputStream os=null;
        try {
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader( "Content-disposition", "attachment; filename="+ java.net.URLEncoder.encode(fileName, "UTF-8"));
            os=response.getOutputStream();//获取输出流
            wb.write(os);
        } catch (Exception e) {
            logger.error("writeXSSFWorkbook():{}", e);
        } finally{
            IOUtils.closeQuietly(os);
        }
    }
    
	//导出样式
	public static void setRowDatas(Workbook wb, Sheet sheet, List<?> dataList, String[] fieldNames) {
	    if (dataList != null && dataList.size() > 0 && fieldNames != null && fieldNames.length > 0) {
	      int rowIndex = 0;
	      Row row = null;
	      Cell cell = null;
	      CellStyle cellStyle = wb.createCellStyle();
	      cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
	      cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
	      cellStyle.setWrapText(true); // 设置单元格内容是否自动换行
	      int fieldLen = fieldNames.length;
	      Object value;
	      DataFormat format= wb.createDataFormat();
	      for (Object obj : dataList) {
	        rowIndex++;
	        row = sheet.createRow(rowIndex);
	        for (int cellIndex = 0; cellIndex < fieldLen; cellIndex++) {
	          cell = row.createCell(cellIndex);
	          if ("loadSequence".equals(fieldNames[cellIndex])) {
	            value = rowIndex + "";
	            cell.setCellValue(value == null ? "" : String.valueOf(value));
	          } else if ("".equals(fieldNames[cellIndex])) {
	            value = "";
	            cell.setCellValue(value == null ? "" : String.valueOf(value));
	          } else {
	            //通过反射获取值
	            value = ReflectClassUtils.invokeGetterMethod(obj, fieldNames[cellIndex]);
	            //判断是否日期类，如果是日期类就格式化为yyyy-mm-dd hh:mm:ss
	            if (value instanceof Date) {
	              value = DateUtils.getDateTime((Date) value);
	            }
	            if( value instanceof BigDecimal ) {  
	            	value = (BigDecimal) value;
	            	cell.setCellValue(value==null?0:((BigDecimal) value).doubleValue());
			        cellStyle.setDataFormat(format.getFormat("0.00_);(0.00)"));
	            }else{
	            	cell.setCellValue(value == null ? "" : String.valueOf(value));
	            }
	          }
	          cell.setCellStyle(cellStyle);
	        }
	      }
	    }
    }
	
	/**
	 * 获取合并单元格的值
	 * @param sheet
	 * @param rowIndex
	 * @param cellIndex
	 * @return
	 */
	public static String getCellValue(Sheet sheet , int rowIndex , int cellIndex){
		String val=null;
		boolean isMergeRegion=isMergedRegion(sheet, rowIndex, cellIndex);
		if(isMergeRegion){
			val=getMergedRegionValue(sheet, rowIndex, cellIndex);
		}else{
			val=getCellValue(sheet.getRow(rowIndex),cellIndex);
		}
		return val;
	}
	
	/**  
	 * 判断指定的单元格是否是合并单元格  
	 * @param sheet   
	 * @param row 行下标  
	 * @param column 列下标  
	 * @return  
	 */  
	private static boolean isMergedRegion(Sheet sheet, int row , int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();  
		for (int i = 0; i < sheetMergeCount; i++) {  
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			if(row >= firstRow && row <= lastRow){  
				if(column >= firstColumn && column <= lastColumn){  
					return true;  
				}  
			}  
		}  
		return false;  
	}  
	
	/**   
	 * 获取合并单元格的值   
	 * @param sheet   
	 * @param row   
	 * @param column   
	 * @return   
	 */    
	private static String getMergedRegionValue(Sheet sheet , int row , int column){
		int sheetMergeCount = sheet.getNumMergedRegions();    
		for(int i = 0 ; i < sheetMergeCount ; i++){    
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();    
			int lastColumn = ca.getLastColumn();    
			int firstRow = ca.getFirstRow();    
			int lastRow = ca.getLastRow();    
			if(row >= firstRow && row <= lastRow){    
				if(column >= firstColumn && column <= lastColumn){    
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getCellValue(fCell) ;    
				}
			}
		}
		return null ;    
	}    
	
	/**  
	 * 判断合并了行  
	 * @param sheet  
	 * @param row  
	 * @param column  
	 * @return  
	 */  
	public static boolean isMergedRow(Sheet sheet, int row , int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();  
		for (int i = 0; i < sheetMergeCount; i++) {  
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			if(row == firstRow && row == lastRow){  
				if(column >= firstColumn && column <= lastColumn){  
					return true;  
				}  
			}  
		}  
		return false;  
	}  
	
	/**  
	 * 判断sheet页中是否含有合并单元格   
	 * @param sheet   
	 * @return  
	 */  
	public static boolean hasMerged(Sheet sheet) {
		return sheet.getNumMergedRegions() > 0 ? true : false;  
	} 
	
	
	public static Sheet createErrSheet(Workbook errorWb, List<String> row, String sheetName){
		int totalCellNum=row.size();//总的标题单位格数，在最后一列添加错误描述
		String[] displayNamesArr=new String[totalCellNum+1];
		for(int i=0;i<totalCellNum;i++){
			displayNamesArr[i]=row.get(i);
		}
		displayNamesArr[totalCellNum]="错误描述";
		return setSheetHeadRowData(errorWb,sheetName, displayNamesArr); 
	}
	
	public static int createErrRow(Workbook errorWb , Sheet errorSheet, int errorRowIndex, List<String> row, int totalCellNum, String errorDesc){
		errorRowIndex++;
		Row errorRow=errorSheet.createRow(errorRowIndex);
		Cell cell =null;
		for(int i=0,len=row.size();i<len;i++){
			cell =errorRow.createCell(i);
			cell.setCellValue(row.get(i));
		}
		cell =errorRow.createCell(totalCellNum);
		cell.setCellValue( errorDesc);
		return errorRowIndex;
	}

	
	public static Workbook createWb(String sheetName, List<List<String>> titleRow){
		Workbook wb = new SXSSFWorkbook();
		setSheetHeadRowData(wb,sheetName,titleRow,null);
		return wb;
	}
	
	/**
	 * 多行标题
	 * @param wb
	 * @param sheetLabelName
	 * @param titleList
	 * @return
	 */
	public static Sheet setSheetHeadRowData(Workbook wb, String sheetLabelName, List<List<String>> titleList, String errTitle) {
        Sheet sheet = wb.createSheet(sheetLabelName);
        CellStyle headStyle=getDefaultHeadCellStyle(wb);
        Row headRow =null;
        Cell cell = null;
        //标题的行数
        int headRows=titleList!=null?titleList.size():0;
        for(int headRowIndex=0;headRowIndex<headRows;headRowIndex++){
        	headRow =sheet.createRow(headRowIndex);
        	List<String> list=titleList.get(headRowIndex);
        	int len = list.size();
        	for (int i = 0; i < len; i++) {
        		sheet.setColumnWidth(i, 6000);
        		cell = headRow.createCell(i);
        		cell.setCellValue(list.get(i));
        		cell.setCellStyle(headStyle);
        	}
        	if(StringUtils.isNotBlank(errTitle)){
        		sheet.setColumnWidth(len, 6000);
        		cell = headRow.createCell(len);
        		cell.setCellValue(errTitle);
        		cell.setCellStyle(headStyle);
        	}
        }
        return sheet;
    }
	
	private static CellStyle getDefaultHeadCellStyle(Workbook wb){
		CellStyle headStyle = wb.createCellStyle();
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        headStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        headStyle.setWrapText(true); // 设置单元格内容是否自动换行
        headStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return headStyle;
	}
}
