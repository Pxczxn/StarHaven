package top.pxczxn.common.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResultTest {

    @Test
    void okUsesZeroCode() {
        Result<String> result = Result.ok("ok");
        assertEquals(0, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("ok", result.getData());
    }

    @Test
    void failKeepsBusinessCode() {
        Result<Void> result = Result.fail(40001, "err");
        assertEquals(40001, result.getCode());
        assertEquals("err", result.getMessage());
        assertNull(result.getData());
    }
}
