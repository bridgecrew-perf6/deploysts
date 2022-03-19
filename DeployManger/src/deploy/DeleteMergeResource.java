package deploy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.tmatesoft.svn.core.SVNException;

import utils.Properties;
import branchmerge.svn.Svn;

public class DeleteMergeResource {

    static final String DEPLOY_DIR = "/data/home/hisis/ec/tools/deploy/";
    static final String DEPLOY_DEL_LIST = "/data/home/hisis/ec/tools/deploy/deploy_del.txt";

    private static Svn svn = null;
    protected static Properties sp = Properties.getInstance("config.properties");



    public DeleteMergeResource(String branch, String deletePath) {

        String SVN_URL = sp.getProperty("SVN_URL");
        String SVN_ID = sp.getProperty("SVN_ID");
        String SVN_PASSWD = sp.getProperty("SVN_PASSWD");

        try {
            Svn svn = new Svn(SVN_URL, SVN_ID, SVN_PASSWD);

            DelBranchFile(branch, svn);
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static void DelBranchFile(String branch, Svn svn) {

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String deployDelfileList = DEPLOY_DEL_LIST;
        try {

            File f = new File(deployDelfileList);

            if (f.exists()) {

                String str;
                String[] strArr;
                String deployStr; // deployStr

                is = new FileInputStream(deployDelfileList);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);


                System.out.println("============================================================================================");
                System.out.println("# Deploy Del File List ");
                System.out.println("");

                while ((str = br.readLine()) != null) {
                    strArr = str.split(":");

                    if (strArr.length > 1 && strArr[0] != null) {
                        deployStr = strArr[0];
                        // System.out.println("deployStr p1:::");
                    } else {

                        strArr = str.split(" ");
                        if (strArr.length > 2 && strArr[1] != null) {
                            deployStr = strArr[1];
                            // System.out.println("deployStr p2:::");
                        } else {
                            deployStr = str;
                            // System.out.println("deployStr p3:::");
                        }

                    }

                    System.out.println(" " + deployStr);

                    deployStr = deployStr.replace("/trunk", "");
                    deployStr = deployStr.replace("{", "");
                    deployStr = deployStr.replace("\"", "");
                    deployStr = deployStr.trim();

                    svn.delete(branch + "/" + deployStr);
                }// while_end
                System.out.println("");
                System.out.println("============================================================================================");



            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }



}
