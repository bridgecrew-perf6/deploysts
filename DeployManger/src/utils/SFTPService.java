package utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

@Slf4j
public class SFTPService {

    private String host;
    private Integer port;
    private String user;
    private String password;

    private JSch jsch;
    private Session session;
    private Channel channel;
    private ChannelSftp sftpChannel;
    private ChannelExec channelExec;


    public SFTPService(String host, Integer port, String user, String password) {

        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    public void connect() {

        System.out.println("sftp connecting..." + host);
        try {
            jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

        } catch (JSchException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    public void disconnect() {

        System.out.println("sftp disconnecting...");
        sftpChannel.disconnect();
        channel.disconnect();
        session.disconnect();
    }


    public void upload(String fileName, String remoteDir) {

        FileInputStream fis = null;
        connect();
        try {

            SftpATTRS attrs = null;
            try {
                attrs = sftpChannel.stat(remoteDir);
            } catch (Exception e) {
                System.out.println(remoteDir + " not found");
            }

            if (attrs != null) {
                System.out.println("Directory exists IsDir=" + attrs.isDir());
            } else {
                System.out.println("Creating dir " + remoteDir);
                // sftpChannel.mkdir(remoteDir);
                mkdirDir(sftpChannel, remoteDir);
            }

            // Change to output directory
            sftpChannel.cd(remoteDir);

            // Upload file
            File file = new File(fileName);

            System.out.println("sftp localDir ::" + fileName);
            System.out.println("sftp remoteDir ::" + remoteDir);

            fis = new FileInputStream(file);
            sftpChannel.put(fis, file.getName());

            fis.close();
            System.out.println("=============================================================================");
            System.out.println("File uploaded successfully - " + remoteDir + file.getName());
            System.out.println("=============================================================================");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            disconnect();
            throw new RuntimeException("File uploaded Fail !!!");
        }
        disconnect();
    }


    public void download(String fileName, String localDir, String deployFtpPath) {

        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        connect();
        try {
            // Change to output directory
            String cdDir = deployFtpPath + fileName.substring(0, fileName.lastIndexOf("/") + 1);
            String fileNm = fileName.substring(fileName.lastIndexOf("/") + 1);
            System.out.println("download :: ");
            System.out.println("cdDir :: " + cdDir);
            System.out.println("fileNm :: " + fileNm);
            System.out.println("localDir :: " + localDir);

            sftpChannel.cd(cdDir);

            // File file = new File(fileName);
            bis = new BufferedInputStream(sftpChannel.get(fileNm));

            File newFile = new File(localDir + "/" + fileNm);

            // Download file
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            bis.close();
            bos.close();
            System.out.println("File downloaded successfully - " + newFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            disconnect();
            throw new RuntimeException("File downloaded Fail !!! ");
        }
        disconnect();
    }


    public List downloadFileImage(String localDir, String wasFtpPath) {

        List<LsEntry> imageFiles = new ArrayList<LsEntry>();
        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        connect();
        try {
            System.out.println("downloadFileImage :: ");
            System.out.println("wasFtpPath :: " + wasFtpPath);
            System.out.println("localDir :: " + localDir);

            sftpChannel.cd(wasFtpPath);
            imageFiles = sftpChannel.ls("*.*"); // 모든 파일명을 가진 파일 검색

            for (int i = 0; i < imageFiles.size(); i++) {
                LsEntry lsEntry = imageFiles.get(i);
                // File file = new File(fileName);
                bis = new BufferedInputStream(sftpChannel.get(lsEntry.getFilename()));

                File newFile = new File(localDir + "/" + lsEntry.getFilename());

                // Download file
                OutputStream os = new FileOutputStream(newFile);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                int readCount;
                while ((readCount = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, readCount);
                }

                bis.close();
                bos.close();
                System.out.println("File downloaded successfully - " + newFile);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // disconnect();
            // throw new RuntimeException("File downloaded Fail !!! ");
        }
        disconnect();
        return imageFiles;
    }


    public void downloadByDeploy(String fileName, String localDir, String deployFtpPath) {

        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        connect();
        try {
            // Change to output directory
            String cdDir = deployFtpPath + fileName.substring(0, fileName.lastIndexOf("/") + 1);
            String fileNm = fileName.substring(fileName.lastIndexOf("/") + 1);
            System.out.println("download :: ");
            System.out.println("cdDir :: " + cdDir);
            System.out.println("fileNm :: " + fileNm);
            System.out.println("localDir :: " + localDir);

            sftpChannel.cd(cdDir);

            // File file = new File(fileName);
            bis = new BufferedInputStream(sftpChannel.get(fileNm));

            File newFile = new File(localDir + "/" + fileNm);

            // Download file
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            bis.close();
            bos.close();
            System.out.println("File downloaded successfully - " + newFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("File downloaded fail !! - ");
            // throw new RuntimeException("File downloaded Fail !!! ");
        }
        disconnect();
    }


    public void downloadFile(String fileName, String localDir) {

        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        connect();
        try {
            // Change to output directory
            String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
            sftpChannel.cd(cdDir);

            File file = new File(fileName);
            bis = new BufferedInputStream(sftpChannel.get(file.getName()));

            File newFile = new File(localDir + "/" + file.getName());

            // Download file
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            bis.close();
            bos.close();
            System.out.println("File downloadFile successfully - " + file.getAbsolutePath());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            disconnect();
            throw new RuntimeException("File downloaded Fail !!! ");
        }
        disconnect();
    }



    public void sshConnect() {

        System.out.println("ssh channelExec Connect..." + host);
        try {
            jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            channel = session.openChannel("exec");
            // channel.connect();
            channelExec = (ChannelExec) channel;

        } catch (JSchException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }


    public void sshDisconnectNoLog() {

        channelExec.disconnect();
        channel.disconnect();
        session.disconnect();
    }


    public void sshConnectNoLog() {

        try {
            jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            channel = session.openChannel("exec");
            // channel.connect();
            channelExec = (ChannelExec) channel;

        } catch (JSchException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }


    public void sshDisconnect() {

        System.out.println("ssh channelExec Disconnect...");
        channelExec.disconnect();
        channel.disconnect();
        session.disconnect();
    }


    public void sshCommandExec(String commandExec) {

        sshConnect();
        try {

            channelExec.setCommand(commandExec);

            // 명령어를 실행한다.
            channelExec.connect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            sshDisconnect();
            throw new RuntimeException(e.getMessage());
        }
        sshDisconnect();
    }


    public void sshCommandExecByShellScript(String commandExec) {

        sshConnect();
        try {

            channelExec.setCommand(commandExec);

            // 명령어를 실행한다.
            channelExec.connect();

            InputStream in = channelExec.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            List<String> result = new ArrayList<String>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result.add(line);
            }

            int exitStatus = channelExec.getExitStatus();

            System.out.println("exitStatus ::" + exitStatus);

            if (exitStatus < 0) {
                System.out.println("Done, but exit status not set!");
            } else if (exitStatus > 0) {
                System.out.println("Done, but with error!");
            } else {
                System.out.println("Done!");
            }



        } catch (Exception e) {
            System.out.println(e.getMessage());
            sshDisconnect();
            throw new RuntimeException(e.getMessage());
        }
        sshDisconnect();
    }


    public void sshCommandExecByShellScriptNoLog(String commandExec) {

        sshConnectNoLog();
        try {

            channelExec.setCommand(commandExec);

            // 명령어를 실행한다.
            channelExec.connect();

            InputStream in = channelExec.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            List<String> result = new ArrayList<String>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result.add(line);
            }

            int exitStatus = channelExec.getExitStatus();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            sshDisconnect();
            throw new RuntimeException(e.getMessage());
        }
        sshDisconnectNoLog();
    }


    public void sshCommandExecByNotLogger(String commandExec) {

        sshConnectNoLog();
        try {

            channelExec.setCommand(commandExec);

            // 명령어를 실행한다.
            channelExec.connect();

            InputStream in = channelExec.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            List<String> result = new ArrayList<String>();

            while ((line = reader.readLine()) != null) {
                result.add(line);
            }

            int exitStatus = channelExec.getExitStatus();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            sshDisconnect();
            throw new RuntimeException(e.getMessage());
        }
        sshDisconnectNoLog();
    }


    /**
     * 헤당 경로가 없으면 mkdir 하는 함수 리턴값 : fullpath
     * 
     * @param path
     * @return
     * @throws SftpException
     */

    public String mkdirDir(ChannelSftp sftpChannel, String path) throws SftpException {

        String[] pathArray = path.split("/");
        String currentDirectory = sftpChannel.pwd();

        String totPathArray = "";

        for (String element : pathArray) {
            totPathArray += element + "/";
            String currentPath = currentDirectory + "/" + totPathArray;

            // System.out.println("currentPath ::" + currentDirectory);
            // System.out.println("totPathArray ::" + totPathArray);

            try {
                sftpChannel.cd(currentDirectory);
                sftpChannel.mkdir(totPathArray);
            } catch (Exception e) {
                sftpChannel.cd(currentDirectory);
            }

        }

        return currentDirectory + "/" + totPathArray;

    }

}
