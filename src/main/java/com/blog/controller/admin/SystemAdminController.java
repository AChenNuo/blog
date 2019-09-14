package com.blog.controller.admin;

import com.blog.entity.Blog;
import com.blog.entity.BlogType;
import com.blog.entity.Blogger;
import com.blog.entity.Link;
import com.blog.service.BlogService;
import com.blog.service.BlogTypeService;
import com.blog.service.BloggerService;
import com.blog.service.LinkService;
import com.blog.util.Const;
import net.sf.json.JSONObject;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/admin/system"})
public class SystemAdminController {


    @Resource
    private BlogTypeService blogTypeService;
    @Resource
    private BloggerService bloggerService;
    @Resource
    private BlogService blogService;
    @Resource
    private LinkService linkService;


    /**属性系统缓存，作用一：因为下拉框是在系统启动的时候加载在servletContext里面的，如果增加下拉框项，就得重新启动项目，所以这里采用刷新系统缓存来达到重新加载下拉框的作用*/
    @ResponseBody
    @RequestMapping({"/refresh"})
    public JSONObject refreshSystem(HttpServletRequest request){

        ServletContext servletContext = request.getServletContext();

        //博客类别
        List<BlogType> blogTypes = blogTypeService.countList();
//        ServletContext servletContext = RequestContextUtils.getWebApplicationContext(request).getServletContext();
//        servletContext.setAttribute(Const.BLOG_TYPE_COUNT_LIST,blogTypes);
        servletContext.setAttribute(Const.BLOG_TYPE_COUNT_LIST,blogTypes);

        //博主信息(非登录用户，无需每次都查博主信息，所以直接项目启动就存储在项目中，这样就不用每次查表了)
        Blogger blogger = bloggerService.find();
        blogger.setPassword(null);
        servletContext.setAttribute(Const.BLOGGER,blogger);

        //按年月分类的博客数量
        List<Blog> blogCountList = blogService.countList();
        servletContext.setAttribute(Const.BLOG_COUNT_LIST,blogCountList);

        //友情链接
        List<Link> linkList = linkService.list(null);
        servletContext.setAttribute(Const.LINK_LIST,linkList);

        JSONObject result = new JSONObject();
        result.put("success",true);
        return result;
    }

}
