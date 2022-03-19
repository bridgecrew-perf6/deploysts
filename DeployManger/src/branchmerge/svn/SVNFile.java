package branchmerge.svn;

import java.io.UnsupportedEncodingException;

public class SVNFile {

    byte data[];
    long revision;

    public SVNFile(long revision, byte byteArray[])
    {
        this.revision = revision;
        data = byteArray;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte data[])
    {
        this.data = data;
    }

    public long getRevision()
    {
        return revision;
    }

    public void setRevision(long revision)
    {
        this.revision = revision;
    }

    public String getText(String enc)
    {
        if(data == null)
            return null;
        try {
			return new String(data, enc);
		} catch (UnsupportedEncodingException e) {
			return new String(data);
		}
    }

}
