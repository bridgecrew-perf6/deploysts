package branchmerge.svn;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import utils.Properties;
import branchmerge.model.WorkingResource;

public class Svn {


    public SVNRepository repository;
    private final String url;
    private final String name;
    private final String password;
    private SVNClientManager ourClientManager;
    static Properties sp = Properties.getInstance("config.properties");


    public Svn(String url, String name, String password) throws SVNException {

        this.url = url;
        this.name = name;
        this.password = password;
        SVNRepositoryFactoryImpl.setup();
        repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        org.tmatesoft.svn.core.auth.ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);
        ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
    }


    public void listEntries(String path, boolean isRecursive) throws SVNException {

        // Collection entries = repository.getDir(path, -1L, null, null);
        Collection entries = repository.getDir(path, -1L, null, (Collection) null);
        SVNDirEntry svndirentry;
        for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
            svndirentry = (SVNDirEntry) iterator.next();
        }

    }


    public void merge(String relativePath, String fromPath, long fromRevisionNo, String toPath, long toRevisionNo) throws SVNException,
            IOException {

        SVNClientManager ourClientManager = SVNClientManager.newInstance(new DefaultSVNOptions(), name, password);
        SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
        SVNRevision fromRevision = SVNRevision.create(fromRevisionNo);
        SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
        SVNRevision toRevision = SVNRevision.HEAD;
        File wcRoot = File.createTempFile("branchmerge", ".tmp");
        if (wcRoot.exists()) {
            wcRoot.delete();
        }
        wcRoot.deleteOnExit();
        wcRoot.mkdirs();
        checkOutWorkingCopy(toUrl, wcRoot, toRevision);
        SVNDiffClient diff = ourClientManager.getDiffClient();
        File mergeTarget = new File(wcRoot, relativePath.substring(relativePath.lastIndexOf("/")));
        diff.doMerge(fromUrl, fromRevision, toUrl, toRevision, mergeTarget, SVNDepth.UNKNOWN, false, false, false, false);
        ourClientManager.getCommitClient().doCommit(new File[] {new File(wcRoot, relativePath.substring(relativePath.lastIndexOf("/")))},
                false, "merge", null, null, false, true, SVNDepth.IMMEDIATES);
    }


    public void checkOutWorkingCopy(SVNURL url, File wcRoot, SVNRevision revision) throws SVNException {

        SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
        url = url.removePathTail();
        updateClient.doCheckout(url, wcRoot, revision, SVNRevision.HEAD, SVNDepth.FILES, false);
    }


    public void listEntries(String path) throws SVNException {

        listEntries(path, false);
    }


    @SuppressWarnings("rawtypes")
    public List listBranches() throws SVNException {

        Collection temp = repository.getDir("branches", -1L, null, (Collection) null);
        List branches = new ArrayList();
        branches.add("trunk");
        SVNDirEntry entry;
        for (Iterator iterator = temp.iterator(); iterator.hasNext(); branches.add(entry.getPath())) {
            entry = (SVNDirEntry) iterator.next();
        }

        return branches;
    }


    public boolean exists(String path, long revision) throws SVNException {

        SVNNodeKind nodeKind = repository.checkPath(path, revision);
        // System.out.println("nodeKind ::" + nodeKind);
        // System.out.println("SVNNodeKind.NONE ::" + SVNNodeKind.NONE);
        return nodeKind != SVNNodeKind.NONE;
    }


    public SVNFile getFile(String path, long revision) throws SVNException, FileNotFoundException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long remoteRevision = repository.getFile(path, revision, null, out);
        return new SVNFile(remoteRevision, out.toByteArray());
    }


    public SVNLogEntry getLastLogEntry(String path) {

        SVNLogEntry last = null;
        try {
            Collection logEntries = repository.log(new String[] {path}, null, 0L, -1L, true, true);
            Iterator iterator = logEntries.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                last = (SVNLogEntry) iterator.next();
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return last;
    }


    public Collection getLogEntry(String path) {

        Collection logEntries = null;
        try {
            logEntries = repository.log(new String[] {path}, null, 0L, -1L, true, false);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return logEntries;
    }


    public SVNFile getFile(String branch, WorkingResource resource) throws SVNException, FileNotFoundException {

        return getFile(resource.getFullPath(branch), resource.getRevision());
    }


    public boolean copyContents(WorkingResource resource, String fromPath, long fromRevisionNo, String toPath, long toRevisionNo)
            throws SVNException, IOException, NoSuchAlgorithmException {

        SVNURL fromUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fromPath).toString());
        SVNRevision fromRevision = SVNRevision.create(fromRevisionNo);
        SVNURL toUrl = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(toPath).toString());
        SVNRevision toRevision = SVNRevision.HEAD;
        String owner = resource.getOwner();
        if (exists(toPath, -1L)) {
            return copyContents(fromPath, fromRevisionNo, toPath, fromUrl, fromRevision, toUrl, toRevision, owner);
        } else {
            if (copyFile(fromUrl, fromRevision, toUrl, toRevision, owner)) {
                return true;
            } else {
                System.out.println("File Not Found Contents. Skip");
                return false;
            }

        }
    }


    private boolean copyContents(String fromPath, long fromRevisionNo, String toPath, SVNURL fromUrl, SVNRevision fromRevision,
            SVNURL toUrl, SVNRevision toRevision, String owner) throws SVNException, FileNotFoundException, IOException,
            NoSuchAlgorithmException {

        long time = System.currentTimeMillis();
        File wcRoot =
                new File((new StringBuilder(String.valueOf(sp.getProperty("MERGE_HOME")))).append("/").append(name).append(time)
                        .append(".tmp").toString());
        if (wcRoot.exists()) {
            deleteDirectory(wcRoot);
        }
        checkOutWorkingCopy(toUrl, wcRoot, toRevision);
        SVNFile file = getFile(fromPath, fromRevisionNo);
        String fileMD5 = md5(file.getData());
        File mergeTarget = new File(wcRoot, toPath.substring(toPath.lastIndexOf("/")));
        String mergeTargetMD5 = md5(getData(mergeTarget));
        if (mergeTargetMD5.equals(fileMD5)) {
            System.out.println("Same Contents. Skip");
            return false;
        }
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(mergeTarget));
        bout.write(file.getData());
        bout.close();
        String commitMessage = makeCommitMessage(fromUrl, fromRevision, toUrl, toRevision, owner);
        SVNCommitInfo result =
                ourClientManager.getCommitClient().doCommit(new File[] {mergeTarget}, false, commitMessage, null, null, false, true,
                        SVNDepth.IMMEDIATES);
        deleteDirectory(wcRoot);
        if (result.getErrorMessage() != null) {
            throw new RuntimeException(result.getErrorMessage().getMessage());
        } else {
            return true;
        }
    }


    private String md5(byte data[]) throws NoSuchAlgorithmException {

        StringBuffer buf;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte digested[] = digest.digest(data);
        buf = new StringBuffer();
        byte abyte0[];
        int j = (abyte0 = digested).length;
        for (int i = 0; i < j; i++) {
            byte b = abyte0[i];
            buf.append(Integer.toHexString(255 & b));
        }

        return buf.toString();
    }


    private byte[] getData(File mergeTarget) throws IOException {

        ByteArrayOutputStream bout;
        bout = new ByteArrayOutputStream();
        FileInputStream fin = new FileInputStream(mergeTarget);
        byte buf[] = new byte[4096];
        do {
            int r = fin.read(buf, 0, 4096);
            if (r == -1) {
                break;
            }
            bout.write(buf, 0, r);
        } while (true);
        bout.close();
        return bout.toByteArray();
    }


    public boolean copyFile(SVNURL fromUrl, SVNRevision fromRevision, SVNURL toUrl, SVNRevision toRevision, String owner) {

        boolean isMove = false;
        boolean makeParents = true;
        boolean failWhenDstExists = true;
        try {
            String commitMessage = makeCommitMessage(fromUrl, fromRevision, toUrl, toRevision, owner);
            org.tmatesoft.svn.core.SVNProperties revisionProperties = null;
            ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {new SVNCopySource(SVNRevision.UNDEFINED, fromRevision, fromUrl)},
                    toUrl, isMove, makeParents, failWhenDstExists, commitMessage, revisionProperties);
        } catch (SVNException e) {
            // #SVNException File Not found 처리
            System.out.println("####################################################################");
            System.out.println("" + e.getMessage());
            System.out.println("####################################################################");
            return false;
        }

        return true;

    }


    private String makeCommitMessage(SVNURL fromUrl, SVNRevision fromRevision, SVNURL toUrl, SVNRevision toRevision, String owner)
            throws SVNException {

        return (new StringBuilder("copy from ")).append(fromUrl.getPath()).append(" (").append(owner).append(":")
                .append(fromRevision.getNumber()).append(") to ").append(toUrl.getPath()).append(" (R").append(toRevision.getNumber())
                .toString();
    }


    public static boolean deleteDirectory(File path) {

        if (path.exists()) {
            File files[] = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }

        }
        return path.delete();
    }


    public void delete(String fullPath) {

        try {
            SVNURL deletResource = SVNURL.parseURIDecoded((new StringBuilder(String.valueOf(url))).append("/").append(fullPath).toString());
            ourClientManager.getCommitClient().doDelete(new SVNURL[] {deletResource}, "DELETED");
            System.out.println((new StringBuilder("## \uBE0C\uB79C\uCE58\uAC00 \uC0AD\uC81C\uB418\uC5C8\uC2B5\uB2C8\uB2E4 :")).append(
                    fullPath).toString());
        } catch (SVNException e) {
            System.out.println("####################################################################");
            System.out.println("" + e.getMessage());
            System.out.println("####################################################################");
        }
    }


}
