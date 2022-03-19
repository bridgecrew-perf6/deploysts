package redmine;


import java.util.Date;

import utils.Properties;

import com.taskadapter.redmineapi.bean.Issue;

public class DeployRequest {

    static Properties sp = Properties.getInstance("config.properties");
    public int id;
    public String assignee;
    public String author;
    public String authorFullName;
    public Date createOn;
    public String StatusName;
    public String description;
    public String delpoyFileList;
    public String subject;
    public String mergeLog;
    public String mergeStatus;
    public String wantedDate;
    public String PL;
    public String requestor;
    public String boRequestNo;


    public DeployRequest(Issue item) {

        PL = "";
        requestor = "";
        author = item.getAuthor().getFullName();
        authorFullName = item.getAuthor().getFullName();
        // author = item.getAuthorName();
        // authorFullName = item.getAuthorName();
        createOn = item.getCreatedOn();
        StatusName = item.getStatusName();
        description = item.getDescription();
        id = item.getId().intValue();
        subject = item.getSubject();
        // delpoyFileList = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_DEPLOYFILE"));
        // mergeLog = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGELOG"));
        // mergeStatus = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGESTATUS"));
        // wantedDate = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_WANTEDDATE"));
        // String userid_pl = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_PL"));

        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_DEPLOYFILE")) != null) {
            delpoyFileList = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_DEPLOYFILE")).toString();
        }
        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGELOG")) != null) {
            mergeLog = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGELOG")).toString();
        }

        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGESTATUS")) != null) {
            mergeStatus = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_MERGESTATUS")).toString();
        }
        String userid_pl = "";
        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_PL")) != null) {
            userid_pl = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_PL")).toString();
        }



        if (userid_pl != null) {
            PL = RedmineClient.getRedmineUser(Integer.parseInt(userid_pl)).getFullName();
        }

        String userid_req = "";
        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_REQUESTOR")) != null) {
            userid_req = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_REQUESTOR")).toString();
            requestor = RedmineClient.getRedmineUser(Integer.parseInt(userid_req)).getFullName();
        }

        // if (userid_req != null) {
        // requestor = RedmineClient.getRedmineUser(Integer.parseInt(userid_req)).getFullName();
        // }

        // boRequestNo = item.getCustomField(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_BOREQUESTNO"));

        if (item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_BOREQUESTNO")) != null) {
            boRequestNo = item.getCustomFieldByName(Properties.getPropertyUTF8("RDM_CUSTOMFILDE_BOREQUESTNO")).toString();
        }
    }


    public DeployRequest() {

        PL = "";
        requestor = "";
    }

}
