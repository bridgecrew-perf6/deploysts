package app.svn.mycommits;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class MyCommits {

    String userName = null;
    String password = null;
    String repositoryUrl = null;
    String branch = "";
    SVNRepository repository = null;

    String tempUser = null;


    public MyCommits() {

    }


    public MyCommits(String repositoryUrl, String userName, String password) throws SVNException {

        this.repositoryUrl = repositoryUrl;
        this.userName = userName;
        this.password = password;
        this.tempUser = userName;
        initRepository();
    }


    public void initRepository() {

        initRepository(this.userName, this.password);
        System.out.println("####init Completed!!");
    }


    public void initRepository(String user, String pw) {

        SVNRepositoryFactoryImpl.setup();
        try {
            this.repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(this.repositoryUrl));

            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, pw);
            this.repository.setAuthenticationManager(authManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Date before(int days) {

        return new Date(System.currentTimeMillis() - 86400000L * days);
    }


    public Between atDaysAgo(int daysAgo) {

        return revision최근부터_이날자까지(before(daysAgo));
    }


    public Between revision최근부터_이날자까지(Date 목적일) {

        try {
            return new Between(revisionAt(목적일), this.repository.getLatestRevision());
        } catch (SVNException e) {
        }
        throw new IllegalArgumentException();
    }


    public long revisionAt(Date at) {

        try {
            return this.repository.getDatedRevision(exactDate(at));
        } catch (SVNException ex) {
            System.out.println(ex);
        }
        throw new IllegalArgumentException();
    }


    private Date exactDate(Date at) {

        return new Date(at.getYear(), at.getMonth(), at.getDate());
    }


    public List<SVNLogEntry> logsFilterByAuthor(long start, long end, String authorFilter) {

        List res = null;
        try {
            res = new ArrayList();
            Collection<SVNLogEntry> logEntries = this.repository.log(new String[] {this.branch}, null, start, end, true, true);

            for (SVNLogEntry entry : logEntries) {
                if (authorFilter == null) {
                    res.add(entry);
                } else if (entry.getAuthor().equals(authorFilter)) {
                    res.add(entry);
                }

            }

        } catch (SVNException e) {
            throw new IllegalStateException(e);
        }
        return res;
    }


    public List<SVNLogEntry> revisions(int dayAgo) {

        long revisionAtDaysAgo = revisionAt(before(dayAgo));

        return logsFilterByAuthor(revisionAtDaysAgo, -1L, this.tempUser);
    }


    public List<Branch> branches() {

        List res = new ArrayList();
        try {
            fillBranches(res);
            fillTrunk(res);
            return res;
        } catch (SVNException e) {
        }
        throw new IllegalStateException();
    }


    private void fillTrunk(List<Branch> res) {

        Branch trunk = new Branch();
        trunk.setName("trunk");
        trunk.setPath("/trunk");
        res.add(0, trunk);
    }


    private void fillBranches(List<Branch> res) throws SVNException {

        collectBranches(res);
        sortBranches(res);
    }


    private void collectBranches(List<Branch> res) throws SVNException {

        Collection entries = this.repository.getDir("/branches", -1L, null, (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            res.add(new Branch(((SVNDirEntry) iterator.next()).getName()));
        }
    }


    private void sortBranches(List<Branch> res) {

        Collections.sort(res, new Comparator() {

            public int compare(Branch b1, Branch b2) {

                return b1.getName().compareTo(b2.getName());
            }


            @Override
            public int compare(Object o1, Object o2) {

                // TODO Auto-generated method stub
                return 0;
            }
        });
    }


    public boolean isDirectory(String path, long revision) {

        try {
            SVNDirEntry info = this.repository.info(path, revision);
            if (info == null) {
                return false;
            }
            return info.getKind() == SVNNodeKind.DIR;
        } catch (SVNException e) {
        }
        throw new IllegalArgumentException();
    }


    public boolean isDirectory(ChangedFile file) {

        long revision = file.getRevision();
        if (file.getType() == 'D') {
            revision -= 1L;
        }

        return isDirectory(file.getPath(), revision);
    }


    public String getUserName() {

        return this.userName;
    }


    public void setUserName(String userName) {

        this.userName = userName;
    }


    public String getPassword() {

        return this.password;
    }


    public void setPassword(String password) {

        this.password = password;
    }


    public String getRepositoryUrl() {

        return this.repositoryUrl;
    }


    public void setRepositoryUrl(String repositoryUrl) {

        this.repositoryUrl = repositoryUrl;
    }


    public String getBranch() {

        return this.branch;
    }


    public void setBranch(String branch) {

        this.branch = branch;
    }


    public SVNRepository getRepository() {

        return this.repository;
    }


    public void setRepository(SVNRepository repository) {

        this.repository = repository;
    }


    public void setTempUser(String user) {

        this.tempUser = user;
    }


    public String getTempUser() {

        return this.tempUser;
    }
}
