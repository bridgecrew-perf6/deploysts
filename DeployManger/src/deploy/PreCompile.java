package deploy;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class PreCompile {

    static final String PRE_COMPILE_SHELL_COMMNAD = "sh /home/hisisJ/precompile_jeus.sh";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};
    static String[] ZIPPING_SET_01 = new String[] {};
    static String[] ZIPPING_SET_FO_MO = new String[] {};

    Properties props = null;
    String targetSystem = null;
    String targetSystemFtp = null;

    String[] setWasArr = null;
    String[] setSpareWasArr = null;


    public PreCompile(Properties props, String targetSystem) {

        preCompileCommandExec(targetSystem, props);
    }


    public PreCompile(Properties props, String targetSystemOrg, String spareOnlyYn) {

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (targetSystemOrg.equals("ZIPPING_SET_01")) {
            // setWasArr = new String[3];
            // setWasArr[0] = "FO1";
            // setWasArr[1] = "MO7";
            // setWasArr[2] = "PO1";

            setWasArr = ZIPPING_SET_01;
        } else if (targetSystemOrg.equals("ZIPPING_SET_FO_MO")) {
            // setWasArr = new String[3];
            // setWasArr[0] = "FO1";
            // setWasArr[1] = "MO7";
            // setWasArr[2] = "PO1";

            setWasArr = ZIPPING_SET_FO_MO;
        } else {

            if (targetSystemOrg.equals("FO_SET_01")) {
                setWasArr = FO_SET_01;
                setSpareWasArr = SPARE_FO_SET_01;
            } else if (targetSystemOrg.equals("MO_SET_01")) {
                setWasArr = MO_SET_01;
                setSpareWasArr = SPARE_MO_SET_01;
            } else if (targetSystemOrg.equals("FO_SET_02")) {
                setWasArr = FO_SET_02;
                setSpareWasArr = SPARE_FO_SET_02;
            } else if (targetSystemOrg.equals("MO_SET_02")) {
                setWasArr = MO_SET_02;
                setSpareWasArr = SPARE_MO_SET_02;
            }

            if ("Y".equals(spareOnlyYn)) {
                setWasArr = new String[] {};
            }
        }


        if (setWasArr != null && setWasArr.length > 0) {

            PreCompileThread deployThread = null;

            for (String element : setWasArr) {

                deployThread = new PreCompileThread(props, element);

                Thread thread = new Thread(deployThread, element);

                thread.start();

                Thread.currentThread().getName();

            }
        }

        if (setSpareWasArr != null) {

            PreCompileThread deployThread2 = null;

            for (String element_spare : setSpareWasArr) {

                deployThread2 = new PreCompileThread(props, element_spare);

                Thread thread2 = new Thread(deployThread2, element_spare);

                thread2.start();

                Thread.currentThread().getName();
            }

        }



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
        ftp.sshCommandExecByShellScript(PRE_COMPILE_SHELL_COMMNAD);
        log.info("PRE_COMPILE_SHELL_COMMNAD :: [" + targetSystem + "]");
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

        String ZIPPING_SET_01_prop = props.getProperty("ZIPPING_SET_01");
        String ZIPPING_SET_FO_MO_prop = props.getProperty("ZIPPING_SET_FO_MO");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");

        ZIPPING_SET_01 = ZIPPING_SET_01_prop.split(",");
        ZIPPING_SET_FO_MO = ZIPPING_SET_FO_MO_prop.split(",");
    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
