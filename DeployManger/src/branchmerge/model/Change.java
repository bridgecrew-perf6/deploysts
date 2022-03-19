package branchmerge.model;

public class Change {

    Resource resource;
    Owner owner;
    String revision;
    String comment;
    boolean toDelete;


    public Change(Resource resource2, Owner owner, String revision2, String comment, boolean toDelete) {

        resource = resource2;
        this.owner = owner;
        revision = revision2;
        this.comment = comment;
        this.toDelete = toDelete;
        this.owner.addChange(this);
        resource.addChange(this);
    }


    public Resource getResource() {

        return resource;
    }


    public void setResource(Resource resource) {

        this.resource = resource;
    }


    public Owner getOwner() {

        return owner;
    }


    public void setOwner(Owner owner) {

        this.owner = owner;
    }


    public String getRevision() {

        return revision;
    }


    public void setRevision(String revision) {

        this.revision = revision;
    }


    public String getComment() {

        return comment;
    }


    public void setComment(String comment) {

        this.comment = comment;
    }


    public boolean isToDelete() {

        return toDelete;
    }


    public void setToDelete(boolean toDelete) {

        this.toDelete = toDelete;
    }


    public boolean isBroken() {

        return revision == null;
    }


    @Override
    public String toString() {

        return (new StringBuilder("Change [owner=")).append(owner).append(", revision=").append(revision).append(", comment=")
                .append(comment).append("]").toString();
    }


    @Override
    public int hashCode() {

        int prime = 31;
        int result = 1;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
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
        Change other = (Change) obj;
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        } else if (!resource.equals(other.resource)) {
            return false;
        }
        if (revision == null) {
            if (other.revision != null) {
                return false;
            }
        } else if (!revision.equals(other.revision)) {
            return false;
        }
        return true;
    }

}
