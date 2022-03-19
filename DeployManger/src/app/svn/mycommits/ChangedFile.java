package app.svn.mycommits;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class ChangedFile implements Comparable<ChangedFile> {

    public char type;
    public long revision;
    public String path;
    public String comment;
    public Date date;
    public boolean directory;


    @Override
    public int compareTo(ChangedFile target) {

        if (this.path.compareTo(target.path) != 0) {
            return this.path.compareTo(target.path);
        }
        return Long.valueOf(this.revision).compareTo(Long.valueOf(target.revision));
    }


    public static List<ChangedFile> from(MyCommits commits, SVNLogEntry entry) {

        List res = new ArrayList();
        Collection<SVNLogEntryPath> paths = entry.getChangedPaths().values();
        for (SVNLogEntryPath path : paths) {
            ChangedFile file = new ChangedFile();
            file.setRevision(entry.getRevision());
            file.setComment(entry.getMessage());
            file.setDate(entry.getDate());
            file.setPath(path.getPath());
            file.setType(path.getType());
            file.setDirectory(commits.isDirectory(file));
            res.add(file);
        }

        return res;
    }


    private void setDirectory(boolean directory) {

        this.directory = directory;
    }


    public String getTruncatedPath() {

        return this.path == null ? this.path : this.path.substring(6);
    }


    public String getAssembledComment() {

        String temp = this.comment;
        if (isDirectory()) {
            temp = "[DIRECTORY] " + temp;
        }
        if (this.type == 'D') {
            temp = "[DELETED] " + temp;
        }
        return temp;
    }


    public String getProject() {

        String temp = getTruncatedPath();
        temp = temp.substring(1);
        temp = temp.substring(0, temp.indexOf("/"));
        return temp;
    }


    public String getSource() {

        String temp = getTruncatedPath();
        return temp.substring(temp.lastIndexOf("/") + 1);
    }


    public String getPathOnly() {

        String temp = getTruncatedPath();
        temp = temp.substring(1);
        temp = temp.substring(temp.indexOf("/"));
        if (temp.contains("/")) {
            return temp.substring(0, temp.lastIndexOf("/"));
        }
        return "";
    }


    public char getType() {

        return this.type;
    }


    public void setType(char type) {

        this.type = type;
    }


    public long getRevision() {

        return this.revision;
    }


    public void setRevision(long revision) {

        this.revision = revision;
    }


    public String getPath() {

        return this.path;
    }


    public void setPath(String path) {

        this.path = path;
    }


    public String getComment() {

        return this.comment;
    }


    public void setComment(String comment) {

        this.comment = comment;
    }


    public Date getDate() {

        return this.date;
    }


    public void setDate(Date date) {

        this.date = date;
    }


    public boolean isDirectory() {

        return this.directory;
    }
}
