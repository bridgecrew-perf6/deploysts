package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class PreCompileThread implements Runnable {

    static final String PRE_COMPILE_SHELL_COMMNAD = "sh /home/hisisJ/precompile_jeus.sh";

    Properties props = null;
    String targetSystem = null;


    @Override
    public void run() {

        System.out.println("==============================================");
        System.out.println("PreCompileThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystem =" + targetSystem);
        System.out.println("==============================================");

        preCompileCommandExec(targetSystem, props);

        System.out.println("==============================================");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystem =" + targetSystem);
        System.out.println("PreCompileThread END");
        System.out.println("==============================================");

    }


    public PreCompileThread(Properties props, String targetSystem) {

        this.props = props;
        this.targetSystem = targetSystem;

    }


    private static void preCompileCommandExec(String targetSystem, Properties props) {

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        // FO1 번기 Thread_dump
        targetFtpIp = props.getProperty(targetSystem + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystem + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystem + ".ftp.pw");

        SFTPService ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);
        ftp.sshCommandExecByNotLogger(PRE_COMPILE_SHELL_COMMNAD);
    }



}
