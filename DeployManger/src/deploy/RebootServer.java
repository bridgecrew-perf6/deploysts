package deploy;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.HttpUrlConnectionCommon;
import utils.SFTPService;

@Slf4j
@Data
public class RebootServer {

    static final String JEUS_HOME = "/data/webapps/JEUS";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};


    public RebootServer(Properties props, String targetSystemOrg, String spareRebootYn, String spareOnlyYn, boolean isWebServer) {

        String targetSystem = "";
        String targetSystemFtp = "";
        String deployL4 = "";

        String[] setWasArr = null;
        String[] setSpareWasArr = null;

        if (targetSystemOrg.contains("SET")) {
            String[] recoverSystemToken = targetSystemOrg.split("_");
            targetSystem = recoverSystemToken[0];
            deployL4 = recoverSystemToken[2];

            // WAS서버 프로퍼티 셋팅
            propServerSet(props);

            if (targetSystemOrg.equals("FO_SET_01")) {
                setWasArr = FO_SET_01;
                setSpareWasArr = SPARE_FO_SET_01;
            } else if (targetSystemOrg.equals("FO_SET_02")) {
                setWasArr = FO_SET_02;
                setSpareWasArr = SPARE_FO_SET_02;
            } else if (targetSystemOrg.equals("MO_SET_01")) {
                setWasArr = MO_SET_01;
                setSpareWasArr = SPARE_MO_SET_01;
            } else if (targetSystemOrg.equals("MO_SET_02")) {
                setWasArr = MO_SET_02;
                setSpareWasArr = SPARE_MO_SET_02;
            }

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }

        } else {

            targetSystemFtp = targetSystemOrg;
            targetSystem = removeRex("[0-9]", targetSystemOrg);

            System.out.println("targetSystemOrg ::" + targetSystemOrg);
            System.out.println("targetSystem ::" + targetSystem);
            System.out.println("targetSystemFtp ::" + targetSystemFtp);
        }



        if (targetSystemOrg.contains("SET")) {

            RebootServerThread deployThread = null;

            for (String element : setWasArr) {

                targetSystemFtp = element;
                targetSystem = removeRex("[0-9]", targetSystemFtp);

                deployThread = new RebootServerThread(props, targetSystem, element, isWebServer);

                Thread thread = new Thread(deployThread, element);

                thread.start();

                Thread.currentThread().getName();

            }

            RebootServerThread deployThread2 = null;

            // 예비서버 재부팅
            if ("Y".equals(spareRebootYn)) {

                for (String element_spare : setSpareWasArr) {

                    targetSystemFtp = element_spare;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    deployThread2 = new RebootServerThread(props, targetSystem, element_spare, isWebServer);

                    Thread thread2 = new Thread(deployThread2, element_spare);

                    thread2.start();

                    Thread.currentThread().getName();
                }
            }



        } else {

            if (isWebServer) {
                webServerShutDownBootCommon(props, targetSystem, targetSystemFtp, "wsdown", "real");

                webServerShutDownBootCommon(props, targetSystem, targetSystemFtp, "wsboot", "real");
            } else {
                // jeus Shutdown
                jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "shutdown", "real");

                // jeus Boot
                jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "boot", "real");
            }
        }

    }


    public RebootServer(Properties props, String deployL4, String targetSystem) {

        String targetSystemFtp = "";
        List<String> targetSystemList = null;

        if (targetSystem.equals("FO")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("FO1");
                targetSystemList.add("FO6");
                targetSystemList.add("LPS1");

                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("FO2");
                targetSystemList.add("FO3");
                targetSystemList.add("FO5");
                targetSystemList.add("LPS2");
                targetSystemList.add("LPS3");


                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else if (targetSystem.equals("MO")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("MO1");
                targetSystemList.add("MO7");
                targetSystemList.add("MLPS1");

                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("MO5");
                targetSystemList.add("MO6");
                targetSystemList.add("MLPS2");
                targetSystemList.add("MLPS3");

                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else if (targetSystem.equals("BO")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("BO1");

                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("BO2");

                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else if (targetSystem.equals("POBO")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("PO1");
                targetSystemList.add("BO1");

                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("PO2");
                targetSystemList.add("BO2");

                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else if (targetSystem.equals("BOALL")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("CC1");
                targetSystemList.add("PO1");
                targetSystemList.add("BO1");
                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("CC2");
                targetSystemList.add("PO2");
                targetSystemList.add("BO2");

                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else if (targetSystem.equals("TO")) {

            if (deployL4.equals("01")) {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("TO1");

                reBootServerCommon(props, targetSystem, targetSystemList);

            } else {

                targetSystemList = new ArrayList<String>();
                targetSystemList.add("TO2");

                reBootServerCommon(props, targetSystem, targetSystemList);
            }

        } else {
            log.info("Deploy System check!!!!!");
        }// deploySystem if_else end


    }


    public static void reBootServerCommon(Properties props, String targetSystem, List<String> targetSystemList) {

        String targetSystemFtp = "";

        for (int i = 0; i < targetSystemList.size(); i++) {
            targetSystemFtp = targetSystemList.get(i);
            targetSystem = targetSystemFtp.replaceAll("[0-9]", "");

            System.out.println("targetSystemFtp  :: " + targetSystemFtp);
            System.out.println("targetSystem  :: " + targetSystem);

            // jeus Shutdown
            jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "shutdown", "real");

            // jeus Boot
            jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "boot", "real");

        }

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
                Thread.sleep(15 * 1000);
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
        String SPARE_MO_SET_01_prop = props.getProperty("SPARE_MO_SET_01_ALL");
        String SPARE_MO_SET_02_prop = props.getProperty("SPARE_MO_SET_02_ALL");

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
