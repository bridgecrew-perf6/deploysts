package utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class HttpUrlConnectionCommon {

    static final String FO_HIMART_URL = "curl -v -X GET http://www.e-himart.co.kr/app/display/showDisplayShop?originReferrer=himartindex/";
    static final String LPS_HIMART_URL =
            "curl -v -X GET https://secure.e-himart.co.kr/app/login/login?returnUrl=http%3A%2F%2Fwww.e-himart.co.kr%2Fapp%2Fdisplay%2FshowDisplayShop%3ForiginReferrer%3Dhimartindex";
    static final String MO_HIMART_URL = "curl -v  -X GET http://m.e-himart.co.kr/app/display/main/";
    static final String MLPS_HIMART_URL =
            "curl -v -X GET https://msecure.e-himart.co.kr/app/login/login?returnUrl=http://m.e-himart.co.kr/app/display/main";

    static final String BO_HIMART_URL = "curl -v -X GET https://bo.e-himart.co.kr/login/show.lecs/";
    static final String PO_HIMART_URL = "curl -v -X GET https://po.e-himart.co.kr/login/show.lecs/";
    static final String CC_HIMART_URL = "curl -v -X GET https://cc.e-himart.co.kr/login/show.lecs/";
    static final String TO_HIMART_URL = "curl -v -X GET http://omniapp.e-himart.co.kr/pp/display/main/";


    public static void httpUrlConnection(String sourceUrl) {

        BufferedReader in = null;

        try {
            URL obj = new URL(sourceUrl); // 호출할 url
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                // response를 차례대로 출력
                System.out.println(line);

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void post(String strUrl, String jsonMessage) {

        OutputStreamWriter wr = null;
        BufferedReader br = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); // 서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            // con.addRequestProperty("x-api-key", RestTestCommon.API_KEY); // key값 설정

            con.setRequestMethod("POST");

            // json으로 message를 전달하고자 할 때
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoInput(true);
            con.setDoOutput(true); // POST 데이터를 OutputStream으로 넘겨 주겠다는 설정
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(jsonMessage); // json 형식의 message 전달
            wr.flush();

            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Stream을 처리해줘야 하는 귀찮음이 있음.
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(con.getResponseMessage());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            try {
                if (wr != null) {
                    wr.close();
                }
                if (br != null) {
                    br.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void get(String strUrl) {

        BufferedReader br = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); // 서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            // con.addRequestProperty("x-api-key", RestTestCommon.API_KEY); // key값 설정

            con.setRequestMethod("GET");



            // URLConnection에 대한 doOutput 필드값을 지정된 값으로 설정한다. URL 연결은 입출력에 사용될 수 있다. URL 연결을 출력용으로 사용하려는 경우 DoOutput 플래그를 true로 설정하고, 그렇지 않은
            // 경우는 false로 설정해야 한다. 기본값은 false이다.

            con.setDoOutput(false);

            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Stream을 처리해줘야 하는 귀찮음이 있음.
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(con.getResponseMessage());
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void curlHttpUrlCall(Properties props, String targetSystemFtp) {

        String curlUrl = "";

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        if (targetSystemFtp.contains("TC")) {
            targetSystemFtp = targetSystemFtp.replaceAll("TC", "TO");
        }

        targetFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
        targetFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
        targetFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

        SFTPService ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

        if (targetSystemFtp.contains("MLPS")) {
            curlUrl = MLPS_HIMART_URL;
        } else {
            if (targetSystemFtp.contains("LPS")) {
                curlUrl = LPS_HIMART_URL;
            } else if (targetSystemFtp.contains("FO")) {
                curlUrl = FO_HIMART_URL;
            } else if (targetSystemFtp.contains("MO")) {
                curlUrl = MO_HIMART_URL;
            } else if (targetSystemFtp.contains("BO")) {
                curlUrl = BO_HIMART_URL;
            } else if (targetSystemFtp.contains("PO")) {
                curlUrl = PO_HIMART_URL;
            } else if (targetSystemFtp.contains("CC")) {
                curlUrl = CC_HIMART_URL;
            } else if (targetSystemFtp.contains("TO")) {
                curlUrl = TO_HIMART_URL;
            }



        }
        System.out.println("curlUrl targetSystemFtp::" + targetSystemFtp);
        System.out.println("curlUrl ::" + curlUrl);
        for (int i = 0; i < 5; i++) {
            // System.out.println("curlUrl count::" + i);
            ftp.sshCommandExecByNotLogger(curlUrl);
        }

        System.out.println("curlUrl END ::");
    }



}
