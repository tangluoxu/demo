

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.List;

public class PoiUtils {
	
	/** 
     * 行复制功能（默认不复制单元格样式）
     * @param fromRow 
     * @param toRow 
     */  
	public static void copyRow(Workbook wb,Row fromRow,Row toRow,boolean isCopyVal,boolean isCopyCellStype){
		copyRow(wb, fromRow, toRow, isCopyVal, isCopyCellStype, null);
	}
	
    /** 
     * 行复制功能 
     * @param fromRow 
     * @param toRow 
     */  
    public static void copyRow(Workbook wb,Row fromRow,Row toRow,boolean isCopyVal,boolean isCopyCellStype,CellStyle wbCellStyle){  
        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {  
            Cell tmpCell = cellIt.next();  
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());  
            copyCell(wb,tmpCell, newCell, isCopyVal, isCopyCellStype, wbCellStyle);  
        }  
    }  
    /** 
     * 复制单元格 
     *  
     * @param srcCell 
     * @param distCell 
     * @param copyValueFlag 
     *            true则连同cell的内容一起复制 
     */  
    private static void copyCell(Workbook wb,Cell srcCell, Cell distCell, boolean isCopyVal,boolean isCopyCellStype,CellStyle wbCellStyle) {  
        //复制单元格样式，会导致消耗时间加大
    	if(isCopyCellStype){
        	if(wbCellStyle!=null){
        		copyCellStyle(srcCell.getCellStyle(), wbCellStyle);  
        		//样式  
        		distCell.setCellStyle(wbCellStyle);  
        	}
        }
        //评论  
        if (srcCell.getCellComment() != null) {  
            distCell.setCellComment(srcCell.getCellComment());  
        }  
        // 不同数据类型处理  
        int srcCellType = srcCell.getCellType();  
        distCell.setCellType(srcCellType);  
        if (isCopyVal) {  
            if (srcCellType == Cell.CELL_TYPE_NUMERIC) {  
                if (HSSFDateUtil.isCellDateFormatted(srcCell)) {  
                    distCell.setCellValue(srcCell.getDateCellValue());  
                } else {  
                    distCell.setCellValue(srcCell.getNumericCellValue());  
                }  
            } else if (srcCellType == Cell.CELL_TYPE_STRING) {  
                distCell.setCellValue(srcCell.getStringCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_BLANK) {  
                // nothing21  
            } else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {  
                distCell.setCellValue(srcCell.getBooleanCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_ERROR) {  
                distCell.setCellErrorValue(srcCell.getErrorCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_FORMULA) {  
                distCell.setCellFormula(srcCell.getCellFormula());  
            } else { // nothing29  
            }  
        }  
    } 
    
    /** 
     * 复制一个单元格样式到目的单元格样式 
     * @param fromStyle 
     * @param toStyle 
     */  
    private static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {  
    	
        toStyle.setAlignment(fromStyle.getAlignment());  
        //边框和边框颜色  
        toStyle.setBorderBottom(fromStyle.getBorderBottom());  
        toStyle.setBorderLeft(fromStyle.getBorderLeft());  
        toStyle.setBorderRight(fromStyle.getBorderRight());  
        toStyle.setBorderTop(fromStyle.getBorderTop());  
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());  
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());  
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());  
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());  
          
        //背景和前景  
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());  
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());  
          
        toStyle.setDataFormat(fromStyle.getDataFormat());  
        toStyle.setFillPattern(fromStyle.getFillPattern());  
//      toStyle.setFont(fromStyle.getFont(null));  
        toStyle.setHidden(fromStyle.getHidden());  
        toStyle.setIndention(fromStyle.getIndention());//首行缩进  
        toStyle.setLocked(fromStyle.getLocked());  
        toStyle.setRotation(fromStyle.getRotation());//旋转  
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());  
        toStyle.setWrapText(fromStyle.getWrapText());  
        
        toStyle.cloneStyleFrom(fromStyle);
          
    }  
    
    
    
    public static void copyRow(Row newRow,List<String> cellList){
    	int i=0;
    	Cell tmpCell =null;
    	for(String val:cellList){
    		tmpCell =newRow.createCell(i);
    		tmpCell.setCellValue(val);
    		i++;
    	}
    }
}
