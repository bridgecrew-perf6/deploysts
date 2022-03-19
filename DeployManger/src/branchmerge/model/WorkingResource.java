package branchmerge.model;

public class WorkingResource implements Comparable {


    Resource resource;
    String status;
    boolean selected;
    String revisionStatus;
    public String comments;
    public String id;
    public String title;
    private String path;
    private String revision;
    private String owner;
    private String mergeResult;


    public WorkingResource(String status, String path, String revision, String owner) {

        this.status = status;
        this.path = path;
        this.revision = revision;
        this.owner = owner;
        selected = true;
        revisionStatus = "";
    }


    public WorkingResource(String status, String path, String revision, String owner, String comments) {

        this.status = status;
        this.path = path;
        this.revision = revision;
        this.owner = owner;
        selected = true;
        revisionStatus = "";
        this.comments = comments;
    }


    public WorkingResource(String status, String path, String revision, String owner, String comments, String ceresID) {

        this.status = status;
        this.path = path;
        this.revision = revision;
        this.owner = owner;
        selected = true;
        revisionStatus = "";
        this.comments = comments;
        id = ceresID;
    }


    public WorkingResource(String status, String path, String revision, String owner, String comments, String ceresID, String mergeResult,
            boolean selected) {

        this.status = status;
        this.path = path;
        this.revision = revision;
        this.owner = owner;
        this.selected = selected;
        this.comments = comments;
        id = ceresID;
        this.mergeResult = mergeResult;
    }


    public WorkingResource(String status, Resource resource) {

        this(status, resource.getPath(), resource.getLatestChange().getRevision(), resource.getLatestChange().getOwner().getName());
        this.resource = resource;
    }


    public String getComment() {

        if (resource.getChanges() != null && resource.getChanges().get(0) != null) {
            return ((Change) resource.getChanges().get(0)).getComment();
        } else {
            return null;
        }


    }


    public String getStatus() {

        return status;
    }


    public String getPath() {

        return path;
    }


    public String getFullPath(String branch) {

        String fullPath = path;
        if (!fullPath.contains("trunk") && branch != null) {
            fullPath = (new StringBuilder(String.valueOf(branch))).append("/").append(fullPath).toString();
        } else if (!fullPath.contains("trunk") && branch == null) {
            fullPath = (new StringBuilder("trunk/")).append(fullPath).toString();
        }
        return (new StringBuilder("/")).append(fullPath).toString();
    }


    public long getRevision() {

        if (revision == null) {
            return -1L;
        } else {
            return Long.parseLong(revision);
        }
    }


    public String getOwner() {

        return owner;
    }


    public void setOwner(String name) {

        owner = name;
    }


    public void setStatus(String string) {

        status = string;
    }


    public void setSelected(boolean check) {

        selected = check;
    }


    public boolean getSelected() {

        return selected;
    }


    public void setRevisionStatus(String value) {

        revisionStatus = value;
    }


    public String getRevisionStatus() {

        return revisionStatus;
    }


    public String getID() {

        return id;
    }


    public String getTitle() {

        return title;
    }


    public String getMergeResult() {

        return mergeResult;
    }


    public String getComments() {

        return comments;
    }


    @Override
    public int compareTo(Object obj) {

        WorkingResource o = (WorkingResource) obj;
        return id.compareTo(o.id);
    }

}
