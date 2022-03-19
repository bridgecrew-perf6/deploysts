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
public class WasLog {

    static final String FO_DEFAULT_LOG_NM = "front_secure";
    static final String MO_DEFAULT_LOG_NM = "frontmobile_secure";
    static final String BO_DEFAULT_LOG_NM = "bo_secure";
    static final String TO_DEFAULT_LOG_NM = "fronttablet_secure";
    static final String EASYPAY_DEFAULT_LOG_NM = "easypayclient";
    static final String WAS_LOG_DEFAULT_DIR = "/data/webapps/archive/E-HIMART/logs/";
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


    // static final String LOCAL_DEFAULT_DIR = "C:\\deploy/waslog/";
    // static final String LOCAL_TEMP_DIR = "C:\\deploy/waslog/temp/";



    public WasLog(Properties props, String targetL4, String targetServer, String waslogSystem, String targetDate, String paramEasypayYn) {

        // 서버 프로퍼티 셋팅
        propServerSet(props);

        String todate = DateUtils.getDate("yyyyMMddHHmm");
        String logNm = "";
        String waslogSystemFtp = "";
        String waslogTempPath = LOCAL_TEMP_DIR + waslogSystem;
        String tempPath = "";
        String easypayYn = "N";

        if (waslogSystem.contains("FO") || waslogSystem.equals("LPS")) {
            logNm = FO_DEFAULT_LOG_NM;

        } else if (waslogSystem.contains("MO") || waslogSystem.equals("MLPS")) {
            logNm = MO_DEFAULT_LOG_NM;

        } else if (waslogSystem.contains("BO") || waslogSystem.equals("PO") || waslogSystem.equals("CC")) {
            logNm = BO_DEFAULT_LOG_NM;

        } else if (waslogSystem.contains("TO")) {
            logNm = TO_DEFAULT_LOG_NM;
        }


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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                        }
                    }

                } else if (targetL4.equals("04")) {

                    waslogSystemFtp = "FO4";
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);

                } else if (targetL4.equals("spare")) {
                    // spare서버 전체
                    for (String element : SPARE_FO_SET_01) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                    }

                    for (String element : SPARE_FO_SET_02) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                        }
                    }

                    for (String element : FO_SET_02) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                        }
                    }

                } else if (targetL4.equals("04")) {

                    waslogSystemFtp = "FO4";
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);

                } else if (targetL4.equals("spare")) {
                    // spare서버 전체
                    for (String element : SPARE_MO_SET_01) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                    }

                    for (String element : SPARE_MO_SET_02) {
                        waslogSystemFtp = element;
                        tempPath = waslogTempPath + "/" + waslogSystemFtp;
                        makeDiretory(tempPath);

                        getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
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

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                        }
                    }

                    for (String element : MO_SET_02) {
                        if (element.contains("LPS")) {
                            continue;
                        } else {
                            waslogSystemFtp = element;
                            tempPath = waslogTempPath + "/" + waslogSystemFtp;
                            makeDiretory(tempPath);

                            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                        }
                    }

                }

            } else if (waslogSystem.equals("LPS") || waslogSystem.equals("MLPS")) {
                // 서버가 3대씩

                easypayYn = paramEasypayYn;

                for (int i = 1; i <= 3; i++) {
                    waslogSystemFtp = waslogSystem + i;
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);
                }

            } else {
                // 나머지는 모두 서버가 2대씩
                for (int i = 1; i <= 2; i++) {
                    waslogSystemFtp = waslogSystem + i;
                    tempPath = waslogTempPath + "/" + waslogSystemFtp;
                    makeDiretory(tempPath);

                    getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystemFtp);

                }

            }
        } else {
            // TST, DEV는 서버가 한대씩
            tempPath = waslogTempPath;
            makeDiretory(tempPath);

            getWasLogCommon(props, tempPath, targetDate, todate, logNm, easypayYn, targetServer, waslogSystem);

        } // targetServer check if_end



        /*************************************************
         * 경로설정
         ***************************************************/
        String zipFile = "";

        if (waslogSystem.equals("LPS") || waslogSystem.equals("MLPS")) {
            if (paramEasypayYn.equals("Y")) {
                waslogSystem = "eaaypay_" + waslogSystem;
            }
        }

        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + waslogSystem + "_" + todate + "_" + targetServer + ".zip";
            if (targetServer.equals("real")) {
                zipFile = LOCAL_DEFAULT_DIR + waslogSystem + "_" + todate + "_" + targetServer + "_" + targetL4 + ".zip";
            }

        } else {
            zipFile = LOCAL_DEFAULT_DIR + waslogSystem + "_" + targetDate + "_" + targetServer + ".zip";
            if (targetServer.equals("real")) {
                zipFile = LOCAL_DEFAULT_DIR + waslogSystem + "_" + targetDate + "_" + targetServer + "_" + targetL4 + ".zip";
            }
        }

        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

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


    public void WasLog_bak(Properties props, String targetL4, String targetServer, String waslogSystem, String targetDate, String easypayYn) {

        String front = "front";
        String frontmobile = "frontmobile";
        String fronttablet = "fronttablet";
        String bo = "bo";
        String easypay = "easypay";

        String waslogDir = "";
        String logNm = "";

        if (targetServer.equals("real")) {
            // Real 서버
            if (waslogSystem.equals("FO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + front;
                logNm = FO_DEFAULT_LOG_NM;
                getWasLogFOByReal(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("LPS")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + front;
                logNm = FO_DEFAULT_LOG_NM;
                if (easypayYn.equals("Y")) {
                    waslogDir = WAS_LOG_DEFAULT_DIR + easypay + "/" + targetDate.substring(2, 6);
                    logNm = EASYPAY_DEFAULT_LOG_NM;
                }

                getWasLogLPSByReal(props, targetL4, targetDate, waslogDir, easypayYn, logNm);

            } else if (waslogSystem.equals("MO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + frontmobile;
                logNm = MO_DEFAULT_LOG_NM;
                getWasLogMOByReal(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("MLPS")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + frontmobile;
                logNm = MO_DEFAULT_LOG_NM;
                if (easypayYn.equals("Y")) {
                    waslogDir = WAS_LOG_DEFAULT_DIR + easypay + "/" + targetDate.substring(2, 6);
                    logNm = EASYPAY_DEFAULT_LOG_NM;
                }

                getWasLogMLPSByReal(props, targetL4, targetDate, waslogDir, easypayYn, logNm);

            } else if (waslogSystem.equals("BO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogBOByReal(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("PO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogPOByReal(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("CC")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogCCByReal(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("TO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + fronttablet;
                logNm = TO_DEFAULT_LOG_NM;
                getWasLogTOByReal(props, targetL4, targetDate, waslogDir, logNm);
            }
        } else if (targetServer.equals("test")) {
            // Test 서버
            if (waslogSystem.equals("FO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + front;
                logNm = FO_DEFAULT_LOG_NM;
                getWasLogFOByTest(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("MO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + frontmobile;
                logNm = MO_DEFAULT_LOG_NM;
                getWasLogMOByTest(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("BO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogBOByTest(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("TO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + fronttablet;
                logNm = TO_DEFAULT_LOG_NM;
                getWasLogTOByTest(props, targetL4, targetDate, waslogDir, logNm);
            }

        } else if (targetServer.equals("dev")) {
            // 개발 서버
            if (waslogSystem.equals("FO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + front;
                logNm = FO_DEFAULT_LOG_NM;
                getWasLogFOByDev(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("MO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + frontmobile;
                logNm = MO_DEFAULT_LOG_NM;
                getWasLogMOByDev(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("BO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogBOByDev(props, targetL4, targetDate, waslogDir, logNm);

            } else if (waslogSystem.equals("TO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + fronttablet;
                logNm = TO_DEFAULT_LOG_NM;
                getWasLogTOByDev(props, targetL4, targetDate, waslogDir, logNm);

            }

        } else if (targetServer.equals("staging")) {
            // STAGING 서버
            if (waslogSystem.equals("FO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + front;
                logNm = FO_DEFAULT_LOG_NM;

                if (easypayYn.equals("Y")) {
                    waslogDir = WAS_LOG_DEFAULT_DIR + easypay + "/" + targetDate.substring(2, 6);
                    logNm = EASYPAY_DEFAULT_LOG_NM;
                }

                getWasLogFOByStaging(props, targetDate, waslogDir, easypayYn, logNm);

            } else if (waslogSystem.equals("MO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + frontmobile;
                logNm = MO_DEFAULT_LOG_NM;

                if (easypayYn.equals("Y")) {
                    waslogDir = WAS_LOG_DEFAULT_DIR + easypay + "/" + targetDate.substring(2, 6);
                    logNm = EASYPAY_DEFAULT_LOG_NM;
                }

                getWasLogMOByStaging(props, targetDate, waslogDir, easypayYn, logNm);

            } else if (waslogSystem.equals("BO")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + bo;
                logNm = BO_DEFAULT_LOG_NM;
                getWasLogBOByStaging(props, targetDate, waslogDir, logNm);

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
    public static void getWasLogFOByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * FO1, FO2, FO3, FO4, FO5, FO6
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "front";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + targetDate + ".zip";
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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogLPSByReal(Properties props, String targetL4, String targetDate, String waslogDir, String easypayYn,
            String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * LPS1, LPS2, LPS3
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "lps";
        if (easypayYn.equals("Y")) {
            waslogTempPath = LOCAL_TEMP_DIR + "eaypaylps";
        }
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "LPS_" + todate + ".zip";
            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_LPS_" + todate + ".zip";
            }
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "LPS_" + targetDate + ".zip";
            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_LPS_" + targetDate + ".zip";
            }
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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);

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
    public static void getWasLogMOByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogMLPSByReal(Properties props, String targetL4, String targetDate, String waslogDir, String easypayYn,
            String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MLPS1, MLPS2, MLPS3
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "mlps";
        if (easypayYn.equals("Y")) {
            waslogTempPath = LOCAL_TEMP_DIR + "eaypaymlps";
        }
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "MLPS_" + todate + ".zip";

            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_MLPS_" + todate + ".zip";
            }
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "MLPS_" + targetDate + ".zip";

            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_MLPS_" + targetDate + ".zip";
            }
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);


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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, easypayYn);

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
    public static void getWasLogBOByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogPOByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "po_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "po_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogCCByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "cc_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "cc_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogTOByReal(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + todate + ".zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + targetDate + ".zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

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

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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

            getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogFOByTest(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "front_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * FO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO_TST.ftp.ip");
        waslogFtpId = props.getProperty("FO_TST.ftp.id");
        waslogFtpPw = props.getProperty("FO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogMOByTest(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * MO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO_TST.ftp.ip");
        waslogFtpId = props.getProperty("MO_TST.ftp.id");
        waslogFtpPw = props.getProperty("MO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogBOByTest(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * BO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO_TST.ftp.ip");
        waslogFtpId = props.getProperty("BO_TST.ftp.id");
        waslogFtpPw = props.getProperty("BO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
     * Dev FO 로그 다운
     */
    public static void getWasLogFOByDev(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "front_" + todate + "_dev.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + targetDate + "_dev.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * FO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO_DEV.ftp.ip");
        waslogFtpId = props.getProperty("FO_DEV.ftp.id");
        waslogFtpPw = props.getProperty("FO_DEV.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
     * Dev MO 로그 다운
     */
    public static void getWasLogMOByDev(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO DEV
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "frontmobile";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + todate + "_dev.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + targetDate + "_dev.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * MO DEV 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO_DEV.ftp.ip");
        waslogFtpId = props.getProperty("MO_DEV.ftp.id");
        waslogFtpPw = props.getProperty("MO_DEV.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
     * Dev BO 로그 다운
     */
    public static void getWasLogBOByDev(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * BO DEV
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + todate + "_dev.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + targetDate + "_dev.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * BO DEV 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO_DEV.ftp.ip");
        waslogFtpId = props.getProperty("BO_DEV.ftp.id");
        waslogFtpPw = props.getProperty("BO_DEV.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
     * Dev TO 로그 다운
     */
    public static void getWasLogTOByDev(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * BO DEV
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "fronttablet";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + todate + "_dev.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + targetDate + "_dev.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * BO DEV 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("TO_DEV.ftp.ip");
        waslogFtpId = props.getProperty("TO_DEV.ftp.id");
        waslogFtpPw = props.getProperty("TO_DEV.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogTOByTest(Properties props, String targetL4, String targetDate, String waslogDir, String logNm) {

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
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + todate + "_test.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "fronttablet_" + targetDate + "_test.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");;

        /**
         * TO TEST 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath;

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("TO_TST.ftp.ip");
        waslogFtpId = props.getProperty("TO_TST.ftp.id");
        waslogFtpPw = props.getProperty("TO_TST.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogFOByStaging(Properties props, String targetDate, String waslogDir, String easypayYn, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * FO_STAGING
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "front";

        if (easypayYn.equals("Y")) {
            waslogTempPath = LOCAL_TEMP_DIR + "eaypaylps";
        }

        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + todate + "_staging.zip";
            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_LPS_" + todate + "_staging.zip";
            }
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "front_" + targetDate + "_staging.zip";
            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_LPS_" + todate + "_staging.zip";
            }
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * FO_STAGING 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("FO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("FO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("FO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogMOByStaging(Properties props, String targetDate, String waslogDir, String easypayYn, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO_STAGING
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "frontmobile";

        if (easypayYn.equals("Y")) {
            waslogTempPath = LOCAL_TEMP_DIR + "eaypaylps";
        }

        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + todate + "_staging.zip";
            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_MLPS_" + todate + "_staging.zip";
            }

        } else {
            zipFile = LOCAL_DEFAULT_DIR + "frontmobile_" + targetDate + "_staging.zip";

            if (easypayYn.equals("Y")) {
                zipFile = LOCAL_DEFAULT_DIR + "eaypay_MLPS_" + todate + "_staging.zip";
            }
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MO_STAGING 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("MO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("MO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("MO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
     * Staing BO 로그 다운
     */
    public static void getWasLogBOByStaging(Properties props, String targetDate, String waslogDir, String logNm) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";

        String todate = DateUtils.getDate("yyyyMMddHHmm");


        /*************************************************
         * MO_STAGING
         ***************************************************/
        String waslogTempPath = LOCAL_TEMP_DIR + "bo";
        String zipFile = "";
        if (targetDate.equals(todate.substring(0, 8))) {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + todate + "_staging.zip";
        } else {
            zipFile = LOCAL_DEFAULT_DIR + "bo_" + targetDate + "_staging.zip";
        }
        makeDiretory(waslogTempPath);

        ZIP_FILE_NM = zipFile.replaceAll(LOCAL_DEFAULT_DIR, "");

        /**
         * MO_STAGING 로그 다운
         **************************************************/
        // temp에 디렉코리 생성
        String tempPath = waslogTempPath + "/staging";
        makeDiretory(tempPath);

        // 로드 다운로드 공통모듈 호출
        waslogFtpIp = props.getProperty("BO_STAGING.ftp.ip");
        waslogFtpId = props.getProperty("BO_STAGING.ftp.id");
        waslogFtpPw = props.getProperty("BO_STAGING.ftp.pw");

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm);

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
    public static void getWasLogCommon(String waslogFtpIp, String waslogFtpId, String waslogFtpPw, String waslogDir, String tempPath,
            String targetDate, String todate, String logNm) {

        getWasLogCommon(waslogFtpIp, waslogFtpId, waslogFtpPw, waslogDir, tempPath, targetDate, todate, logNm, "N");
    }


    /**
     * 로그 다운 공통
     */
    public static void getWasLogCommon(String waslogFtpIp, String waslogFtpId, String waslogFtpPw, String waslogDir, String tempPath,
            String targetDate, String todate, String logNm, String easypayYn) {


        String waslogFileNm = "";
        String waslogFilePath = "";

        String trgTargetDate = DateUtils.convertDateFormat(targetDate, "yyyyMMdd", "yyyy-MM-dd");
        // long compareNowDate = DateUtils.diffOfDate(targetDate, todate.substring(0, 8));

        System.out.println("todate  substring(0, 8)::" + todate.substring(0, 8));
        System.out.println("trgTargetDate ::" + trgTargetDate);

        SFTPService ftp = new SFTPService(waslogFtpIp, 22, waslogFtpId, waslogFtpPw);

        if (targetDate.equals(todate.substring(0, 8))) {
            // 오늘날짜 로그
            waslogFileNm = logNm + ".log";
            if (easypayYn.equals("Y")) {
                waslogFileNm = logNm + "." + targetDate;
            }
            waslogFilePath = waslogDir + "/" + waslogFileNm;


            System.out.println("waslogFilePath ::" + waslogFilePath);
            System.out.println("tempPath ::" + tempPath);

            // 파일 다운로드
            ftp.downloadFile(waslogFilePath, tempPath);

        } else {

            waslogFileNm = logNm + "-" + trgTargetDate + ".log";
            if (easypayYn.equals("Y")) {
                waslogFileNm = logNm + "." + targetDate;
            }
            waslogFilePath = waslogDir + "/" + waslogFileNm;

            System.out.println("waslogFilePath ::" + waslogFilePath);
            System.out.println("tempPath ::" + tempPath);

            // 파일 다운로드
            ftp.downloadFile(waslogFilePath, tempPath);

        }


        // logFile Split
        // logFileSplitCommandExec(tempPath, waslogFileNm);


    }



    /**
     * 로그 다운 공통
     */
    public static void getWasLogCommon(Properties props, String tempPath, String targetDate, String todate, String logNm, String easypayYn,
            String targetServer, String waslogSystemFtp) {

        String waslogFtpIp = "";
        int waslogFtpPort = 22;
        String waslogFtpId = "";
        String waslogFtpPw = "";
        String waslogDir = "";

        String waslogFileNm = "";
        String waslogFilePath = "";

        // 로드 다운로드 공통모듈 호출
        if (targetServer.equals("real")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + ".ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + ".ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + ".ftp.pw");
            waslogDir = props.getProperty(waslogSystemFtp + ".waslog.path");

            if (easypayYn.equals("Y")) {
                waslogDir = WAS_LOG_DEFAULT_DIR + "easypay/" + targetDate.substring(2, 6);
                logNm = EASYPAY_DEFAULT_LOG_NM;
            }

        } else if (targetServer.equals("test")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + "_TST.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_TST.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_TST.ftp.pw");
            waslogDir = props.getProperty(waslogSystemFtp + "_TST.waslog.path");


        } else if (targetServer.equals("staging")) {

            waslogFtpIp = props.getProperty(waslogSystemFtp + "_STAGING.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_STAGING.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_STAGING.ftp.pw");
            waslogDir = props.getProperty(waslogSystemFtp + "_STAGING.waslog.path");


        } else if (targetServer.equals("dev")) {
            waslogFtpIp = props.getProperty(waslogSystemFtp + "_DEV.ftp.ip");
            waslogFtpId = props.getProperty(waslogSystemFtp + "_DEV.ftp.id");
            waslogFtpPw = props.getProperty(waslogSystemFtp + "_DEV.ftp.pw");
            waslogDir = props.getProperty(waslogSystemFtp + "_DEV.waslog.path");

        }

        String trgTargetDate = DateUtils.convertDateFormat(targetDate, "yyyyMMdd", "yyyy-MM-dd");
        // long compareNowDate = DateUtils.diffOfDate(targetDate, todate.substring(0, 8));

        System.out.println("todate  substring(0, 8)::" + todate.substring(0, 8));
        System.out.println("trgTargetDate ::" + trgTargetDate);

        SFTPService ftp = new SFTPService(waslogFtpIp, 22, waslogFtpId, waslogFtpPw);

        if (targetDate.equals(todate.substring(0, 8))) {
            // 오늘날짜 로그
            waslogFileNm = logNm + ".log";
            if (easypayYn.equals("Y")) {
                waslogFileNm = logNm + "." + targetDate;
            }
            waslogFilePath = waslogDir + "/" + waslogFileNm;


            System.out.println("waslogFilePath ::" + waslogFilePath);
            System.out.println("tempPath ::" + tempPath);

            // 파일 다운로드
            ftp.downloadFile(waslogFilePath, tempPath);

        } else {

            waslogFileNm = logNm + "-" + trgTargetDate + ".log";
            if (easypayYn.equals("Y")) {
                waslogFileNm = logNm + "." + targetDate;
            }
            waslogFilePath = waslogDir + "/" + waslogFileNm;

            System.out.println("waslogFilePath ::" + waslogFilePath);
            System.out.println("tempPath ::" + tempPath);

            // 파일 다운로드
            ftp.downloadFile(waslogFilePath, tempPath);

        }


        // logFile Split
        // logFileSplitCommandExec(tempPath, waslogFileNm);


    }


    private static void logFileSplitCommandExec(String tempPath, String waslogFileNm) {

        String command = "split -d -a 2 -b 100m " + tempPath + "/" + waslogFileNm + "  " + waslogFileNm + ".part";
        command = "split -C 10240000 " + tempPath + "/" + waslogFileNm;

        System.out.println("logFileSplitCommandExec split ::" + command);

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
            // Process process2 = runTime.exec(command);
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

        FO_SET_01 = FO_SET_01_prop.split(",");
        FO_SET_02 = FO_SET_02_prop.split(",");
        MO_SET_01 = MO_SET_01_prop.split(",");
        MO_SET_02 = MO_SET_02_prop.split(",");

        SPARE_FO_SET_01 = SPARE_FO_SET_01_prop.split(",");
        SPARE_FO_SET_02 = SPARE_FO_SET_02_prop.split(",");
        SPARE_MO_SET_01 = SPARE_MO_SET_01_prop.split(",");
        SPARE_MO_SET_02 = SPARE_MO_SET_02_prop.split(",");
    }

}
