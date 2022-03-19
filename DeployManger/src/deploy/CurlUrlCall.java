package deploy;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CurlUrlCall {

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

    Properties props = null;
    String targetSystem = null;
    String targetSystemFtp = null;

    String[] setWasArr = null;
    String[] setSpareWasArr = null;


    public CurlUrlCall(Properties props, String targetSystemOrg) {

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (targetSystemOrg.contains("SET")) {

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

            curlUrlCallThreadArrSet(props, setWasArr);
            curlUrlCallThreadArrSet(props, setSpareWasArr);

        } else if (targetSystemOrg.contains("ALL")) {

            if (targetSystemOrg.contains("01")) {

                curlUrlCallThreadArrSet(props, FO_SET_01);
                curlUrlCallThreadArrSet(props, SPARE_FO_SET_01);

                curlUrlCallThreadArrSet(props, MO_SET_01);
                curlUrlCallThreadArrSet(props, SPARE_MO_SET_01);

                curlUrlCallThreadUnitSet(props, "BO1");
                curlUrlCallThreadUnitSet(props, "PO1");
                curlUrlCallThreadUnitSet(props, "CC1");
                curlUrlCallThreadUnitSet(props, "TO1");

            } else if (targetSystemOrg.contains("02")) {

                curlUrlCallThreadArrSet(props, FO_SET_02);
                curlUrlCallThreadArrSet(props, SPARE_FO_SET_02);

                curlUrlCallThreadArrSet(props, MO_SET_02);
                curlUrlCallThreadArrSet(props, SPARE_MO_SET_02);

                curlUrlCallThreadUnitSet(props, "BO2");
                curlUrlCallThreadUnitSet(props, "PO2");
                curlUrlCallThreadUnitSet(props, "CC2");
                curlUrlCallThreadUnitSet(props, "TO2");
            }



        } else {
            curlUrlCallThreadUnitSet(props, targetSystemOrg);

        }// targetSystem Set contain if_else_end



    }


    public static void curlUrlCallThreadArrSet(Properties props, String[] wasArr) {

        if (wasArr != null && wasArr.length > 0) {

            CurlUrlCallThread curlUrlCallThread = null;

            for (String element : wasArr) {

                curlUrlCallThread = new CurlUrlCallThread(props, element);

                Thread thread = new Thread(curlUrlCallThread, element);

                thread.start();

                Thread.currentThread().getName();

            }
        }

    }


    public static void curlUrlCallThreadUnitSet(Properties props, String targetSystem) {


        CurlUrlCallThread curlUrlCallThread = null;
        curlUrlCallThread = new CurlUrlCallThread(props, targetSystem);

        Thread thread = new Thread(curlUrlCallThread, targetSystem);

        thread.start();

        Thread.currentThread().getName();

    }


    public static void propServerSet(Properties props) {

        String FO_SET_01_prop = props.getProperty("FO_SET_01");
        String FO_SET_02_prop = props.getProperty("FO_SET_02");
        String MO_SET_01_prop = props.getProperty("MO_SET_01");
        String MO_SET_02_prop = props.getProperty("MO_SET_02_ALL");

        String SPARE_FO_SET_01_prop = props.getProperty("SPARE_FO_SET_01");
        String SPARE_FO_SET_02_prop = props.getProperty("SPARE_FO_SET_02");
        String SPARE_MO_SET_01_prop = props.getProperty("SPARE_MO_SET_01");
        String SPARE_MO_SET_02_prop = props.getProperty("SPARE_MO_SET_02");

        String ZIPPING_SET_01_prop = props.getProperty("ZIPPING_SET_01");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");

        ZIPPING_SET_01 = ZIPPING_SET_01_prop.split(",");
    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
