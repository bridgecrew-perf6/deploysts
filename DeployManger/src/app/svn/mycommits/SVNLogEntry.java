package app.svn.mycommits;

import java.util.ArrayList;
import java.util.Date;

public class SVNLogEntry
{
  public Date date;
  public String author;
  public long revision;
  public String message;
  public ArrayList<SVNLogEntryPath> paths;

  public SVNLogEntry(long revision, String author, Date date, ArrayList<SVNLogEntryPath> paths, String message)
  {
    this.date = date;
    this.revision = revision;
    this.author = author;
    this.paths = paths;
    this.message = message;
  }
}