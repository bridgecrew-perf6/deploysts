package branchmerge.model;


import java.util.HashSet;
import java.util.Set;

public class Owner implements Comparable {

    String name;
    Set changes;


    public Owner(String name) {

        changes = new HashSet();
        this.name = name;
    }


    public String getName() {

        return name;
    }


    public void setName(String name) {

        this.name = name;
    }


    public Set getChanges() {

        return changes;
    }


    public void addChange(Change change) {

        changes.add(change);
    }


    public int compareTo(Owner o) {

        return name.compareTo(o.name);
    }


    @Override
    public String toString() {

        return (new StringBuilder("Owner [name=")).append(name).append("]").toString();
    }


    @Override
    public int compareTo(Object obj) {

        return compareTo((Owner) obj);
    }

}
