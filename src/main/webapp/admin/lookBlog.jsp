<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>查看博客页面</title>
    <%--注意遵循引入的顺序，否则会渲染失败--%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>

    <%--富文本编辑器--%>
    <script type="text/javascript" charset="GBK" src="${pageContext.request.contextPath}/static/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="GBK" src="${pageContext.request.contextPath}/static/ueditor/ueditor.all.min.js"></script>
    <%--手动加载语言，避免在IE下有时因为加载语言导致编辑器加载失败--%>
    <%--在这里加载的语言会覆盖在配置项目里添加的语言类型，比如在配置项目里配置的是英文，但这里加载的是中文，那最后就是按照这里的中文--%>
    <script type="text/javascript" charset="GBK" src="${pageContext.request.contextPath}/static/ueditor/lang/zh-cn/zh-cn.js"></script>

    <script type="text/javascript">
        //实例化编辑器
        var ue = UE.getEditor("editor");
        ue.ready(function() {
            //不可编辑
            ue.setDisabled();
        });

        //采用ajax加载该条信息，首先得在UE加载完毕之后，再加载数据。
        ue.addListener("ready",function () {
            //通过ajax请求数据
            UE.ajax.request("${pageContext.request.contextPath}/admin/blog/findById.do",
                {
                    method:"post",
                    asyc:false,
                    data:{"id":"${param.id}"},
                    onsuccess:function (result) {
                        //将json字符串转化为JavaScript对象，从而可以使用，两者方法都可以。eval，responseText;;
                        // result = eval("(" + result.responseText + ")");
                        result = JSON.parse(result.responseText);
                        $("#title").val(result.title);
                        $("#keyWord").val(result.keyWord);
                        $("#blogTypeId").combobox("setValue",result.blogType.id);
                        UE.getEditor("editor").setContent(result.content);
                    }
                }
            );
        });


        //发布博客
        function submitData(){
            var title = $("#title").val();
            var blogTypeId = $("#blogTypeId").combobox("getValue");
            var content = UE.getEditor('editor').getContent();
            var keyWord = $("#keyWord").val();

            if (title == null || title == ''){
                $.messager.alert("系统提示","请输入标题");
            } else if (blogTypeId == null || blogTypeId == 0) {
                $.messager.alert("系统提示","请输入博客类别");
            } else if (content == null || content == '') {
                $.messager.alert("系统提示","请输入内容");
            } else {
                $.post("${pageContext.request.contextPath}/admin/blog/save.do",
                    {'id':'${param.id}','title':title,'blogType.id':blogTypeId,'content':content,
                        'summary':UE.getEditor('editor').getContentTxt().substr(0,155),'keyWord':keyWord},
                    function (result) {
                        if (result.success){
                            $.messager.alert("系统提示","博客发布成功");
                        } else{
                            $.messager.alert("系统提示","博客发布失败");
                        }
                    },"json"
                );
            }

        }




    </script>
</head>
<body style="margin: 10px">

<div id="p" class="easyui-panel" title="修改博客" style="padding: 10px">
    <table cellpadding="20px">
        <tr>
            <td width="80px">博客标题：</td>
            <td><input type="text" id="title" name="title" style="width: 400px" readonly="readonly"></td>
        </tr>
        <tr>
            <td>博客类别：</td>
            <td>
                <select class="easyui-combobox" id="blogTypeId" name="blogType.id" style="width: 154px;" editable="false" panelHeight="auto" readonly="readonly">
                    <option value="0">请选择博客类别</option>
                    <c:forEach items="${blogTypeCountList}" var="blogType">
                        <option value="${blogType.id}">${blogType.typeName}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td>博客内容：</td>
            <td><script id="editor" type="text/plain" style="width: 100%;height: 500px;"></script></td>
        </tr>
        <tr>
            <td>关键字：</td>
            <td><input type="text" id="keyWord" name="keyWord" style="width: 400px" readonly="readonly">&nbsp;(多个关键字中间空格隔开)</td>
        </tr>
    </table>
</div>

</body>
</html>