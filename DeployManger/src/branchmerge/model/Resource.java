package branchmerge.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Resource implements Comparable {

    String path;
    List changes;


    public Resource(String fullPath) {

        changes = new ArrayList();
        path = fullPath;
    }


    public String getPath() {

        return path;
    }


    public void setPath(String path) {

        this.path = path;
    }


    public List getChanges() {

        return changes;
    }


    public void setChanges(List changes) {

        this.changes = changes;
    }


    public void addChange(Change change) {

        changes.add(change);
    }


    public void removeChange(Change change) {

        changes.remove(change);
    }


    @Override
    public int hashCode() {

        int prime = 31;
        int result = 1;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Resource other = (Resource) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {

        return (new StringBuilder("Resource [path=")).append(path).append(", isConflict()=").append(isConflict()).append(", changes=")
                .append(changes).append("]").toString();
    }


    public int compareTo(Resource o) {

        return path.compareTo(o.path);
    }


    public boolean isConflict() {

        for (Iterator iterator = changes.iterator(); iterator.hasNext();) {
            Change change = (Change) iterator.next();
            if (change.isBroken()) {
                return true;
            }
        }

        return changes.size() > 1;
    }


    public Change getLatestChange() {

        if (changes == null || changes.size() == 0) {
            return null;
        }
        if (changes.size() == 1) {
            return (Change) changes.get(0);
        }
        Change latest = (Change) changes.get(0);
        boolean debug = false;
        if (latest.resource.path.contains("Format")) {
            debug = true;
        }
        for (Iterator iterator = changes.iterator(); iterator.hasNext();) {
            Change change = (Change) iterator.next();
            if (debug) {
                System.out.println(change);
            }
            if (Integer.parseInt(latest.getRevision()) < Integer.parseInt(change.getRevision())) {
                latest = change;
            }
        }

        if (debug) {
            System.out.println((new StringBuilder("Lateeset:")).append(latest).toString());
        }
        return latest;
    }


    @Override
    public int compareTo(Object obj) {

        return compareTo((Resource) obj);
    }

}
