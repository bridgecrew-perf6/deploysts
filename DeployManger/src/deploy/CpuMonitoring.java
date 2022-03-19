package deploy;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class CpuMonitoring {

    static final String LOCAL_DEFAULT_DIR = "/data/home/hisis/ec/tools/hudson/apache-tomcat-7.0.59/webapps/ROOT/cpucheck/";
    static final String CPU_CHECK_SHELL_COMMNAD = "sh /NAS-EC_PRD/files/monitoring/run_cpucheck.sh";
    static final String CPU_SEARCH_CHECK_SHELL_COMMNAD = "sh /data/WiseNut/cpu/run_search_cpucheck.sh";
    static final String DEPLOY_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/temp/";


    public CpuMonitoring(Properties props, String searchMonth) {

        // 파일다운
        getCpulogDownCommon(props);

        // cpu csv파일 생성
        CpuCheck(props, searchMonth);

        String command = "sh /data/home/hisis/ec/tools/deploy/bin/cpucheck/cpu_option3.sh";
        localCommandExec(command);

        System.out.println("######################################################################");
        System.out.println("DOWNLOAD  Click Url ");
        System.out.println("######################################################################");
        System.out.println("");
        System.out.println("http://10.154.17.205:8081/cpucheck/cpu.tar");
        System.out.println("http://10.154.17.205:8081/cpucheck/CPU-AVG_" + searchMonth + ".csv");
        System.out.println("http://10.154.17.205:8081/cpucheck/CPU_" + searchMonth + ".csv");
        System.out.println("http://10.154.17.205:8081/cpucheck/NAS_" + searchMonth + ".csv");
        System.out.println("");
        System.out.println("######################################################################");



    }


    /**
     * CPU CHECK
     * 
     * 이미지 파일인지 확인한다.
     * 
     * @param files
     * @return
     */
    public static void CpuCheck(Properties props, String searchMonth) {

        // MO
        Reader mo_reader = null;
        InputStream mo_is = null;
        BufferedReader mo_br = null;

        // FO
        Reader fo_reader = null;
        InputStream fo_is = null;
        BufferedReader fo_br = null;

        // LPS
        Reader lps_reader = null;
        InputStream lps_is = null;
        BufferedReader lps_br = null;

        // mLPS
        Reader mlps_reader = null;
        InputStream mlps_is = null;
        BufferedReader mlps_br = null;

        // BATCH
        Reader batch_reader = null;
        InputStream batch_is = null;
        BufferedReader batch_br = null;

        // BO
        Reader bo_reader = null;
        InputStream bo_is = null;
        BufferedReader bo_br = null;

        // BO
        Reader search_reader = null;
        InputStream search_is = null;
        BufferedReader search_br = null;


        // MO_CPU
        Reader mo_cpu_reader = null;
        InputStream mo_cpu_is = null;
        BufferedReader mo_cpu_br = null;

        // FO_CPU
        Reader fo_cpu_reader = null;
        InputStream fo_cpu_is = null;
        BufferedReader fo_cpu_br = null;

        // NAS
        Reader nas_reader = null;
        InputStream nas_is = null;
        BufferedReader nas_br = null;

        // CPU AVG FILE
        FileWriter fw = null;
        BufferedWriter bw = null;

        // CPU FILE
        FileWriter fw_cpu = null;
        BufferedWriter bw_cpu = null;

        // NAS FILE
        FileWriter fw_nas = null;
        OutputStreamWriter ow = null;
        FileOutputStream fs = null;
        BufferedWriter bw_nas = null;

        String cpuCheckFullPath = DEPLOY_TEMP_DIR + "cpu/";

        System.out.println("cpuCheckFullPath ::" + cpuCheckFullPath);

        String moCpuCheckDir = cpuCheckFullPath + "MO/";
        String foCpuCheckDir = cpuCheckFullPath + "FO/";
        String lpsCpuCheckDir = cpuCheckFullPath + "LPS/";
        String mLpsCpuCheckDir = cpuCheckFullPath + "MLPS/";
        String batchCpuCheckDir = cpuCheckFullPath + "BATCH/";
        String boCpuCheckDir = cpuCheckFullPath + "BO/";
        String nasCheckDir = cpuCheckFullPath + "NAS/";
        String searchCpuCheckDir = cpuCheckFullPath + "avg/";

        try {

            String str;
            String[] strArr;

            StringBuffer cpuAvg = new StringBuffer();
            StringBuffer cpu = new StringBuffer();

            List<String> cpuAvgList = new ArrayList<String>();
            List<String> nasList = new ArrayList<String>();
            List<String> cpuList = new ArrayList<String>();

            String searchDate = null;

            cpuAvg.append("DATE");
            cpuAvg.append(",");
            cpuAvg.append("MO");
            cpuAvg.append(",");
            cpuAvg.append("MO(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("FO");
            cpuAvg.append(",");
            cpuAvg.append("FO(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("LPS");
            cpuAvg.append(",");
            cpuAvg.append("LPS(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("mLPS");
            cpuAvg.append(",");
            cpuAvg.append("mLPS(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("BATCH");
            cpuAvg.append(",");
            cpuAvg.append("BATCH(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("BO");
            cpuAvg.append(",");
            cpuAvg.append("BO(MAX)");
            cpuAvg.append(",");
            cpuAvg.append("SEARCH");
            cpuAvg.append(",");
            cpuAvg.append("SEARCH(MAX)");
            cpuAvgList.add(cpuAvg.toString());

            nasList.add("DATE,%,,,전체,USE,,,AVAIL");

            for (int i = 1; i <= 31; i++) {
                String cpuCheckflist = "CPU_";
                if (i < 10) {
                    searchDate = searchMonth + "0" + i;
                    cpuCheckflist = cpuCheckflist + searchMonth + "0" + i + ".txt";
                } else {
                    searchDate = searchMonth + i;
                    cpuCheckflist = cpuCheckflist + searchMonth + i + ".txt";
                }

                File f = new File(moCpuCheckDir + cpuCheckflist);

                if (f.exists()) {
                    cpu.append(searchDate + "_FO_DATE");
                    cpu.append(",");
                    cpu.append(searchDate + "_FO_CPU");
                    cpu.append(",");
                    cpu.append(searchDate + "_MO_DATE");
                    cpu.append(",");
                    cpu.append(searchDate + "_MO_CPU");
                    cpu.append(",");
                }
            }
            cpuList.add(cpu.toString());

            for (int i = 1; i <= 31; i++) {

                String cpuAvgCheckflist = "CPU-AVG_";
                String cpuCheckflist = "CPU_";
                String nasCheckflist = "NAS_";

                if (i < 10) {
                    searchDate = searchMonth + "0" + i;
                    cpuAvgCheckflist = cpuAvgCheckflist + searchMonth + "0" + i + ".txt";
                    cpuCheckflist = cpuCheckflist + searchMonth + "0" + i + ".txt";
                    nasCheckflist = nasCheckflist + searchMonth + "0" + i + ".txt";
                } else {
                    searchDate = searchMonth + i;
                    cpuAvgCheckflist = cpuAvgCheckflist + searchMonth + i + ".txt";
                    cpuCheckflist = cpuCheckflist + searchMonth + i + ".txt";
                    nasCheckflist = nasCheckflist + searchMonth + i + ".txt";
                }

                File f = new File(moCpuCheckDir + cpuAvgCheckflist);

                if (f.exists()) {

                    // # CPU-AVG_
                    cpuAvg = new StringBuffer();
                    cpuAvg.append(searchDate);
                    cpuAvg.append(",");

                    // MO
                    mo_is = new FileInputStream(moCpuCheckDir + cpuAvgCheckflist);
                    mo_reader = new InputStreamReader(mo_is, "utf-8");
                    mo_br = new BufferedReader(mo_reader);

                    while ((str = mo_br.readLine()) != null) {
                        strArr = str.split("=");

                        if (strArr != null && strArr[1] != null) {
                            if (str.contains("Average")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            } else if (str.contains("Max")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            }
                        }
                    }// while_end


                    // FO
                    fo_is = new FileInputStream(foCpuCheckDir + cpuAvgCheckflist);
                    fo_reader = new InputStreamReader(fo_is, "utf-8");
                    fo_br = new BufferedReader(fo_reader);

                    while ((str = fo_br.readLine()) != null) {
                        strArr = str.split("=");
                        if (strArr != null && strArr[1] != null) {
                            if (str.contains("Average")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            } else if (str.contains("Max")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            }
                        }
                    }// while_end

                    // LPS
                    lps_is = new FileInputStream(lpsCpuCheckDir + cpuAvgCheckflist);
                    lps_reader = new InputStreamReader(lps_is, "utf-8");
                    lps_br = new BufferedReader(lps_reader);

                    while ((str = lps_br.readLine()) != null) {
                        strArr = str.split("=");
                        if (strArr != null && strArr[1] != null) {
                            if (str.contains("Average")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            } else if (str.contains("Max")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            }
                        }
                    }// while_end

                    // LPS
                    mlps_is = new FileInputStream(mLpsCpuCheckDir + cpuAvgCheckflist);
                    mlps_reader = new InputStreamReader(mlps_is, "utf-8");
                    mlps_br = new BufferedReader(mlps_reader);

                    while ((str = mlps_br.readLine()) != null) {
                        strArr = str.split("=");
                        if (strArr != null && strArr[1] != null) {
                            if (str.contains("Average")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            } else if (str.contains("Max")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            }
                        }
                    }// while_end

                    // BATCH
                    batch_is = new FileInputStream(batchCpuCheckDir + cpuAvgCheckflist);
                    batch_reader = new InputStreamReader(batch_is, "utf-8");
                    batch_br = new BufferedReader(batch_reader);

                    while ((str = batch_br.readLine()) != null) {
                        strArr = str.split("=");
                        if (strArr != null && strArr[1] != null) {
                            if (str.contains("Average")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            } else if (str.contains("Max")) {
                                cpuAvg.append(strArr[1]);
                                cpuAvg.append(",");
                            }
                        }
                    }// while_end


                    // BO
                    File f_bo = new File(boCpuCheckDir + cpuAvgCheckflist);
                    if (f_bo.exists()) {
                        bo_is = new FileInputStream(boCpuCheckDir + cpuAvgCheckflist);
                        bo_reader = new InputStreamReader(bo_is, "utf-8");
                        bo_br = new BufferedReader(bo_reader);

                        while ((str = bo_br.readLine()) != null) {
                            strArr = str.split("=");
                            if (strArr != null && strArr[1] != null) {
                                if (str.contains("Average")) {
                                    cpuAvg.append(strArr[1]);
                                    cpuAvg.append(",");
                                } else if (str.contains("Max")) {
                                    cpuAvg.append(strArr[1]);
                                    cpuAvg.append(",");
                                }
                            }
                        }// while_end
                    }

                    // SEARCH
                    File f_search = new File(searchCpuCheckDir + cpuAvgCheckflist);
                    if (f_search.exists()) {
                        search_is = new FileInputStream(searchCpuCheckDir + cpuAvgCheckflist);
                        search_reader = new InputStreamReader(search_is, "utf-8");
                        search_br = new BufferedReader(search_reader);

                        while ((str = search_br.readLine()) != null) {
                            strArr = str.split("=");
                            if (strArr != null && strArr[1] != null) {
                                if (str.contains("Average")) {
                                    cpuAvg.append(strArr[1]);
                                    cpuAvg.append(",");
                                } else if (str.contains("Max")) {
                                    cpuAvg.append(strArr[1]);
                                }
                            }
                        }// while_end
                    }

                    cpuAvgList.add(cpuAvg.toString());



                    // FO_CPU
                    fo_cpu_is = new FileInputStream(foCpuCheckDir + cpuCheckflist);
                    fo_cpu_reader = new InputStreamReader(fo_cpu_is, "utf-8");
                    fo_cpu_br = new BufferedReader(fo_cpu_reader);
                    int loopint = 1;

                    String listGetStr = "";
                    while ((str = fo_cpu_br.readLine()) != null) {
                        listGetStr = "";
                        str = str.replace(" ", ",").trim();

                        if (loopint < cpuList.size() && cpuList.get(loopint) != null) {
                            listGetStr = cpuList.get(loopint);
                        }

                        listGetStr = listGetStr + str + ",";
                        if (listGetStr.equals(str + ",")) {
                            cpuList.add(loopint, listGetStr);
                        } else {
                            cpuList.set(loopint, listGetStr);
                        }

                        loopint++;

                        if (loopint == 284) {
                            break;
                        }
                    }// while_end

                    if (loopint < 284) {
                        listGetStr = "";
                        for (int j = loopint; j < 284; j++) {
                            if (j < cpuList.size() && cpuList.get(j) != null) {
                                listGetStr = cpuList.get(j);
                            }

                            listGetStr = listGetStr + ",,";
                            if (listGetStr.equals(",,")) {
                                cpuList.add(j, listGetStr);
                            } else {
                                cpuList.set(j, listGetStr);
                            }

                        }
                    }


                    loopint = 1;

                    // MO_CPU
                    mo_cpu_is = new FileInputStream(moCpuCheckDir + cpuCheckflist);
                    mo_cpu_reader = new InputStreamReader(mo_cpu_is, "utf-8");
                    mo_cpu_br = new BufferedReader(mo_cpu_reader);

                    while ((str = mo_cpu_br.readLine()) != null) {
                        listGetStr = "";
                        str = str.replace(" ", ",").trim();

                        if (loopint < cpuList.size() && cpuList.get(loopint) != null) {
                            listGetStr = cpuList.get(loopint);
                        }

                        listGetStr = listGetStr + str + ",";
                        if (listGetStr.equals(str + ",")) {
                            cpuList.add(loopint, listGetStr);
                        } else {
                            cpuList.set(loopint, listGetStr);
                        }

                        loopint++;

                        if (loopint == 284) {
                            break;
                        }

                    }// while_end

                    if (loopint < 284) {
                        listGetStr = "";
                        for (int j = loopint; j < 284; j++) {
                            if (j < cpuList.size() && cpuList.get(j) != null) {
                                listGetStr = cpuList.get(j);
                            }

                            listGetStr = listGetStr + ",,";
                            if (listGetStr.equals(",,")) {
                                cpuList.add(j, listGetStr);
                            } else {
                                cpuList.set(j, listGetStr);
                            }

                        }
                    }


                }// file 체크 여부


                // NAS 용량 체크
                strArr = null;
                String nasStr = null; // nasStr

                File f_nas = new File(nasCheckDir + nasCheckflist);
                if (f_nas.exists()) {
                    // NAS
                    nas_is = new FileInputStream(nasCheckDir + nasCheckflist);
                    nas_reader = new InputStreamReader(nas_is, "utf-8");
                    nas_br = new BufferedReader(nas_reader);

                    while ((str = nas_br.readLine()) != null) {
                        strArr = str.split(",");

                        if (strArr[0] != null) {
                            nasStr = strArr[0];
                            nasStr = nasStr + ",";
                        }

                        if (strArr[4] != null) {
                            nasStr = nasStr + "NAS 사용율 : " + strArr[4];
                            nasStr = nasStr + ",";
                            nasStr = nasStr + ",";
                            nasStr = nasStr + ",";
                        }
                        if (strArr[1] != null) {
                            nasStr = nasStr + "전체 : " + strArr[1];
                            nasStr = nasStr + ",";
                        }
                        if (strArr[2] != null) {
                            nasStr = nasStr + "사용 중 : " + strArr[2];
                            nasStr = nasStr + ",";
                            nasStr = nasStr + ",";
                            nasStr = nasStr + ",";
                        }
                        if (strArr[3] != null) {
                            nasStr = nasStr + "남은 용량 : " + strArr[3];
                        }
                        nasList.add(nasStr);
                    }// while_end
                }

            }// for_end


            // CPU-AVG_ 파일 생성
            File cpuAvgFile = new File(LOCAL_DEFAULT_DIR + "CPU-AVG_" + searchMonth + ".csv");

            fw = new FileWriter(cpuAvgFile);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < cpuAvgList.size(); i++) {
                bw.write(cpuAvgList.get(i) + "\n");
                bw.flush();
            }
            System.out.println("cpuAvgList.size() ::::" + cpuAvgList.size());
            System.out.println("CPU-AVG 파일 생성 SUCCESS ::::");

            // NAS 파일 생성
            File nasFile = new File(LOCAL_DEFAULT_DIR + "NAS_" + searchMonth + ".csv");

            // fw_nas = new FileWriter(nasFile);
            fs = new FileOutputStream(nasFile);;
            ow = new OutputStreamWriter(fs, "euc-kr");;

            bw_nas = new BufferedWriter(ow);

            for (int i = 0; i < nasList.size(); i++) {
                bw_nas.write(nasList.get(i) + "\n");
                bw_nas.flush();
            }
            System.out.println("nasList.size() ::::" + nasList.size());
            System.out.println("NAS 파일 생성 SUCCESS ::::");

            // CPU_ 파일 생성
            File cpuFile = new File(LOCAL_DEFAULT_DIR + "CPU_" + searchMonth + ".csv");

            fw_cpu = new FileWriter(cpuFile);
            bw_cpu = new BufferedWriter(fw_cpu);


            for (int i = 0; i < cpuList.size(); i++) {
                bw_cpu.write(cpuList.get(i) + "\n");
                bw_cpu.flush();
            }
            System.out.println("cpuList.size() ::::" + cpuList.size());
            System.out.println("CPU_ 파일 생성 SUCCESS ::::");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (mo_br != null) {
                    mo_br.close();
                }
                if (mo_reader != null) {
                    mo_reader.close();
                }
                if (mo_is != null) {
                    mo_is.close();
                }

                if (fo_br != null) {
                    fo_br.close();
                }
                if (fo_reader != null) {
                    fo_reader.close();
                }
                if (fo_is != null) {
                    fo_is.close();
                }

                if (lps_br != null) {
                    lps_br.close();
                }
                if (lps_reader != null) {
                    lps_reader.close();
                }
                if (lps_is != null) {
                    lps_is.close();
                }

                if (mlps_br != null) {
                    mlps_br.close();
                }
                if (mlps_reader != null) {
                    mlps_reader.close();
                }
                if (mlps_is != null) {
                    mlps_is.close();
                }

                if (batch_br != null) {
                    batch_br.close();
                }
                if (batch_reader != null) {
                    batch_reader.close();
                }
                if (batch_is != null) {
                    batch_is.close();
                }

                if (bo_br != null) {
                    bo_br.close();
                }
                if (bo_reader != null) {
                    bo_reader.close();
                }
                if (bo_is != null) {
                    bo_is.close();
                }

                if (search_br != null) {
                    search_br.close();
                }
                if (search_reader != null) {
                    search_reader.close();
                }
                if (search_is != null) {
                    search_is.close();
                }

                if (nas_br != null) {
                    nas_br.close();
                }
                if (nas_reader != null) {
                    nas_reader.close();
                }
                if (nas_is != null) {
                    nas_is.close();
                }

                if (fo_cpu_is != null) {
                    fo_cpu_is.close();
                }
                if (fo_cpu_reader != null) {
                    fo_cpu_reader.close();
                }
                if (fo_cpu_is != null) {
                    fo_cpu_is.close();
                }

                if (mo_cpu_br != null) {
                    mo_cpu_br.close();
                }
                if (mo_cpu_reader != null) {
                    mo_cpu_reader.close();
                }
                if (mo_cpu_is != null) {
                    mo_cpu_is.close();
                }


                if (fw != null) {
                    fw.close();
                }

                if (bw != null) {
                    bw.close();
                }

                if (fs != null) {
                    fs.close();
                }

                if (ow != null) {
                    ow.close();
                }

                if (bw_nas != null) {
                    bw_nas.close();
                }

                if (fw_cpu != null) {
                    fw_cpu.close();
                }

                if (bw_cpu != null) {
                    bw_cpu.close();
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

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


    /**
     * 로그 다운 공통
     */
    public static void getCpulogDownCommon_bak(Properties props) {

        String cpuFtpIp = "";
        int cpuFtpPort = 22;
        String cpuFtpId = "";
        String cpuFtpPw = "";
        String cpuFtpPath = "/NAS-EC_PRD/files/monitoring/";

        cpuFtpIp = props.getProperty("BO_STAGING.ftp.ip");
        cpuFtpId = props.getProperty("BO_STAGING.ftp.id");
        cpuFtpPw = props.getProperty("BO_STAGING.ftp.pw");

        SFTPService ftp = new SFTPService(cpuFtpIp, 22, cpuFtpId, cpuFtpPw);

        // cpu파일 tar생성
        ftp.sshCommandExecByNotLogger(CPU_CHECK_SHELL_COMMNAD);

        // LOCAL_DEFAULT_DIR로 다운로드
        ftp.download("cpu.tar", DEPLOY_TEMP_DIR, cpuFtpPath);

        // 압축해제
        // tar xvf cpu.tar
        // String command = "sh /data/home/hisis/ec/tools/deploy/bin/cpucheck/cpu_option1.sh";
        String command = "tar xvf /data/home/hisis/ec/tools/deploy/temp/cpu.tar -C /data/home/hisis/ec/tools/deploy/temp/";
        localCommandExec(command);

        // // 검색서버 접속
        // cpuFtpPath = "/data/WiseNut/cpu/";
        //
        // cpuFtpIp = props.getProperty("SEARCH3.ftp.ip");
        // cpuFtpId = props.getProperty("SEARCH3.ftp.id");
        // cpuFtpPw = props.getProperty("SEARCH3.ftp.pw");
        //
        // ftp = new SFTPService(cpuFtpIp, 22, cpuFtpId, cpuFtpPw);
        //
        // // avg파일 tar생성
        // ftp.sshCommandExecByNotLogger(CPU_SEARCH_CHECK_SHELL_COMMNAD);
        //
        // // LOCAL_DEFAULT_DIR로 다운로드
        // ftp.download("avg.tar", DEPLOY_TEMP_DIR + "cpu/", cpuFtpPath);
        //
        // try {
        // // System.out.println("sleep start::");
        // Thread.sleep(2 * 1000);
        // // System.out.println("sleep end::");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // // 압축해제
        // // tar xvf avg.tar
        // command = "sh /data/home/hisis/ec/tools/deploy/bin/cpucheck/cpu_option2.sh";
        // localCommandExec(command);

    }


    /**
     * 로그 다운 공통
     */
    public static void getCpulogDownCommon(Properties props) {

        String cpuFtpIp = "";
        int cpuFtpPort = 22;
        String cpuFtpId = "";
        String cpuFtpPw = "";
        String cpuFtpPath = "/NAS-EC_PRD/files/monitoring/";

        cpuFtpIp = props.getProperty("BO_STAGING.ftp.ip");
        cpuFtpId = props.getProperty("BO_STAGING.ftp.id");
        cpuFtpPw = props.getProperty("BO_STAGING.ftp.pw");

        SFTPService ftp = new SFTPService(cpuFtpIp, 22, cpuFtpId, cpuFtpPw);

        // cpu파일 tar생성
        ftp.sshCommandExecByNotLogger(CPU_CHECK_SHELL_COMMNAD);

        // LOCAL_DEFAULT_DIR로 다운로드
        ftp.download("cpu.tar", DEPLOY_TEMP_DIR, cpuFtpPath);

        // 압축해제
        // tar xvf cpu.tar
        // String command = "sh /data/home/hisis/ec/tools/deploy/bin/cpucheck/cpu_option1.sh";
        String command = "tar xvf /data/home/hisis/ec/tools/deploy/temp/cpu.tar -C /data/home/hisis/ec/tools/deploy/temp/";
        localCommandExec(command);

        // 검색서버 접속
        cpuFtpPath = "/data/WiseNut/cpu/";

        cpuFtpIp = props.getProperty("SEARCH3.ftp.ip");
        cpuFtpId = props.getProperty("SEARCH3.ftp.id");
        cpuFtpPw = props.getProperty("SEARCH3.ftp.pw");

        ftp = new SFTPService(cpuFtpIp, 22, cpuFtpId, cpuFtpPw);

        // avg파일 tar생성
        ftp.sshCommandExecByNotLogger(CPU_SEARCH_CHECK_SHELL_COMMNAD);

        // LOCAL_DEFAULT_DIR로 다운로드
        ftp.download("avg.tar", DEPLOY_TEMP_DIR + "cpu/", cpuFtpPath);

        try {
            // System.out.println("sleep start::");
            Thread.sleep(2 * 1000);
            // System.out.println("sleep end::");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 압축해제
        // tar xvf avg.tar
        command = "sh /data/home/hisis/ec/tools/deploy/bin/cpucheck/cpu_option2.sh";
        localCommandExec(command);

    }


    private static void localCommandExec(String command) {

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 정상적 출력

            // System.out.println("Process ===");
            String line = "";
            while (line != null) {
                line = reader.readLine();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



}
