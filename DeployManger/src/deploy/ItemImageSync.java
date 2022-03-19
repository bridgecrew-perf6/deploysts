package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import utils.SFTPService;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@Slf4j
public class ItemImageSync {

    static final String NAS_ITEM_DEFAULT_DIR = "/NAS-EC_PRD/files/contents/goods/";
    static final String NAS_ITEM_TEST_DEFAULT_DIR = "/NAS-EC_DEV/files/contents/goods/";
    static final String NAS_ITEM_DEV_DEFAULT_DIR = "/data/webapps/archive/E-HIMART/CONTENTS/contents/goods/";
    static final String LOCAL_DEFAULT_DIR = "/data/home/hisis/ec/tools/deploy/itemimage/";
    static final String LOCAL_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/waslog/temp/";
    static final String ITEM_IMAGE_LIST = "/data/home/hisis/ec/tools/deploy/item_image.txt";


    public ItemImageSync(Properties props, String targetServer, boolean test) {

    }


    public ItemImageSync(Properties props, String targetServer) {

        fileUpload(props, targetServer);
    }


    public void fileUpload(Properties props, String targetServer) {


        // 운영서버
        String wasFtpIp = props.getProperty("BO3.ftp.ip");
        String wasFtpId = props.getProperty("BO3.ftp.id");
        String wasFtpPw = props.getProperty("BO3.ftp.pw");
        int wasFtpPort = 22;


        String wasTestFtpIp = "";
        String wasTestFtpId = "";
        String wasTestFtpPw = "";
        int wasTestFtpPort = 22;

        SFTPService sftp = new SFTPService(wasFtpIp, wasFtpPort, wasFtpId, wasFtpPw);

        /**
         * 상품이미지 다운로드
         **************************************************/

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String command = "find /data/home/hisis/ec/tools/deploy/itemimage/resizereq/ | head -n 3 > " + ITEM_IMAGE_LIST;
        // command = "cd /data/home/hisis/ec/tools/deploy/bin:sh find_item_image.sh ";

        String[] cmd = {"/bin/bash", "-c", "cd /data/home/hisis/ec/tools/deploy/bin; sh find_item_image.sh"};

        // Runtime runTime = null;
        // Process process = null;
        try {

            System.out.println("command ::" + command);

            // runTime = Runtime.getRuntime();
            Process process = Runtime.getRuntime().exec(cmd);

            // BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //
            // String line = null;
            //
            // while ((line = br2.readLine()) != null) {
            //
            // System.out.println(line);
            //
            // }


            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println("ITEM_IMAGE_LIST :: " + ITEM_IMAGE_LIST);
        File f = new File(ITEM_IMAGE_LIST);

        int loopcount = 0;

        System.out.println("f.exists() :: " + f.exists());

        List<String> imageList = new ArrayList<String>();

        String wasPath = "";
        String localPath = "";


        if (f.exists()) {

            String str;
            String[] strArr;
            String deployStr; // deployStr

            try {
                is = new FileInputStream(ITEM_IMAGE_LIST);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                String itemImagePath = LOCAL_DEFAULT_DIR + "resizereq" + "/";

                while ((str = br.readLine()) != null) {

                    str = str.replace(itemImagePath, "");

                    // System.out.println("str ::" + str);

                    if (!str.isEmpty()) {
                        wasPath = NAS_ITEM_DEFAULT_DIR + "resizereq" + "/" + str;
                        localPath = LOCAL_DEFAULT_DIR + "resizereq" + "/" + str;

                        imageList.add(str);

                        // System.out.println("wasPath ::" + wasPath);
                        // System.out.println("localPath ::" + localPath);

                        // sftp.upload(localPath, wasPath);

                        // Thread.sleep(30 * 1000);

                        // Thread.sleep(1 * 1000);
                        //
                        // command = "rm -f " + localPath;
                        //
                        // System.out.println("rm -f  ::" + command);
                        //
                        // runTime = Runtime.getRuntime();
                        // process = runTime.exec(command);

                        loopcount++;
                    }


                }

                //
                // br.close();
                // reader.close();
                // is.close();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("=======");
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

            for (int i = 0; i < imageList.size(); i++) {

                wasPath = NAS_ITEM_DEFAULT_DIR + "resizereq" + "/" + imageList.get(i);
                localPath = LOCAL_DEFAULT_DIR + "resizereq" + "/" + imageList.get(i);

                // System.out.println("wasPath ::" + wasPath);
                // System.out.println("localPath ::" + localPath);

                sftp.upload(localPath, wasPath);

                try {
                    // Thread.sleep(30 * 1000);
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                command = "rm -f " + localPath;

                System.out.println("rm -f  ::" + command);

                // runTime = Runtime.getRuntime();
                try {
                    Process process2 = Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }



            System.out.println("loopcount ::" + loopcount);

            if (loopcount > 0) {
                // Thread.sleep(30 * 1000);

                command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_item_image.sh ";

                System.out.println("command  ::" + command);

                // runTime = Runtime.getRuntime();
                try {
                    Process process3 = Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // Thread.sleep(1 * 1000);
                System.out.println("ItemImageSync fileUpload loopcount");

                // loopcount = 0;
                fileUpload(props, targetServer);

            } else {
                System.out.println("ItemImageSync fileUpload End");
            }
        }


    }


    public ItemImageSync(Properties props, String targetServer, String testBak) {


        // 운영서버
        String wasFtpIp = props.getProperty("BO3.ftp.ip");
        String wasFtpId = props.getProperty("BO3.ftp.id");
        String wasFtpPw = props.getProperty("BO3.ftp.pw");
        int wasFtpPort = 22;


        String wasTestFtpIp = "";
        String wasTestFtpId = "";
        String wasTestFtpPw = "";
        int wasTestFtpPort = 22;

        if (targetServer.equals("test")) {
            // 테스트서버
            wasTestFtpIp = props.getProperty("FO_TST.ftp.ip");
            wasTestFtpId = props.getProperty("FO_TST.ftp.id");
            wasTestFtpPw = props.getProperty("FO_TST.ftp.pw");
            wasTestFtpPort = 22;
        } else {
            // 개발서버 dev
            wasTestFtpIp = props.getProperty("FO_DEV.ftp.ip");
            wasTestFtpId = props.getProperty("FO_DEV.ftp.id");
            wasTestFtpPw = props.getProperty("FO_DEV.ftp.pw");
            wasTestFtpPort = 22;
        }

        SFTPService sftp = new SFTPService(wasFtpIp, wasFtpPort, wasFtpId, wasFtpPw);
        SFTPService sftpTest = new SFTPService(wasTestFtpIp, wasTestFtpPort, wasTestFtpId, wasTestFtpPw);

        /**
         * 상품이미지 다운로드
         **************************************************/

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        File f = new File(ITEM_IMAGE_LIST);

        if (f.exists()) {

            String str;
            String[] strArr;
            String deployStr; // deployStr

            try {
                is = new FileInputStream(ITEM_IMAGE_LIST);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                String wasPath = "";
                String localPath = "";
                String wasTestPath = "";

                int count = 0;
                while ((str = br.readLine()) != null) {
                    if (str.length() == 10) {
                        String item_image_1 = str.substring(0, 2);
                        String item_image_2 = str.substring(2, 4);
                        String item_image_3 = str.substring(4, 6);
                        String item_image_4 = str.substring(6, 8);
                        String item_image_5 = str.substring(8, 10);

                        String itemImagePath =
                                item_image_1 + "/" + item_image_2 + "/" + item_image_3 + "/" + item_image_4 + "/" + item_image_5 + "/";

                        wasPath = NAS_ITEM_DEFAULT_DIR + itemImagePath;
                        if (targetServer.equals("test")) {
                            // 테스트서버
                            wasTestPath = NAS_ITEM_TEST_DEFAULT_DIR + itemImagePath;
                        } else {
                            // 개발서버
                            wasTestPath = NAS_ITEM_DEV_DEFAULT_DIR + itemImagePath;
                        }
                        // 로컬 디렉토리 생성
                        localPath = LOCAL_DEFAULT_DIR + itemImagePath;
                        makeDiretory(localPath);

                        // System.out.println("wasPath ::" + wasPath);
                        // System.out.println("wasTestPath ::" + wasTestPath);
                        // System.out.println("localPath ::" + localPath);
                        // 운영 이미지 다운로드
                        List<LsEntry> imageFiles = new ArrayList<LsEntry>();
                        imageFiles = sftp.downloadFileImage(localPath, wasPath);

                        if (imageFiles.size() > 0) {
                            for (int i = 0; i < imageFiles.size(); i++) {
                                LsEntry lsEntry = imageFiles.get(i);
                                // System.out.println("lsEntry.getFilename() ::" + lsEntry.getFilename());
                                // 테스트서버에 이미지 업로드
                                localPath = LOCAL_DEFAULT_DIR + itemImagePath + lsEntry.getFilename();
                                // System.out.println("imageFiles localPath::" + localPath);
                                sftpTest.upload(localPath, wasTestPath);
                            }

                        }// if imageFiles.size() > 0
                    }

                    if (count == 50) {
                        String command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy.sh";
                        Runtime runTime = Runtime.getRuntime();
                        Process process = runTime.exec(command);

                        Thread.sleep(3 * 1000);
                    }
                    count++;
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("=======");
            }
        }

    }


    private String imageFiles(int i) {

        // TODO Auto-generated method stub
        return null;
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
            fileName.add(fi.getName());
            // fileName.add(fi.getPath().replace(DEPLOY_FILE_DIR, ""));
        }

        return fileName;
    }

}
