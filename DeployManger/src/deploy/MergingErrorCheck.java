package deploy;


import java.util.ArrayList;
import java.util.List;

import redmine.DeployRequest;
import redmine.RedmineClient;
import utils.DateUtils;
import utils.Properties;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.Transport.ResultsWrapper;


public class MergingErrorCheck {

    static Properties sp = Properties.getInstance("config.properties");
    static String RDM_PRJ_ID;
    static String RDM_URL;
    static String RDM_LOGINID;
    static String RDM_PWD;
    static int RDM_QUERY_DPR_DEV;
    static int RDM_QUERY_DPR_TST;
    static int RDM_QUERY_DPR_FINISH = 16;


    public MergingErrorCheck(String targetServer, String searchStr) {

        List resources;

        sp = Properties.getInstance("config.properties");

        RDM_PRJ_ID = Properties.getPropertyUTF8("RDM_PRJ_ID");
        RDM_URL = Properties.getPropertyUTF8("RDM_URL");
        RDM_LOGINID = Properties.getPropertyUTF8("RDM_LOGINID");
        RDM_PWD = Properties.getPropertyUTF8("RDM_PWD");

        RDM_QUERY_DPR_DEV = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_DEV"));
        RDM_QUERY_DPR_TST = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_TST"));
        // RDM_QUERY_DPR_FINISH = Integer.parseInt(sp.getProperty("RDM_QUERY_DPR_FINISH"));

        IssueManager issueManger = RedmineClient.getIssueManager(RDM_URL, RDM_LOGINID, RDM_PWD);

        if (targetServer.equals("dev")) {
            checkErrorDeployRequest(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_DEV, searchStr, targetServer);
        } else if (targetServer.equals("test")) {
            checkErrorDeployRequest(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_TST, searchStr, targetServer);
        } else if (targetServer.equals("real")) {
            checkErrorDeployRequest(issueManger, RDM_PRJ_ID, RDM_QUERY_DPR_FINISH, searchStr, targetServer);
        }

    }


    public void checkErrorDeployRequest(IssueManager redmineIssueManager, String projectId, int queryId, String searchStr,
            String targetServer) {

        boolean isSearch = false;
        try {

            DeployRequest deployRequest = null;
            String files = "";
            String author = "";
            int id = 0;
            String subject = "";
            // List list = RedmineClient.getDeployRequests(redmineIssueManager, projectId, queryId);

            List requests = redmineIssuesRestApiList(redmineIssueManager, projectId, targetServer);

            // MergingErrorCheckThread mergingErrorCheckthread = new MergingErrorCheckThread(targetServer);
            // Thread thread = new Thread(mergingErrorCheckthread);
            // thread.start();
            //
            // Thread.currentThread().getName();
            //
            // if (!thread.isAlive()) {
            // List requests = MergingErrorCheckThread.requests;
            System.out.println("targetServer :;" + targetServer);
            System.out.println(" requests.size(): " + requests.size());


            List errorFiles = new ArrayList();
            for (int i = 0; i < requests.size(); i++) {
                deployRequest = (DeployRequest) requests.get(i);
                files = deployRequest.delpoyFileList;
                author = deployRequest.author;
                id = deployRequest.id;
                subject = deployRequest.subject;

                System.out.println("id: " + id);
                System.out.println("author: " + author);
                System.out.println("subject: " + subject);
                System.out.println("StatusName: " + deployRequest.StatusName);
                System.out.println("wantedDate: " + deployRequest.wantedDate);

                if (files != null && files.contains(searchStr)) {
                    isSearch = true;
                    errorFiles.add(requests.get(i));
                }
            }

            if (isSearch) {
                for (int i = 0; i < errorFiles.size(); i++) {
                    deployRequest = (DeployRequest) errorFiles.get(i);
                    files = deployRequest.delpoyFileList;
                    author = deployRequest.author;
                    id = deployRequest.id;
                    subject = deployRequest.subject;

                    System.out.println("######################################################");
                    System.out.println("서버 : " + targetServer);
                    System.out.println("에러파일명 : " + searchStr);
                    System.out.println("일감번호 : " + id);
                    System.out.println("제목 : " + subject);
                    System.out.println("담당자 : " + author);
                    System.out.println("######################################################");
                }
            } else {
                System.out.println("######################################################");
                System.out.println("서버 : " + targetServer);
                System.out.println("에러파일명 : " + searchStr + "에 해당되는 일감이 없습니다.");
                System.out.println("######################################################");
            }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List redmineIssuesRestApiList(IssueManager redmineIssueManager, String projectId, String targetServer) throws RedmineException {

        List requests = new ArrayList();
        List requestsAdd = new ArrayList();

        String resultDate = DateUtils.getDate("yyyy-MM-dd");
        // resultDate = DateUtils.getNextDate(1, "yyyy-MM-dd");

        Params params = new Params();

        params.add("project_id", projectId);
        params.add("set_filter", "1");

        params.add("f[]", "tracker_id");
        // params.add("op[tracker_id]", "=");
        params.add("v[tracker_id][]", "4");
        params.add("v[tracker_id][]", "17");

        params.add("f[]", "status_id");
        // params.add("op[status_id]", "=");
        params.add("v[status_id][]", "1");
        params.add("v[status_id][]", "19");
        params.add("v[status_id][]", "20");


        if (targetServer.equals("real")) {

            params.add("v[status_id][]", "21");
            params.add("v[status_id][]", "22");

            params.add("f[]", "cf_8");
            params.add("op[cf_8]", "=");
            params.add("v[cf_8][]", resultDate);
        }

        for (int i = 1; i <= 10; i++) {
            params.add("page", i + "");
            System.out.println("params ::" + params);
            ResultsWrapper<Issue> issuesResults = redmineIssueManager.getIssues(params);
            List issues = issuesResults.getResults();

            System.out.println(" i ::" + i + "");
            System.out.println("issues ::" + issues.size());

            if (issues != null && issues.size() > 0) {
                DeployRequest request;
                for (Object element : issues) {
                    Issue issue = (Issue) element;
                    request = new DeployRequest(issue);
                    requestsAdd.add(request);
                }
                requests.addAll(requestsAdd);
            } else {
                break;
            }
        }

        return requests;
    }


}
