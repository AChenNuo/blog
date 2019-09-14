package com.blog.controller;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.service.BlogService;
import com.blog.service.CommentService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/comment"})
public class CommentController {

    @Resource
    private CommentService commentService;
    @Resource
    private BlogService blogService;

    @RequestMapping({"/save"})
    @ResponseBody
    public JSONObject save(Comment comment, @RequestParam("imageCode")String imageCode, HttpServletRequest request){

        JSONObject result = new JSONObject();

        String imageCode1 = (String)request.getSession().getAttribute("sRand");
        if (!imageCode.equals(imageCode1)){
            result.put("errorInfo","验证码填写错误");
        }else {
//            String IP = "";
//            //得到本地的IP地址
//            try {
//                InetAddress address = InetAddress.getLocalHost();
//                IP = address.getHostAddress();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            String userIP = request.getRemoteAddr();
            comment.setUserIp(userIP);
            Integer resultTotal;
            if (comment.getBlog().getId() != null){
                resultTotal = commentService.add(comment);
                if (resultTotal  == 1){
                    //给对应的博客评论数加1
                    Blog blog = blogService.findById(comment.getBlog().getId());
                    blog.setReplyHit(blog.getReplyHit()+1);
                    blogService.updateToReplyHit(blog.getId(),blog.getReplyHit());
                    result.put("success",Boolean.valueOf(true));
                }else {
                    result.put("success",Boolean.valueOf(false));
                }
            }
        }
        return result;
    }
}
