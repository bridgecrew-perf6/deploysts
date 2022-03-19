package parser;



public class MergeTest {

    public static void main(String[] args) {

        String path = "/02_Application/src/main/java/ehimart/app/domain/bo/ods/goods/model/OdsPrAlgnTgtGoodsTmpInfo.java"; // 970592

        String fromPath = "/tags/2020-03-10:10:30/";
        String toPath = "/branches/REL_20190628_ORG_BAK/";

        String aa = "2020/03/10";
        String bb = "20200310";

        String parseVal = ",2020/03/12,운영서버,,";

        String[] parseValArr = parseVal.split(",");
        System.out.println("parseValArr.length:: " + parseValArr.length);
        for (int i = 0; i < parseValArr.length; i++) {
            System.out.println("i:: " + i);
            System.out.println("" + parseValArr[i]);
        }

        System.out.println("aa.length() ::" + aa.length());
        System.out.println("isParsableToInt(bb)() ::" + isParsableToInt(bb));


        // try {
        // TagController.syncFromTo("svn://10.154.17.205/ehimart", "litters", "LotteTa", fromPath + path, toPath + path);
        // } catch (SVNException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }


    static boolean isParsableToInt(String i) {

        try {
            Integer.parseInt(i);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
