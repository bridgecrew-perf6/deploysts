package branchmerge.model;


import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ChangeSet {

    Set owners;
    Set resources;


    public ChangeSet() {

        owners = new TreeSet();
        resources = new TreeSet();
    }


    public void addOwner(Owner owner) {

        owners.add(owner);
    }


    public void removeOwner(Owner owner) {

        owners.remove(owner);
    }


    public Owner findOwner(String name) {

        Owner owner;
        for (Iterator iterator = owners.iterator(); iterator.hasNext();) {
            owner = (Owner) iterator.next();
            if (name.equals(owner.getName())) {
                return owner;
            }
        }

        owner = new Owner(name);
        addOwner(owner);
        return owner;
    }


    public void addResource(Resource resource) {

        resources.add(resource);
    }


    public void removeResource(Resource resource) {

        resources.remove(resource);
    }


    public Resource findResource(String path) {

        Resource resource;
        for (Iterator iterator = resources.iterator(); iterator.hasNext();) {
            resource = (Resource) iterator.next();
            if (resource.getPath().equals(path)) {
                return resource;
            }
        }

        resource = new Resource(path);
        addResource(resource);
        return resource;
    }


    public Set getOwners() {

        return owners;
    }


    public void setOwners(Set owners) {

        this.owners = owners;
    }


    public Set getResources() {

        return resources;
    }


    public void setResources(Set resources) {

        this.resources = resources;
    }
}
