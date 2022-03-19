package app.svn.mycommits;

public class Between
{
  long start;
  long end;

  public Between(long start, long end)
  {
    this.start = start;
    this.end = end;
  }

  public long getStart() {
    return this.start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return this.end;
  }

  public void setEnd(long end) {
    this.end = end;
  }
}