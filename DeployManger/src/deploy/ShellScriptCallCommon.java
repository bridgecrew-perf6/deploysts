package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class ShellScriptCallCommon {

    static final String SHELL_SCRIPT_01 = "df -h";

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


    public ShellScriptCallCommon(Properties props, String shellScriptNo, String parameter01, String parameter02, String parameter03) {

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        System.out.println("ShellScriptCallCommon START:: [" + shellScriptNo + "]");

        if ("SHELL_SCRIPT_01".equals(shellScriptNo)) {
            for (String element : FO_SET_01) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : FO_SET_02) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : SPARE_FO_SET_01) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : SPARE_FO_SET_02) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : MO_SET_01) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : MO_SET_02) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : SPARE_MO_SET_01_ALL) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }
            for (String element : SPARE_MO_SET_02_ALL) {
                ftpSshCommandExecCommon(props, element, SHELL_SCRIPT_01);
            }

            ftpSshCommandExecCommon(props, "MPP1", SHELL_SCRIPT_01);
            ftpSshCommandExecCommon(props, "MPP2", SHELL_SCRIPT_01);
        }
        System.out.println("ShellScriptCallCommon END:: [" + shellScriptNo + "]");

    }


    public static void ftpSshCommandExecCommon(Properties props, String targetSystem, String shellScript) {

        SFTPService ftp = null;

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        targetFtpIp = props.getProperty(targetSystem + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystem + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystem + ".ftp.pw");

        ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

        System.out.println("############################################");
        System.out.println("# [" + targetSystem + "] START");
        System.out.println("#");
        ftp.sshCommandExecByShellScript(shellScript);
        System.out.println("#");
        System.out.println("# [" + targetSystem + "] END");
        System.out.println("############################################");
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
