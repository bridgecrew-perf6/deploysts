package app.svn.mycommits;

import java.io.Serializable;

public class SVNLogEntryPath
  implements Serializable
{
  public String copyPath;
  public long copyRevision;
  public String kind;
  public String path;
  public char type;

  public SVNLogEntryPath(String copyPath, long copuRevision, String path, char type, String kind)
  {
    this.copyPath = copyPath;
    this.kind = kind;
    this.copyRevision = this.copyRevision;
    this.path = path;
    this.type = type;
  }

  public SVNLogEntryPath(String copyPath, long copuRevision, String path, char type)
  {
    this.copyPath = copyPath;
    this.copyRevision = this.copyRevision;
    this.path = path;
    this.type = type;
  }
}