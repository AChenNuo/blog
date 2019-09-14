package com.blog.controller.admin;

import com.blog.entity.Blogger;
import com.blog.service.BloggerService;
import com.blog.util.Const;
import com.blog.util.CryptographyUtil;
import com.blog.util.DateUtil;
import com.blog.util.ResponseUtil;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;



@Controller
@RequestMapping({"/admin/blogger"})
public class BloggerAdminController {

    @Resource
    private BloggerService bloggerService;

    @RequestMapping({"/save"})
    public String save(@RequestParam("imageFile")MultipartFile imageFile, Blogger blogger, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!imageFile.isEmpty()){
            //先获取当前项目的路径
            //图片存放地址声明
            String filePath = request.getServletContext().getRealPath("/");
            System.out.println(filePath);//D:\IDEA_Test\Blog\target\Blog\
            System.out.println(request.getServletContext().getRealPath(""));//D:\IDEA_Test\Blog\target\Blog\
            System.out.println(request.getServletContext().getContextPath());
            //图片名字声明，getOriginalFilename得到上传的图片的全名称，然后split取后缀，重命名图片名字为时间类型的，这样不会重复。
            String imageName = DateUtil.getCurrentDateStr() + "." + imageFile.getOriginalFilename().split("\\.")[1];
            //正式把图片存储在哪个地方的代码执行
            imageFile.transferTo(new File(filePath + "static/userImages/" + imageName));
            //数据库中只存储文件名
            blogger.setImageName(imageName);

        }

        Integer resultTotal = bloggerService.update(blogger);
        StringBuffer result = new StringBuffer();
        if (resultTotal > 0){
            result.append("<script language='javascript'>alert('修改成功');</script>");
            //登录进来的个人信息首先是从数据库中查出来的（Realm中），并且放入到了session中，
            // 然后修改个人信息后，会从前台提交数据给后台，这时候可以不用再去查数据库了，因为修改的数据，就是最终形成的数据，
            //所以在修改数据库成功之后，将前台传来的blogger数据直接赋值到session中就可以了，不需要再查数据库了。
            SecurityUtils.getSubject().getSession().setAttribute(Const.CURRENT_USER,blogger);
        }else {
            result.append("<script language='javascript'>alert('修改失败');</script>");
        }
        ResponseUtil.write(response,result);
        System.out.println("进来了");
        return null;
    }

    //获取博主的json格式,富文本编辑器需要，因为传入数据库的值，在从后台传到前台的过程中发生了变化，所以要以json格式来传输。
    //前台通过json格式来获取数据
    @RequestMapping({"/find"})
    @ResponseBody
    public JSONObject find(){
        Blogger blogger = (Blogger) SecurityUtils.getSubject().getSession().getAttribute(Const.CURRENT_USER);
        JSONObject jsonObject = JSONObject.fromObject(blogger);
        return jsonObject;
    }

    //修改密码
    @RequestMapping({"/modifyPassword"})
    @ResponseBody
    public JSONObject modifyPassword(@RequestParam("id")String id,@RequestParam("newPassword")String newPassword){

        Blogger blogger = new Blogger();
        blogger.setId(Integer.parseInt(id));
        blogger.setPassword(CryptographyUtil.md5(newPassword, "java1234"));
        Integer resultTotal = bloggerService.update(blogger);
        JSONObject result = new JSONObject();
        if (resultTotal > 0){
            result.put("success",Boolean.TRUE);
        }else {
            result.put("success",Boolean.FALSE);
        }
        return result;
    }

    //安全退出
    @RequestMapping({"/logout"})
    public String logout(){
        //shiro自带的退出功能，会帮我们自动清除session的，而login的方法，需要我们自己定义realm类进行实现，以及定义登录权限认证的配置。
        SecurityUtils.getSubject().logout();
        return "redirect:/login.jsp";
    }
}


