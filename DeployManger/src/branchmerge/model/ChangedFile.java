package branchmerge.model;


import java.util.Date;

public class ChangedFile implements Comparable {

    char type;
    long revision;
    String path;
    String comment;
    Date date;
    boolean directory;


    public ChangedFile() {

    }


    public int compareTo(ChangedFile target) {

        if (path.compareTo(target.path) != 0) {
            return path.compareTo(target.path);
        } else {
            return Long.valueOf(revision).compareTo(Long.valueOf(target.revision));
        }
    }


    private void setDirectory(boolean directory) {

        this.directory = directory;
    }


    public String getTruncatedPath() {

        return path != null ? path.substring(6) : path;
    }


    public String getAssembledComment() {

        String temp = comment;
        if (isDirectory()) {
            temp = (new StringBuilder("[DIRECTORY] ")).append(temp).toString();
        }
        if (type == 'D') {
            temp = (new StringBuilder("[DELETED] ")).append(temp).toString();
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
        } else {
            return "";
        }
    }


    public char getType() {

        return type;
    }


    public void setType(char type) {

        this.type = type;
    }


    public long getRevision() {

        return revision;
    }


    public void setRevision(long revision) {

        this.revision = revision;
    }


    public String getPath() {

        return path;
    }


    public void setPath(String path) {

        this.path = path;
    }


    public String getComment() {

        return comment;
    }


    public void setComment(String comment) {

        this.comment = comment;
    }


    public Date getDate() {

        return date;
    }


    public void setDate(Date date) {

        this.date = date;
    }


    public boolean isDirectory() {

        return directory;
    }


    @Override
    public int compareTo(Object obj) {

        return compareTo((ChangedFile) obj);
    }

}
