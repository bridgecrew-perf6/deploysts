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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import utils.DateUtils;
import utils.HttpUrlConnectionCommon;
import utils.SFTPService;

@Slf4j
@Data
public class Deploy {

    static final String DEPLOY_DIR = "/data/home/hisis/ec/tools/deploy/";
    static final String DEPLOY_UPFILE_LIST = "/data/home/hisis/ec/tools/deploy/deploy.txt";
    static final String DEPLOY_ALL_UPFILE_LIST = "/data/home/hisis/ec/tools/deploy/deploy.all.txt";


    static final String DEPLOY_FILE_DIR = DEPLOY_DIR + "upfile";
    static final String JEUS_HOME = "/data/webapps/JEUS";
    static final String RUN_JAVA_COMPILE_SHELL = "sh  /NAS-EC_PRD/tools/javacompile/run_java_compile.sh";
    static final String RUN_JAVA_COMPILE_NOLOG_SHELL = "sh  /NAS-EC_PRD/tools/javacompile/run_java_compile_nolog.sh";
    static final String JAVA_COMPILE_SERVER_PATH = "/NAS-EC_PRD/tools/javacompile/";

    static List<String> COMP_ERR_LIST = null;
    static boolean IS_DEPLOY_SUCCESS = true;

    static String[] FO_SET_01 = new String[] {};
    static String[] MO_SET_01 = new String[] {};


    public Deploy(Properties props, String deployL4, String deploySystem) {

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


        String deployFileDir = DEPLOY_FILE_DIR;
        String deployFileBackDir = "";
        Map<String, Object> returnMap = null;

        deployFileBackDir = DEPLOY_DIR + "backup";

        // 디플로이 파일 목록
        returnMap = getDeployFileListMap(deploySystem, deployL4);

        // 업로드 파일이 모두 있는지 확인
        boolean isUpFile = uploadfileEqaulCheckCommon(deploySystem, returnMap);

        System.out.println("isUpFile :::" + isUpFile);

        if (!isUpFile) {
            System.out.println("isUpFile :::" + isUpFile);
            return;
        }

        if (deploySystem.equals("FO")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            //
            // System.out.println("FO fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            foStrList = (List<String>) returnMap.get("foStrList");
            foClassStrList = (List<String>) returnMap.get("foClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * FO L4_1번기 : FO1, FO6, LPS1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                // setDeployFOTest(foStrList, foClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                // commonClassStrList, deployL4);

                setDeployL401ByFO(foStrList, foClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * FO L4_2번기 : FO2, FO3, FO5, LPS2, LPS3 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByFO(foStrList, foClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }

        } else if (deploySystem.equals("MO")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("MO fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            moStrList = (List<String>) returnMap.get("moStrList");
            moClassStrList = (List<String>) returnMap.get("moClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * MO L4_1번기 : MO1, MO7, mLPS1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                setDeployL401ByMO(moStrList, moClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * MO L4_2번기 : MO5, MO6, mLPS2, mLPS3 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByMO(moStrList, moClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }

        } else if (deploySystem.equals("LPS3")) {
            System.out.println("deployFileUpload MLPS3  setDeployL401ByFO_lps3_only  ::");

            foStrList = (List<String>) returnMap.get("foStrList");
            foClassStrList = (List<String>) returnMap.get("foClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            setDeployL401ByFO_lps3_only(foStrList, foClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                    commonClassStrList, deployL4);
        } else if (deploySystem.equals("MLPS3")) {
            System.out.println("deployFileUpload MLPS3  setDeployL401ByMO_mLPS3_Only  ::");

            moStrList = (List<String>) returnMap.get("moStrList");
            moClassStrList = (List<String>) returnMap.get("moClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");
            setDeployL401ByMO_mLPS3_Only(moStrList, moClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);

        } else if (deploySystem.equals("BO")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("BO fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            boStrList = (List<String>) returnMap.get("boStrList");
            boClassStrList = (List<String>) returnMap.get("boClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * BO L4_1번기 : BO1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                setDeployL401ByBO(boStrList, boClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * BO L4_2번기 : BO2 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByBO(boStrList, boClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("PO")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("PO fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            boStrList = (List<String>) returnMap.get("boStrList");
            boClassStrList = (List<String>) returnMap.get("boClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * PO L4_1번기 : PO1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                setDeployL401ByPO(boStrList, boClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * PO L4_2번기 : PO2 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByPO(boStrList, boClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("CC")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("CC fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            boStrList = (List<String>) returnMap.get("boStrList");
            boClassStrList = (List<String>) returnMap.get("boClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * CC L4_1번기 : CC1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                setDeployL401ByCC(boStrList, boClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * CC L4_2번기 : CC2 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByCC(boStrList, boClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("TO")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("TO fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            toStrList = (List<String>) returnMap.get("toStrList");
            toClassStrList = (List<String>) returnMap.get("toClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * TO L4_1번기 : TO1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 4. 서버재부팅]
                 *************************************************************************************************/
                setDeployL401ByTO(toStrList, toClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * TO L4_2번기 : TO2 [1. 요청파일 업로드 2. 서버재부팅]
                 *************************************************************************************************/
                setDeployL402ByTO(toStrList, toClassStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("BATCH")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("BATCH fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            batchClassStrList = (List<String>) returnMap.get("batchClassStrList");
            commonClassStrList = (List<String>) returnMap.get("commonClassStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * BATCH L4_1번기 : BATCH1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 ]
                 *************************************************************************************************/
                setDeployL401ByBatch(batchClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * BATCH L4_2번기 : BATCH2 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드]
                 *************************************************************************************************/
                setDeployL402ByBatch(batchClassStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem,
                        commonClassStrList, deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("FO_IMAGE")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("FO_IMAGE fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            foStrList = (List<String>) returnMap.get("foStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * FO_IMAGE L4_1번기 : BATCH1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 ]
                 *************************************************************************************************/
                setDeployL401ByFoImage(foStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem, commonClassStrList,
                        deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * FO_IMAGE L4_2번기 : BATCH2 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드]
                 *************************************************************************************************/
                setDeployL402ByFoImage(foStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem, commonClassStrList,
                        deployL4);
            }
            // }// BO fileList.size() check if_end

        } else if (deploySystem.equals("MO_IMAGE")) {

            // deployFileDirList = new File(deployFileDir);
            // fileList = getAllFileList(deployFileDirList);
            // System.out.println("MO_IMAGE fileList.size() " + fileList.size());
            //
            // if (fileList.size() > 0) {

            moStrList = (List<String>) returnMap.get("moStrList");

            if (deployL4.equals("01")) {
                /*************************************************************************************************
                 * FO_IMAGE L4_1번기 : BATCH1 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드 ]
                 *************************************************************************************************/
                setDeployL401ByMoImage(moStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem, commonClassStrList,
                        deployL4);

            } else if (deployL4.equals("02")) {
                /*************************************************************************************************
                 * FO_IMAGE L4_2번기 : BATCH2 [1. 요청파일 체크 2. 백업파일 다운로드 3. 요청파일 업로드]
                 *************************************************************************************************/
                setDeployL402ByMoImage(moStrList, fileList, props, deployFileDir, deployFileBackDir, deploySystem, commonClassStrList,
                        deployL4);
            }
            // }// BO fileList.size() check if_end

        } else {
            System.out.println("Deploy System check!!!!!");
        }// deploySystem if_else end


        if (!IS_DEPLOY_SUCCESS) {
            throw new RuntimeException("Build Fail !!!!!!!!!!!!!!!!!!!!!!!");
        }

    }


    /**
     * FO L4 1번기
     */
    public static void setDeployFOTest(List<String> foStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(foStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile FO_TST :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("FO_TST.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO_TST.ftp.id");
        deployFtpPw = props.getProperty("FO_TST.ftp.pw");
        deployFtpPath = props.getProperty("FO_TST.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);
        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(foStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);
        System.out.println("isFileBakDown FO  ::" + isFileBakDown);

        if (isFileBakDown) {

            /**
             * FO_TST 파일 업로드
             **************************************************/

            System.out.println("deployFileUpload  FO_TST::");

            String deploySystemFtp = "FO";
            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown", "test");

            // FO1 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);

            // jeusBootCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot", "test");


        }// 파일 백업파일 다운로드 성공여부 if_end



        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * FO L4 1번기
     */
    public static void setDeployL401ByFO(List<String> foStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(foStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile FO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("FO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO1.ftp.id");
        deployFtpPw = props.getProperty("FO1.ftp.pw");
        deployFtpPath = props.getProperty("FO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(foStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown FO  ::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // FO1
            /*************************************************
             * L4_1번기 : FO1, FO6, LPS1
             ***************************************************/
            /**
             * FO1 파일 업로드
             **************************************************/

            System.out.println("deployFileUpload  FO1::");

            String deploySystemFtp = "FO1";
            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            boolean isFileUpladSucess = false;
            // FO1 파일 업로드
            isFileUpladSucess =
                    deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);


            if (isFileUpladSucess) {

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

                /**
                 * FO4 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload FO4 ::");

                deployFtpIp = props.getProperty("FO4.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("FO4.ftp.id");
                deployFtpPw = props.getProperty("FO4.ftp.pw");
                deployFtpPath = props.getProperty("FO4.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "FO4";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * FO6 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload FO6 ::");

                deployFtpIp = props.getProperty("FO6.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("FO6.ftp.id");
                deployFtpPw = props.getProperty("FO6.ftp.pw");
                deployFtpPath = props.getProperty("FO6.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "FO6";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * LPS1 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload LPS1 ::");
                // 서버다운
                deployFtpIp = props.getProperty("LPS1.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("LPS1.ftp.id");
                deployFtpPw = props.getProperty("LPS1.ftp.pw");
                deployFtpPath = props.getProperty("LPS1.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "LPS1";
                deploySystem = "LPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


            }// 파일 컴파일 성공여부

        }// 파일 백업파일 다운로드 성공여부 if_end


        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * FO L4 1번기_with_lps3
     */
    public static void setDeployL401ByFO_with_lps3(List<String> foStrList, List<String> classStrList, ArrayList<String> fileList,
            Properties props, String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList,
            String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(foStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile FO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("FO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO1.ftp.id");
        deployFtpPw = props.getProperty("FO1.ftp.pw");
        deployFtpPath = props.getProperty("FO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(foStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown FO  ::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // FO1
            /*************************************************
             * L4_1번기 : FO1, FO6, LPS1
             ***************************************************/
            /**
             * FO1 파일 업로드
             **************************************************/

            System.out.println("deployFileUpload  FO1::");

            String deploySystemFtp = "FO1";
            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            boolean isFileUpladSucess = false;
            // FO1 파일 업로드
            isFileUpladSucess =
                    deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);


            if (isFileUpladSucess) {

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

                /**
                 * FO4 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload FO4 ::");

                deployFtpIp = props.getProperty("FO4.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("FO4.ftp.id");
                deployFtpPw = props.getProperty("FO4.ftp.pw");
                deployFtpPath = props.getProperty("FO4.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "FO4";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * FO6 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload FO6 ::");

                deployFtpIp = props.getProperty("FO6.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("FO6.ftp.id");
                deployFtpPw = props.getProperty("FO6.ftp.pw");
                deployFtpPath = props.getProperty("FO6.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "FO6";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * LPS1 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload LPS1 ::");
                // 서버다운
                deployFtpIp = props.getProperty("LPS1.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("LPS1.ftp.id");
                deployFtpPw = props.getProperty("LPS1.ftp.pw");
                deployFtpPath = props.getProperty("LPS1.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "LPS1";
                deploySystem = "LPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * LPS3 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload LPS3 ::");
                // 서버다운
                deployFtpIp = props.getProperty("LPS3.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("LPS3.ftp.id");
                deployFtpPw = props.getProperty("LPS3.ftp.pw");
                deployFtpPath = props.getProperty("LPS3.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "LPS3";
                deploySystem = "LPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


            }// 파일 컴파일 성공여부

        }// 파일 백업파일 다운로드 성공여부 if_end


        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * FO L4 1번기_lps3_only
     */
    public static void setDeployL401ByFO_lps3_only(List<String> foStrList, List<String> classStrList, ArrayList<String> fileList,
            Properties props, String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList,
            String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * LPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload LPS3 ::");
        // 서버다운
        deployFtpIp = props.getProperty("LPS3.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("LPS3.ftp.id");
        deployFtpPw = props.getProperty("LPS3.ftp.pw");
        deployFtpPath = props.getProperty("LPS3.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        String deploySystemFtp = "LPS3";
        deploySystem = "LPS";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

    }


    /**
     * FO L4 2번기
     */
    public static void setDeployL402ByFO(List<String> foStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * FO2 파일 업로드
         **************************************************/
        deployFtpIp = props.getProperty("FO2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO2.ftp.id");
        deployFtpPw = props.getProperty("FO2.ftp.pw");
        deployFtpPath = props.getProperty("FO2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        String deploySystemFtp = "FO2";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        System.out.println("deployFileUpload  FO2::");

        // 파일업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        /**
         * FO3 파일 업로드
         **************************************************/
        // 서버다운
        deployFtpIp = props.getProperty("FO3.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO3.ftp.id");
        deployFtpPw = props.getProperty("FO3.ftp.pw");
        deployFtpPath = props.getProperty("FO3.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "FO3";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        System.out.println("deployFileUpload FO3 ::");

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        /**
         * FO5 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("FO5.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO5.ftp.id");
        deployFtpPw = props.getProperty("FO5.ftp.pw");
        deployFtpPath = props.getProperty("FO5.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "FO5";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        System.out.println("deployFileUpload FO5 ::");

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        /**
         * LPS2 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("LPS2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("LPS2.ftp.id");
        deployFtpPw = props.getProperty("LPS2.ftp.pw");
        deployFtpPath = props.getProperty("LPS2.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "LPS2";
        deploySystem = "LPS";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        System.out.println("deployFileUpload LPS2 ::");

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        /**
         * LPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload LPS3 ::");

        deployFtpIp = props.getProperty("LPS3.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("LPS3.ftp.id");
        deployFtpPw = props.getProperty("LPS3.ftp.pw");
        deployFtpPath = props.getProperty("LPS3.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "LPS3";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        System.out.println("deployFileUpload LPS3 ::");
        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

    }


    /**
     * mlps 3번기 Only
     */
    public static void setDeployL401ByMO_mLPS3_Only(List<String> moStrList, List<String> classStrList, Properties props,
            String deployFileDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * MLPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MLPS3 ::");

        deployFtpIp = props.getProperty("MLPS3.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MLPS3.ftp.id");
        deployFtpPw = props.getProperty("MLPS3.ftp.pw");
        deployFtpPath = props.getProperty("MLPS3.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        String deploySystemFtp = "MLPS3";
        deploySystem = "MLPS";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


    }


    /**
     * MO L4 1번기
     */
    public static void setDeployL401ByMO(List<String> moStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(moStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile  MO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("MO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO1.ftp.id");
        deployFtpPw = props.getProperty("MO1.ftp.pw");
        deployFtpPath = props.getProperty("MO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(moStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown MO ::" + isFileBakDown);

        if (isFileBakDown) {

            // new DeployWithProperties(props, "MO_SET_01", "N", "N", "01");



            // 신규파일 업로드
            // MO1
            /*************************************************
             * L4_1번기 : MO1, MO7, MO4, mLPS1
             ***************************************************/
            /**
             * MO1 파일 업로드
             **************************************************/

            System.out.println("deployFileUpload  MO1::");

            String deploySystemFtp = "MO1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {
                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

                /**
                 * MO7 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MO7 ::");

                deployFtpIp = props.getProperty("MO7.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MO7.ftp.id");
                deployFtpPw = props.getProperty("MO7.ftp.pw");
                deployFtpPath = props.getProperty("MO7.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MO7";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * MO4 파일 업로드
                 **************************************************/
                // System.out.println("deployFileUpload MO4 ::");
                //
                // deployFtpIp = props.getProperty("MO4.ftp.ip");
                // // deployFtpPort = 22;
                // deployFtpId = props.getProperty("MO4.ftp.id");
                // deployFtpPw = props.getProperty("MO4.ftp.pw");
                // deployFtpPath = props.getProperty("MO4.ftp.path");
                //
                // ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);
                //
                // deploySystemFtp = "MO4";
                //
                // // jeusShutDownCommon
                // jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");
                //
                // // 파일업로드
                // deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                // deploySystemFtp, props);
                //
                // // jeusBootCommon
                // jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * MLPS1 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MLPS1 ::");

                deployFtpIp = props.getProperty("MLPS1.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MLPS1.ftp.id");
                deployFtpPw = props.getProperty("MLPS1.ftp.pw");
                deployFtpPath = props.getProperty("MLPS1.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MLPS1";
                deploySystem = "MLPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


            }// 파일 백업파일 다운로드 성공여부 if_end

        }



        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }



    /**
     * MO L4 1번기 with_mLPS3
     */
    public static void setDeployL401ByMO_with_mLPS3(List<String> moStrList, List<String> classStrList, ArrayList<String> fileList,
            Properties props, String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList,
            String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(moStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile  MO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("MO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO1.ftp.id");
        deployFtpPw = props.getProperty("MO1.ftp.pw");
        deployFtpPath = props.getProperty("MO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(moStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown MO ::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // MO1
            /*************************************************
             * L4_1번기 : MO1, MO7, mLPS1
             ***************************************************/
            /**
             * MO1 파일 업로드
             **************************************************/

            System.out.println("deployFileUpload  MO1::");

            String deploySystemFtp = "MO1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {
                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

                /**
                 * MO7 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MO7 ::");

                deployFtpIp = props.getProperty("MO7.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MO7.ftp.id");
                deployFtpPw = props.getProperty("MO7.ftp.pw");
                deployFtpPath = props.getProperty("MO7.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MO7";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * MO8 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MO8 ::");

                deployFtpIp = props.getProperty("MO8.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MO8.ftp.id");
                deployFtpPw = props.getProperty("MO8.ftp.pw");
                deployFtpPath = props.getProperty("MO8.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MO8";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * MLPS1 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MLPS1 ::");

                deployFtpIp = props.getProperty("MLPS1.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MLPS1.ftp.id");
                deployFtpPw = props.getProperty("MLPS1.ftp.pw");
                deployFtpPath = props.getProperty("MLPS1.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MLPS1";
                deploySystem = "MLPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


                /**
                 * MLPS3 파일 업로드
                 **************************************************/
                System.out.println("deployFileUpload MLPS3 ::");

                deployFtpIp = props.getProperty("MLPS3.ftp.ip");
                // deployFtpPort = 22;
                deployFtpId = props.getProperty("MLPS3.ftp.id");
                deployFtpPw = props.getProperty("MLPS3.ftp.pw");
                deployFtpPath = props.getProperty("MLPS3.ftp.path");

                ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

                deploySystemFtp = "MLPS3";
                deploySystem = "MLPS";

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

                // 파일 업로드
                deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


            }// 파일 백업파일 다운로드 성공여부 if_end

        }



        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * MO L4 2번기
     */
    public static void setDeployL402ByMO(List<String> moStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";


        /**
         * MO5 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("MO5.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO5.ftp.id");
        deployFtpPw = props.getProperty("MO5.ftp.pw");
        deployFtpPath = props.getProperty("MO5.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        System.out.println("deployFileUpload  MO5::");

        String deploySystemFtp = "MO5";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


        /**
         * MO6 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MO6 ::");

        deployFtpIp = props.getProperty("MO6.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO6.ftp.id");
        deployFtpPw = props.getProperty("MO6.ftp.pw");
        deployFtpPath = props.getProperty("MO6.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MO6";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


        /**
         * MO8 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MO8 ::");

        deployFtpIp = props.getProperty("MO8.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO8.ftp.id");
        deployFtpPw = props.getProperty("MO8.ftp.pw");
        deployFtpPath = props.getProperty("MO8.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MO8";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

        /**
         * MLPS2 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MLPS2 ::");

        deployFtpIp = props.getProperty("MLPS2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MLPS2.ftp.id");
        deployFtpPw = props.getProperty("MLPS2.ftp.pw");
        deployFtpPath = props.getProperty("MLPS2.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MLPS2";
        deploySystem = "MLPS";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");


        /**
         * MLPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MLPS3 ::");

        deployFtpIp = props.getProperty("MLPS3.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MLPS3.ftp.id");
        deployFtpPw = props.getProperty("MLPS3.ftp.pw");
        deployFtpPath = props.getProperty("MLPS3.ftp.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MLPS3";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(moStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");

    }



    /**
     * BO L4 1번기
     */
    public static void setDeployL401ByBO(List<String> boStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(boStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile BO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("BO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("BO1.ftp.id");
        deployFtpPw = props.getProperty("BO1.ftp.pw");
        deployFtpPath = props.getProperty("BO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(boStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown BO::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // BO1
            /*************************************************
             * L4_1번기 : BO1
             ***************************************************/
            /**
             * BO1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  BO1::");

            String deploySystemFtp = "BO1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * BO L4 2번기
     */
    public static void setDeployL402ByBO(List<String> boStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * BO2 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("BO2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("BO2.ftp.id");
        deployFtpPw = props.getProperty("BO2.ftp.pw");
        deployFtpPath = props.getProperty("BO2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        System.out.println("deployFileUpload  BO2::");

        String deploySystemFtp = "BO2";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
    }


    /**
     * PO L4 1번기
     */
    public static void setDeployL401ByPO(List<String> boStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(boStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile PO1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("PO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("PO1.ftp.id");
        deployFtpPw = props.getProperty("PO1.ftp.pw");
        deployFtpPath = props.getProperty("PO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(boStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown PO1::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // BO1
            /*************************************************
             * L4_1번기 : PO1
             ***************************************************/
            /**
             * PO1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  PO1::");

            String deploySystemFtp = "PO1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * PO L4 2번기
     */
    public static void setDeployL402ByPO(List<String> boStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * PO2 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("PO2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("PO2.ftp.id");
        deployFtpPw = props.getProperty("PO2.ftp.pw");
        deployFtpPath = props.getProperty("PO2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        System.out.println("deployFileUpload  PO2::");

        String deploySystemFtp = "PO2";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
    }


    /**
     * CC L4 1번기
     */
    public static void setDeployL401ByCC(List<String> boStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(boStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile CC1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("CC1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("CC1.ftp.id");
        deployFtpPw = props.getProperty("CC1.ftp.pw");
        deployFtpPath = props.getProperty("CC1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(boStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown CC::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // BO1
            /*************************************************
             * L4_1번기 : PO1
             ***************************************************/
            /**
             * PO1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  CC1::");

            String deploySystemFtp = "CC1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * CC L4 2번기
     */
    public static void setDeployL402ByCC(List<String> boStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * CC2 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("CC2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("CC2.ftp.id");
        deployFtpPw = props.getProperty("CC2.ftp.pw");
        deployFtpPath = props.getProperty("CC2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        System.out.println("deployFileUpload  CC2::");

        String deploySystemFtp = "CC2";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(boStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
    }


    /**
     * TO L4 1번기
     */
    public static void setDeployL401ByTO(List<String> toStrList, List<String> classStrList, ArrayList<String> fileList, Properties props,
            String deployFileDir, String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(toStrList, classStrList, fileList);
        //
        // System.out.println("isUpFile TO :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("TO1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("TO1.ftp.id");
        deployFtpPw = props.getProperty("TO1.ftp.pw");
        deployFtpPath = props.getProperty("TO1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(toStrList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown TO::" + isFileBakDown);

        if (isFileBakDown) {
            // 신규파일 업로드
            // BO1
            /*************************************************
             * L4_1번기 : BO1
             ***************************************************/
            /**
             * BO1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  TO1::");

            String deploySystemFtp = "TC1";

            // jeusShutDownCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

            // 파일 업로드
            boolean isFileUpladSucess = false;
            isFileUpladSucess =
                    deployFileUpload(toStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4,
                            deploySystem, deploySystemFtp, props);

            if (isFileUpladSucess) {

                if (classStrList != null && classStrList.size() > 0) {
                    // 컴파일 로그 삭제
                    delCompileLogFile(deploySystem, ftp);
                }

                // jeusBootCommon
                jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * TO L4 2번기
     */
    public static void setDeployL402ByTO(List<String> toStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        /**
         * TO2 파일 업로드
         **************************************************/

        deployFtpIp = props.getProperty("TO2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("TO2.ftp.id");
        deployFtpPw = props.getProperty("TO2.ftp.pw");
        deployFtpPath = props.getProperty("TO2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        System.out.println("deployFileUpload  TO2::");

        String deploySystemFtp = "TC2";

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");

        // 파일 업로드
        deployFileUpload(toStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);

        if (classStrList != null && classStrList.size() > 0) {
            // 컴파일 로그 삭제
            delCompileLogFile(deploySystem, ftp);
        }

        // jeusBootCommon
        jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
    }


    /**
     * BATCH L4 1번기
     */
    public static void setDeployL401ByBatch(List<String> classStrList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> strList = new ArrayList<String>();

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(strList, classStrList, fileList);
        //
        // System.out.println("isUpFile BATCH1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("BATCH1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("BATCH1.ftp.id");
        deployFtpPw = props.getProperty("BATCH1.ftp.pw");
        deployFtpPath = props.getProperty("BATCH1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown BATCH::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "BATCH1", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // BATCH1
            /*************************************************
             * BATCH1
             ***************************************************/
            /**
             * BATCH1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  BATCH1::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "BATCH1", props);

            if (classStrList != null && classStrList.size() > 0) {
                // 컴파일 로그 삭제
                delCompileLogFile(deploySystem, ftp);
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * BATCH L4 2번기
     */
    public static void setDeployL402ByBatch(List<String> classStrList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> strList = new ArrayList<String>();

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(strList, classStrList, fileList);
        //
        // System.out.println("isUpFile BATCH1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("BATCH2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("BATCH2.ftp.id");
        deployFtpPw = props.getProperty("BATCH2.ftp.pw");
        deployFtpPath = props.getProperty("BATCH2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown BATCH::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "BATCH2", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // BATCH1
            /*************************************************
             * BATCH1
             ***************************************************/
            /**
             * BATCH1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  BATCH2::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "BATCH2", props);

            if (classStrList != null && classStrList.size() > 0) {
                // 컴파일 로그 삭제
                delCompileLogFile(deploySystem, ftp);
            }

        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * FO_IMAGE L4 1번기
     */
    public static void setDeployL401ByFoImage(List<String> strList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> classStrList = new ArrayList<String>();

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(strList, classStrList, fileList);
        //
        // System.out.println("isUpFile FO_IMAGE1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("FO_IMAGE1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO_IMAGE1.ftp.id");
        deployFtpPw = props.getProperty("FO_IMAGE1.ftp.pw");
        deployFtpPath = props.getProperty("FO_IMAGE1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown FO_IMAGE1::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "FO_IMAGE1", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // FO_IMAGE1
            /*************************************************
             * FO_IMAGE1
             ***************************************************/
            /**
             * FO_IMAGE1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  FO_IMAGE1::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "FO_IMAGE1", props);


            // FO L4 1번기 WEB서버 업로드
            deploySystem = "FO";
            setDeployL401ByFOWeb(strList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);


        }// 파일 백업파일 다운로드 성공여부 if_end
         //
         // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * FO_IMAGE L4 2번기
     */
    public static void setDeployL402ByFoImage(List<String> strList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> classStrList = new ArrayList<String>();

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(strList, classStrList, fileList);
        //
        // System.out.println("isUpFile FO_IMAGE2 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("FO_IMAGE2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("FO_IMAGE2.ftp.id");
        deployFtpPw = props.getProperty("FO_IMAGE2.ftp.pw");
        deployFtpPath = props.getProperty("FO_IMAGE2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown FO_IMAGE2::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "FO_IMAGE2", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // FO_IMAGE2
            /*************************************************
             * FO_IMAGE2
             ***************************************************/
            /**
             * FO_IMAGE2 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  FO_IMAGE2::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "FO_IMAGE2", props);

            // FO L4 2번기 WEB서버 업로드
            deploySystem = "FO";
            setDeployL402ByFOWeb(strList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);



        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * MO_IMAGE L4 1번기
     */
    public static void setDeployL401ByMoImage(List<String> strList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> classStrList = new ArrayList<String>();

        // // 업로드 파일이 모두 있는지 확인
        // boolean isUpFile = uploadfileEqaulCheck(strList, classStrList, fileList);
        //
        // System.out.println("isUpFile MO_IMAGE1 :::" + isUpFile);
        //
        // if (isUpFile) {

        deployFtpIp = props.getProperty("MO_IMAGE1.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO_IMAGE1.ftp.id");
        deployFtpPw = props.getProperty("MO_IMAGE1.ftp.pw");
        deployFtpPath = props.getProperty("MO_IMAGE1.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown MO_IMAGE1::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "MO_IMAGE1", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // MO_IMAGE1
            /*************************************************
             * MO_IMAGE1
             ***************************************************/
            /**
             * MO_IMAGE1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  MO_IMAGE1::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "MO_IMAGE1", props);

            // MO L4 1번기 WEB서버 업로드
            deploySystem = "MO";
            setDeployL401ByMOWeb(strList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);



        }// 파일 백업파일 다운로드 성공여부 if_end

        // }// 업로드 파일가 모두 있는지 여부 체크 fi_end


    }


    /**
     * MO_IMAGE L4 2번기
     */
    public static void setDeployL402ByMoImage(List<String> strList, ArrayList<String> fileList, Properties props, String deployFileDir,
            String deployFileBackDir, String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        List<String> classStrList = new ArrayList<String>();


        deployFtpIp = props.getProperty("MO_IMAGE2.ftp.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty("MO_IMAGE2.ftp.id");
        deployFtpPw = props.getProperty("MO_IMAGE2.ftp.pw");
        deployFtpPath = props.getProperty("MO_IMAGE2.ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        // 기존파일 백업
        boolean isFileBakDown = fileBakDownLoad(strList, classStrList, ftp, deployFtpPath, deployFileBackDir);

        System.out.println("isFileBakDown MO_IMAGE2::" + isFileBakDown);

        if (isFileBakDown) {

            sourceBakupCommandExec(props, "MO_IMAGE2", deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 신규파일 업로드
            // MO_IMAGE2
            /*************************************************
             * MO_IMAGE2
             ***************************************************/
            /**
             * MO_IMAGE2 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload  MO_IMAGE2::");

            // 파일 업로드
            deployFileUpload(strList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    "MO_IMAGE2", props);

            // MO L4 2번기 WEB서버 업로드
            deploySystem = "MO";
            setDeployL402ByMOWeb(strList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deployL4);



        }// 파일 백업파일 다운로드 성공여부 if_end


    }


    /**
     * FO WEB L4 1번기
     */
    public static void setDeployL401ByFOWeb(List<String> foStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";


        deployFtpIp = props.getProperty("FO1.web.ip");
        deployFtpId = props.getProperty("FO1.web.id");
        deployFtpPw = props.getProperty("FO1.web.pw");
        deployFtpPath = props.getProperty("FO1.web.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);


        // 신규파일 업로드
        // FO1
        /*************************************************
         * L4_1번기 : FO1, FO6, LPS1
         ***************************************************/
        /**
         * FO1 WEB 파일 업로드
         **************************************************/
        System.out.println("================================================================");
        System.out.println("WEB Server Upload FO 1번기 ::");
        System.out.println("================================================================");
        System.out.println("");
        System.out.println("deployFileUpload FO1 WEB ::");

        String deploySystemFtp = "FO1";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        boolean isFileUpladSucess = false;
        // FO1 파일 업로드
        isFileUpladSucess =
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);


        if (isFileUpladSucess) {

            /**
             * FO4 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload FO4 WEB ::");

            deployFtpIp = props.getProperty("FO4.web.ip");
            deployFtpId = props.getProperty("FO4.web.id");
            deployFtpPw = props.getProperty("FO4.web.pw");
            deployFtpPath = props.getProperty("FO4.web.path");

            ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

            deploySystemFtp = "FO4";

            // 소스백업
            webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);

            /**
             * FO6 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload FO6 WEB ::");

            deployFtpIp = props.getProperty("FO6.web.ip");
            deployFtpId = props.getProperty("FO6.web.id");
            deployFtpPw = props.getProperty("FO6.web.pw");
            deployFtpPath = props.getProperty("FO6.web.path");

            ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

            deploySystemFtp = "FO6";

            // 소스백업
            webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);


            /**
             * LPS1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload LPS1 ::");

            deployFtpIp = props.getProperty("LPS1.web.ip");
            deployFtpId = props.getProperty("LPS1.web.id");
            deployFtpPw = props.getProperty("LPS1.web.pw");
            deployFtpPath = props.getProperty("LPS1.web.path");

            ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

            deploySystemFtp = "LPS1";
            deploySystem = "LPS";

            // 소스백업
            webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);


        }// 파일 컴파일 성공여부



    }


    /**
     * FO WEB L4 2번기
     */
    public static void setDeployL402ByFOWeb(List<String> foStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {


        /**
         * FO2 WEB 파일 업로드
         **************************************************/
        System.out.println("================================================================");
        System.out.println("WEB Server Upload FO 2번기 ::");
        System.out.println("================================================================");
        System.out.println("");
        System.out.println("deployFileUpload FO2 WEB ::");

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";


        deployFtpIp = props.getProperty("FO2.web.ip");
        deployFtpId = props.getProperty("FO2.web.id");
        deployFtpPw = props.getProperty("FO2.web.pw");
        deployFtpPath = props.getProperty("FO2.web.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);


        String deploySystemFtp = "FO2";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        boolean isFileUpladSucess = false;
        // FO1 파일 업로드
        isFileUpladSucess =
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);



        /**
         * FO3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload FO3 WEB ::");

        deployFtpIp = props.getProperty("FO3.web.ip");
        deployFtpId = props.getProperty("FO3.web.id");
        deployFtpPw = props.getProperty("FO3.web.pw");
        deployFtpPath = props.getProperty("FO3.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "FO3";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * FO5 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload FO5 WEB ::");

        deployFtpIp = props.getProperty("FO5.web.ip");
        deployFtpId = props.getProperty("FO5.web.id");
        deployFtpPw = props.getProperty("FO5.web.pw");
        deployFtpPath = props.getProperty("FO5.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "FO5";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * LPS2 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload LPS2 ::");

        deployFtpIp = props.getProperty("LPS2.web.ip");
        deployFtpId = props.getProperty("LPS2.web.id");
        deployFtpPw = props.getProperty("LPS2.web.pw");
        deployFtpPath = props.getProperty("LPS2.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "LPS2";
        deploySystem = "LPS";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * LPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload LPS3 ::");

        deployFtpIp = props.getProperty("LPS3.web.ip");
        deployFtpId = props.getProperty("LPS3.web.id");
        deployFtpPw = props.getProperty("LPS3.web.pw");
        deployFtpPath = props.getProperty("LPS3.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "LPS3";
        deploySystem = "LPS";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);
    }


    /**
     * MO WEB L4 1번기
     */
    public static void setDeployL401ByMOWeb(List<String> foStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        /**
         * MO1 WEB 파일 업로드
         **************************************************/
        System.out.println("================================================================");
        System.out.println("WEB Server Upload MO1 1번기 ::");
        System.out.println("================================================================");
        System.out.println("");
        System.out.println("deployFileUpload MO1 WEB ::");

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";


        deployFtpIp = props.getProperty("MO1.web.ip");
        deployFtpId = props.getProperty("MO1.web.id");
        deployFtpPw = props.getProperty("MO1.web.pw");
        deployFtpPath = props.getProperty("MO1.web.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);


        String deploySystemFtp = "MO1";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        boolean isFileUpladSucess = false;
        // FO1 파일 업로드
        isFileUpladSucess =
                deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                        deploySystemFtp, props);


        if (isFileUpladSucess) {

            /**
             * MO7 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload MO7 WEB ::");

            deployFtpIp = props.getProperty("MO7.web.ip");
            deployFtpId = props.getProperty("MO7.web.id");
            deployFtpPw = props.getProperty("MO7.web.pw");
            deployFtpPath = props.getProperty("MO7.web.path");

            ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

            deploySystemFtp = "MO7";

            // 소스백업
            webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);



            /**
             * MO4 파일 업로드
             **************************************************/
            // System.out.println("deployFileUpload MO4 WEB ::");
            //
            // deployFtpIp = props.getProperty("MO4.web.ip");
            // deployFtpId = props.getProperty("MO4.web.id");
            // deployFtpPw = props.getProperty("MO4.web.pw");
            // deployFtpPath = props.getProperty("MO4.web.path");
            //
            // ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);
            //
            // deploySystemFtp = "MO4";
            //
            // // 소스백업
            // webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);
            //
            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(3 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            //
            // // 파일 업로드
            // deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
            // deploySystemFtp, props);

            /**
             * MLPS1 파일 업로드
             **************************************************/
            System.out.println("deployFileUpload MLPS1 ::");

            deployFtpIp = props.getProperty("MLPS1.web.ip");
            deployFtpId = props.getProperty("MLPS1.web.id");
            deployFtpPw = props.getProperty("MLPS1.web.pw");
            deployFtpPath = props.getProperty("MLPS1.web.path");

            ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

            deploySystemFtp = "MLPS1";
            deploySystem = "MLPS";

            // 소스백업
            webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

            try {
                // System.out.println("sleep start::");
                Thread.sleep(3 * 1000);
                // System.out.println("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // 파일 업로드
            deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                    deploySystemFtp, props);

        }// 파일 컴파일 성공여부

    }


    /**
     * MO WEB L4 2번기
     */
    public static void setDeployL402ByMOWeb(List<String> foStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deployL4) {

        /**
         * MO WEB 파일 업로드
         **************************************************/
        System.out.println("================================================================");
        System.out.println("WEB Server Upload MO 2번기 ::");
        System.out.println("================================================================");
        System.out.println("");
        System.out.println("deployFileUpload MO5 WEB ::");

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";


        deployFtpIp = props.getProperty("MO5.web.ip");
        deployFtpId = props.getProperty("MO5.web.id");
        deployFtpPw = props.getProperty("MO5.web.pw");
        deployFtpPath = props.getProperty("MO5.web.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        String deploySystemFtp = "MO5";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * MO6 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MO6 WEB ::");

        deployFtpIp = props.getProperty("MO6.web.ip");
        deployFtpId = props.getProperty("MO6.web.id");
        deployFtpPw = props.getProperty("MO6.web.pw");
        deployFtpPath = props.getProperty("MO6.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MO6";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);



        /**
         * MO8 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MO8 WEB ::");

        deployFtpIp = props.getProperty("MO8.web.ip");
        deployFtpId = props.getProperty("MO8.web.id");
        deployFtpPw = props.getProperty("MO8.web.pw");
        deployFtpPath = props.getProperty("MO8.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MO8";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * MLPS2 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MLPS2 ::");

        deployFtpIp = props.getProperty("MLPS2.web.ip");
        deployFtpId = props.getProperty("MLPS2.web.id");
        deployFtpPw = props.getProperty("MLPS2.web.pw");
        deployFtpPath = props.getProperty("MLPS2.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MLPS2";
        deploySystem = "MLPS";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);


        /**
         * MLPS3 파일 업로드
         **************************************************/
        System.out.println("deployFileUpload MLPS3 ::");

        deployFtpIp = props.getProperty("MLPS3.web.ip");
        deployFtpId = props.getProperty("MLPS3.web.id");
        deployFtpPw = props.getProperty("MLPS3.web.pw");
        deployFtpPath = props.getProperty("MLPS3.web.path");

        ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        deploySystemFtp = "MLPS3";
        deploySystem = "MLPS";

        // 소스백업
        webSourceBakupCommandExec(props, deploySystemFtp, deploySystem);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(3 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 파일 업로드
        deployFileUpload(foStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deployL4, deploySystem,
                deploySystemFtp, props);
    }


    /**
     * 
     * 
     * @param files
     * @return
     */
    private static Map<String, Object> getDeployFileListMap(String deploySystem, String deployL4) {

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

        if (deploySystem.equals("BO")) {
            deployUpfileList = DEPLOY_ALL_UPFILE_LIST;
        }

        if (deployL4.equals("02")) {
            deployUpfileList = DEPLOY_ALL_UPFILE_LIST;
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

                            if (deploySystem.equals("BO") || deploySystem.equals("PO")) {
                                if (boDeployStr.contains("api/openapi")) {
                                    // openapi는 BO,PO는 배포하지 않는다
                                } else {
                                    boStrList.add(boDeployStr);
                                }
                            } else {
                                boStrList.add(boDeployStr);
                            }

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

                                if (deploySystem.equals("BO") || deploySystem.equals("PO")) {
                                    if (classDeployStr.contains("api/openapi")) {
                                        // openapi는 BO,PO는 배포하지 않는다
                                    } else {
                                        boClassStrList.add(classDeployStr);
                                    }
                                } else {
                                    boClassStrList.add(classDeployStr);
                                }

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

                                    if (deploySystem.equals("BO") || deploySystem.equals("PO")) {
                                        if (classDeployStr.contains("api/openapi")) {
                                            // openapi는 BO,PO는 배포하지 않는다
                                        } else {
                                            commonClassStrList.add(classDeployStr);
                                        }
                                    } else {
                                        commonClassStrList.add(classDeployStr);
                                    }


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

            if (deploySystem.equals("FO") || deploySystem.equals("FO_IMAGE")) {
                strList = (List<String>) returnMap.get("foStrList");
                classStrList = (List<String>) returnMap.get("foClassStrList");

            } else if (deploySystem.equals("MO") || deploySystem.equals("MO_IMAGE")) {
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
            String deployFileDir, List<String> commonClassStrList, String deployL4, String deploySystem, String deploySystemFtp,
            Properties props) {

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
        // ftp.sshCommandExecByNotLogger(sshCommand);
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
                ftp.sshCommandExecByNotLogger(sshCommand);



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
        // ftp.sshCommandExecByNotLogger(sshCommand);

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

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

            // 복구 소스 생성
            if (targetSystemFtp.equals("TC1") || targetSystemFtp.equals("TC2")) {
                targetSystemFtp = targetSystemFtp.replace("TC", "TO");
            }
            sourceBakupCommandExec(props, targetSystemFtp, targetSystem);


            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(20 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

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

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

            HttpUrlConnectionCommon.curlHttpUrlCall(props, targetSystemFtp);


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
                    commandExec(deployStr, deployFileDir);
                }

            }// isNotEmpty check if_end

        }
        System.out.println("getSvnFile Success!!!");
        System.out.println("============================================================================================");


        try {
            // System.out.println("sleep start::");
            Thread.sleep(5 * 1000);
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

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 정상적 출력
            //
            // System.out.println("Process ===");
            // String line = "";
            // while (line != null) {
            // System.out.println(line);
            // line = reader.readLine();
            // }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private static void sourceBakupCommandExec(Properties props, String targetSystemFtp, String targetSystem) {

        if (targetSystem.contains("BATCH")) {
            sourceBakupCommandExec_bak(props, targetSystemFtp, targetSystem);
        } else {
            System.out.println("2021.10.28 서버 DATA FULL 에러로 인해 수시배포시 백업중지 ");
        }


    }


    private static void sourceBakupCommandExec_bak(Properties props, String targetSystemFtp, String targetSystem) {

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
        }

        String backupDir = wasDir + "backup/";

        String backupsshCommand =
                "cd " + wasDir + ";" + "cp -r " + targetSystem + " " + backupDir + targetSystem + "_" + yyyyMMdd + "_" + HHmmss;
        System.out.println("sourceBakupCommandExec ::::" + backupsshCommand);

        ftp.sshCommandExecByNotLogger(backupsshCommand);

    }


    private static void webSourceBakupCommandExec(Properties props, String targetSystemFtp, String targetSystem) {

        System.out.println("2021.10.28 서버 DATA FULL 에러로 인해 수시배포시 백업중지 ");
    }


    private static void webSourceBakupCommandExec_bak(Properties props, String targetSystemFtp, String targetSystem) {

        String deployFtpIp = "";
        int deployFtpPort = 22;
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        deployFtpIp = props.getProperty(targetSystemFtp + ".web.ip");
        // deployFtpPort = 22;
        deployFtpId = props.getProperty(targetSystemFtp + ".web.id");
        deployFtpPw = props.getProperty(targetSystemFtp + ".web.pw");
        deployFtpPath = props.getProperty(targetSystemFtp + ".web.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

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

        ftp.sshCommandExecByNotLogger(backupsshCommand);

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

        String[] CHECK_FILE_NAME =
                {"jpg", "png", "gif", "jpeg", "js", "css", "ttf", "eot", "avi", "bmp", "cab", "css", "do", "doc", "docx", "dot", "dotx",
                        "dtd", "eot", "exe", "gif", "htm", "html", "hmt", "jfif", "jpe", "jpeg", "jpg", "js", "mpe", "mpeg", "mpegv",
                        "mpg", "mpv", "ocx", "pdf", "pjp", "pjpeg", "png", "potx", "ppsx", "ppt", "pptx", "shtml", "sldx", "swf", "txt",
                        "vbs", "woff", "woff2", "xls", "xlsx", "xlt", "xltx", "zip", "mp4"};

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

        String[] CHECK_FILE_NAME =
                {"jpg", "png", "gif", "jpeg", "js", "css", "xls", "xlsx", "txt", "html", "ttf", "eot", "mp4", "avi", "bmp", "cab", "css",
                        "do", "doc", "docx", "dot", "dotx", "dtd", "eot", "exe", "gif", "htm", "html", "hmt", "jfif", "jpe", "jpeg", "jpg",
                        "js", "mpe", "mpeg", "mpegv", "mpg", "mpv", "ocx", "pdf", "pjp", "pjpeg", "png", "potx", "ppsx", "ppt", "pptx",
                        "shtml", "sldx", "swf", "txt", "vbs", "woff", "woff2", "xls", "xlsx", "xlt", "xltx", "zip", "mp4"};

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
            ftp.sshCommandExecByNotLogger(command);
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



}
