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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Data
public class DeployCheckList {

    static final String DEPLOY_UPFILE_LIST = "C:\\deploy/deploy.txt";
    static final String DEPLOY_DIR = "C:\\deploy/";
    static final String DEPLOY_FILE_DIR_FO = DEPLOY_DIR + "upfile/FO/";
    static final String DEPLOY_FILE_DIR_MO = DEPLOY_DIR + "upfile/MO/";
    static final String DEPLOY_FILE_DIR_BO = DEPLOY_DIR + "upfile/BO/";
    static final String DEPLOY_FILE_DIR_TO = DEPLOY_DIR + "upfile/TO/";


    public static void main(String[] args) {

        DeployCheckList.getDeployFileList();
    }


    /**
     * 디플로이 파일 목록
     * 
     * @param files
     * @return
     */
    private static void getDeployFileList() {

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        List<String> newDeployList = null;
        HashSet<String> listSet = null;
        List<String> processedList = null;

        List<String> foStrList = null;
        List<String> moStrList = null;
        List<String> boStrList = null;
        List<String> toStrList = null;
        ArrayList<String> fileList = null;

        List<String> foClassStrList = null;
        List<String> moClassStrList = null;
        List<String> boClassStrList = null;
        List<String> toClassStrList = null;
        List<String> batchClassStrList = null;
        List<String> commonClassStrList = null;

        List<String> foCompilePathList = null;
        List<String> moCompilePathList = null;
        List<String> boCompilePathList = null;
        List<String> toCompilePathList = null;
        List<String> batchCompilePathList = null;


        String foDeployStr = null;
        String moDeployStr = null;
        String boDeployStr = null;
        String toDeployStr = null;
        String classDeployStr = null;

        try {

            File f = new File(DEPLOY_UPFILE_LIST);

            if (f.exists()) {

                String str;
                String[] strArr;
                String deployStr = ""; // deployStr

                newDeployList = new ArrayList<String>();
                is = new FileInputStream(DEPLOY_UPFILE_LIST);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {

                    strArr = str.split(":");

                    if (strArr.length > 1 && strArr[0] != null) {
                        deployStr = strArr[0];
                        // System.out.println("deployStr p1:::");
                    } else {

                        strArr = str.split(" ");
                        if (strArr.length > 2 && strArr[1] != null) {
                            deployStr = strArr[1];
                            // System.out.println("deployStr p2:::");
                        } else {
                            deployStr = str;
                            // System.out.println("deployStr p3:::");
                        }

                    }

                    deployStr = deployStr.replace("/trunk", "");
                    deployStr = deployStr.replace("{", "");
                    deployStr = deployStr.replace("\"", "");
                    deployStr = deployStr.trim();

                    System.out.println("deployStr:::" + deployStr);

                    // if (!fileExtCheck(deployStr)) {
                    newDeployList.add(deployStr);
                    // }
                }// while_end

                System.out.println("newDeployList.size():::" + newDeployList.size());

                listSet = new HashSet<String>(newDeployList);
                processedList = new ArrayList<String>(listSet);

                foStrList = new ArrayList<String>();
                moStrList = new ArrayList<String>();
                boStrList = new ArrayList<String>();
                toStrList = new ArrayList<String>();

                commonClassStrList = new ArrayList<String>();
                foClassStrList = new ArrayList<String>();
                moClassStrList = new ArrayList<String>();
                boClassStrList = new ArrayList<String>();
                toClassStrList = new ArrayList<String>();
                batchClassStrList = new ArrayList<String>();


                foCompilePathList = new ArrayList<String>();
                moCompilePathList = new ArrayList<String>();
                boCompilePathList = new ArrayList<String>();
                toCompilePathList = new ArrayList<String>();
                batchCompilePathList = new ArrayList<String>();



                System.out.println("processedList.size():::" + processedList.size());

                for (int i = 0; i < processedList.size(); i++) {

                    if (processedList.get(i).contains("/03_Front/WebContent")) {
                        foDeployStr = processedList.get(i);
                        foStrList.add(foDeployStr);
                    } else if (processedList.get(i).contains("/04_FrontMobile/WebContent")) {
                        moDeployStr = processedList.get(i);
                        moStrList.add(moDeployStr);
                    } else if (processedList.get(i).contains("/05_BO/WebContent")) {
                        boDeployStr = processedList.get(i);
                        boStrList.add(boDeployStr);
                    } else if (processedList.get(i).contains("/09_Tablet/WebContent")) {
                        toDeployStr = processedList.get(i);
                        toStrList.add(toDeployStr);
                    } else {

                        if (processedList.get(i).contains("/03_Front")) {
                            classDeployStr = processedList.get(i);
                            foClassStrList.add(classDeployStr);
                        } else if (processedList.get(i).contains("/04_FrontMobile")) {
                            classDeployStr = processedList.get(i);
                            moClassStrList.add(classDeployStr);
                        } else if (processedList.get(i).contains("/05_BO")) {
                            classDeployStr = processedList.get(i);
                            boClassStrList.add(classDeployStr);
                        } else if (processedList.get(i).contains("/09_Tablet")) {
                            classDeployStr = processedList.get(i);
                            toClassStrList.add(classDeployStr);
                        } else if (processedList.get(i).contains("/06_Batch")) {
                            classDeployStr = processedList.get(i);
                            batchClassStrList.add(classDeployStr);
                        } else {
                            if (processedList.get(i).contains("/app/domain/fo/")) {
                                classDeployStr = processedList.get(i);
                                foClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/app/domain/mo/")) {
                                classDeployStr = processedList.get(i);
                                moClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/app/domain/bo/")) {
                                classDeployStr = processedList.get(i);
                                boClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/app/domain/tc/")) {
                                classDeployStr = processedList.get(i);
                                toClassStrList.add(classDeployStr);
                            } else {
                                classDeployStr = processedList.get(i);
                                if (StringUtils.isNotEmpty(classDeployStr)) {
                                    commonClassStrList.add(classDeployStr);
                                }
                            }
                        }


                    }

                }// for_end

                if (commonClassStrList.size() > 0) {
                    for (int j = 0; j < commonClassStrList.size(); j++) {
                        foClassStrList.add(commonClassStrList.get(j));
                        moClassStrList.add(commonClassStrList.get(j));
                        boClassStrList.add(commonClassStrList.get(j));
                        toClassStrList.add(commonClassStrList.get(j));
                        batchClassStrList.add(commonClassStrList.get(j));
                    }
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("Common deploy List Start : " + commonClassStrList.size());
                System.out.println("=============================================== ");
                System.out.println("");

                for (int j = 0; j < commonClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(commonClassStrList.get(j)));
                }

                System.out.println("");

                System.out.println("=============================================== ");
                System.out.println("FO deploy List Start : " + (foStrList.size() + foClassStrList.size()));
                System.out.println("=============================================== ");
                System.out.println("");

                for (int j = 0; j < foStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(foStrList.get(j)));
                }
                for (int j = 0; j < foClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(foClassStrList.get(j)));
                }


                for (int j = 0; j < foClassStrList.size(); j++) {
                    if (foClassStrList.get(j).contains(".java")) {
                        String cdDir =
                                getChageWasFullPathFileNamc(foClassStrList.get(j), "Y").substring(0,
                                        getChageWasFullPathFileNamc(foClassStrList.get(j), "Y").lastIndexOf("/") + 1);
                        foCompilePathList.add(cdDir);
                    }
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("MO deploy List Start : " + (moStrList.size() + moClassStrList.size()));
                System.out.println("=============================================== ");
                System.out.println("");

                for (int j = 0; j < moStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(moStrList.get(j)));
                }
                for (int j = 0; j < moClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(moClassStrList.get(j)));
                }

                for (int j = 0; j < moClassStrList.size(); j++) {
                    if (moClassStrList.get(j).contains(".java")) {
                        String cdDir =
                                getChageWasFullPathFileNamc(moClassStrList.get(j), "Y").substring(0,
                                        getChageWasFullPathFileNamc(moClassStrList.get(j), "Y").lastIndexOf("/") + 1);
                        moCompilePathList.add(cdDir);
                    }
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("BO deploy List Start : " + (boStrList.size() + boClassStrList.size()));
                System.out.println("=============================================== ");
                System.out.println("");

                for (int j = 0; j < boStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(boStrList.get(j)));
                }
                for (int j = 0; j < boClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(boClassStrList.get(j)));
                }

                for (int j = 0; j < boClassStrList.size(); j++) {
                    if (boClassStrList.get(j).contains(".java")) {
                        String cdDir =
                                getChageWasFullPathFileNamc(boClassStrList.get(j), "Y").substring(0,
                                        getChageWasFullPathFileNamc(boClassStrList.get(j), "Y").lastIndexOf("/") + 1);
                        boCompilePathList.add(cdDir);
                    }
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("TO deploy List Start : " + (toStrList.size() + toClassStrList.size()));
                System.out.println("=============================================== ");
                System.out.println("");

                for (int j = 0; j < toStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(toStrList.get(j)));
                }
                for (int j = 0; j < toClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(toClassStrList.get(j)));
                }

                for (int j = 0; j < toClassStrList.size(); j++) {
                    if (toClassStrList.get(j).contains(".java")) {
                        String cdDir =
                                getChageWasFullPathFileNamc(toClassStrList.get(j), "Y").substring(0,
                                        getChageWasFullPathFileNamc(toClassStrList.get(j), "Y").lastIndexOf("/") + 1);
                        toCompilePathList.add(cdDir);
                    }
                }


                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("BATCH deploy List Start : " + (batchClassStrList.size()));
                System.out.println("=============================================== ");
                System.out.println("");


                for (int j = 0; j < batchClassStrList.size(); j++) {
                    System.out.println(getChageWasFullPathFileNamc(batchClassStrList.get(j)));
                }

                for (int j = 0; j < batchClassStrList.size(); j++) {
                    if (batchClassStrList.get(j).contains(".java")) {
                        String cdDir =
                                getChageWasFullPathFileNamc(batchClassStrList.get(j), "Y").substring(0,
                                        getChageWasFullPathFileNamc(batchClassStrList.get(j), "Y").lastIndexOf("/") + 1);
                        batchCompilePathList.add(cdDir);
                    }
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("FO foCompilePathList List Start : " + (foCompilePathList.size()));
                System.out.println("=============================================== ");

                listSet = new HashSet<String>(foCompilePathList);
                processedList = new ArrayList<String>(listSet);
                Collections.sort(processedList);
                System.out.println("FO processedList =================" + processedList.size());
                for (int j = 0; j < processedList.size(); j++) {
                    System.out.println(processedList.get(j));
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("MO moCompilePathList List Start : " + (moCompilePathList.size()));
                System.out.println("=============================================== ");

                listSet = new HashSet<String>(moCompilePathList);
                processedList = new ArrayList<String>(listSet);
                Collections.sort(processedList);
                System.out.println("MO processedList =================" + processedList.size());
                for (int j = 0; j < processedList.size(); j++) {
                    System.out.println(processedList.get(j));
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("BO boCompilePathList List Start : " + (boCompilePathList.size()));
                System.out.println("=============================================== ");

                listSet = new HashSet<String>(boCompilePathList);
                processedList = new ArrayList<String>(listSet);
                Collections.sort(processedList);
                System.out.println("BO processedList =================" + processedList.size());
                for (int j = 0; j < processedList.size(); j++) {
                    System.out.println(processedList.get(j));
                }


                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("TO toCompilePathList List Start : " + (toCompilePathList.size()));
                System.out.println("=============================================== ");

                listSet = new HashSet<String>(toCompilePathList);
                processedList = new ArrayList<String>(listSet);
                Collections.sort(processedList);
                System.out.println("TO processedList =================" + processedList.size());
                for (int j = 0; j < processedList.size(); j++) {
                    System.out.println(processedList.get(j));
                }

                System.out.println("");
                System.out.println("=============================================== ");
                System.out.println("BATCH batchCompilePathList List Start : " + (batchCompilePathList.size()));
                System.out.println("=============================================== ");

                listSet = new HashSet<String>(batchCompilePathList);
                processedList = new ArrayList<String>(listSet);
                Collections.sort(processedList);
                System.out.println("BATCH processedList =================" + processedList.size());
                for (int j = 0; j < processedList.size(); j++) {
                    System.out.println(processedList.get(j));
                }

            }
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

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }


    /**
     * 업로드 파일이 모두 있는지 확인
     * 
     */
    public static boolean uploadfileEqaulCheck(List<String> strList, List<String> classStrList, ArrayList<String> fileList) {

        List<String> compList = new ArrayList<String>();
        // String cdDir = "";
        String fileName = "";

        for (int i = 0; i < strList.size(); i++) {
            // cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            fileName = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);
            compList.add(fileName);
        }

        for (int i = 0; i < classStrList.size(); i++) {

            // cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);
            fileName = classStrList.get(i).substring(classStrList.get(i).lastIndexOf("/") + 1);
            compList.add(fileName);
        }

        Collections.sort(compList);
        Collections.sort(fileList);

        return compList.equals(fileList);
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
     * 디플로이 파일을 WAS경로로 변환
     * 
     * @param files
     * @return
     */
    private static String getChageWasFullPathFileNamc(String deployStr) {

        // return getChageWasFullPathFileNamc(deployStr, "Y");
        return getChageWasFullPathFileNamc(deployStr, "N");
    }


    /**
     * 디플로이 파일을 WAS경로로 변환
     * 
     * @param files
     * @return
     */
    private static String getChageWasFullPathFileNamc(String deployStr, String changeYn) {

        if (changeYn.equals("N")) {
            return deployStr;
        }

        String chageDeployStr = "";

        chageDeployStr = deployStr.replace("/03_Front/WebContent", "");
        chageDeployStr = chageDeployStr.replace("/04_FrontMobile/WebContent", "");
        chageDeployStr = chageDeployStr.replace("/05_BO/WebContent", "");

        chageDeployStr = chageDeployStr.replace("/03_Front", "");
        chageDeployStr = chageDeployStr.replace("/04_FrontMobile", "");
        chageDeployStr = chageDeployStr.replace("/05_BO", "");
        chageDeployStr = chageDeployStr.replace("/02_Application", "");
        chageDeployStr = chageDeployStr.replace("/01_Framework", "");


        if (chageDeployStr.contains(".java") || chageDeployStr.contains(".xml") || chageDeployStr.contains("/src/main/resources")
                || chageDeployStr.contains("/src/main/java")) {


            chageDeployStr = chageDeployStr.replace("/src/main/resources", "");
            chageDeployStr = chageDeployStr.replace("/src/main/java", "");

            // java 파일일 경우 확장자를 class로 변경
            // chageDeployStr = chageDeployStr.replace(".java", ".class");

            if (chageDeployStr.contains("/06_Batch")) {
                chageDeployStr = chageDeployStr.replace("/06_Batch", "");
                chageDeployStr = "/classes" + chageDeployStr;
            } else {
                chageDeployStr = "/WEB-INF/classes" + chageDeployStr;
            }
        }

        return chageDeployStr;
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
