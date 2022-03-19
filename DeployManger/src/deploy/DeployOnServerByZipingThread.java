package deploy;


import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import utils.DateUtils;
import utils.HttpUrlConnectionCommon;
import utils.SFTPService;

@Slf4j
@Data
public class DeployOnServerByZipingThread implements Runnable {

    static final String ZIPPING_COMMON_SHELL = "sh /NAS-EC_PRD/files/was_source/run_zipping.sh ";
    static final String EXPANDING_COMMON_SHELL = "sh /NAS-EC_PRD/files/was_source/run_expanding.sh ";
    static final String DEPLOY_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/temp/";
    static final String DEPLOY_TEMP_BAK_DIR = "/data/home/hisis/ec/tools/deploy/temp_bak/";
    static final String DEFAULT_DIR = "/data/webapps/archive/E-HIMART/";
    static final String JEUS_HOME = "/data/webapps/JEUS";
    static final String JEUS_HOME_MPP = "/data/webapps/JEUS8";

    static final String JEUS_JSP_WORK_FO_PATH_01 = "/data/webapps/JEUS/domains/fo_domain/servers/";
    static final String JEUS_JSP_WORK_MO_PATH_01 = "/data/webapps/JEUS/domains/mo_domain/servers/";
    static final String JEUS_JSP_WORK_LPS_PATH_01 = "/data/webapps/JEUS/domains/lps_domain/servers/";
    static final String JEUS_JSP_WORK_MLPS_PATH_01 = "/data/webapps/JEUS/domains/mlps_domain/servers/";
    static final String JEUS_JSP_WORK_PO_PATH_01 = "/data/webapps/JEUS/domains/po_domain/servers/";
    static final String JEUS_JSP_WORK_BO_PATH_01 = "/data/webapps/JEUS/domains/bo_domain/servers/";
    static final String JEUS_JSP_WORK_MPP_PATH_01 = "/data/webapps/JEUS8/domains/MPP_domain/servers/";

    static final String JEUS_JSP_WORK_FO_PATH_02 = "/.workspace/deployed/_generated_/FO/__jspwork/";
    static final String JEUS_JSP_WORK_MO_PATH_02 = "/.workspace/deployed/_generated_/MO/__jspwork/";
    static final String JEUS_JSP_WORK_LPS_PATH_02 = "/.workspace/deployed/_generated_/LPS/__jspwork/";
    static final String JEUS_JSP_WORK_MLPS_PATH_02 = "/.workspace/deployed/_generated_/mLPS/__jspwork/";
    static final String JEUS_JSP_WORK_PO_PATH_02 = "/.workspace/deployed/_generated_/PO/__jspwork/";
    static final String JEUS_JSP_WORK_BO_PATH_02 = "/.workspace/deployed/_generated_/BO/__jspwork/";
    static final String JEUS_JSP_WORK_MPP_PATH_02 = "/.workspace/deployed/_generated_/MPP/__jspwork/";

    static final String JEUS_JSP_WORK_NM = "jeus_jspwork";

    static final String FO_HIMART_URL = "curl -X GET http://www.e-himart.co.kr/app/display/showDisplayShop?originReferrer=himartindex/";
    static final String LPS_HIMART_URL =
            "curl -X GET https://secure.e-himart.co.kr/app/login/login?returnUrl=http%3A%2F%2Fwww.e-himart.co.kr%2Fapp%2Fdisplay%2FshowDisplayShop%3ForiginReferrer%3Dhimartindex";
    static final String MO_HIMART_URL = "curl -X GET http://m.e-himart.co.kr/app/display/main/";
    static final String MLPS_HIMART_URL =
            "curl -X GET https://msecure.e-himart.co.kr/app/login/login?returnUrl=http://m.e-himart.co.kr/app/display/main";

    static boolean IS_DEPLOY_SUCCESS = true;
    Properties props = null;
    String deploySystemFtp = null;
    boolean webServerRecover = false;

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};

    static String[] WAS_SERVER_ARRAY = new String[] {};


    @Override
    public void run() {

        System.out.println("==============================================");
        System.out.println("DeployOnServerByZipingThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("deploySystemFtp =" + deploySystemFtp);
        System.out.println("==============================================");

        if (webServerRecover) {
            sourceUploadModuleForImage(props, deploySystemFtp);
        } else {
            sourceUploadModule(props, deploySystemFtp);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (!IS_DEPLOY_SUCCESS) {
            throw new RuntimeException("Build Fail !!!!!!!!!!!!!!!!!!!!!!!");
        }

        System.out.println("==============================================");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("deploySystemFtp =" + deploySystemFtp);
        System.out.println("DeployOnServerByZipingThread END");
        System.out.println("==============================================");

    }


    public DeployOnServerByZipingThread(Properties props, String deploySystemFtp) {

        this.props = props;
        this.deploySystemFtp = deploySystemFtp;
        propServerSet(props);
    }


    public DeployOnServerByZipingThread(Properties props, String deploySystemFtp, boolean webServerRecover) {

        this.props = props;
        this.deploySystemFtp = deploySystemFtp;
        this.webServerRecover = webServerRecover;
        propServerSet(props);
    }


    /**
     * 소스파일다운
     */
    public static void getWasSourceDownByBaseSystem(Properties props, String basetSystem, boolean bacupYn) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";
        String webDir = uploadServerDir + "/WEB/";

        String deployTempDir = DEPLOY_TEMP_DIR;
        if (bacupYn) {
            deployTempDir = DEPLOY_TEMP_BAK_DIR;
        }

        String[] setWasArr = null;
        String[] setSpareWasArr = null;
        SFTPService ftp = null;
        SFTPService web_ftp = null;

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        int deployWebPort = 22;
        String deployWebIp = "";
        String deployWebId = "";
        String deployWebPw = "";
        String deployWebPath = "";

        String zipping_shell_was = "";
        String zipping_shell_web = "";

        String zipping_shell_was_server = "";
        String zipping_shell_web_server = "";


        String jspwork_zipping_shell = "";
        String jspwork_zipping_dir = "";

        if (basetSystem.contains("FO")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO";

        } else if (basetSystem.contains("MO")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO";

        } else if (basetSystem.contains("PO")) {
            zipping_shell_was_server = "PO";
            zipping_shell_web_server = "PO";

        } else if (basetSystem.contains("PO")) {
            zipping_shell_was_server = "PO";
            zipping_shell_web_server = "PO";

        } else if (basetSystem.contains("FO_IMAGE")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO_IMAGE";

            wasDir = uploadServerDir + "/WEB/";

        } else if (basetSystem.contains("MO_IMAGE")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO_IMAGE";

            wasDir = uploadServerDir + "/WEB/";

        } else if (basetSystem.contains("BO")) {
            // PO2, BO1, BO2

            zipping_shell_was_server = "PO";
            zipping_shell_web_server = "PO";

        } else if (basetSystem.contains("CC")) {
            zipping_shell_was_server = "CC";
            zipping_shell_web_server = "CC";

        } else if (basetSystem.contains("TO")) {
            zipping_shell_was_server = "TO";
            zipping_shell_web_server = "TO";

        }

        if (basetSystem.contains("MPP")) {
            zipping_shell_was_server = "MPP";
            zipping_shell_web_server = "MPP";
        }



        deployFtpIp = props.getProperty(basetSystem + ".ftp.ip");
        deployFtpId = props.getProperty(basetSystem + ".ftp.id");
        deployFtpPw = props.getProperty(basetSystem + ".ftp.pw");
        deployFtpPath = props.getProperty(basetSystem + ".ftp.path");

        deployWebIp = props.getProperty(basetSystem + ".web.ip");
        deployWebId = props.getProperty(basetSystem + ".web.id");
        deployWebPw = props.getProperty(basetSystem + ".web.pw");
        deployWebPath = props.getProperty(basetSystem + ".web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        zipping_shell_was = ZIPPING_COMMON_SHELL + " " + wasDir + " " + zipping_shell_was_server + "_WAS.tar " + zipping_shell_was_server;
        zipping_shell_web = ZIPPING_COMMON_SHELL + " " + webDir + " " + zipping_shell_web_server + "_WEB.tar " + zipping_shell_web_server;

        if (basetSystem.contains("IMAGE")) {
            zipping_shell_was =
                    ZIPPING_COMMON_SHELL + " " + wasDir + " " + zipping_shell_web_server + "_WEB.tar " + zipping_shell_was_server;
        }

        System.out.println("zipping_shell_was ::" + zipping_shell_was);
        System.out.println("zipping_shell_web ::" + zipping_shell_web);
        // was_소스 tar생성
        ftp.sshCommandExecByNotLogger(zipping_shell_was);

        // LOCAL_DEFAULT_DIR로 다운로드
        if (basetSystem.contains("IMAGE")) {
            ftp.download(zipping_shell_web_server + "_WEB.tar", deployTempDir, wasDir);
        } else {
            ftp.download(zipping_shell_was_server + "_WAS.tar", deployTempDir, wasDir);

            web_ftp = new SFTPService(deployWebIp, deployWebPort, deployWebId, deployWebPw);
            // web_소스 tar생성
            web_ftp.sshCommandExecByNotLogger(zipping_shell_web);

            web_ftp.download(zipping_shell_was_server + "_WEB.tar", deployTempDir, webDir);
        }


        if (basetSystem.contains("FO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_FO_PATH_01 + basetSystem + JEUS_JSP_WORK_FO_PATH_02;
        } else if (basetSystem.contains("MO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_MO_PATH_01 + basetSystem + JEUS_JSP_WORK_MO_PATH_02;
        } else if (basetSystem.contains("PO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_PO_PATH_01 + basetSystem + JEUS_JSP_WORK_PO_PATH_02;
        }

        if (basetSystem.contains("MPP")) {
            String jeusStartNm = props.getProperty(basetSystem + ".jeusStartNm");
            jspwork_zipping_dir = JEUS_JSP_WORK_MPP_PATH_01 + jeusStartNm + JEUS_JSP_WORK_MPP_PATH_02;
        }

        if (basetSystem.contains("IMAGE")) {
            jspwork_zipping_dir = null;
        }


        if (StringUtils.isNotEmpty(jspwork_zipping_dir)) {
            jspwork_zipping_shell =
                    ZIPPING_COMMON_SHELL + " " + jspwork_zipping_dir + " " + zipping_shell_was_server + "_jspwork.tar " + JEUS_JSP_WORK_NM;

            System.out.println("jspwork_zipping_shell ::" + jspwork_zipping_shell);
            // was_소스 tar생성
            ftp.sshCommandExecByNotLogger(jspwork_zipping_shell);

            ftp.download(zipping_shell_was_server + "_jspwork.tar", deployTempDir, jspwork_zipping_dir);
        }

    }



    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModule(Properties props, String deploySystemFtp) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";
        String webDir = uploadServerDir + "/WEB/";

        SFTPService ftp = null;
        SFTPService web_ftp = null;

        String expanding_shell = "";

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        int deployWebPort = 22;
        String deployWebIp = "";
        String deployWebId = "";
        String deployWebPw = "";
        String deployWebPath = "";

        String deploySystem = "";
        String deploySystemDir = "";
        String zippingSystem = "";

        deploySystem = removeRex("[0-9]", deploySystemFtp);

        deployFtpIp = props.getProperty(deploySystemFtp + ".ftp.ip");
        deployFtpId = props.getProperty(deploySystemFtp + ".ftp.id");
        deployFtpPw = props.getProperty(deploySystemFtp + ".ftp.pw");
        deployFtpPath = props.getProperty(deploySystemFtp + ".ftp.path");

        deployWebIp = props.getProperty(deploySystemFtp + ".web.ip");
        deployWebId = props.getProperty(deploySystemFtp + ".web.id");
        deployWebPw = props.getProperty(deploySystemFtp + ".web.pw");
        deployWebPath = props.getProperty(deploySystemFtp + ".web.path");

        deploySystemDir = deploySystem;

        if (deploySystem.equals("LPS")) {
            zippingSystem = "FO";
        } else if (deploySystem.equals("MLPS")) {
            deploySystemDir = "mLPS";
            zippingSystem = "MO";
        } else if (deploySystem.equals("BO")) {
            zippingSystem = "PO";
        } else if (deploySystem.equals("FO_IMAGE")) {
            zippingSystem = "FO";
        } else if (deploySystem.equals("MO_IMAGE")) {
            zippingSystem = "MO";
        } else {
            zippingSystem = deploySystem;
        }

        /*****************************************************************
         * WAS관련
         *****************************************************************/
        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown", ftp);

        // WAS 소스 업로드
        ftp.upload(DEPLOY_TEMP_DIR + zippingSystem + "_WAS.tar", wasDir);

        // 업로드파일 WAS파일로 교체
        String sshCommand = "rm -r " + wasDir + deploySystemDir;

        ftp.sshCommandExecByNotLogger(sshCommand);
        System.out.println("#");
        System.out.println("#sshCommand  WAS 기존소스 삭제 ::" + sshCommand);
        System.out.println("##############################################################################");
        //
        // try {
        // Thread.sleep(2 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // sshCommand = "tar xvf " + wasDir + targetSystem + "_WAS.tar" + " -C " + wasDir;
        expanding_shell = EXPANDING_COMMON_SHELL + " " + wasDir + " " + zippingSystem + "_WAS.tar";

        System.out.println("#sshCommand tar xvf  ::" + expanding_shell);
        ftp.sshCommandExecByNotLogger(expanding_shell);

        // try {
        // // System.out.println("sleep start::");
        // Thread.sleep(12 * 1000);
        // // System.out.println("sleep end::");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        if (deploySystem.equals("MLPS") || deploySystem.equals("LPS") || deploySystem.equals("BO")) {
            sshCommand = "cp -rp " + wasDir + zippingSystem + " " + wasDir + deploySystemDir;
            System.out.println("#sshCommand MLPS, LPS ::" + sshCommand);
            ftp.sshCommandExecByNotLogger(sshCommand);
            //
            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(2 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            sshCommand = "rm -r " + wasDir + zippingSystem;
            System.out.println("#sshCommand rm ::" + sshCommand);
            ftp.sshCommandExecByNotLogger(sshCommand);
        }


        String jspwork_dir = "";

        if (deploySystemFtp.equals("LPS1") || deploySystemFtp.equals("LPS2") || deploySystemFtp.equals("LPS3")) {
            jspwork_dir = JEUS_JSP_WORK_LPS_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_LPS_PATH_02;
        }

        if (deploySystemFtp.contains("FO")) {
            jspwork_dir = JEUS_JSP_WORK_FO_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_FO_PATH_02;
        } else if (deploySystemFtp.contains("MO")) {
            jspwork_dir = JEUS_JSP_WORK_MO_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_MO_PATH_02;
        } else if (deploySystemFtp.contains("MLPS")) {
            jspwork_dir = JEUS_JSP_WORK_MLPS_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_MLPS_PATH_02;
        } else if (deploySystemFtp.contains("PO") || deploySystemFtp.contains("BO")) {
            jspwork_dir = JEUS_JSP_WORK_PO_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_PO_PATH_02;
        }

        if (StringUtils.isNotEmpty(jspwork_dir)) {

            // jspwork파일 업로드
            ftp.upload(DEPLOY_TEMP_DIR + zippingSystem + "_jspwork.tar", jspwork_dir);

            // 업로드파일 WAS파일로 교체
            sshCommand = "rm -r " + jspwork_dir + JEUS_JSP_WORK_NM;

            ftp.sshCommandExecByNotLogger(sshCommand);
            System.out.println("#");
            System.out.println("#sshCommand  jspwork 기존소스 삭제 ::" + sshCommand);
            System.out.println("##############################################################################");
            //
            // try {
            // Thread.sleep(2 * 1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            // sshCommand = "tar xvf " + jspwork_dir + targetSystem + "_jspwork.tar" + " -C " + jspwork_dir;
            expanding_shell = EXPANDING_COMMON_SHELL + " " + jspwork_dir + " " + zippingSystem + "_jspwork.tar";

            System.out.println("#sshCommand jspwork_dir tar xvf  ::" + expanding_shell);
            ftp.sshCommandExecByNotLogger(expanding_shell);
            //
            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(2 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        }

        /*****************************************************************
         * 웹서버관련
         *****************************************************************/
        web_ftp = new SFTPService(deployWebIp, deployWebPort, deployWebId, deployWebPw);
        // 소스백업
        webSourceBakupCommandExec(props, deploySystem, web_ftp);


        // WEB 소스 업로드
        web_ftp.upload(DEPLOY_TEMP_DIR + zippingSystem + "_WEB.tar", webDir);

        // 기존소스 삭제
        sshCommand = "rm -r " + webDir + deploySystemDir;
        web_ftp.sshCommandExecByNotLogger(sshCommand);
        System.out.println("#");
        System.out.println("#sshCommand WEB 기존소스 삭제 ::" + sshCommand);
        System.out.println("##############################################################################");
        //
        // try {
        // Thread.sleep(2 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // 압축해제
        // sshCommand = "tar xvf " + webDir + targetSystem + "_WEB.tar" + " -C " + webDir;
        expanding_shell = EXPANDING_COMMON_SHELL + " " + webDir + " " + zippingSystem + "_WEB.tar";

        System.out.println("#sshCommand WEB  압축해제 ::" + expanding_shell);
        web_ftp.sshCommandExecByNotLogger(expanding_shell);

        if (deploySystem.equals("MLPS") || deploySystem.equals("LPS") || deploySystem.equals("BO")) {
            sshCommand = "cp -rp " + webDir + zippingSystem + " " + webDir + deploySystemDir;

            System.out.println("#sshCommand MLPS, LPS ::" + sshCommand);

            web_ftp.sshCommandExecByNotLogger(sshCommand);
            //
            // try {
            // Thread.sleep(2 * 1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            sshCommand = "rm -r " + webDir + zippingSystem;
            System.out.println("#sshCommand rm ::" + sshCommand);
            web_ftp.sshCommandExecByNotLogger(sshCommand);
        }

        // WAS서버 부팅
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot", ftp);



    }


    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModuleForImage(Properties props, String deploySystemFtp) {

        String uploadServerDir = props.getProperty("defaultDir");

        String webDir = uploadServerDir + "/WEB/";

        SFTPService web_ftp = null;

        String expanding_shell = "";

        int deployWebPort = 22;
        String deployWebIp = "";
        String deployWebId = "";
        String deployWebPw = "";
        String deployWebPath = "";

        String deploySystem = "";
        String deploySystemDir = "";
        String zippingSystem = "";

        deploySystem = removeRex("[0-9]", deploySystemFtp);

        deployWebIp = props.getProperty(deploySystemFtp + ".web.ip");
        deployWebId = props.getProperty(deploySystemFtp + ".web.id");
        deployWebPw = props.getProperty(deploySystemFtp + ".web.pw");
        deployWebPath = props.getProperty(deploySystemFtp + ".web.path");

        deploySystemDir = deploySystem;

        if (deploySystem.equals("LPS")) {
            zippingSystem = "FO";
        } else if (deploySystem.equals("MLPS")) {
            deploySystemDir = "mLPS";
            zippingSystem = "MO";
        } else if (deploySystem.equals("BO")) {
            zippingSystem = "PO";
        } else if (deploySystem.equals("FO_IMAGE")) {
            zippingSystem = "FO_IMAGE";
            deploySystemDir = "FO";
        } else if (deploySystem.equals("MO_IMAGE")) {
            zippingSystem = "MO_IMAGE";
            deploySystemDir = "MO";
        } else {
            zippingSystem = deploySystem;
        }



        /*****************************************************************
         * 웹서버관련
         *****************************************************************/

        // 웹서버 다운
        webServerShutDownBootCommon(props, deploySystem, deploySystemFtp, "wsdown", "real");

        web_ftp = new SFTPService(deployWebIp, deployWebPort, deployWebId, deployWebPw);
        // 소스백업
        webSourceBakupCommandExec(props, deploySystem, web_ftp);


        // WEB 소스 업로드
        web_ftp.upload(DEPLOY_TEMP_DIR + zippingSystem + "_WEB.tar", webDir);

        // 기존소스 삭제
        String sshCommand = "rm -r " + webDir + deploySystemDir;
        web_ftp.sshCommandExecByNotLogger(sshCommand);
        System.out.println("#");
        System.out.println("#sshCommand WEB 기존소스 삭제 ::" + sshCommand);
        System.out.println("##############################################################################");
        //
        // try {
        // Thread.sleep(2 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // 압축해제
        // sshCommand = "tar xvf " + webDir + targetSystem + "_WEB.tar" + " -C " + webDir;
        expanding_shell = EXPANDING_COMMON_SHELL + " " + webDir + " " + zippingSystem + "_WEB.tar";

        System.out.println("#sshCommand WEB  압축해제 ::" + expanding_shell);
        web_ftp.sshCommandExecByNotLogger(expanding_shell);

        if (deploySystem.equals("MLPS") || deploySystem.equals("LPS") || deploySystem.equals("BO")) {
            sshCommand = "cp -rp " + webDir + zippingSystem + " " + webDir + deploySystemDir;

            System.out.println("#sshCommand MLPS, LPS ::" + sshCommand);

            web_ftp.sshCommandExecByNotLogger(sshCommand);
            //
            // try {
            // Thread.sleep(2 * 1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            sshCommand = "rm -r " + webDir + zippingSystem;
            System.out.println("#sshCommand rm ::" + sshCommand);
            web_ftp.sshCommandExecByNotLogger(sshCommand);
        }

        // 웹서버 BOOT
        webServerShutDownBootCommon(props, deploySystem, deploySystemFtp, "wsboot", "real");

    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            SFTPService ftp) {

        jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, rebootFlag, ftp, "real");
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            SFTPService ftp, String targetServer) {

        String rebootFtpIp = "";
        int rebootFtpPort = 22;
        String rebootFtpId = "";
        String rebootFtpPw = "";

        String jeusAddr = "";
        String jeusStartNm = "";
        String jeusStartPort = "";
        String jeusBootHost = "";

        if (targetSystem.contains("TO")) {
            targetSystemFtp = targetSystemFtp.replace("TO", "TC");
        }

        if (targetServer.equals("real")) {

            jeusAddr = props.getProperty(targetSystem + ".jeusAddr");
            jeusStartNm = targetSystemFtp;
            jeusStartPort = props.getProperty(targetSystem + ".jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + ".jeusBootHost");

            rebootFtpIp = props.getProperty(targetSystem + "1.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "1.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "1.ftp.pw");

        } else if (targetServer.equals("test")) {

            jeusAddr = props.getProperty(targetSystem + "_TST.jeusAddr");
            jeusStartNm = targetSystemFtp;
            jeusStartPort = props.getProperty(targetSystem + "_TST.jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + "_TST.jeusBootHost");

            rebootFtpIp = props.getProperty(targetSystem + "_TST.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "_TST.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "_TST.ftp.pw");
        }

        SFTPService rebootftp = new SFTPService(rebootFtpIp, rebootFtpPort, rebootFtpId, rebootFtpPw);

        String bootSsshCommand = "";

        if (rebootFlag.equals("shutdown")) {
            // shutdown
            bootSsshCommand =
            // "nohup " +
                    "" + JEUS_HOME + "/bin/jeusadmin  " + "-host " + jeusAddr + ":" + jeusStartPort + " " + "-f " + JEUS_HOME
                            + "/bin/scripts/jeusEncode \"stopserver " + jeusStartNm + " -to 10\"  ";
            // + "& sleep 15";
            System.out
                    .println("====================================================================================================================================");
            System.out.println("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
            System.out
                    .println("====================================================================================================================================");

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

            // 복구 소스 생성
            if (targetSystemFtp.equals("TC1") || targetSystemFtp.equals("TC2")) {
                targetSystemFtp = targetSystemFtp.replace("TC", "TO");
            }
            sourceBakupCommandExec(props, targetSystem, ftp);


            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(20 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

        } else if (rebootFlag.equals("boot")) {
            // booot
            bootSsshCommand =
            // "nohup " +
                    "" + JEUS_HOME + "/bin/jeusadmin  -host " + jeusBootHost + ":" + jeusStartPort + " -f " + JEUS_HOME
                            + "/bin/scripts/jeusEncode \"startserver " + jeusStartNm + "\" ";
            // + " &";
            System.out
                    .println("====================================================================================================================================");
            System.out.println("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
            System.out
                    .println("====================================================================================================================================");

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

            HttpUrlConnectionCommon.curlHttpUrlCall(props, targetSystemFtp);
        }
        System.out
                .println("====================================================================================================================================");
        System.out.println("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END");
        System.out
                .println("====================================================================================================================================");

    }


    /**
     * Jeus webServerShutDownBootCommon 공통
     */
    public static void webServerShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            String targetServer) {

    }


    /**
     * Jeus webServerShutDownBootCommon 공통
     */
    public static void webServerShutDownBootCommonORG(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            String targetServer) {

        boolean isWebServerCheck = false;

        // FO7,FO8, MO12,MO13은 웹서버 재부팅 제외
        for (String element : WAS_SERVER_ARRAY) {
            if (targetSystemFtp.equals(element)) {
                isWebServerCheck = true;
                break;
            }
        }

        if (isWebServerCheck) {

            String rebootFtpIp = "";
            int rebootFtpPort = 22;
            String rebootFtpId = "";
            String rebootFtpPw = "";

            String jeusAddr = "";
            String jeusStartNm = "";
            String jeusStartPort = "";
            String jeusBootHost = "";

            if (targetSystem.contains("TO")) {
                targetSystemFtp = targetSystemFtp.replace("TO", "TC");
            }

            if (targetServer.equals("real")) {

                rebootFtpIp = props.getProperty(targetSystemFtp + ".web.ip");
                rebootFtpId = props.getProperty(targetSystemFtp + ".web.id");
                rebootFtpPw = props.getProperty(targetSystemFtp + ".web.pw");

            } else if (targetServer.equals("test")) {

                rebootFtpIp = props.getProperty(targetSystemFtp + "_TST.web.ip");
                rebootFtpId = props.getProperty(targetSystemFtp + "_TST.web.id");
                rebootFtpPw = props.getProperty(targetSystemFtp + "_TST.web.pw");
            }

            SFTPService rebootftp = new SFTPService(rebootFtpIp, rebootFtpPort, rebootFtpId, rebootFtpPw);

            String bootSsshCommand = "";

            if (rebootFlag.equals("wsdown")) {
                // shutdown
                bootSsshCommand = "sh /NAS-EC_PRD/tools/exec_wsdown.sh";
                System.out
                        .println("====================================================================================================================================");
                System.out.println("webServerReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
                System.out
                        .println("====================================================================================================================================");

                rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

                // try {
                // // System.out.println("sleep start::");
                // Thread.sleep(5 * 1000);
                // // System.out.println("sleep end::");
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }

            } else if (rebootFlag.equals("wsboot")) {
                // booot
                bootSsshCommand = "sh /NAS-EC_PRD/tools/exec_wsboot.sh";
                System.out
                        .println("====================================================================================================================================");
                System.out.println("webServerReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
                System.out
                        .println("====================================================================================================================================");

                rebootftp.sshCommandExecByNotLogger(bootSsshCommand);
            }
            System.out
                    .println("====================================================================================================================================");
            System.out.println("webServerReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END");
            System.out
                    .println("====================================================================================================================================");

        }
    }


    private static void sourceBakupCommandExec(Properties props, String targetSystem, SFTPService ftp) {

        System.out.println("2021.10.28 서버 DATA FULL 에러로 인해 수시배포시 백업중지 ");
    }


    private static void sourceBakupCommandExec_bak(Properties props, String targetSystem, SFTPService ftp) {


        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String yyyyMMdd = todate.substring(0, 8);
        String HHmmss = todate.substring(8, 14);
        String wasDir = props.getProperty("defaultDir") + "/WAS/";

        if (targetSystem.equals("MLPS")) {
            targetSystem = "mLPS";
        }

        if (targetSystem.contains("BATCH")) {
            wasDir = props.getProperty("defaultDir") + "/BATCH/";
        } else if (targetSystem.equals("FO_IMAGE") || targetSystem.equals("MO_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystem.equals("FO_IMAGE")) {
                targetSystem = "FO";
            } else if (targetSystem.equals("MO_IMAGE")) {
                targetSystem = "MO";
            }
        } else if (targetSystem.equals("FO_STAGING_IMAGE") || targetSystem.equals("MO_STAGING_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystem.equals("FO_STAGING_IMAGE")) {
                targetSystem = "FO_STG";
            } else if (targetSystem.equals("MO_STAGING_IMAGE")) {
                targetSystem = "MO_STG";
            }
        }

        String backupDir = wasDir + "backup/";

        String backupsshCommand =
                "cd " + wasDir + ";" + "cp -r " + targetSystem + " " + backupDir + targetSystem + "_" + yyyyMMdd + "_" + HHmmss;
        System.out.println("sourceBakupCommandExec ::::" + backupsshCommand);

        ftp.sshCommandExecByNotLogger(backupsshCommand);

    }


    private static void webSourceBakupCommandExec(Properties props, String targetSystem, SFTPService web_ftp) {

        System.out.println("2021.10.28 서버 DATA FULL 에러로 인해 수시배포시 백업중지 ");
    }


    private static void webSourceBakupCommandExec_bak(Properties props, String targetSystem, SFTPService web_ftp) {

        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String yyyyMMdd = todate.substring(0, 8);
        String HHmmss = todate.substring(8, 14);
        String webDir = props.getProperty("defaultDir") + "/WEB/";

        if (targetSystem.equals("MLPS")) {
            targetSystem = "mLPS";
        }

        String backupDir = webDir + "backup/";

        String backupsshCommand =
                "cd " + webDir + ";" + "cp -r " + targetSystem + " " + backupDir + targetSystem + "_" + yyyyMMdd + "_" + HHmmss;
        System.out.println("webSourceBakupCommandExec ::::" + backupsshCommand);

        web_ftp.sshCommandExecByNotLogger(backupsshCommand);

    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }


    public static void propServerSet(Properties props) {

        String FO_SET_01_prop = props.getProperty("FO_SET_01");
        String FO_SET_02_prop = props.getProperty("FO_SET_02");
        String MO_SET_01_prop = props.getProperty("MO_SET_01");
        String MO_SET_02_prop = props.getProperty("MO_SET_02");

        String SPARE_FO_SET_01_prop = props.getProperty("SPARE_FO_SET_01");
        String SPARE_FO_SET_02_prop = props.getProperty("SPARE_FO_SET_02");
        String SPARE_MO_SET_01_prop = props.getProperty("SPARE_MO_SET_01");
        String SPARE_MO_SET_02_prop = props.getProperty("SPARE_MO_SET_02");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");

        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, FO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, FO_SET_02);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, MO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, MO_SET_02);

        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_FO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_FO_SET_02);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_MO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_MO_SET_02);

        // System.out.println(Arrays.toString(WAS_SERVER_ARRAY));


    }


    public static <T> T[] append2Array(T[] elements, T[] newElements) {

        T[] newArray = Arrays.copyOf(elements, elements.length + newElements.length);
        System.arraycopy(newElements, 0, newArray, elements.length, newElements.length);

        return newArray;
    }



}
