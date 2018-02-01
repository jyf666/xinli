
package utils;

import models.User;
import models.vo.UserVo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ExcelUtils {


    /**
     * 打开excel文件
     *
     * @param file 文件
     * @return Workbook excel文件
     **/
    public static Workbook openExcel(File file){
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(file);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (InvalidFormatException ie){
            ie.printStackTrace();
        }
        return workbook;
    }


    /**
     * 创建一个Excel文件
     **/
    public static HSSFWorkbook createExcel(){

        return new HSSFWorkbook();
    }

    /**
     * 创建一个Excel工作簿
     **/
    public static HSSFSheet createSheet(HSSFWorkbook excel,String sheetName){

        return excel.createSheet(sheetName);
    }

    /**
     * 获取excel工作簿
     *
     * @param file 文件
     * @return List 工作簿集合
     **/
    public static List<Sheet> getSheets(File file){
        try {
            List<Sheet> sheets = new ArrayList<Sheet>();
            Workbook workbook = openExcel(file);
            for(int index= 0;index<workbook.getNumberOfSheets();index++){
                Sheet sheet = workbook.getSheetAt(index);
                if(sheet == null) {
                    continue;
                }
                sheets.add(sheet);
            }
            return sheets;
        }catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    public static List<?> getObjectList(File file,Class objectName){
        try {
            List<Sheet> sheets = getSheets(file);
            return mappedObject(sheets, objectName);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取excel工作簿
     *
     * @param sheets 工作簿集合
     * @param objectName 对象类名称
     * @return List 对象集合
     **/
    private static List<?> mappedObject( List<Sheet> sheets,Class objectName){
        List<Object> list = new ArrayList();
        for(int i = 0;i<sheets.size();i++){
            Sheet sheet = sheets.get(i);
            for(int rowNum = 1;rowNum<=sheet.getLastRowNum();rowNum++){
                Row row = sheet.getRow(rowNum);
                if(row!=null &&  row.getCell(0)!=null &&  !row.getCell(2).getStringCellValue().equals("")){
                    Object obj = mappingObject(row,objectName);
                    list.add(obj);
                }
            }
        }
        return list;
    }

    /**
     * 获取excel工作簿
     *
     * @param row 工作簿一条记录
     * @param objectName 对象类名称
     * @return Object 对象
     **/
    private static Object mappingObject(Row row,Class objectName){
        try {
            Field[] fields = objectName.getDeclaredFields();
            Object obj = objectName.newInstance();

            for (int i = 1; i < row.getLastCellNum(); i++) {

                if (fields[i].getType().getName().equals("java.util.Date")) {
                    setter(obj, fields[i].getName(), row.getCell(i).getDateCellValue(), fields[i].getType());
                } else {
                    setter(obj, fields[i].getName(), getStringCellValue(row.getCell(i)), fields[i].getType());
                }
            }
            return obj;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getStringCellValue(Cell cell) {
        String strCell = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param obj 对象实例
     * @param att 对象字段
     * @param value 对象字段值
     * @param type 对象字段类型
     * @return String 单元格数据内容
     */
    private static void setter(Object obj, String att, Object value, Class<?> type) {
        try {
            att = att.substring(0,1).toUpperCase() + att.substring(1);
            Method method = obj.getClass().getMethod("set" + att, type);
            method.invoke(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        File file = new File("F://a.xlsx");
        List<Sheet> sheets = getSheets(file);
        for(int i = 0;i<sheets.size();i++){
            Sheet sheet = sheets.get(i);
            for(int rowNum = 1;rowNum<=sheet.getLastRowNum();rowNum++){
                Row row = sheet.getRow(rowNum);
                if(row!=null && row.getFirstCellNum()==0){
                    String sql = "INSERT INTO question (  choices, answer, q_type, sub_type, date_created) \n" +
                            "VALUES (  \n" +
                            "\t'";
                    String a = '"'+ "A" + '"' + ":" + '"' + getStringCellValue(row.getCell(0)) + '"';
                    String b = '"'+ "B" + '"' + ":" + '"' + getStringCellValue(row.getCell(1)) + '"';
                    String m = "{" + a + "," + b + "}";
                    sql += m;
                    sql += "', 'N', 10, '01', '2015-09-11 11:35:01');";
//                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        System.out.println(sql);
//                    }
                }
            }
        }
    }
}

