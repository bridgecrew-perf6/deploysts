package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class ThreadDumpThread implements Runnable {

    static final String THREAD_DUMNP_SHELL_COMMNAD = "sh /home/hisisJ/thread_dump.sh";


    @Override
    public void run() {

    }


    public ThreadDumpThread(Properties props, String targetSystem) {

        // if (targetSystem.equals("FO")) {
        // threadDumpFO(targetSystem, props);
        //
        // } else if (targetSystem.equals("LPS")) {
        // threadDumpLPS(targetSystem, props);
        //
        // } else if (targetSystem.equals("MO")) {
        // threadDumpMO(targetSystem, props);
        //
        // } else if (targetSystem.equals("MLPS")) {
        // threadDumpMLPS(targetSystem, props);
        //
        // } else if (targetSystem.equals("BO")) {
        // threadDumpBO(targetSystem, props);
        //
        // } else if (targetSystem.equals("TO")) {
        // threadDumpTO(targetSystem, props);
        //
        // } else {
        // threadDumpMain(targetSystem, props);
        // }

        threadDumpMain(targetSystem, props);

    }


    private static void threadDumpMain(String targetSystem, Properties props) {

        threadDumpCommandExec(targetSystem, props);

    }


    private static void threadDumpFO(String targetSystem, Properties props) {

        // FO
        for (int i = 1; i <= 6; i++) {
            // FO 1,2,3,5,6ll
            if (i == 4) {
                continue;
            } else {
                targetSystem = "FO" + i;
                threadDumpCommandExec(targetSystem, props);
            }

        }
    }


    private static void threadDumpLPS(String targetSystem, Properties props) {

        // LPS , MLPS
        for (int i = 1; i <= 3; i++) {
            // LPS 1,2,3
            targetSystem = "LPS" + i;
            threadDumpCommandExec(targetSystem, props);
        }
    }


    private static void threadDumpMO(String targetSystem, Properties props) {

        // MO
        for (int i = 1; i <= 7; i++) {
            // MO 1,5,6,7
            if (i == 2 || i == 3 || i == 4) {
                continue;
            } else {
                targetSystem = "MO" + i;
                threadDumpCommandExec(targetSystem, props);
            }
        }
    }


    private static void threadDumpMLPS(String targetSystem, Properties props) {

        // LPS , MLPS
        for (int i = 1; i <= 3; i++) {
            // MLPS 1,2,3
            targetSystem = "MLPS" + i;
            threadDumpCommandExec(targetSystem, props);
        }
    }


    private static void threadDumpBO(String targetSystem, Properties props) {

        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "BO" + i;
            threadDumpCommandExec(targetSystem, props);
        }

    }


    private static void threadDumpTO(String targetSystem, Properties props) {

        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "TO" + i;
            threadDumpCommandExec(targetSystem, props);
        }

    }


    private static void threadDumpAll(Properties props) {

        String targetSystem = "";
        // FO
        for (int i = 1; i <= 6; i++) {
            // FO 1,2,3,5,6
            if (i == 4) {
                continue;
            } else {
                targetSystem = "FO" + i;
                threadDumpCommandExec(targetSystem, props);
            }

        }

        // MO
        for (int i = 1; i <= 8; i++) {
            // MO 1,5,6,7
            if (i == 2 || i == 3) {
                continue;
            } else {
                targetSystem = "MO" + i;
                threadDumpCommandExec(targetSystem, props);
            }
        }

        // LPS , MLPS
        for (int i = 1; i <= 3; i++) {
            // LPS 1,2,3
            targetSystem = "LPS" + i;
            threadDumpCommandExec(targetSystem, props);

            // MLPS 1,2,3
            targetSystem = "MLPS" + i;
            threadDumpCommandExec(targetSystem, props);
        }


        // BO
        for (int i = 1; i <= 2; i++) {
            // BO 1,2
            targetSystem = "BO" + i;
            threadDumpCommandExec(targetSystem, props);
        }

        // TO
        for (int i = 1; i <= 2; i++) {
            // TO 1,2
            targetSystem = "TO" + i;
            threadDumpCommandExec(targetSystem, props);
        }


    }


    private static void threadDumpCommandExec(String targetSystem, Properties props) {

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        // FO1 번기 Thread_dump
        targetFtpIp = props.getProperty(targetSystem + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystem + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystem + ".ftp.pw");

        SFTPService ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);
        ftp.sshCommandExecByShellScript(THREAD_DUMNP_SHELL_COMMNAD);
        log.info("THREAD_DUMNP_SHELL_COMMNAD :: [" + targetSystem + "]");
    }



}
