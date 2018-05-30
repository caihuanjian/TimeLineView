package com.rain.timelineview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        ThreadTest test = new ThreadTest();
        test.test();
        ChineseSubStringTest chineseSubStringTest = new ChineseSubStringTest();
        String sub = chineseSubStringTest.cutChinese("我ABC汉", 6);
        System.out.println(sub);
        String sub1 = chineseSubStringTest.cutChinese("hhgj含含糊糊哈哈哈哈哈啊啊啊啊哈哈哈", 20);
        System.out.println(sub1);
    }
}