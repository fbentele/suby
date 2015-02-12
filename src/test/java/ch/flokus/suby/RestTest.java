package ch.flokus.suby;

import junit.framework.TestCase;
import ch.flokus.suby.rest.Status;

public class RestTest extends TestCase {
    public void testConnection() {
        Status status = Status.getInstance();
        String res = status.testConnection();
        assertNotNull(res);
    }
}
