package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class RebootServerDev {

    static final String JEUS_HOME = "/data/webapps/JEUS";


    public RebootServerDev(Properties props, String targetSystemOrg, String targetServer) {


        String targetSystem = "";
        String targetSystemFtp = "";

        // jeus Shutdown
        jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "shutdown", "real");

        // jeus Boot
        jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, "boot", "real");

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

            rebootftp.sshCommandExec(bootSsshCommand);

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

            rebootftp.sshCommandExec(bootSsshCommand);

        }

        log.info("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END  ====================::");

    }



}
