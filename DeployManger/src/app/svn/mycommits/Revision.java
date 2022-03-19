package app.svn.mycommits;

import java.text.SimpleDateFormat;
import org.tmatesoft.svn.core.SVNLogEntry;

public class Revision
{
  private SVNLogEntry svnLogEntry;

  public Revision(SVNLogEntry svnLogEntry)
  {
    this.svnLogEntry = svnLogEntry;
  }

  public String toString()
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd hh:mm");
    return String.format("%d (%s)", new Object[] { Long.valueOf(this.svnLogEntry.getRevision()), format.format(this.svnLogEntry.getDate()) }) + " " + this.svnLogEntry.getAuthor();
  }

  public SVNLogEntry getSvnLogEntry() {
    return this.svnLogEntry;
  }

  public void setSvnLogEntry(SVNLogEntry svnLogEntry) {
    this.svnLogEntry = svnLogEntry;
  }
}