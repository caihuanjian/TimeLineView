package com.rain.timelineview;

import java.nio.charset.Charset;

/**
 * Created by HwanJ.Choi on 2018-5-29.
 */

public class ChineseSubStringTest {
    String STRING_CHARSET_NAME = "GBK";
    Charset CHARSET = Charset.forName(STRING_CHARSET_NAME);

    public String cutChinese(String str, int length) {
        byte[] bytes = str.getBytes(CHARSET);
        if (length > bytes.length)
            return str;
        int count = 0;
        for (int i = 0; i < length; i++) {
            if (bytes[i] < 0) {
                i = i + 1;
                if (i < length)
                    count += 2;
            } else {
                count += 1;
            }
        }
        byte[] result = new byte[count];
        System.arraycopy(bytes, 0, result, 0, count);
        return new String(result,CHARSET);
    }
}
