package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class WasServerRunCheckThread implements Runnable {

    static final String DEPLOY_DIR = "/data/home/hisis/ec/tools/deploy/";
    static final String RUN_SHELL_COMMAND = "sh /NAS-EC_PRD/tools/check_was_running_status.sh ";
    static final String RUN_SHELL_RESULT_SERVER_PATH = "/home/hisisJ/result_for_was_running_status.txt";
    static final String RUN_SHELL_RESULT_COMMAND = "cat /home/hisisJ/result_for_was_running_status.txt";

    static final String RUN_WEB_SHELL_COMMAND = "sh /NAS-EC_PRD/tools/check_web_running_status.sh ";
    static final String RUN_WEB_SHELL_RESULT_COMMAND = "cat /home/hisisJ/result_for_web_running_status.txt";

    Properties props = null;
    String targetServer = null;
    String targetSystemFtp = null;
    boolean isWebServer = false;


    @Override
    public void run() {

        // System.out.println("==============================================");
        // System.out.println("WasServerRunCheckThread START");
        // System.out.println("ThreadName =" + Thread.currentThread().getName());
        // System.out.println("targetSystemFtp =" + targetSystemFtp);
        // System.out.println("==============================================");

        runShellCommandExecCommon(targetServer, targetSystemFtp, props, isWebServer);

        // System.out.println("==============================================");
        // System.out.println("ThreadName =" + Thread.currentThread().getName());
        // System.out.println("targetSystemFtp =" + targetSystemFtp);
        // System.out.println("WasServerRunCheckThread END");
        // System.out.println("==============================================");
    }


    public WasServerRunCheckThread(String targetServer, String targetSystemFtp, Properties props, boolean isWebServer) {

        this.targetServer = targetServer;
        this.targetSystemFtp = targetSystemFtp;
        this.props = props;
        this.isWebServer = isWebServer;
    }


    private static void runShellCommandExecCommon(String targetServer, String targetSystem, Properties props, boolean isWebServer) {



        String wasAliasName = "";

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        String targeWebIp = "";
        int targetWebPort = 22;
        String targetWebId = "";
        String targetWebPw = "";

        targetFtpIp = props.getProperty(targetSystem + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystem + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystem + ".ftp.pw");

        targeWebIp = props.getProperty(targetSystem + ".web.ip");
        targetWebId = props.getProperty(targetSystem + ".web.id");
        targetWebPw = props.getProperty(targetSystem + ".web.pw");

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

        if (isWebServer) {

            SFTPService web_ftp = new SFTPService(targeWebIp, targetWebPort, targetWebId, targetWebPw);

            web_ftp.sshCommandExecByShellScriptNoLog(RUN_WEB_SHELL_COMMAND);
            web_ftp.sshCommandExecByShellScriptNoLog(RUN_WEB_SHELL_RESULT_COMMAND);

        } else {
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
    }


    public static void runEtcServerCheck(String targetServer, Properties props, boolean isWebServer) {

        String targetSystem = "";

        System.out.println("################################################");
        System.out.println("# BO");
        System.out.println("################################################");
        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "BO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props, isWebServer);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# PO");
        System.out.println("################################################");

        // PO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "PO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props, isWebServer);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# CC");
        System.out.println("################################################");

        // CC
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "CC" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props, isWebServer);
        }
        System.out.println("");

        System.out.println("################################################");
        System.out.println("# TO");
        System.out.println("################################################");
        // TO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "TO" + i;
            runShellCommandExecCommon(targetServer, targetSystem, props, isWebServer);
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


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
