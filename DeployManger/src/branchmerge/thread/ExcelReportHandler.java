package branchmerge.thread;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import branchmerge.model.WorkingResource;

public class ExcelReportHandler {

    static FileInputStream fileIn = null;
    static FileOutputStream fileOut = null;
    static POIFSFileSystem fs = null;
    static HSSFWorkbook wb = null;
    static HSSFSheet sh = null;


    public ExcelReportHandler() {

    }


    public ExcelReportHandler(String filePath) throws Exception {

        File file = new File(filePath);
        if (file.exists()) {
            file.createNewFile();
        }
        fileIn = new FileInputStream(filePath);
        fs = new POIFSFileSystem(fileIn);
        wb = new HSSFWorkbook(fs);
        sh = wb.getSheet("Sheet1");
        fileOut = new FileOutputStream(filePath);
    }


    public static List readExceReport(String filePath) throws IOException {

        FileInputStream fileIn;
        List result;
        fileIn = null;
        result = new ArrayList();
        fileIn = new FileInputStream(filePath);
        POIFSFileSystem fs = new POIFSFileSystem(fileIn);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheet("Sheet1");
        int lastRowNum = sheet.getLastRowNum();
        for (int rowNum = 4; rowNum < lastRowNum; rowNum++) {
            HSSFRow row = sheet.getRow(rowNum);
            String ceresID = row.getCell(0).getStringCellValue();
            String path = row.getCell(1).getStringCellValue();
            long revision = (long) row.getCell(2).getNumericCellValue();
            String owner = row.getCell(3).getStringCellValue();
            String comments = row.getCell(4).getStringCellValue();
            boolean selected = row.getCell(5).getBooleanCellValue();
            String mergeResult = row.getCell(6).getStringCellValue();
            WorkingResource rowValue =
                    new WorkingResource(" ", path, (new StringBuilder(String.valueOf(revision))).toString(), owner, "", ceresID,
                            mergeResult, selected);
            result.add(rowValue);
        }


        try {
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void writeExcelFile(List list, String filePath) throws IOException {

        checkDublicationInReport(list);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sheet1");
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        HSSFCellStyle titlestyle = workbook.createCellStyle();
        titlestyle.setFillForegroundColor((short) 40);
        titlestyle.setFillPattern((short) 1);
        titlestyle.setAlignment((short) 2);
        titlestyle.setFont(font);
        HSSFCellStyle hcs = workbook.createCellStyle();
        hcs.setBorderBottom((short) 5);
        hcs.setBorderRight((short) 1);
        hcs.setBorderLeft((short) 1);
        hcs.setBorderTop((short) 1);
        hcs.setBorderBottom((short) 1);
        HSSFRow row0 = sheet.createRow(0);
        HSSFCell cell_00 = row0.createCell((short) 0);
        cell_00.setCellValue("\uBC18\uC601\uC694\uCCAD\uC11C");
        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell_10 = row1.createCell((short) 0);
        HSSFCell cell_11 = row1.createCell((short) 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String dateTime = format.format(new Date());
        cell_10.setCellValue("\uC77C\uC2DC");
        cell_11.setCellValue(dateTime);
        HSSFRow row2 = sheet.createRow(2);
        HSSFCell cell_20 = row2.createCell((short) 0);
        HSSFCell cell_21 = row2.createCell((short) 1);
        cell_20.setCellValue("\uB2F4\uB2F9\uC790");
        cell_21.setCellValue("\uAE40\uC2B9\uCCA0");
        HSSFRow row = sheet.createRow(3);
        HSSFCell cell0 = row.createCell((short) 0);
        cell0.setCellValue("CR_ID");
        cell0.setCellStyle(titlestyle);
        HSSFCell cell1 = row.createCell((short) 1);
        cell1.setCellValue("\uACBD\uB85C");
        cell1.setCellStyle(titlestyle);
        HSSFCell cell2 = row.createCell((short) 2);
        cell2.setCellValue("\uB9AC\uBE44\uC804");
        cell2.setCellStyle(titlestyle);
        HSSFCell cell3 = row.createCell((short) 3);
        cell3.setCellValue("\uC694\uCCAD\uC790");
        cell3.setCellStyle(titlestyle);
        HSSFCell cell4 = row.createCell((short) 4);
        cell4.setCellValue("\uCEE4\uB9E8\uD2B8");
        cell4.setCellStyle(titlestyle);
        HSSFCell cell5 = row.createCell((short) 5);
        cell5.setCellValue("\uC120\uD0DD");
        cell5.setCellStyle(titlestyle);
        HSSFCell cell6 = row.createCell((short) 6);
        cell6.setCellValue("\uBA38\uC9C0 \uACB0\uACFC");
        cell6.setCellStyle(titlestyle);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        HSSFCellStyle styleCenter = workbook.createCellStyle();
        styleCenter.setAlignment((short) 2);
        styleCenter.setFont(font);
        WorkingResource entry;
        HSSFCell newCell6;
        for (Iterator iterator = list.iterator(); iterator.hasNext(); newCell6.setCellValue(entry.getStatus())) {
            entry = (WorkingResource) iterator.next();
            HSSFRow lastRow = sheet.getRow(sheet.getLastRowNum());
            HSSFRow newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            HSSFCell newCell0 = newRow.createCell((short) 0);
            HSSFCell newCell1 = newRow.createCell((short) 1);
            HSSFCell newCell2 = newRow.createCell((short) 2);
            HSSFCell newCell3 = newRow.createCell((short) 3);
            HSSFCell newCell4 = newRow.createCell((short) 4);
            HSSFCell newCell5 = newRow.createCell((short) 5);
            newCell6 = newRow.createCell((short) 6);
            newCell0.setCellType(1);
            newCell1.setCellType(1);
            newCell2.setCellType(1);
            newCell3.setCellType(1);
            newCell4.setCellType(1);
            newCell5.setCellType(1);
            newCell6.setCellType(1);
            newCell0.setCellValue(entry.id);
            newCell1.setCellValue(entry.getPath());
            newCell2.setCellValue(entry.getRevision());
            newCell3.setCellValue(entry.getOwner());
            newCell4.setCellValue(entry.getComments());
            newCell5.setCellValue(entry.getSelected());
        }

        String realName = filePath;
        File file = new File(realName);
        FileOutputStream fileOutput = new FileOutputStream(file);
        workbook.write(fileOutput);
        fileOutput.close();
    }


    public void update(WorkingResource wr, int rowNum) {

        try {
            HSSFRow newRow = sh.getRow(rowNum);
            System.out.println((new StringBuilder(String.valueOf(rowNum))).append(" ").append(newRow.getRowNum()).append(" ")
                    .append(wr.getStatus()).toString());
            HSSFCell newCell0 = newRow.getCell((short) 0);
            HSSFCell newCell1 = newRow.getCell((short) 1);
            HSSFCell newCell2 = newRow.getCell((short) 2);
            HSSFCell newCell3 = newRow.getCell((short) 3);
            HSSFCell newCell4 = newRow.getCell((short) 4);
            HSSFCell newCell5 = newRow.getCell((short) 5);
            HSSFCell newCell6 = newRow.getCell((short) 6);
            newCell0.setCellValue(wr.id.toString());
            newCell1.setCellValue(wr.getPath().toString());
            newCell2.setCellValue((new StringBuilder(String.valueOf(wr.getRevision()))).toString());
            newCell3.setCellValue(wr.getOwner().toString());
            newCell4.setCellValue(wr.getComments().toString());
            newCell5.setCellValue((new StringBuilder(String.valueOf(wr.getSelected()))).toString());
            newCell6.setCellValue(wr.getMergeResult().toString());
            wb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() {

        try {
            fileOut.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static List checkDublicationInReport(List list) {

        return null;
    }


}
