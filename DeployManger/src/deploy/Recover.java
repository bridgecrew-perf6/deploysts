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
import java.util.List;
import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.DateUtils;
import utils.SFTPService;
import utils.StringUtils;

@Slf4j
@Data
public class Recover {

    static final String JEUS_HOME = "/data/webapps/JEUS";
    static final String RECOVER_DIR = "/data/home/hisis/ec/tools/deploy/recover/";


    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};


    public Recover() {

    }


    public Recover(Properties props, String targetServer, String recoverSystem, String spareDeployYn, String spareOnlyYn,
            String targetNumber, String targetDate) {

        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String targetSystemFtp = "";
        String targetSystem = "";
        String recoverL4 = "";

        String[] setWasArr = null;
        String[] setSpareWasArr = null;

        if (recoverSystem.equals("FO_SET_01") || recoverSystem.equals("FO_IMAGE1")) {
            setWasArr = FO_SET_01;
            setSpareWasArr = SPARE_FO_SET_01;
        } else if (recoverSystem.equals("FO_SET_02") || recoverSystem.equals("FO_IMAGE2")) {
            setWasArr = FO_SET_02;
            setSpareWasArr = SPARE_FO_SET_02;
        } else if (recoverSystem.equals("MO_SET_01") || recoverSystem.equals("MO_IMAGE1")) {
            setWasArr = MO_SET_01;
            setSpareWasArr = SPARE_MO_SET_01;
        } else if (recoverSystem.equals("MO_SET_02") || recoverSystem.equals("MO_IMAGE2")) {
            setWasArr = MO_SET_02;
            setSpareWasArr = SPARE_MO_SET_02;
        }


        if ("Y".equals(spareOnlyYn)) {
            setWasArr = new String[] {};
        }


        if (recoverSystem.contains("SET")) {

            String[] recoverSystemToken = recoverSystem.split("_");
            targetSystem = recoverSystemToken[0];
            recoverL4 = recoverSystemToken[2];

            RecoverThread deployThread = null;

            for (String element : setWasArr) {
                deployThread = new RecoverThread(props, targetDate, targetNumber, todate, targetSystem, element, targetServer);

                Thread thread = new Thread(deployThread, element);

                thread.start();

                Thread.currentThread().getName();

            }

            RecoverThread deployThread2 = null;

            // 예비서버 디플로이
            if ("Y".equals(spareDeployYn)) {

                for (String element_spare : setSpareWasArr) {

                    deployThread2 = new RecoverThread(props, targetDate, targetNumber, todate, targetSystem, element_spare, targetServer);

                    Thread thread2 = new Thread(deployThread2, element_spare);

                    thread2.start();

                    Thread.currentThread().getName();
                }
            }


        } else {

            targetSystemFtp = recoverSystem;
            targetSystem = StringUtils.removeRex("[0-9]", recoverSystem);

            System.out.println("recoverSystem ::" + recoverSystem);
            System.out.println("targetSystemFtp ::" + targetSystemFtp);
            System.out.println("targetSystem ::" + targetSystem);

            if (targetServer.equals("test")) {
                targetSystem = recoverSystem;
                targetSystemFtp = recoverSystem + "_TST";
            }

            setRecoverCommon(props, targetDate, targetNumber, todate, targetSystem, targetSystemFtp, targetServer);

            if (recoverSystem.contains("IMAGE")) {
                new DeployOnServerByZiping(props, recoverSystem, spareDeployYn, spareOnlyYn);
            }
        }


    }


    public Recover(Properties props, String targetServer, String recoverSystem, String targetNumber, String targetDate) {

        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String targetSystemFtp = "";
        String targetSystem = "";
        String recoverL4 = "";

        if (recoverSystem.contains("SET")) {
            String[] recoverSystemToken = recoverSystem.split("_");
            targetSystem = recoverSystemToken[0];
            recoverL4 = recoverSystemToken[2];

            String[] setWasArr = propServerSet(props, recoverSystem);

            for (String element : setWasArr) {

                targetSystemFtp = element;
                targetSystem = StringUtils.removeRex("[0-9]", element);

                setRecoverCommon(props, targetDate, targetNumber, todate, targetSystem, targetSystemFtp, targetServer);
            }


        } else {

            targetSystemFtp = recoverSystem;
            targetSystem = StringUtils.removeRex("[0-9]", recoverSystem);

            System.out.println("recoverSystem ::" + recoverSystem);
            System.out.println("targetSystemFtp ::" + targetSystemFtp);
            System.out.println("targetSystem ::" + targetSystem);

            if (targetServer.equals("test")) {
                targetSystem = recoverSystem;
                targetSystemFtp = recoverSystem + "_TST";
            }

            setRecoverCommon(props, targetDate, targetNumber, todate, targetSystem, targetSystemFtp, targetServer);
        }


    }


    public static String[] propServerSet(Properties props, String recoverSystem) {

        String[] setWasArr = null;

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


        if (recoverSystem.equals("FO_SET_01")) {
            setWasArr = FO_SET_01;
        } else if (recoverSystem.equals("FO_SET_02")) {
            setWasArr = FO_SET_02;
        } else if (recoverSystem.equals("MO_SET_01")) {
            setWasArr = MO_SET_01;
        } else if (recoverSystem.equals("MO_SET_02")) {
            setWasArr = MO_SET_02;
        } else if (recoverSystem.equals("SPARE_FO_SET_01")) {
            setWasArr = SPARE_FO_SET_01;
        } else if (recoverSystem.equals("SPARE_FO_SET_02")) {
            setWasArr = SPARE_FO_SET_02;
        } else if (recoverSystem.equals("SPARE_MO_SET_01")) {
            setWasArr = SPARE_MO_SET_01;
        } else if (recoverSystem.equals("SPARE_MO_SET_02")) {
            setWasArr = SPARE_MO_SET_02;
        }

        return setWasArr;
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
     * Recover 공통
     */
    public static void setRecoverCommon(Properties props, String targetSystem, String targetSystemFtp) {

        String targetDate = DateUtils.getDate("yyyyMMdd");
        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String targetNumber = "99";
        String targetServer = "real";

        setRecoverCommon(props, targetDate, targetNumber, todate, targetSystem, targetSystemFtp, targetServer);
    }


    /**
     * Recover 공통
     */
    public static void setRecoverCommon(Properties props, String targetDate, String targetNumber, String todate, String targetSystem,
            String targetSystemFtp, String targetServer) {

        String recoverFtpIp = "";
        int recoverFtpPort = 22;
        String recoverFtpId = "";
        String recoverFtpPw = "";
        String recoverSystem = "";

        String wasDir = props.getProperty("defaultDir") + "/WAS/";

        if (targetSystem.contains("BATCH")) {
            wasDir = props.getProperty("defaultDir") + "/BATCH/";
        }

        if (targetSystemFtp.contains("FO_IMAGE") || targetSystemFtp.contains("MO_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystemFtp.contains("FO_IMAGE")) {
                targetSystem = "FO";
            } else if (targetSystemFtp.contains("MO_IMAGE")) {
                targetSystem = "MO";
            }
        }

        recoverSystem = targetSystem;

        if (targetSystem.equals("MLPS")) {
            recoverSystem = "mLPS";
        }

        String sshCommand = "";

        String backupDir = wasDir + "/backup/";

        log.info("backupDir :::::" + backupDir);

        recoverFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
        recoverFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
        recoverFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

        log.info("recoverFtpIp :::::" + recoverFtpIp);

        SFTPService ftp = new SFTPService(recoverFtpIp, recoverFtpPort, recoverFtpId, recoverFtpPw);


        String str;
        String[] strArr;

        String recoverDir = "";
        int recoverListInt = 0;


        List<String> recoverDirList = getRecoverDirListCommon(props, ftp, targetDate, todate, targetSystem, targetSystemFtp, targetServer);

        log.info("recoverDirList.size() :::::" + recoverDirList.size());

        if (recoverDirList.size() > 0) {

            // 목록 정렬
            Collections.sort(recoverDirList);

            if (targetNumber.equals("99")) {
                recoverListInt = recoverDirList.size() - 1;
            } else {
                recoverListInt = Integer.parseInt(targetNumber);
                if (recoverListInt > 0) {
                    recoverListInt = recoverListInt - 1;
                }
            }

            recoverDir = recoverDirList.get(recoverListInt);

            System.out.println("=========================================================================");
            System.out.println("#recoverDir :: " + recoverDir);
            System.out.println("=========================================================================");

            if (targetSystem.equals("batch") || targetSystem.equals("BATCH") || targetSystemFtp.contains("FO_IMAGE")
                    || targetSystemFtp.contains("MO_IMAGE")) {
                // batch일경우
                // 현재 WAS파일 백업
                // sshCommand = "cd " + wasDir + ";" + "cp -r " + recoverSystem + " " + backupDir + recoverSystem + "_recover_" + todate;
                sshCommand = "cd " + wasDir + ";" + "cp -r " + recoverSystem + " " + backupDir + recoverSystem + "_" + todate + "_recover";
                ftp.sshCommandExecByShellScriptNoLog(sshCommand);

                System.out.println("#");
                System.out.println("#sshCommand WAS BAKUP ::" + sshCommand);
                System.out.println("##############################################################################");
                try {
                    // log.info("sleep start::");
                    Thread.sleep(1 * 1000);
                    // log.info("sleep end::");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // 백업파일 WAS파일로 교체
                sshCommand =
                        "rm -r " + wasDir + recoverSystem + ";" + "cd " + backupDir + ";" + "cp -r " + recoverDir + " " + wasDir
                                + recoverSystem;
                ftp.sshCommandExecByShellScriptNoLog(sshCommand);
                System.out.println("#");
                System.out.println("#sshCommand WAS CP ::" + sshCommand);
                System.out.println("##############################################################################");

            } else {

                // jeusShutDownCommon
                jeusShutDownBootCommon(props, targetServer, targetSystem, targetSystemFtp, "shutdown");

                // if (targetSystemFtp.equals("MLPS1") || targetSystemFtp.equals("MLPS2") || targetSystemFtp.equals("MLPS3")) {
                // targetSystem = "mLPS";
                // }

                // 현재 WAS파일 백업
                sshCommand = "cd " + wasDir + ";" + "cp -r " + recoverSystem + " " + backupDir + recoverSystem + "_recover_" + todate;
                ftp.sshCommandExecByShellScriptNoLog(sshCommand);
                System.out.println("#");
                System.out.println("#sshCommand WAS BAKUP ::" + sshCommand);
                System.out.println("##############################################################################");

                try {
                    // log.info("sleep start::");
                    Thread.sleep(1 * 1000);
                    // log.info("sleep end::");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                log.info("recoverDir ::" + recoverDir);

                // 백업파일 WAS파일로 교체
                sshCommand =
                        "rm -r " + wasDir + recoverSystem + ";" + "cd " + backupDir + ";" + "cp -r " + recoverDir + " " + wasDir
                                + recoverSystem;
                ftp.sshCommandExecByShellScriptNoLog(sshCommand);
                System.out.println("#");
                System.out.println("#sshCommand WAS CP ::" + sshCommand);
                System.out.println("##############################################################################");

                // jeusBootCommon
                jeusShutDownBootCommon(props, targetServer, targetSystem, targetSystemFtp, "boot");

            }// recoverDirList.size() > 0 check if_end

        }// batch 여부 if_end
    }


    public void getRecoverDirList(Properties props, String targetDate, String recoverSystem, String targetServer) {


        String todate = DateUtils.getDate("yyyyMMddHHmmss");
        String targetSystemFtp = "";
        String targetSystem = "";
        String recoverL4 = "";

        String recoverFtpIp = "";
        int recoverFtpPort = 22;
        String recoverFtpId = "";
        String recoverFtpPw = "";

        String recoverWebIp = "";
        int recoverWebPort = 22;
        String recoverWebId = "";
        String recoverWebPw = "";
        String recoverWebPath = "";

        List<String> recoverDirList = null;
        SFTPService ftp = null;
        SFTPService web_ftp = null;

        if (recoverSystem.contains("SET")) {

            String[] setWasArr = propServerSet(props, recoverSystem);

            for (String element : setWasArr) {

                targetSystemFtp = element;
                targetSystem = StringUtils.removeRex("[0-9]", element);

                recoverFtpIp = props.getProperty(element + ".ftp.ip");
                recoverFtpId = props.getProperty(element + ".ftp.id");
                recoverFtpPw = props.getProperty(element + ".ftp.pw");

                recoverWebIp = props.getProperty(element + ".web.ip");
                recoverWebId = props.getProperty(element + ".web.id");
                recoverWebPw = props.getProperty(element + ".web.pw");
                recoverWebPath = props.getProperty(element + ".web.path");

                // WAS서버
                ftp = new SFTPService(recoverFtpIp, recoverFtpPort, recoverFtpId, recoverFtpPw);

                recoverDirList = getRecoverDirListCommon(props, ftp, targetDate, todate, targetSystem, targetSystemFtp, targetServer);

                if (recoverDirList.size() > 0) {

                    // 목록 정렬
                    Collections.sort(recoverDirList);
                    System.out.println("########################################################################");
                    System.out.println("#WAS SERVER ");
                    System.out.println("########################################################################");
                    System.out.println("#" + element);
                    System.out.println("=========================================================================");
                    for (int i = 0; i < recoverDirList.size(); i++) {
                        System.out.println("# " + (i + 1) + " 번째 백업디렉토리 : " + recoverDirList.get(i));
                    }
                    System.out.println("=========================================================================");
                    System.out.println("");
                }

            }

        } else {

            targetSystemFtp = recoverSystem;
            targetSystem = StringUtils.removeRex("[0-9]", recoverSystem);

            if (targetServer.equals("test")) {
                targetSystem = recoverSystem;
                targetSystemFtp = recoverSystem + "_TST";
            }

            recoverFtpIp = props.getProperty(targetSystemFtp + ".ftp.ip");
            recoverFtpId = props.getProperty(targetSystemFtp + ".ftp.id");
            recoverFtpPw = props.getProperty(targetSystemFtp + ".ftp.pw");

            recoverWebIp = props.getProperty(targetSystemFtp + ".web.ip");
            recoverWebId = props.getProperty(targetSystemFtp + ".web.id");
            recoverWebPw = props.getProperty(targetSystemFtp + ".web.pw");
            recoverWebPath = props.getProperty(targetSystemFtp + ".web.path");

            ftp = new SFTPService(recoverFtpIp, recoverFtpPort, recoverFtpId, recoverFtpPw);

            recoverDirList = getRecoverDirListCommon(props, ftp, targetDate, todate, targetSystem, targetSystemFtp, targetServer);

            if (recoverDirList.size() > 0) {

                // 목록 정렬
                Collections.sort(recoverDirList);
                System.out.println("#" + targetSystemFtp);
                System.out.println("=========================================================================");
                for (int i = 0; i < recoverDirList.size(); i++) {
                    System.out.println("# " + (i + 1) + " 번째 백업디렉토리 : " + recoverDirList.get(i));
                }
                System.out.println("=========================================================================");
            }


        }// SET_check_if_end



    }


    public static List<String> getRecoverDirListCommon(Properties props, SFTPService ftp, String targetDate, String todate,
            String targetSystem, String targetSystemFtp, String targetServer) {

        List<String> recoverDirList = new ArrayList<String>();

        String wasDir = props.getProperty("defaultDir") + "/WAS/";

        if (targetSystem.contains("BATCH")) {
            wasDir = props.getProperty("defaultDir") + "/BATCH/";
        }

        if (targetSystemFtp.contains("FO_IMAGE") || targetSystemFtp.contains("MO_IMAGE")) {
            wasDir = props.getProperty("defaultDir") + "/WEB/";

            if (targetSystemFtp.contains("FO_IMAGE")) {
                targetSystem = "FO";
            } else if (targetSystemFtp.contains("MO_IMAGE")) {
                targetSystem = "MO";
            }
        }

        String recoverSystem = targetSystem;

        if (targetSystem.equals("MLPS")) {
            recoverSystem = "mLPS";
        }

        String backupDir = wasDir + "/backup/";
        String recoverListTxt = "RECOVER_LIST_" + todate + ".txt";

        // 백업된 디렉토리 목록 파일 생성
        // String sshCommand = "cd " + backupDir + ";" + "echo " + recoverSystem + "_" + targetDate + "_* > " + recoverListTxt;
        String sshCommand = "cd " + backupDir + ";" + "echo " + recoverSystem + "_" + targetDate + "* > " + recoverListTxt;

        log.info("sshCommand " + sshCommand);

        ftp.sshCommandExecByShellScriptNoLog(sshCommand);

        // 목록 파일 다운로드
        String recoverDirListFile = backupDir + recoverListTxt;
        ftp.downloadFile(recoverDirListFile, RECOVER_DIR);

        try {
            log.info("sleep start::");
            Thread.sleep(5 * 1000);
            log.info("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File f = new File(RECOVER_DIR + recoverListTxt);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;
        String[] strArr;


        if (f.exists()) {

            try {
                is = new FileInputStream(RECOVER_DIR + recoverListTxt);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {
                    strArr = str.split(" ");
                    if (strArr != null) {
                        for (String element : strArr) {

                            // if (element.equals(recoverSystem + "_" + targetDate + "_*")) {
                            // continue;
                            // } else {
                            // System.out.println("#" + element);
                            // recoverDirList.add(element);
                            // }

                            System.out.println("#" + element);
                            recoverDirList.add(element);
                        }
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

                    // File logOrgFile = new File(cdnFlushFile);
                    // FileUtils.moveFile(logOrgFile, new File(cdnFlushFileBakupFile));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }// f.exists if_end

        return recoverDirList;
    }


    public static List<String> getRecoverDirListCommonByWeb(Properties props, SFTPService web_ftp, String targetDate, String todate,
            String targetSystem, String targetSystemFtp, String targetServer) {

        List<String> recoverDirList = new ArrayList<String>();

        String webDir = props.getProperty("defaultDir") + "/WEB/";

        String recoverSystem = targetSystem;

        if (targetSystem.equals("MLPS")) {
            recoverSystem = "mLPS";
        }

        String backupDir = webDir + "/backup/";
        String recoverListTxt = "RECOVER_LIST_WEB" + todate + ".txt";

        // 백업된 디렉토리 목록 파일 생성
        // String sshCommand = "cd " + backupDir + ";" + "echo " + recoverSystem + "_" + targetDate + "_* > " + recoverListTxt;
        String sshCommand = "cd " + backupDir + ";" + "echo " + recoverSystem + "_" + targetDate + "* > " + recoverListTxt;

        log.info("sshCommand " + sshCommand);

        web_ftp.sshCommandExecByShellScriptNoLog(sshCommand);

        // 목록 파일 다운로드
        String recoverDirListFile = backupDir + recoverListTxt;
        web_ftp.downloadFile(recoverDirListFile, RECOVER_DIR);

        try {
            log.info("sleep start::");
            Thread.sleep(5 * 1000);
            log.info("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File f = new File(RECOVER_DIR + recoverListTxt);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;
        String[] strArr;


        if (f.exists()) {

            try {
                is = new FileInputStream(RECOVER_DIR + recoverListTxt);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {
                    strArr = str.split(" ");
                    if (strArr != null) {
                        for (String element : strArr) {

                            // if (element.equals(recoverSystem + "_" + targetDate + "_*")) {
                            // continue;
                            // } else {
                            // System.out.println("#" + element);
                            // recoverDirList.add(element);
                            // }

                            System.out.println("#" + element);
                            recoverDirList.add(element);
                        }
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

                    // File logOrgFile = new File(cdnFlushFile);
                    // FileUtils.moveFile(logOrgFile, new File(cdnFlushFileBakupFile));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }// f.exists if_end

        return recoverDirList;
    }



    /**
     * Recover 재부팅 공통
     */
    public static void setRecoverRebootCommon(Properties props, String targetServer, String targetSystem, String targetSystemFtp) {

        // jeusShutDownCommon
        jeusShutDownBootCommon(props, targetServer, targetSystem, targetSystemFtp, "shutdown");

        // jeusBootCommon
        jeusShutDownBootCommon(props, targetServer, targetSystem, targetSystemFtp, "boot");
    }


    /**
     * Jeus jeusShutDownBootCommon 공통
     */
    public static void jeusShutDownBootCommon(Properties props, String targetServer, String targetSystem, String targetSystemFtp,
            String rebootFlag) {

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

        if (targetServer.equals("test")) {

            rebootFtpIp = props.getProperty(targetSystem + "_TST.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "_TST.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "_TST.ftp.pw");

            jeusAddr = props.getProperty(targetSystem + "_TST.jeusAddr");

            if (targetSystem.equals("TO") || targetSystem.equals("CC")) {
                jeusStartNm = "server3";
            } else if (targetSystem.equals("LPS") || targetSystem.equals("MLPS") || targetSystem.equals("PO")) {
                jeusStartNm = "server2";
            } else if (targetSystem.equals("TOLPS")) {
                jeusStartNm = "server4";
            } else {
                jeusStartNm = "server1";
            }

            jeusStartPort = props.getProperty(targetSystem + "_TST.jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + "_TST.jeusBootHost");

        } else {

            rebootFtpIp = props.getProperty(targetSystem + "1.ftp.ip");
            rebootFtpId = props.getProperty(targetSystem + "1.ftp.id");
            rebootFtpPw = props.getProperty(targetSystem + "1.ftp.pw");

            jeusAddr = props.getProperty(targetSystem + ".jeusAddr");
            jeusStartNm = targetSystemFtp;
            jeusStartPort = props.getProperty(targetSystem + ".jeusStartPort");
            jeusBootHost = props.getProperty(targetSystem + ".jeusBootHost");

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


            log.info("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);

            // try {
            // // log.info("sleep start::");
            // Thread.sleep(20 * 1000);
            // // log.info("sleep end::");
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


            log.info("jeusReBootCommon [" + rebootFlag + "] :: " + bootSsshCommand);

            try {
                // log.info("sleep start::");
                Thread.sleep(5 * 1000);
                // log.info("sleep end::");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            rebootftp.sshCommandExecByNotLogger(bootSsshCommand);
        }


        log.info("jeusReBootCommon [" + rebootFlag + "]  [" + targetSystemFtp + "]  END  ====================::");

    }
}
