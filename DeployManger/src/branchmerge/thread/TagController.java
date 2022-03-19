package branchmerge.thread;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;

import utils.Properties;
import branchmerge.svn.Svn;

public class TagController {

    protected static Properties sp;
    protected static String TAG_FORMAT;
    static String BRANCH_DEV;
    static String BRANCH_TST;
    static String BRANCH_REL;
    static String BRANCH_TAGS;
    static String BRANCH_LAST;

    static {
        sp = Properties.getInstance("config.properties");
        TAG_FORMAT = sp.getProperty("TAG_FORMAT");
        BRANCH_DEV = sp.getProperty("BRANCH_DEV");
        BRANCH_TST = sp.getProperty("BRANCH_TST");
        BRANCH_REL = sp.getProperty("BRANCH_REL");
        BRANCH_TAGS = sp.getProperty("BRANCH_TAGS");
        BRANCH_LAST = sp.getProperty("BRANCH_LAST");
    }


    public TagController() {

    }


    public static String makeTag(String url, String name, String password) {

        String label = makeTagLabel("REL");
        try {
            String fromPath = BRANCH_REL;
            String toPath = (new StringBuilder(String.valueOf(BRANCH_TAGS))).append(label).toString();
            copyBranch(url, name, password, fromPath, toPath);
            System.out.println((new StringBuilder("## TAG created!!:")).append(label).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return label;
    }


    public static String makeTagWithLabel(String url, String name, String password, String fromPath, String toPath) {

        try {
            copyBranch(url, name, password, fromPath, toPath);
            System.out.println((new StringBuilder("## TAG created!!: ")).append(toPath).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return toPath;
    }


    public static void syncRELToTST(String url, String name, String password) {

        try {
            String fromPath = BRANCH_REL;
            String toPath = BRANCH_TST;
            SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
            SVNRevision fromRevision = SVNRevision.HEAD;
            SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
            SVNRevision toRevision = SVNRevision.HEAD;
            boolean isMove = false;
            boolean makeParents = true;
            boolean failWhenDstExists = true;
            String commitMessage =
                    makeCommitMessage((new StringBuilder("From : ")).append(fromPath).append(" To : ").append(toPath).toString());
            org.tmatesoft.svn.core.SVNProperties revisionProperties = null;
            deleteBranch(url, name, password, toPath);
            SVNClientManager ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
            ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {new SVNCopySource(SVNRevision.UNDEFINED, fromRevision, fromUrl)},
                    toUrl, isMove, makeParents, failWhenDstExists, commitMessage, revisionProperties);
            System.out.println((new StringBuilder("## TST created!!:  From : ")).append(fromUrl.getPath()).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }


    public static void syncLastTagWithREL(String url, String name, String password) {

        try {
            String fromPath = BRANCH_REL;
            String toPath = BRANCH_LAST;
            SVNURL fromUrl = syncFromTo(url, name, password, fromPath, toPath);
            System.out.println((new StringBuilder("## LAST Branches were created!!:  From : ")).append(fromUrl.getPath()).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }


    public static void syncRELWithTag(String url, String name, String password, String tag) {

        try {
            String fromPath = BRANCH_TAGS;
            String toPath = BRANCH_REL;
            SVNURL fromUrl = syncFromTo(url, name, password, fromPath, toPath);
            System.out.println((new StringBuilder("## REL Branches were created!!:  From : ")).append(fromUrl.getPath()).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }


    public static SVNURL syncFromTo(String url, String name, String password, String from, String to) throws SVNException {

        String fromPath = from;
        String toPath = to;
        SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
        SVNRevision fromRevision = SVNRevision.HEAD;
        SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
        SVNRevision toRevision = SVNRevision.HEAD;
        boolean isMove = false;
        boolean makeParents = true;
        boolean failWhenDstExists = true;
        String commitMessage =
                makeCommitMessage((new StringBuilder("From : ")).append(fromPath).append(" To : ").append(toPath).toString());
        org.tmatesoft.svn.core.SVNProperties revisionProperties = null;
        SVNClientManager ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
        try {
            deleteBranch(url, name, password, toPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {new SVNCopySource(SVNRevision.UNDEFINED, fromRevision, fromUrl)},
                toUrl, isMove, makeParents, failWhenDstExists, commitMessage, revisionProperties);
        System.out.println((new StringBuilder("From : ")).append(fromPath).append(" To : ").append(toPath).append(" Copy가 완료되었습니다.")
                .toString());
        return fromUrl;
    }


    public static void copyTagToREL(String url, String name, String password, String tagLabel) {

        try {
            String fromPath = (new StringBuilder(String.valueOf(BRANCH_TAGS))).append(tagLabel).toString();
            String toPath = BRANCH_REL;
            SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
            SVNRevision fromRevision = SVNRevision.HEAD;
            SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
            SVNRevision toRevision = SVNRevision.HEAD;
            boolean isMove = false;
            boolean makeParents = true;
            boolean failWhenDstExists = true;
            String commitMessage =
                    makeCommitMessage((new StringBuilder("From : ")).append(fromPath).append(" To : ").append(toPath).toString());
            org.tmatesoft.svn.core.SVNProperties revisionProperties = null;
            SVNClientManager ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
            deleteBranch(url, name, password, toPath);
            ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {new SVNCopySource(SVNRevision.UNDEFINED, fromRevision, fromUrl)},
                    toUrl, isMove, makeParents, failWhenDstExists, commitMessage, revisionProperties);
            System.out.println((new StringBuilder("## REL LAST Branches created!!  From : ")).append(fromUrl.getPath()).toString());
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }


    private static void deleteBranch(String url, String name, String password, String toPath) {

        try {
            Svn svn = new Svn(url, name, password);
            svn.delete(toPath);
        } catch (SVNException e) {
            e.printStackTrace();
        }

    }


    public static void copyBranch(String url, String name, String password, String fromPath, String toPath) throws SVNException {

        SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
        SVNRevision fromRevision = SVNRevision.HEAD;
        SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
        SVNRevision toRevision = SVNRevision.HEAD;
        boolean isMove = false;
        boolean makeParents = true;
        boolean failWhenDstExists = true;
        String commitMessage =
                makeCommitMessage((new StringBuilder("From : ")).append(fromPath).append(" To : ").append(toPath).toString());
        org.tmatesoft.svn.core.SVNProperties revisionProperties = null;
        SVNClientManager ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
        ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {new SVNCopySource(SVNRevision.UNDEFINED, fromRevision, fromUrl)},
                toUrl, isMove, makeParents, failWhenDstExists, commitMessage, revisionProperties);
    }


    private static String makeCommitMessage(String message) {

        String commitMessage = (new StringBuilder("[\uD615\uC0C1\uAD00\uB9AC] \n")).append(message).toString();
        return commitMessage;
    }


    public static String makeTagLabel(String BranchName) {

        String time_label = makeTagLabel();
        return (new StringBuilder(String.valueOf(BranchName))).append("-").append(time_label).toString();
    }


    public static String makeTagLabel() {

        String _format = TAG_FORMAT;
        SimpleDateFormat format2 = new SimpleDateFormat(_format);
        String time_label = format2.format(new Date());
        return time_label;
    }


    public static void delete(String path) {

        Svn svn = null;
        String SVN_URL = sp.getProperty(path);
        String SVN_ID = sp.getProperty("SVN_ID");
        String SVN_PASSWD = sp.getProperty("SVN_PASSWD");
        deleteBranch(SVN_URL, SVN_ID, SVN_PASSWD, path);
    }


}
