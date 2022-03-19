package deploy;


import java.util.ArrayList;
import java.util.List;
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
public class DeployOnServerByZiping {

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

    // /data/webapps/JEUS/domains/fo_domain/servers/FO1/.workspace/deployed/_generated_/FO/__jspwork/jeus_jspwork
    // /data/webapps/JEUS/domains/mo_domain/servers/MO1/.workspace/deployed/_generated_/MO/__jspwork/jeus_jspwork
    // /data/webapps/JEUS/domains/lps_domain/servers/LPS1/.workspace/deployed/_generated_/LPS/__jspwork/jeus_jspwork
    // /data/webapps/JEUS/domains/mlps_domain/servers/MLPS1/.workspace/deployed/_generated_/mLPS/__jspwork/jeus_jspwork



    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};

    static String[] SPARE_MO_SET_01_ALL = new String[] {};
    static String[] SPARE_MO_SET_02_ALL = new String[] {};

    static String[] MPP_SET_01 = new String[] {};
    static String[] MPP_SET_02 = new String[] {};

    static String[] ZIPPING_SET_01 = new String[] {};
    static String[] ZIPPING_SET_ALL = new String[] {};


    public DeployOnServerByZiping(Properties props, String baseSystem, List<String> targetSystemArr, boolean zippingYn) {

        boolean isTargetSystem = targetSystemCheck(baseSystem, targetSystemArr);
        if (!isTargetSystem) {
            throw new RuntimeException("baseSystem과 TargetSystem을 정확하게 선택하세요. !!!");
        }

        if (baseSystem.contains("IMAGE")) {

            getWasSourceDownByBaseSystemForImage(props, baseSystem);

            for (int i = 0; i < targetSystemArr.size(); i++) {
                // 이미지 소스
                sourceUploadModuleForImage(props, targetSystemArr.get(i), targetSystemArr.get(i));
            }

        } else {

            if (zippingYn) {
                // 소스파일다운
                getWasSourceDownByBaseSystem(props, baseSystem);
            }

            DeployOnServerByZipingThread deployThread = null;

            for (int i = 0; i < targetSystemArr.size(); i++) {

                deployThread = new DeployOnServerByZipingThread(props, targetSystemArr.get(i));
                Thread thread = new Thread(deployThread, targetSystemArr.get(i));
                thread.start();

                Thread.currentThread().getName();
            }
        }

    }


    public boolean targetSystemCheck(String baseSystem, List<String> targetSystemArr) {

        boolean isTargetSystem = false;

        for (int i = 0; i < targetSystemArr.size(); i++) {
            String targetSystem = targetSystemArr.get(i);

            if (baseSystem.contains("IMAGE")) {
                if (targetSystem.contains("IMAGE")) {
                    isTargetSystem = true;
                } else {
                    isTargetSystem = false;
                    // throw new RuntimeException("baseSystem과 TargetSystem을 정확하게 선택하세요. !!!");
                }
            } else {
                baseSystem = removeRex("[0-9]", baseSystem);
                targetSystem = removeRex("[0-9]", targetSystem);

                if (targetSystem.equals("LPS")) {
                    targetSystem = "FO";
                }
                if (targetSystem.equals("MLPS")) {
                    targetSystem = "MO";
                }

                if (baseSystem.equals(targetSystem)) {
                    isTargetSystem = true;
                } else {
                    isTargetSystem = false;
                }


                if (targetSystem.equals("PO") && baseSystem.equals("BO")) {
                    isTargetSystem = true;
                }
                if (targetSystem.equals("BO") && baseSystem.equals("PO")) {
                    isTargetSystem = true;
                }
            }
        }

        return isTargetSystem;

    }


    // 파일 압축만 실행
    public DeployOnServerByZiping(Properties props, String basetSystem) {

        if (basetSystem.contains("ZIPPING_SET")) {

            String[] setWasArr = null;

            boolean backupYn = false;

            // WAS서버 프로퍼티 셋팅
            propServerSet(props);

            if (basetSystem.equals("ZIPPING_SET_01")) {
                setWasArr = ZIPPING_SET_01;

            } else if (basetSystem.equals("ZIPPING_SET_ALL")) {
                setWasArr = ZIPPING_SET_ALL;
                backupYn = true;
            }

            DeployOnServerByZipingBakThread deployThread = null;


            for (String element : setWasArr) {

                deployThread = new DeployOnServerByZipingBakThread(props, element, backupYn);

                Thread thread = new Thread(deployThread, element);

                thread.start();

                Thread.currentThread().getName();

            }

        } else {
            getWasSourceDownByBaseSystem(props, basetSystem);
        }


    }


    public DeployOnServerByZiping(Properties props, String targetSystem, String spareOnlyYn, boolean zippingYn) {

        // boolean zippingYn = false;

        String[] setWasArr = null;
        String[] setSpareWasArr = null;
        SFTPService ftp = null;

        boolean webSourceDownYn = true;

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (targetSystem.equals("FO_SET_01")) {
            setWasArr = FO_SET_01;
            setSpareWasArr = SPARE_FO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }

        } else if (targetSystem.equals("FO_SET_02")) {
            setWasArr = FO_SET_02;
            setSpareWasArr = SPARE_FO_SET_02;
        } else if (targetSystem.equals("MO_SET_01")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }
        } else if (targetSystem.equals("MO_SET_02")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02;
        } else if (targetSystem.equals("MO_SET_01_ALL")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01_ALL;
        } else if (targetSystem.equals("MO_SET_02_ALL")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02_ALL;
        } else if (targetSystem.equals("MPP_PARTNER1")) {
            setWasArr = MPP_SET_01;
        } else if (targetSystem.equals("MPP_PARTNER2")) {
            setWasArr = MPP_SET_02;
        }


        if (targetSystem.equals("FO_IMAGE2") || targetSystem.equals("FO_IMAGE1")) {
            webSourceDownYn = false;
        } else if (targetSystem.equals("MO_IMAGE2") || targetSystem.equals("MO_IMAGE1")) {
            webSourceDownYn = false;
        }

        if (zippingYn) {
            // 소스파일다운
            getWasSourceDownCommon(props, targetSystem, webSourceDownYn);
        }


        if (targetSystem.contains("SET")) {

            DeployOnServerByZipingThread deployThread = null;

            List<String> wasArrList = new ArrayList<String>();


            // 예비서버 디플로이
            if (!"Y".equals(spareOnlyYn)) {

                for (String element : setWasArr) {

                    if (element.equals("FO1") || element.equals("MO1")) {
                        // 웹서버 재부팅
                        // webServerShutDownBootCommon(props, targetSystem, element, "wsdown", "real");

                        // webServerShutDownBootCommon(props, targetSystem, element, "wsboot", "real");

                    } else {
                        wasArrList.add(element);
                    }

                }
            }

            for (String element_spare : setSpareWasArr) {
                wasArrList.add(element_spare);
            }

            System.out.println("Deploy List ::\n" + wasArrList);
            for (int i = 0; i < wasArrList.size(); i++) {
                // System.out.println("" + wasArrList.get(i));

                deployThread = new DeployOnServerByZipingThread(props, wasArrList.get(i));

                Thread thread = new Thread(deployThread, wasArrList.get(i));

                thread.start();

                Thread.currentThread().getName();
            }

            /*
             * // 예비서버 디플로이 if (!"Y".equals(spareOnlyYn)) {
             * 
             * for (String element : setWasArr) {
             * 
             * if (element.equals("FO1") || element.equals("MO1")) { // 웹서버 재부팅 webServerShutDownBootCommon(props, targetSystem, element,
             * "wsdown", "real");
             * 
             * webServerShutDownBootCommon(props, targetSystem, element, "wsboot", "real");
             * 
             * } else { deployThread = new DeployOnServerByZipingThread(props, element);
             * 
             * Thread thread = new Thread(deployThread, element);
             * 
             * thread.start();
             * 
             * Thread.currentThread().getName(); }
             * 
             * } }
             * 
             * DeployOnServerByZipingThread deployThread2 = null;
             * 
             * for (String element_spare : setSpareWasArr) {
             * 
             * deployThread2 = new DeployOnServerByZipingThread(props, element_spare);
             * 
             * Thread thread2 = new Thread(deployThread2, element_spare);
             * 
             * thread2.start();
             * 
             * Thread.currentThread().getName(); }
             */

        } else {

            if (targetSystem.contains("MPP_PARTNER")) {

                // MPP_PARTNER
                for (String element : setWasArr) {
                    wasSourceUploadCommon(props, element, spareOnlyYn, webSourceDownYn);
                }

                JeusShutDownBootThread deployThread = null;


                for (String element : setWasArr) {

                    deployThread = new JeusShutDownBootThread(props, element, element);

                    Thread thread = new Thread(deployThread, element);

                    thread.start();

                    Thread.currentThread().getName();

                }


            } else {
                // 소스파일업로드
                wasSourceUploadCommon(props, targetSystem, spareOnlyYn, webSourceDownYn);
            }
        }
    }


    // Web서버 복구용
    public DeployOnServerByZiping(Properties props, String targetSystem, String spareDeployYn, String spareOnlyYn) {

        String[] setWasArr = null;
        String[] setSpareWasArr = null;

        boolean webSourceDownYn = true;

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (targetSystem.equals("FO_IMAGE1")) {
            setWasArr = FO_SET_01;
            setSpareWasArr = SPARE_FO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }

        } else if (targetSystem.equals("FO_IMAGE2")) {
            setWasArr = FO_SET_02;
            setSpareWasArr = SPARE_FO_SET_02;
        } else if (targetSystem.equals("MO_IMAGE1")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }
        } else if (targetSystem.equals("MO_IMAGE2")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02;
        } else if (targetSystem.equals("MPP_PARTNER1")) {
            setWasArr = MPP_SET_01;
        } else if (targetSystem.equals("MPP_PARTNER2")) {
            setWasArr = MPP_SET_02;
        }


        if (targetSystem.equals("FO_IMAGE2")) {
            webSourceDownYn = false;
        } else if (targetSystem.equals("MO_IMAGE2")) {
            webSourceDownYn = false;
        }

        if ("Y".equals(spareOnlyYn)) {
            setWasArr = new String[] {};
        }

        // 소스파일다운
        getWasSourceDownCommon(props, targetSystem, webSourceDownYn);

        DeployOnServerByZipingThread deployThread = null;


        for (String element : setWasArr) {

            deployThread = new DeployOnServerByZipingThread(props, element, true);

            Thread thread = new Thread(deployThread, element);

            thread.start();

            Thread.currentThread().getName();


        }

        DeployOnServerByZipingThread deployThread2 = null;

        // 예비서버
        if ("Y".equals(spareDeployYn)) {

            for (String element_spare : setSpareWasArr) {

                deployThread2 = new DeployOnServerByZipingThread(props, element_spare, true);

                Thread thread2 = new Thread(deployThread2, element_spare);

                thread2.start();

                Thread.currentThread().getName();
            }
        }


    }


    /**
     * 소스파일다운
     */
    public static void getWasSourceDownCommon(Properties props, String targetSystem, boolean webSourceDownYn) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";
        String webDir = uploadServerDir + "/WEB/";

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

        if (targetSystem.equals("FO_SET_01") || targetSystem.equals("FO_SET_02") || targetSystem.equals("LPS3")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO";

            deployFtpIp = props.getProperty("FO1" + ".ftp.ip");
            deployFtpId = props.getProperty("FO1" + ".ftp.id");
            deployFtpPw = props.getProperty("FO1" + ".ftp.pw");
            deployFtpPath = props.getProperty("FO1" + ".ftp.path");

            deployWebIp = props.getProperty("FO1" + ".web.ip");
            deployWebId = props.getProperty("FO1" + ".web.id");
            deployWebPw = props.getProperty("FO1" + ".web.pw");
            deployWebPath = props.getProperty("FO1" + ".web.path");

        } else if (targetSystem.equals("MO_SET_01") || targetSystem.equals("MO_SET_02") || targetSystem.equals("MO_SET_01_ALL")
                || targetSystem.equals("MO_SET_02_ALL") || targetSystem.equals("MLPS3")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO";

            deployFtpIp = props.getProperty("MO1" + ".ftp.ip");
            deployFtpId = props.getProperty("MO1" + ".ftp.id");
            deployFtpPw = props.getProperty("MO1" + ".ftp.pw");
            deployFtpPath = props.getProperty("MO1" + ".ftp.path");

            deployWebIp = props.getProperty("MO1" + ".web.ip");
            deployWebId = props.getProperty("MO1" + ".web.id");
            deployWebPw = props.getProperty("MO1" + ".web.pw");
            deployWebPath = props.getProperty("MO1" + ".web.path");

        } else if (targetSystem.equals("FO_IMAGE1") || targetSystem.equals("FO_IMAGE2")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO_IMAGE";

            deployFtpIp = props.getProperty("FO_IMAGE1" + ".ftp.ip");
            deployFtpId = props.getProperty("FO_IMAGE1" + ".ftp.id");
            deployFtpPw = props.getProperty("FO_IMAGE1" + ".ftp.pw");
            deployFtpPath = props.getProperty("FO_IMAGE1" + ".ftp.path");

            wasDir = uploadServerDir + "/WEB/";

        } else if (targetSystem.equals("MO_IMAGE1") || targetSystem.equals("MO_IMAGE2")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO_IMAGE";

            deployFtpIp = props.getProperty("MO_IMAGE1" + ".ftp.ip");
            deployFtpId = props.getProperty("MO_IMAGE1" + ".ftp.id");
            deployFtpPw = props.getProperty("MO_IMAGE1" + ".ftp.pw");
            deployFtpPath = props.getProperty("MO_IMAGE1" + ".ftp.path");

            wasDir = uploadServerDir + "/WEB/";

        } else if (targetSystem.equals("PO2") || targetSystem.equals("BO1") || targetSystem.equals("BO2")) {
            // PO2, BO1, BO2

            zipping_shell_was_server = "PO";
            zipping_shell_web_server = "PO";

            deployFtpIp = props.getProperty("PO1" + ".ftp.ip");
            deployFtpId = props.getProperty("PO1" + ".ftp.id");
            deployFtpPw = props.getProperty("PO1" + ".ftp.pw");
            deployFtpPath = props.getProperty("PO1" + ".ftp.path");

            deployWebIp = props.getProperty("PO1" + ".web.ip");
            deployWebId = props.getProperty("PO1" + ".web.id");
            deployWebPw = props.getProperty("PO1" + ".web.pw");
            deployWebPath = props.getProperty("PO1" + ".web.path");

        } else if (targetSystem.equals("CC2")) {
            zipping_shell_was_server = "CC";
            zipping_shell_web_server = "CC";

            deployFtpIp = props.getProperty("CC1" + ".ftp.ip");
            deployFtpId = props.getProperty("CC1" + ".ftp.id");
            deployFtpPw = props.getProperty("CC1" + ".ftp.pw");
            deployFtpPath = props.getProperty("CC1" + ".ftp.path");

            deployWebIp = props.getProperty("CC1" + ".web.ip");
            deployWebId = props.getProperty("CC1" + ".web.id");
            deployWebPw = props.getProperty("CC1" + ".web.pw");
            deployWebPath = props.getProperty("CC1" + ".web.path");

        } else if (targetSystem.equals("TO2")) {
            zipping_shell_was_server = "TO";
            zipping_shell_web_server = "TO";

            deployFtpIp = props.getProperty("TO1" + ".ftp.ip");
            deployFtpId = props.getProperty("TO1" + ".ftp.id");
            deployFtpPw = props.getProperty("TO1" + ".ftp.pw");
            deployFtpPath = props.getProperty("TO1" + ".ftp.path");

            deployWebIp = props.getProperty("TO1" + ".web.ip");
            deployWebId = props.getProperty("TO1" + ".web.id");
            deployWebPw = props.getProperty("TO1" + ".web.pw");
            deployWebPath = props.getProperty("TO1" + ".web.path");

        }

        if (targetSystem.contains("MPP")) {
            zipping_shell_was_server = "MPP";
            zipping_shell_web_server = "MPP";

            deployFtpIp = props.getProperty("MPP_PARTNER1" + ".ftp.ip");
            deployFtpId = props.getProperty("MPP_PARTNER1" + ".ftp.id");
            deployFtpPw = props.getProperty("MPP_PARTNER1" + ".ftp.pw");
            deployFtpPath = props.getProperty("MPP_PARTNER1" + ".ftp.path");

            deployWebIp = props.getProperty("MPP_PARTNER1" + ".web.ip");
            deployWebId = props.getProperty("MPP_PARTNER1" + ".web.id");
            deployWebPw = props.getProperty("MPP_PARTNER1" + ".web.pw");
            deployWebPath = props.getProperty("MPP_PARTNER1" + ".web.path");
        }

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);


        if (targetSystem.equals("MLPS")) {
            targetSystem = "mLPS";
        }

        zipping_shell_was = ZIPPING_COMMON_SHELL + " " + wasDir + " " + zipping_shell_was_server + "_WAS.tar " + zipping_shell_was_server;
        zipping_shell_web = ZIPPING_COMMON_SHELL + " " + webDir + " " + zipping_shell_web_server + "_WEB.tar " + zipping_shell_web_server;

        if (targetSystem.contains("IMAGE")) {
            zipping_shell_was =
                    ZIPPING_COMMON_SHELL + " " + wasDir + " " + zipping_shell_web_server + "_WEB.tar " + zipping_shell_was_server;
        }

        System.out.println("zipping_shell_was ::" + zipping_shell_was);
        System.out.println("zipping_shell_web ::" + zipping_shell_web);
        // was_소스 tar생성
        ftp.sshCommandExecByNotLogger(zipping_shell_was);

        if (webSourceDownYn) {
            web_ftp = new SFTPService(deployWebIp, deployWebPort, deployWebId, deployWebPw);
            // web_소스 tar생성
            web_ftp.sshCommandExecByNotLogger(zipping_shell_web);
        }
        //
        // try {
        // // System.out.println("sleep start::");
        // Thread.sleep(8 * 1000);
        // // System.out.println("sleep end::");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        // LOCAL_DEFAULT_DIR로 다운로드

        if (targetSystem.contains("IMAGE")) {
            ftp.download(zipping_shell_web_server + "_WEB.tar", DEPLOY_TEMP_DIR, wasDir);
        } else {
            ftp.download(zipping_shell_was_server + "_WAS.tar", DEPLOY_TEMP_DIR, wasDir);
        }
        if (webSourceDownYn) {
            web_ftp.download(zipping_shell_was_server + "_WEB.tar", DEPLOY_TEMP_DIR, webDir);
        }

        // try {
        // // System.out.println("sleep start::");
        // Thread.sleep(5 * 1000);
        // // System.out.println("sleep end::");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }


        if (targetSystem.contains("FO") || targetSystem.equals("LPS2") || targetSystem.equals("LPS3")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_FO_PATH_01 + "FO1" + JEUS_JSP_WORK_FO_PATH_02;
        } else if (targetSystem.contains("MO") || targetSystem.equals("MLPS2") || targetSystem.equals("MLPS3")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_MO_PATH_01 + "MO1" + JEUS_JSP_WORK_MO_PATH_02;
        } else if (targetSystem.equals("PO2") || targetSystem.equals("BO1") || targetSystem.equals("BO2")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_PO_PATH_01 + "PO1" + JEUS_JSP_WORK_PO_PATH_02;
        }
        if (targetSystem.equals("MPP_PARTNER2")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_MPP_PATH_01 + "admin_hiweb1" + JEUS_JSP_WORK_MPP_PATH_02;
        }

        if (targetSystem.contains("IMAGE")) {
            jspwork_zipping_dir = null;
        }


        if (StringUtils.isNotEmpty(jspwork_zipping_dir)) {
            jspwork_zipping_shell =
                    ZIPPING_COMMON_SHELL + " " + jspwork_zipping_dir + " " + zipping_shell_was_server + "_jspwork.tar " + JEUS_JSP_WORK_NM;


            System.out.println("jspwork_zipping_shell ::" + jspwork_zipping_shell);
            // was_소스 tar생성
            ftp.sshCommandExecByNotLogger(jspwork_zipping_shell);

            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(5 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            ftp.download(zipping_shell_was_server + "_jspwork.tar", DEPLOY_TEMP_DIR, jspwork_zipping_dir);

            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(5 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

        }

    }


    /**
     * 소스파일다운
     */
    public static void getWasSourceDownByBaseSystem(Properties props, String basetSystem) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";
        String webDir = uploadServerDir + "/WEB/";

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

        if (basetSystem.contains("FO_IMAGE")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO_IMAGE";

            wasDir = uploadServerDir + "/WEB/";

        }

        if (basetSystem.contains("MO_IMAGE")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO_IMAGE";

            wasDir = uploadServerDir + "/WEB/";

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
            ftp.download(zipping_shell_web_server + "_WEB.tar", DEPLOY_TEMP_DIR, wasDir);
        } else {
            ftp.download(zipping_shell_was_server + "_WAS.tar", DEPLOY_TEMP_DIR, wasDir);

            web_ftp = new SFTPService(deployWebIp, deployWebPort, deployWebId, deployWebPw);
            // web_소스 tar생성
            web_ftp.sshCommandExecByNotLogger(zipping_shell_web);

            web_ftp.download(zipping_shell_was_server + "_WEB.tar", DEPLOY_TEMP_DIR, webDir);
        }


        if (basetSystem.contains("FO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_FO_PATH_01 + basetSystem + JEUS_JSP_WORK_FO_PATH_02;
        } else if (basetSystem.contains("MO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_MO_PATH_01 + basetSystem + JEUS_JSP_WORK_MO_PATH_02;
        } else if (basetSystem.contains("PO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_PO_PATH_01 + basetSystem + JEUS_JSP_WORK_PO_PATH_02;
        } else if (basetSystem.contains("BO")) {
            jspwork_zipping_dir = JEUS_JSP_WORK_BO_PATH_01 + basetSystem + JEUS_JSP_WORK_BO_PATH_02;
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

            ftp.download(zipping_shell_was_server + "_jspwork.tar", DEPLOY_TEMP_DIR, jspwork_zipping_dir);
        }

    }


    /**
     * 소스파일다운
     */
    public static void getWasSourceDownByBaseSystemForImage(Properties props, String basetSystem) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";

        String[] setWasArr = null;
        String[] setSpareWasArr = null;
        SFTPService ftp = null;
        SFTPService web_ftp = null;

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        String zipping_shell_was = "";

        String zipping_shell_was_server = "";
        String zipping_shell_web_server = "";

        if (basetSystem.equals("FO_IMAGE1") || basetSystem.equals("FO_IMAGE2")) {
            zipping_shell_was_server = "FO";
            zipping_shell_web_server = "FO_IMAGE";

        } else if (basetSystem.equals("MO_IMAGE1") || basetSystem.equals("MO_IMAGE2")) {
            zipping_shell_was_server = "MO";
            zipping_shell_web_server = "MO_IMAGE";

        }

        deployFtpIp = props.getProperty(basetSystem + ".ftp.ip");
        deployFtpId = props.getProperty(basetSystem + ".ftp.id");
        deployFtpPw = props.getProperty(basetSystem + ".ftp.pw");
        deployFtpPath = props.getProperty(basetSystem + ".ftp.path");

        wasDir = uploadServerDir + "/WEB/";

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        zipping_shell_was = ZIPPING_COMMON_SHELL + " " + wasDir + " " + zipping_shell_web_server + "_WEB.tar " + zipping_shell_was_server;

        System.out.println("zipping_shell_was ::" + zipping_shell_was);
        // was_소스 tar생성
        ftp.sshCommandExecByNotLogger(zipping_shell_was);

        // LOCAL_DEFAULT_DIR로 다운로드
        ftp.download(zipping_shell_web_server + "_WEB.tar", DEPLOY_TEMP_DIR, wasDir);

    }


    /**
     * 소스파일업로드
     */
    public static void wasSourceUploadCommon(Properties props, String targetSystem, String spareOnlyYn, boolean webSourceDownYn) {

        String[] setWasArr = null;
        String[] setSpareWasArr = null;

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (targetSystem.equals("FO_SET_01")) {
            setWasArr = FO_SET_01;
            setSpareWasArr = SPARE_FO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }

        } else if (targetSystem.equals("FO_SET_02")) {
            setWasArr = FO_SET_02;
            setSpareWasArr = SPARE_FO_SET_02;
        } else if (targetSystem.equals("MO_SET_01")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01;

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }
        } else if (targetSystem.equals("MO_SET_02")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02;

        }


        if (targetSystem.contains("SET")) {
            // FO, MO

            for (String element : setWasArr) {
                sourceUploadModule(props, targetSystem, element);
            }

            for (String element_spare : setSpareWasArr) {
                sourceUploadModule(props, targetSystem, element_spare);
            }

        } else {
            if (webSourceDownYn) {
                sourceUploadModule(props, targetSystem, targetSystem);
            } else {
                // 이미지 소스
                sourceUploadModuleForImage(props, targetSystem, targetSystem);
            }

        }



    }


    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModule(Properties props, String targetSystem, String deploySystemFtp) {

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
            wasDir = webDir;
        } else if (deploySystem.equals("MO_IMAGE")) {
            zippingSystem = "MO";
            wasDir = webDir;
        } else {
            zippingSystem = deploySystem;
        }

        if (deploySystem.contains("MPP")) {
            zippingSystem = "MPP";
            deploySystemDir = "MPP";
        }

        /*****************************************************************
         * WAS관련
         *****************************************************************/
        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);
        if (!deploySystemFtp.contains("IMAGE")) {
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown", ftp);
        }

        String sshCommand = "";

        if (!deploySystemFtp.contains("MPP_PARTNER") || !deploySystemFtp.contains("MPP_MOBILE")) {


            // WAS 소스 업로드
            ftp.upload(DEPLOY_TEMP_DIR + zippingSystem + "_WAS.tar", wasDir);

            // 업로드파일 WAS파일로 교체
            sshCommand = "rm -r " + wasDir + deploySystemDir;

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
            } else if (deploySystemFtp.contains("PO")) {
                jspwork_dir = JEUS_JSP_WORK_PO_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_PO_PATH_02;
            } else if (deploySystemFtp.contains("BO")) {
                jspwork_dir = JEUS_JSP_WORK_BO_PATH_01 + deploySystemFtp + JEUS_JSP_WORK_BO_PATH_02;
            } else if (deploySystemFtp.contains("MPP")) {
                String jeusStartNm = props.getProperty(deploySystemFtp + ".jeusStartNm");
                jspwork_dir = JEUS_JSP_WORK_MPP_PATH_01 + jeusStartNm + JEUS_JSP_WORK_MPP_PATH_02;
            }

            if (deploySystemFtp.contains("IMAGE") || deploySystemFtp.contains("MPP")) {
                jspwork_dir = null;
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
        }

        if (!deploySystemFtp.contains("IMAGE")) {
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

            if (!deploySystem.contains("MPP")) {
                // WAS서버 부팅
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot", ftp);
            }

        }



    }


    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModuleForImage(Properties props, String targetSystem, String deploySystemFtp) {

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

    }


    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModuleForImage(Properties props, String baseSystem, String targetSystem, String deploySystemFtp) {

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

    }


    /**
     * wasSourceUploadModule
     */
    public static void sourceUploadModuleByImage(Properties props, String targetSystem, String deploySystemFtp) {

        String uploadServerDir = props.getProperty("defaultDir");
        String wasDir = uploadServerDir + "/WAS/";
        String webDir = uploadServerDir + "/WEB/";

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

        String deploySystem = "";
        String deploySystemDir = "";
        String zippingSystem = "";

        deploySystem = removeRex("[0-9]", deploySystemFtp);
        if (deploySystemFtp.contains("IMAGE")) {
            deploySystem = deploySystem.replace("_IMAGE", "");
        }


        deployFtpIp = props.getProperty(deploySystemFtp + ".ftp.ip");
        deployFtpId = props.getProperty(deploySystemFtp + ".ftp.id");
        deployFtpPw = props.getProperty(deploySystemFtp + ".ftp.pw");
        deployFtpPath = props.getProperty(deploySystemFtp + ".ftp.path");

        deployWebIp = props.getProperty(deploySystemFtp + ".web.ip");
        deployWebId = props.getProperty(deploySystemFtp + ".web.id");
        deployWebPw = props.getProperty(deploySystemFtp + ".web.pw");
        deployWebPath = props.getProperty(deploySystemFtp + ".web.path");

        deploySystemDir = deploySystem;

        if (deploySystem.equals("MLPS")) {
            deploySystemDir = "mLPS";
        }

        /*****************************************************************
         * WAS관련
         *****************************************************************/
        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // WAS 소스 업로드
        ftp.upload(DEPLOY_TEMP_DIR + deploySystem + "_WAS.tar", webDir);

        // 업로드파일 WAS파일로 교체
        String sshCommand = "rm -r " + webDir + deploySystemDir;

        ftp.sshCommandExecByNotLogger(sshCommand);
        System.out.println("#");
        System.out.println("#sshCommand  WAS 기존소스 삭제 ::" + sshCommand);
        System.out.println("##############################################################################");

        // sshCommand = "tar xvf " + webDir + targetSystem + "_WAS.tar" + " -C " + webDir;
        String expanding_shell = EXPANDING_COMMON_SHELL + " " + webDir + " " + deploySystem + "_WAS.tar";

        System.out.println("#sshCommand WEB  압축해제 ::" + expanding_shell);
        ftp.sshCommandExecByNotLogger(expanding_shell);

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

            if (targetSystemFtp.contains("MPP")) {

                jeusStartNm = props.getProperty(targetSystemFtp + ".jeusStartNm");

                jeusAddr = props.getProperty("MPP1.ftp.ip");
                jeusStartPort = props.getProperty("MPP1.jeusStartPort");
                jeusBootHost = props.getProperty("MPP1.ftp.ip");
                rebootFtpIp = props.getProperty("MPP1.ftp.ip");
                rebootFtpId = props.getProperty("MPP1.ftp.id");
                rebootFtpPw = props.getProperty("MPP1.ftp.pw");
            } else {

                jeusAddr = props.getProperty(targetSystem + ".jeusAddr");
                jeusStartNm = targetSystemFtp;
                jeusStartPort = props.getProperty(targetSystem + ".jeusStartPort");
                jeusBootHost = props.getProperty(targetSystem + ".jeusBootHost");

                rebootFtpIp = props.getProperty(targetSystem + "1.ftp.ip");
                rebootFtpId = props.getProperty(targetSystem + "1.ftp.id");
                rebootFtpPw = props.getProperty(targetSystem + "1.ftp.pw");

            }

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

            if (targetSystemFtp.contains("MPP")) {
                bootSsshCommand =
                        "source ~/.bash_profile; nohup /data/webapps/JEUS8/bin/jeusadmin -domain MPP_domain -host 10.154.12.51:8736 -f /data/webapps/JEUS8/bin/jeusEncode -domain MPP_domain -u administrator -cachelogin "
                                + "\"" + "stopserver " + jeusStartNm + "\"  &";
                // + " & sleep 5 ";
            }

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

            if (targetSystemFtp.contains("MPP")) {
                bootSsshCommand =
                        "source ~/.bash_profile; nohup /data/webapps/JEUS8/bin/jeusadmin -domain MPP_domain -host 10.154.12.51:8736 -f /data/webapps/JEUS8/bin/jeusEncode -domain MPP_domain -u administrator -cachelogin "
                                + "\"" + "startserver " + jeusStartNm + "\"  &";
                // + " & sleep 5 ";
            }

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

        String rebootFtpIp = "";
        int rebootFtpPort = 22;
        String rebootFtpId = "";
        String rebootFtpPw = "";

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

            try {
                // System.out.println("sleep start::");
                Thread.sleep(5 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

        if (targetSystem.equals("FO_IMAGE")) {
            targetSystem = "FO";
        } else if (targetSystem.equals("MO_IMAGE")) {
            targetSystem = "MO";
        } else if (targetSystem.equals("MLPS")) {
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
        String SPARE_MO_SET_01_ALL_prop = props.getProperty("SPARE_MO_SET_01_ALL");
        String SPARE_MO_SET_02_ALL_prop = props.getProperty("SPARE_MO_SET_02_ALL");

        String MPP_SET_01_prop = props.getProperty("MPP_SET_01");
        String MPP_SET_02_prop = props.getProperty("MPP_SET_02");

        String ZIPPING_SET_01_prop = props.getProperty("ZIPPING_SET_01");
        String ZIPPING_SET_ALL_prop = props.getProperty("ZIPPING_SET_ALL");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");
        SPARE_MO_SET_01_ALL = SPARE_MO_SET_01_ALL_prop.split(",");
        SPARE_MO_SET_02_ALL = SPARE_MO_SET_02_ALL_prop.split(",");

        MPP_SET_01 = MPP_SET_01_prop.split(",");
        MPP_SET_02 = MPP_SET_02_prop.split(",");

        ZIPPING_SET_01 = ZIPPING_SET_01_prop.split(",");
        ZIPPING_SET_ALL = ZIPPING_SET_ALL_prop.split(",");
    }



}
