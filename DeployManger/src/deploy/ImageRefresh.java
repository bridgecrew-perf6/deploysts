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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import utils.DateUtils;
import utils.SFTPService;

@Slf4j
@Data
public class ImageRefresh {

    static final String DEPLOY_DIR = "/data/home/hisis/ec/tools/deploy/";
    static final String DEPLOY_UPFILE_LIST = "/data/home/hisis/ec/tools/deploy/deploy.txt";
    static final String DEPLOY_ALL_UPFILE_LIST = "/data/home/hisis/ec/tools/deploy/deploy.all.txt";
    static final String DEPLOY_STAGING_UPFILE_LIST = "/data/home/hisis/ec/tools/deploy/deploy_staging.txt";


    static final String DEPLOY_FILE_DIR = DEPLOY_DIR + "upfile";
    static final String JEUS_HOME = "/data/webapps/JEUS";
    static final String RUN_JAVA_COMPILE_SHELL = "sh  /NAS-EC_PRD/tools/javacompile/run_java_compile.sh";
    // static final String RUN_JAVA_COMPILE_NOLOG_SHELL = "sh  /NAS-EC_PRD/tools/javacompile/run_java_compile_nolog.sh";
    static final String JAVA_COMPILE_SERVER_PATH = "/NAS-EC_PRD/tools/javacompile/";
    static final String REFRESH_CONTENTS_SHELL = "sh /NAS-EC_PRD/tools/refresh_contents.sh";



    static List<String> COMP_ERR_LIST = new ArrayList<String>();

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};

    static boolean IS_DEPLOY_SUCCESS = true;


    public ImageRefresh(Properties props, String deploySystemOrg, String spareDeployYn, String spareOnlyYn) {

        List<String> viewStrList = null;
        List<String> imageStrList = null;

        List<String> classStrList = null;
        List<String> commonClassStrList = null;

        boolean isStaging = false;



        String deployFileDir = DEPLOY_FILE_DIR;
        String deployFileBackDir = DEPLOY_DIR + "backup";
        Map<String, Object> returnMap = null;

        String deploySystem = "";
        String deploySystemFtp = "";
        String deployL4 = "";

        String[] setWasArr = null;
        String[] setSpareWasArr = null;
        SFTPService ftp = null;

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // WAS서버 프로퍼티 셋팅
        propServerSet(props);

        if (deploySystemOrg.equals("FO_SET_01") || deploySystemOrg.equals("FO_IMAGE1")) {
            setWasArr = FO_SET_01;
            setSpareWasArr = SPARE_FO_SET_01;
        } else if (deploySystemOrg.equals("FO_SET_02") || deploySystemOrg.equals("FO_IMAGE2")) {
            setWasArr = FO_SET_02;
            setSpareWasArr = SPARE_FO_SET_02;
        } else if (deploySystemOrg.equals("MO_SET_01") || deploySystemOrg.equals("MO_IMAGE1")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01;
        } else if (deploySystemOrg.equals("MO_SET_02") || deploySystemOrg.equals("MO_IMAGE2")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02;
        }

        if ("Y".equals(spareOnlyYn)) {
            setWasArr = new String[] {};
        }

        deploySystemFtp = deploySystemOrg;
        deploySystem = removeRex("[0-9]", deploySystemOrg);

        System.out.println("deploySystemOrg ::" + deploySystemOrg);
        System.out.println("deploySystem ::" + deploySystem);
        System.out.println("deploySystemFtp ::" + deploySystemFtp);

        deployFtpIp = props.getProperty(deploySystemFtp + ".ftp.ip");
        deployFtpId = props.getProperty(deploySystemFtp + ".ftp.id");
        deployFtpPw = props.getProperty(deploySystemFtp + ".ftp.pw");
        deployFtpPath = props.getProperty(deploySystemFtp + ".ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        refreshContentsCompileCommandExec(deploySystemFtp, props, true);


        // for (String element : setWasArr) {
        //
        // deploySystemFtp = element;
        // deploySystem = removeRex("[0-9]", deploySystemFtp);
        //
        // // 웹서버 업로드 공통
        // setDeployWebCommon(viewStrList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deploySystemFtp);
        //
        // refreshContentsCompileCommandExec(deploySystemFtp, props, false);
        // }
        //
        //
        // // 예비서버 WEB파일 업로드
        // if ("Y".equals(spareDeployYn)) {
        //
        // for (String element_spare : setSpareWasArr) {
        //
        // deploySystemFtp = element_spare;
        // deploySystem = removeRex("[0-9]", deploySystemFtp);
        //
        // // 웹서버 업로드 공통
        // setDeployWebCommon(viewStrList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deploySystemFtp);
        //
        // refreshContentsCompileCommandExec(deploySystemFtp, props, false);
        // }
        // }


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


    /**
     * Spare Server Deploy
     */
    public static void setDeployCommon_bak(List<String> viewStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deploySystemFtp, SFTPService ftp, String deployFtpPath) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp ::" + deploySystemFtp);
        System.out.println("deploySystem ::" + deploySystem);
        System.out.println("###############################################");
    }


    /**
     * setDeployCommon
     */
    public static void setDeployCommon(List<String> viewStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deploySystemFtp, SFTPService ftp, String deployFtpPath) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon START::");
        System.out.println("###############################################");
        // jeusShutDownCommon
        // 소스백업
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드 및 컴파일
        deployFileUpload(viewStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deploySystem, deploySystemFtp,
                props);


        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon END::");
        System.out.println("###############################################");
    }


    /**
     * setDeployCommon
     */
    public static void setDeployCommonByImageCheck(List<String> viewStrList, List<String> classStrList, Properties props,
            String deployFileDir, String deploySystem, List<String> commonClassStrList, String deploySystemFtp, SFTPService ftp,
            String deployFtpPath) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon START::");
        System.out.println("###############################################");

        String cdDir = "";
        String fileNm = "";
        boolean isImageUpload = false;
        boolean isStrUpload = false;

        if (viewStrList.size() > 0) {
            for (int i = 0; i < viewStrList.size(); i++) {
                cdDir = viewStrList.get(i).substring(0, viewStrList.get(i).lastIndexOf("/") + 1);
                fileNm = viewStrList.get(i).substring(viewStrList.get(i).lastIndexOf("/") + 1);

                if (fileExtCheckByBo(fileNm)) {
                    // 웹서버로 업로드
                    isImageUpload = true;
                } else {
                    isStrUpload = true;
                }
            }
        }

        if (isStrUpload || classStrList.size() > 0) {

            // jeusShutDownCommon
            // 소스백업
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드 및 컴파일
            deployFileUploadByStaging(viewStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deploySystem,
                    deploySystemFtp, props);


            if (classStrList != null && classStrList.size() > 0) {
                // 컴파일 로그 삭제
                delCompileLogFile(deploySystem, ftp);
            }

            // jeusBootCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        }

        if (isImageUpload) {
            // 웹서버 업로드 공통
            setDeployWebCommonByStaging(viewStrList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deploySystemFtp);

            // if (deploySystemFtp.equals("FO_STAGING") || deploySystemFtp.equals("MO_STAGING")) {
            //
            // String deployFtpIp = props.getProperty(deploySystemFtp + "_IMAGE.ftp.ip");
            // String deployFtpId = props.getProperty(deploySystemFtp + "_IMAGE.ftp.id");
            // String deployFtpPw = props.getProperty(deploySystemFtp + "_IMAGE.ftp.pw");
            // deployFtpPath = props.getProperty(deploySystemFtp + "_IMAGE.ftp.path");
            //
            // SFTPService ftp_image = new SFTPService(deployFtpIp, 22, deployFtpId, deployFtpPw);
            //
            // sourceBakupCommandExec(props, deploySystemFtp, deploySystem);
            //
            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(3 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // /**
            // * 파일 업로드
            // **************************************************/
            // deployFileUploadByStaging(viewStrList, classStrList, ftp_image, deployFtpPath, deployFileDir, commonClassStrList,
            // deploySystem, deploySystemFtp, props);
            //
            // }// FO, MO STAGING만 이미지서버 처리 if_end

        }// isImageUpload if_end

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon END::");
        System.out.println("###############################################");
    }


    /**
     * WEB 서버 소스 업로드
     */
    public static void setDeployWebCommon(List<String> viewStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deploySystemFtp) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] WEB Server DeployCommon START::");
        System.out.println("###############################################");

        String deployFtpIp = props.getProperty(deploySystemFtp + ".web.ip");
        int deployFtpPort = 22;
        String deployFtpId = props.getProperty(deploySystemFtp + ".web.id");
        String deployFtpPw = props.getProperty(deploySystemFtp + ".web.pw");
        String deployFtpPath = props.getProperty(deploySystemFtp + ".web.path");

        SFTPService ftp_web = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 소스백업
        webSourceBakupCommandExec(props, deploySystem, ftp_web);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(viewStrList, classStrList, ftp_web, deployFtpPath, deployFileDir, commonClassStrList, deploySystem,
                deploySystemFtp, props);

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] WEB Server DeployCommon END::");
        System.out.println("###############################################");
    }


    /**
     * WEB 서버 소스 업로드 By Staging
     */
    public static void setDeployWebCommonByStaging(List<String> viewStrList, List<String> classStrList, Properties props,
            String deployFileDir, String deploySystem, List<String> commonClassStrList, String deploySystemFtp) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] WEB Server DeployCommon START::");
        System.out.println("###############################################");

        String deployFtpIp = props.getProperty(deploySystemFtp + ".web.ip");
        int deployFtpPort = 22;
        String deployFtpId = props.getProperty(deploySystemFtp + ".web.id");
        String deployFtpPw = props.getProperty(deploySystemFtp + ".web.pw");
        String deployFtpPath = props.getProperty(deploySystemFtp + ".web.path");

        SFTPService ftp_web = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 소스백업
        webSourceBakupCommandExec(props, deploySystem, ftp_web);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUploadForWeb(viewStrList, classStrList, ftp_web, deployFtpPath, deployFileDir, commonClassStrList, deploySystem,
                deploySystemFtp, props);

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] WEB Server DeployCommon END::");
        System.out.println("###############################################");
    }


    /**
     * 
     * 
     * @param files
     * @return
     */
    private static Map<String, Object> getDeployFileListMap(String deploySystem, String deployL4, String spareOnlyYn, boolean isStaging) {

        Map<String, Object> returnMap = new HashMap<String, Object>();

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

        String foDeployStr = null;
        String moDeployStr = null;
        String boDeployStr = null;
        String toDeployStr = null;
        String classDeployStr = null;

        List<String> commonClassStrList = null;
        List<String> foClassStrList = null;
        List<String> moClassStrList = null;
        List<String> boClassStrList = null;
        List<String> toClassStrList = null;
        List<String> batchClassStrList = null;

        String deployUpfileList = DEPLOY_UPFILE_LIST;

        if ("Y".equals(spareOnlyYn)) {
            deployUpfileList = DEPLOY_ALL_UPFILE_LIST;
        }

        if (deploySystem.equals("BO")) {
            deployUpfileList = DEPLOY_ALL_UPFILE_LIST;
        }

        if (deployL4.equals("02")) {
            deployUpfileList = DEPLOY_ALL_UPFILE_LIST;
        }

        if (isStaging) {
            deployUpfileList = DEPLOY_STAGING_UPFILE_LIST;
        }

        try {

            File f = new File(deployUpfileList);

            if (f.exists()) {

                String str;
                String[] strArr;
                String deployStr; // deployStr

                newDeployList = new ArrayList<String>();
                is = new FileInputStream(deployUpfileList);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);


                System.out.println("============================================================================================");
                System.out.println("# Deploy File List ");
                System.out.println("");

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

                    System.out.println(" " + deployStr);

                    deployStr = deployStr.replace("/trunk", "");
                    deployStr = deployStr.replace("{", "");
                    deployStr = deployStr.replace("\"", "");
                    deployStr = deployStr.trim();
                    if (deploySystem.equals("TO") || deploySystem.equals("BO") || deploySystem.equals("PO") || deploySystem.equals("CC")) {

                        newDeployList.add(deployStr);

                    } else {

                        if (deploySystem.contains("IMAGE")) {
                            if (fileExtCheck(deployStr)) {
                                newDeployList.add(deployStr);
                            }
                        } else {
                            if (!fileExtCheck(deployStr)) {
                                newDeployList.add(deployStr);
                            }
                        }

                    }

                }// while_end
                System.out.println("");
                System.out.println("============================================================================================");

                // System.out.println("newDeployList.size():::" + newDeployList.size());

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

                // System.out.println("processedList.size():::" + processedList.size());

                // SVN 파일 다운로드
                getSvnFile(processedList);

                for (int i = 0; i < processedList.size(); i++) {

                    if (StringUtils.isNotEmpty(processedList.get(i))) {

                        if (processedList.get(i).contains("/03_Front/WebContent")) {
                            foDeployStr = processedList.get(i);
                            // foDeployStr = getChageWasFullPathFileName(foDeployStr);

                            foStrList.add(foDeployStr);
                        } else if (processedList.get(i).contains("/04_FrontMobile/WebContent")) {
                            moDeployStr = processedList.get(i);
                            // moDeployStr = getChageWasFullPathFileName(moDeployStr);

                            moStrList.add(moDeployStr);
                        } else if (processedList.get(i).contains("/05_BO/WebContent")) {
                            boDeployStr = processedList.get(i);
                            // boDeployStr = getChageWasFullPathFileName(boDeployStr);

                            boStrList.add(boDeployStr);
                        } else if (processedList.get(i).contains("/09_Tablet/WebContent")) {
                            toDeployStr = processedList.get(i);
                            // toDeployStr = getChageWasFullPathFileName(toDeployStr);

                            toStrList.add(toDeployStr);
                        } else {

                            if (processedList.get(i).contains("/03_Front")) {
                                classDeployStr = processedList.get(i);
                                // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                foClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/04_FrontMobile")) {
                                classDeployStr = processedList.get(i);
                                // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                moClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/05_BO")) {
                                classDeployStr = processedList.get(i);
                                // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                boClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/09_Tablet")) {
                                classDeployStr = processedList.get(i);
                                // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                toClassStrList.add(classDeployStr);
                            } else if (processedList.get(i).contains("/06_Batch")) {
                                classDeployStr = processedList.get(i);
                                // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                batchClassStrList.add(classDeployStr);
                            } else {

                                classDeployStr = processedList.get(i);
                                if (StringUtils.isNotEmpty(classDeployStr)) {
                                    // classDeployStr = getChageWasFullPathFileName(classDeployStr);

                                    commonClassStrList.add(classDeployStr);

                                }

                                /*
                                 * if (processedList.get(i).contains("/app/domain/fo/")) { classDeployStr = processedList.get(i);
                                 * classDeployStr = getChageWasFullPathFileName(classDeployStr);
                                 * 
                                 * foClassStrList.add(classDeployStr);
                                 * 
                                 * } else if (processedList.get(i).contains("/app/domain/mo/")) { classDeployStr = processedList.get(i);
                                 * classDeployStr = getChageWasFullPathFileName(classDeployStr);
                                 * 
                                 * moClassStrList.add(classDeployStr);
                                 * 
                                 * } else if (processedList.get(i).contains("/app/domain/bo/")) { classDeployStr = processedList.get(i);
                                 * classDeployStr = getChageWasFullPathFileName(classDeployStr);
                                 * 
                                 * boClassStrList.add(classDeployStr);
                                 * 
                                 * } else if (processedList.get(i).contains("/app/domain/tc/")) { classDeployStr = processedList.get(i);
                                 * classDeployStr = getChageWasFullPathFileName(classDeployStr);
                                 * 
                                 * toClassStrList.add(classDeployStr);
                                 * 
                                 * } else { classDeployStr = processedList.get(i); if (StringUtils.isNotEmpty(classDeployStr)) {
                                 * classDeployStr = getChageWasFullPathFileName(classDeployStr);
                                 * 
                                 * commonClassStrList.add(classDeployStr);
                                 * 
                                 * } }
                                 */
                            }
                        }

                    }// isNotEmpty check if_end

                }// for_end

                if (commonClassStrList.size() > 0) {
                    for (int j = 0; j < commonClassStrList.size(); j++) {
                        foClassStrList.add(commonClassStrList.get(j));
                        moClassStrList.add(commonClassStrList.get(j));
                        boClassStrList.add(commonClassStrList.get(j));
                        toClassStrList.add(commonClassStrList.get(j));
                        batchClassStrList.add(commonClassStrList.get(j));
                        // batchClassStrList.add(commonClassStrList.get(j).replace("/WEB-INF", ""));
                    }
                }

                // System.out.println("foClassStrList size :: " + foClassStrList.size());
                // System.out.println("moClassStrList size :: " + moClassStrList.size());
                // System.out.println("boClassStrList size :: " + moClassStrList.size());
                // System.out.println("toClassStrList size :: " + toClassStrList.size());
                // System.out.println("batchClassStrList size :: " + batchClassStrList.size());


                try {
                    // System.out.println("sleep start::");
                    Thread.sleep(3 * 1000);
                    // System.out.println("sleep end::");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                returnMap.put("foStrList", foStrList);
                returnMap.put("moStrList", moStrList);
                returnMap.put("boStrList", boStrList);
                returnMap.put("toStrList", toStrList);

                returnMap.put("foClassStrList", foClassStrList);
                returnMap.put("moClassStrList", moClassStrList);
                returnMap.put("boClassStrList", boClassStrList);
                returnMap.put("toClassStrList", toClassStrList);
                returnMap.put("batchClassStrList", batchClassStrList);
                returnMap.put("commonClassStrList", commonClassStrList);

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


        return returnMap;
    }


    /**
     * 전체 파일 목록
     */
    public static ArrayList<String> getAllFileList(File dir) {

        ArrayList<File> files = new ArrayList<File>(FileUtils.listFiles(dir, null, true));
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
            fileName.add(fi.getPath().replace(DEPLOY_FILE_DIR, ""));
        }

        return fileName;
    }



    /**
     * 프로퍼티 로딩
     * 
     * @param configFile
     * @return
     */
    private static Properties loadConfig(String configFile) {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(configFile)));
            return props;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 업로드 파일이 모두 있는지 확인
     * 
     */
    public static boolean uploadfileEqaulCheck(List<String> strList, List<String> classStrList, ArrayList<String> fileList) {

        // return true;

        List<String> compList = new ArrayList<String>();
        // String cdDir = "";
        String fileName = "";

        for (int i = 0; i < strList.size(); i++) {
            // cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            // fileName = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);

            System.out.println("strList ::: " + strList.get(i));

            compList.add(strList.get(i));
        }

        for (int i = 0; i < classStrList.size(); i++) {

            // cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);
            // fileName = classStrList.get(i).substring(classStrList.get(i).lastIndexOf("/") + 1);

            System.out.println("classStrList ::: " + classStrList.get(i));

            compList.add(classStrList.get(i));
        }

        Collections.sort(compList);
        Collections.sort(fileList);

        for (int i = 0; i < compList.size(); i++) {
            System.out.println("compList ::" + compList.get(i));
        }

        for (int i = 0; i < fileList.size(); i++) {
            System.out.println("fileList ::" + fileList.get(i));
        }

        if (fileList.equals(compList)) {
            return true;
        } else {
            return fileList.containsAll(compList);
        }


    }



    /**
     * 업로드 파일이 모두 있는지 확인_공통
     * 
     */
    public static boolean uploadfileEqaulCheckCommon(String deploySystem, Map<String, Object> returnMap) {

        File deployFileDirList = new File(DEPLOY_FILE_DIR);
        ArrayList<String> fileList = getAllFileList(deployFileDirList);

        if (fileList.size() > 0) {

            List<String> strList = new ArrayList<String>();
            List<String> classStrList = new ArrayList<String>();

            if (deploySystem.contains("FO")) {
                strList = (List<String>) returnMap.get("foStrList");
                classStrList = (List<String>) returnMap.get("foClassStrList");

            } else if (deploySystem.contains("MO")) {
                strList = (List<String>) returnMap.get("moStrList");
                classStrList = (List<String>) returnMap.get("moClassStrList");

            } else if (deploySystem.equals("BO") || deploySystem.equals("PO") || deploySystem.equals("CC")) {
                strList = (List<String>) returnMap.get("boStrList");
                classStrList = (List<String>) returnMap.get("boClassStrList");

            } else if (deploySystem.equals("TO")) {
                strList = (List<String>) returnMap.get("toStrList");
                classStrList = (List<String>) returnMap.get("toClassStrList");

            } else if (deploySystem.equals("BATCH")) {
                classStrList = (List<String>) returnMap.get("batchClassStrList");

            }

            List<String> uploadFileList = new ArrayList<String>();
            // String cdDir = "";
            String fileName = "";

            for (int i = 0; i < strList.size(); i++) {

                // System.out.println("strList ::: " + strList.get(i));

                uploadFileList.add(strList.get(i));
            }

            for (int i = 0; i < classStrList.size(); i++) {

                // cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);
                // fileName = classStrList.get(i).substring(classStrList.get(i).lastIndexOf("/") + 1);

                // System.out.println("classStrList ::: " + classStrList.get(i));

                uploadFileList.add(classStrList.get(i));
            }

            Collections.sort(uploadFileList);
            Collections.sort(fileList);

            System.out.println("==================================================================================================");
            for (int i = 0; i < uploadFileList.size(); i++) {
                System.out.println("uploadFileList ::" + uploadFileList.get(i));
            }
            System.out.println("==================================================================================================");

            // for (int i = 0; i < fileList.size(); i++) {
            // System.out.println("fileList ::" + fileList.get(i));
            // }
            //
            // System.out.println("fileList.containsAll(uploadFileList) ::" + fileList.containsAll(uploadFileList));

            if (fileList.equals(uploadFileList)) {
                return true;
            } else {
                return fileList.containsAll(uploadFileList);
            }

        } else {

            return false;
        }

    }


    /**
     * 기존 파일 백업 다운
     * 
     */
    public static boolean fileBakDownLoad(List<String> strList, List<String> classStrList, SFTPService ftp, String deployFtpPath,
            String deployFileBackDir) {

        boolean isFileBakDown = false;
        File bakFile = null;
        String fileNm = "";
        String cdDir = "";
        for (int i = 0; i < strList.size(); i++) {
            cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            fileNm = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);
            bakFile = new File(deployFileBackDir + "/" + cdDir + "/" + fileNm);

            System.out.println("deployFileBackDir ::" + deployFileBackDir);
            if (!bakFile.exists()) {
                makeDiretory(deployFileBackDir + "/" + cdDir);
                ftp.downloadByDeploy(getChageWasFullPathFileName(strList.get(i)), deployFileBackDir + "/" + cdDir, deployFtpPath);
            }
        }

        String classStr = "";
        for (int i = 0; i < classStrList.size(); i++) {
            classStr = classStrList.get(i);

            if (classStr.contains(".java")) {
                classStr = classStr.replace(".java", ".class");
            }
            cdDir = classStr.substring(0, classStr.lastIndexOf("/") + 1);
            fileNm = classStr.substring(classStr.lastIndexOf("/") + 1);
            bakFile = new File(deployFileBackDir + "/" + cdDir + "/" + fileNm);
            if (!bakFile.exists()) {
                makeDiretory(deployFileBackDir + "/" + cdDir);
                ftp.downloadByDeploy(getChageWasFullPathFileName(classStr), deployFileBackDir + "/" + cdDir, deployFtpPath);
            }


        }

        isFileBakDown = true;

        return isFileBakDown;
    }



    /**
     * 신규 파일 업로드
     * 
     */
    public static boolean deployFileUpload(List<String> strList, List<String> classStrList, SFTPService ftp, String deployFtpPath,
            String deployFileDir, List<String> commonClassStrList, String deploySystem, String deploySystemFtp, Properties props) {

        boolean isFileUpladSucess = false;

        String cdDir = "";
        String fileNm = "";

        String web_deployFtpIp = "";
        int web_deployFtpPort = 22;
        String web_deployFtpId = "";
        String web_deployFtpPw = "";
        String web_deployFtpPath = "";
        SFTPService web_ftp = null;

        if (deploySystem.equals("TO") || deploySystem.equals("BO") || deploySystem.equals("PO") || deploySystem.equals("CC")) {
            if (deploySystemFtp.contains("TC")) {
                deploySystemFtp = deploySystemFtp.replace("TC", "TO");
            }
            System.out.println("deploySystemFtp ::" + deploySystemFtp);
            web_deployFtpIp = props.getProperty(deploySystemFtp + ".web.ip");
            web_deployFtpId = props.getProperty(deploySystemFtp + ".web.id");
            web_deployFtpPw = props.getProperty(deploySystemFtp + ".web.pw");
            web_deployFtpPath = props.getProperty(deploySystemFtp + ".web.path");

            web_ftp = new SFTPService(web_deployFtpIp, web_deployFtpPort, web_deployFtpId, web_deployFtpPw);
        }


        for (int i = 0; i < strList.size(); i++) {
            cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            fileNm = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);

            System.out.println("cdDir ::" + cdDir);
            System.out.println("deployFileDir + cdDir + fileNm ::" + deployFileDir + cdDir + fileNm);
            System.out
                    .println("deployFtpPath + getChageWasFullPathFileName(cdDir) ::" + deployFtpPath + getChageWasFullPathFileName(cdDir));


            if (deploySystem.equals("TO") || deploySystem.equals("BO") || deploySystem.equals("PO") || deploySystem.equals("CC")) {
                System.out.println(" 웹서버로 업로드 ===::" + cdDir);
                System.out.println(" 웹서버로 업로드 fileExtCheckByBo(fileNm) ===::" + fileExtCheckByBo(fileNm));
                System.out.println(" 웹서버로 업로드 web_deployFtpPath ===::" + web_deployFtpPath);
                if (fileExtCheckByBo(fileNm)) {
                    // 웹서버로 업로드
                    web_ftp.upload(deployFileDir + cdDir + fileNm, web_deployFtpPath + getChageWasFullPathFileName(cdDir));
                } else {
                    ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
                }
            } else {
                ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
            }

        }

        String wasFullPathName = "";

        for (int i = 0; i < classStrList.size(); i++) {
            cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);
            fileNm = classStrList.get(i).substring(classStrList.get(i).lastIndexOf("/") + 1);

            wasFullPathName = getChageWasFullPathFileName(cdDir);

            if (deploySystem.equals("BATCH")) {
                wasFullPathName = wasFullPathName.replace("/WEB-INF", "");
            }

            System.out.println("cdDir ::" + cdDir);
            System.out.println("deployFileDir + cdDir + fileNm ::" + deployFileDir + cdDir + fileNm);
            System.out
                    .println("deployFtpPath + getChageWasFullPathFileName(cdDir) ::" + deployFtpPath + getChageWasFullPathFileName(cdDir));


            ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + wasFullPathName);
        }


        // 공통 java 컴파일
        // compileCommon(commonClassStrList, ftp, deployFtpPath, deploySystem);

        // java 컴파일
        // isFileUpladSucess = compileCommon(classStrList, ftp, deployFtpPath, deploySystem);
        if (classStrList.size() > 0) {
            isFileUpladSucess = compileCommonWithShellScript(classStrList, ftp, deployFtpPath, deploySystem);
        } else {
            isFileUpladSucess = true;
        }


        // JAVA 파일 삭제
        // String sshCommand = "";
        // for (int i = 0; i < classStrList.size(); i++) {
        // if (classStrList.get(i).contains(".java")) {
        // sshCommand = "rm " + deployFtpPath + classStrList.get(i);
        // ftp.sshCommandExec(sshCommand);
        // }
        // }

        System.out.println("isFileUpladSucess ::" + isFileUpladSucess);

        // if (!isFileUpladSucess) {
        //
        // // 소스 최종버전으로 원복처리
        // Recover recover = new Recover();
        // recover.setRecoverCommon(props, deploySystem, deploySystemFtp);
        //
        // throw new RuntimeException("Build Fail !!!!!!!!!!!!!!!!!!!!!!!");
        // }

        if (!isFileUpladSucess) {
            IS_DEPLOY_SUCCESS = false;
        }

        return isFileUpladSucess;
    }


    /**
     * STAGING 신규 파일 업로드
     * 
     */
    public static boolean deployFileUploadByStaging(List<String> strList, List<String> classStrList, SFTPService ftp, String deployFtpPath,
            String deployFileDir, List<String> commonClassStrList, String deploySystem, String deploySystemFtp, Properties props) {

        boolean isFileUpladSucess = false;

        String cdDir = "";
        String fileNm = "";


        for (int i = 0; i < strList.size(); i++) {
            cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            fileNm = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);

            System.out.println("cdDir ::" + cdDir);
            System.out.println("deployFileDir + cdDir + fileNm ::" + deployFileDir + cdDir + fileNm);
            System.out
                    .println("deployFtpPath + getChageWasFullPathFileName(cdDir) ::" + deployFtpPath + getChageWasFullPathFileName(cdDir));

            if (deploySystem.contains("IMAGE")) {
                if (fileExtCheck(fileNm)) {
                    ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
                }
            } else {
                ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
            }

        }

        String wasFullPathName = "";

        for (int i = 0; i < classStrList.size(); i++) {
            cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);
            fileNm = classStrList.get(i).substring(classStrList.get(i).lastIndexOf("/") + 1);

            wasFullPathName = getChageWasFullPathFileName(cdDir);

            if (deploySystem.equals("BATCH")) {
                wasFullPathName = wasFullPathName.replace("/WEB-INF", "");
            }

            System.out.println("cdDir ::" + cdDir);
            System.out.println("deployFileDir + cdDir + fileNm ::" + deployFileDir + cdDir + fileNm);
            System.out
                    .println("deployFtpPath + getChageWasFullPathFileName(cdDir) ::" + deployFtpPath + getChageWasFullPathFileName(cdDir));


            ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + wasFullPathName);
        }


        // 공통 java 컴파일
        // compileCommon(commonClassStrList, ftp, deployFtpPath, deploySystem);

        // java 컴파일
        // isFileUpladSucess = compileCommon(classStrList, ftp, deployFtpPath, deploySystem);
        if (classStrList.size() > 0) {
            isFileUpladSucess = compileCommonWithShellScript(classStrList, ftp, deployFtpPath, deploySystem);
        } else {
            isFileUpladSucess = true;
        }


        // JAVA 파일 삭제
        // String sshCommand = "";
        // for (int i = 0; i < classStrList.size(); i++) {
        // if (classStrList.get(i).contains(".java")) {
        // sshCommand = "rm " + deployFtpPath + classStrList.get(i);
        // ftp.sshCommandExec(sshCommand);
        // }
        // }

        System.out.println("isFileUpladSucess ::" + isFileUpladSucess);

        // if (!isFileUpladSucess) {
        //
        // // 소스 최종버전으로 원복처리
        // Recover recover = new Recover();
        // recover.setRecoverCommon(props, deploySystem, deploySystemFtp);
        //
        // throw new RuntimeException("Build Fail !!!!!!!!!!!!!!!!!!!!!!!");
        // }

        if (!isFileUpladSucess) {
            IS_DEPLOY_SUCCESS = false;
        }

        return isFileUpladSucess;
    }


    /**
     * Web서버 신규 파일 업로드
     * 
     */
    public static boolean deployFileUploadForWeb(List<String> strList, List<String> classStrList, SFTPService ftp, String deployFtpPath,
            String deployFileDir, List<String> commonClassStrList, String deploySystem, String deploySystemFtp, Properties props) {

        boolean isFileUpladSucess = false;

        String cdDir = "";
        String fileNm = "";


        for (int i = 0; i < strList.size(); i++) {
            cdDir = strList.get(i).substring(0, strList.get(i).lastIndexOf("/") + 1);
            fileNm = strList.get(i).substring(strList.get(i).lastIndexOf("/") + 1);

            System.out.println("cdDir ::" + cdDir);
            System.out.println("deployFileDir + cdDir + fileNm ::" + deployFileDir + cdDir + fileNm);
            System.out
                    .println("deployFtpPath + getChageWasFullPathFileName(cdDir) ::" + deployFtpPath + getChageWasFullPathFileName(cdDir));

            if (fileExtCheckByBo(fileNm)) {
                ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
            }

        }

        isFileUpladSucess = true;

        return isFileUpladSucess;
    }


    /**
     * java 파일 컴파일
     * 
     */
    public static void compileCommon(List<String> classStrList, SFTPService ftp, String deployFtpPath) {

        compileCommon(classStrList, ftp, deployFtpPath, "");
    }


    /**
     * java 파일 컴파일
     * 
     */
    public static void compileCommon(List<String> classStrList, SFTPService ftp, String deployFtpPath, String deploySystem) {

        String cdDir = "";
        String sshCommand = "";

        HashSet<String> listSet = null;
        List<String> processedList = null;

        List<String> compilePathList = new ArrayList<String>();


        for (int i = 0; i < classStrList.size(); i++) {
            if (classStrList.get(i).contains(".java")) {
                cdDir = classStrList.get(i).substring(0, classStrList.get(i).lastIndexOf("/") + 1);

                if (deploySystem.equals("BATCH")) {
                    cdDir = cdDir.replace("/WEB-INF", "");
                }
                compilePathList.add(cdDir);
            }
        }

        // 중복제거 및 정렬
        listSet = new HashSet<String>(compilePathList);
        processedList = new ArrayList<String>(listSet);
        Collections.sort(processedList);


        // 컴파일 실패가 있을 수 있으니 3번 컴파일 진행
        for (int k = 0; k < 2; k++) {

            for (int i = 0; i < processedList.size(); i++) {

                sshCommand =
                        "cd " + deployFtpPath + processedList.get(i) + ";" + "javac -g -encoding utf-8 -classpath " + deployFtpPath
                                + "/WEB-INF/classes:" + "/data/webapps/JEUS/lib/system/*:" + deployFtpPath + "/WEB-INF/lib/* *.java";

                if (deploySystem.equals("BATCH")) {
                    sshCommand =
                            "cd " + deployFtpPath + processedList.get(i) + ";" + "javac -g -encoding utf-8 -classpath " + deployFtpPath
                                    + "/classes:" + "/data/webapps/JEUS/lib/system/*:" + deployFtpPath + "/lib/* *.java";
                }


                System.out.println("javac sshCommand ::" + sshCommand);
                ftp.sshCommandExec(sshCommand);



                try {
                    // System.out.println("sleep start::");
                    Thread.sleep(5 * 1000);
                    // System.out.println("sleep end::");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    }


    /**
     * java 파일 컴파일
     * 
     */
    public static boolean compileCommonWithShellScriptBak(List<String> classStrList, SFTPService ftp, String deployFtpPath,
            String deploySystem) {

        boolean compResult = false;


        for (int i = 0; i < 3; i++) {

            System.out.println("#############################################");
            System.out.println("compileCommonWithShellScript ::: " + i);
            System.out.println("#############################################");
            compResult = compileCommonProcess(classStrList, ftp, deployFtpPath, deploySystem);
            if (compResult) {
                break;
            }
        }

        return compResult;

    }


    /**
     * java 파일 컴파일
     * 
     */
    public static boolean compileCommonWithShellScript(List<String> classStrList, SFTPService ftp, String deployFtpPath, String deploySystem) {

        boolean compResult = true;
        String cdDir = "";
        String sshCommand = "";

        HashSet<String> listSet = null;
        List<String> processedList = null;

        List<String> compilePathList = new ArrayList<String>();


        String wasFullPathStr = "";

        for (int i = 0; i < classStrList.size(); i++) {
            wasFullPathStr = getChageWasFullPathFileName(classStrList.get(i));
            if (wasFullPathStr.contains(".java")) {
                cdDir = wasFullPathStr.substring(0, wasFullPathStr.lastIndexOf("/") + 1);

                if (deploySystem.equals("BATCH")) {
                    cdDir = cdDir.replace("/WEB-INF", "");
                }
                compilePathList.add(cdDir);
            }
        }

        if (compilePathList.size() == 0) {
            return true;
        }

        // 중복제거 및 정렬
        listSet = new HashSet<String>(compilePathList);
        processedList = new ArrayList<String>(listSet);
        Collections.sort(processedList);

        compResult = compileCommonProcess(processedList, ftp, deployFtpPath, deploySystem);

        if (!compResult) {
            for (int i = 1; i < 4; i++) {

                System.out.println("#############################################");
                System.out.println("컴파일 실패시 재컴파일 시도 ::: " + i);
                System.out.println("#############################################");
                compResult = compileCommonProcess(COMP_ERR_LIST, ftp, deployFtpPath, deploySystem);
                System.out.println("#############################################");
                System.out.println("컴파일 실패시 재컴파일 시도 ::: " + compResult);
                System.out.println("#############################################");
                if (compResult) {
                    break;
                }
            }
        }

        return compResult;

    }


    /**
     * java 파일 컴파일
     * 
     */
    public static boolean compileCommonProcess(List<String> processedList, SFTPService ftp, String deployFtpPath, String deploySystem) {

        boolean compResult = true;
        COMP_ERR_LIST = new ArrayList<String>();

        String sshCommand = "";

        String rootPath = ""; // $1
        String classPath = "";
        String sourcePath = "";
        String javacResultFile = "";

        boolean compResultDtl = false;

        for (int i = 0; i < processedList.size(); i++) {
            javacResultFile = "javac_result_" + deploySystem + "_" + i;
            rootPath = deployFtpPath + processedList.get(i);
            classPath = deployFtpPath + "/WEB-INF/classes:" + "/data/webapps/JEUS/lib/system/*:" + deployFtpPath + "/WEB-INF/lib/* ";
            sourcePath = deployFtpPath + "/WEB-INF/classes";


            if (deploySystem.equals("BATCH")) {
                classPath = deployFtpPath + "/classes:" + "/data/webapps/JEUS/lib/system/*:" + deployFtpPath + "/lib/* ";
                sourcePath = deployFtpPath + "/classes";
            }

            sshCommand = RUN_JAVA_COMPILE_SHELL + " " + rootPath + " " + classPath + " " + sourcePath + " " + javacResultFile;

            System.out.println("javac sshCommand ::" + sshCommand);
            ftp.sshCommandExecByShellScript(sshCommand);

            String javacServerResultFile = JAVA_COMPILE_SERVER_PATH + "log/" + javacResultFile;
            String javacResultDownFile = DEPLOY_DIR + "temp/" + javacResultFile;

            System.out.println("javacServerResultFile ::" + javacServerResultFile);
            System.out.println("javacResultDownFile ::" + javacResultDownFile);

            // 컴파일이 완료시까지 체크
            compResultDtl = getCompileResultCheckRun(deploySystem, ftp, javacServerResultFile, javacResultDownFile);

            if (!compResultDtl) {
                compResult = false;
                COMP_ERR_LIST.add(processedList.get(i));
            }

            System.out.println("## compResultDtl ::" + compResultDtl);

        }

        System.out.println("#############################################");
        System.out.println("###############   compResult ::" + compResult);
        System.out.println("#############################################");

        return compResult;

    }


    /**
     * 컴파일 결과 확인
     */
    public static boolean getCompileResultCheckRun(String deploySystem, SFTPService ftp, String javacServerResultFile,
            String javacResultDownFile) {

        boolean compResult = false;

        compResult = getCompileResultCheck(deploySystem, ftp, javacServerResultFile, javacResultDownFile);

        return compResult;
    }


    /**
     * 컴파일 결과 확인
     */
    public static boolean getCompileResultCheck(String deploySystem, SFTPService ftp, String javacServerResultFile,
            String javacResultDownFile) {

        boolean compResult = false;
        ftp.downloadFile(javacServerResultFile, DEPLOY_DIR + "temp");

        File f = new File(javacResultDownFile);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        if (f.exists()) {

            try {
                is = new FileInputStream(javacResultDownFile);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {
                    if (str.contains("SUCCESS")) {
                        compResult = true;
                    } else if (str.contains("FAIL")) {
                        compResult = false;
                        getCompileErrFileDownRun(deploySystem, ftp, javacServerResultFile + ".log", javacResultDownFile + ".log");
                    }


                }// while_end


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

        } else {
            return getCompileResultCheck(deploySystem, ftp, javacServerResultFile, javacResultDownFile);
        }// f.exists if_end


        // javac_result.txt 파일 삭제
        // String sshCommand = "rm " + javacResultFile;
        // ftp.sshCommandExec(sshCommand);

        return compResult;
    }


    /**
     * 컴파일 에러파일 다운
     */
    public static void getCompileErrFileDownRun(String deploySystem, SFTPService ftp, String javacServerResultErrorFile,
            String javacResultErrorFile) {

        getCompileErrFileDown(deploySystem, ftp, javacServerResultErrorFile, javacResultErrorFile);
    }


    /**
     * 컴파일 에러파일 다운확인
     */
    public static void getCompileErrFileDown(String deploySystem, SFTPService ftp, String javacServerResultErrorFile,
            String javacResultErrorFile) {

        ftp.downloadFile(javacServerResultErrorFile, DEPLOY_DIR + "temp");

        File f = new File(javacResultErrorFile);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        if (f.exists()) {

            try {
                is = new FileInputStream(javacResultErrorFile);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);
                System.out.println("################################################################");
                System.out.println("#컴파일 에러#");
                while ((str = br.readLine()) != null) {
                    System.out.println(str);
                }// while_end
                System.out.println("################################################################");

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

        } else {
            getCompileErrFileDown(deploySystem, ftp, javacServerResultErrorFile, javacResultErrorFile);
        }// f.exists if_end

    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag) {

        jeusShutDownBootCommon(props, targetSystem, targetSystemFtp, rebootFlag, "real");
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetSystem, String targetSystemFtp, String rebootFlag,
            String targetServer) {

        String rebootFtpIp = "";
        int rebootFtpPort = 22;
        String rebootFtpId = "";
        String rebootFtpPw = "";

        String jeusAddr = "";
        String jeusStartNm = "";
        String jeusStartPort = "";
        String jeusBootHost = "";

        if (targetSystem.contains("TO")) {
            targetSystemFtp = targetSystemFtp.replace("TO", "TC");
        }

        if (targetServer.equals("real")) {

            jeusAddr = props.getProperty(targetSystem + ".jeusAddr");
            jeusStartNm = targetSystemFtp;
            jeusStartPort = props.getProperty(targetSystem + ".jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + ".jeusBootHost");

            rebootFtpIp = props.getProperty(targetSystem + "1.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "1.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "1.ftp.pw");

        } else if (targetServer.equals("test")) {

            jeusAddr = props.getProperty(targetSystem + "_TST.jeusAddr");
            jeusStartNm = targetSystemFtp;
            jeusStartPort = props.getProperty(targetSystem + "_TST.jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + "_TST.jeusBootHost");

            rebootFtpIp = props.getProperty(targetSystem + "_TST.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "_TST.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "_TST.ftp.pw");
        }

        SFTPService rebootftp = new SFTPService(rebootFtpIp, rebootFtpPort, rebootFtpId, rebootFtpPw);

        String bootSsshCommand = "";

        if (rebootFlag.equals("shutdown")) {
            // shutdown
            bootSsshCommand =
            // "nohup " +
                    "" + JEUS_HOME + "/bin/jeusadmin  " + "-host " + jeusAddr + ":" + jeusStartPort + " " + "-f " + JEUS_HOME
                            + "/bin/scripts/jeusEncode \"stopserver " + jeusStartNm + " -to 10\"  ";
            // + "& sleep 15";
            System.out
                    .println("====================================================================================================================================");
            System.out.println("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
            System.out
                    .println("====================================================================================================================================");

            rebootftp.sshCommandExec(bootSsshCommand);

            // 복구 소스 생성
            if (targetSystemFtp.equals("TC1") || targetSystemFtp.equals("TC2")) {
                targetSystemFtp = targetSystemFtp.replace("TC", "TO");
            }
            sourceBakupCommandExec(props, targetSystemFtp, targetSystem);


            try {
                // System.out.println("sleep start::");
                Thread.sleep(20 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (rebootFlag.equals("boot")) {
            // booot
            bootSsshCommand =
            // "nohup " +
                    "" + JEUS_HOME + "/bin/jeusadmin  -host " + jeusBootHost + ":" + jeusStartPort + " -f " + JEUS_HOME
                            + "/bin/scripts/jeusEncode \"startserver " + jeusStartNm + "\" ";
            // + " &";
            System.out
                    .println("====================================================================================================================================");
            System.out.println("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);
            System.out
                    .println("====================================================================================================================================");

            rebootftp.sshCommandExec(bootSsshCommand);
        }
        System.out
                .println("====================================================================================================================================");
        System.out.println("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END");
        System.out
                .println("====================================================================================================================================");

    }


    /**
     * 디플로이 파일을 WAS경로로 변환
     * 
     * @param files
     * @return
     */
    private static String getChageWasFullPathFileName(String deployStr) {

        String chageDeployStr = "";

        chageDeployStr = deployStr.replace("/03_Front/WebContent", "");
        chageDeployStr = chageDeployStr.replace("/04_FrontMobile/WebContent", "");
        chageDeployStr = chageDeployStr.replace("/05_BO/WebContent", "");
        chageDeployStr = chageDeployStr.replace("/09_Tablet/WebContent", "");

        chageDeployStr = chageDeployStr.replace("/03_Front", "");
        chageDeployStr = chageDeployStr.replace("/04_FrontMobile", "");
        chageDeployStr = chageDeployStr.replace("/05_BO", "");
        chageDeployStr = chageDeployStr.replace("/09_Tablet", "");
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
     * SVN 파일
     * 
     * @param files
     * @return
     */
    private static void getSvnFile(List<String> processedList) {

        String deployFileDir = "";
        String deployStr = "";
        String cdDir = "";
        List<String> commonClassStrList = new ArrayList<String>();

        boolean isCommanExec = true;

        // System.out.println("============================================================================================");
        // System.out.println("getSvnFile makeDiretory");
        System.out.println("============================================================================================");

        for (int i = 0; i < processedList.size(); i++) {

            isCommanExec = true;

            if (StringUtils.isNotEmpty(processedList.get(i))) {
                deployStr = processedList.get(i);

                cdDir = deployStr.substring(0, deployStr.lastIndexOf("/") + 1);
                deployFileDir = DEPLOY_FILE_DIR + cdDir;
                makeDiretory(deployFileDir);

                // System.out.println("cdDir ::" + cdDir);
                // System.out.println("deployFileDir ::" + deployFileDir);
                // System.out.println("deployStr ::" + deployStr);
                // System.out.println("getSvnFile  isCommanExec ::" + isCommanExec);
                if (isCommanExec) {

                    if (deployStr.contains("@")) {
                        deployStr = deployStr + "@";
                    }
                    commandExec(deployStr, deployFileDir);
                }

            }// isNotEmpty check if_end

        }
        System.out.println("getSvnFile Success!!!");
        System.out.println("============================================================================================");


        try {
            // System.out.println("sleep start::");
            Thread.sleep(2 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    /**
     * 디플로이 파일을 WAS경로로 변환
     * 
     * @param files
     * @return
     */
    private static void getSvnFile(String svnPath, String deployFile) {

        String deployFileDir = "";

        String cdDir = deployFile.substring(0, deployFile.lastIndexOf("/") + 1);
        deployFileDir = DEPLOY_FILE_DIR + cdDir;
        makeDiretory(deployFileDir);

        commandExec(svnPath, deployFileDir);


        // try {
        // //System.out.println("sleep start::");
        // Thread.sleep(2 * 1000);
        // //System.out.println("sleep end::");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }


    }



    private static void commandExec(String deployStr, String deployFileDir) {

        String command = "svn export --force --password LotteTa svn://10.154.17.205/ehimart/branches/REL";
        command = command + deployStr + " " + deployFileDir;
        System.out.println("====================================");
        System.out.println("deployStr :: " + deployStr);
        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 정상적 출력
            //
            // String line = "";
            // while (line != null) {
            // System.out.println(line);
            // line = reader.readLine();
            // }
            // System.out.println("====================================");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private static void sourceBakupCommandExec(Properties props, String targetSystemFtp, String targetSystem) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        deployFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
        deployFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");
        deployFtpPath = props.getProperty(targetSystemFtp + ".ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String yyyyMMdd = todate.substring(0, 8);
        String HHmmss = todate.substring(8, 14);
        String wasDir = props.getProperty("defaultDir") + "/WAS/";

        if (targetSystem.equals("MLPS")) {
            targetSystem = "mLPS";
        }

        if (targetSystem.contains("BATCH")) {
            wasDir = props.getProperty("defaultDir") + "/BATCH/";
        } else if (targetSystem.equals("FO_IMAGE") || targetSystem.equals("MO_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystem.equals("FO_IMAGE")) {
                targetSystem = "FO";
            } else if (targetSystem.equals("MO_IMAGE")) {
                targetSystem = "MO";
            }
        } else if (targetSystem.equals("FO_STAGING_IMAGE") || targetSystem.equals("MO_STAGING_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystem.equals("FO_STAGING_IMAGE")) {
                targetSystem = "FO_STG";
            } else if (targetSystem.equals("MO_STAGING_IMAGE")) {
                targetSystem = "MO_STG";
            }
        }

        String backupDir = wasDir + "backup/";

        String backupsshCommand =
                "cd " + wasDir + ";" + "cp -r " + targetSystem + " " + backupDir + targetSystem + "_" + yyyyMMdd + "_" + HHmmss;
        System.out.println("sourceBakupCommandExec ::::" + backupsshCommand);

        ftp.sshCommandExec(backupsshCommand);

    }


    private static void webSourceBakupCommandExec(Properties props, String targetSystem, SFTPService ftp) {

        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String yyyyMMdd = todate.substring(0, 8);
        String HHmmss = todate.substring(8, 14);
        String webDir = props.getProperty("defaultDir") + "/WEB/";

        if (targetSystem.equals("MLPS")) {
            targetSystem = "mLPS";
        }

        String backupDir = webDir + "backup/";

        String backupsshCommand =
                "cd " + webDir + ";" + "cp -r " + targetSystem + " " + backupDir + targetSystem + "_" + yyyyMMdd + "_" + HHmmss;
        System.out.println("webSourceBakupCommandExec ::::" + backupsshCommand);

        ftp.sshCommandExec(backupsshCommand);

    }


    private static void refreshContentsCompileCommandExec(String deploySystemFtp, Properties props, boolean isImageServer) {

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        if (isImageServer) {
            targetFtpIp = props.getProperty(deploySystemFtp + ".ftp.ip");
            targetFtpId = props.getProperty(deploySystemFtp + ".ftp.id");
            targetFtpPw = props.getProperty(deploySystemFtp + ".ftp.pw");
        } else {
            targetFtpIp = props.getProperty(deploySystemFtp + ".web.ip");
            targetFtpId = props.getProperty(deploySystemFtp + ".web.id");
            targetFtpPw = props.getProperty(deploySystemFtp + ".web.pw");
        }

        SFTPService ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);
        ftp.sshCommandExecByShellScript(REFRESH_CONTENTS_SHELL);
        log.info("REFRESH_CONTENTS_SHELL :: [" + deploySystemFtp + "]");
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

        String[] CHECK_FILE_NAME = {"jpg", "png", "gif", "jpeg", "js", "css", "html"};

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


    /**
     * 파일 확장자 체크
     * 
     * 이미지 파일인지 확인한다.
     * 
     * @param files
     * @return
     */
    public static boolean fileExtCheckByBo(String fileName) {

        String[] CHECK_FILE_NAME = {"jpg", "png", "gif", "jpeg", "js", "css", "xls", "txt", "html"};

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


    private static void delCompileLogFile(String deploySystem, SFTPService ftp) {

        if (StringUtils.isNotEmpty(deploySystem)) {
            String command = "rm " + JAVA_COMPILE_SERVER_PATH + "log/javac_result_" + deploySystem + "_*.txt";
            ftp.sshCommandExec(command);
        }

    }


    /**
     * 디렉토리 생성
     * 
     * @param goodsNo
     */
    public static String makeDiretory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }


    private static void failReturn(String errMessage) throws Exception {

        throw new Exception(errMessage);
    }


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }



}
