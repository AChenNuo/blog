package com.blog.controller;

import com.blog.entity.Blogger;
import com.blog.service.BloggerService;
import com.blog.util.CryptographyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 博主登录相关
 */
@Controller
@RequestMapping("/blogger")
public class BloggerController {

    @Resource
    private BloggerService bloggerService;

//    因为我们用spring已经把.do给过滤掉了，所以这里只写/login就可以了，实际对应的请求路径是/logger/login.do
    @RequestMapping("/login")
    public String login(Blogger blogger, HttpServletRequest request){
        //因为form提交应用的是post方式，要把用户名和密码传过来，用对象封装的方式传，所以要把blogger这个bean给写出来
        //直接把bean作为参数写在方法参数里面，就可以直接将form中的值读取到，可以得到post传来的值了。
        //这里需要注意的是，form提交的input标签的name属性必须与实体类的属性名一致才可以获取到。

        /**用户名*/
        String userName = blogger.getUserName();
        /**密码*/
        String password = blogger.getPassword();
        String pw = CryptographyUtil.md5(password, "java1234");//加密之后为pw,pw为数据库存储的密码字符串

        /**Subject*/
        //使用SecurityUtils.getSubject()，我们可以得到当前正在执行的主题。
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, pw);
        try {
            //传递token给shiro的realm
            subject.login(token);
            return "redirect:/admin/main.jsp";
        }catch (Exception e){
            e.printStackTrace();
            request.setAttribute("blogger",blogger);
            request.setAttribute("errorInfo","用户名或者密码错误！");
        }
        return "login";
    }

    /**
     * 关于博主
     */
    @RequestMapping("/aboutMe")
    public String aboutMe(Model model){

        model.addAttribute("blogger",bloggerService.find());
        model.addAttribute("mainPage","foreground/blogger/info.jsp");
        model.addAttribute("pageTitle","关于博主_个人博客系统");
        return "index";
    }


}
