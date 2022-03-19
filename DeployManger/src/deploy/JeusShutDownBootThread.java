package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class JeusShutDownBootThread implements Runnable {

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
    String targetSystemFtp = null;
    String targetSystem = null;
    boolean bacupYn = false;

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


    @Override
    public void run() {

        System.out.println("==============================================");
        System.out.println("JeusShutDownBootThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("==============================================");

        // jeus Boot
        jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "boot", null);

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
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("JeusShutDownBootThread END");
        System.out.println("==============================================");

    }


    public JeusShutDownBootThread(Properties props, String targetSystem, String targetSystemFtp) {

        this.props = props;
        this.targetSystem = targetSystem;
        this.targetSystemFtp = targetSystemFtp;
        propServerSet(props);
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommonControl(Properties props, String targetSystem, String targetSystemFtp) {

        // jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, rebootFlag, ftp, "real");
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            SFTPService ftp) {

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        deployFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
        deployFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
        deployFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");
        deployFtpPath = props.getProperty(targetSystemFtp + ".ftp.path");


        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

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
            // sourceBakupCommandExec(props, targetSystem, ftp);


            try {
                // System.out.println("sleep start::");
                Thread.sleep(20 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

            // HttpUrlConnectionCommon.curlHttpUrlCall(props, targetSystemFtp);
        }
        System.out
                .println("====================================================================================================================================");
        System.out.println("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END");
        System.out
                .println("====================================================================================================================================");

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
