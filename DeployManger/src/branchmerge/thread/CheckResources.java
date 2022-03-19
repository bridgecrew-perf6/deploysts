package branchmerge.thread;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import branchmerge.model.WorkingResource;
import branchmerge.svn.Svn;

public class CheckResources {

    private List resources;
    private final Svn svn;
    private final String branch;
    private final String toBranch;
    private String reportPath;


    public CheckResources(String fromBranch, String toBranch, Svn svn) {

        branch = fromBranch;
        this.toBranch = toBranch;
        this.svn = svn;
    }


    public WorkingResource check(WorkingResource wr) {

        WorkingResource resource = wr;
        try {
            if (toBranch.equals("trunk")) {
                svn.getFile(branch, resource);
                resource.setRevisionStatus("OK");
            } else {
                svn.getFile(branch, resource);
                String fullPath4ToBranch = resource.getFullPath(toBranch);
                if (svn.exists(fullPath4ToBranch, -1L)) {
                    SVNLogEntry last = svn.getLastLogEntry(fullPath4ToBranch);
                    if (!last.getMessage().contains("copy from")) {
                        last = findRealRevision(fullPath4ToBranch);
                    }
                    if (!isNew(resource.getRevision(), last)) {
                        resource.setRevisionStatus("OLD");
                        resource.setSelected(false);
                    } else {
                        resource.setRevisionStatus("OK");
                        resource.setSelected(true);
                    }
                } else {
                    svn.getFile(branch, resource);
                    resource.setRevisionStatus("NEW");
                    resource.setSelected(true);
                }
            }
        } catch (SVNException e) {
            e.printStackTrace();
            String message = e.getMessage();
            resource.setStatus(message.substring(message.indexOf("svn:") + 4).trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String message = e.getMessage();
            resource.setStatus(message);
        }
        return resource;
    }


    public WorkingResource checkNoCheckRivision(WorkingResource wr) {

        WorkingResource resource = wr;
        try {

            svn.getFile(branch, resource);
            resource.setRevisionStatus("NEW");
            resource.setSelected(true);

        } catch (SVNException e) {
            e.printStackTrace();
            String message = e.getMessage();
            resource.setStatus(message.substring(message.indexOf("svn:") + 4).trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String message = e.getMessage();
            resource.setStatus(message);
        }
        return resource;
    }


    private SVNLogEntry findRealRevision(String fullPath4ToBranch) {

        Collection orglist = svn.getLogEntry(fullPath4ToBranch);
        Iterator iterator = orglist.iterator();
        ArrayList list = new ArrayList();
        SVNLogEntry returnValue = null;
        for (; iterator.hasNext(); list.add(iterator.next())) {
            ;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            SVNLogEntry entry = (SVNLogEntry) list.get(i);
            if (!entry.getMessage().contains("[\uD615\uC0C1\uAD00\uB9AC]")) {
                returnValue = entry;
                return returnValue;
            }
        }

        return returnValue;
    }


    private void generateReport(List report) {

        Collections.sort(report);
        try {
            File reportFile = new File(new File(reportPath), "check_reject.txt");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(reportFile), "utf-8"));
            String string;
            for (Iterator iterator = report.iterator(); iterator.hasNext(); pw.println(string)) {
                string = (String) iterator.next();
            }

            pw.close();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }


    private boolean isNew(long revision, SVNLogEntry last) {

        boolean returnValue = false;
        String message = last.getMessage();
        System.out.println("message :: " + message);
        if (message.contains("copy from")) {
            message = message.replace(")", ":");
            String temp1[] = message.split(":");
            // String tt = temp1[1].replace(" ", "");
            String tt = temp1[temp1.length - 2].replace(" ", "");
            System.out.println("tt :: " + tt);
            long fromRevision = Long.parseLong(tt);
            System.out.println((new StringBuilder("## Last:")).append(fromRevision).append(" New:").append(revision).append(" path:")
                    .append(last.getChangedPaths()).toString());
            if (revision >= fromRevision) {
                returnValue = true;
            }
        } else {
            returnValue = true;
        }
        return returnValue;
    }

}
