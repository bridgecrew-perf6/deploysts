package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Properties extends java.util.Properties
{

    private static Properties instance = null;
    private static String PROPERTIES_NAME = null;


    public Properties(String propertiesName)
        throws IOException
    {
        PROPERTIES_NAME = propertiesName;
        init();
    }

    public Properties()
        throws IOException
    {
        init();
    }

    private void init()
        throws FileNotFoundException, IOException
    {
    	InputStream in = null;
    	try{
	        
	        String propPath = System.getProperty(PROPERTIES_NAME);
	        if(propPath != null)
	            in = new FileInputStream(new File(propPath));
	        else
	            in = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_NAME);
	        load(in);
        
    	} catch(IOException ioexception) { 
    		throw new IllegalArgumentException(ioexception);
    	} finally{
    		if(in != null){
                in.close();
    		}
    	}
        
    }

    public static synchronized Properties getInstance(String filename)
    {
        if(instance == null)
            try
            {
                instance = new Properties(filename);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        return instance;
    }

    public static String getPropertyUTF8(String propertyName)
    {
        String value = null;
        try
        {
            value = new String(instance.getProperty(propertyName).getBytes("ISO-8859-1"), "UTF8");
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return value;
    }

}

