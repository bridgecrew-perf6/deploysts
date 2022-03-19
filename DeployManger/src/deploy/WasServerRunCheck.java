package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class WasServerRunCheck {

    static final String DEPLOY_DIR = "/data/home/hisis/ec/tools/deploy/";
    static final String RUN_SHELL_COMMAND = "sh /NAS-EC_PRD/tools/check_was_running_status.sh ";
    static final String RUN_SHELL_RESULT_SERVER_PATH = "/home/hisisJ/result_for_was_running_status.txt";
    static final String RUN_SHELL_RESULT_COMMAND = "cat /home/hisisJ/result_for_was_running_status.txt";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_ETC = new String[] {};


    public WasServerRunCheck(Properties props, String targetServer, String targetSystemOrg, boolean isWebServer) {

        SFTPService ftp = null;

        String targetSystem = "";
        String targetSystemFtp = "";

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        targetSystemFtp = targetSystemOrg;
        targetSystem = removeRex("[0-9]", targetSystemOrg);

        if (targetServer.equals("real")) {

            if (targetSystemOrg.contains("SET")) {

                String[] setWasArr = null;
                String[] setSpareWasArr = null;

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

                String[] targetSystemToken = targetSystemOrg.split("_");
                targetSystem = targetSystemToken[0];

                System.out.println("################################################");
                System.out.println("# " + targetSystemOrg);
                System.out.println("################################################");

                WasServerRunCheckThread deployThread = null;

                for (String element : setWasArr) {
                    deployThread = new WasServerRunCheckThread(targetServer, element, props, isWebServer);

                    Thread thread = new Thread(deployThread, element);

                    thread.start();

                    Thread.currentThread().getName();

                }


                WasServerRunCheckThread deployThread2 = null;

                for (String element_spare : setSpareWasArr) {

                    deployThread2 = new WasServerRunCheckThread(targetServer, element_spare, props, isWebServer);

                    Thread thread2 = new Thread(deployThread2, element_spare);

                    thread2.start();

                    Thread.currentThread().getName();
                }

                System.out.println("");

            } else if (targetSystemOrg.equals("ALL_THREAD")) {
                propServerSetThread(props, isWebServer);

                runEtcServerCheck(targetServer, props, isWebServer);

            } else if (targetSystemOrg.equals("ETC_SERVER")) {
                runEtcServerCheck(targetServer, props, isWebServer);

            } else if (targetSystemOrg.equals("ALL")) {

                // WAS서버 프로퍼티 셋팅
                propServerSet(props);

                System.out.println("################################################");
                System.out.println("# FO_SET_01");
                System.out.println("################################################");
                for (String element : FO_SET_01) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }

                for (String element : SPARE_FO_SET_01) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }
                System.out.println("");

                System.out.println("################################################");
                System.out.println("# FO_SET_02");
                System.out.println("################################################");

                for (String element : FO_SET_02) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }

                for (String element : SPARE_FO_SET_02) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }

                System.out.println("");

                System.out.println("################################################");
                System.out.println("# MO_SET_01");
                System.out.println("################################################");

                for (String element : MO_SET_01) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }

                for (String element : SPARE_MO_SET_01) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }
                System.out.println("");

                System.out.println("################################################");
                System.out.println("# MO_SET_02");
                System.out.println("################################################");

                for (String element : MO_SET_02) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }

                for (String element : SPARE_MO_SET_02) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    runShellCommandExecCommon(targetServer, targetSystemFtp, props);
                }
                System.out.println("");

                runEtcServerCheck(targetServer, props, isWebServer);

            } else {
                System.out.println("################################################");
                System.out.println("# " + targetSystemFtp);
                System.out.println("################################################");

                runShellCommandExecCommon(targetServer, targetSystemFtp, props);

                System.out.println("");
            }

        } else if (targetServer.equals("test")) {
            System.out.println("################################################");
            System.out.println("# " + targetSystemFtp);
            System.out.println("################################################");

            runShellCommandExecCommon(targetServer, targetSystemFtp, props);
            System.out.println("");
        } else if (targetServer.equals("dev")) {
            System.out.println("################################################");
            System.out.println("# " + targetSystemFtp);
            System.out.println("################################################");

            runShellCommandExecCommon(targetServer, targetSystemFtp, props);
            System.out.println("");
        }


        // System.out.println("targetSystemOrg ::" + targetSystemOrg);
        // System.out.println("targetSystem ::" + targetSystem);
        // System.out.println("targetSystemFtp ::" + targetSystemFtp);
    }


    private static void runShellCommandExecCommon(String targetServer, String targetSystem, Properties props) {



        String wasAliasName = "";

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        targetFtpIp = props.getProperty(targetSystem + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystem + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystem + ".ftp.pw");

        if (targetSystem.equals("LPS1") || targetSystem.equals("LPS2") || targetSystem.equals("LPS3")) {
            wasAliasName = "LHIECLPSWAS1";
        }

        // if (targetSystem.equals("MLPS1") || targetSystem.equals("MLPS2") || targetSystem.equals("MLPS3")) {
        // wasAliasName = "LHIECmLPSWAS1";
        // }

        if (targetSystem.contains("FO")) {
            wasAliasName = "LHIECFOWAS1";
        } else if (targetSystem.contains("MO")) {
            wasAliasName = "LHIECMOWAS1";
        } else if (targetSystem.contains("MLPS")) {
            wasAliasName = "LHIECmLPSWAS1";
        } else if (targetSystem.contains("BO")) {
            wasAliasName = "LHIECBOWAS1";
        } else if (targetSystem.contains("PO")) {
            wasAliasName = "LHIECPOWAS1";
        } else if (targetSystem.contains("CC")) {
            wasAliasName = "LHIECCCWAS1";
        } else if (targetSystem.contains("TO")) {
            wasAliasName = "LHIECTCWAS1";
        }

        if (targetServer.equals("test")) {
            targetFtpIp = props.getProperty(targetSystem + "_TST.ftp.ip");
            targetFtpId = props.getProperty(targetSystem + "_TST.ftp.id");
            targetFtpPw = props.getProperty(targetSystem + "_TST.ftp.pw");
        } else if (targetServer.equals("dev")) {
            targetFtpIp = props.getProperty(targetSystem + "_DEV.ftp.ip");
            targetFtpId = props.getProperty(targetSystem + "_DEV.ftp.id");
            targetFtpPw = props.getProperty(targetSystem + "_DEV.ftp.pw");
        }

        // System.out.println("targetSystem ::" + targetSystem);
        // System.out.println("wasAliasName ::" + wasAliasName);

        SFTPService ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);
        if (targetSystem.contains("TO")) {
            targetSystem = targetSystem.replaceAll("TO", "TC");
        }
        ftp.sshCommandExecByShellScriptNoLog(RUN_SHELL_COMMAND + " " + wasAliasName + " " + targetSystem);
        // log.info("RUN_SHELL_COMMAND :: [" + targetSystem + "]");

        // try {
        // Thread.sleep(1 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        ftp.sshCommandExecByShellScriptNoLog(RUN_SHELL_RESULT_COMMAND);
    }


    public static void runEtcServerCheck(String targetServer, Properties props, boolean isWebServer) {

        String targetSystem = "";

        WasServerRunCheckThread deployThread1 = null;

        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "BO" + i;

            deployThread1 = new WasServerRunCheckThread("real", targetSystem, props, isWebServer);

            Thread thread1 = new Thread(deployThread1, targetSystem);

            thread1.start();

            Thread.currentThread().getName();
        }


        WasServerRunCheckThread deployThread2 = null;

        System.out.println("");

        // PO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "PO" + i;
            deployThread2 = new WasServerRunCheckThread("real", targetSystem, props, isWebServer);

            Thread thread2 = new Thread(deployThread2, targetSystem);

            thread2.start();

            Thread.currentThread().getName();
        }
        System.out.println("");


        WasServerRunCheckThread deployThread3 = null;
        // CC
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "CC" + i;
            deployThread3 = new WasServerRunCheckThread("real", targetSystem, props, isWebServer);

            Thread thread3 = new Thread(deployThread3, targetSystem);

            thread3.start();

            Thread.currentThread().getName();
        }
        System.out.println("");

        WasServerRunCheckThread deployThread4 = null;

        // TO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "TO" + i;
            deployThread4 = new WasServerRunCheckThread("real", targetSystem, props, isWebServer);

            Thread thread4 = new Thread(deployThread4, targetSystem);

            thread4.start();

            Thread.currentThread().getName();
        }
        System.out.println("");
    }


    public static void runEtcServerCheck_Bak(String targetServer, Properties props) {

        String targetSystem = "";

        System.out.println("################################################");
        System.out.println("# BO");
        System.out.println("################################################");
        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "BO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# PO");
        System.out.println("################################################");

        // PO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "PO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# CC");
        System.out.println("################################################");

        // CC
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "CC" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# TO");
        System.out.println("################################################");
        // TO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "TO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props);
        }
        System.out.println("");
    }



    /**
     * 서버상태 결과 출력
     */
    public static boolean getRunShellResultCheck(String deploySystem, SFTPService ftp, String javacServerResultFile,
            String javacResultDownFile) {

        boolean compResult = false;
        ftp.downloadFile(javacServerResultFile, DEPLOY_DIR + "temp");

        File f = new File(javacResultDownFile);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        try {
            is = new FileInputStream(javacResultDownFile);
            reader = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(reader);

            while ((str = br.readLine()) != null) {
                System.out.println("" + str);
            }// while_end


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


        // javac_result.txt 파일 삭제
        // String sshCommand = "rm " + javacResultFile;
        // ftp.sshCommandExec(sshCommand);

        return compResult;
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


    public static void propServerSetThread(Properties props, boolean isWebServer) {

        String FO_SET_01_prop = props.getProperty("FO_SET_01");
        String FO_SET_02_prop = props.getProperty("FO_SET_02");
        String MO_SET_01_prop = props.getProperty("MO_SET_01");
        String MO_SET_02_prop = props.getProperty("MO_SET_02");

        String SPARE_FO_SET_01_prop = props.getProperty("SPARE_FO_SET_01");
        String SPARE_FO_SET_02_prop = props.getProperty("SPARE_FO_SET_02");
        String SPARE_MO_SET_01_prop = props.getProperty("SPARE_MO_SET_01");
        String SPARE_MO_SET_02_prop = props.getProperty("SPARE_MO_SET_02");
        String SPARE_MO_SET_ETC_prop = props.getProperty("SPARE_MO_SET_ETC");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");
        SPARE_MO_SET_ETC = SPARE_MO_SET_ETC_prop.split(",");

        String[] WAS_SERVER_ARRAY = new String[] {};

        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, FO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, FO_SET_02);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, MO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, MO_SET_02);

        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_FO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_FO_SET_02);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_MO_SET_01);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_MO_SET_02);
        WAS_SERVER_ARRAY = append2Array(WAS_SERVER_ARRAY, SPARE_MO_SET_ETC);
        System.out.println(Arrays.toString(WAS_SERVER_ARRAY));


        WasServerRunCheckThread deployThread1 = null;

        for (String element : WAS_SERVER_ARRAY) {

            deployThread1 = new WasServerRunCheckThread("real", element, props, isWebServer);

            Thread thread1 = new Thread(deployThread1, element);

            thread1.start();

            Thread.currentThread().getName();
        }

    }


    public static <T> T[] append2Array(T[] elements, T[] newElements) {

        T[] newArray = Arrays.copyOf(elements, elements.length + newElements.length);
        System.arraycopy(newElements, 0, newArray, elements.length, newElements.length);

        return newArray;
    }



    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
