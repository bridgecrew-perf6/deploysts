package branchmerge.thread;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;

import redmine.DeployRequest;
import redmine.RedmineClient;
import utils.CryptoUtil;
import utils.DateUtils;
import utils.Properties;
import branchmerge.model.WorkingResource;
import branchmerge.svn.Svn;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public class MergeResouceThreadHandler {

    static final String LICENSE_FILE = "/data/home/hisis/ec/tools/deploy/config/LICENSE.deploy";

    private static List resources;
    private String fromBranch;
    private String toBranch;
    private String TagLabel;
    static Properties sp;
    private static ArrayList report;
    public static ExcelReportHandler excelHandler = null;
    static String SVN_URL;
    static String SVN_ID;
    static String SVN_PASSWD;
    static String BRANCH_DEV;
    static String BRANCH_TST;
    static String BRANCH_REL;
    static String BRANCH_TAGS;
    static int RDM_QUERY_DPR_DEV;
    static int RDM_QUERY_DPR_TST;
    static int RDM_QUERY_DPR_REL;
    static int RDM_QUERY_DPR_FINISH;
    static String STATUS_NEW;
    static String STATUS_SUBMIT;
    static String CHECKED_DEV;
    static String DEPLOYED_DEV;
    static String CHECKED_TST;
    static String DEPLOYED_REL;
    static String CHECKED_REL;
    int STATUS_DEPLOY_DEV;
    int STATUS_DEPLOY_TST;
    int STATUS_DEPLOY_REL;
    static String RDM_PRJ_ID;
    static String RDM_URL;
    static String RDM_LOGINID;
    static String RDM_PWD;

    static String BRANCH_STAGING;
    static String BRANCH_STAGING_TAGS;

    static String BRANCH_REL_BAK;

    static String BRANCH_TEMP_01;
    static String BRANCH_TEMP_01_TAGS;

    static String BRANCH_TEMP_02;
    static String BRANCH_TEMP_02_TAGS;


    public MergeResouceThreadHandler() {

        init();
    }


    public void setPraperties() {

        sp = Properties.getInstance("config.properties");
        SVN_URL = sp.getProperty("SVN_URL");
        SVN_ID = sp.getProperty("SVN_ID");
        SVN_PASSWD = sp.getProperty("SVN_PASSWD");
        RDM_QUERY_DPR_DEV = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_DEV"));
        RDM_QUERY_DPR_TST = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_TST"));
        RDM_QUERY_DPR_REL = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_REL"));
        RDM_QUERY_DPR_FINISH = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_FINISH"));


        STATUS_DEPLOY_DEV = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_DEV"));
        STATUS_DEPLOY_TST = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_TST"));
        STATUS_DEPLOY_REL = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_REL"));

        STATUS_NEW = Properties.getPropertyUTF8("NEW");
        STATUS_SUBMIT = Properties.getPropertyUTF8("SUBMIT");
        CHECKED_DEV = Properties.getPropertyUTF8("CHECKED_DEV");
        DEPLOYED_DEV = Properties.getPropertyUTF8("DEPLOYED_DEV");
        CHECKED_TST = Properties.getPropertyUTF8("CHECKED_TST");
        DEPLOYED_REL = Properties.getPropertyUTF8("DEPLOYED_REL");
        CHECKED_REL = Properties.getPropertyUTF8("CHECKED_REL");
        RDM_PRJ_ID = Properties.getPropertyUTF8("RDM_PRJ_ID");
        RDM_URL = Properties.getPropertyUTF8("RDM_URL");
        RDM_LOGINID = Properties.getPropertyUTF8("RDM_LOGINID");
        RDM_PWD = Properties.getPropertyUTF8("RDM_PWD");
        BRANCH_DEV = sp.getProperty("BRANCH_DEV");
        BRANCH_TST = sp.getProperty("BRANCH_TST");
        BRANCH_REL = sp.getProperty("BRANCH_REL");
        BRANCH_TAGS = sp.getProperty("BRANCH_TAGS");

        BRANCH_STAGING = sp.getProperty("BRANCH_STAGING");
        BRANCH_STAGING_TAGS = sp.getProperty("BRANCH_STAGING_TAGS");

        BRANCH_REL_BAK = " /branches/REL_20181011";

        BRANCH_TEMP_01 = sp.getProperty("BRANCH_TEMP_01");
        BRANCH_TEMP_01_TAGS = sp.getProperty("BRANCH_TEMP_01_TAGS");
        BRANCH_TEMP_02 = sp.getProperty("BRANCH_TEMP_02");
        BRANCH_TEMP_02_TAGS = sp.getProperty("BRANCH_TEMP_02_TAGS");
    }


    private void init() {

        setPraperties();

        report = new ArrayList();
        TagLabel = TagController.makeTagLabel();
    }


    public static void main(String args[]) throws Exception {

        String inputFromBranch = null;

        if (!LicenseCheck()) {
            printLicence();
            return;
        }

        MergeResouceThreadHandler mt = new MergeResouceThreadHandler();
        IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, RDM_LOGINID, RDM_PWD);
        String REQ_STATUS = "";
        String BD_TYPE = null;
        if (ArrayUtils.isEmpty(args)) {
            System.out.println("java tools.jar TST [CR_ID1] [CR_ID1] [CR_ID1] [CR_ID1] [CR_ID1]");
            System.exit(0);
        } else if (args.length == 1) {
            if (args[0].equals("DEV")) {
                resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_DEV);
            } else if (args[0].equals("TST")) {
                resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_TST);
                TagController.syncRELToTST(SVN_URL, SVN_ID, SVN_PASSWD);
            } else if (args[0].equals("REL")) {
                resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_REL);
                System.out.println((new StringBuilder("BRANCH_REL :")).append(BRANCH_REL).toString());
                TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_REL, (new StringBuilder(String.valueOf(BRANCH_TAGS)))
                        .append(mt.TagLabel).toString());

            } else if (args[0].equals("REL_ANYTIME")) {
                resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_REL);
                System.out.println((new StringBuilder("BRANCH_REL :")).append(BRANCH_REL).toString());
                TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_REL, (new StringBuilder(String.valueOf(BRANCH_TAGS)))
                        .append(mt.TagLabel).toString());

            } else if (args[0].equals("STAGING")) {
                // resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_TST);
                // System.out.println((new StringBuilder("BRANCH_STAGING :")).append(BRANCH_STAGING).toString());
                System.out.println("BRANCH_STAGING : INPUT REDMINE ITEM NO!!!");

            } else if (args[0].equals("CDN_PURGING_LIST")) {
                resources = getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_FINISH);
            }
        } else if (args.length > 1) {
            resources = new ArrayList();
            ArrayList CRs = new ArrayList();

            if (args[0].equals("REL_ANYTIME")) {
                for (int i = 3; i < args.length; i++) {
                    if (isParsableToInt(args[i])) {
                        CRs.add(args[i]);
                        resources.addAll(getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, Integer.parseInt(args[i])));
                    }
                }


                String copiedAll = args[1];
                String buildNumber = args[2];
                System.out
                        .println("##################################################################################################################################################################  ");
                System.out.println("소스 리머징 :: 아래URL 클릭 ");
                System.out.println("");
                System.out
                        .println("http://10.154.17.205:8081/view/07.%EC%88%98%EC%8B%9C%EB%B0%B0%ED%8F%AC_Set_01/job/100_Source_Merge_REL_ANYTIME_ReBuild_By_BuildNumber/buildWithParameters?jobName=00_Source_Merge_REL_ANYTIME&build_num="
                                + buildNumber);
                System.out.println("");
                System.out
                        .println("##################################################################################################################################################################  ");

            } else if (args[0].equals("REL_RECOVER")) {

                inputFromBranch = args[1];

                if (isParsableToInt(args[1])) {
                    System.out.println("복구할 Tag브랜치를 입력해 주세요.");
                    System.exit(0);
                }

                System.out.println("inputFromBranch ::" + inputFromBranch);

                for (int i = 2; i < args.length; i++) {
                    if (isParsableToInt(args[i])) {
                        CRs.add(args[i]);
                        resources.addAll(getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, Integer.parseInt(args[i])));
                    }
                }
            } else if (args[0].equals("REL_ANYTIME_REBUILD")) {

            } else if (args[0].equals("REL")) {
                // for (int i = 2; i < args.length; i++) {
                // if (isParsableToInt(args[i])) {
                // CRs.add(args[i]);
                // resources.addAll(getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, Integer.parseInt(args[i])));
                // }
                // }
                //
                // String buildNumber = args[1];
                // System.out
                // .println("##################################################################################################################################################################  ");
                // System.out.println("소스 리머징 :: 아래URL 클릭 ");
                // System.out.println("");
                // System.out
                // .println("http://10.154.17.205:8081/view/07.%EC%88%98%EC%8B%9C%EB%B0%B0%ED%8F%AC_Set_01/job/100_Source_Merge_REL_ANYTIME_ReBuild_By_BuildNumber/buildWithParameters?jobName=00_Source_Merge_REL&build_num="
                // + buildNumber);
                // System.out.println("");
                // System.out
                // .println("##################################################################################################################################################################  ");


                for (int i = 1; i < args.length; i++) {
                    if (isParsableToInt(args[i])) {
                        CRs.add(args[i]);
                        resources.addAll(getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, Integer.parseInt(args[i])));
                    }
                }

            } else {
                for (int i = 1; i < args.length; i++) {
                    if (isParsableToInt(args[i])) {
                        CRs.add(args[i]);
                        resources.addAll(getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, Integer.parseInt(args[i])));
                    }
                }
            }

            if (CRs.size() > 0) {
                if (args[0].equals("REL") || args[0].equals("REL_ANYTIME") || args[0].equals("REL_RECOVER")) {
                    TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_REL,
                            (new StringBuilder(String.valueOf(BRANCH_TAGS))).append(mt.TagLabel).toString());
                } else if (args[0].equals("STAGING")) {
                    TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_STAGING,
                            (new StringBuilder(String.valueOf(BRANCH_STAGING_TAGS))).append(mt.TagLabel).toString());
                } else if (args[0].equals("BRANCH_TEMP_01")) {
                    TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_TEMP_01,
                            (new StringBuilder(String.valueOf(BRANCH_TEMP_01_TAGS))).append(mt.TagLabel).toString());
                } else if (args[0].equals("BRANCH_TEMP_02")) {
                    TagController.makeTagWithLabel(SVN_URL, SVN_ID, SVN_PASSWD, BRANCH_TEMP_02,
                            (new StringBuilder(String.valueOf(BRANCH_TEMP_02_TAGS))).append(mt.TagLabel).toString());
                }
            }


        }


        WorkingResource rw;

        if (args[0].equals("CDN_PURGING_LIST")) {
            List<String> cndFlushList = new ArrayList<String>();

            for (int i = 0; i < resources.size(); i++) {
                rw = (WorkingResource) resources.get(i);
                // System.out.println(rw.getPath());
                cndFlushList.add(rw.getPath());
            }

            cndFlushListMake(cndFlushList);

        } else if (args[0].equals("REL_ANYTIME_REBUILD")) {

            String buildNumber = args[1];
            String jobName = args[2];

            System.out.println("buildNumber ::" + buildNumber);
            System.out.println("jobName ::" + jobName);
            ReMergeFileCreate(buildNumber, jobName);

        } else {
            System.out.println("## \uC18C\uC2A4\uBA38\uC9C0 \uBAA9\uB85D");

            for (int i = 0; i < resources.size(); i++) {
                rw = (WorkingResource) resources.get(i);

                System.out.println((new StringBuilder(String.valueOf(rw.id))).append(" ").append(rw.getPath()).append(" ")
                        .append(rw.getRevision()).append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString());
            }

            // for (Iterator iterator = resources.iterator(); iterator.hasNext(); System.out.println((new
            // StringBuilder(String.valueOf(rw.id)))
            // .append(" ").append(rw.getPath()).append(" ").append(rw.getRevision()).append(" ").append(rw.getStatus()).append(" ")
            // .append(rw.getRevisionStatus()).toString())) {
            // rw = (WorkingResource) iterator.next();
            // }

            System.out.println("\n");
            List units = new ArrayList();
            System.out.println("## \uC18C\uC2A4\uBA38\uC9C0 \uC2DC\uC791");

            for (int row = 0; row < resources.size(); row++) {
                units.add(resources.get(row));
                if (args[0].equals("DEV")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_DEV);
                } else if (args[0].equals("TST")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_TST);
                } else if (args[0].equals("REL") || args[0].equals("REL_ANYTIME")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_REL);
                } else if (args[0].equals("STAGING")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_STAGING);
                } else if (args[0].equals("BRANCH_TEMP_01")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_TEMP_01);
                } else if (args[0].equals("BRANCH_TEMP_02")) {
                    mt.runMergeResourceHandler(units, report, BRANCH_TEMP_02);
                }
                units.clear();
            }

            System.out.println("\n");
            // try {
            // ExcelReportHandler.writeExcelFile(
            // report,
            // (new StringBuilder(String.valueOf(sp.getProperty("REPORT_HOME")))).append("/\uBC30\uD3EC\uACB0\uACFC")
            // .append(mt.TagLabel).append(".xls").toString());
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            System.out.println("## \uC18C\uC2A4\uBA38\uC9C0 \uACB0\uACFC \uCD9C\uB825 ");
            // WorkingResource rw;
            // for (Iterator iterator1 = resources.iterator(); iterator1.hasNext(); System.out.println((new
            // StringBuilder(String.valueOf(rw.id)))
            // .append(" ").append(rw.getPath()).append(" ").append(rw.getRevision()).append(" ").append(rw.getStatus()).append(" ")
            // .append(rw.getRevisionStatus()).toString())) {
            // rw = (WorkingResource) iterator1.next();
            // }

            String sb = "";
            for (int i = 0; i < resources.size(); i++) {
                rw = (WorkingResource) resources.get(i);

                sb =
                        new StringBuilder(String.valueOf(rw.id)).append(" ").append(rw.getPath()).append(" ").append(rw.getRevision())
                                .append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString();

                System.out.println("" + sb);

                // System.out.println((new StringBuilder(String.valueOf(rw.id))).append(" ").append(rw.getPath()).append(" ")
                // .append(rw.getRevision()).append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString());
            }

            if (args[0].equals("REL_ANYTIME")) {
                try {
                    String copiedAll = args[1];
                    System.out.println("copiedAll :: " + copiedAll);
                    // deploy.txt
                    MergeFileCreate(resources, copiedAll, "REL_ANYTIME");

                    // APPEND : deploy.all.txt
                    MergeFileAppend(resources, copiedAll);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (args[0].equals("REL_RECOVER")) {

                for (int i = 0; i < resources.size(); i++) {
                    rw = (WorkingResource) resources.get(i);
                    System.out.println("rw.getPath() ::" + rw.getPath());
                    try {
                        TagController.syncFromTo(SVN_URL, SVN_ID, SVN_PASSWD, inputFromBranch + "/" + rw.getPath(),
                                BRANCH_REL + "/" + rw.getPath());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        resources.remove(i);
                        System.out.println("PASS");
                    }
                }
                //
                // try {
                // for (int i = 0; i < resources.size(); i++) {
                // rw = (WorkingResource) resources.get(i);
                // System.out.println("rw.getPath() ::" + rw.getPath());
                // TagController.syncFromTo(SVN_URL, SVN_ID, SVN_PASSWD, inputFromBranch + "/" + rw.getPath(),
                // BRANCH_REL + "/" + rw.getPath());
                // }
                // } catch (SVNException e) {
                // e.printStackTrace();
                // }

                // deploy.txt
                MergeFileCreate(resources, "Y", "REL_ANYTIME");

                // APPEND : deploy.all.txt
                MergeFileAppend(resources, "Y");

            } else if (args[0].equals("STAGING")) {
                try {
                    String copiedAll = args[1];
                    System.out.println("copiedAll :: " + copiedAll);
                    // deploy_staging.txt
                    MergeFileCreate(resources, copiedAll, "STAGING");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            System.out.println("## \uC5C5\uB370\uC774\uD2B8 Server");
            mt.updateReportToServer(RDM_URL, RDM_LOGINID, RDM_PWD, RDM_PRJ_ID, args[0], resources, mt.TagLabel);

        }// CDN_PURGING_LIST check_if_end
    }


    private void runMergeResourceHandler(List units, ArrayList report, String toBranch) {

        MergeResourceHandler irh = new MergeResourceHandler(units, report, toBranch);
        Thread tr = new Thread(irh);
        tr.start();
        for (; tr.isAlive(); wait(100)) {
            ;
        }
    }


    private void runMergeResourceHandler(List units, ArrayList report, String toBranch, String fromBranch) {

        MergeResourceHandler irh = new MergeResourceHandler(units, report, toBranch, fromBranch);
        Thread tr = new Thread(irh);
        tr.start();
        for (; tr.isAlive(); wait(100)) {
            ;
        }
    }


    private void runMergeResourceHandler(List units, ArrayList report, String toBranch, String fromBranch, boolean isChcekRevision) {

        MergeResourceHandler irh = new MergeResourceHandler(units, report, toBranch, fromBranch);
        Thread tr = new Thread(irh);
        tr.start();
        for (; tr.isAlive(); wait(100)) {
            ;
        }
    }


    private static void wait(int internal) {

        try {
            Thread.sleep(internal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static List getWorkingResource(IssueManager redmineIssueManager, String projectId, int queryId) {

        List deploReqs = getDeployRequest(redmineIssueManager, projectId, queryId);
        List returnValue = new ArrayList();
        List wrs = new ArrayList();
        for (Iterator iterator = deploReqs.iterator(); iterator.hasNext(); returnValue.addAll(wrs)) {
            DeployRequest request = (DeployRequest) iterator.next();
            wrs = RedmineClient.getWorkingResource(request);
        }

        return returnValue;
    }


    public static List getWorkingResourceByItemId(IssueManager redmineIssueManager, String projectId, int itemId) {

        DeployRequest req = null;
        try {
            req = RedmineClient.getDeployRequestByItemId(redmineIssueManager, projectId, itemId);
        } catch (RedmineException e) {
            e.printStackTrace();
        }

        List returnValue = RedmineClient.getWorkingResource(req);
        return returnValue;
    }


    private static List getDeployRequest(IssueManager redmineIssueManager, String projectId, int queryId) {

        List list = null;
        try {
            list = RedmineClient.getDeployRequests(redmineIssueManager, projectId, queryId);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List checkWorkingResource(String branch, String toBranch, List resources, String reportPath) throws SVNException {

        Svn svn = new Svn(sp.getProperty("SVN_URL"), sp.getProperty("SVN_ID"), sp.getProperty("SVN_PASSWD"));
        CheckResources cr = new CheckResources(branch, toBranch, svn);
        WorkingResource wr;
        for (Iterator iterator = resources.iterator(); iterator.hasNext(); cr.check(wr)) {
            wr = (WorkingResource) iterator.next();
        }

        return resources;
    }


    static boolean isParsableToInt(String i) {

        try {
            Integer.parseInt(i);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    private String updateReportToServer(String url, String id, String password, String projectId, String Target, List report,
            String tagLabel) {

        Collections.sort(report);
        List cr = new ArrayList();
        for (int row = 0; row < report.size(); row++) {
            cr.add(report.get(row));
            if (row + 1 == report.size()
                    || !((WorkingResource) report.get(row)).getID().equals(((WorkingResource) report.get(row + 1)).getID())) {
                try {
                    Issue issue =
                            RedmineClient
                                    .getIssueByItemId(url, id, password, projectId, Integer.parseInt(((WorkingResource) cr.get(0)).id));
                    DeployRequest req =
                            RedmineClient.getDeployRequestByItemId(url, id, password, projectId,
                                    Integer.parseInt(((WorkingResource) cr.get(0)).id));
                    String beforeLog = req.mergeLog;
                    beforeLog = beforeLog.replace(" \n[", "[");
                    String log =
                            (new StringBuilder(String.valueOf(beforeLog))).append("[").append(Target).append("_").append(tagLabel)
                                    .append("]\n").toString();
                    for (Iterator iterator = cr.iterator(); iterator.hasNext();) {
                        WorkingResource wr = (WorkingResource) iterator.next();
                        String logMessage =
                                (new StringBuilder(String.valueOf(wr.getStatus()))).append(" , ").append(wr.getPath()).append(" , ")
                                        .append(wr.getRevision()).toString();
                        log = (new StringBuilder(String.valueOf(log))).append("{").append(logMessage).append("}\n").toString();
                    }

                    log = (new StringBuilder(String.valueOf(log))).append("\n").toString();
                    String mergeStatus = issue.getStatusName();
                    if (Target.contains("DEV")) {
                        if (!mergeStatus.contains("DEV")) {
                            if (mergeStatus.equals("")) {
                                mergeStatus = ";#DEV;#";
                            } else if (mergeStatus.contains("TST") && mergeStatus.contains("REL")) {
                                mergeStatus = ";#DEV;#TST;#REL;#";
                            } else if (mergeStatus.contains("TST") && !mergeStatus.contains("REL")) {
                                mergeStatus = ";#DEV;#TST;#";
                            }
                        }
                    } else if (Target.contains("TST")) {
                        if (!mergeStatus.contains("TST")) {
                            if (mergeStatus.equals("")) {
                                mergeStatus = ";#TST;#";
                            } else if (mergeStatus.contains("DEV") && mergeStatus.contains("REL")) {
                                mergeStatus = ";#DEV;#TST;#REL;#";
                            } else if (mergeStatus.contains("DEV") && !mergeStatus.contains("REL")) {
                                mergeStatus = ";#DEV;#TST;#";
                            }
                        }
                    } else if (Target.contains("STAGING")) {
                        if (!mergeStatus.contains("TST")) {
                            if (mergeStatus.equals("")) {
                                mergeStatus = ";#TST;#";
                            } else if (mergeStatus.contains("DEV") && mergeStatus.contains("BRANCH_STAGING")) {
                                mergeStatus = ";#DEV;#TST;#BRANCH_STAGING;#";
                            } else if (mergeStatus.contains("DEV") && !mergeStatus.contains("BRANCH_STAGING")) {
                                mergeStatus = ";#DEV;#TST;#";
                            }
                        }
                    } else if (Target.contains("REL")) {
                        if (mergeStatus.equals("")) {
                            mergeStatus = ";#REL;#";
                        } else if (mergeStatus.contains("DEV") && mergeStatus.contains("TST")) {
                            mergeStatus = ";#DEV;#TST;#REL;#";
                        } else if (mergeStatus.contains("DEV") && !mergeStatus.contains("TST")) {
                            mergeStatus = ";#DEV;#REL;#";
                        } else if (!mergeStatus.contains("DEV") && mergeStatus.contains("TST")) {
                            mergeStatus = ";#TST;#REL;#";
                        }
                    }
                    String _temp = issue.getStatusName();
                    // System.out.println("_temp ::" + _temp);
                    // System.out.println("Target ::" + Target);
                    // System.out.println("CHECKED_REL ::" + CHECKED_REL);

                    if (StringUtils.isEmpty(_temp)) {
                        _temp = "";
                    }
                    int statusId = issue.getStatusId().intValue();
                    if (Target.contains("DEV")) {
                        if (!_temp.contains(CHECKED_DEV)) {
                            statusId = STATUS_DEPLOY_DEV;
                        }
                    } else if (Target.contains("TST")) {
                        if (_temp.contains(STATUS_NEW)) {
                            statusId = STATUS_DEPLOY_TST;
                        }
                    } else if (Target.contains("REL")) {
                        if (!_temp.contains(CHECKED_REL)) {
                            statusId = STATUS_DEPLOY_REL;
                        }
                    }

                    RedmineClient.setCustomFieldInIssue(issue, Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGELOG"), log);
                    issue.setStatusId(Integer.valueOf(statusId));
                    IssueManager issueManager = RedmineClient.getIssueManager(url, id, password);
                    RedmineClient.updateIssue(issueManager, issue);
                } catch (RedmineException e) {
                    e.printStackTrace();
                }
                cr.clear();
            }
        }

        return null;
    }


    private static void ReMergeFileCreate(String buildNumber, String jobName) throws IOException {

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        FileWriter fw = null;
        BufferedWriter bw = null;

        String mergingLogFulFilelPath = "/data/home/hisis/.hudson/jobs/" + jobName + "/builds/";

        String jenkinsLogPath = mergingLogFulFilelPath + buildNumber + "/log";

        try {
            is = new FileInputStream(jenkinsLogPath);
            reader = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(reader);

            String str;
            List<String> resources = new ArrayList<String>();

            while ((str = br.readLine()) != null) {
                if (str.contains("Copied OK")) {
                    resources.add(str);
                }
            }

            for (int i = 0; i < resources.size(); i++) {
                System.out.println(resources.get(i));
            }

            if (resources.size() > 0) {
                String command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy_item.sh";
                String deployFile = "/data/home/hisis/ec/tools/deploy/deploy.txt";

                commandExec(command);

                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                fw = new FileWriter(deployFile);
                bw = new BufferedWriter(fw);

                for (int i = 0; i < resources.size(); i++) {
                    bw.write(resources.get(i) + "\n");
                    bw.flush();
                }


                String deployFileAll = "/data/home/hisis/ec/tools/deploy/deploy.all.txt";

                fw = new FileWriter(deployFileAll, true);
                bw = new BufferedWriter(fw);

                for (int i = 0; i < resources.size(); i++) {
                    bw.write(resources.get(i) + "\n");
                    bw.flush();
                }
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
    }


    private static void MergeFileCreate(List resources, String copiedAll, String MerginType) throws IOException {

        FileWriter fw = null;
        BufferedWriter bw = null;

        List<String> buildList = new ArrayList<String>();

        WorkingResource rw = null;
        String sb = "";
        for (int i = 0; i < resources.size(); i++) {
            rw = (WorkingResource) resources.get(i);
            sb =
                    new StringBuilder(String.valueOf(rw.id)).append(" ").append(rw.getPath()).append(" ").append(rw.getRevision())
                            .append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString();

            if (copiedAll.equals("Y")) {
                buildList.add(sb);
            } else {
                // Copied 된 소스만 빌드처리
                if (sb.contains("Copied")) {
                    buildList.add(sb);
                }
            }

        }

        if (buildList.size() > 0) {

            try {

                String command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy_item.sh";
                String deployFile = "/data/home/hisis/ec/tools/deploy/deploy.txt";
                // deployFile = "C:\\mergetool\\deploy.txt";

                if (MerginType.equals("REL_ANYTIME")) {
                    command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy_item.sh";
                    deployFile = "/data/home/hisis/ec/tools/deploy/deploy.txt";
                } else if (MerginType.equals("STAGING")) {
                    command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy_staging.sh";
                    deployFile = "/data/home/hisis/ec/tools/deploy/deploy_staging.txt";
                }

                commandExec(command);

                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                fw = new FileWriter(deployFile);
                bw = new BufferedWriter(fw);

                for (int i = 0; i < buildList.size(); i++) {
                    bw.write(buildList.get(i) + "\n");
                    bw.flush();
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    fw.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        }// buildList.size() check if_end

    }


    private static void MergeFileCreate(List resources, String copiedAll) throws IOException {

        FileWriter fw = null;
        BufferedWriter bw = null;

        List<String> buildList = new ArrayList<String>();

        WorkingResource rw = null;
        String sb = "";
        for (int i = 0; i < resources.size(); i++) {
            rw = (WorkingResource) resources.get(i);
            sb =
                    new StringBuilder(String.valueOf(rw.id)).append(" ").append(rw.getPath()).append(" ").append(rw.getRevision())
                            .append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString();

            if (copiedAll.equals("Y")) {
                buildList.add(sb);
            } else {
                // Copied 된 소스만 빌드처리
                if (sb.contains("Copied")) {
                    buildList.add(sb);
                }
            }

        }

        if (buildList.size() > 0) {

            try {

                String command = "sh /data/home/hisis/ec/tools/deploy/bin/clean_deploy_item.sh";
                commandExec(command);

                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String deployFile = "/data/home/hisis/ec/tools/deploy/deploy.txt";
                // deployFile = "C:\\mergetool\\deploy.txt";

                fw = new FileWriter(deployFile);
                bw = new BufferedWriter(fw);

                for (int i = 0; i < buildList.size(); i++) {
                    bw.write(buildList.get(i) + "\n");
                    bw.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    fw.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        }// buildList.size() check if_end

    }


    private static void MergeFileAppend(List resources, String copiedAll) throws IOException {

        FileWriter fw = null;
        BufferedWriter bw = null;
        List<String> buildList = new ArrayList<String>();

        WorkingResource rw = null;
        String sb = "";
        for (int i = 0; i < resources.size(); i++) {
            rw = (WorkingResource) resources.get(i);
            sb =
                    new StringBuilder(String.valueOf(rw.id)).append(" ").append(rw.getPath()).append(" ").append(rw.getRevision())
                            .append(" ").append(rw.getStatus()).append(" ").append(rw.getRevisionStatus()).toString();

            if (copiedAll.equals("Y")) {
                buildList.add(sb);
            } else {
                // Copied 된 소스만 빌드처리
                if (sb.contains("Copied")) {
                    buildList.add(sb);
                }
            }

        }


        if (buildList.size() > 0) {
            try {

                String deployFile = "/data/home/hisis/ec/tools/deploy/deploy.all.txt";
                // deployFile = "C:\\mergetool\\deploy.all.txt";

                fw = new FileWriter(deployFile, true);
                bw = new BufferedWriter(fw);

                for (int i = 0; i < buildList.size(); i++) {
                    bw.write(buildList.get(i) + "\n");
                    bw.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    fw.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }

        }// buildList.size() check if_end

    }


    private static void commandExec(String command) {

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    private static void cndFlushListMake(List<String> cndFlushList) {

        List<String> newCdnFlushList = null;
        HashSet<String> listSet = null;
        List<String> processedList = null;

        List<String> foStrList = null;
        List<String> moStrList = null;
        List<String> toStrList = null;

        String foFlushStr = null;
        String moFlushStr = null;
        String toFlushStr = null;

        String fulshStr;

        newCdnFlushList = new ArrayList<String>();

        for (int i = 0; i < cndFlushList.size(); i++) {
            fulshStr = cndFlushList.get(i);
            fulshStr = fulshStr.replace("/trunk", "");
            fulshStr = fulshStr.replace("{", "");
            fulshStr = fulshStr.replace("\"", "");
            fulshStr = fulshStr.trim();

            // System.out.println("fulshStr :::" + fulshStr);

            if (fileExtCheck(fulshStr)) {
                newCdnFlushList.add(fulshStr);
            }

        }


        listSet = new HashSet<String>(newCdnFlushList);
        processedList = new ArrayList<String>(listSet);


        foStrList = new ArrayList<String>();
        moStrList = new ArrayList<String>();
        toStrList = new ArrayList<String>();

        List<String> foFullStrList = new ArrayList<String>();
        List<String> moFullStrList = new ArrayList<String>();

        for (int i = 0; i < processedList.size(); i++) {
            // System.out.println(processedList.get(i));

            if (processedList.get(i).contains("/03_Front/WebContent")) {
                foFlushStr = processedList.get(i);
                foFullStrList.add(foFlushStr);
                foFlushStr = foFlushStr.replace("/03_Front/WebContent", "");
                foStrList.add(foFlushStr);
            } else if (processedList.get(i).contains("/04_FrontMobile/WebContent")) {
                moFlushStr = processedList.get(i);
                moFullStrList.add(moFlushStr);
                moFlushStr = moFlushStr.replace("/04_FrontMobile/WebContent", "");
                moStrList.add(moFlushStr);
            } else if (processedList.get(i).contains("/09_Tablet/WebContent")) {
                toFlushStr = processedList.get(i);
                toFlushStr = toFlushStr.replace("/09_Tablet/WebContent", "");
                toStrList.add(toFlushStr);
            } else {
                // System.out.println("기타 시스템 : " + processedList.get(i));
            }

        }

        System.out.println("");
        System.out.println("");
        System.out.println("=============================================== ");
        System.out.println("MO FlushList Start : " + moStrList.size());
        System.out.println("=============================================== ");
        for (int i = 0; i < moStrList.size(); i++) {
            System.out.println(moStrList.get(i));
        }
        System.out.println("=============================================== ");
        System.out.println("MO FlushList END");
        System.out.println("=============================================== ");
        System.out.println("");
        System.out.println("");
        System.out.println("=============================================== ");
        System.out.println("FO FlushList Start : " + foStrList.size());
        System.out.println("=============================================== ");
        for (int i = 0; i < foStrList.size(); i++) {
            System.out.println(foStrList.get(i));
        }
        System.out.println("=============================================== ");
        System.out.println("FO FlushList END");
        System.out.println("=============================================== ");

        System.out.println("");
        System.out.println("");
        System.out.println("=============================================== ");
        System.out.println("TO FlushList Start : " + toStrList.size());
        System.out.println("=============================================== ");
        for (int i = 0; i < toStrList.size(); i++) {
            System.out.println(toStrList.get(i));
        }
        System.out.println("=============================================== ");
        System.out.println("TO FlushList END");
        System.out.println("=============================================== ");


        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("=============================================== ");
        System.out.println("FULL Path FlushList Start : " + moStrList.size());
        System.out.println("=============================================== ");
        for (int i = 0; i < moFullStrList.size(); i++) {
            System.out.println("{" + moFullStrList.get(i) + " : 000000 :  }");
        }
        for (int i = 0; i < foFullStrList.size(); i++) {
            System.out.println("{" + foFullStrList.get(i) + " : 000000 :  }");
        }
        System.out.println("=============================================== ");
        System.out.println("FULL Path FlushList END");
        System.out.println("=============================================== ");
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

        String[] CHECK_FILE_NAME = {"jpg", "png", "gif", "jpeg", "js", "css", "html", "ttf", "eot", "woff", "woff2", "svg"};

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
    public static boolean LicenseCheck() throws Exception {

        boolean isLicense = false;

        File f = new File(LICENSE_FILE);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        // System.out.println("LICENSE_FILE:: " + LICENSE_FILE);

        if (f.exists()) {

            try {
                is = new FileInputStream(LICENSE_FILE);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                String key = "10.154.17.205_vEC-WTB_SUB";

                str = br.readLine();

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

                } else {
                    isLicense = false;
                }

                System.out.println("isLicense:::" + isLicense);

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


    private static void printLicence() {

        System.out.println("### 라이센스가 만료되었습니다.");
    }



}
