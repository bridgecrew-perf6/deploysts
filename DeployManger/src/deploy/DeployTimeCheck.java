package deploy;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.DateUtils;

@Slf4j
@Data
public class DeployTimeCheck {


    public DeployTimeCheck(String adminCheck) {

        // System.out.println("adminCheck ::" + adminCheck);

        // if (adminCheck.equals("admin") || adminCheck.equals("qcuser")) {
        // System.out.println("권한이 있는 사용자입니다.");
        // } else {
        String todateTime = DateUtils.getDate("HH");
        int intTime = Integer.parseInt(todateTime);

        // System.out.println("intTime ::" + intTime);

        int day = DateUtils.getDayOfTheWeek();
        // System.out.println("day ::" + day);

        // if (day == 2 || day == 4) {
        if (14 <= intTime && intTime < 17) {
            throw new RuntimeException("\n테스트기 배포 중지 시간 입니다.\n [오후 2시∼오후 5시] \n 배포 필요시 QC담당자에게 요청 하시기 바랍니다.");
        }
        // }
        // }
    }

}
