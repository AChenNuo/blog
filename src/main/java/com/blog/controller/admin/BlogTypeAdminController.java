package com.blog.controller.admin;

import com.blog.entity.BlogType;
import com.blog.entity.PageBean;
import com.blog.service.BlogService;
import com.blog.service.BlogTypeService;
import com.blog.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客类型管理
 */
@Controller
@RequestMapping({"/admin/blogType"})
public class BlogTypeAdminController {

    @Resource
    private BlogTypeService blogTypeService;

    @Resource
    private BlogService blogService;

    /**博客类型列表*/
    //返回json给前台，同时前台easyUI通过默认属性值，
    @ResponseBody
    @RequestMapping({"/list"})
    public JSONObject list(@RequestParam(value="page",required=false)String page,
                       @RequestParam(value="rows",required=false)String rows,
                       HttpServletResponse response) throws Exception {
        //page是第几页，rows是每一页的理论记录数（new(page,pageSize)）
        PageBean pageBean = new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
        Map<String,Object> map = new HashMap<String, Object>();
        //start是从第几条记录数开始。size依次向下获取几条记录数
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        //查询博客类型列表
        List<BlogType> blogTypeList = blogTypeService.list(map);
        //查询总共有多少条数据
        Long total = blogTypeService.getTotal(map);
        //将数据写入response
        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(blogTypeList);
        //将start-size规定范围的（分页查询的）集合传入result中，从而在前台页面中显示当前分页里面的一条条的数据（一页的数据，换下一页会再次执行查询的）
        result.put("rows", jsonArray);
        //将总共的记录数放入result，从而在前台页面有 共total条记录 。
        result.put("total", total);
//        ResponseUtil.write(response, result);// response.getWriter().println(o.toString());---返回json格式的数据字符串；与注解@ResponseBody效果一样的
        //在前台按照格式显示在页面是easyUI内部对输出的json数据进行了进一步格式化输出到页面。
        return result;//返回json格式的数据字符串
    }
    //返回json数据给前台
    @ResponseBody
    @RequestMapping({"/save"})
    public JSONObject save(BlogType blogType,HttpServletResponse response){
        int resultTotal = 0;
        if (blogType.getId() == null){
            //添加
            resultTotal = blogTypeService.add(blogType);
        }else {
            //更新
            resultTotal = blogTypeService.update(blogType);
        }
        JSONObject result = new JSONObject();
        if (resultTotal > 0){
            result.put("success",Boolean.valueOf(true));
        }else{
            result.put("success",Boolean.valueOf(false));
        }
        return result;
    }

    @RequestMapping({"/delete"})
    @ResponseBody
    public JSONObject delete(@RequestParam("ids")String ids){
        JSONObject result= new JSONObject();
        result.put("success",Boolean.valueOf(false));
        String[] id = ids.split(",");
        int sum = 0;
        int n = 0;
        for (int i = 0; i < id.length ; i++){
            Integer blogTotals = blogService.getBlogByTypeId(Integer.valueOf(id[i]));
            if (blogTotals > 0){
                result.put("existTrue",true);
                String typeName = blogTypeService.findById(Integer.valueOf(id[i])).getTypeName();
                result.put("exist","存在一条或多条【" + typeName + "】类型的博客，【" + typeName +"】博客类型不能被删除，请先删除该博客类型下的所有博客，再做此操作");
                return result;
            }else {
                n = blogTypeService.delete(Integer.valueOf(id[i]));
                sum = sum + n;
            }
        }
        if (sum > 0){
            result.put("success",Boolean.valueOf(true));
        }
        return result;
    }
}
