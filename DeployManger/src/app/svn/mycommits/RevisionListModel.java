package app.svn.mycommits;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

import org.tmatesoft.svn.core.SVNLogEntry;

public class RevisionListModel extends AbstractListModel {

    private static final long serialVersionUID = 3244306992882994459L;
    List<SVNLogEntry> model;


    public void setModel(List<SVNLogEntry> model) {

        this.model = model;
        Collections.sort(model, new Comparator() {

            public int compare(SVNLogEntry s1, SVNLogEntry s2) {

                return Long.valueOf(s1.getRevision()).compareTo(Long.valueOf(s2.getRevision())) * -1;
            }


            @Override
            public int compare(Object o1, Object o2) {

                // TODO Auto-generated method stub
                return 0;
            }
        });
        fireContentsChanged(model, 0, getSize());
    }


    @Override
    public Object getElementAt(int index) {

        return new Revision(this.model.get(index));
    }


    @Override
    public int getSize() {

        return this.model == null ? 0 : this.model.size();
    }
}
