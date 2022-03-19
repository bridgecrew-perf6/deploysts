package utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @class StringUtils
 * @brief String에 관련된 전반적인 기능을 제공하는 Util 패키지
 * @version 0.1
 * @author Kim Donghyeong
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * <pre>
     * 빈문자열 검사.
     * </pre>
     * 
     * @param input
     *        Input String
     * @return boolean
     */
    public static boolean isEmpty(String input) {

        return (input == null || input.trim().equals(""));
    }


    /**
     * <pre>
     * 빈 문자열 검사.
     * </pre>
     * 
     * @param input
     *        Input String
     * @return boolean
     */
    public static boolean isEmpty(String... input) {

        for (String str : input) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;

    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }


    public static boolean spaceCheck(String spaceCheck) {

        for (int i = 0; i < spaceCheck.length(); i++) {
            if (spaceCheck.charAt(i) == ' ') {
                return true;
            }
        }

        return false;
    }


}
