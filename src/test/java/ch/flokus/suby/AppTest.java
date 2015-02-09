package ch.flokus.suby;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        String s = "Premiers sympt&#244;mes";
        Normalizer.normalize(s, Normalizer.Form.NFD);
        System.out.println(s);
        String e;
        try {
            e = new String(s.getBytes("UTF-8"), "UTF-8");
            System.out.println(e);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            System.out.println(URLEncoder.encode(s, "UTF-8"));
            
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
