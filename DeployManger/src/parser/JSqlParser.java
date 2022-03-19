package parser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;


public class JSqlParser {

    public static void main(String[] args) {

        boolean isValidation = true;
        String errorMessage = "";

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String table_name = "";
        /*
         * 1. 스크립트 테이블명 체크 - Foreign Key 제외
         */

        // CCJSqlParserManager pm = new CCJSqlParserManager();
        String sql = "SELECT * FROM cool";
        sql = "UPDATE BUBBA_SQL  realname = 'Bubba Jean' WHERE id = 1000000;";
        // sql = "INSERT INTO BUBBA_SQL (id, userid, realname, displayname) VALUES ( 1000000, 'bubba@bubba.com', 'BUBBA JOE', 'Bubba');";
        sql = "";

        StringBuffer sqlStr = new StringBuffer();
        try {
            File f = new File("S:\\개발관련\\배포관리시스템\\DDL_샘플_및_자료\\test\\00.템플릿_테이블_인덱스생성.sql");
            if (f.exists()) {

                String str;
                is = new FileInputStream("S:\\개발관련\\배포관리시스템\\DDL_샘플_및_자료\\test\\00.템플릿_테이블_인덱스생성.sql");
                reader = new InputStreamReader(is, "euc-kr");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {
                    sqlStr.append(str);
                    sqlStr.append("\n");

                    if (str.contains("CREATE")) {
                        if (str.contains(table_name)) {
                            isValidation = true;
                        } else {
                            isValidation = false;
                            errorMessage = "해당 스크립트에 TABLE명이 정확하기 않습니다.";
                            break;
                        }


                    }

                    if (str.contains("GRANT") || str.contains("ALTER") || str.contains("COMMENT")) {
                        if (str.contains(table_name)) {
                            isValidation = true;
                        } else {
                            isValidation = false;
                            errorMessage = "해당 스크립트에 TABLE명이 정확하기 않습니다.";
                            break;
                        }
                    }
                }
            }

            if (!isValidation) {
                StringReader statementReader = new StringReader(sqlStr.toString());
                // pm.parse(new StringReader(statementReader.toString()));

                System.out.println("SUCCESS");
                // } catch (JSQLParserException e) {
                // System.out.println("SQL: " + sqlStr.toString());
                // System.out.println("NOT VALID SQL: " + e.getCause().getMessage());
                // // throw new RuntimeException(e.getCause().getMessage(), e);
            } else {
                System.out.println("FAIL");
                System.out.println(errorMessage);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
