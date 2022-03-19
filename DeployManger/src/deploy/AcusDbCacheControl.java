package deploy;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class AcusDbCacheControl {

    static final String DB_CACHE_ENABLE = "sh /NAS-EC_PRD/tools/db_cache_enable.sh  ";
    static final String DB_CACHE_DISABLE = "sh /NAS-EC_PRD/tools/db_cache_disable.sh  ";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};


    public AcusDbCacheControl(Properties props, String targetSystem, String cacheEnableYn, String spareDeployYn, String spareOnlyYn) {

        dbCacheControlMain(targetSystem, cacheEnableYn, spareDeployYn, spareOnlyYn, props);

    }


    private static void dbCacheControlMain(String targetSystem, String cacheEnableYn, String spareDeployYn, String spareOnlyYn,
            Properties props) {

        dbCacheControlCommandExec(targetSystem, cacheEnableYn, spareDeployYn, spareOnlyYn, props);

    }


    private static void dbCacheControlCommandExec(String targetSystemOrg, String cacheEnableYn, String spareDeployYn, String spareOnlyYn,
            Properties props) {

        String[] setWasArr = null;
        String[] setSpareWasArr = null;
        SFTPService ftp = null;


        String targetSystem = "";
        String targetSystemFtp = "";
        String deployL4 = "";

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        String dbCatchControlShellCommnad = "";

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

            for (String element : setWasArr) {

                targetSystemFtp = element;
                targetSystem = removeRex("[0-9]", targetSystemFtp);

                targetFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
                targetFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
                targetFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

                ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

                if ("Y".equals(cacheEnableYn)) {
                    dbCatchControlShellCommnad = DB_CACHE_ENABLE + targetSystem;
                } else {
                    dbCatchControlShellCommnad = DB_CACHE_DISABLE + targetSystem;
                }

                System.out.println("cacheEnableYn :: [" + cacheEnableYn + "]");
                ftp.sshCommandExecByShellScript(dbCatchControlShellCommnad);
                System.out.println("DB_CACHE_COMMNAD :: [" + targetSystem + "]");

                new RebootServer(props, targetSystemFtp, spareDeployYn, spareOnlyYn, false);

            }

            // 예비서버 디플로이
            if ("Y".equals(spareDeployYn)) {

                for (String element : setSpareWasArr) {

                    targetSystemFtp = element;
                    targetSystem = removeRex("[0-9]", targetSystemFtp);

                    targetFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
                    targetFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
                    targetFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

                    ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

                    if ("Y".equals(cacheEnableYn)) {
                        dbCatchControlShellCommnad = DB_CACHE_ENABLE + targetSystem;
                    } else {
                        dbCatchControlShellCommnad = DB_CACHE_DISABLE + targetSystem;
                    }

                    System.out.println("cacheEnableYn :: [" + cacheEnableYn + "]");
                    ftp.sshCommandExecByShellScript(dbCatchControlShellCommnad);
                    System.out.println("DB_CACHE_COMMNAD :: [" + targetSystem + "]");

                    new RebootServer(props, targetSystemFtp, spareDeployYn, spareOnlyYn, false);

                }
            }


        } else {
            targetFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
            targetFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
            targetFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

            ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

            if ("Y".equals(cacheEnableYn)) {
                dbCatchControlShellCommnad = DB_CACHE_ENABLE + targetSystem;
            } else {
                dbCatchControlShellCommnad = DB_CACHE_DISABLE + targetSystem;
            }

            System.out.println("cacheEnableYn :: [" + cacheEnableYn + "]");
            ftp.sshCommandExecByShellScript(dbCatchControlShellCommnad);
            System.out.println("DB_CACHE_COMMNAD :: [" + targetSystem + "]");

            new RebootServer(props, targetSystemFtp, spareDeployYn, spareOnlyYn, false);

        }
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
