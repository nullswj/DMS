package com.example.dms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCheck {

    public static boolean checkPasswordStrength(String pw)
    {
        int len = pw.length();
        int strength = 0;
        char[] array = pw.toCharArray();
        for(int i = 0; i < len; i++)
        {
            if(array[i] <= 'Z' && array[i] >= 'A')
            {
                strength ++;
                break;
            }
        }
        for(int i = 0; i < len; i++)
        {
            if(array[i] <= 'z' && array[i] >= 'a')
            {
                strength ++;
                break;
            }
        }
        for(int i = 0; i < len; i++)
        {
            if(array[i] <= '9' && array[i] >= '0')
            {
                strength ++;
                break;
            }
        }
        for(int i = 0; i < len; i++)
        {
            if(array[i] == '_')
            {
                strength ++;
                break;
            }
        }
        if(strength >= 3 && len >= 8)
            return true;
        else
            return false;
    }
    /**
     * 方法名 checkTel
     * 功能 正则匹配手机号码
     * 参数 tel
     * 返回值 true OR false
     * */
    public static boolean checkTel(String tel)
    {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    public static boolean checkString(String user)
    {
        Pattern p = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
        Matcher matcher = p.matcher(user);
        return matcher.matches();
    }
}
