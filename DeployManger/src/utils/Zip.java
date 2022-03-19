package utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
public class Zip {

    static List<String> filesListInDir = new ArrayList<String>();


    public static void zipDirectory(File dir, String zipDirName) {

        try {
            populateFilesList(dir);
            // now zip files one by one
            // create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                System.out.println("Zipping " + filePath);
                // for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                // read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Zipping  Fail! ~~~~");
        }
    }


    /**
     * This method populates all the files in a directory to a List
     * 
     * @param dir
     * @throws IOException
     */
    public static void populateFilesList(File dir) throws IOException {

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                filesListInDir.add(file.getAbsolutePath());
            } else {
                populateFilesList(file);
            }
        }
    }


    public static void main(String[] args) {

        try {
            // File file = new File("c:\\deploy/waslog/temp/front/");
            // Zip.zipDirectory(file, "c:\\deploy/waslog/front.zip");

            List<String> testList = new ArrayList<String>();
            testList.add("1");
            testList.add("2");
            testList.add("3");
            testList.add("4");
            testList.add("5");

            System.out.println("Initial ArrayList:\n" + testList);
            // Add elements without running into any error
            testList.add("6");
            testList.add("7");

            // Print the list after adding elements
            System.out.println("Modified ArrayList:\n" + testList);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
