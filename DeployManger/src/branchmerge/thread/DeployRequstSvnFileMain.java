package branchmerge.thread;


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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import redmine.DeployRequest;
import redmine.RedmineClient;
import utils.CryptoUtil;
import utils.DateUtils;
import utils.Properties;
import app.svn.mycommits.MyCommits;
import branchmerge.model.WorkingResource;
import branchmerge.svn.Svn;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

public class DeployRequstSvnFileMain {

    static final String LICENSE_FILE = "/data/home/hisis/ec/tools/deploy/config/LICENSE.deploy";

    private static List resources;
    private String fromBranch;
    private String toBranch;
    private String TagLabel = TagController.makeTagLabel();
    static Properties sp = Properties.getInstance("config.properties");
    private static ArrayList report;
    public static ExcelReportHandler excelHandler = null;
    static String SVN_URL = sp.getProperty("SVN_URL");
    static String SVN_ID = sp.getProperty("SVN_ID");
    static String SVN_PASSWD = sp.getProperty("SVN_PASSWD");
    static String BRANCH_DEV = sp.getProperty("BRANCH_DEV");
    static String BRANCH_TST = sp.getProperty("BRANCH_TST");
    static String BRANCH_REL = sp.getProperty("BRANCH_REL");
    static String BRANCH_TAGS = sp.getProperty("BRANCH_TAGS");
    static int RDM_QUERY_DPR_DEV = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_DEV"));
    static int RDM_QUERY_DPR_TST = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_TST"));
    static int RDM_QUERY_DPR_REL = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_REL"));
    static int RDM_QUERY_DPR_FINISH = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_FINISH"));
    static String STATUS_NEW = Properties.getPropertyUTF8("NEW");
    static String STATUS_SUBMIT = Properties.getPropertyUTF8("SUBMIT");
    static String CHECKED_DEV = Properties.getPropertyUTF8("CHECKED_DEV");
    static String DEPLOYED_DEV = Properties.getPropertyUTF8("DEPLOYED_DEV");
    static String CHECKED_TST = Properties.getPropertyUTF8("CHECKED_TST");
    static String DEPLOYED_REL = Properties.getPropertyUTF8("DEPLOYED_REL");
    static String CHECKED_REL = Properties.getPropertyUTF8("CHECKED_REL");
    int STATUS_DEPLOY_DEV = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_DEV"));
    int STATUS_DEPLOY_TST = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_TST"));
    int STATUS_DEPLOY_REL = Integer.parseInt(Properties.getPropertyUTF8("STATUS_DEPLOY_REL"));
    static String RDM_PRJ_ID = Properties.getPropertyUTF8("RDM_PRJ_ID");
    static String RDM_URL = Properties.getPropertyUTF8("RDM_URL");
    // static String RDM_LOGINID = Properties.getPropertyUTF8("RDM_LOGINID");
    // static String RDM_PWD = Properties.getPropertyUTF8("RDM_PWD");

    static String BRANCH_STAGING = sp.getProperty("BRANCH_STAGING");
    static String BRANCH_STAGING_TAGS = sp.getProperty("BRANCH_STAGING_TAGS");

    static int RDM_QUERY_ALLJOB = Integer.parseInt(sp.getProperty("RDM_QUERY_ALLJOB"));
    static int RDM_QUERY_MYJOB = Integer.parseInt(sp.getProperty("RDM_QUERY_MYJOB"));


    public static void main(String args[]) throws Exception {

        if (!LicenseCheck()) {
            printLicence();
            // return;
        }

        if (ArrayUtils.isEmpty(args)) {
            printUsage();
            return;
        }

        String runType = args[0];
        String rdmLoginId = args[1];
        String rdmLoginPwd = args[2];

        if (StringUtils.isEmpty(rdmLoginId) || StringUtils.isEmpty(rdmLoginPwd)) {
            printUsage();
            return;
        }

        DeployRequstSvnFile deployRequstSvnFile = new DeployRequstSvnFile();
        System.out.println("RDM_URL ::" + RDM_URL);
        System.out.println("runType ::" + runType);
        System.out.println("rdmLoginId ::" + rdmLoginId);
        System.out.println("rdmLoginPwd ::" + rdmLoginPwd);
        IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, rdmLoginId, rdmLoginPwd);

        if (runType.equals("all_job")) {
            // 모든 배포요청

            List rtnList = deployRequstSvnFile.getDeployRequest(issueManger, RDM_PRJ_ID, 9);
            for (int i = 0; i < rtnList.size(); i++) {
                DeployRequest dr = (DeployRequest) rtnList.get(i);

                System.out.println(".getID() ::" + dr.id);
                System.out.println("제목 getSubject ::" + dr.subject);
                System.out.println("상태 getStatusName::" + dr.StatusName);
                System.out.println("개발자 getAuthor ::" + dr.author);
                System.out.println("요청자 requestor ::" + dr.requestor);
                System.out.println("wantedDate ::" + dr.wantedDate);

            }

        } else if (runType.equals("my_job")) {
            // 나의 배포요청
            // List rtnList = deployRequstSvnFile.getWorkingResource(issueManger, RDM_PRJ_ID, RDM_QUERY_MYJOB);
            // for (int i = 0; i < rtnList.size(); i++) {
            // WorkingResource wr = (WorkingResource) rtnList.get(i);
            //
            // System.out.println("wr.getID() ::" + wr.getID());
            // System.out.println("제목 ::" + wr.getTitle());
            // System.out.println("상태 ::" + wr.getStatus());
            // System.out.println("개발자 ::" + wr.getOwner());
            // }

            List rtnList = deployRequstSvnFile.getDeployRequest(issueManger, RDM_PRJ_ID, 1);
            for (int i = 0; i < rtnList.size(); i++) {
                DeployRequest dr = (DeployRequest) rtnList.get(i);

                System.out.println(".getID() ::" + dr.id);
                System.out.println("제목 getSubject ::" + dr.subject);
                System.out.println("상태 getStatusName::" + dr.StatusName);
                System.out.println("개발자 getAuthor ::" + dr.author);
            }

        } else if (runType.equals("item_info")) {
            // 배포요청 정보 By Item
            DeployRequest dr = deployRequstSvnFile.getWorkingResourceByItemId(issueManger, RDM_PRJ_ID, 8828);

            System.out.println(".getID() ::" + dr.id);
            System.out.println("제목 getSubject ::" + dr.subject);
            System.out.println("상태 getStatusName::" + dr.StatusName);
            System.out.println("개발자 getAuthor ::" + dr.author);
            System.out.println("PL ::" + dr.PL);
            System.out.println("requestor ::" + dr.requestor);
            System.out.println("delpoyFileList ::" + dr.delpoyFileList);

            List<String> filesList = new ArrayList<String>();
            String delpoyFileList = dr.delpoyFileList;
            String[] delpoyFileListArr = delpoyFileList.split("\n");
            for (String element : delpoyFileListArr) {
                filesList.add(element);
            }

            for (int i = 0; i < filesList.size(); i++) {
                System.out.println("" + filesList.get(i));
            }


            StringBuffer filesListStr = new StringBuffer();
            for (int i = 0; i < filesList.size(); i++) {
                filesListStr.append(filesList.get(i));
                filesListStr.append("\n");
            }

            System.out.println("filesListStr START ==============");
            System.out.println(filesListStr.toString());
            System.out.println("filesListStr END ==============");



        } else if (runType.equals("del_deploy_file_all")) {
            // 전체삭제

        } else if (runType.equals("del_deploy_file_check")) {
            // 선택삭제
            List<String> filesList = new ArrayList<String>();
            filesList.add("{/05_BO/src/main/java/ehimart/webapp/bo/login/controller/LoginController.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/model/LoginUserSearchParam.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/service/LoginService.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/service/LoginServiceImpl.java : 617172 :  }");

            StringBuffer filesListStr = new StringBuffer();
            for (int i = 0; i < filesList.size(); i++) {

                if ("{/05_BO/src/main/java/ehimart/webapp/bo/login/controller/LoginController.java : 617172 :  }"
                        .contains(filesList.get(i))) {
                    continue;
                } else {
                    filesListStr.append(filesList.get(i));
                    filesListStr.append("\n");
                }
            }



        } else if (runType.equals("update_deploy_file")) {
            // 배포목록 추가

            List<String> filesList = new ArrayList<String>();
            filesList.add("{/05_BO/src/main/java/ehimart/webapp/bo/login/controller/LoginController.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/model/LoginUserSearchParam.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/service/LoginService.java : 617172 :  }");
            filesList.add("{/02_Application/src/main/java/ehimart/app/domain/bo/login/service/LoginServiceImpl.java : 617172 :  }");

            StringBuffer filesListStr = new StringBuffer();
            for (int i = 0; i < filesList.size(); i++) {
                filesListStr.append(filesList.get(i));
                filesListStr.append("\n");
            }

            deployRequstSvnFile.updateDeployFileList2Server(RDM_URL, rdmLoginId, rdmLoginPwd, RDM_PRJ_ID, 8828, filesListStr.toString());


        } else if (runType.equals("get_svn_list")) {
            // SVN 목록

            MyCommits mycommits = new MyCommits(SVN_URL, SVN_ID, SVN_PASSWD);
            List<SVNLogEntry> commitList = mycommits.revisions(3);

            for (int i = 0; i < commitList.size(); i++) {
                System.out.println("getAuthor ::" + commitList.get(i).getAuthor());
                System.out.println("getRevision ::" + commitList.get(i).getRevision());
                System.out.println("getChangedPaths ::" + commitList.get(i).getChangedPaths());
                System.out.println("getMessage ::" + commitList.get(i).getMessage());
            }

        }



    }


    private void runMergeResourceHandler(List units, ArrayList report, String toBranch) {

        MergeResourceHandler irh = new MergeResourceHandler(units, report, toBranch);
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
        List wrs;
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

                    String decStr = CryptoUtil.decryptAES(str, key);
                    int decStrInt = Integer.parseInt(decStr);
                    String todate = DateUtils.getDate("yyyy");

                    int compStrInt = Integer.parseInt(todate);

                    if (decStrInt == compStrInt) {
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

        System.out.println("### Deploy Solution Licence is fired");
    }


    private static void printUsage() {

        System.out.println("### Deploy RunType need argument !!!");
        throw new RuntimeException("Deploy RunType need argument !!!");
    }



}
