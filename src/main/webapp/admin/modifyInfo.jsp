<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改个人信息页面</title>
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

        //首先得在UE加载完毕之后，再加载数据。，否则可能加载不到数据
        ue.addListener("ready",function () {
            //通过ajax获取数据，然后再将数据设置到富文本编辑器中
            UE.ajax.request("${pageContext.request.contextPath}/admin/blogger/find.do",{
                method:"post",
                async:false,
                data:{},
                onsuccess:function (result) {
                    //将result这个json数据转换为js对象，这样就可以取里面的值了
                    result = eval("(" + result.responseText + ")");
                    UE.getEditor("editor").setContent(result.profile);
                }
            });
        });

        //提交博主信息
        function submitData(){
            var nickname = $("#nickname").val();
            var sign = $("#sign").val();
            var proFile = UE.getEditor("editor").getContent();

            if (nickname == null || nickname ==""){
                $.messager.alert("系统提示","请输入昵称！");
            } else if (sign == null || sign ==""){
                $.messager.alert("系统提示","请输入个性签名！");
            } else if (proFile == null || proFile ==""){
                $.messager.alert("系统提示","请输入个人信息！");
            } else {
                $("#profile").val(proFile);
                $("#form1").submit();
                <%--$.post("${pageContext.request.contextPath}/admin/blogger/save.do",function (r) {--%>
                    <%--$.messager.alert("系统提示",r.toString());--%>
                <%--});--%>
            }
        }

    </script>
</head>
<body style="margin: 10px">
<div id="p" class="easyui-panel" title="修改个人信息" style="padding: 10px">

    <form id="form1" action="${pageContext.request.contextPath}/admin/blogger/save.do" method="post" enctype="multipart/form-data">
        <input type="hidden" id="id" name="id" value="${currentUser.id}">
        <%--对于富文本编辑器的值，不能通过el表达式获取（会乱掉的。），得通过json,ajax的方式来获取，再输出到富文本编辑器内容块上
        所以， value="${currentUser.profile}"是不可以取的。--%>
        <input type="hidden" id="profile" name="profile">
        <table cellpadding="20px">
            <tr>
                <td width="80px">用户名：</td>
                <td colspan="2"><input type="text" id="userName" name="userName" style="width: 200px;" value="${currentUser.userName}" readonly="readonly"></td>
            </tr>
            <tr>
                <td>昵  称：</td>
                <td colspan="2"><input type="text" id="nickname" name="nickname" style="width: 200px;" value="${currentUser.nickname}"></td>
            </tr>
            <tr>
                <td>个性签名：</td>
                <td colspan="2"><input type="text" id="sign" name="sign" style="width: 400px;" value="${currentUser.sign}"></td>
            </tr>
            <tr>
                <td>个人头像：</td>
                <td width="30px" height="30px"><img name="imageName" src="${pageContext.request.contextPath}/static/userImages/${currentUser.imageName}" width="100" height="100"></td>
                <td colspan="2"><input type="file" id="imageFile" name="imageFile" style="width: 400px;"></td>
            </tr>
            <tr>
                <td>个人信息：</td>
                <td colspan="2"><script id="editor" type="text/plain" style="width: 100%;height: 500px;"></script></td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2"><a href="javascript:submitData()" class="easyui-linkbutton" data-options="iconCls:'icon-submit'">提交</a></td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>