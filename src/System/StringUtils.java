/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package System;

/**
 * Random string utilities
 */
public class StringUtils {

    /**
     * Count number of time subStr occurs in str.
     * @param cstr
     * @param subStr
     * @return count.
     */
    public static int subStrCount(String str, String subStr) {
        int n=0;
        int p=str.indexOf(subStr);
        while (p>=0) {
            ++n;
            p = str.indexOf(subStr, p+subStr.length());
        }
        return n;
    }

    /*
     * Replace oldStr with newStr in s
     */
    public static String replace(String s, String oldStr, String newStr) {
        int count = s.indexOf(oldStr);
        if (count < 0) return s;
        return s.substring(0, count) + newStr +
                s.substring(count+oldStr.length());
    }

    /**
     * Replace all occurances of oldStr in str with newStr.
     * @param str
     * @param oldStr
     * @param newStr
     * @return string with replaced substrings.
     */
    public static String replaceAll(String string, String oldS, String newS) {
        String os = "";
        int p = string.indexOf(oldS);
        int op = 0;
        while (p >= 0) {
            os += string.substring(op, p) + newS;
            op = p+oldS.length();
            p = string.indexOf(oldS, op);
        }
        os += string.substring(op);
        return os;
    }

    /**
     * Decode rot13 enciphered text. Text in []-brackets are considered decoded
     * @return decoded string.
     */
    public static String decodeHint(String s) {
        String res="";
        boolean decrypt=true;

        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if (c == '[') { decrypt = false; }
            else if (c == ']') { decrypt = true; }
            if (decrypt) {
                if (c >= 'a' && c <= 'm') { c += 13; }
                else if (c >= 'n' && c <= 'z') { c -= 13; }
                else if (c >= 'A' && c <= 'M') { c += 13; }
                else if (c >= 'N' && c <= 'Z') { c -= 13; }
            }
            res += c;
        }
        return res;
    }

    /**
     * Decode "&amp;" entities with its proper letter.
     * @param s: Input string
     * @return: decoded sting s
     */
    public static String XMLDecode(String s) {
        //fixme: not implemented
        System.out.println("XMLDecode not impl");
        return s;
    }

    /**
     * Decode "&aring;" etc. entities with its proper letter.
     * @param s: Input string
     * @return: decoded sting s
     */
    public static String HTMLDecode(String s) {
        //fixme: not implemented
        System.out.println("HTMLDecode not impl");
        return s;
    }

}
