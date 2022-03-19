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

import redmine.DeployRequest;
import redmine.RedmineClient;
import utils.DateUtils;
import utils.HMDrmUtil;
import utils.HttpDownloader;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;

public class ApprCheckDeployExpected {

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
    static String createDeployPath;

    static List<RedmineExcel> unapprovedList = new ArrayList<RedmineExcel>(); // 배포미승인
    static List<RedmineExcel> noChangeStatusList = new ArrayList<RedmineExcel>(); // 운영배포 미 승인

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

        // List<RedmineExcel> redmineExcelList = getRedmineIssueForApprovalCheck(REDMINE_ISSUE_FILE);
        List<RedmineExcel> redmineExcelList = getRedmineIssueHTTPUrl();
        List<String> deplyDateList = new ArrayList<String>();
        for (int i = 0; i < redmineExcelList.size(); i++) {
            RedmineExcel value = redmineExcelList.get(i);
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
            deplyDateList.add(value.getDeployDate());
        }

        String resultDate = DateUtils.getNextDate(1, "yyyyMMdd");
        str_yyyy = resultDate.substring(0, 4);
        str_MM = resultDate.substring(4, 6);
        str_dd = resultDate.substring(6, 8);
        korDayOfTheWeek = DateUtils.getDayOfTheWeekString(1);

        dayMsgStr = str_MM + "/" + str_dd + " (" + korDayOfTheWeek + ")";

        createDeployPath = str_yyyy + FILE_SEPARATOR + str_MM + FILE_SEPARATOR + str_dd;

        String orgFilePath = DEPLOY_PATH + createDeployPath + FILE_SEPARATOR + "배포승인체크_" + resultDate + ".xlsx";
        // createDeployPath = createDeployPath + "배포목록\\\\승인체크";
        System.out.println("createDeployPath::::" + createDeployPath);
        List<RedmineExcel> aprovalOrgExcelList = getRedmineIssueForApprovalCheck(orgFilePath);

        StringBuffer preChekcMsg = new StringBuffer();


        List<RedmineExcel> deployCancelList = new ArrayList<RedmineExcel>();
        List<RedmineExcel> deplyNewList = new ArrayList<RedmineExcel>();

        List<RedmineExcel> deployCheckList = new ArrayList<>();
        List<String> deployCheckItemList = new ArrayList<>();
        List<String> aprovalOrgExcelItemList = new ArrayList<>();
        // deployCheckList = new ArrayList<>(new HashSet<RedmineExcel>(deployCheckList));

        deployCheckList.addAll(redmineExcelList);

        for (int i = 0; i < redmineExcelList.size(); i++) {
            RedmineExcel redmineExcel = redmineExcelList.get(i);
            deployCheckItemList.add(Integer.toString(redmineExcel.getItemId()));
        }

        for (int i = 0; i < aprovalOrgExcelList.size(); i++) {
            RedmineExcel redmineExcel = aprovalOrgExcelList.get(i);
            aprovalOrgExcelItemList.add(Integer.toString(redmineExcel.getItemId()));
        }

        // New 목록
        for (int i = 0; i < redmineExcelList.size(); i++) {
            RedmineExcel redmineExcel = redmineExcelList.get(i);
            if (!aprovalOrgExcelItemList.contains(Integer.toString(redmineExcel.getItemId()))) {
                deployCheckList.add(redmineExcel);
                deplyNewList.add(redmineExcel);
            }
        }

        // 누락 목록
        for (int i = 0; i < aprovalOrgExcelList.size(); i++) {
            RedmineExcel redmineExcel = aprovalOrgExcelList.get(i);
            if (!deployCheckItemList.contains(Integer.toString(redmineExcel.getItemId()))) {
                deployCancelList.add(redmineExcel);
            }
        }


        System.out.println("redmineExcelList.size() ::" + redmineExcelList.size());
        System.out.println("deployCheckList.size() ::" + deployCheckList.size());
        System.out.println("deplyNewList.size() ::" + deplyNewList.size());


        preChekcMsg = new StringBuffer();
        preChekcMsg.append("\n");
        preChekcMsg.append("\n");
        preChekcMsg.append("아래 일감은 " + dayMsgStr + " 배포점검회의 목록에서 제외된 일감입니다. \n");
        preChekcMsg.append("배포취소가 맞는지 확인하시고 확인쪽지 보내 주시기 바랍니다.\n");
        preChekcMsg.append("\n");
        preChekcMsg.append(" - 점검목록 중 추가 또는 제외될 일감이 있을 경우 반드시 별도 쪽지로 알려 주시기 바랍니다. \n");
        preChekcMsg.append(" ----------------------------------------------------------------------------------------\n");

        for (int i = 0; i < deployCancelList.size(); i++) {
            RedmineExcel value = deployCancelList.get(i);
            preChekcMsg.append("#" + value.getItemId() + " | " + value.getSubject() + "  " + " | " + value.getManager() + " \n");
        }
        System.out.println(preChekcMsg.toString());
        System.out.println("");
        System.out.println("#########################################################");
        System.out.println("");


        preChekcMsg = new StringBuffer();
        preChekcMsg.append("아래 일감은 " + dayMsgStr + " 배포점검회의 목록에 추가된 일감입니다. \n");
        preChekcMsg.append(dayMsgStr + " 배포일감이 맞는지 확인하시고 확인쪽지 보내 주시기 바랍니다. \n");
        preChekcMsg.append("\n");
        preChekcMsg.append(" - 점검목록 중 추가 또는 제외될 일감이 있을 경우 반드시 별도 쪽지로 알려 주시기 바랍니다. \n");
        preChekcMsg.append(" ----------------------------------------------------------------------------------------\n");

        for (int i = 0; i < deplyNewList.size(); i++) {
            RedmineExcel value = deplyNewList.get(i);
            preChekcMsg.append("#" + value.getItemId() + " | " + value.getSubject() + "  " + " | " + value.getManager() + " \n");
        }
        System.out.println(preChekcMsg.toString());
        System.out.println("");
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


    public static List<RedmineExcel> getRedmineIssueHTTPUrl() {

        String searchDate = DateUtils.getNextDate(1, "yyyy-MM-dd");

        List<RedmineExcel> retList = new ArrayList<RedmineExcel>();
        String csvFileAddress = DEPLOY_TEMP_DIR + "down_" + searchDate + ".csv";
        String xlsxFileAddress = DEPLOY_TEMP_DIR + "down_" + searchDate + ".xlsx";

        String redmineApprUrl =
                "http://10.154.17.206:3000/projects/hms_/issues.csv?c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=subject&c%5B%5D=assigned_to&c%5B%5D=cf_5&c%5B%5D=cf_12&c%5B%5D=cf_10&c%5B%5D=cf_3&c%5B%5D=cf_8&c%5B%5D=cf_9&c%5B%5D=cf_58&f%5B%5D=tracker_id&f%5B%5D=cf_8&f%5B%5D=status_id&f%5B%5D=&group_by=&op%5Bcf_8%5D=%3D&op%5Bstatus_id%5D=%3D&op%5Btracker_id%5D=%3D&set_filter=1&utf8=%E2%9C%93&v%5Bcf_8%5D%5B%5D="
                        + searchDate
                        + "&v%5Bstatus_id%5D%5B%5D=1&v%5Bstatus_id%5D%5B%5D=19&v%5Bstatus_id%5D%5B%5D=20&v%5Bstatus_id%5D%5B%5D=21&v%5Bstatus_id%5D%5B%5D=22&v%5Btracker_id%5D%5B%5D=4&v%5Btracker_id%5D%5B%5D=17&description=1";
        HttpDownloader.download(redmineApprUrl, csvFileAddress);
        System.out.println("redmineApprUrl ::" + redmineApprUrl);
        System.out.println("HttpDownloader.download :: success");
        System.out.println(" csvFileAddress Down Url :: " + csvFileAddress);

        csvToXLSX(csvFileAddress, xlsxFileAddress);

        retList = getRedmineIssueForApprovalCheck(xlsxFileAddress);

        return retList;

    }


    public static List<RedmineExcel> getRedmineIssueForApprovalCheck(String readExcelFile) {

        XSSFWorkbook workbook = null;
        XSSFSheet sheet;
        File orgFile = null;
        String orgFilePath = "";

        List<RedmineExcel> retList = new ArrayList<RedmineExcel>();

        unapprovedList = new ArrayList<RedmineExcel>();
        noChangeStatusList = new ArrayList<RedmineExcel>();

        // intWorkRoll_01_count = 0; // 전시
        // intWorkRoll_02_count = 0; // 상품
        // intWorkRoll_03_count = 0; // 주문/클레임
        // intWorkRoll_04_count = 0; // 모바일
        // intWorkRoll_05_count = 0; // 회원
        // intWorkRoll_06_count = 0; // 이벤트
        // intWorkRoll_07_count = 0; // 옴니앱
        // intWorkRoll_08_count = 0; // 기타

        try {

            orgFile = new File(readExcelFile);

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

                boolean isUnApporvaled = false;
                boolean isNoChangeStatus = false;

                XSSFRow row = sheet.getRow(rowindex);
                if (row != null) {
                    // 셀의 수
                    int cells = row.getPhysicalNumberOfCells();
                    for (columnindex = 0; columnindex <= cells; columnindex++) {

                        isUnApporvaled = false;

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

                            // 배포요청(승인전) 리스트에 추가
                            if (value.equals("배포요청(승인전)")) {
                                unapprovedList.add(redmineExcel);
                                isUnApporvaled = true;
                            }

                        } else if (columnindex == 2) {
                            redmineExcel.setStatus(value);
                            // 운영배포승인 상태변경 안한 리스트에 추가
                            if (!value.equals("운영배포승인")) {
                                isNoChangeStatus = true;

                                if (redmineExcel.getDeployType().equals("배포요청(승인전)")) {
                                    isNoChangeStatus = false;
                                }
                            }

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

                    // List<String> deploySystemList = new ArrayList<String>();
                    //
                    // System.out.println("redmineExcel.getDeployFiles() ::" + redmineExcel.getDeployFiles());
                    //
                    // if (redmineExcel.getDeployFiles().contains("/03_Front") || redmineExcel.getDeployFiles().contains("/app/domain/fo/"))
                    // {
                    // deploySystemList.add("FO/LPS");
                    // fo_count++;
                    // } else if (redmineExcel.getDeployFiles().contains("/04_FrontMobile")
                    // || redmineExcel.getDeployFiles().contains("/app/domain/mo/")) {
                    // deploySystemList.add("MO/mLPS");
                    // mo_count++;
                    // } else if (redmineExcel.getDeployFiles().contains("/05_BO")
                    // || redmineExcel.getDeployFiles().contains("/app/domain/bo/")) {
                    // deploySystemList.add("BO/CC/PO");
                    // bo_count++;
                    // } else if (redmineExcel.getDeployFiles().contains("/09_Tablet")
                    // || redmineExcel.getDeployFiles().contains("/app/domain/tc/")) {
                    // deploySystemList.add("옴니앱");
                    // tc_count++;
                    // } else if (redmineExcel.getDeployFiles().contains("/06_Batch")) {
                    // deploySystemList.add("BATCH");
                    // batch_count++;
                    // } else {
                    // deploySystemList.add("기타");
                    // etc_count++;
                    // }
                    //
                    // for (int i = 0; i < deploySystemList.size(); i++) {
                    // String deploySystem = redmineExcel.getDeploySystem();
                    // if (!deploySystem.contains(deploySystemList.get(i))) {
                    // deploySystem = deploySystem + "," + deploySystemList.get(i);
                    // redmineExcel.setDeploySystem(deploySystem);
                    // }
                    // }


                    if (redmineExcel.getDeployType() != null) {

                        retList.add(redmineExcel);

                        if (isUnApporvaled) {
                            unapprovedList.add(redmineExcel);
                        }

                        if (isNoChangeStatus) {
                            noChangeStatusList.add(redmineExcel);
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
