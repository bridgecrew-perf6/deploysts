package excel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.DateUtils;
import utils.HMDrmUtil;

public class ExcelReadTest {

    public static final String RESULT_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포결과서_ver1.1_SAMPLE.xlsx";
    public static final String RESULT_EMERGENCY_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포결과서_ver1.1_긴급_SAMPLE.xlsx";
    public static final String PLAN_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포계획서_ver1.1_SAMPLE.xlsx";
    public static final String PRE_CHECK_SAMPLE_FILE = "S:\\배포목록\\자동생성\\배포점검회의목록_SAMPLE.xlsx";
    public static final String DEPLOEYLIST_SAMPLE_FILE = "S:\\배포목록\\자동생성\\배포목록_SAMPLE.xlsx";
    public static final String APPROVAL_CHECK_SAMPLE_FILE = "S:\\\\배포목록\\\\자동생성\\\\배포승인체크_SAMPLE.xlsx";
    public static final String REDMINE_ISSUE_FILE = "S:\\배포목록\\자동생성\\레드마인_배포_ISSUE.xlsx";
    public static final String DEPLOY_PATH = "S:\\배포목록\\";
    // public static final String FILE_SEPARATOR = File.separator;
    public static final String FILE_SEPARATOR = "\\\\";
    public static final String SAMPLE_FILE = RESULT_SAMPLE_FILE;


    public static void main(String[] args) {

        try {
            String resultDate = DateUtils.getDate("yyyyMMdd");

            String str_yyyy = resultDate.substring(0, 4);
            String str_MM = resultDate.substring(4, 6);
            String str_dd = resultDate.substring(6, 8);

            // String orgFilePath = "S:\\개발관련\\배포관리시스템\\HMS_배포결과서_ver1.1_20200305.xlsx";
            // javaFileCopy(SAMPLE_FILE, orgFilePath);


            File orgFile = new File(SAMPLE_FILE);
            // File orgFile = new File("S:\\개발관련\\배포관리시스템\\HMS_배포결과서_ver1.1_20200305.xlsx");

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            FileInputStream file = new FileInputStream(excelFile);

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet = workbook.getSheetAt(1);
            // 행의 수
            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = 0; rowindex < rows; rowindex++) {
                // 행을읽는다
                XSSFRow row = sheet.getRow(rowindex);
                if (row != null) {
                    // 셀의 수
                    // int cells = row.getPhysicalNumberOfCells();
                    int cells = row.getLastCellNum();
                    System.out.println("getPhysicalNumberOfCells: " + row.getPhysicalNumberOfCells());
                    System.out.println("getLastCellNum: " + row.getLastCellNum());
                    for (columnindex = 0; columnindex <= cells; columnindex++) {
                        // 셀값을 읽는다
                        XSSFCell cell = row.getCell(columnindex);
                        String value = "";
                        // 셀이 빈값일경우를 위한 널체크
                        if (cell == null) {
                            continue;
                        } else {
                            // 타입별로 내용 읽기
                            switch (cell.getCellType()) {
                                case XSSFCell.CELL_TYPE_FORMULA:
                                    value = cell.getCellFormula();
                                    break;
                                case XSSFCell.CELL_TYPE_NUMERIC:
                                    value = cell.getNumericCellValue() + "";
                                    break;
                                case XSSFCell.CELL_TYPE_STRING:
                                    value = cell.getStringCellValue() + "";
                                    break;
                                case XSSFCell.CELL_TYPE_BLANK:
                                    value = cell.getBooleanCellValue() + "";
                                    break;
                                case XSSFCell.CELL_TYPE_ERROR:
                                    value = cell.getErrorCellValue() + "";
                                    break;
                            }
                        }
                        System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
                    }

                }
            }


            for (int i = 4; i < 35; i++) {
                String korDayOfTheWeekBySample = DateUtils.getDayOfTheWeekString(i - 3);
                System.out.println("korDayOfTheWeekBySample ::" + korDayOfTheWeekBySample);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public static void javaFileCopy(String oriFilePath, String copyFilePath) {

        // 파일객체생성
        File oriFile = new File(oriFilePath);
        // 복사파일객체생성
        File copyFile = new File(copyFilePath);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {

            fis = new FileInputStream(oriFile); // 읽을파일
            fos = new FileOutputStream(copyFile); // 복사할파일

            int fileByte = 0;
            // fis.read()가 -1 이면 파일을 다 읽은것
            while ((fileByte = fis.read()) != -1) {
                fos.write(fileByte);
            }
            // 자원사용종료
            System.out.println("File Copied Sucess");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("File Copied Fail");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("File Copied Fail");
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
