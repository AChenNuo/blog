package com.blog.controller.admin;

import com.blog.entity.Blog;
import com.blog.entity.PageBean;
import com.blog.lucene.BlogIndex;
import com.blog.service.BlogService;
import com.blog.util.DateJsonValueProcessor;
import com.blog.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.action.PutAllAction;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客信息管理
 */
@Controller
@RequestMapping({"/admin/blog"})
public class BlogAdminController {
    @Resource
    private BlogService blogService;
    private BlogIndex blogIndex = new BlogIndex();


    /**保存一条博客信息*/
    @ResponseBody
    @RequestMapping({"/save"})
    public JSONObject save(Blog blog)throws Exception{
        Integer resultTotal = 0;
        if (blog.getId() == null){
            //添加
            resultTotal = blogService.add(blog);
            blogIndex.addIndex(blog);
        }else {
            //修改
            resultTotal = blogService.update(blog);
            blogIndex.updateIndex(blog);
        }
        JSONObject result = new JSONObject();
        if (resultTotal > 0){
            result.put("success",true);
        }else{
            result.put("success",false);
        }
        return result;
    }

    /**查询博客信息列表*/
    @ResponseBody
    @RequestMapping({"/list"})
    public JSONObject list(@RequestParam(value = "page",required = false)String page,@RequestParam(value = "rows",required = false)String rows,Blog blog){

        PageBean pageBean = new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("start",pageBean.getStart());
        map.put("size",pageBean.getPageSize());
        map.put("title", StringUtil.formatLike(blog.getTitle()));
        //分页查询博客信息列表
        List<Blog> list = blogService.list(map);
        //获取共有多少条博客信息
        Long total = blogService.getTotal(map);

        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class,new DateJsonValueProcessor("yyyy-MM-dd"));

        //将博客列表list封装到json中，以json的格式数组存储
        JSONArray jsonArray = JSONArray.fromObject(list,config);
        JSONObject result = new JSONObject();
        result.put("rows",jsonArray);
        result.put("total",total);
        return result;
    }


    //根据主键查询一条博客信息
    @ResponseBody
    @RequestMapping({"/findById"})
    public JSONObject findById(@RequestParam("id")String id){
        Blog blog = blogService.findById(Integer.parseInt(id));
        JSONObject result = JSONObject.fromObject(blog);
        return result;
    }

    //根据主键删除至少一条博客信息
    @ResponseBody
    @RequestMapping({"/delete"})
    public JSONObject delete(@RequestParam("ids")String ids) throws Exception {

        String[] split = ids.split(",");
        int n = 0;
        int sum = 0;
        for (int i = 0; i < split.length ; i++){
            n = blogService.delete(Integer.parseInt(split[i]));
            blogIndex.deleteIndex(split[i]);
            sum = sum + n;
        }
        JSONObject result = new JSONObject();
        if (sum > 0){
            result.put("success","true");
        }else{
            result.put("success",Boolean.valueOf(false));
        }
        return result;
    }
}
