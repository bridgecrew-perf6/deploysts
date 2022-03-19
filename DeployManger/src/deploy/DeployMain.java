package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import svn.SvnManage;
import utils.CryptoUtil;
import utils.DateUtils;

@Slf4j
@Data
public class DeployMain {

    static final String CONFIG_FILE = "/data/home/hisis/ec/tools/deploy/config/deploy.config.properties";
    static final String LICENSE_FILE = "/data/home/hisis/ec/tools/deploy/config/LICENSE.deploy";
    static final String LICENSE_FILE_SLT = "/data/home/hisis/djk/hudson/apache-tomcat-7.0.59/conf/Catalina/LICENSE.deploy";

    static String[] FO_SET_01 = new String[] {};
    static String[] FO_SET_02 = new String[] {};
    static String[] MO_SET_01 = new String[] {};
    static String[] MO_SET_02 = new String[] {};

    static String[] SPARE_FO_SET_01 = new String[] {};
    static String[] SPARE_FO_SET_02 = new String[] {};
    static String[] SPARE_MO_SET_01 = new String[] {};
    static String[] SPARE_MO_SET_02 = new String[] {};


    // static final String CONFIG_FILE = "C:\\deploymanager\\config\\deploy.config.properties";
    // static final String LICENSE_FILE = "C:\\deploymanager\\config\\LICENSE.deploy";

    // static final String CONFIG_FILE = "C:\\deploy/config/deploy.config.properties";


    public static void main(String[] args) throws Exception {

        String runType = StringUtils.defaultString(StringUtils.trim(args[0]), "deploy"); // 기본동작은 deploy [배포], recover [원복], waslog [로그다운]


        System.out.println("Copyright 2018. Kim Won Yong all rights reserved.");
        if (!LicenseCheck(runType)) {
            printLicence();
            return;
        }

        runType = runType.replace("_slt", "");

        if (ArrayUtils.isEmpty(args)) {
            printUsage();
            return;
        }

        // 설정 파일 로드
        Properties props = loadConfig(CONFIG_FILE);

        System.out.println("[Deploy [runType : {} >> START] ======[" + runType + "]");

        String targetL4 = ""; // Target L4
        String baseSystem = "";
        String targetSystem = ""; // Target System FO, MO, BO, TO
        String targetDate = DateUtils.getDate("yyyyMMdd"); // Tartget일자 예)20180301
        String targetNumber = "99"; // 1: 첫번째, 99: 마지막, 그외는 그 숫자 그대로
        String targetOrder = ""; //
        String targetServer = "real"; // real, test
        String easypayYn = "N"; // easypay Log 여부
        String searchMonth = DateUtils.getDate("yyyyMM");
        String spareDeployYn = "N";
        String spareOnlyYn = "N";
        String cacheEnableYn = "Y";
        String jenkinsUser = "";
        String searchStr = "";
        String itemId = "";
        String webServerCheck = "";

        boolean zippingYn = true;
        String zippingYnStr = "Y";

        List<String> targetSystemArr = null;

        String shellScriptNo = "";
        String parameter01 = "";
        String parameter02 = "";
        String parameter03 = "";


        String lincenseKey = ""; // lincenseKey

        if ("deploycommon".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    spareDeployYn = StringUtils.trim(args[2]); // spareDeployYn
                } else if (i == 3) {
                    spareOnlyYn = StringUtils.trim(args[3]); // spareOnlyYn
                } else if (i == 4) {
                    targetL4 = StringUtils.trim(args[4]); // Target L4
                }

            }

        } else if ("reboot".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    spareDeployYn = StringUtils.trim(args[2]); // spareDeployYn
                } else if (i == 3) {
                    spareOnlyYn = StringUtils.trim(args[3]); // spareOnlyYn
                } else if (i == 4) {
                    webServerCheck = StringUtils.trim(args[4]); // webServerCheck
                }

            }

        } else if ("precompilecommon".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    spareOnlyYn = StringUtils.trim(args[2]); // spareOnlyYn
                }

            }

        } else if ("deployzippingonly".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    baseSystem = StringUtils.trim(args[1]); // baseSystem
                }

            }

        } else if ("deployzipping".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    spareOnlyYn = StringUtils.trim(args[2]); // spareOnlyYn
                } else if (i == 3) {
                    zippingYnStr = StringUtils.trim(args[3]); // zippingYnStr
                }


            }

        } else if ("deployzippingbybasesystem".equals(runType)) {
            targetSystemArr = new ArrayList<String>();

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    baseSystem = StringUtils.trim(args[1]); // base_system
                } else if (i == 2) {
                    zippingYnStr = StringUtils.trim(args[2]); // zippingYnStr
                }
            }

            for (int i = 3; i < args.length; i++) {
                targetSystemArr.add(args[i]);
            }

        } else if ("arcusdbcache".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    cacheEnableYn = StringUtils.trim(args[2]); // cacheEnableYn
                } else if (i == 3) {
                    spareDeployYn = StringUtils.trim(args[3]); // spareDeployYn
                } else if (i == 4) {
                    spareOnlyYn = StringUtils.trim(args[4]); // spareOnlyYn
                }

            }
        } else if ("deploy".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetL4 = StringUtils.trim(args[1]); // Target L4
                } else if (i == 2) {
                    targetSystem = StringUtils.trim(args[2]); // Target System FO, MO, BO, TO
                } else if (i == 3) {
                    lincenseKey = StringUtils.trim(args[3]); // LincenseKey
                }

            }
        } else if ("recover".equals(runType)) {
            /**
             * 서버 복구시 사용
             * 
             * targetNumber는 특정숫자 미지정시에 99[마지막벅째로]로 기본 지정
             * 
             * Tartget일자는 특정한 날짜 미지정시 현재날짜로 기본 지정
             * 
             * server [10.154.17.205]
             * 
             * 경로 [/data/home/hisis/ec/tools/deploy/bin/recover]
             * 
             * 1. 예) java -jar ../deploy.jar recover FO1 99 real
             * 
             * 8. [/data/home/hisis/ec/tools/deploy/bin/] clean_deploy.sh : 초기화
             * 
             ********************************************************************************/

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test
                } else if (i == 2) {
                    targetSystem = StringUtils.trim(args[2]); // Target System FO1, LPS1, MO1, MLPS1, BO1, PO1, CC1, TO1, BATCH1, FOIMAGE1,
                                                              // MOIMAGE1
                } else if (i == 3) {
                    targetOrder = StringUtils.trim(args[3]); // 1: 첫번째, 99: 마지막, 그외는 그 숫자 그대로
                } else if (i == 4) {
                    targetNumber = StringUtils.trim(args[4]); //
                } else if (i == 5) {
                    targetDate = StringUtils.trim(args[5]); // Tartget일자 예)20180301 , 값이 없을 경우 오늘날짜로 셋팅
                }

            }
        } else if ("recoverzipping".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test
                } else if (i == 2) {
                    targetSystem = StringUtils.trim(args[2]); // Target System FO1, LPS1, MO1, MLPS1, BO1, PO1, CC1, TO1, BATCH1, FOIMAGE1,
                } else if (i == 3) {
                    spareDeployYn = StringUtils.trim(args[3]); // spareDeployYn
                } else if (i == 4) {
                    spareOnlyYn = StringUtils.trim(args[4]); // spareOnlyYn
                } else if (i == 5) {
                    targetOrder = StringUtils.trim(args[5]); // 1: 첫번째, 99: 마지막, 그외는 그 숫자 그대로
                } else if (i == 6) {
                    targetNumber = StringUtils.trim(args[6]); //
                } else if (i == 7) {
                    targetDate = StringUtils.trim(args[7]); // Tartget일자 예)20180301 , 값이 없을 경우 오늘날짜로 셋팅
                }

            }
        } else if ("recoverdirlist".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test
                } else if (i == 2) {
                    targetSystem = StringUtils.trim(args[2]); // Target System FO1, LPS1, MO1, MLPS1, BO1, PO1, CC1, TO1, BATCH1, FOIMAGE1,
                                                              // MOIMAGE1
                } else if (i == 3) {
                    targetDate = StringUtils.trim(args[3]); // Tartget일자 예)20180301 , 값이 없을 경우 오늘날짜로 셋팅
                }

            }
        } else if ("waslog".equals(runType) || "jeuslog".equals(runType)) {

            /**
             * 서버 로그 다운시 사용
             * 
             * server [10.154.17.205]
             * 
             * 경로 [/data/home/hisis/ec/tools/deploy/bin/waslog]
             * 
             * Tartget일자는 특정한 날짜 미지정시 현재날짜로 기본 지정
             * 
             * 1. 예) java -jar ../deploy.jar waslog 01 real FO
             * 
             * 2. TEST서버 FO 로그 다운시 : ./waslog_test.sh FO
             * 
             * 3. TEST서버 FO 로그 다운시 [20180302] : ./waslog_test.sh FO 20180302
             * 
             * 4. 운영서버 FO 로그 다운시 : ./waslog_real.sh FO
             * 
             * 5. 운영서버 FO 로그 다운시 [20180302] : ./waslog_real.sh FO 20180302
             * 
             * 6. 운영서버 1번기만 FO 로그 다운시: ./waslog_01_real.sh FO
             * 
             * 7. 운영서버 LPS easypay 로그 다운시: ./waslog_real.sh LPS 20180302 Y
             * 
             * 8. [/data/home/hisis/ec/tools/deploy/bin/] clean_deploy.sh : 초기화
             * 
             ********************************************************************************/


            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetL4 = StringUtils.trim(args[1]); // Target L4
                } else if (i == 2) {
                    targetServer = StringUtils.trim(args[2]); // real, test, staging
                } else if (i == 3) {
                    targetSystem = StringUtils.trim(args[3]); // Target System FO, MO, BO, TO
                } else if (i == 4) {
                    easypayYn = StringUtils.trim(args[4]); // eaypay로그 여부 예) Y 면 esaypay로그
                } else if (i == 5) {
                    targetDate = StringUtils.trim(args[5]); // Tartget일자 예)20180301 , 값이 없을 경우 오늘날짜로 셋팅
                }

            }
        } else if ("threaddump".equals(runType) || "precompile".equals(runType) || "ssoproperties".equals(runType)
                || "curlurlcall".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // targetSystem
                }
            }
        } else if ("cpucheck".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    searchMonth = StringUtils.trim(args[1]); // searchMonth
                }
            }
        } else if ("imagerefresh".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetSystem = StringUtils.trim(args[1]); // target_system
                } else if (i == 2) {
                    spareDeployYn = StringUtils.trim(args[2]); // spareDeployYn
                } else if (i == 3) {
                    spareOnlyYn = StringUtils.trim(args[3]); // spareOnlyYn
                }
            }
        } else if ("wasserverruncheck".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test
                } else if (i == 2) {
                    targetSystem = StringUtils.trim(args[2]); // Target System
                } else if (i == 3) {
                    webServerCheck = StringUtils.trim(args[3]); // webServerCheck
                }

            }
        } else if ("deploytimecheck".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    jenkinsUser = StringUtils.trim(args[1]); // jenkinsUser
                }

            }
        } else if ("megingerrorcheck".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test, dev
                } else if (i == 2) {
                    searchStr = StringUtils.trim(args[2]); // searchStr
                }


            }
        } else if ("templatefileupload".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    targetServer = StringUtils.trim(args[1]); // real, test, dev
                } else if (i == 2) {
                    itemId = StringUtils.trim(args[2]); // itemId
                }


            }
        } else if ("shellscriptcommon".equals(runType)) {

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    runType = StringUtils.trim(args[0]);
                } else if (i == 1) {
                    shellScriptNo = StringUtils.trim(args[1]); // shellScriptNo
                } else if (i == 2) {
                    parameter01 = StringUtils.trim(args[2]); // parameter01
                } else if (i == 3) {
                    parameter02 = StringUtils.trim(args[3]); // parameter02
                } else if (i == 4) {
                    parameter03 = StringUtils.trim(args[4]); // parameter03
                }


            }

        }


        System.out.println("================================================================");
        System.out.println("targetL4 : " + targetL4);
        System.out.println("targetSystem : " + targetSystem);
        System.out.println("targetDate : " + targetDate);
        System.out.println("targetOrder : " + targetOrder);
        System.out.println("targetNumber : " + targetNumber);
        System.out.println("targetServer : " + targetServer);
        System.out.println("easypayYn : " + easypayYn);
        System.out.println("searchMonth : " + searchMonth);
        System.out.println("spareDeployYn : " + spareDeployYn);
        System.out.println("spareOnlyYn : " + spareOnlyYn);
        System.out.println("zippingYnStr : " + zippingYnStr);
        System.out.println("shellScriptNo : " + shellScriptNo);
        System.out.println("parameter01 : " + parameter01);


        System.out.println("================================================================");

        runType = runType.replace("_slt", "");

        if (zippingYnStr.equals("N")) {
            zippingYn = false;
        }

        if (!"taglistdel".equals(runType) && !"cpucheck".equals(runType) && !"arcusdbcache".equals(runType)) {

            if ("threaddump".equals(runType) || "precompile".equals(runType) || "ssoproperties".equals(runType)) {
                if (StringUtils.isEmpty(targetSystem)) {
                    printUsage();
                    return;
                }
            } else if ("recover".equals(runType)) {

                if (StringUtils.isNotEmpty(targetOrder)) {
                    String[] targetOrderToken = targetOrder.split("[|]");
                    if (targetOrderToken[1].equals("00")) {
                        if (StringUtils.isEmpty(targetNumber)) {
                            printUsage();
                            return;
                        }

                    } else {
                        targetNumber = targetOrderToken[1];
                    }
                }

                if (StringUtils.isEmpty(targetServer) || StringUtils.isEmpty(targetSystem) || StringUtils.isEmpty(targetNumber)) {
                    printUsage();
                    return;
                }


            } else if ("recoverdirlist".equals(runType) || "wasserverruncheck".equals(runType)) {

                if (StringUtils.isEmpty(targetServer) || StringUtils.isEmpty(targetSystem)) {
                    printUsage();
                    return;
                }


            } else if ("item_image".equals(runType)) {
                if (StringUtils.isEmpty(targetServer)) {
                    printUsage();
                    return;
                }
            } else if ("deploycommon".equals(runType) || "imagerefresh".equals(runType)) {
                if (StringUtils.isEmpty(targetSystem)) {
                    printUsage();
                    return;
                }
            } else if ("reboot".equals(runType) || "deployzipping".equals(runType)) {
                if (StringUtils.isEmpty(targetSystem)) {
                    printUsage();
                    return;
                }
            } else if ("deployzippingonly".equals(runType)) {
                if (StringUtils.isEmpty(baseSystem)) {
                    printUsage();
                    return;
                }
            } else if ("deployzippingbybasesystem".equals(runType)) {
                if (StringUtils.isEmpty(baseSystem) || targetSystemArr.size() == 0) {
                    printUsage();
                    return;
                }

                if (!baseSystemCheck(props, baseSystem)) {
                    log.info("### base System을 정확하게 입력해주세요. !!!");
                    throw new RuntimeException("base System을 정확하게 입력해주세요. !!");
                }
            } else {

                // if (StringUtils.isEmpty(targetL4) || StringUtils.isEmpty(targetSystem)) {
                // printUsage();
                // return;
                // }

            }



        }

        if (targetSystem.equals("---CHOICE---") || targetSystem.equals("-----REAL_SERVER_CHOISE-------")
                || targetSystem.equals("-----TEST_SERVER_CHOISE-------")) {
            printUsage();
            return;
        }

        // 라이센스키 체크
        // if (!LicenseKeyCheck(lincenseKey, runType)) {
        // printLicence();
        // return;
        // }

        // 모두 대문자로 변환
        targetSystem = targetSystem.toUpperCase();

        /*
         * 실행 방식
         * 
         * deploy : 배포
         * 
         * recover : WAS 원복
         * 
         * waslog : WAS 로그 다운
         * 
         * jeuslog : JEUS 로그 다운
         * 
         * threaddump : 운영 WAS서버 thread dump
         */
        if ("deploycommon".equals(runType)) {

            new DeployWithProperties(props, targetSystem, spareDeployYn, spareOnlyYn, targetL4);

        } else if ("deploy".equals(runType)) {

            new Deploy(props, targetL4, targetSystem);

        } else if ("arcusdbcache".equals(runType)) {

            new AcusDbCacheControl(props, targetSystem, cacheEnableYn, spareDeployYn, spareOnlyYn);

        } else if ("ssoproperties".equals(runType)) {

            new SsoPropertiesControl(props, targetSystem);

        } else if ("recover".equals(runType)) {


            System.out.println("targetDate :::" + targetDate);
            System.out.println("targetNumber :::" + targetNumber);
            System.out.println("targetServer :::" + targetServer);
            System.out.println("targetSystem :::" + targetSystem);

            new Recover(props, targetServer, targetSystem, targetNumber, targetDate);

        } else if ("recoverdirlist".equals(runType)) {

            Recover recover = new Recover();
            recover.getRecoverDirList(props, targetDate, targetSystem, targetServer);

        } else if ("recoverzipping".equals(runType)) {


            System.out.println("targetDate :::" + targetDate);
            System.out.println("targetNumber :::" + targetNumber);
            System.out.println("targetServer :::" + targetServer);
            System.out.println("targetSystem :::" + targetSystem);
            System.out.println("spareDeployYn : " + spareDeployYn);
            System.out.println("spareOnlyYn : " + spareOnlyYn);

            new Recover(props, targetServer, targetSystem, spareDeployYn, spareOnlyYn, targetNumber, targetDate);

        } else if ("waslog".equals(runType)) {

            if (targetDate.equals("Y") || targetDate.equals("N")) {
                easypayYn = targetDate;
                targetDate = DateUtils.getDate("yyyyMMdd");
            }

            new WasLog(props, targetL4, targetServer, targetSystem, targetDate, easypayYn);

        } else if ("jeuslog".equals(runType)) {

            new JeusServerLog(props, targetL4, targetServer, targetSystem, targetDate);

        } else if ("threaddump".equals(runType)) {
            // ./threaddump.sh
            new ThreadDump(props, targetSystem);

        } else if ("precompile".equals(runType)) {
            // ./precompile.sh
            new PreCompile(props, targetSystem);

        } else if ("precompilecommon".equals(runType)) {
            // ./precompile_common.sh

            if (targetSystem.contains("SET")) {
                new PreCompile(props, targetSystem, spareOnlyYn);
            } else {
                new PreCompile(props, targetSystem);
            }


        } else if ("reboot".equals(runType)) {
            boolean isWebServer = false;
            if (webServerCheck.equals("web")) {
                isWebServer = true;
            }
            new RebootServer(props, targetSystem, spareDeployYn, spareOnlyYn, isWebServer);

        } else if ("cpucheck".equals(runType)) {
            new CpuMonitoring(props, searchMonth);

        } else if ("taglistdel".equals(runType)) {
            new SvnManage(runType);

        } else if ("item_image".equals(runType)) {
            new ItemImageSync(props, targetServer);

        } else if ("imagerefresh".equals(runType)) {
            new ImageRefresh(props, targetSystem, spareDeployYn, spareOnlyYn);

        } else if ("wasserverruncheck".equals(runType)) {
            boolean isWebServer = false;
            if (webServerCheck.equals("web")) {
                isWebServer = true;
            }
            new WasServerRunCheck(props, targetServer, targetSystem, isWebServer);

        } else if ("deployzippingonly".equals(runType)) {
            new DeployOnServerByZiping(props, baseSystem);

        } else if ("deployzipping".equals(runType)) {
            new DeployOnServerByZiping(props, targetSystem, spareOnlyYn, zippingYn);

        } else if ("deployzippingbybasesystem".equals(runType)) {
            new DeployOnServerByZiping(props, baseSystem, targetSystemArr, zippingYn);

        } else if ("deploytimecheck".equals(runType)) {
            new DeployTimeCheck(jenkinsUser);

        } else if ("megingerrorcheck".equals(runType)) {
            new MergingErrorCheck(targetServer, searchStr);

        } else if ("templatefileupload".equals(runType)) {
            if (isParsableToInt(itemId)) {
                int intItemId = Integer.parseInt(itemId);
                new TemplateFileUpload(props, targetServer, intItemId);
            }


        } else if ("curlurlcall".equals(runType)) {
            new CurlUrlCall(props, targetSystem);

        } else if ("shellscriptcommon".equals(runType)) {

            new ShellScriptCallCommon(props, shellScriptNo, parameter01, parameter02, parameter03);

        }



        log.info("[Deploy [runType : {} >> END] ======[" + runType + "]");

    }


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
     * 라이센스체크
     * 
     * @throws Exception
     * @throws BadPaddingException
     * @throws InvalidParameterSpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static boolean LicenseCheck(String runType) throws Exception {

        boolean isLicense = false;
        File f = null;
        if (runType.contains("_slt")) {
            f = new File(LICENSE_FILE_SLT);
        } else {
            f = new File(LICENSE_FILE);
        }


        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        // System.out.println("LICENSE_FILE:: " + LICENSE_FILE);

        if (f.exists()) {

            try {
                if (runType.contains("_slt")) {
                    is = new FileInputStream(LICENSE_FILE_SLT);
                } else {
                    is = new FileInputStream(LICENSE_FILE);
                }

                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                String key = "10.154.17.205_vEC-WTB_SUB";

                str = br.readLine();
                // System.out.println("str:::" + str);

                if (CryptoUtil.decryptAES(str, key).equals("himart")) {
                    isLicense = true;

                    str = br.readLine();

                    String decStr = CryptoUtil.decryptAES(str, key).trim();
                    // int decStrInt = Integer.parseInt(decStr);
                    String todate = DateUtils.getDate("yyyy");

                    if (decStr.equals(todate)) {
                        isLicense = true;
                    } else {
                        isLicense = false;
                    }

                    // int compStrInt = Integer.parseInt(todate);
                    //
                    // if (decStrInt == compStrInt) {
                    // isLicense = true;
                    // } else {
                    // isLicense = false;
                    // }

                } else {
                    isLicense = false;
                }

                System.out.println("LICENCE CHECK :::" + isLicense);

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

        return isLicense;
    }


    /**
     * 라이센스Key체크
     * 
     * @throws Exception
     * @throws BadPaddingException
     * @throws InvalidParameterSpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static boolean LicenseKeyCheck(String lincenseKey, String runType) throws Exception {

        boolean isLicenseKey = false;
        String key = "10.154.17.205_vEC-WTB_SUB";

        if (StringUtils.isEmpty(lincenseKey) || lincenseKey.equals("N")) {
            lincenseKey = "mdaQRMKMqz8+yHskqdzR40K6XpLctXDn+CPHrjXeXhD2eE9KblSpE4ot0ugd7KWpd5aiBg==";
        }

        if (runType.equals("deploy") || runType.equals("deploycommon") || runType.equals("waslog") || runType.equals("jeuslog")) {
            if (CryptoUtil.decryptAES(lincenseKey, key).equals("dragonvnfmgksmf")) {
                isLicenseKey = true;
            }

            System.out.println("deploy, deploycommonk, LICENCE KEY CHECK 11 :::" + isLicenseKey);
            // isLicenseKey = true;

        } else {
            isLicenseKey = true;
        }


        System.out.println("LICENCE KEY CHECK :::" + isLicenseKey);

        return isLicenseKey;
    }


    private static void printUsage() {

        log.info("### Deploy RunType need argument !!!");
        throw new RuntimeException("Deploy RunType need argument !!!");
    }


    private static void printLicence() {

        log.info("### 라이센스가 만료되었습니다.");
        throw new RuntimeException("라이센스가 만료되었습니다. !!!");
    }


    public static boolean test(Properties props) throws Exception {

        String deploySystem = "PO";
        String deploySystemFtp = "PO1";

        Recover recover = new Recover();
        recover.setRecoverCommon(props, deploySystem, deploySystemFtp);

        return false;
    }


    private static boolean isParsableToInt(String i) {

        try {
            Integer.parseInt(i);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public static boolean baseSystemCheck(Properties props, String baseSystem) {

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

        for (String element : FO_SET_01) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : FO_SET_02) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : MO_SET_01) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : MO_SET_02) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : SPARE_FO_SET_01) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : SPARE_FO_SET_02) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : SPARE_MO_SET_01) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        for (String element : SPARE_MO_SET_02) {
            if (baseSystem.equals(element)) {
                return true;
            }
        }

        if (baseSystem.equals("MO_IMAGE1") || baseSystem.equals("MO_IMAGE2") || baseSystem.equals("FO_IMAGE1")
                || baseSystem.equals("FO_IMAGE2")) {
            return true;
        }

        return false;
    }

}
