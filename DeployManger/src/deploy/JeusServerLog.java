package deploy;


import java.io.File;
import java.io.IOException;
import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import utils.DateUtils;
import utils.SFTPService;
import utils.Zip;

@Slf4j
@Data
public class JeusServerLog {

    static final String JEUS_LOG_DEFAULT_DIR = "/data/webapps/JEUS/domains/";
    static final String JEUS_TEST_LOG_DEFAULT_DIR = "/data/webapps/JEUS/domains/jeus_domain/servers/";

    // static final String LOCAL_DEFAULT_DIR = "/data/home/hisis/ec/tools/deploy/waslog/";
    static final String LOCAL_DEFAULT_DIR = "/data/home/hisis/ec/tools/hudson/apache-tomcat-7.0.59/webapps/ROOT/waslog/";
    static final String LOCAL_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/waslog/temp/";
    static String ZIP_FILE_NM = null;

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};

    static String[] SPARE_MO_SET_01_ALL = new String[] {};
    static String[] SPARE_MO_SET_02_ALL = new String[] {};


    public JeusServerLog(Properties props, String targetL4, String targetServer, String waslogSystem, String targetDate) {

        // 서버 프로퍼티 셋팅
        propServerSet(props);

        String todate = DateUtils.getDate("yyyyMMddHHmm");

        String waslogSystemFtp = "";
        String waslogTempPath = LOCAL_TEMP_DIR + waslogSystem;
        String tempPath = "";

        if (targetServer.equals("real")) {
            // Real 서버
            if (waslogSystem.equals("FO")) {
                if (targetL4.equals("01")) {
                    // 운영1번기
                    for (String element : FO_SET_01) {

                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                } else if (targetL4.equals("02")) {
                    // 운영2번기
                    for (String element : FO_SET_02) {

                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }

                    }

                } else if (targetL4.equals("04")) {

                    waslogSystemFtp = "FO4";
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);

                } else if (targetL4.equals("spare")) {
                    // spare서버 전체
                    for (String element : SPARE_FO_SET_01) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                    }

                    for (String element : SPARE_FO_SET_02) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                    }

                } else {
                    // spare제외 모든 서버
                    for (String element : FO_SET_01) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                    for (String element : FO_SET_02) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                }

            } else if (waslogSystem.equals("MO")) {
                if (targetL4.equals("01")) {
                    // 운영1번기
                    for (String element : MO_SET_01) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                } else if (targetL4.equals("02")) {
                    // 운영2번기
                    for (String element : MO_SET_02) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                } else if (targetL4.equals("04")) {

                    waslogSystemFtp = "MO4";
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);

                } else if (targetL4.equals("spare")) {
                    // spare서버 전체
                    for (String element : SPARE_MO_SET_01_ALL) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                    }

                    for (String element : SPARE_MO_SET_02_ALL) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                    }

                } else {
                    // spare제외 모든 서버
                    for (String element : MO_SET_01) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                    for (String element : MO_SET_02) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                        }
                    }

                }

            } else if (waslogSystem.equals("LPS") || waslogSystem.equals("MLPS")) {
                // 서버가 3대씩

                if (targetL4.equals("01")) {
                    waslogSystemFtp = waslogSystem + "1";
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                } else {

                    for (int i = 1; i <= 3; i++) {
                        waslogSystemFtp = waslogSystem + i;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);
                    }
                }

            } else {
                // 나머지는 모두 서버가 2대씩
                for (int i = 1; i <= 2; i++) {
                    waslogSystemFtp = waslogSystem + i;
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystemFtp);

                }

            }
        } else {
            // TST, DEV는 서버가 한대씩
            tempPath = waslogTempPath;
            makeDiretory(tempPath);

            getWasLogCommon(props, tempPath, targetDate, todate, targetServer, waslogSystem);

        } // targetServer check if_end



        /*************************************************
         * 경로설정
         ***************************************************/
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_" + waslogSystem + "_" + todate + "_" + targetServer + ".zip";
            if (targetServer.equals("real")) {
                zipFile = LOCAL_DEFAULT_DIR + "_jeus_" + waslogSystem + "_" + todate + "_" + targetServer + "_" + targetL4 + ".zip";
            }

        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_" + waslogSystem + "_" + targetDate + "_" + targetServer + ".zip";
            if (targetServer.equals("real")) {
                zipFile = LOCAL_DEFAULT_DIR + "_jeus_" + waslogSystem + "_" + targetDate + "_" + targetServer + "_" + targetL4 + ".zip";
            }
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        try {

            // 압축하기 Zip.compress("c:\\deploy/waslog/temp/front/", "c:\\deploy/waslog/front.zip");
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        System.out.println("######################################################################");
        System.out.println("DOWNLOAD  Click Url ");
        System.out.println("######################################################################");
        // System.out.println("<a href='http://10.154.17.205:8081/waslog/" + ZIP_FILE_NM + "' download='WasLog'>" + ZIP_FILE_NM +
        // " 다운로드</a>");
        System.out.println("");
        System.out.println("http://10.154.17.205:8081/waslog/" + ZIP_FILE_NM);
        System.out.println("");
        System.out.println("######################################################################");

    }


    public void JeusServerLog_bak(Properties props, String targetL4, String targetServer, String waslogSystem, String targetDate) {

        String fo = "fo_domain/servers/";
        String lps = "lps_domain/servers/";
        String mo = "mo_domain/servers/";
        String mlps = "mlps_domain/servers/";
        String to = "tc_domain/servers/";
        String bo = "bo_domain/servers/";
        String po = "po_domain/servers/";
        String cc = "cc_domain/servers/";

        String jeuslogDir = "";
        String logNm = "";

        // /data/webapps/JEUS/domains/po_domain/servers/PO1/logs>

        if (targetServer.equals("real")) {
            // Real 서버
            if (waslogSystem.equals("FO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + fo;
                getWasLogFOByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("LPS")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + lps;
                getWasLogLPSByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("MO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + mo;
                getWasLogMOByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("MLPS")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + mlps;
                getWasLogMLPSByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("BO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + bo;
                getWasLogBOByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("PO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + po;
                getWasLogPOByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("CC")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + cc;
                getWasLogCCByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("TO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + to;
                getWasLogTOByReal(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);
            }
        } else if (targetServer.equals("test")) {
            // Test 서버

            jeuslogDir = JEUS_TEST_LOG_DEFAULT_DIR;

            if (waslogSystem.equals("FO")) {
                getWasLogFOByTest(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("MO")) {
                getWasLogMOByTest(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("BO")) {
                getWasLogBOByTest(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("TO")) {
                getWasLogTOByTest(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);
            }

        } else if (targetServer.equals("staging")) {
            // STAGING 서버
            if (waslogSystem.equals("FO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + fo;
                getWasLogFOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("MO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + mo;
                getWasLogMOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("BO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + bo;
                getWasLogBOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            }

        } else if (targetServer.equals("dev")) {
            // DEV 서버
            if (waslogSystem.equals("FO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + fo;
                getWasLogFOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("MO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + mo;
                getWasLogMOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            } else if (waslogSystem.equals("BO")) {
                jeuslogDir = JEUS_LOG_DEFAULT_DIR + bo;
                getWasLogBOByStaging(props, targetL4, targetDate, jeuslogDir, waslogSystem, targetServer);

            }

        } // targetServer check if_end


        System.out.println("######################################################################");
        System.out.println("DOWNLOAD  Click Url ");
        System.out.println("######################################################################");
        // System.out.println("<a href='http://10.154.17.205:8081/waslog/" + ZIP_FILE_NM + "' download='WasLog'>" + ZIP_FILE_NM +
        // " 다운로드</a>");
        System.out.println("");
        System.out.println("http://10.154.17.205:8081/waslog/" + ZIP_FILE_NM);
        System.out.println("");
        System.out.println("######################################################################");

    }


    /**
     * Real FO 로그 다운
     */
    public static void getWasLogFOByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * FO1, FO2, FO3, FO5, FO6
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "front";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * FO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO1.ftp.ip");
        waslogFtpId = props.getProperty("FO1.ftp.id");
        waslogFtpPw = props.getProperty("FO1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * FO2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("FO2.ftp.ip");
            waslogFtpId = props.getProperty("FO2.ftp.id");
            waslogFtpPw = props.getProperty("FO2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO2", targetServer);


            /**
             * FO3 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/03";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("FO3.ftp.ip");
            waslogFtpId = props.getProperty("FO3.ftp.id");
            waslogFtpPw = props.getProperty("FO3.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO3", targetServer);

            /**
             * FO4 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/04";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("FO4.ftp.ip");
            waslogFtpId = props.getProperty("FO4.ftp.id");
            waslogFtpPw = props.getProperty("FO4.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO4", targetServer);


            /**
             * FO5 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/05";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("FO5.ftp.ip");
            waslogFtpId = props.getProperty("FO5.ftp.id");
            waslogFtpPw = props.getProperty("FO5.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO5", targetServer);

            /**
             * FO6 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/06";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("FO6.ftp.ip");
            waslogFtpId = props.getProperty("FO6.ftp.id");
            waslogFtpPw = props.getProperty("FO6.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO6", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기 Zip.compress("c:\\deploy/waslog/temp/front/", "c:\\deploy/waslog/front.zip");
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real LPS 로그 다운
     */
    public static void getWasLogLPSByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * LPS1, LPS2, LPS3
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "lps";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_LPS_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_LPS_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * LPS1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("LPS1.ftp.ip");
        waslogFtpId = props.getProperty("LPS1.ftp.id");
        waslogFtpPw = props.getProperty("LPS1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "LPS1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * LPS2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("LPS2.ftp.ip");
            waslogFtpId = props.getProperty("LPS2.ftp.id");
            waslogFtpPw = props.getProperty("LPS2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "LPS2", targetServer);


            /**
             * LPS3 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/03";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("LPS3.ftp.ip");
            waslogFtpId = props.getProperty("LPS3.ftp.id");
            waslogFtpPw = props.getProperty("LPS3.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "LPS3", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real MO 로그 다운
     */
    public static void getWasLogMOByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO1, MO5, MO6, MO7
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "frontmobile";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO1.ftp.ip");
        waslogFtpId = props.getProperty("MO1.ftp.id");
        waslogFtpPw = props.getProperty("MO1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * MO5 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/05";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MO5.ftp.ip");
            waslogFtpId = props.getProperty("MO5.ftp.id");
            waslogFtpPw = props.getProperty("MO5.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO5", targetServer);


            /**
             * MO6 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/06";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MO6.ftp.ip");
            waslogFtpId = props.getProperty("MO6.ftp.id");
            waslogFtpPw = props.getProperty("MO6.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO6", targetServer);


            /**
             * MO7 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/07";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MO7.ftp.ip");
            waslogFtpId = props.getProperty("MO7.ftp.id");
            waslogFtpPw = props.getProperty("MO7.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO7", targetServer);

            /**
             * MO8 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/08";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MO8.ftp.ip");
            waslogFtpId = props.getProperty("MO8.ftp.id");
            waslogFtpPw = props.getProperty("MO8.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO8", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real MLPS 로그 다운
     */
    public static void getWasLogMLPSByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MLPS1, MLPS2, MLPS3
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "mlps";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_MLPS_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_MLPS_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MLPS1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MLPS1.ftp.ip");
        waslogFtpId = props.getProperty("MLPS1.ftp.id");
        waslogFtpPw = props.getProperty("MLPS1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MLPS1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * MLPS2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MLPS2.ftp.ip");
            waslogFtpId = props.getProperty("MLPS2.ftp.id");
            waslogFtpPw = props.getProperty("MLPS2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MLPS2", targetServer);


            /**
             * MLPS3 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/03";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("MLPS3.ftp.ip");
            waslogFtpId = props.getProperty("MLPS3.ftp.id");
            waslogFtpPw = props.getProperty("MLPS3.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MLPS3", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real BO 로그 다운
     */
    public static void getWasLogBOByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * BO1, BO2
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * BO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO1.ftp.ip");
        waslogFtpId = props.getProperty("BO1.ftp.id");
        waslogFtpPw = props.getProperty("BO1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "BO1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * BO2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("BO2.ftp.ip");
            waslogFtpId = props.getProperty("BO2.ftp.id");
            waslogFtpPw = props.getProperty("BO2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "BO2", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");

            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real PO 로그 다운
     */
    public static void getWasLogPOByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * PO1, PO2
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_po_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_po_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * PO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("PO1.ftp.ip");
        waslogFtpId = props.getProperty("PO1.ftp.id");
        waslogFtpPw = props.getProperty("PO1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "PO1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * PO2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("PO2.ftp.ip");
            waslogFtpId = props.getProperty("PO2.ftp.id");
            waslogFtpPw = props.getProperty("PO2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "PO2", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");

            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real CC 로그 다운
     */
    public static void getWasLogCCByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * CC1, CC2
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_cc_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_cc_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * CC1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("CC1.ftp.ip");
        waslogFtpId = props.getProperty("CC1.ftp.id");
        waslogFtpPw = props.getProperty("CC1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "CC1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * CC2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("CC2.ftp.ip");
            waslogFtpId = props.getProperty("CC2.ftp.id");
            waslogFtpPw = props.getProperty("CC2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "CC2", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");

            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Real TO 로그 다운
     */
    public static void getWasLogTOByReal(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * TO1, TO2
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "fronttablet";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_fronttablet_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_fronttablet_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * TO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/01";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("TO1.ftp.ip");
        waslogFtpId = props.getProperty("TO1.ftp.id");
        waslogFtpPw = props.getProperty("TO1.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "TO1", targetServer);

        if (targetL4.equals("all")) {

            /**
             * TO2 로그 다운
             **************************************************/
            // temp에 디렉코리 생성
            tempPath = waslogTempPath + "/02";
            makeDiretory(tempPath);

            // 로드 다운로드 공통모듈 호출
            waslogFtpIp = props.getProperty("TO2.ftp.ip");
            waslogFtpId = props.getProperty("TO2.ftp.id");
            waslogFtpPw = props.getProperty("TO2.ftp.pw");

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "TO2", targetServer);

        }// targetL4 check if_end

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Test FO 로그 다운
     */
    public static void getWasLogFOByTest(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * FO TEST
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "front";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * FO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO_TST.ftp.ip");
        waslogFtpId = props.getProperty("FO_TST.ftp.id");
        waslogFtpPw = props.getProperty("FO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "server1", targetServer);

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Test MO 로그 다운
     */
    public static void getWasLogMOByTest(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO TEST
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "frontmobile";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO_TST.ftp.ip");
        waslogFtpId = props.getProperty("MO_TST.ftp.id");
        waslogFtpPw = props.getProperty("MO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "server1", targetServer);

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Test BO 로그 다운
     */
    public static void getWasLogBOByTest(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * BO TEST
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * BO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO_TST.ftp.ip");
        waslogFtpId = props.getProperty("BO_TST.ftp.id");
        waslogFtpPw = props.getProperty("BO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "server1", targetServer);

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Test TO 로그 다운
     */
    public static void getWasLogTOByTest(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * TO TEST
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "fronttablet";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_fronttablet_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_fronttablet_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * TO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("TO_TST.ftp.ip");
        waslogFtpId = props.getProperty("TO_TST.ftp.id");
        waslogFtpPw = props.getProperty("TO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "server3", targetServer);

        try {

            // 압축하기
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Staing FO 로그 다운
     */
    public static void getWasLogFOByStaging(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * FO4
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "front";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + todate + "_staging.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_front_" + targetDate + "_staging.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * FO1 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("FO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("FO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "FO4", targetServer);

        try {

            // 압축하기 Zip.compress("c:\\deploy/waslog/temp/front/", "c:\\deploy/waslog/front.zip");
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Staing MO 로그 다운
     */
    public static void getWasLogMOByStaging(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO4
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "frontmobile";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + todate + "_staging.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_frontmobile_" + targetDate + "_staging.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MO4 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("MO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("MO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "MO4", targetServer);

        try {

            // 압축하기 Zip.compress("c:\\deploy/waslog/temp/front/", "c:\\deploy/waslog/front.zip");
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * Staing MO 로그 다운
     */
    public static void getWasLogBOByStaging(Properties props, String targetL4, String targetDate, String jeuslogDir, String waslogSystem,
            String targetServer) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * BO3
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + todate + "_staging.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "_jeus_bo_" + targetDate + "_staging.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * BO3 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("BO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("BO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, jeuslogDir, tempPath, targetDate, todate, "BO3", targetServer);

        try {

            // 압축하기 Zip.compress("c:\\deploy/waslog/temp/front/", "c:\\deploy/waslog/front.zip");
            System.out.println("Zip.compress START ::");
            File waslogTempPathFile = new File(waslogTempPath);
            Zip.zipDirectory(waslogTempPathFile, zipFile);
            System.out.println("Zip.compress END ::");

            // TEMP파일 삭제
            System.out.println("TEMP FILE Delete START ::");
            FileUtils.forceDelete(waslogTempPathFile);
            System.out.println("TEMP FILE Delete END ::");

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 로그 다운 공통
     */
    public static void getWasLogCommon(String waslogFtpIp, String waslogFtpId, String waslogFtpPw, String jeuslogDir, String tempPath,
            String targetDate, String todate, String jeusLogServerPath, String targetServer) {


        String jeuslogFileNm = "";
        String jeuslogFilePath = "";

        SFTPService ftp = new SFTPService(waslogFtpIp, 22, waslogFtpId, waslogFtpPw);

        String jeusServerNm = "";
        jeusServerNm = jeusLogServerPath + "/logs/";

        if (targetDate.equals(todate.substring(0, 8))) {
            // 오늘날짜 로그
            jeuslogFileNm = "JeusServer.log";
        } else {
            jeuslogFileNm = "JeusServer_" + targetDate + ".log";
        }

        jeuslogFilePath = jeuslogDir + jeusServerNm + jeuslogFileNm;

        System.out.println("jeuslogFilePath ::" + jeuslogFilePath);
        System.out.println("tempPath ::" + tempPath);

        // 파일 다운로드
        ftp.downloadFile(jeuslogFilePath, tempPath);

        // logFile Split
        // logFileSplitCommandExec(tempPath, jeuslogFileNm);


    }


    /**
     * 로그 다운 공통
     */
    public static void getWasLogCommon(Properties props, String tempPath, String targetDate, String todate, String targetServer,
            String waslogSystemFtp) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";
        String jeuslogDir = "";

        String jeuslogFileNm = "";
        String jeuslogFilePath = "";

        // 로드 다운로드 공통모듈 호출
        if (targetServer.equals("real")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + ".ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + ".ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + ".ftp.pw");
            jeuslogDir = props.getProperty(waslogSystemFtp + ".jeuslog.path");


        } else if (targetServer.equals("test")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + "_TST.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_TST.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_TST.ftp.pw");
            jeuslogDir = props.getProperty(waslogSystemFtp + "_TST.jeuslog.path");


        } else if (targetServer.equals("staging")) {

            waslogFtpIp = props.getProperty(waslogSystemFtp + "_STAGING.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_STAGING.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_STAGING.ftp.pw");
            jeuslogDir = props.getProperty(waslogSystemFtp + "_STAGING.jeuslog.path");


        } else if (targetServer.equals("dev")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + "_DEV.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_DEV.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_DEV.ftp.pw");
            jeuslogDir = props.getProperty(waslogSystemFtp + "_DEV.jeuslog.path");

        }

        SFTPService ftp = new SFTPService(waslogFtpIp, 22, waslogFtpId, waslogFtpPw);

        if (targetDate.equals(todate.substring(0, 8))) {
            // 오늘날짜 로그
            jeuslogFileNm = "JeusServer.log";
        } else {
            jeuslogFileNm = "JeusServer_" + targetDate + ".log";
        }

        jeuslogFilePath = jeuslogDir + jeuslogFileNm;

        System.out.println("jeuslogFilePath ::" + jeuslogFilePath);
        System.out.println("tempPath ::" + tempPath);

        // 파일 다운로드
        ftp.downloadFile(jeuslogFilePath, tempPath);

        // logFile Split
        // logFileSplitCommandExec(tempPath, jeuslogFileNm);


    }


    private static void logFileSplitCommandExec(String tempPath, String waslogFileNm) {

        String command = "split -d -a 2 -b 100m " + tempPath + "/" + waslogFileNm + "  " + waslogFileNm + ".part";
        command = "split -C 10240000 " + tempPath + "/" + waslogFileNm;

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            // try {
            // // System.out.println("sleep start::");
            // Thread.sleep(8 * 1000);
            // // System.out.println("sleep end::");
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            command = "rm -rf " + tempPath + "/" + waslogFileNm;
            Process process2 = runTime.exec(command);
            System.out.println("logFileSplitCommandExec rm -rf ::" + command);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("logFile Split Error!! ");
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


    public static void propServerSet(Properties props) {

        String FO_SET_01_prop = props.getProperty("FO_SET_01");
        String FO_SET_02_prop = props.getProperty("FO_SET_02");
        String MO_SET_01_prop = props.getProperty("MO_SET_01");
        String MO_SET_02_prop = props.getProperty("MO_SET_02");

        String SPARE_FO_SET_01_prop = props.getProperty("SPARE_FO_SET_01");
        String SPARE_FO_SET_02_prop = props.getProperty("SPARE_FO_SET_02");
        String SPARE_MO_SET_01_prop = props.getProperty("SPARE_MO_SET_01");
        String SPARE_MO_SET_02_prop = props.getProperty("SPARE_MO_SET_02");

        String SPARE_MO_SET_01_ALL_prop = props.getProperty("SPARE_MO_SET_01_ALL");
        String SPARE_MO_SET_02_ALL_prop = props.getProperty("SPARE_MO_SET_02_ALL");

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");
        SPARE_MO_SET_01_ALL = SPARE_MO_SET_01_ALL_prop.split(",");
        SPARE_MO_SET_02_ALL = SPARE_MO_SET_02_ALL_prop.split(",");
    }

}
