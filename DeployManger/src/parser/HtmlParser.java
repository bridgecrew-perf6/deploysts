package parser;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.w3c.tidy.Tidy;


public class HtmlParser {

    public static void main(String[] args) {

        // TODO Auto-generated method stub
        String deployUpfileList = "C:\\work\\workspace\\03_Front\\WebContent\\WEB-INF\\jsp\\test.0520.jsp";
        InputStream is = null;
        try {
            is = new FileInputStream(deployUpfileList);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Tidy tidy = new Tidy();

        String htmlData = "<html><head></head><body><div>Hello Java><c:if><c:out></body></html>";

        tidy.setInputEncoding("UTF8");
        tidy.setOutputEncoding("UTF8");
        tidy.setQuiet(false);
        tidy.setShowWarnings(false);

        // InputStream stream = new ByteArrayInputStream(htmlData.getBytes());
        tidy.parse(is, System.out);
        // tidy.parseDOM(is, System.out);

        // tidy.getOnlyErrors();

        System.out.println("tidy.getParseErrors() ::" + tidy.getParseErrors());
    }
}
