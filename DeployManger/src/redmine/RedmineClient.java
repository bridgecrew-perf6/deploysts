package redmine;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import utils.Properties;
import utils.StringUtils;
import branchmerge.model.Resource;
import branchmerge.model.WorkingResource;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.UserManager;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

public class RedmineClient {

    static Properties sp = Properties.getInstance("config.properties");
    static UserManager userMgr = null;


    public RedmineClient() {

    }


    private static List getIssues(String uri, String id, String password, String projectId, int queryId) throws RedmineException {

        IssueManager issueManager = getIssueManager(uri, id, password);
        List issues = issueManager.getIssues(projectId, Integer.valueOf(queryId), new Include[0]);
        return issues;
    }


    public static List getProjects(String uri, String id, String password) throws RedmineException {

        ProjectManager pm = createRedmineManager(uri, id, password).getProjectManager();
        List projects = pm.getProjects();
        return projects;
    }


    public static List getDeployRequests(String uri, String id, String password, String projectId, int queryId) throws RedmineException {

        List requests = new ArrayList();
        List issues = getIssueManager(uri, id, password).getIssues(projectId, Integer.valueOf(queryId), new Include[0]);
        DeployRequest request;

        ArrayList<Integer> noBoReqeustNoList = new ArrayList<Integer>();

        for (Iterator iterator = issues.iterator(); iterator.hasNext();) {
            Issue issue = (Issue) iterator.next();
            request = new DeployRequest(issue);

            // System.out.println("request.id :: " + request.id);
            // System.out.println("request.boRequestNo :: " + request.boRequestNo);
            if (StringUtils.isEmpty(request.boRequestNo)) {
                noBoReqeustNoList.add(request.id);
            } else {
                requests.add(request);
            }
        }

        ArrayList<Integer> noBoReqeustNoListArr = new ArrayList<Integer>();
        for (int i = 0; i < noBoReqeustNoList.size(); i++) {
            if (!noBoReqeustNoListArr.contains(noBoReqeustNoList.get(i))) {
                noBoReqeustNoListArr.add(noBoReqeustNoList.get(i));
            }
        }

        if (noBoReqeustNoListArr.size() > 0) {
            System.out.println("########################################### ");
            System.out.println("#아래 일감은 BO요청번호가 없는 일감입니다.");
            System.out.println("########################################### ");

            for (int i = 0; i < noBoReqeustNoListArr.size(); i++) {
                System.out.println("#" + noBoReqeustNoListArr.get(i));
            }

            System.out.println("########################################### ");
        }

        return requests;
    }


    public static List getDeployRequests(IssueManager issueManager, String projectId, int queryId) throws RedmineException {

        if (issueManager == null) {
            return null;
        }
        List requests = new ArrayList();

        List issues = issueManager.getIssues(projectId, Integer.valueOf(queryId), new Include[0]);
        DeployRequest request;

        ArrayList<Integer> noBoReqeustNoList = new ArrayList<Integer>();

        for (Iterator iterator = issues.iterator(); iterator.hasNext();) {
            Issue issue = (Issue) iterator.next();
            request = new DeployRequest(issue);

            // System.out.println("request.id :: " + request.id);
            // System.out.println("request.boRequestNo :: " + request.boRequestNo);
            if (StringUtils.isEmpty(request.boRequestNo)) {
                noBoReqeustNoList.add(request.id);
            }

            requests.add(request);
        }

        ArrayList<Integer> noBoReqeustNoListArr = new ArrayList<Integer>();
        for (int i = 0; i < noBoReqeustNoList.size(); i++) {
            if (!noBoReqeustNoListArr.contains(noBoReqeustNoList.get(i))) {
                noBoReqeustNoListArr.add(noBoReqeustNoList.get(i));
            }
        }

        if (noBoReqeustNoListArr.size() > 0) {
            System.out.println("########################################### ");
            System.out.println("#아래 일감은 BO요청번호가 없는 일감입니다.");
            System.out.println("########################################### ");

            for (int i = 0; i < noBoReqeustNoListArr.size(); i++) {
                System.out.println("#" + noBoReqeustNoListArr.get(i));
            }

            System.out.println("########################################### ");
        }

        return requests;
    }


    public static Issue getIssueByItemId(String uri, String id, String password, String projectId, int itemId) throws RedmineException {

        Issue issue = getIssueManager(uri, id, password).getIssueById(Integer.valueOf(itemId), new Include[0]);
        return issue;
    }


    public static Issue getIssueByItemId(IssueManager issueManger, String projectId, int itemId) throws RedmineException {

        return issueManger.getIssueById(Integer.valueOf(itemId), new Include[0]);
    }


    public static DeployRequest getDeployRequestByItemId(String uri, String id, String password, String projectId, int itemId)
            throws RedmineException {

        Issue issue = getIssueByItemId(uri, id, password, projectId, itemId);
        return getDeployRequestFromIssue(issue);
    }


    public static DeployRequest getDeployRequestByItemId(IssueManager issueManger, String projectId, int itemId) throws RedmineException {

        Issue issue = getIssueByItemId(issueManger, projectId, itemId);
        return getDeployRequestFromIssue(issue);
    }


    private static DeployRequest getDeployRequestFromIssue(Issue item) {

        DeployRequest deployRequest = new DeployRequest(item);
        // System.out.println("request.id :: " + deployRequest.id);
        // System.out.println("request.boRequestNo :: " + deployRequest.boRequestNo);
        if (StringUtils.isEmpty(deployRequest.boRequestNo)) {
            System.out.println("###################################################### ");
            System.out.println("# BO업무번호가 없는 일감입니다. :: #" + deployRequest.id);
            System.out.println("###################################################### ");
            // deployRequest = null;
        }
        return deployRequest;
    }


    public static void updateDeployFileList2Server(String uri, String id, String password, String projectId, int itemId, String filesList)
            throws RedmineException {

        IssueManager issueManager = getIssueManager(uri, id, password);
        updateDeployFileList2Server(issueManager, itemId, filesList);
    }


    private static void updateDeployFileList2Server(IssueManager issueManager, int itemId, String filesList) throws RedmineException {

        Issue issue1 = issueManager.getIssueById(Integer.valueOf(itemId), new Include[0]);
        Collection customFieldsList = issue1.getCustomFields();
        String temp = Properties.getPropertyUTF8("RDM_CUSTOMFILDE_DEPLOYFILE");
        setCustomFieldInIssue(issue1, temp, filesList);
        issueManager.update(issue1);
    }


    public static void updateMergeLog2Server(IssueManager issueManager, int itemId, String mergeLog) throws RedmineException {

        Issue issue1 = issueManager.getIssueById(Integer.valueOf(itemId), new Include[0]);
        Collection customFieldsList = issue1.getCustomFields();
        setCustomFieldInIssue(issue1, Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGELOG"), mergeLog);
        issueManager.update(issue1);
    }


    public static Issue setCustomFieldInIssue(Issue issue, String fieldName, String value) {

        Collection customFieldsList = issue.getCustomFields();
        for (Iterator iterator = customFieldsList.iterator(); iterator.hasNext();) {
            CustomField customField = (CustomField) iterator.next();
            if (customField.getName().equalsIgnoreCase(fieldName)) {
                customField.setValue(value);
            }
        }

        return issue;
    }


    public static void updateIssue(IssueManager issueManager, Issue item) throws RedmineException {

        issueManager.update(item);
    }


    public static List getWorkingResource(DeployRequest req) {

        List returnValue = new ArrayList();
        String files = req.delpoyFileList;
        files = files.replace("{", "");
        files.replace("\n", "");
        String entrys[] = files.split("}");
        for (String entry : entrys) {
            String temp = entry;
            temp = temp.replace("/trunk", "");
            String tokens[] = temp.split(":");
            if (tokens.length == 3 && !tokens[2].contains("[DIRECTORY]")) {
                String path = StringUtils.strip(tokens[0]);

                if (path.indexOf(".") > 0) {

                    if (StringUtils.spaceCheck(path)) {
                        System.out.println("##########################################");
                        System.out.println("# 공백이 들어가 있는 파일입니다.");
                        System.out.println("" + path);
                        System.out.println("##########################################");

                        throw new RuntimeException("공백이 들어가 있는 파일이 있습니다 확인하시기 바랍니다. !!!");

                    } else {

                        String revision = StringUtils.strip(tokens[1]);
                        String comments = StringUtils.strip(tokens[2]);
                        Resource rc = new Resource(path);
                        String owner = req.author;

                        WorkingResource wr =
                                new WorkingResource("", path, revision, owner, comments,
                                        (new StringBuilder(String.valueOf(req.id))).toString());
                        wr.setRevisionStatus(revision);
                        wr.setSelected(true);
                        wr.title = req.subject;
                        returnValue.add(wr);
                    }


                } else {
                    System.out.println("[DIRECTORY] :: " + path);
                }


            }
        }

        return returnValue;
    }


    private static RedmineManager createRedmineManager(String uri, String id, String password) {

        return RedmineManagerFactory.createWithUserAuth(uri, id, password);
    }


    public static boolean Redminelogin(String uri, String id, String password) {

        UserManager um;
        RedmineManager mgr = RedmineManagerFactory.createWithUserAuth(uri, id, password);
        um = mgr.getUserManager();
        User usr = null;
        try {
            usr = um.getCurrentUser();
        } catch (RedmineException e1) {
            return usr.getLogin().equals(id);
        }
        return false;
    }


    public static User getRedmineUser(int no) {

        getUserMgr();
        User returnValue = null;
        try {
            returnValue = userMgr.getUserById(Integer.valueOf(no));
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return returnValue;
    }


    private static void getUserMgr() {

        RedmineManager mgr =
                RedmineManagerFactory.createWithUserAuth(sp.getProperty("RDM_URL"), sp.getProperty("RDM_LOGINID"),
                        sp.getProperty("RDM_PWD"));
        userMgr = mgr.getUserManager();
    }


    public static IssueManager getIssueManager(String uri, String id, String password) {

        RedmineManager mgr = createRedmineManager(uri, id, password);
        return mgr.getIssueManager();
    }


    public static List getProjectList(String uri, String id, String password) throws RedmineException {

        RedmineManager mgr = createRedmineManager(uri, id, password);
        ProjectManager pm = mgr.getProjectManager();
        return pm.getProjects();
    }


    public static List getQueryList(String uri, String id, String password) throws RedmineException {

        IssueManager im = getIssueManager(uri, id, password);
        return im.getSavedQueries();
    }

}
