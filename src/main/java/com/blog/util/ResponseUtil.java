package com.blog.util;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 写入response的工具类
 */
public class ResponseUtil {

    //输出json格式的字符串，类似于@ResponseBody的作用
    public static void write(HttpServletResponse response,Object o) throws Exception {
       //text/html是响应头，utf-8是字符流编码格式----输出内容用字符流，下载内容用字节流
        response.setContentType("text/html;charset=utf-8");
        //获取字符流
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        System.out.println(o.toString());
        out.flush();
        out.close();
    }
}
