package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.HttpUrlConnectionCommon;

@Slf4j
@Data
public class CurlUrlCallThread implements Runnable {

    static final String PRE_COMPILE_SHELL_COMMNAD = "sh /home/hisisJ/precompile_jeus.sh";

    Properties props = null;
    String targetSystemFtp = null;


    @Override
    public void run() {

        System.out.println("==============================================");
        System.out.println("PreCompileThread START");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("==============================================");

        HttpUrlConnectionCommon.curlHttpUrlCall(props, targetSystemFtp);

        System.out.println("==============================================");
        System.out.println("ThreadName =" + Thread.currentThread().getName());
        System.out.println("targetSystemFtp =" + targetSystemFtp);
        System.out.println("PreCompileThread END");
        System.out.println("==============================================");

    }


    public CurlUrlCallThread(Properties props, String targetSystemFtp) {

        this.props = props;
        this.targetSystemFtp = targetSystemFtp;

    }



}
