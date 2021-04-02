package com.site.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 */
public class ExcelUtil {

    /**
     * 导入 Excel 数据
     * @param filePath excel文件所在路径（一般先上传在读取）
     * @param fields 字段数组
     * @param startRowNum 从excel哪一行开始读
     * @return
     * @throws IOException
     */
    public static List<Map<String,Object>> read(String filePath, List<String> fields, int startRowNum) throws IOException {
        InputStream is = new FileInputStream(filePath);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<Map<String,Object>> list = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = startRowNum; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                Map<String,Object> map = new HashMap<>();
                for (int i = 0,leng=fields.size(); i < leng; i++) {
                    map.put(fields.get(i),hssfRow.getCell(i));
                }
                list.add(map);
            }
        }
        return list;
    }

    /** 导出 Excel
     * @param title 标题
     * @param headers  表头
     * @param values  表中元素
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String title, String headers[], String [][] values){
        //创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(title);
        //创建标题合并行
        hssfSheet.addMergedRegion(new CellRangeAddress(0,(short)0,0,(short)headers.length - 1));
        //设置标题样式
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);   //设置居中样式
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置标题字体
        Font titleFont = hssfWorkbook.createFont();
        titleFont.setFontHeightInPoints((short) 14);
        style.setFont(titleFont);
        //设置值表头样式 设置表头居中
        HSSFCellStyle hssfCellStyle = hssfWorkbook.createCellStyle();
        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);   //设置居中样式
        hssfCellStyle.setBorderBottom(BorderStyle.THIN);
        hssfCellStyle.setBorderLeft(BorderStyle.THIN);
        hssfCellStyle.setBorderRight(BorderStyle.THIN);
        hssfCellStyle.setBorderTop(BorderStyle.THIN);
        //设置表内容样式
        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style1 = hssfWorkbook.createCellStyle();
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        style1.setBorderTop(BorderStyle.THIN);
        //产生标题行
        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell cell = hssfRow.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
        //产生表头
        HSSFRow row1 = hssfSheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell hssfCell = row1.createCell(i);
            hssfCell.setCellValue(headers[i]);
            hssfCell.setCellStyle(hssfCellStyle);
        }
        //创建内容
        for (int i = 0; i <values.length; i++){
            row1 = hssfSheet.createRow(i +2);
            for (int j = 0; j < values[i].length; j++){
                //将内容按顺序赋给对应列对象
                HSSFCell hssfCell = row1.createCell(j);
                hssfCell.setCellValue(values[i][j]);
                hssfCell.setCellStyle(style1);
            }
        }
        return hssfWorkbook;
    }
}
