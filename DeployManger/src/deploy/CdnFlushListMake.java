package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class CdnFlushListMake {

    public static void main(String[] args) {

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String cdnFlushFullPath = "C:\\cdnflush\\";
        File cdnFlushDir = new File(cdnFlushFullPath);
        ArrayList<String> fileList = getAllFileList(cdnFlushDir);

        String cdnFlushFile = "";
        String cdnFlushFileBakupFile = "";

        List<String> cdnFlushList = null;
        List<String> newCdnFlushList = null;
        HashSet<String> listSet = null;
        List<String> processedList = null;

        List<String> foStrList = null;
        List<String> moStrList = null;
        List<String> toStrList = null;

        String foFlushStr = null;
        String moFlushStr = null;
        String toFlushStr = null;


        // 원본 파일명 분리 후 세팅
        for (String flist : fileList) {
            try {
                cdnFlushFile = cdnFlushFullPath + flist;
                cdnFlushFileBakupFile = cdnFlushFullPath + "/completed/" + flist;
                is = new FileInputStream(cdnFlushFile);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                cdnFlushList = new ArrayList<String>();
                newCdnFlushList = new ArrayList<String>();

                String str;
                String[] strArr;
                String fulshStr; // FlushStr


                List<String> foFullStrList = new ArrayList<String>();
                List<String> moFullStrList = new ArrayList<String>();

                while ((str = br.readLine()) != null) {
                    strArr = str.split(":");

                    if (strArr.length > 1 && strArr[0] != null) {
                        fulshStr = strArr[0];

                        if (fulshStr.indexOf(".") > 0) {

                        } else {
                            System.out.println("Diretory :: " + fulshStr);
                        }

                        if (fulshStr.contains("/03_Front/WebContent")) {
                            foFlushStr = fulshStr;
                            foFullStrList.add(foFlushStr);
                        } else if (fulshStr.contains("/04_FrontMobile/WebContent")) {
                            moFlushStr = fulshStr;
                            moFullStrList.add(moFlushStr);
                        }

                        fulshStr = fulshStr.replace("/trunk", "");
                        fulshStr = fulshStr.replace("{", "");
                        fulshStr = fulshStr.replace("\"", "");
                        fulshStr = fulshStr.trim();

                        if (fileExtCheck(fulshStr)) {
                            newCdnFlushList.add(fulshStr);
                        }
                        // System.out.println("fulshStr : " + fulshStr);
                    } else {
                        strArr = str.split(" ");
                        if (strArr.length > 2 && strArr[1] != null) {
                            fulshStr = strArr[1];
                            fulshStr = fulshStr.replace("/trunk", "");
                            fulshStr = fulshStr.replace("{", "");
                            fulshStr = fulshStr.replace("\"", "");
                            fulshStr = fulshStr.trim();

                            // System.out.println("fulshStr 222:::" + fulshStr);

                            if (fileExtCheck(fulshStr)) {
                                newCdnFlushList.add(fulshStr);
                            }
                        }
                    }
                }


                // for (int i = 0; i < newCdnFlushList.size(); i++) {
                // System.out.println("newCdnFlushList : " + newCdnFlushList.get(i));
                // }

                // System.out.println("newCdnFlushList :::" + newCdnFlushList.size());

                listSet = new HashSet<String>(newCdnFlushList);
                processedList = new ArrayList<String>(listSet);

                // System.out.println("processedList.size():::" + processedList.size());


                foStrList = new ArrayList<String>();
                moStrList = new ArrayList<String>();
                toStrList = new ArrayList<String>();



                for (int i = 0; i < processedList.size(); i++) {
                    // System.out.println(processedList.get(i));

                    if (processedList.get(i).contains("/03_Front/WebContent")) {
                        foFlushStr = processedList.get(i);
                        foFlushStr = foFlushStr.replace("/03_Front/WebContent", "");
                        foStrList.add(foFlushStr);
                    } else if (processedList.get(i).contains("/04_FrontMobile/WebContent")) {
                        moFlushStr = processedList.get(i);
                        moFlushStr = moFlushStr.replace("/04_FrontMobile/WebContent", "");
                        moStrList.add(moFlushStr);
                    } else if (processedList.get(i).contains("/09_Tablet/WebContent")) {
                        toFlushStr = processedList.get(i);
                        toFlushStr = toFlushStr.replace("/09_Tablet/WebContent", "");
                        toStrList.add(toFlushStr);
                    } else {
                        // System.out.println("기타 시스템 : " + processedList.get(i));
                    }

                }

                System.out.println("");
                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("MO FlushList Start : " + moStrList.size());
                System.out.println("=============================================== ");
                for (int i = 0; i < moStrList.size(); i++) {
                    System.out.println(moStrList.get(i));
                }
                System.out.println("=============================================== ");
                System.out.println("MO FlushList END");
                System.out.println("=============================================== ");

                System.out.println("");
                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("FO FlushList Start : " + foStrList.size());
                System.out.println("=============================================== ");
                for (int i = 0; i < foStrList.size(); i++) {
                    System.out.println(foStrList.get(i));
                }
                System.out.println("=============================================== ");
                System.out.println("FO FlushList END");
                System.out.println("=============================================== ");

                System.out.println("");
                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("TO FlushList Start : " + toStrList.size());
                System.out.println("=============================================== ");
                for (int i = 0; i < toStrList.size(); i++) {
                    System.out.println(toStrList.get(i));
                }
                System.out.println("=============================================== ");
                System.out.println("TO FlushList END");
                System.out.println("=============================================== ");



                System.out.println("");
                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("MO FULL Path FlushList Start : " + moStrList.size());
                System.out.println("=============================================== ");
                for (int i = 0; i < moFullStrList.size(); i++) {
                    System.out.println(moFullStrList.get(i));
                }
                System.out.println("=============================================== ");
                System.out.println("MO FULL Path FlushList END");
                System.out.println("=============================================== ");

                System.out.println("");
                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("FO FULL Path FlushList Start : " + foStrList.size());
                System.out.println("=============================================== ");
                for (int i = 0; i < foFullStrList.size(); i++) {
                    System.out.println(foFullStrList.get(i));
                }
                System.out.println("=============================================== ");
                System.out.println("FO FULL Path FlushList END");
                System.out.println("=============================================== ");

                System.out.println("");
                System.out.println("");


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (is != null) {
                        is.close();
                    }

                    // File logOrgFile = new File(cdnFlushFile);
                    // FileUtils.moveFile(logOrgFile, new File(cdnFlushFileBakupFile));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }


        }// for_end



    }


    /**
     * 전체 파일 목록
     */
    public static ArrayList<String> getAllFileList(File dir) {


        ArrayList<File> files = new ArrayList<File>(FileUtils.listFiles(dir, null, false));
        return getFileName(files);
    }


    /**
     * 파일 정보에서 파일명 추출
     * 
     * @param files
     * @return
     */
    private static ArrayList<String> getFileName(ArrayList<File> files) {

        ArrayList<String> fileName = new ArrayList<String>(files.size());

        for (File fi : files) {
            fileName.add(fi.getName());
        }

        return fileName;
    }


    /**
     * 파일 확장자 체크
     * 
     * 이미지 파일인지 확인한다.
     * 
     * @param files
     * @return
     */
    public static boolean fileExtCheck(String fileName) {

        String[] CHECK_FILE_NAME = {"jpg", "png", "gif", "jpeg", "js", "css"};

        if (fileName.indexOf(".") > 0) {
            String fielExt = "";
            fielExt = fileName.substring(fileName.lastIndexOf(".") + 1);

            for (String element : CHECK_FILE_NAME) {
                if (fielExt.toLowerCase().equals(element.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
