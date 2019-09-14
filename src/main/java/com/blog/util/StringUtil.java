package com.blog.util;

import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作字符串的工具类
 */
public class StringUtil {

    /**在字符串前后加%，提供模糊查询的like字符*/
    public static String formatLike(String str){
        if (isNotEmpty(str)){
            return "%" + str + "%";
        }
        return null;
    }

    /**判断字符串是否不为空*/
    public static boolean isNotEmpty(String str){
        if (str != null && !"".equals(str.trim())){
            return true;
        }else{
            return false;
        }
    }

    /**判断字符串是否为空*/
    public static boolean isEmpty(String str){
        if (str == null || "".equals(str.trim())){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     */
    public static List<String> filterWhite(List<String> list){
        List<String> resultList = new ArrayList<>();
        for (String l :list){
            if (isNotEmpty(l)){
                resultList.add(l);
            }
        }
        return resultList;
    }


    /**
     * java api中正则表达式里面的这个\s了
     * 而在使用中\s需要转义，也就是不能直接用split("\s")，而是split("\\s")
     * 同时如果不确定中间的空格到底是多少个的话，可以使用split("\\s+")
     */
    public static void main(String[] args) {
        System.out.println(Arrays.asList("a  b c d  dd  cc  ee e c d".split("\\s+")));
    }
}
