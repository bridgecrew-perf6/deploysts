package excel;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.DateUtils;
import utils.HMDrmUtil;
import utils.HttpDownloader;

public class DeployExcelReport {

    static final String CONFIG_PROPERTIES = "/data/home/hisis/ec/tools/deploy/config/config.properties";
    // static final String DEPLOY_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/temp/";
    static final String DEPLOY_TEMP_DIR = "S:\\배포목록\\자동생성\\TEMP\\";
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


    private static final Pattern csvRE = Pattern.compile(",|[^,\"]+|\"(?:[^\"]|\"\")*\"");
    static List<RedmineExcel> newParseList = new ArrayList<RedmineExcel>();
    static RedmineExcel csvRedmineExcel = new RedmineExcel();

    static List<String> fileStrList = new ArrayList<String>();
    static List<String> descriptionStrList = new ArrayList<String>();

    static int parseCellCnt = 0;
    static int parseCellCnt2 = 0;

    static boolean isSetFileStr = false;
    static boolean isSetDescription = false;
    static boolean isSetBoWorkNo = false;


    public static void main(String[] args) {

        int isEmergency = 0; // 0:정기배포, 1:이벤트배포, 9:긴급배포


        // List<RedmineExcel> redmineExcelList = getRedmineIssue();
        List<RedmineExcel> redmineExcelList = getRedmineIssueHTTPUrl();

        // List<String> deplyDateList = new ArrayList<String>();
        // for (int i = 0; i < redmineExcelList.size(); i++) {
        // RedmineExcel value = redmineExcelList.get(i);
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
        //
        // System.out.println("===============================");
        // deplyDateList.add(value.getDeployDate());
        // }
        //
        // System.out.println("전시 Count:: " + intWorkRoll_01_count);
        // System.out.println("상품 Count:: " + intWorkRoll_02_count);
        // System.out.println("주문/클레임 Count:: " + intWorkRoll_03_count);
        // System.out.println("모바일 Count:: " + intWorkRoll_04_count);
        // System.out.println("회원 Count:: " + intWorkRoll_05_count);
        // System.out.println("이벤트 Count:: " + intWorkRoll_06_count);
        // System.out.println("옴니앱 Count:: " + intWorkRoll_07_count);
        // System.out.println("기타 Count:: " + intWorkRoll_08_count);
        //
        System.out.println("fo_count :: " + fo_count);
        System.out.println("mo_count:: " + mo_count);
        System.out.println("bo_count:: " + bo_count);
        System.out.println("batch_count:: " + batch_count);
        System.out.println("etc_count:: " + etc_count);
        System.out.println("tc_count:: " + tc_count);

        // for (int i = 0; i < deplyDateList.size(); i++) {
        // if (deplyDateList.contains(DateUtils.getDate("yyyy-MM-dd"))) {
        // isEmergency = 9;
        // }
        // }

        isEmergency = 0;
        // System.out.println("isEmergency::::" + isEmergency);

        String resultDate = DateUtils.getDate("yyyyMMdd");
        // resultDate = DateUtils.getNextDate(-1, "yyyyMMdd");

        str_yyyy = resultDate.substring(0, 4);
        str_MM = resultDate.substring(4, 6);
        str_dd = resultDate.substring(6, 8);
        korDayOfTheWeek = DateUtils.getDayOfTheWeekString(0);

        // isEmergency 0:정기배포, 1:이벤트배포, 9:긴급배포
        if (korDayOfTheWeek.equals("화") || korDayOfTheWeek.equals("목")) {
            isEmergency = 0;
        } else {
            isEmergency = 9;
        }

        dayMsgStr = str_MM + "/" + str_dd + " (" + korDayOfTheWeek + ")";

        createDeployPath = str_yyyy + FILE_SEPARATOR + str_MM + FILE_SEPARATOR + str_dd;

        File deployPath = new File(DEPLOY_PATH + createDeployPath);
        System.out.println(DEPLOY_PATH + createDeployPath);
        if (!deployPath.isDirectory()) {
            System.out.println("deployPath False");
            deployPath.mkdirs();
        } else {
            System.out.println("deployPath True");
        }

        /**
         * 배포승인체크 생성
         *************************************************************/
        // DeployApprovalCheckListExcelCreate(redmineExcelList);

        /**
         * 배포계획서 생성
         *************************************************************/
        // DeployPlanListExcelCreate(redmineExcelList);

        /**
         * 배포결과서 샘플 업데이트
         *************************************************************/
        DeployResultListExcelSampleUpdate(str_MM, str_dd, RESULT_SAMPLE_FILE, isEmergency);

        DeployResultListExcelSampleUpdate(str_MM, str_dd, RESULT_EMERGENCY_SAMPLE_FILE, isEmergency);

        /**
         * 배포목록 생성
         *************************************************************/
        DeployListExcelCreate(redmineExcelList);

        /**
         * 배포결과서 생성
         *************************************************************/
        String createFilePath = DeployResultListExcelCreate(redmineExcelList, isEmergency);
        DeployResultListExcelUpdate(str_MM, str_dd, createFilePath, isEmergency);



        StringBuffer preChekcMsg = new StringBuffer();
        preChekcMsg.append("\n");
        preChekcMsg.append("\n");
        preChekcMsg.append(dayMsgStr + "  정기 배포 목록입니다. [파일첨부] \n");
        preChekcMsg.append("빨강색으로 표시한 일감은 배포제외 대상입니다. \n");
        preChekcMsg.append(" - 배포를 원하실 경우에는 확인완료 후 추가배포 요청하시면 됩니다.\n");
        preChekcMsg.append("----------------------------------------------------------------------------------------\n");
        preChekcMsg.append("\n");
        preChekcMsg.append("아래 일감은 '배포요청(승인전)' 일감으로 배포 제외 처리됩니다. \n");
        preChekcMsg.append(" ----------------------------------------------------------------------------------------\n");
        for (int i = 0; i < unapprovedList.size(); i++) {
            RedmineExcel value = unapprovedList.get(i);
            preChekcMsg.append("#" + value.getItemId() + " | " + value.getDeployType() + " | " + value.getSubject() + "  " + " | "
                    + value.getManager() + " \n");
        }
        System.out.println(preChekcMsg.toString());
        System.out.println("");
        System.out.println("#########################################################");
        System.out.println("");

        preChekcMsg = new StringBuffer();
        preChekcMsg.append(dayMsgStr + "  운영1번기 배포되었습니다. \n");
        preChekcMsg.append("일감별로 확인하여 주시고 확인된 일감번호를 알려주시기 바랍니다. \n");
        preChekcMsg.append("재 배포 또는 확인요청시 유기준책임을 참조하여 보내주시기 바랍니다.\n");
        preChekcMsg.append("\n");
        preChekcMsg.append(" ---------------------------------------------------------------------------------------- \n");
        preChekcMsg.append(" 재 배포 또는 추가배포[긴급배포] 요청 시 필수사항 샘플입니다. \n");
        preChekcMsg.append(" ---------------------------------------------------------------------------------------- \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("아래 일감 재 배포[추가배포] 요청합니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("1. 일감번호 : 111111 \n");
        preChekcMsg.append("2. 적용시스템 : FO, MO, BO, TC, BATCH \n");
        preChekcMsg.append("3. 수정파일 :  \n");
        preChekcMsg.append("  - /04_FrontMobile/WebContent/WEB-INF/jsp/search/searchRelationKeyword.jsp 779996 \n");
        preChekcMsg.append("4. 추가파일 :  \n");
        preChekcMsg.append("  - /04_FrontMobile/WebContent/resources/layout/css/common.css 779996 \n");
        preChekcMsg.append("  - /04_FrontMobile/WebContent/WEB-INF/jsp/search/searchSmartFilter.jsp 7798291 \n");
        System.out.println(preChekcMsg.toString());
        System.out.println("#########################################################");

        preChekcMsg = new StringBuffer();
        preChekcMsg.append(dayMsgStr + "  운영1번기 배포되었습니다. \n");
        preChekcMsg.append("일감별로 확인하여 주시고 확인된 일감번호를 알려주시기 바랍니다. \n");
        preChekcMsg.append("재 배포 또는 확인요청시 유기준책임을 참조하여 보내주시기 바랍니다.\n");
        System.out.println(preChekcMsg.toString());
        System.out.println("");
        System.out.println("#########################################################");
        System.out.println("");

        preChekcMsg = new StringBuffer();
        preChekcMsg.append("아래 일감은 현재 미확인 일감입니다.  \n");
        preChekcMsg.append("담당자 및 PL은 확인하시고 확인여부 쪽지로 보내주세요. \n");
        preChekcMsg.append("확인이 지체될 경우에는 확인 예상시간 알려주시기 바랍니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("---------------------------------------------------------------------------------------- \n");
        preChekcMsg.append("* 피드백이 없을 경우 롤백 처리합니다.\n");
        preChekcMsg.append("\n");
        preChekcMsg.append("---------------------------------------------------------------------------------------- \n");
        System.out.println(preChekcMsg.toString());
        System.out.println("");
        System.out.println("#########################################################");
        System.out.println("");

        preChekcMsg = new StringBuffer();
        preChekcMsg.append("금일 " + dayMsgStr + "  배포 건 퍼징(Fuzzing) 확인하시고 알려주세요. \n");
        preChekcMsg.append("ver. " + resultDate + "0002 \n");
        preChekcMsg.append("---------------------------------------------------------------------------------------- \n");
        System.out.println(preChekcMsg.toString());
        System.out.println("#########################################################");

        preChekcMsg = new StringBuffer();
        preChekcMsg.append("금일 " + dayMsgStr + "  정기배포 건 운영 1번기 확인이  완료되었습니다. \n");
        preChekcMsg.append("각 업무 PL 분들께서는 체크리스트 작성하시기 바랍니다. \n");
        System.out.println(preChekcMsg.toString());
        System.out.println("");
        System.out.println("#########################################################");
        System.out.println("");

        preChekcMsg = new StringBuffer();

        preChekcMsg.append("정기배포 시작합니다. Ston작업 부탁합니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("금일은 FO1, MO1만 남겨두고 모두 Ston2번기에 적용하시면 됩니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("\n");
        preChekcMsg.append("2번기 진행하려 합니다. Ston 원복 요청합니다.");
        preChekcMsg.append("\n");
        preChekcMsg.append("\n");
        preChekcMsg.append("2번기 배포를 위해 Ston 원복 했습니다.");
        preChekcMsg.append("\n");
        preChekcMsg.append("확인하시기 바랍니다.");
        preChekcMsg.append("\n");
        preChekcMsg.append("\n");

        preChekcMsg.append("금일 " + dayMsgStr + "  정기배포 건 운영 2번기 진행합니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("운영 1번기 운영시스템에 붙였으며 운영 2번기 배포작업 진행중입니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("운영 2번기 배포작업 끝났으며 운영 2번기 운영시스템에 투입합니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("배포작업이 완료되었습니다. \n");

        preChekcMsg.append("\n");
        preChekcMsg.append("운영 1번기에 배포 했습니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("재 배포 했습니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("추가배포 했습니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("PO 1번기에 배포 했습니다. \n");
        preChekcMsg.append("확인되면 BO에 배포됩니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("배치서버에 적용 했습니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append("운영 1,2번기 모두 적용 했습니다. \n");
        preChekcMsg.append("\n");
        System.out.println(preChekcMsg.toString());
        System.out.println("#########################################################");
        System.out.println("");
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

        String todate = DateUtils.getDate("yyyyMMdd");
        // todate = DateUtils.getNextDate(-1, "yyyyMMdd");

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


    // 배포결과서 생성
    public static String DeployResultListExcelCreate(List<RedmineExcel> redmineExcelList, int isEmergency) {

        String resultDate = DateUtils.getDate("yyyyMMdd");
        // resultDate = DateUtils.getNextDate(-1, "yyyyMMdd");

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

        return orgFilePath;
    }


    // 배포결과서 업데이트
    public static void DeployResultListExcelSampleUpdate(String month, String day, String orgFilePath, int isEmergency) {

        int intDay = Integer.parseInt(day);

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;

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

            sheet = workbook.getSheetAt(1);

            XSSFRow row = null;
            XSSFCell cell = null;

            // 요일 셋팅
            // 2번 행 : 1번 열 값은: 03월

            row = sheet.getRow(2);
            cell = row.getCell(1);
            String compMonth = cell.getStringCellValue();
            compMonth = compMonth.replace("월", "").trim();

            if (!compMonth.equals(month)) {
                cell.setCellValue(month + "월");

                row = sheet.getRow(3);
                for (int i = 4; i < 35; i++) {
                    String korDayOfTheWeekBySample = DateUtils.getDayOfTheWeekStringSet(i - 3);
                    cell = row.getCell(i);
                    cell.setCellValue(korDayOfTheWeekBySample);
                }
            }

            // 해당 날짜에 값세팅
            int intDayCell = intDay + 3;

            for (int i = 5; i <= 20; i++) {
                row = sheet.getRow(i);
                for (int j = intDayCell + 1; j < 35; j++) {
                    cell = row.getCell(j);
                    cell.setCellValue(0);
                }
            }


            // 전시
            row = sheet.getRow(5);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_01_count);

            // 상품
            row = sheet.getRow(6);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_02_count);

            // 주문/클레임
            row = sheet.getRow(7);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_03_count);


            // 모바일
            row = sheet.getRow(8);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_04_count);

            // 회원
            row = sheet.getRow(9);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_05_count);

            // 이벤트
            row = sheet.getRow(10);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_06_count);

            // 옴니앱
            row = sheet.getRow(11);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_07_count);

            // 기타
            row = sheet.getRow(12);
            cell = row.getCell(intDayCell);
            cell.setCellValue(intWorkRoll_08_count);


            int deployTotalCount =
                    intWorkRoll_01_count + intWorkRoll_02_count + intWorkRoll_03_count + intWorkRoll_04_count + intWorkRoll_05_count
                            + intWorkRoll_06_count + intWorkRoll_07_count + intWorkRoll_08_count;

            int deployPartTotalCount = fo_count + mo_count + tc_count + bo_count + batch_count + etc_count;

            // 전체
            row = sheet.getRow(13);
            cell = row.getCell(intDayCell);
            cell.setCellValue(deployPartTotalCount);

            // FO
            row = sheet.getRow(14);
            cell = row.getCell(intDayCell);
            cell.setCellValue(fo_count);

            // MO
            row = sheet.getRow(15);
            cell = row.getCell(intDayCell);
            cell.setCellValue(mo_count);

            // BO, BATCH,기타
            row = sheet.getRow(16);
            cell = row.getCell(intDayCell);
            cell.setCellValue(bo_count + batch_count + etc_count);

            // TC
            row = sheet.getRow(17);
            cell = row.getCell(intDayCell);
            cell.setCellValue(tc_count);

            // 전체
            row = sheet.getRow(18);
            cell = row.getCell(intDayCell);
            cell.setCellValue(deployTotalCount);

            // 정기
            row = sheet.getRow(19);
            cell = row.getCell(intDayCell);
            if (isEmergency == 0) {
                cell.setCellValue(deployTotalCount);
            } else {
                cell.setCellValue(0);
            }

            // 긴급
            row = sheet.getRow(20);
            cell = row.getCell(intDayCell);
            if (isEmergency == 0) {
                cell.setCellValue(0);
            } else {
                cell.setCellValue(deployTotalCount);
            }

            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();

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


    // 배포결과서 업데이트
    public static void DeployResultListExcelUpdate(String month, String day, String orgFilePath, int isEmergency) {

        int intDay = Integer.parseInt(day);

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;

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

            sheet = workbook.getSheetAt(1);

            XSSFRow row = null;
            XSSFCell cell = null;

            // 해당 날짜에 값세팅
            int intDayCell = intDay + 3;

            for (int i = 5; i <= 20; i++) {
                row = sheet.getRow(i);
                for (int j = intDayCell + 1; j < 35; j++) {
                    cell = row.getCell(j);
                    cell.setCellValue("");
                }
            }

            // Write the workbook in file system
            out = new FileOutputStream(new File(orgFilePath));
            workbook.write(out);
            out.close();

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


    // 배포승인체크생성
    public static void DeployApprovalCheckListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getDate("yyyyMMdd");

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


    // 배포계획서 생성
    public static void DeployPlanListExcelCreate(List<RedmineExcel> redmineExcelList) {

        String todate = DateUtils.getDate("yyyyMMdd");

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
        boolean isSetDeploySystem = false;

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
                        if (value != null) {
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
                                isSetDeploySystem = false;
                                System.out.println("setDeploySystem  ::" + value);
                                if (value.contains("FO")) {
                                    fo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("MO")) {
                                    mo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("BO")) {
                                    bo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("옴니앱")) {
                                    tc_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("BATCH")) {
                                    batch_count++;
                                    isSetDeploySystem = true;
                                }

                                if (!isSetDeploySystem) {
                                    etc_count++;
                                }

                                System.out.println("fo_count ::" + fo_count);
                                System.out.println("mo_count ::" + mo_count);

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
                        }

                        // System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
                    }

                    List<String> deploySystemList = new ArrayList<String>();

                    isSetDeploySystem = false;
                    if (redmineExcel.getDeployFiles() != null) {
                        if (redmineExcel.getDeployFiles().contains("/03_Front")
                                || redmineExcel.getDeployFiles().contains("/app/domain/fo/")) {
                            deploySystemList.add("FO/LPS");
                            isSetDeploySystem = true;
                        }

                        if (redmineExcel.getDeployFiles().contains("/04_FrontMobile")
                                || redmineExcel.getDeployFiles().contains("/app/domain/mo/")) {
                            deploySystemList.add("MO/mLPS");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/05_BO") || redmineExcel.getDeployFiles().contains("/app/domain/bo/")) {
                            deploySystemList.add("BO/CC/PO");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/09_Tablet")
                                || redmineExcel.getDeployFiles().contains("/app/domain/tc/")) {
                            deploySystemList.add("옴니앱");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/06_Batch")) {
                            deploySystemList.add("BATCH");
                            isSetDeploySystem = true;
                        }

                        if (!isSetDeploySystem) {
                            deploySystemList.add("기타");
                            // etc_count++;
                        }
                    }


                    for (int i = 0; i < deploySystemList.size(); i++) {
                        String deploySystem = redmineExcel.getDeploySystem();

                        if (!deploySystem.contains(deploySystemList.get(i))) {
                            deploySystem = deploySystem + "," + deploySystemList.get(i);
                            redmineExcel.setDeploySystem(deploySystem);

                            if (deploySystemList.get(i).contains("FO/LPS")) {
                                fo_count++;
                            } else if (deploySystemList.get(i).contains("MO/mLPS")) {
                                mo_count++;
                            } else if (deploySystemList.get(i).contains("BO/CC/PO")) {
                                bo_count++;
                            } else if (deploySystemList.get(i).contains("옴니앱")) {
                                tc_count++;
                            } else if (deploySystemList.get(i).contains("BATCH")) {
                                batch_count++;
                            } else {
                                etc_count++;
                            }
                        }
                    }

                    if (redmineExcel.getDeployType() != null) {
                        retList.add(redmineExcel);
                        // 승인전 리스트에 추가
                        if (redmineExcel.getDeployType().equals("배포요청(승인전)")) {
                            unapprovedList.add(redmineExcel);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retList;
    }


    public static List<RedmineExcel> getRedmineIssueHTTPUrl() {

        String searchDate = DateUtils.getDate("yyyy-MM-dd");

        List<RedmineExcel> retList = new ArrayList<RedmineExcel>();
        String csvFileAddress = DEPLOY_TEMP_DIR + "down_" + searchDate + ".csv";
        String xlsxFileAddress = DEPLOY_TEMP_DIR + "down_" + searchDate + ".xlsx";

        String redmineApprUrl =
                "http://10.154.17.206:3000/projects/hms_/issues.csv?c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=subject&c%5B%5D=assigned_to&c%5B%5D=cf_5&c%5B%5D=cf_12&c%5B%5D=cf_10&c%5B%5D=cf_3&c%5B%5D=cf_8&c%5B%5D=cf_9&c%5B%5D=cf_58&f%5B%5D=tracker_id&f%5B%5D=cf_8&f%5B%5D=status_id&f%5B%5D=&group_by=&op%5Bcf_8%5D=%3D&op%5Bstatus_id%5D=%3D&op%5Btracker_id%5D=%3D&set_filter=1&utf8=%E2%9C%93&v%5Bcf_8%5D%5B%5D="
                        + searchDate
                        + "&f%5B%5D=cf_9&op%5Bcf_9%5D=%3D&v%5Bcf_9%5D%5B%5D=%EC%9A%B4%EC%98%81%EC%84%9C%EB%B2%84"
                        + "&v%5Bstatus_id%5D%5B%5D=1&v%5Bstatus_id%5D%5B%5D=19&v%5Bstatus_id%5D%5B%5D=20&v%5Bstatus_id%5D%5B%5D=21&v%5Bstatus_id%5D%5B%5D=22&v%5Btracker_id%5D%5B%5D=4&v%5Btracker_id%5D%5B%5D=17&description=1";
        HttpDownloader.download(redmineApprUrl, csvFileAddress);
        System.out.println("redmineApprUrl ::" + redmineApprUrl);
        System.out.println("HttpDownloader.download :: success");
        System.out.println(" csvFileAddress Down Url :: " + csvFileAddress);

        csvToXLSX(csvFileAddress, xlsxFileAddress);

        retList = getRedmineIssue(xlsxFileAddress);

        return retList;

    }


    public static List<RedmineExcel> getRedmineIssue(String orgFilePath) {

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        boolean isSetDeploySystem = false;

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

            // orgFilePath = REDMINE_ISSUE_FILE;
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
                        if (value != null) {
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
                                isSetDeploySystem = false;
                                System.out.println("setDeploySystem  ::" + value);
                                if (value.contains("FO")) {
                                    fo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("MO")) {
                                    mo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("BO")) {
                                    bo_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("옴니앱")) {
                                    tc_count++;
                                    isSetDeploySystem = true;
                                }
                                if (value.contains("BATCH")) {
                                    batch_count++;
                                    isSetDeploySystem = true;
                                }

                                if (!isSetDeploySystem) {
                                    etc_count++;
                                }

                                System.out.println("fo_count ::" + fo_count);
                                System.out.println("mo_count ::" + mo_count);

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
                        }

                        // System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
                    }

                    List<String> deploySystemList = new ArrayList<String>();

                    isSetDeploySystem = false;
                    if (redmineExcel.getDeployFiles() != null) {
                        if (redmineExcel.getDeployFiles().contains("/03_Front")
                                || redmineExcel.getDeployFiles().contains("/app/domain/fo/")) {
                            deploySystemList.add("FO/LPS");
                            isSetDeploySystem = true;

                        }

                        if (redmineExcel.getDeployFiles().contains("/04_FrontMobile")
                                || redmineExcel.getDeployFiles().contains("/app/domain/mo/")) {
                            deploySystemList.add("MO/mLPS");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/05_BO") || redmineExcel.getDeployFiles().contains("/app/domain/bo/")) {
                            deploySystemList.add("BO/CC/PO");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/09_Tablet")
                                || redmineExcel.getDeployFiles().contains("/app/domain/tc/")) {
                            deploySystemList.add("옴니앱");
                            isSetDeploySystem = true;
                        }
                        if (redmineExcel.getDeployFiles().contains("/06_Batch")) {
                            deploySystemList.add("BATCH");
                            isSetDeploySystem = true;
                        }

                        if (!isSetDeploySystem) {
                            deploySystemList.add("기타");
                            isSetDeploySystem = true;
                        }
                    }


                    for (int i = 0; i < deploySystemList.size(); i++) {
                        String deploySystem = redmineExcel.getDeploySystem();

                        if (!deploySystem.contains(deploySystemList.get(i))) {
                            deploySystem = deploySystem + "," + deploySystemList.get(i);
                            redmineExcel.setDeploySystem(deploySystem);

                            if (deploySystemList.get(i).contains("FO/LPS")) {
                                fo_count++;
                            } else if (deploySystemList.get(i).contains("MO/mLPS")) {
                                mo_count++;
                            } else if (deploySystemList.get(i).contains("BO/CC/PO")) {
                                bo_count++;
                            } else if (deploySystemList.get(i).contains("옴니앱")) {
                                tc_count++;
                            } else if (deploySystemList.get(i).contains("BATCH")) {
                                batch_count++;
                            } else {
                                etc_count++;
                            }
                        }
                    }

                    if (redmineExcel.getDeployType() != null) {
                        retList.add(redmineExcel);
                        // 승인전 리스트에 추가
                        if (redmineExcel.getDeployType().equals("배포요청(승인전)")) {
                            unapprovedList.add(redmineExcel);
                        }
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

        // Properties configProps = loadConfig("C:\\deploy\\config\\config.properties");
        //
        // RDM_PRJ_ID = configProps.getProperty("RDM_PRJ_ID");
        // RDM_URL = configProps.getProperty("RDM_URL");
        // RDM_LOGINID = configProps.getProperty("RDM_LOGINID");
        // RDM_PWD = configProps.getProperty("RDM_PWD");
        //
        // IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, RDM_LOGINID, RDM_PWD);
        //
        // try {
        //
        // DeployRequest deployRequest = null;
        // String files = "";
        // String author = "";
        // int id = 0;
        // String subject = "";
        //
        // List requests = new ArrayList();
        // List requestsAdd = new ArrayList();
        //
        // String resultDate = DateUtils.getDate("yyyy-MM-dd");
        //
        // Params params = new Params();
        //
        // params.add("project_id", RDM_PRJ_ID);
        // params.add("set_filter", "1");
        //
        // params.add("f[]", "tracker_id");
        // params.add("op[tracker_id]", "=");
        // params.add("v[tracker_id][]", "4");
        // params.add("v[tracker_id][]", "17");
        //
        // params.add("f[]", "status_id");
        // params.add("op[status_id]", "=");
        // params.add("v[status_id][]", "1");
        // params.add("v[status_id][]", "19");
        // params.add("v[status_id][]", "20");
        // params.add("v[status_id][]", "21");
        // params.add("v[status_id][]", "22");
        //
        // params.add("f[]", "cf_8");
        // params.add("op[cf_8]", "=");
        // params.add("v[cf_8][]", resultDate);
        //
        // for (int i = 1; i <= 10; i++) {
        // params.add("page", i + "");
        // System.out.println("params ::" + params);
        // ResultsWrapper<Issue> issuesResults = issueManger.getIssues(params);
        // List issues = issuesResults.getResults();
        //
        // System.out.println(" i ::" + i + "");
        // System.out.println("issues ::" + issues.size());
        //
        // if (issues != null && issues.size() > 0) {
        // DeployRequest request;
        // for (Object element : issues) {
        // Issue issue = (Issue) element;
        // request = new DeployRequest(issue);
        // requestsAdd.add(request);
        // }
        // requests.addAll(requestsAdd);
        // } else {
        // break;
        // }
        // }
        //
        // for (int i = 0; i < requests.size(); i++) {
        // deployRequest = (DeployRequest) requests.get(i);
        // files = deployRequest.delpoyFileList;
        // author = deployRequest.author;
        // id = deployRequest.id;
        // subject = deployRequest.subject;
        //
        // System.out.println("id: " + id);
        // System.out.println("author: " + author);
        // System.out.println("subject: " + subject);
        // System.out.println("StatusName: " + deployRequest.StatusName);
        // System.out.println("wantedDate: " + deployRequest.wantedDate);
        //
        // }
        //
        //
        // } catch (RedmineException e) {
        // e.printStackTrace();
        // }

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
        String workRoll_06 = "김보영"; // 이벤트/판촉/프로모션
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


    public static void csvToXLSX(String csvFileAddress, String xlsxFileAddress) {

        BufferedReader br = null;
        FileReader fr = null;
        FileOutputStream out = null;
        Reader reader = null;
        InputStream is = null;

        XSSFWorkbook workBook = null;
        try {
            // String csvFileAddress = "test.csv"; //csv file address
            // String xlsxFileAddress = "test.xlsx"; //xlsx file address
            workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("sheet1");
            String currentLine = null;
            int RowNum = 0;

            is = new FileInputStream(csvFileAddress);
            reader = new InputStreamReader(is, "euc-kr");
            br = new BufferedReader(reader);

            List<String> parseList = new ArrayList<String>();

            while ((currentLine = br.readLine()) != null) {
                parseList.addAll(parse(currentLine));
                // parseList = parse(currentLine);
            }

            csvRedmineExcel = new RedmineExcel();

            for (int i = 13; i < parseList.size(); i++) {
                String parseVal = parseList.get(i);
                // parseVal = parseVal.replaceAll(",", "");

                if (i == parseList.size() - 1) {
                    String descriptionStr = "";
                    for (int j = 0; j < descriptionStrList.size(); j++) {
                        descriptionStr = descriptionStr + descriptionStrList.get(j) + "\n";
                    }
                    csvRedmineExcel.setDescription(descriptionStr);
                    newParseList.add(csvRedmineExcel);
                }

                if (parseCellCnt == 0) {

                    if (parseVal.length() == 5 && isParsableToInt(parseVal)) {
                        if (csvRedmineExcel.getLastDeployServer() != null) {
                            String descriptionStr = "";
                            for (int j = 0; j < descriptionStrList.size(); j++) {
                                descriptionStr = descriptionStr + descriptionStrList.get(j) + "\n";
                            }
                            csvRedmineExcel.setDescription(descriptionStr);
                            newParseList.add(csvRedmineExcel);

                        }
                        csvRedmineExcel.setItemId(Integer.parseInt(parseVal));
                        parseCellCnt++;

                    } else {
                        descriptionStrList.add(parseVal);
                        parseCellCnt = 100;
                    }
                } else if (parseCellCnt == 1) {
                    csvRedmineExcel.setDeployType(parseVal);
                    isSetBoWorkNo = false;
                    parseCellCnt++;
                } else if (parseCellCnt == 2) {
                    csvRedmineExcel.setStatus(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 3) {
                    csvRedmineExcel.setSubject(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 4) {
                    csvRedmineExcel.setManager(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 5) {
                    csvRedmineExcel.setPl(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 6) {
                    csvRedmineExcel.setWorkRoll(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 7) {
                    csvRedmineExcel.setDeploySystem(parseVal);
                    parseCellCnt++;
                } else if (parseCellCnt == 100) {

                    if (parseVal.length() == 5 && isParsableToInt(parseVal)) {

                        setItemIdCommon(parseVal);
                    } else {
                        descriptionStrList.add(parseVal);
                    }
                } else {

                    if (!isSetFileStr) {

                        String compParseVal = parseVal.replace("/", "");

                        if (parseVal.length() == 10 && isParsableToInt(compParseVal)) {
                            csvRedmineExcel.setDeployDate(parseVal);
                            parseCellCnt2++;

                        } else {



                            if (parseCellCnt2 > 0) {

                                setDeployFilesCommon(1);

                                if (parseCellCnt2 == 1) {
                                    csvRedmineExcel.setLastDeployServer(parseVal);
                                    parseCellCnt2++;
                                } else if (parseCellCnt2 == 2) {
                                    csvRedmineExcel.setBoWorkReqNo(Integer.parseInt(parseVal));
                                    parseCellCnt2++;
                                } else if (parseCellCnt2 == 3) {
                                    csvRedmineExcel.setDescription(parseVal);
                                    parseCellCnt2 = 0;
                                } // parseCellCnt2 if_else_end

                            } else {
                                if (parseVal.contains("서버")) {

                                    setDeployFilesCommon(0);

                                    String[] parseValArr = parseVal.split(",");

                                    csvRedmineExcel.setDeployDate(parseValArr[1]);
                                    csvRedmineExcel.setLastDeployServer(parseValArr[2]);
                                    if (parseValArr.length == 4) {
                                        csvRedmineExcel.setBoWorkReqNo(Integer.parseInt(parseValArr[3]));

                                        parseCellCnt2 = 0;
                                        isSetDescription = true;

                                        isSetFileStr = false;
                                        isSetBoWorkNo = false;
                                        isSetDescription = false;
                                        parseCellCnt = 0;
                                    } else {
                                        isSetBoWorkNo = true;
                                    }


                                } else {
                                    fileStrList.add(parseVal);
                                }
                            }
                        }



                    } else {
                        if (parseCellCnt2 > 0) {

                            if (parseVal.length() == 5 && isParsableToInt(parseVal)) {

                                if (csvRedmineExcel.getSubject().contains(parseVal)) {
                                    csvRedmineExcel.setBoWorkReqNo(Integer.parseInt(parseVal));
                                    parseCellCnt2++;
                                } else {
                                    setItemIdCommon(parseVal);
                                }
                            } else {
                                parseCellCnt = 100;
                                descriptionStrList.add(parseVal);
                            }

                        } else {
                            parseCellCnt = 100;
                            descriptionStrList.add(parseVal);
                        }

                    }
                }// parseCellCnt check if_end



            }// for end


            // RowNum++;
            XSSFRow currentRow = sheet.createRow(RowNum);
            currentRow.createCell(0).setCellValue("#");
            currentRow.createCell(1).setCellValue("유형");
            currentRow.createCell(2).setCellValue("상태");
            currentRow.createCell(3).setCellValue("제목");
            currentRow.createCell(4).setCellValue("담당자");
            currentRow.createCell(5).setCellValue("PL");
            currentRow.createCell(6).setCellValue("업무구분");
            currentRow.createCell(7).setCellValue("대상시스템");
            currentRow.createCell(8).setCellValue("배포파일");
            currentRow.createCell(9).setCellValue("배포희망");
            currentRow.createCell(10).setCellValue("최종배포서버");
            currentRow.createCell(11).setCellValue("BO업무요청번호");
            currentRow.createCell(12).setCellValue("설명");

            System.out.println("newParseList.size() :::::" + newParseList.size());

            for (int j = 0; j < newParseList.size(); j++) {
                RedmineExcel re = newParseList.get(j);

                if (re.getLastDeployServer().equals("운영서버")) {

                    RowNum++;
                    currentRow = sheet.createRow(RowNum);
                    for (int i = 0; i < 13; i++) {
                        if (i == 0) {
                            currentRow.createCell(i).setCellValue(re.getItemId());
                        } else if (i == 1) {
                            currentRow.createCell(i).setCellValue(re.getDeployType());
                        } else if (i == 2) {
                            currentRow.createCell(i).setCellValue(re.getStatus());
                        } else if (i == 3) {
                            currentRow.createCell(i).setCellValue(re.getSubject());
                        } else if (i == 4) {
                            currentRow.createCell(i).setCellValue(re.getManager());
                        } else if (i == 5) {
                            currentRow.createCell(i).setCellValue(re.getPl());
                        } else if (i == 6) {
                            currentRow.createCell(i).setCellValue(re.getWorkRoll());
                        } else if (i == 7) {
                            currentRow.createCell(i).setCellValue(re.getDeploySystem());
                        } else if (i == 8) {
                            currentRow.createCell(i).setCellValue(re.getDeployFiles());
                        } else if (i == 9) {
                            currentRow.createCell(i).setCellValue(re.getDeployDate());
                        } else if (i == 10) {
                            currentRow.createCell(i).setCellValue(re.getLastDeployServer());
                        } else if (i == 11) {
                            currentRow.createCell(i).setCellValue(re.getBoWorkReqNo());
                        } else if (i == 12) {
                            currentRow.createCell(i).setCellValue(re.getDescription());
                        }

                    }
                }
            }



            out = new FileOutputStream(xlsxFileAddress);
            workBook.write(out);
            out.close();
            System.out.println("Done");

        } catch (Exception ex) {
            ex.printStackTrace();
            // System.out.println(ex.getMessage() + "Exception in try");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (workBook != null) {
                    workBook.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static List<String> parse(final String line) {

        List list = new ArrayList();
        Matcher m = csvRE.matcher(line);
        // For each field
        while (m.find()) {
            String match = m.group();
            if (match == null) {
                break;
            }
            if (match.endsWith(",")) { // trim trailing ,
                match = match.substring(0, match.length() - 1);
            }
            if (match.startsWith("\"")) { // assume also ends with
                match = match.substring(1, match.length() - 1);
            }
            if (match.length() == 0) {
                match = null;
            } else {
                list.add(match);
            }

        }
        return list;
    }


    public static void setItemIdCommon(String parseVal) {

        String descriptionStr = "";
        for (int j = 0; j < descriptionStrList.size(); j++) {
            descriptionStr = descriptionStr + descriptionStrList.get(j) + "\n";
        }

        csvRedmineExcel.setDescription(descriptionStr);
        descriptionStrList = new ArrayList<String>();

        parseCellCnt2 = 0;
        isSetDescription = true;

        newParseList.add(csvRedmineExcel);
        csvRedmineExcel = new RedmineExcel();
        csvRedmineExcel.setItemId(Integer.parseInt(parseVal));

        isSetFileStr = false;
        isSetBoWorkNo = false;
        isSetDescription = false;
        parseCellCnt = 1;

    }


    public static void setDeployFilesCommon(int i) {

        String filesVal = "";
        for (int j = 0; j < fileStrList.size(); j++) {
            filesVal = filesVal + fileStrList.get(j) + "\n";
        }
        csvRedmineExcel.setDeployFiles(filesVal + "\n");
        if (i == 0) {
            parseCellCnt2 = 0;
        }
        isSetFileStr = true;

        fileStrList = new ArrayList<String>();
    }



}
