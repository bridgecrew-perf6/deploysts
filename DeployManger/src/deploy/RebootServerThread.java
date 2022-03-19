package deploy;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.HttpUrlConnectionCommon;
import utils.SFTPService;

@Slf4j
@Data
public class RebootServerThread implements Runnable {

    static final String JEUS_HOME = "/data/webapps/JEUS";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};

    Properties props = null;
    String targetSystem = null;
    String targetSystemFtp = null;
    boolean isWasServer = false;

    static final String FO_HIMART_URL = "curl -X GET http://www.e-himart.co.kr/app/display/showDisplayShop?originReferrer=himartindex/";
    static final String LPS_HIMART_URL =
            "curl -X GET https://secure.e-himart.co.kr/app/login/login?returnUrl=http%3A%2F%2Fwww.e-himart.co.kr%2Fapp%2Fdisplay%2FshowDisplayShop%3ForiginReferrer%3Dhimartindex";
    static final String MO_HIMART_URL = "curl -X GET http://m.e-himart.co.kr/app/display/main/";
    static final String MLPS_HIMART_URL =
            "curl -X GET https://msecure.e-himart.co.kr/app/login/login?returnUrl=http://m.e-himart.co.kr/app/display/main";



    @Override
    public void run() {

        System.out.println("==============================================");
        System.out.println("RebootServerThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("==============================================");

        if (isWasServer) {
            webServerShutDownBootCommon(props, targetSystem, targetSystemFtp, "wsdown", "real");

            webServerShutDownBootCommon(props, targetSystem, targetSystemFtp, "wsboot", "real");
        } else {
            // jeus Shutdown
            jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "shutdown", "real");

            // jeus Boot
            jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "boot", "real");
        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("==============================================");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("RebootServerThread END");
        System.out.println("==============================================");

    }


    public RebootServerThread(Properties props, String targetSystem, String targetSystemFtp, boolean isWasServer) {

        this.props = props;
        this.targetSystem = targetSystem;
        this.targetSystemFtp = targetSystemFtp;
        this.isWasServer = isWasServer;
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            String targetServer) {

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

            log.info("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);

            rebootftp.sshCommandExecByShellScriptNoLog(bootSsshCommand);

            try {
                log.info("sleep start::");
                Thread.sleep(20 * 1000);
                log.info("sleep end::");
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

            log.info("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);

            rebootftp.sshCommandExecByShellScriptNoLog(bootSsshCommand);

            HttpUrlConnectionCommon.curlHttpUrlCall(props, targetSystemFtp);


        }

        log.info("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END  ====================::");

    }


    /**
     * Jeus webServerShutDownBootCommon 공통
     */
    public static void webServerShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
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

            rebootftp.sshCommandExecByShellScript(bootSsshCommand);

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

            rebootftp.sshCommandExecByShellScript(bootSsshCommand);
        }
        System.out
                .println("====================================================================================================================================");
        System.out.println("webServerReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END");
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

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");
    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
