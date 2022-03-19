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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import utils.DateUtils;
import utils.HttpUrlConnectionCommon;
import utils.SFTPService;

@Slf4j
@Data
public class DeployWithPropertiesThread implements Runnable {

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

    static boolean IS_DEPLOY_SUCCESS = true;

    List<String> viewStrList = null;
    List<String> imageStrList = null;

    List<String> classStrList = null;
    List<String> commonClassStrList = null;
    Properties props = null;
    String deploySystemFtp = null;
    boolean isRebootServer = false;



    @Override
    public void run() {

        String deployFileDir = DEPLOY_FILE_DIR;

        System.out.println("==============================================");
        System.out.println("DeployWithPropertiesThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("deploySystemFtp =" + deploySystemFtp);
        System.out.println("==============================================");


        String deploySystem = removeRex("[0-9]", deploySystemFtp);

        int deployFtpPort = 22;
        String deployFtpIp = props.getProperty(deploySystemFtp + ".ftp.ip");
        String deployFtpId = props.getProperty(deploySystemFtp + ".ftp.id");
        String deployFtpPw = props.getProperty(deploySystemFtp + ".ftp.pw");
        String deployFtpPath = props.getProperty(deploySystemFtp + ".ftp.path");

        SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);

        setDeployCommon(viewStrList, classStrList, props, deployFileDir, deploySystem, commonClassStrList, deploySystemFtp, ftp,
                deployFtpPath, isRebootServer);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (!IS_DEPLOY_SUCCESS) {
            throw new RuntimeException("Build Fail !!!!!!!!!!!!!!!!!!!!!!!");
        }

        System.out.println("==============================================");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("deploySystemFtp =" + deploySystemFtp);
        System.out.println("DeployWithPropertiesThread END");
        System.out.println("==============================================");

    }


    public DeployWithPropertiesThread(Properties props, List<String> viewStrList, List<String> classStrList,
            List<String> commonClassStrList, String deploySystemFtp, boolean isRebootServer) {

        this.props = props;
        this.viewStrList = viewStrList;
        this.classStrList = classStrList;
        this.commonClassStrList = commonClassStrList;
        this.deploySystemFtp = deploySystemFtp;
        this.isRebootServer = isRebootServer;
    }


    /**
     * setDeployCommon
     */
    public static void setDeployCommon(List<String> viewStrList, List<String> classStrList, Properties props, String deployFileDir,
            String deploySystem, List<String> commonClassStrList, String deploySystemFtp, SFTPService ftp, String deployFtpPath,
            boolean isRebootServer) {

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon START::");
        System.out.println("###############################################");

        if (classStrList == null) {
            isRebootServer = false;
        } else {

            if (classStrList.size() == 0) {
                isRebootServer = false;
            } else {
                isRebootServer = true;
            }
        }

        if (isRebootServer) {
            // jeusShutDownCommon
            // 소스백업
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "shutdown");
        } else {
            // 소스백업
            sourceBakupCommandExec(props, deploySystemFtp, deploySystem);
        }

        // 파일 업로드 및 컴파일
        deployFileUpload(viewStrList, classStrList, ftp, deployFtpPath, deployFileDir, commonClassStrList, deploySystem, deploySystemFtp,
                props);

        if (isRebootServer) {
            if (classStrList != null && classStrList.size() > 0) {
                // 컴파일 로그 삭제
                delCompileLogFile(deploySystem, ftp);
            }

            // jeusBootCommon
            jeusShutDownBootCommon(props, deploySystem, deploySystemFtp, "boot");
        }

        System.out.println("###############################################");
        System.out.println("deploySystemFtp[ " + deploySystemFtp + " ] DeployCommon END::");
        System.out.println("###############################################");
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

        if (!deploySystem.equals("BATCH")) {
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


            // if (deploySystem.equals("TO") || deploySystem.equals("BO") || deploySystem.equals("PO") || deploySystem.equals("CC")) {
            // System.out.println(" 웹서버로 업로드 ===::" + cdDir);
            // System.out.println(" 웹서버로 업로드 fileExtCheckByBo(fileNm) ===::" + fileExtCheckByBo(fileNm));
            // System.out.println(" 웹서버로 업로드 web_deployFtpPath ===::" + web_deployFtpPath);
            // if (fileExtCheckByBo(fileNm)) {
            // // 웹서버로 업로드
            // web_ftp.upload(deployFileDir + cdDir + fileNm, web_deployFtpPath + getChageWasFullPathFileName(cdDir));
            // } else {
            // ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
            // }
            // } else {
            // ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));
            // }

            ftp.upload(deployFileDir + cdDir + fileNm, deployFtpPath + getChageWasFullPathFileName(cdDir));

            if (!deploySystem.equals("BATCH")) {
                if (fileExtCheckByBo(fileNm)) {
                    // 웹서버로 업로드
                    System.out.println(" 웹서버로 업로드 ===::" + cdDir);
                    System.out.println(" 웹서버로 업로드 fileExtCheckByBo(fileNm) ===::" + fileExtCheckByBo(fileNm));
                    System.out.println(" 웹서버로 업로드 web_deployFtpPath ===::" + web_deployFtpPath);
                    if (fileExtCheckByBo(fileNm)) {
                        web_ftp.upload(deployFileDir + cdDir + fileNm, web_deployFtpPath + getChageWasFullPathFileName(cdDir));
                    }
                }

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

        System.out.println("2021.10.28 서버 DATA FULL 에러로 인해 수시배포시 백업중지 ");
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
                {"jpg", "png", "gif", "jpeg", "js", "css", "html", "ttf", "eot", "woff", "woff2", "mp4", "avi", "bmp", "cab", "css", "do",
                        "doc", "docx", "dot", "dotx", "dtd", "eot", "exe", "gif", "htm", "html", "hmt", "jfif", "jpe", "jpeg", "jpg", "js",
                        "mpe", "mpeg", "mpegv", "mpg", "mpv", "ocx", "pdf", "pjp", "pjpeg", "png", "potx", "ppsx", "ppt", "pptx", "shtml",
                        "sldx", "swf", "txt", "vbs", "woff", "woff2", "xls", "xlsx", "xlt", "xltx", "zip", "mp4", "svg"};

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
                        "shtml", "sldx", "swf", "txt", "vbs", "woff", "woff2", "xls", "xlsx", "xlt", "xltx", "zip", "mp4", "svg"};

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


    public static String removeRex(String rex, String inp) {

        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");

        return inp;

    }


}
