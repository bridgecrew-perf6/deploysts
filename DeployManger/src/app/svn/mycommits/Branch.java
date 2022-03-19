package app.svn.mycommits;

public class Branch
{
  String name;
  String path;

  public Branch()
  {
  }

  public Branch(String entryName)
  {
    setPath("/branches/" + entryName);
    setName(entryName);
  }

  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}