package com.blog.util;

public class PageUtil {



    /**翻页方法
     * targetUrl:请求controller路径
     * currentPage：当前在第几页
     * totalNum：总共有多少条数据
     * pageSize：每页的数量
     * param：其他查询条件
     * */
    public static String genPagination(String targetUrl,long currentPage,long totalNum,int pageSize,String param){
        //总共页数
        if(totalNum == 0){
            return "未查询到数据";
        }
        //totalPage：总共有多少页
        long totalPage = 1;
        if (totalNum % pageSize == 0){
            totalPage = totalNum/pageSize;
        }else {
            totalPage = totalNum/pageSize + 1;
        }
        StringBuffer pageCode = new StringBuffer();
        pageCode.append("<li><a href='" + targetUrl + "?page=1&" + param + "'>首页</a></li>");
        //上一页
        if (currentPage > 1){
            //当前页不是第一页时，可以点击首页
            pageCode.append("<li><a href='" + targetUrl + "?page=" + (currentPage-1) + "&" + param + "'>上一页</a></li>");
        }else {
            //当前页是第一页，不可以点击首页
            pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
        }
        //显示页数
        for (int i = 1; i <= totalPage; i++){
            if (i == currentPage){
                pageCode.append("<li class='active'><a href='" + targetUrl + "?page=" + i + "&" + param + "'>" + i + "</a></li>");
            }else {
                pageCode.append("<li><a href='" + targetUrl + "?page=" + i + "&" + param + "'>" + i + "</a></li>");
            }
        }
        //下一页
        if (currentPage < totalPage){
            pageCode.append("<li><a href='" + targetUrl + "?page=" + (currentPage+1) + "&" + param + "'>下一页</a></li>");
        }else {
            pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
        }
        //尾页
        pageCode.append("<li><a href='" + targetUrl + "?page=" + totalPage + "&" + param + "'>尾页</a></li>");

//    <li><a href='/index.html?page=1&'>首页</a></li>
//    <li class='disabled'><a href='#'>上一页</a></li>
//    <li class='active'><a href='/index.html?page=1&'>1</a></li>
//    <li class='disabled'><a href='#'>下一页</a></li>
//    <li><a href='/index.html?page=1&'>尾页</a></li>

        return pageCode.toString();
    }
}