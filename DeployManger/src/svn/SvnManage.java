package svn;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import utils.DateUtils;

@Slf4j
@Data
public class SvnManage {

    static final String SVN_MNG_TMP_DIR = "/data/home/hisis/ec/tools/deploy/temp";


    public SvnManage(String runType) {

        if (runType.equals("taglistdel")) {
            deleteSvnTagList();
        }
    }


    /**
     * SVN TAG 목록중 7일전 목록 삭제
     */
    public static void deleteSvnTagList() {

        // TAG목록 파일 저장
        String svnTagListPath = SVN_MNG_TMP_DIR + "/svn_tag_list.txt";
        // String command = "svn list --password LotteTa svn://10.154.17.205/ehimart/tags > " + svnTagListPath;
        String command = "svn list --password LotteTa svn://10.154.17.205/ehimart/tags";
        List<String> svnTagList = commandExecBySvnList(command);

        System.out.println("command :::" + command);
        System.out.println("svnTagListPath :::" + svnTagListPath);

        // 저장된 TAG목록 중 7일전 목록 모두 삭제
        if (svnTagList != null && svnTagList.size() > 0) {
            for (int i = 0; i < svnTagList.size(); i++) {
                String tag = svnTagList.get(i);
                command = "svn delete --password LotteTa svn://10.154.17.205/ehimart/tags/" + tag + " --message 'Deleting'";
                commandExec(command);

                log.info("tag :: " + tag);
            }
        }
    }


    private static List<String> commandExecBySvnList(String command) {

        // 5일전 날짜 계산
        String targetDate = DateUtils.getNextDate(-7, "yyyyMMdd");

        log.info("getSvnTagList targetDate ::" + targetDate);

        List<String> svnTagList = new ArrayList<String>();

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 정상적 출력

            System.out.println("Process ===");
            String line = "";
            while (line != null) {
                System.out.println(line);

                log.info("line :: " + line);
                if (StringUtils.isNotEmpty(line) && line.length() > 10) {
                    String compTags = line.substring(0, 10);
                    log.info("compTags :: " + compTags);
                    long diffOfDate = DateUtils.diffOfDate(compTags.replace("-", ""), targetDate);

                    log.info("diffOfDate :: " + diffOfDate);

                    if (diffOfDate > 0) {
                        svnTagList.add(line);
                    }
                }

                line = reader.readLine();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return svnTagList;

    }


    private static void commandExec(String command) {

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);

            // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 정상적 출력
            //
            // System.out.println("Process ===");
            // String line = "";
            // while (line != null) {
            // System.out.println(line);
            // line = reader.readLine();
            // }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 컴파일 결과 확인 재귀호출
     */
    public static List<String> getSvnTagListRun(String svnTagListPath) {

        return getSvnTagList(svnTagListPath);
    }


    /**
     * 컴파일 결과 확인
     */
    public static List<String> getSvnTagList(String svnTagListPath) {

        // 7일전 날짜 계산
        String targetDate = DateUtils.getNextDate(-7, "yyyyMMdd");

        log.info("getSvnTagList targetDate ::" + targetDate);

        List<String> svnTagList = null;

        File f = new File(svnTagListPath);

        Reader reader = null;
        InputStream is = null;
        BufferedReader br = null;

        String str;

        if (f.exists()) {

            log.info("f.exists() :: ");

            svnTagList = new ArrayList<String>();

            try {
                is = new FileInputStream(svnTagListPath);
                reader = new InputStreamReader(is, "utf-8");
                br = new BufferedReader(reader);

                while ((str = br.readLine()) != null) {
                    log.info("str :: " + str);
                    String compTags = str.substring(0, 10);
                    log.info("compTags :: " + compTags);
                    long diffOfDate = DateUtils.diffOfDate(compTags.replace("-", ""), targetDate);

                    log.info("str :: " + str);
                    log.info("diffOfDate :: " + diffOfDate);

                    if (diffOfDate >= 0) {
                        svnTagList.add(str);
                    } else {
                        continue;
                    }

                }// while_end


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
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

        } // f.exists if_end

        if (svnTagList == null || svnTagList.size() == 0) {
            // return getSvnTagList(svnTagListPath);
        }

        return svnTagList;
    }

}
