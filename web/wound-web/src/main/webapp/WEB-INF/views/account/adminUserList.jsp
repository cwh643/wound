<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>用户管理</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	<div class="row">
		<div class="span4 ">
			<form class="form-search" action="#">
				<label>登录名：</label> <input type="text" name="search_LIKE_loginName" class="input-medium" value="${param.search_LIKE_loginName}"> 
				<button type="submit" class="btn" id="search_btn">查询</button>
		    </form>
	    </div>
	</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>登录名</th><th>姓名</th><th>科室<th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${users.content}" var="user">
			<tr>
				<td><a href="${ctx}/admin/user/update/${user.id}">${user.loginName}</a></td>
				<td>${user.name}</td>
				<td>${user.department}</td>
					<%-- <fmt:formatDate value="${user.registerDate}" pattern="yyyy年MM月dd日  HH时mm分ss秒" /> --%>
					
				<td><c:if test="${user.loginName ne 'admin'}"><a class="deleteUser"  data-id="${user.id}" href="javascript:void(0)">删除</a></c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<tags:pagination page="${users}" paginationSize="10"/>

	<div><a class="btn" href="${ctx}/admin/user/create">创建用户</a></div>
	
	<script>
		$(document).ready(function() {
			$('.deleteUser').on('click', onDeleteUser);
			setTimeout(function() {
				$("#message").hide();
			}, 3000);
		});
		function onDeleteUser() {
			var id = $(this).data('id');
			bootbox.confirm('确定要删除此用户吗？', function(result) {
				if (result) {
                    window.location.href = '${ctx}/admin/user/delete/' + id;
                }
			});
		}
	</script>
	
	
	
</body>
</html>
