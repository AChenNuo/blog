<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>博客类别管理页面</title>
    <%--注意遵循引入的顺序，否则会渲染失败--%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>

    <script type="text/javascript">
        var url;
        /**弹出添加博客类别信息对话框*/
        function openBlogTypeAddDialog() {
            $("#dlg").dialog("open").dialog("setTitle","添加博客类别信息");
            url="${pageContext.request.contextPath}/admin/blogType/save.do";
        }
        /**弹出修改博客类别信息对话框*/
        function openBlogTypeModifyDialog() {
            var selectedRows = $("#dg").datagrid("getSelections");
            if (selectedRows.length != 1){
                $.messager.alert("系统提示","请选择一条要编辑的数据");
                //return; !=return xxx;  return;代表无返回值，不做任何操作。返回到触发按钮之前的状态。
                return;
            }
            var row = selectedRows[0];
            $("#dlg").dialog("open").dialog("setTitle","编辑博客类别信息")
            $("#fm").form("load",row);
            url="${pageContext.request.contextPath}/admin/blogType/save.do?id=" + row.id;
        }

        /**保存博客类别信息*/
        function saveBlogType() {
            $("#fm").form("submit",{
               url:url,
               onSubmit:function(){
                   return $(this).form("validate");
               },
                //这里用到了ajax，然后是result代表的是后台返回的值，此时后台返回的是json格式的数据，然后result.xxx=值
                //代表的是后台{xxx:值,yyy:值,...}
               success:function (result) {
                    var result = eval('(' + result + ')');
                    if (result.success){
                        $.messager.alert("系统提示","保存成功！");
                        resetValue();
                        //关闭对话框
                        $("#dlg").dialog("close");
                        //刷新查询结果
                        $("#dg").datagrid("reload");
                    }else{
                        $.messager.alert("系统提示","保存失败！");
                        return;
                    }
                }
            });
        }
        /**删除博客类别信息*/
        function deleteBlogTypeDialog() {
            var selectedRows = $("#dg").datagrid("getSelections");
            if (selectedRows.length == 0){
                $.messager.alert("系统提示","请至少选择一条要删除的数据");
                return;
            }
            var strIds = [];
            for (var i = 0 ; i < selectedRows.length; i++){
                strIds.push(selectedRows[i].id);
            }
            var ids = strIds.join(",");
            $.messager.confirm("系统提示","您确定要删除这<font color=red>" + selectedRows.length + "</font>条数据吗？",function (r) {
                if (r){
                    $.post("${pageContext.request.contextPath}/admin/blogType/delete.do",{ids:ids},function (result) {
                        if (result.success){
                            $.messager.alert("系统提示","数据已成功删除!");
                        }else{
                            if (result.existTrue){
                                $.messager.alert("系统提示",result.exist);
                            }
                            $.messager.alert("系统提示","数据删除失败!");
                            $("#dg").datagrid("reload");
                        }
                        $("#dg").datagrid("reload");
                    },"json");
                }
            });
        }

        /**重置弹出的对话框*/
        function resetValue() {
            $("#typeName").val("");
            $("#orderNo").val("");
        }

        /**关闭对话框*/
        function closeBlogTypeDialog() {
            $("#dlg").dialog("close");
            resetValue();
        }
    </script>
</head>
<body style="margin: 1px">
    <table id="dg" title="博客类别管理" class="easyui-datagrid" fitcolumns="true" pagination="true" rownumbers="true"
           url="${pageContext.request.contextPath}/admin/blogType/list.do" fit="true" toolbar="#tb">
        <thead>
            <tr>
                <th field="cb" checkbox="true" align="center"></th>
                <th field="id" width="20" align="center">编号</th>
                <th field="typeName" width="100" align="center">博客类型名称</th>
                <th field="orderNo" width="100" align="center">排序序号</th>
            </tr>
        </thead>
    </table>
<div id="tb">
    <a href="javascript:openBlogTypeAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
    <a href="javascript:openBlogTypeModifyDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
    <a href="javascript:deleteBlogTypeDialog()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>

</div>

<div id="dlg" class="easyui-dialog" style="width: 500px;height: 180px;padding: 10px 20px" closed="true" buttons="#dlg-buttons">
    <form id="fm" action="" method="post">
        <table cellpadding="8px">
            <tr>
                <td>博客类别名称：</td>
                <td><input type="text" id="typeName" name="typeName" class="easyui-validatebox" required="true"></td>
            </tr>
            <tr>
                <td>博客类别排序：</td>
                <td><input type="text" id="orderNo" name="orderNo" class="easyui-validatebox" required="true">(类别根据该序号排序)</td>
            </tr>
        </table>
    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:saveBlogType()" class="easyui-linkbutton" iconCls="icon-ok" >保存</a>
    <a href="javascript:closeBlogTypeDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>

</body>
</html>