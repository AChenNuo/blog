<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
    //显示所有评论
    function showOtherComment() {
        $(".otherComment").show();
    }
    
    //重新加载验证码
    function loadimage() {
        //通过随机数来改变url参数，进而改变url地址，使得请求改变，重新提交请求，进而实现了图片局部刷新
        document.getElementById("randImage").src = "${pageContext.request.contextPath}/image.jsp?" + Math.random();
        <%--$("#randImage")[0].src = "${pageContext.request.contextPath}/image.jsp?" + Math.random();--%>
    }
    
    //提交评论
    function submitData() {
        var content = $("#content").val();
        var imageCode = $("#imageCode").val();
        if (content == null || content ==''){
            $.messager.alert("系统提示","请输入评论内容！")
        }else if (imageCode == null || imageCode == ''){
            $.messager.alert("系统提示","请填写验证码");
        }else {
            $.post("${pageContext.request.contextPath}/comment/save.do",
                {"content":content,"imageCode":imageCode,"blog.id":${blog.id}},
                function (result) {
                    if (result.success){
                        $.messager.alert("系统提示","评论已提交，审核通过后显示");

                        window.location.reload();
                    }else{
                        $.messager.alert("系统提示",result.errorInfo);
                    }
                },"json"
            );
        }
        
    }


    function resetValue() {
        $("#content").val("");
        $("#imageCode").val("");
    }

    //根据关键字查询
    function query22(keyWord) {
        $("#q1").val(keyWord);
        $("#queryForm22").submit();
    }

</script>


<div class="data_list">
    <div class="data_list_title">
        <img src="${pageContext.request.contextPath}/static/images/blog_show_icon.png"/>
        博客信息
    </div>
    <div>
        <div class="blog_title">
            <h3><strong>${blog.title}</strong></h3>
        </div>
        <div style="padding-left: 500px;padding-bottom: auto;padding-top: auto;">
            <div class="bshare-custom">
                <a title="分享到QQ空间" class="bshare-qzone"></a>
                <a title="分享到新浪微博" class="bshare-sinaminiblog"></a>
                <a title="分享到QQ微博" class="bshare-qqmb"></a>
                <a title="分享到网易微博" class="bshare-neteasemb"></a>
                <a title="更多平台" class="bshare-more bshare-more-icon more-style-addthis"></a>
                <script type="text/javascript" charset="UTF-8" src="http://static.bshare.cn/b/buttonLite.js#style=-1&amp;uuid=&amp;pophcol=1&amp;lang=zh"></script>
                <script type="text/javascript" charset="UTF-8" src="http://static.bshare.cn/b/bshareC0.js"></script>
            </div>
        </div>

        <div class="blog_info">
            发布时间：【<fmt:formatDate value="${blog.releaseDate}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>】
            &nbsp;&nbsp;博客类别：${blog.blogType.typeName}
            &nbsp;&nbsp;阅读：${blog.clickHit}
            &nbsp;&nbsp;评论：${blog.replyHit}
        </div>

        <div class="blog_content">
            ${blog.content}
        </div>
        <div class="blog_keyWord">
            <font><strong>关键字：</strong></font>
            <c:choose>
                <c:when test="${keyWords == null}">
                    &nbsp;&nbsp;无
                </c:when>
                <c:otherwise>
                    <form id="queryForm22" action="${pageContext.request.contextPath}/blog/q.do" method="post" target="_blank">
                        <input type="hidden" id="q1" name="q"/>
                        <c:forEach var="keyWord" items="${keyWords}">
                            &nbsp;&nbsp;<a href="javascript:query22('${keyWord}')">${keyWord}</a>&nbsp;&nbsp;
                        </c:forEach>
                    </form>
                </c:otherwise>
            </c:choose>

        </div>
        <div class="blog_lastAndNextPage">
            ${pageCode}
        </div>
    </div>
</div>







<div class="data_list">
    <div class="data_list_title">
        <img src="${pageContext.request.contextPath}/static/images/publish_comment_icon.png">
        发表评论
    </div>
    <div class="publish_comment">
        <div>
            <textarea rows="3" style="width: 100%" id="content" name="content" placeholder="来说两句吧..."></textarea>
        </div>
        <div class="verCode">
            验证码：<input type="text" name="imageCode" id="imageCode" size="10"
                    onkeydown="if (event.keyCode == 13)  submitData()">
            &nbsp;<img src="${pageContext.request.contextPath}/image.jsp" name="randImage" id="randImage"
                    title="换一张试试" onclick="loadimage()" width="60" height="20" border="1" align="absmiddle">
        </div>
        <div class="publishButton">
            <button class="btn btn-primary" type="button" onclick="submitData()">发表评论</button>
        </div>
    </div>
</div>








<div class="data_list">
    <div class="data_list_title">
        <img src="${pageContext.request.contextPath}/static/images/comment_icon.png"/>
        评论信息
        <c:if test="${commentList.size() > 10}">
            <a href="javascript:showOtherComment()" style="float: right;padding-right: 40px;">显示所有评论</a>
        </c:if>
    </div>
    
    <div class="commentDatas">
        <c:choose>
            <c:when test="${commentList.size() == 0}">
                暂无评论
            </c:when>
            <c:otherwise>
                <c:forEach var="comment" items="${commentList}" varStatus="status">
                    <c:choose>
                        <c:when test="${status.index < 10}">
                            <div class="comment">
                                <font>${status.index+1}楼&nbsp;&nbsp;&nbsp;&nbsp;
                                    IP地址:${comment.userIp}
                                </font>
                                <span style="float: right">【<fmt:formatDate value="${comment.commentDate}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>】
                                </span>
                                </br></br>
                                    <font color="red">评论说：</font>${comment.content}
                                </br>
                                </span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="otherComment">
                                <div class="comment">
                                    <font>${status.index+1}楼&nbsp;&nbsp;&nbsp;&nbsp;
                                        IP地址:${comment.userIp}
                                     </font>
                                    <span style="float: right">【<fmt:formatDate value="${comment.commentDate}" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>】
                                    </span></br></br>
                                        <font color="red">评论说：</font>${comment.content}
                                    </span>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>