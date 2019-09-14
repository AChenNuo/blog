package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.lucene.BlogIndex;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import com.blog.util.StringUtil;
import org.apache.lucene.search.NumericRangeQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/blog"})
public class BlogController {

    @Resource
    private BlogService blogService;
    @Resource
    private CommentService commentService;

    private BlogIndex blogIndex = new BlogIndex();

    //通过路径中的id划分不同的页面,该id不是普通参数，而是包含在路径中的信息。
    @RequestMapping({"/articles/{id}"})
    public String details(@PathVariable("id")Integer id, HttpServletRequest request, Model model){

        System.out.println("博客页面进来了");
        Blog blog = blogService.findById(id);

        //阅读次数/点击次数+1
        blog.setClickHit(blog.getClickHit() + 1);
        blogService.update(blog);

        model.addAttribute("blog",blog);

        model.addAttribute("mainPage","foreground/blog/view.jsp");
        model.addAttribute("pageTitle",blog.getTitle() + "_个人博客系统");

        //上一篇，下一篇的访问请求路径存储
        model.addAttribute("pageCode",genUpAndDownPageCode(blogService.getLastBlog(id),blogService.getNextBlog(id),request.getContextPath()));

        //查询评论
        Map<String,Object> map = new HashMap<>();
//        map.put("blogId",blog.getId());
        map.put("blog",blog);
        map.put("state",1);
        model.addAttribute("commentList", commentService.list(map));

        //处理关键字，把关键字拆分放入具体博客页面的域中
        String keyWord = blog.getKeyWord();
        if (StringUtil.isEmpty(keyWord)){
            model.addAttribute("keyWords",null);
        }else{
            String[] arr = keyWord.split("\\s+");
            model.addAttribute("keyWords", Arrays.asList(arr));
        }

        return "index";
    }

    /**
     * 上一篇，下一篇
     */
    private String genUpAndDownPageCode(Blog lastBlog,Blog nextBlog,String projectContext){
        StringBuffer pageCode = new StringBuffer();
        if (lastBlog == null || lastBlog.getId() == null){
            pageCode.append("<p>上一篇：没有了</p>");
        }else {
            pageCode.append("<p>上一篇：<a href='" + projectContext + "/blog/articles/" + lastBlog.getId() + ".do'>" + lastBlog.getTitle() + "</a></p>");
        }
        if (nextBlog == null || nextBlog.getId() == null){
            pageCode.append("<p>下一篇：没有了</p>");
        }else {
            pageCode.append("<p>下一篇：<a href='" + projectContext + "/blog/articles/" + nextBlog.getId() + ".do'>" + nextBlog.getTitle() + "</a></p>");
        }

        return pageCode.toString();
    }



    //根据关键字查询
    @RequestMapping({"/q"})
    public String q(@RequestParam(value = "q",required = true)String q,
                    @RequestParam(value = "page",required = false)String page,
                    HttpServletRequest request,Model model) throws Exception {

        if (StringUtil.isEmpty(page)){
            page = "1";
        }

        model.addAttribute("mainPage","foreground/blog/result.jsp");

        //在Lucene中查询
        List<Blog> blogList = blogIndex.searchBlog(q.trim());
        int toIndex = 0;
        if (blogList.size()>=Integer.parseInt(page)*10){
            toIndex = Integer.parseInt(page) * 10;
        }else {
            toIndex = blogList.size();
        }
        model.addAttribute("blogList",blogList.subList((Integer.parseInt(page)-1)*10,toIndex));

        model.addAttribute("pageCode",genUpAndDownPageCode(Integer.parseInt(page),blogList.size(),q,10,request.getContextPath()));
        model.addAttribute("q",q);
        model.addAttribute("resultTotal",blogList.size());
        model.addAttribute("pageTitle","搜索关键字'" + q + "'结果页面_个人博客");
        model.addAttribute("blogList",blogList);

        return "index";
    }


    /**
     * 查询结果的翻页功能
     * */
    private String genUpAndDownPageCode(int page,int totalNum,String q,int pageSize,String projectContext){
        StringBuffer pageCode = new StringBuffer();
        //总共页数
        int totalPage = totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
        if (totalPage == 0){
            return "";
        }
        pageCode.append("<nav>");
        pageCode.append("<ur class='pager' >");
        //如果当前页大于1
        if (page > 1){
            pageCode.append("<li><a href='" + projectContext + "/blog/q.do?page=" + (page - 1) + "&q=" + q + "'>上一页</a></li>");
        }else {
            //如果当前页是第一页
            pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
        }
        if (page < totalPage){
            //如果不是最后一页
            pageCode.append("<li><a href='" + projectContext + "/blog/q.do?page=" + (page + 1) + "&q=" + q + "'>下一页</a></li>");
        }else {
            pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
        }

        pageCode.append("</nav>");

        return pageCode.toString();
    }
}
