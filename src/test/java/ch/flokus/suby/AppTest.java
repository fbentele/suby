package ch.flokus.suby;

import junit.framework.TestCase;
import ch.flokus.suby.service.AppUtils;

public class AppTest extends TestCase {

    public void testAppUtilsReplacer() {
        String s = "Premiers sympt&#244;mes";
        String exp = "Premiers symptômes";
        String cleaned = AppUtils.replaceAsciiChars(s);
        assertEquals(exp, cleaned);
    }

    public void testAppUtilsHex() {
        String s = "mySuperPassword";
        String exp = "00000000006d79537570657250617373776f7264";
        String hex = AppUtils.toHex(s);
        assertEquals(exp, hex);
    }
}
