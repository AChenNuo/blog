<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="data_list">
		<div class="data_list_title">
		<img src="${pageContext.request.contextPath}/static/images/search_icon.png"/>
			搜索&nbsp;<font color="red">${q}</font>&nbsp;的结果&nbsp;(总共搜索到&nbsp;${resultTotal}&nbsp;条记录)
		</div>
		<div class="datas">
			<ul>
				<c:choose>
					<c:when test="${blogList.size()==0}">
						<div align="center" style="padding-top: 20px"> 未查询到结果，请换个关键字看看！</div>
					</c:when>
					<c:otherwise>
						<c:forEach var="blog" items="${blogList}">
							<li style="padding-top: 20px">
								<span class="title">
									<a href="${pageContext.request.contextPath}/blog/articles/${blog.id}.do" target="_blank">${blog.title}</a>
								</span>
								<span class="summary">
									摘要：${blog.content}...
								</span>
								<span>
									<a href="${pageContext.request.contextPath}/blog/articles/${blog.id}.do" target="_blank">
										http://localhost:8080/${pageContext.request.contextPath}/blog/articles/${blog.id}.do&nbsp;&nbsp; </a>
									<span>发布日期：${blog.releaseDateStr}</span>
								</span>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
	${pageCode}
</div>

<div>
	<nav>
	  <ul class="pagination pagination-sm">

	  </ul>
	</nav>
 </div>
