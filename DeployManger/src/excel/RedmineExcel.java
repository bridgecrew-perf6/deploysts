package excel;


import lombok.Data;

@Data
public class RedmineExcel {

    private static final long serialVersionUID = 664161851363825838L;

    private int itemId; // 일감번호
    private String deployType; // 유형
    private String status; // 상태
    private String subject; // 제목
    private String manager; // 담당자
    private String pl; // PL
    private String workRoll; // 업무구분
    private String deploySystem; // 대상시스템
    private String deployFiles; // 배포파일
    private String deployDate; // 배포희망일자
    private String lastDeployServer; // 최종배포서버
    private int boWorkReqNo; // BO요청번호
    private String description; // 설명
}
