package com.blackcard.logan.collector;

import com.blankj.utilcode.util.StringUtils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        assertArrayEquals(
                new byte[]{ 2, 1, 6, 22, -1, 67, 68, 2, 21, 86, 3, 1, 2, 2, 0, 97, -101, 35, -108, -81, 50, 0, 1, 28, 72, -56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                , new byte[]{ 2, 1, 6, 22, -1, 67, 68, 2, 21, 86, 3, 1, 2, 2, 0, 97, -101, 35, -108, -81, 50, 0, 1, 28, 72, -56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                );
        assertTrue(StringUtils.isEmpty(""));
    }
}