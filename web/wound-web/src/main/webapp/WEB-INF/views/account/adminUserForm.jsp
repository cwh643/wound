<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>用户管理</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-error"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	<form id="inputForm" action="${ctx}/admin/user/${action}" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${user.id}"/>
		<fieldset>
			<legend><small>用户管理</small></legend>
			<div class="control-group">
				<label class="control-label">登录名:</label>
				<div class="controls">
					<c:if test="${action eq 'update'}">
						<input type="text" value="${user.loginName}" class="input-large" disabled="" />
					</c:if>
					<c:if test="${action eq 'create'}">
						<input type="text" id="loginName" name="loginName" value="${user.loginName}" class="input-large required" />
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">姓名:</label>
				<div class="controls">
					<input type="text" id="name" name="name" value="${user.name}" class="input-large required"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">科室:</label>
				<div class="controls">
					<input type="text" id="department" name="department" value="${user.department}" class="input-large required"/>
				</div>
			</div>
			<!-- 
			<div class="control-group">
				<label class="control-label">注册日期:</label>
				<div class="controls">
					<span class="help-inline" style="padding:5px 0px"><fmt:formatDate value="${user.registerDate}" pattern="yyyy年MM月dd日  HH时mm分ss秒" /></span>
				</div>
			</div>
			 -->
			<div class="form-actions">
				<input id="submit_btn" class="btn btn-primary" type="submit" value="提交"/>&nbsp;	
				<input id="cancel_btn" class="btn" type="button" value="返回" onclick="history.back()"/>
			</div>
		</fieldset>
	</form>
	
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			//$("#name").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate();
		});
	</script>
</body>
</html>
