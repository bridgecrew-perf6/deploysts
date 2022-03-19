package excel;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import redmine.DeployRequest;
import redmine.RedmineClient;
import utils.DateUtils;
import utils.HMDrmUtil;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;

public class UpdateExcel {

    static final String CONFIG_PROPERTIES = "/data/home/hisis/ec/tools/deploy/config/config.properties";
    static final String DEPLOY_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/temp/";
    static final String NAS_DIR_REL = "/NAS-EC_PRD/files/";
    static final String NAS_DIR_TST = "/NAS-EC_DEV/files/";
    static String RDM_PRJ_ID;
    static String RDM_URL;
    static String RDM_LOGINID;
    static String RDM_PWD;

    public static final String RESULT_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포결과서_ver1.1_SAMPLE.xlsx";
    public static final String RESULT_EMERGENCY_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포결과서_ver1.1_긴급_SAMPLE.xlsx";
    public static final String PLAN_SAMPLE_FILE = "S:\\배포목록\\자동생성\\HMS_배포계획서_ver1.1_SAMPLE.xlsx";
    public static final String PRE_CHECK_SAMPLE_FILE = "S:\\배포목록\\자동생성\\배포점검회의목록_SAMPLE.xlsx";
    public static final String DEPLOEYLIST_SAMPLE_FILE = "S:\\배포목록\\자동생성\\배포목록_SAMPLE.xlsx";
    public static final String APPROVAL_CHECK_SAMPLE_FILE = "S:\\\\배포목록\\\\자동생성\\\\배포승인체크_SAMPLE.xlsx";
    public static final String REDMINE_ISSUE_FILE = "S:\\배포목록\\자동생성\\레드마인_배포_ISSUE.xlsx";
    public static final String DEPLOY_PATH = "S:\\배포목록\\";
    public static final String DEPLOY_FILESERVER_PATH = "\\\\vhifileserver\\\\쇼핑몰운영\\\\(0002) e커머스 하이마트팀\\\\00.배포목록\\\\";
    // public static final String FILE_SEPARATOR = File.separator;
    public static final String FILE_SEPARATOR = "\\\\";

    static int intWorkRoll_01_count = 0; // 전시
    static int intWorkRoll_02_count = 0; // 상품
    static int intWorkRoll_03_count = 0; // 주문/클레임
    static int intWorkRoll_04_count = 0; // 모바일
    static int intWorkRoll_05_count = 0; // 회원
    static int intWorkRoll_06_count = 0; // 이벤트
    static int intWorkRoll_07_count = 0; // 옴니앱
    static int intWorkRoll_08_count = 0; // 기타

    static int fo_count = 0; // FO
    static int mo_count = 0; // MO
    static int tc_count = 0; // TC
    static int bo_count = 0; // BO
    static int batch_count = 0; // BATCH
    static int etc_count = 0; // 기타

    static String str_yyyy = "";
    static String str_MM = "";
    static String str_dd = "";
    static String korDayOfTheWeek = "";
    static String dayMsgStr = "";
    static String createDeployPath = "";

    static List<RedmineExcel> unapprovedList = new ArrayList<RedmineExcel>();


    // Unapproved


    public static void main(String[] args) {

        int isEmergency = 0; // 0:정기배포, 1:이벤트배포, 9:긴급배포


        List<RedmineExcel> redmineExcelList = getRedmineIssue();
        List<String> deplyDateList = new ArrayList<String>();
        for (int i = 0; i < redmineExcelList.size(); i++) {
            RedmineExcel value = redmineExcelList.get(i);
            System.out.println("value getItemId ::" + value.getItemId());
            System.out.println("value getDeployType ::" + value.getDeployType());
            System.out.println("value getStatus ::" + value.getStatus());
            System.out.println("value getSubject ::" + value.getSubject());
            System.out.println("value getManager ::" + value.getManager());
            System.out.println("value getPl ::" + value.getPl());
            System.out.println("value getWorkRoll ::" + value.getWorkRoll());
            System.out.println("value getDeploySystem ::" + value.getDeploySystem());
            System.out.println("value getDeployFiles ::" + value.getDeployFiles());
            System.out.println("value getDeployDate ::" + value.getDeployDate());
            System.out.println("value getLastDeployServer ::" + value.getLastDeployServer());
            System.out.println("value getBoWorkReqNo ::" + value.getBoWorkReqNo());
            System.out.println("value getDescription ::" + value.getDescription());

            System.out.println("===============================");
            deplyDateList.add(value.getDeployDate());
        }

        System.out.println("전시 Count:: " + intWorkRoll_01_count);
        System.out.println("상품 Count:: " + intWorkRoll_02_count);
        System.out.println("주문/클레임 Count:: " + intWorkRoll_03_count);
        System.out.println("모바일 Count:: " + intWorkRoll_04_count);
        System.out.println("회원 Count:: " + intWorkRoll_05_count);
        System.out.println("이벤트 Count:: " + intWorkRoll_06_count);
        System.out.println("옴니앱 Count:: " + intWorkRoll_07_count);
        System.out.println("기타 Count:: " + intWorkRoll_08_count);

        System.out.println("fo_count :: " + fo_count);
        System.out.println("mo_count:: " + mo_count);
        System.out.println("bo_count:: " + bo_count);
        System.out.println("batch_count:: " + batch_count);
        System.out.println("etc_count:: " + etc_count);
        System.out.println("tc_count:: " + tc_count);

        for (int i = 0; i < deplyDateList.size(); i++) {
            if (deplyDateList.contains(DateUtils.getDate("yyyy-MM-dd"))) {
                isEmergency = 9;
            }
        }

        System.out.println("isEmergency::::" + isEmergency);

        String resultDate = DateUtils.getNextDate(1, "yyyyMMdd");
        if (isEmergency != 0) {
            resultDate = DateUtils.getDate("yyyyMMdd");
        }
        str_yyyy = resultDate.substring(0, 4);
        str_MM = resultDate.substring(4, 6);
        str_dd = resultDate.substring(6, 8);
        korDayOfTheWeek = DateUtils.getDayOfTheWeekString(1);

        dayMsgStr = str_MM + "/" + str_dd + "(" + korDayOfTheWeek + ")";

        createDeployPath = str_yyyy + FILE_SEPARATOR + str_MM + FILE_SEPARATOR + str_dd;


        /**
         * 배포결과서 생성
         *************************************************************/
        DeployResultListExcelCreate(redmineExcelList, isEmergency);

        if (isEmergency == 0) {

            /**
             * 배포목록 생성
             *************************************************************/
            DeployListExcelCreate(redmineExcelList);

            /**
             * 배포승인체크 생성
             *************************************************************/
            DeployApprovalCheckListExcelCreate(redmineExcelList);

            /**
             * 배포계획서 생성
             *************************************************************/
            DeployPlanListExcelCreate(redmineExcelList);

            /**
             * 배포점검목록 생성
             *************************************************************/
            DeployPreCheckListExcelCreate(redmineExcelList);

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


    // 배포목록생성
    public static void DeployListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getNextDate(1, "yyyyMMdd");

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = DEPLOEYLIST_SAMPLE_FILE;
        // createDeployPath = createDeployPath + "배포목록";
        System.out.println("createDeployPath::::" + createDeployPath);

        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }

        FileInputStream file = null;
        FileOutputStream out = null;

        try {
            orgFilePath = orgFilePath.replaceAll("SAMPLE", todate);
            orgFilePath = orgFilePath.replaceAll("자동생성", createDeployPath);

            javaFileCopy(DEPLOEYLIST_SAMPLE_FILE, orgFilePath);

            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);

            XSSFRow row = null;
            XSSFCell cell = null;
            int i = 1;
            for (i = 1; i <= redmineExcelList.size(); i++) {
                RedmineExcel value = redmineExcelList.get(i - 1);

                row = sheet.getRow(i);
                cell = row.getCell(0);
                cell.setCellValue(value.getItemId());
                // setCellFormat(workbook, sheet, 0, i);

                cell = row.getCell(1);
                cell.setCellValue(value.getSubject());

                cell = row.getCell(2);
                cell.setCellValue(value.getDescription());

                cell = row.getCell(3);
                cell.setCellValue(value.getManager());

                cell = row.getCell(4);
                cell.setCellValue(value.getPl());

                cell = row.getCell(5);
                cell.setCellValue(value.getWorkRoll());

                cell = row.getCell(6);
                cell.setCellValue(value.getDeploySystem());

                cell = row.getCell(7);
                cell.setCellValue(value.getDeployFiles());

                cell = row.getCell(8);
                cell.setCellValue(value.getDeployDate());

            }

            // removeEmptyRows(sheet, redmineExcelList.size());

            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = redmineExcelList.size() + 1; rowindex < rows; rowindex++) {
                // 나머지 행 삭제
                XSSFRow row1 = sheet.getRow(rowindex);
                if (row1 != null) {
                    sheet.removeRow(row1);
                }
            }

            // int shiftRows = rows - redmineExcelList.size();
            // sheet.shiftRows(100, rows, -shiftRows);

            // Write the workbook in file system
            System.out.println("orgFilePath :: " + orgFilePath);
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();
            System.out.println("Update Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    // 배포점검회의목록생성
    public static void DeployPreCheckListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getNextDate(1, "yyyyMMdd");
        String todateFormat02 = DateUtils.getNextDate(1, "yyyy-MM-dd");

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = PRE_CHECK_SAMPLE_FILE;
        // createDeployPath = createDeployPath + "배포예정목록";
        System.out.println("createDeployPath::::" + createDeployPath);

        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }

        FileInputStream file = null;
        FileOutputStream out = null;

        String cellTitle = "운영배포 점검목록 : " + todateFormat02 + "(" + korDayOfTheWeek + ")";
        try {
            orgFilePath = orgFilePath.replaceAll("SAMPLE", todate);
            orgFilePath = orgFilePath.replaceAll("자동생성", createDeployPath);

            javaFileCopy(PRE_CHECK_SAMPLE_FILE, orgFilePath);

            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);

            XSSFRow row = null;
            XSSFCell cell = null;

            row = sheet.getRow(1);
            cell = row.getCell(4);
            cell.setCellValue(cellTitle);

            int sampleStartCellNum = 4; // org 18

            for (int i = sampleStartCellNum; i < redmineExcelList.size() + sampleStartCellNum; i++) {
                RedmineExcel value = redmineExcelList.get(i - sampleStartCellNum);

                // 3번 행 : 0번 열 값은: No.
                // 3번 행 : 1번 열 값은: 일감번호
                // 3번 행 : 2번 열 값은: 유형
                // 3번 행 : 3번 열 값은: 제목
                // 3번 행 : 4번 열 값은: 설명
                // 3번 행 : 5번 열 값은: 담당자
                // 3번 행 : 6번 열 값은: 업무구분
                // 3번 행 : 7번 열 값은: 대상시스템
                // 3번 행 : 8번 열 값은: 배포희망

                row = sheet.getRow(i);
                cell = row.getCell(1);
                cell.setCellValue(value.getItemId());
                // setCellFormat(workbook, sheet, 1, i);

                cell = row.getCell(2);
                cell.setCellValue(value.getDeployType());

                cell = row.getCell(3);
                cell.setCellValue(value.getSubject());

                cell = row.getCell(4);
                cell.setCellValue(value.getDescription());

                cell = row.getCell(5);
                cell.setCellValue(value.getManager());

                cell = row.getCell(6);
                cell.setCellValue(value.getWorkRoll());

                cell = row.getCell(7);
                cell.setCellValue(value.getDeploySystem());

                cell = row.getCell(8);
                cell.setCellValue(value.getDeployDate());

                if (value.getDeployType().equals("배포요청(승인전)")) {
                    for (int j = 0; j < 8; j++) {
                        // setCellFillForegroundColor(workbook, sheet, j, i);
                    }

                }
            }

            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = redmineExcelList.size() + sampleStartCellNum; rowindex < rows; rowindex++) {
                // 나머지 행 삭제
                XSSFRow row1 = sheet.getRow(rowindex);
                if (row1 != null) {
                    int cells = row1.getPhysicalNumberOfCells();
                    for (columnindex = 0; columnindex <= cells; columnindex++) {
                        // 셀값을 읽는다
                        XSSFCell cell1 = row1.getCell(columnindex);
                        if (cell1 == null) {
                            continue;
                        } else {
                            row1.removeCell(cell1);
                        }
                    }
                    sheet.removeRow(row1);
                }
            }

            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();
            System.out.println("Update Successfully");

            StringBuffer preChekcMsg = new StringBuffer();
            preChekcMsg.append(dayMsgStr + " 배포점검회의목록입니다. [파일첨부] \n");
            preChekcMsg.append("점검목록이 있으신 분들은 운영배포승인으로 변경해주시기 바랍니다. \n");
            preChekcMsg.append("점검목록중 추가 또는 제외될 일감이 있을 경우 반드시 별도 쪽지로 알려 주시기 바랍니다. \n");
            preChekcMsg.append("   - 배포요청(승인전)인 일감은 배포시에 제외 될 수 있으니 필히 확인하시기 바랍니다. \n");

            if (unapprovedList.size() > 0) {
                preChekcMsg.append("\n");
                preChekcMsg.append("아래 일감은 배포요청(승인전) 일감 입니다. \n");
                preChekcMsg.append(" ----------------------------------------------------------------------------------------\n");
                for (int i = 0; i < unapprovedList.size(); i++) {
                    RedmineExcel value = unapprovedList.get(i);
                    preChekcMsg.append("#" + value.getItemId() + " | " + value.getSubject() + "  " + " | " + value.getManager() + " \n");
                }
            }

            System.out.println(preChekcMsg.toString());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


    // 배포승인체크생성
    public static void DeployApprovalCheckListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getNextDate(1, "yyyyMMdd");

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = APPROVAL_CHECK_SAMPLE_FILE;
        // createDeployPath = createDeployPath + "배포목록\\\\승인체크";
        System.out.println("createDeployPath::::" + createDeployPath);

        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }

        FileInputStream file = null;
        FileOutputStream out = null;

        try {
            orgFilePath = orgFilePath.replaceAll("SAMPLE", todate);
            orgFilePath = orgFilePath.replaceAll("자동생성", createDeployPath);

            javaFileCopy(APPROVAL_CHECK_SAMPLE_FILE, orgFilePath);

            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);

            XSSFRow row = null;
            XSSFCell cell = null;
            int i = 1;
            for (i = 1; i <= redmineExcelList.size(); i++) {
                RedmineExcel value = redmineExcelList.get(i - 1);

                row = sheet.getRow(i);
                cell = row.getCell(0);
                cell.setCellValue(value.getItemId());
                // setCellFormat(workbook, sheet, 0, i);

                cell = row.getCell(1);
                cell.setCellValue(value.getDeployType());

                cell = row.getCell(2);
                cell.setCellValue(value.getStatus());

                cell = row.getCell(3);
                cell.setCellValue(value.getSubject());

                cell = row.getCell(4);
                cell.setCellValue(value.getManager());

                cell = row.getCell(5);
                cell.setCellValue(value.getPl());

                cell = row.getCell(6);
                cell.setCellValue(value.getWorkRoll());

                cell = row.getCell(7);
                cell.setCellValue(value.getDeploySystem());

                cell = row.getCell(8);
                cell.setCellValue(value.getDeployFiles());

                cell = row.getCell(9);
                cell.setCellValue(value.getDeployDate());

                cell = row.getCell(10);
                cell.setCellValue(value.getLastDeployServer());

                cell = row.getCell(11);
                cell.setCellValue(value.getBoWorkReqNo());

                cell = row.getCell(12);
                cell.setCellValue(value.getDescription());

                // System.out.println("value getItemId ::" + value.getItemId());
                // System.out.println("value getDeployType ::" + value.getDeployType());
                // System.out.println("value getStatus ::" + value.getStatus());
                // System.out.println("value getSubject ::" + value.getSubject());
                // System.out.println("value getManager ::" + value.getManager());
                // System.out.println("value getPl ::" + value.getPl());
                // System.out.println("value getWorkRoll ::" + value.getWorkRoll());
                // System.out.println("value getDeploySystem ::" + value.getDeploySystem());
                // System.out.println("value getDeployFiles ::" + value.getDeployFiles());
                // System.out.println("value getDeployDate ::" + value.getDeployDate());
                // System.out.println("value getLastDeployServer ::" + value.getLastDeployServer());
                // System.out.println("value getBoWorkReqNo ::" + value.getBoWorkReqNo());
                // System.out.println("value getDescription ::" + value.getDescription());

            }

            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = redmineExcelList.size() + 1; rowindex < rows; rowindex++) {
                // 나머지 행 삭제
                XSSFRow row1 = sheet.getRow(rowindex);
                if (row1 != null) {
                    int cells = row1.getPhysicalNumberOfCells();
                    for (columnindex = 0; columnindex <= cells; columnindex++) {
                        // 셀값을 읽는다
                        XSSFCell cell1 = row1.getCell(columnindex);
                        if (cell1 == null) {
                            continue;
                        } else {
                            row1.removeCell(cell1);
                        }
                    }
                    sheet.removeRow(row1);
                }
            }


            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();
            System.out.println("Update Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    // 배포결과서 생성
    public static void DeployResultListExcelCreate(List<RedmineExcel> redmineExcelList, int isEmergency) {

        String resultDate = DateUtils.getNextDate(1, "yyyyMMdd");

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = RESULT_SAMPLE_FILE;
        String replaceSampleStr = "SAMPLE";

        String cellTitle =
                resultDate.substring(0, 4) + "년 " + resultDate.substring(4, 6) + "월 " + resultDate.substring(6, 8) + "일 " + "("
                        + korDayOfTheWeek + ") 배포 작업결과서";
        String jobTime =
                resultDate.substring(0, 4) + "년 " + resultDate.substring(4, 6) + "월 " + resultDate.substring(6, 8) + "일 " + "09시 44분 ~ "
                        + resultDate.substring(0, 4) + "년 " + resultDate.substring(4, 6) + "월 " + resultDate.substring(6, 8) + "일 "
                        + "17시 58분";


        if (isEmergency != 0) {
            orgFilePath = RESULT_EMERGENCY_SAMPLE_FILE;
            replaceSampleStr = "긴급_SAMPLE";
            resultDate = DateUtils.getNextDate(1, "yyyyMMdd");
            resultDate = DateUtils.getDate("yyyyMMdd");
            korDayOfTheWeek = DateUtils.getDayOfTheWeekString(0);

            if (isEmergency == 1) {
                // 0:정기배포, 1:이벤트배포, 9:긴급배포
                jobTime = jobTime + " [이벤트배포]";
            } else if (isEmergency == 9) {
                jobTime = jobTime + " [긴급배포]";
            }

        }

        System.out.println("DEPLOY_PATH::::" + DEPLOY_PATH);
        System.out.println("DEPLOY_PATH::::" + DEPLOY_FILESERVER_PATH);
        System.out.println("createDeployPath::::" + createDeployPath);

        String deployFileServerPath = DEPLOY_FILESERVER_PATH + createDeployPath + "\\\\test";
        // File deployFileServerPathFile = new File(deployFileServerPath);
        // if (!deployFileServerPathFile.isDirectory()) {
        // System.out.println("deployFileServerPathFile False");
        // deployFileServerPathFile.mkdirs();
        // } else {
        // System.out.println("deployFileServerPathFile True");
        // }

        // createDeployPath = createDeployPath + "배포목록_보고서";
        System.out.println("createDeployPath::::" + createDeployPath);


        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }



        orgFilePath = orgFilePath.replaceAll(replaceSampleStr, resultDate);
        orgFilePath = orgFilePath.replaceAll("자동생성", createDeployPath);

        if (isEmergency != 0) {
            javaFileCopy(RESULT_EMERGENCY_SAMPLE_FILE, orgFilePath);

        } else {
            javaFileCopy(RESULT_SAMPLE_FILE, orgFilePath);
        }

        FileInputStream file = null;
        FileOutputStream out = null;

        try {

            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);

            int sampleStartCellNum = 18; // org 18
            int sampleLastCellNum = 107; // org 107

            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = sampleStartCellNum + 1; rowindex <= sampleLastCellNum - redmineExcelList.size() + 1; rowindex++) {
                // 나머지 행 삭제
                XSSFRow row1 = sheet.getRow(rowindex);
                if (row1 != null) {
                    sheet.removeRow(row1);
                }
            }
            // Shift rows 6 - 11 on the spreadsheet to the top (rows 0 - 5)
            // sheet.shiftRows(5, 10, -5);
            int shiftRows = 90 - redmineExcelList.size();
            sheet.shiftRows(rowindex, rows + 1, -shiftRows);


            XSSFRow row = null;
            XSSFCell cell = null;

            // cellTitle
            row = sheet.getRow(1);
            cell = row.getCell(1);
            cell.setCellValue(cellTitle);

            // jobTime
            row = sheet.getRow(6);
            cell = row.getCell(2);
            cell.setCellValue(jobTime);


            String jobDtlStr = "";

            for (int i = sampleStartCellNum; i < redmineExcelList.size() + sampleStartCellNum; i++) {
                RedmineExcel value = redmineExcelList.get(i - sampleStartCellNum);

                // 17번 행 : 1번 열 값은: 번호
                // 17번 행 : 2번 열 값은: #Redmine (일감번호)
                // 17번 행 : 3번 열 값은: 구분
                // 17번 행 : 4번 열 값은: 제목
                // 17번 행 : 5번 열 값은: 내용
                // 17번 행 : 6번 열 값은: BO 요청번호
                // 17번 행 : 7번 열 값은: 요청자
                // 17번 행 : 8번 열 값은: 담당파트
                // 17번 행 : 9번 열 값은: 개발담당자
                // 17번 행 : 10번 열 값은: 작업결과
                // 17번 행 : 11번 열 값은: 비고
                // 17번 행 : 12번 열 값은: false


                row = sheet.getRow(i);
                cell = row.getCell(1);
                cell.setCellValue(i - (sampleStartCellNum - 1));

                cell = row.getCell(2);
                cell.setCellValue(value.getItemId());

                jobDtlStr = jobDtlStr + " #" + value.getItemId();

                cell = row.getCell(4);
                cell.setCellValue(value.getSubject());

                cell = row.getCell(5);
                cell.setCellValue(value.getDescription());

                cell = row.getCell(6);
                cell.setCellValue(value.getBoWorkReqNo());

                cell = row.getCell(8);
                cell.setCellValue(value.getWorkRoll());

                cell = row.getCell(9);
                cell.setCellValue(value.getManager());

            }


            if (isEmergency != 0) {

                row = sheet.getRow(9);
                cell = row.getCell(4);

                // 0:정기배포, 1:이벤트배포, 9:긴급배포
                if (isEmergency == 1) {
                    cell.setCellValue(" [이벤트배포] " + jobDtlStr);
                } else if (isEmergency == 9) {
                    cell.setCellValue(" [긴급배포] " + jobDtlStr);
                }
            }

            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();
            System.out.println("redmineExcelList.size() ::" + redmineExcelList.size());
            System.out.println("orgFilePath ::" + orgFilePath);
            // System.out.println("deployFileServerPath ::" + deployFileServerPath);
            System.out.println("Update Successfully");
            // javaFileCopy(orgFilePath, deployFileServerPath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    // 배포계획서 생성
    public static void DeployPlanListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getNextDate(1, "yyyyMMdd");

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = PLAN_SAMPLE_FILE;
        // createDeployPath = createDeployPath + "배포목록_보고서";
        System.out.println("createDeployPath::::" + createDeployPath);

        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }

        FileInputStream file = null;
        FileOutputStream out = null;

        String cellTitle =
                todate.substring(0, 4) + "년 " + todate.substring(4, 6) + "월 " + todate.substring(6, 8) + "일 " + "(" + korDayOfTheWeek
                        + ") 배포 작업계획서";
        try {
            orgFilePath = orgFilePath.replaceAll("SAMPLE", todate);
            orgFilePath = orgFilePath.replaceAll("자동생성", createDeployPath);

            javaFileCopy(PLAN_SAMPLE_FILE, orgFilePath);

            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);

            int sampleStartCellNum = 9;
            int sampleLastCellNum = 98;

            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = sampleStartCellNum + 1; rowindex <= sampleLastCellNum - redmineExcelList.size() + 1; rowindex++) {
                // 나머지 행 삭제
                XSSFRow row1 = sheet.getRow(rowindex);
                if (row1 != null) {
                    sheet.removeRow(row1);
                }
            }
            // Shift rows 6 - 11 on the spreadsheet to the top (rows 0 - 5)
            // sheet.shiftRows(5, 10, -5);
            int shiftRows = 90 - redmineExcelList.size();
            sheet.shiftRows(rowindex, rows + 1, -shiftRows);


            XSSFRow row = null;
            XSSFCell cell = null;

            row = sheet.getRow(2);
            cell = row.getCell(1);
            cell.setCellValue(cellTitle);

            for (int i = sampleStartCellNum; i < redmineExcelList.size() + sampleStartCellNum; i++) {
                RedmineExcel value = redmineExcelList.get(i - sampleStartCellNum);

                // 8번 행 : 1번 열 값은: 번호
                // 8번 행 : 2번 열 값은: #Redmine (일감번호)
                // 8번 행 : 3번 열 값은: 구분
                // 8번 행 : 4번 열 값은: 제목
                // 8번 행 : 5번 열 값은: 내용
                // 8번 행 : 6번 열 값은: BO 요청번호
                // 8번 행 : 7번 열 값은: 요청자
                // 8번 행 : 8번 열 값은: 담당파트
                // 8번 행 : 9번 열 값은: 개발담당자
                // 8번 행 : 10번 열 값은: 비고


                row = sheet.getRow(i);
                cell = row.getCell(1);
                cell.setCellValue(i - (sampleStartCellNum - 1));

                cell = row.getCell(2);
                cell.setCellValue(value.getItemId());

                cell = row.getCell(4);
                cell.setCellValue(value.getSubject());

                cell = row.getCell(5);
                cell.setCellValue(value.getDescription());

                cell = row.getCell(6);
                cell.setCellValue(value.getBoWorkReqNo());

                cell = row.getCell(8);
                cell.setCellValue(value.getWorkRoll());

                cell = row.getCell(9);
                cell.setCellValue(value.getManager());

                if (value.getDeployType().contains("승인전")) {
                    cell = row.getCell(10);
                    cell.setCellValue(value.getDeployType());
                }
            }

            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();
            System.out.println("redmineExcelList.size() ::" + redmineExcelList.size());
            System.out.println("Update Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


    public static List<RedmineExcel> getRedmineIssue() {

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = "";

        List<RedmineExcel> retList = new ArrayList<RedmineExcel>();

        unapprovedList = new ArrayList<RedmineExcel>();

        // intWorkRoll_01_count = 0; // 전시
        // intWorkRoll_02_count = 0; // 상품
        // intWorkRoll_03_count = 0; // 주문/클레임
        // intWorkRoll_04_count = 0; // 모바일
        // intWorkRoll_05_count = 0; // 회원
        // intWorkRoll_06_count = 0; // 이벤트
        // intWorkRoll_07_count = 0; // 옴니앱
        // intWorkRoll_08_count = 0; // 기타

        try {

            orgFilePath = REDMINE_ISSUE_FILE;
            orgFile = new File(orgFilePath);

            /**********************
             * DRM 엑셀 복호화
             * ********************/
            File excelFile = HMDrmUtil.decode(orgFile);

            FileInputStream file = new FileInputStream(excelFile);
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            // 시트 수 (첫번째에만 존재하므로 0을 준다)
            // 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            sheet = workbook.getSheetAt(0);


            // 행의 수
            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = 1; rowindex < rows; rowindex++) {
                // 행을읽는다

                RedmineExcel redmineExcel = new RedmineExcel();

                XSSFRow row = sheet.getRow(rowindex);
                if (row != null) {
                    // 셀의 수
                    int cells = row.getPhysicalNumberOfCells();
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
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        Date date = cell.getDateCellValue();
                                        value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                                    } else {
                                        value = cell.getNumericCellValue() + "";
                                        // System.out.println("value getNumericCellValue ::" + value);
                                    }
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

                        // private int itemId; // 일감번호
                        // private String deployType; // 유형
                        // private String status; // 상태
                        // private String subject; // 제목
                        // private String manager; // 담당자
                        // private String pl; // PL
                        // private String workRoll; // 업무구분
                        // private String deploySystem; // 대상시스템
                        // private String deployFiles; // 배포파일
                        // private String deployDate; // 배포희망일자
                        // private String lastDeployServer; // 최종배포서버
                        // private String boWorkReqNo; // BO요청번호
                        // private String description; // 설명

                        if (columnindex == 0) {
                            // System.out.println("value setItemId ::" + value);

                            double dvalue = Double.parseDouble(value);
                            DecimalFormat df = new DecimalFormat("0");
                            redmineExcel.setItemId(Integer.parseInt(df.format(dvalue)));

                        } else if (columnindex == 1) {
                            redmineExcel.setDeployType(value);
                        } else if (columnindex == 2) {
                            redmineExcel.setStatus(value);
                        } else if (columnindex == 3) {
                            redmineExcel.setSubject(value);
                        } else if (columnindex == 4) {
                            redmineExcel.setManager(value);
                        } else if (columnindex == 5) {
                            redmineExcel.setPl(value);
                        } else if (columnindex == 6) {
                            String rtnWorkRoll = checkWorkRoll(redmineExcel.getManager(), redmineExcel.getPl(), value);

                            if (rtnWorkRoll.contains("전시")) {
                                intWorkRoll_01_count++;
                            } else if (rtnWorkRoll.contains("상품")) {
                                intWorkRoll_02_count++;
                            } else if (rtnWorkRoll.contains("주문/클레임")) {
                                intWorkRoll_03_count++;
                            } else if (rtnWorkRoll.contains("모바일")) {
                                intWorkRoll_04_count++;
                            } else if (rtnWorkRoll.contains("회원")) {
                                intWorkRoll_05_count++;
                            } else if (rtnWorkRoll.contains("이벤트")) {
                                intWorkRoll_06_count++;
                            } else if (rtnWorkRoll.contains("옴니앱")) {
                                intWorkRoll_07_count++;
                            } else if (rtnWorkRoll.contains("기타")) {
                                intWorkRoll_08_count++;
                            } else {
                                intWorkRoll_08_count++;
                            }
                            // intWorkRoll_01_count = 0; // 전시
                            // intWorkRoll_02_count = 0; // 상품
                            // intWorkRoll_03_count = 0; // 주문/클레임
                            // intWorkRoll_04_count = 0; // 모바일
                            // intWorkRoll_05_count = 0; // 회원
                            // intWorkRoll_06_count = 0; // 이벤트
                            // intWorkRoll_07_count = 0; // 옴니앱
                            // intWorkRoll_08_count = 0; // 기타

                            redmineExcel.setWorkRoll(rtnWorkRoll);
                        } else if (columnindex == 7) {
                            redmineExcel.setDeploySystem(value);
                        } else if (columnindex == 8) {
                            redmineExcel.setDeployFiles(value);
                        } else if (columnindex == 9) {
                            redmineExcel.setDeployDate(value);
                        } else if (columnindex == 10) {
                            redmineExcel.setLastDeployServer(value);
                        } else if (columnindex == 11) {
                            if (value != null) {
                                double dvalue = Double.parseDouble(value);
                                DecimalFormat df = new DecimalFormat("0");
                                redmineExcel.setBoWorkReqNo(Integer.parseInt(df.format(dvalue)));
                            }

                        } else if (columnindex == 12) {
                            redmineExcel.setDescription(value);
                        }

                        // System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
                    }

                    List<String> deploySystemList = new ArrayList<String>();

                    System.out.println("redmineExcel.getDeployFiles() ::" + redmineExcel.getDeployFiles());


                    if (redmineExcel.getDeployFiles().contains("/03_Front") || redmineExcel.getDeployFiles().contains("/app/domain/fo/")) {
                        deploySystemList.add("FO/LPS");
                        fo_count++;
                    } else if (redmineExcel.getDeployFiles().contains("/04_FrontMobile")
                            || redmineExcel.getDeployFiles().contains("/app/domain/mo/")) {
                        deploySystemList.add("MO/mLPS");
                        mo_count++;
                    } else if (redmineExcel.getDeployFiles().contains("/05_BO")
                            || redmineExcel.getDeployFiles().contains("/app/domain/bo/")) {
                        deploySystemList.add("BO/CC/PO");
                        bo_count++;
                    } else if (redmineExcel.getDeployFiles().contains("/09_Tablet")
                            || redmineExcel.getDeployFiles().contains("/app/domain/tc/")) {
                        deploySystemList.add("옴니앱");
                        tc_count++;
                    } else if (redmineExcel.getDeployFiles().contains("/06_Batch")) {
                        deploySystemList.add("BATCH");
                        batch_count++;
                    } else {
                        deploySystemList.add("기타");
                        etc_count++;
                    }

                    for (int i = 0; i < deploySystemList.size(); i++) {
                        String deploySystem = redmineExcel.getDeploySystem();
                        if (!deploySystem.contains(deploySystemList.get(i))) {
                            deploySystem = deploySystem + "," + deploySystemList.get(i);
                            redmineExcel.setDeploySystem(deploySystem);
                        }
                    }

                    retList.add(redmineExcel);

                    // 승인전 리스트에 추가
                    if (redmineExcel.getDeployType().equals("배포요청(승인전)")) {
                        unapprovedList.add(redmineExcel);
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retList;
    }


    public static List<List<String>> getRedmineCsvFile() {

        // String csvPath = "C:\\Users\\1326618\\Downloads\\issues(1).csv";
        String csvPath = "S:\\개발관련\\배포관리시스템\\issues.csv";
        List<RedmineExcel> retList = new ArrayList<RedmineExcel>();
        List<List<String>> ret = new ArrayList<List<String>>();
        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;
        List<String> tmpList = null;



        RedmineExcel redmineExcel = new RedmineExcel();
        try {
            is = new FileInputStream(csvPath);
            reader = new InputStreamReader(is, "euc-kr");
            br = new BufferedReader(reader);
            String str = "";
            while ((str = br.readLine()) != null) {
                tmpList = new ArrayList<String>();
                String array[] = str.split(",");
                // 배열에서 리스트 반환
                tmpList = Arrays.asList(array);
                // System.out.println(tmpList);
                // System.out.println("=============================");

                if (isParsableToInt(array[0])) {
                    // redmineExcel = new RedmineExcel();
                    // redmineExcel.setItemId(Integer.parseInt(array[0]));
                    // redmineExcel.setDeployType(array[1]);
                    // redmineExcel.setStatus(array[2]);
                    // redmineExcel.setSubject(array[3]);
                    // redmineExcel.setManager(array[4]);
                    // redmineExcel.setPl(array[5]);
                    // redmineExcel.setWorkRoll(array[6]);
                    // redmineExcel.setDeploySystem(array[7]);
                    // redmineExcel.setDeployFiles(array[8]);
                    // redmineExcel.setDeployDate(array[9]);
                    // redmineExcel.setLastDeployServer(array[10]);
                    // redmineExcel.setBoWorkReqNo(array[11]);
                    // redmineExcel.setDescription(array[12]);
                    //
                    // retList.add(redmineExcel);

                    // private int itemId; // 일감번호
                    // private String deployType; // 유형
                    // private String status; // 상태
                    // private String subject; // 제목
                    // private String manager; // 담당자
                    // private String pl; // PL
                    // private String workRoll; // 업무구분
                    // private String deploySystem; // 대상시스템
                    // private String deployFiles; // 배포파일
                    // private String deployDate; // 배포희망일자
                    // private String lastDeployServer; // 최종배포서버
                    // private String boWorkReqNo; // BO요청번호
                    // private String description; // 설명

                    ret.add(tmpList);
                }
            }// while_end

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    public static List<List<String>> getRedmineCsvFile2() {

        List resources = null;

        Properties configProps = loadConfig("C:\\deploy\\config\\config.properties");

        RDM_PRJ_ID = configProps.getProperty("RDM_PRJ_ID");
        RDM_URL = configProps.getProperty("RDM_URL");
        RDM_LOGINID = configProps.getProperty("RDM_LOGINID");
        RDM_PWD = configProps.getProperty("RDM_PWD");

        IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, RDM_LOGINID, RDM_PWD);

        try {
            boolean isSearch = false;
            DeployRequest deployRequest = null;
            String files = "";
            String author = "";
            int id = 0;
            String subject = "";
            List list = RedmineClient.getDeployRequests(issueManger, RDM_PRJ_ID, 16);
            for (int i = 0; i < list.size(); i++) {
                deployRequest = (DeployRequest) list.get(i);
                files = deployRequest.delpoyFileList;
                author = deployRequest.author;
                id = deployRequest.id;
                subject = deployRequest.subject;
                System.out.println("author ::" + author);
            }

        } catch (RedmineException e) {
            e.printStackTrace();
        }

        return resources;
    }


    public static void setCellFormat(XSSFWorkbook workbook, XSSFSheet sheet, int column, int row) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_JUSTIFY);

        XSSFDataFormat df = workbook.createDataFormat();
        cellStyle.setDataFormat(df.getFormat("####"));
        XSSFRow xssfRow = sheet.getRow(row);
        XSSFCell cell = xssfRow.getCell(column);
        cell.setCellStyle(cellStyle);

        setCellBoarderFillBoldLine(workbook, sheet, column, row);
    }


    public static void setCellBoarderFillBoldLine(XSSFWorkbook workbook, XSSFSheet sheet, int column, int row) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        // cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        // cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        // cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        XSSFRow xssfRow = sheet.getRow(row);
        XSSFCell cell = xssfRow.getCell(column);
        cell.setCellStyle(cellStyle);
    }


    public static void setCellFillForegroundColor(XSSFWorkbook workbook, XSSFSheet sheet, int column, int row) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(new XSSFColor(Color.green));
        // cellStyle.setFillBackgroundColor(new XSSFColor(Color.green));
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

        XSSFRow xssfRow = sheet.getRow(row);
        XSSFCell cell = xssfRow.getCell(column);
        cell.setCellStyle(cellStyle);
    }


    public static String checkWorkRoll(String getManager, String pl, String workRoll) {

        String rtnStr = "";

        String workRoll_01 = "박소영"; // 전시
        String workRoll_02 = "임재유"; // 상품
        String workRoll_03 = "최인철"; // 주문/클레임
        String workRoll_04 = "이민환"; // 모바일
        String workRoll_05 = "한만희"; // 회원
        String workRoll_06 = "김대희"; // 이벤트/판촉/프로모션
        String workRoll_07 = "강덕래"; // 옴니앱
        String workRoll_08 = "김양수"; // 제휴
        String workRoll_09 = "박양희"; // BO
        String workRoll_10 = "김미옥"; // 퍼블

        if (pl.contains(workRoll_01)) {
            rtnStr = "전시";
        } else if (pl.contains(workRoll_02)) {
            rtnStr = "상품";
        } else if (pl.contains(workRoll_03)) {
            rtnStr = "주문/클레임";
        } else if (pl.contains(workRoll_04)) {
            rtnStr = "모바일";
            if (getManager.contains(workRoll_05)) {
                rtnStr = "회원";
            }
        } else if (pl.contains(workRoll_06)) {
            rtnStr = "이벤트/판촉/프로모션";
        } else if (pl.contains(workRoll_07)) {
            rtnStr = "옴니앱";
            if (getManager.contains("AS")) {
                rtnStr = "AS서비스플랫폼";
            }
        } else if (pl.contains(workRoll_08)) {
            rtnStr = "제휴";
        } else if (pl.contains(workRoll_09)) {
            rtnStr = "BO";
        } else if (pl.contains(workRoll_10)) {
            if (workRoll.contains("기타")) {
                rtnStr = "전시";
            } else {
                rtnStr = workRoll;
            }
        } else {
            if (workRoll.contains("기타")) {
                rtnStr = "기타";
            } else {
                rtnStr = workRoll;
            }
        }
        return rtnStr;

    }


    private static Properties loadConfig(String configFile) {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(configFile)));
            return props;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    static boolean isParsableToInt(String i) {

        try {
            Integer.parseInt(i);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
