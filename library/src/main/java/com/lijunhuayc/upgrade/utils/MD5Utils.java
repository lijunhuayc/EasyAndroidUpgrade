package com.lijunhuayc.upgrade.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Desc:
 * Created by ${junhua.li} on 2017/02/08 13:08.
 * Email: lijunhuayc@sina.com
 */
public class MD5Utils {
    public static String MD5EncodeFile(File filename) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte tmp[] = MD5EncodeFileByte(filename);
        char str[] = new char[16 * 2];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        s = new String(str);
        return s;
    }

    private static byte[] MD5EncodeFileByte(File filename) {
        if (filename != null) {
            int i;
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
            byte[] data = new byte[4096];
            FileInputStream fis;
            try {
                fis = new FileInputStream(filename);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
                return null;
            }
            while (true) {
                try {
                    i = fis.read(data);
                    if (i != -1) {
                        md.update(data, 0, i);
                    } else {
                        fis.close();
                        return md.digest();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        fis.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return null;
                }
            }
        }
        return null;
    }

}
