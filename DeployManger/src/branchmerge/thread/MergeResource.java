package branchmerge.thread;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import utils.Properties;
import branchmerge.model.WorkingResource;
import branchmerge.svn.Svn;

public class MergeResource implements Runnable {

    private final WorkingResource resource;
    private String fromBranch;
    private String toBranch;
    private static Svn svn = null;
    private List report;
    ExcelReportHandler excelHandler;
    int row;
    protected static Properties sp = Properties.getInstance("config.properties");


    public MergeResource(WorkingResource resource, String fromBranch, String toBranch, List report) {

        excelHandler = null;
        this.resource = resource;
        this.fromBranch = fromBranch;
        this.toBranch = toBranch;
        this.report = report;
        if (fromBranch.equals(toBranch)) {
            throw new IllegalArgumentException(
                    "\uAC19\uC740 \uBE0C\uB79C\uCE58\uB85C\uB294 \uC791\uC5C5\uD560\uC218 \uC5C6\uC2B5\uB2C8\uB2E4. ");
        } else {
            return;
        }
    }


    public MergeResource(WorkingResource wr) {

        excelHandler = null;
        resource = wr;
        init();
    }


    public MergeResource(WorkingResource wr, ExcelReportHandler excelHandler, int row) {

        this(wr);
        this.row = row;
    }


    public MergeResource(WorkingResource wr, ArrayList report, String toBranch) {

        this(wr);
        this.report = report;
        this.toBranch = toBranch;
    }


    public MergeResource(WorkingResource wr, ArrayList report, String toBranch, String fromBranch) {

        this(wr);
        this.report = report;
        this.toBranch = toBranch;
        this.fromBranch = fromBranch;
    }


    protected void init() {

        if (svn == null) {
            String SVN_URL = sp.getProperty("SVN_URL");
            String SVN_ID = sp.getProperty("SVN_ID");
            String SVN_PASSWD = sp.getProperty("SVN_PASSWD");
            try {
                svn = new Svn(SVN_URL, SVN_ID, SVN_PASSWD);
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {

        merge();
    }


    private synchronized void merge() {

        try {
            mergeAction();
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            resource.setStatus(message);
            merge();
        }
        updateReport(resource);
    }


    private void mergeAction() throws SVNException, IOException, NoSuchAlgorithmException {

        // System.out.println("fromBranch ::" + fromBranch);
        // System.out.println("toBranch ::" + toBranch);
        checkWorkingResource(fromBranch, toBranch, resource);
        if (!resource.getSelected()) {
            resource.setStatus((new StringBuilder(String.valueOf(resource.getRevisionStatus()))).append(" . SKIP").toString());
        } else if (resource.getSelected()) {
            if (resource.comments != null && resource.comments.contains("[DELETED]")) {
                svn.delete(resource.getFullPath(toBranch));
                resource.setStatus("Deleted");
            } else if (svn.copyContents(resource, resource.getFullPath(fromBranch), resource.getRevision(), resource.getFullPath(toBranch),
                    -1L)) {
                resource.setStatus("Copied");
            } else {
                resource.setStatus("Same Contents. SKIP");
            }
        } else {
            resource.setStatus("Not Checked. SKIP");
        }
        System.out.println((new StringBuilder("## Merge Finished : ")).append(resource.getPath()).append(" ")
                .append(resource.getRevision()).toString());
    }


    private String updateReport(WorkingResource resource) {

        String log =
                (new StringBuilder("{")).append(resource.getStatus()).append(" , ").append(resource.getPath()).append(" , ")
                        .append(resource.getRevision()).append("}").toString();
        WorkingResource wrlog = resource;
        report.add(wrlog);
        return log;
    }


    public WorkingResource checkWorkingResource(String sourceBranch, String targetBranch, WorkingResource resource) throws SVNException {

        if (svn == null) {
            init();
        }
        fromBranch = sourceBranch;
        String fullPath4Trunk = resource.getFullPath(sourceBranch);
        String fullPath4Branch = resource.getFullPath(targetBranch);

        boolean isRevisionCheck = true;
        if (fromBranch == null) {
            isRevisionCheck = true;
        } else if (fromBranch.contains("/tags/")) {
            isRevisionCheck = false;
        }

        if (isRevisionCheck) {
            if (!resource.getComments().contains("[DELETED]") && !svn.exists(fullPath4Trunk, resource.getRevision())) {
                resource.setRevisionStatus("Not Exists");
                resource.setSelected(false);
            } else if (resource.getComments().contains("[DELETED]") && svn.exists(fullPath4Branch, svn.repository.getLatestRevision())) {
                resource.setRevisionStatus("DELETED");
                resource.setSelected(true);
            } else {
                CheckResources cr = new CheckResources(fromBranch, toBranch, svn);
                resource = cr.check(resource);
            }
        } else {
            //
        }

        return resource;
    }
}
