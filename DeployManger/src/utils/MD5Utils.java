package utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class MD5Utils {

    /**
     * 메시지 다이제스트 알고리즘 128비트 해쉬값 생성, 단방향 암호화 같은 입력값이면 항상 같은 출력값 다른 입력값에서 같은 출력값이 나올 확률은 낮음 0은 아님 현재는 네트워크로 전송된 큰 파일의 무결성 확인
     */
    public static byte[] digest(String a_szAlgorithm, byte[] a_input) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(a_szAlgorithm);
        return md.digest(a_input);

    } // end public static byte[] digest(String a_szAlgorithm, byte[] a_input) throws NoSuchAlgorithmException


    public static String getCryptoMD5String(String a_szSource) throws Exception {

        String result = "";
        if (a_szSource == null) {

            throw new Exception("Can't conver to Message Digest String value!!");

        } // end if (a_szSource == null)

        if (a_szSource.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {

            byte[] bip = digest("MD5", a_szSource.getBytes());
            String eip;

            int nSize = bip.length;

            for (int i = 0; i < nSize; i++) {

                eip = "" + Integer.toHexString(bip[i] & 0x000000ff);

                if (eip.length() < 2) {

                    eip = "0" + eip;

                } // end if (eip.length() < 2)

                result = result + eip;

            } // end for

        } else {
            result = a_szSource;
        }

        return result;

    } // end public static String getCryptoMD5String(String a_szSource) throws Exception


    public static byte[] encodeBase64(byte[] a_src) throws Exception {

        try {

            return Base64.encodeBase64(a_src);

        } catch (Exception e) {

            throw e;

        } // end try

    } // end public static byte[] encodeBase64(byte[] a_src) throws Exception


    public static String encode(String a_src) throws Exception {

        try {

            return new String(encodeBase64(a_src.getBytes("8859_1")));

        } catch (Exception e) {

            throw e;

        } // end try

    } // end public static String encodeBase64(String a_src) throws Exception


    public static byte[] decodeBase64(byte[] a_src) throws Exception {

        try {

            return Base64.decodeBase64(a_src);

        } catch (Exception e) {

            throw e;

        } // end try

    } // end public static byte[] decodeBase64(byte[] a_src) throws Exception


    public static String decode(String a_src) throws Exception {

        try {

            return new String(decodeBase64(a_src.getBytes("8859_1")));

        } catch (Exception e) {

            throw e;

        } // end try

    } // end public static String decode(String a_src) throws Exception


    public static void main(String[] args) {

        try {

            String testStr = "배포관리시스템.TXT";
            // testStr = "배포_TEST시스템.TXT";
            // testStr = "redmine_test.TXT";

            String md5Str = MD5Utils.getCryptoMD5String(testStr);
            System.out.println("md5Str ::: " + md5Str);

            // 200211141341_5dc6d3658fadd124d0cf11d99187bce9.TXT

            String crypStr = CryptoUtil.md5(testStr);
            System.out.println("crypStr ::" + crypStr);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

} // end MD5Util
