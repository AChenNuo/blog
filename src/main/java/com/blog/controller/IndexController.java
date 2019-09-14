package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.entity.PageBean;
import com.blog.service.BlogService;
import com.blog.service.BloggerService;
import com.blog.util.PageUtil;
import com.blog.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Resource
    private BlogService blogService;

    @RequestMapping(value = {"/index.do"})
    public String index(Model model, @RequestParam(value = "page",required = false)String page,
                        @RequestParam(value = "typeId",required = false)String typeId,
                        @RequestParam(value = "releaseDateStr",required = false)String releaseDateStr,
                        HttpServletRequest request){
//        ModelAndView model = new ModelAndView();,ModelAndView作为返回值
        model.addAttribute("title","哈哈哈哈");
        if (StringUtil.isEmpty(page)){
            page = "1";
        }
        PageBean pageBean = new PageBean(Integer.parseInt(page),10);
        Map<String, Object> map = new HashMap<>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("typeId",typeId);
        map.put("releaseDateStr",releaseDateStr);


        List<Blog> list = blogService.list(map);

        StringBuffer param = new StringBuffer();
        //根据博客类别查询，作为分页的参数
        if (StringUtil.isNotEmpty(typeId)){
            param.append("typeId=" + typeId + "&");
        }
        //根据日期查询，作为分页的参数
        if (StringUtil.isNotEmpty(releaseDateStr)){
            param.append("releaseDateStr=" + releaseDateStr + "&");
        }

        System.out.println(request.getContextPath());
        //因为param是stringBuffer,所以变为String,得toString一下的
        String pageCode = PageUtil.genPagination(request.getContextPath() + "/index.do",Integer.parseInt(page),blogService.getTotal(map),10,param.toString());

        model.addAttribute("mainPage","foreground/blog/list.jsp");
        model.addAttribute("blogList",list);
        model.addAttribute("pageCode",pageCode);

        return "index";
    }


}
