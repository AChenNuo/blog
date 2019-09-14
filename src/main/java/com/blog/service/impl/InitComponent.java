package com.blog.service.impl;

import com.blog.entity.Blog;
import com.blog.entity.BlogType;
import com.blog.entity.Blogger;
import com.blog.entity.Link;
import com.blog.service.BlogService;
import com.blog.service.BlogTypeService;
import com.blog.service.BloggerService;
import com.blog.service.LinkService;
import com.blog.util.Const;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

@Component
public class InitComponent implements ServletContextListener, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        //博客类别，项目启动后存储---如果要实现刷新缓存，可以获取servletContext缓存，然后更改里面内容。
        BlogTypeService blogTypeService = (BlogTypeService)applicationContext.getBean("blogTypeService");
        List<BlogType> blogTypes = blogTypeService.countList();
        servletContext.setAttribute(Const.BLOG_TYPE_COUNT_LIST,blogTypes);

        //博主信息(非登录用户，无需每次都查博主信息，所以直接项目启动就存储在项目中，这样就不用每次查表了)
        BloggerService bloggerService = (BloggerService)applicationContext.getBean("bloggerService");
        Blogger blogger = bloggerService.find();
        blogger.setPassword(null);
        servletContext.setAttribute(Const.BLOGGER,blogger);

        //按年月分类的博客数量
        BlogService blogService = (BlogService) applicationContext.getBean("blogService");
        List<Blog> blogCountList = blogService.countList();
        servletContext.setAttribute(Const.BLOG_COUNT_LIST,blogCountList);

        //友情链接
        LinkService linkService = (LinkService)applicationContext.getBean("linkService");
        List<Link> linkList = linkService.list(null);
        servletContext.setAttribute(Const.LINK_LIST,linkList);


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
