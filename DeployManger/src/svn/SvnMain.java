package svn;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Data
public class SvnMain {


    public static void main(String[] args) {


        if (ArrayUtils.isEmpty(args)) {
            printUsage();
            return;
        }

        String runType = StringUtils.defaultString(args[0], "taglistdel");

        log.info("[Deploy [runType : {} >> START] ======[" + runType + "]");

        new SvnManage(runType);


        log.info("[Deploy [runType : {} >> END] ======[" + runType + "]");

    }



    private static void printUsage() {

        log.info("### Deploy RunType need argument");
    }

}
