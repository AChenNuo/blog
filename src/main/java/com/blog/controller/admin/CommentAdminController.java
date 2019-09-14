package com.blog.controller.admin;

import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.PageBean;
import com.blog.service.CommentService;
import com.blog.util.DateJsonValueProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/admin/comment"})
public class CommentAdminController {

    @Resource
    private CommentService commentService;

    //查询评论列表
    @RequestMapping({"/list"})
    @ResponseBody
    public JSONObject list(@RequestParam(value = "page",required = false)String page, @RequestParam(value = "rows",required = false)String rows, @RequestParam(value = "state",required = false)String state, Blog blog){

        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        if (state != null){
            map.put("state",Integer.parseInt(state));
        }else {
            map.put("state",state);
        }
        map.put("blog",blog);
        Long total = commentService.getTotal(map);
        List<Comment> list = commentService.list(map);
        JSONObject result = new JSONObject();
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class,new DateJsonValueProcessor("yyyy-MM-dd"));
        JSONArray jsonArray = JSONArray.fromObject(list, config);

        result.put("rows",jsonArray);
        result.put("total",total);
        return result;
    }

    //删除评论
    //查询评论列表
    @RequestMapping({"/delete"})
    @ResponseBody
    public JSONObject delete(@RequestParam("ids")String ids){
        String[] idStr = ids.split(",");
        for (int i=0; i<idStr.length;i++){
            commentService.delete(Integer.parseInt(idStr[i]));
        }
        JSONObject result = new JSONObject();
        result.put("success",true);
        return result;
    }


    //审核评论
    @RequestMapping({"/review"})
    @ResponseBody
    public JSONObject review(@RequestParam("ids")String ids,@RequestParam("state")String state){
        String[] idStr = ids.split(",");
        for (int i=0; i<idStr.length;i++){
            Comment comment = new Comment();
            comment.setId(Integer.parseInt(idStr[i]));
            comment.setState(Integer.parseInt(state));
            commentService.update(comment);
        }
        JSONObject result = new JSONObject();
        result.put("success",true);
        return result;
    }
}
