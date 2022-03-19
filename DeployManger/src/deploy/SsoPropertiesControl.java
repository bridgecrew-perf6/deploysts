package deploy;


import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.SFTPService;

@Slf4j
@Data
public class SsoPropertiesControl {

    static final String SSO_PROPERTIES_SHELL = "sh /home/hisisJ/cp_tst_sso_properties.sh  ";


    public SsoPropertiesControl(Properties props, String targetSystem) {

        SFTPService ftp = null;

        String targetFtpIp = "";
        int targetFtpPort = 22;
        String targetFtpId = "";
        String targetFtpPw = "";

        targetFtpIp = props.getProperty(targetSystem + "_TST.ftp.ip");
        targetFtpId = props.getProperty(targetSystem + "_TST.ftp.id");
        targetFtpPw = props.getProperty(targetSystem + "_TST.ftp.pw");

        ftp = new SFTPService(targetFtpIp, targetFtpPort, targetFtpId, targetFtpPw);

        System.out.println("SsoPropertiesControl :: [" + targetSystem + "]");
        ftp.sshCommandExecByShellScript(SSO_PROPERTIES_SHELL);
        System.out.println("SSO_PROPERTIES_SHELL END:: [" + SSO_PROPERTIES_SHELL + "]");

    }

}
