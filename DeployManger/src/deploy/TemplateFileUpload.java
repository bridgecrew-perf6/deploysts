package deploy;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import redmine.RedmineClient;
import utils.HttpDownloader;
import utils.SFTPService;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.Issue;

public class TemplateFileUpload {

    static final String CONFIG_PROPERTIES = "/data/home/hisis/ec/tools/deploy/config/config.properties";
    static final String DEPLOY_TEMP_DIR = "/data/home/hisis/ec/tools/deploy/temp/";
    static final String NAS_DIR_REL = "/NAS-EC_PRD/files/";
    static final String NAS_DIR_TST = "/NAS-EC_DEV/files/";
    static String RDM_PRJ_ID;
    static String RDM_URL;
    static String RDM_LOGINID;
    static String RDM_PWD;


    public TemplateFileUpload(Properties props, String targetServer, int itemId) {

        List resources;

        Properties configProps = loadConfig(CONFIG_PROPERTIES);

        RDM_PRJ_ID = configProps.getProperty("RDM_PRJ_ID");
        RDM_URL = configProps.getProperty("RDM_URL");
        RDM_LOGINID = configProps.getProperty("RDM_LOGINID");
        RDM_PWD = configProps.getProperty("RDM_PWD");

        IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, RDM_LOGINID, RDM_PWD);

        templateFileUploadByItem(props, issueManger, RDM_PRJ_ID, itemId, targetServer);


    }


    public void templateFileUploadByItem(Properties props, IssueManager redmineIssueManager, String projectId, int itemId,
            String targetServer) {

        int deployFtpPort = 22;
        String deployFtpIp = "";
        String deployFtpId = "";
        String deployFtpPw = "";
        String deployFtpPath = "";

        String uploadPath = "";
        String remotePath = "";
        String nas_name = "";

        try {

            Issue issue = getIssueByItemId(redmineIssueManager, projectId, itemId);

            String author = issue.getAuthor().getFullName();
            // String author = issue.getAuthorName();
            String description = issue.getDescription();
            String subject = issue.getSubject();
            uploadPath = issue.getCustomField("템플릿파일경로");
            // if (issue.getCustomFieldByName("템플릿파일경로") != null) {
            // uploadPath = issue.getCustomFieldByName("템플릿파일경로").getValue();
            // }



            String targetSystem = "";
            if (targetServer.equals("dev")) {
                targetSystem = "FO_DEV";
                nas_name = NAS_DIR_TST;
            } else if (targetServer.equals("test")) {
                targetSystem = "FO_TST";
                nas_name = NAS_DIR_TST;
            } else if (targetServer.equals("real")) {
                targetSystem = "MO4";
                nas_name = NAS_DIR_REL;
            }

            uploadPath = uploadPath.replaceAll(nas_name, "");
            remotePath = nas_name + uploadPath;


            deployFtpIp = props.getProperty(targetSystem + ".ftp.ip");
            deployFtpId = props.getProperty(targetSystem + ".ftp.id");
            deployFtpPw = props.getProperty(targetSystem + ".ftp.pw");
            deployFtpPath = props.getProperty(targetSystem + ".ftp.path");

            SFTPService ftp = new SFTPService(deployFtpIp, deployFtpPort, deployFtpId, deployFtpPw);



            String fileName = "";
            String contentURL = "";
            String contentType = "";
            final List<Attachment> uploads = new ArrayList<Attachment>();
            for (Attachment attachment : issue.getAttachments()) {
                fileName = attachment.getFileName();
                contentURL = attachment.getContentURL();
                HttpDownloader.download(contentURL, DEPLOY_TEMP_DIR + fileName);

                ftp.upload(DEPLOY_TEMP_DIR + fileName, remotePath);

                System.out.println("############################################");
                System.out.println("fileName ::" + fileName);
                System.out.println("contentURL ::" + contentURL);
                System.out.println("############################################");

            }

            System.out.println("author ::" + author);
            System.out.println("subject ::" + subject);
            System.out.println("uploadPath ::" + uploadPath);

        } catch (RedmineException e) {
            e.printStackTrace();
        }
    }


    public static Issue getIssueByItemId(IssueManager issueManger, String projectId, int itemId) throws RedmineException {

        return issueManger.getIssueById(Integer.valueOf(itemId), Include.attachments);
    }


    static boolean isParsableToInt(String i) {

        try {
            Integer.parseInt(i);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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


}
