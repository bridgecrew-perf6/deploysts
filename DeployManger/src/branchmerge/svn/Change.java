package branchmerge.svn;

public class Change {


    String project;
    String path;
    String resource;
    long revision;
    private final ChangeType changeType;

    public Change(String project2, String path2, String resource2, long revision2, ChangeType changeType)
    {
        project = project2;
        path = path2;
        resource = resource2;
        revision = revision2;
        this.changeType = changeType;
    }

    public String getProject()
    {
        return project;
    }

    public void setProject(String project)
    {
        this.project = project;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getResource()
    {
        return resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public long getRevision()
    {
        return revision;
    }

    public void setRevision(long revision)
    {
        this.revision = revision;
    }

    public ChangeType getChangeType()
    {
        return changeType;
    }

    public int hashCode()
    {
        int prime = 31;
        int result = 1;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Change other = (Change)obj;
        if(path == null)
        {
            if(other.path != null)
                return false;
        } else
        if(!path.equals(other.path))
            return false;
        if(project == null)
        {
            if(other.project != null)
                return false;
        } else
        if(!project.equals(other.project))
            return false;
        if(resource == null)
        {
            if(other.resource != null)
                return false;
        } else
        if(!resource.equals(other.resource))
            return false;
        return true;
    }

    public String toString()
    {
        return (new StringBuilder(String.valueOf(project))).append(",").append(path).append(",").append(resource).append(",").append(revision).append(",").append(changeType).toString();
    }
}
