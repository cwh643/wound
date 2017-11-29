<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>创伤档案</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	<form class="form-search" action="#">
	<div class="row">
		 <div class="span4 ">
				<label>采集时间：</label> 
				<input type="text" class="input-small input-datepick" name="search_GTE_beginTime" value="${param.search_GTE_beginTime}" />
				~
				<input type="text" class="input-small input-datepick" name="search_LTE_endTime" value="${param.search_LTE_endTime}" />
	    </div>
	    <div class="span3 ">
				<label>设备ID：</label> <input type="text" name="search_LIKE_deviceId" class="input-small" value="${param.search_LIKE_deviceId}">
		</div>
		 <div class="span3 ">
				<label>患者姓名：</label> <input type="text" name="search_LIKE_patientName" class="input-small" value="${param.search_LIKE_patientName}"> 
		</div>
	    <div class="span1 ">
	    	<button type="submit" class="btn" id="search_btn">Search</button>
	    </div>
	    <%-- <tags:sort/> --%>
	</div>
	</form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>采集时间</th><th>设备ID</th><th>患者姓名</th><th>性别</th><th>是否手术</th></tr></thead>
		<tbody>
		<c:forEach items="${records.content}" var="v">
			<tr>
				<td><a href="${ctx}/archivesRecord/details?inpatientNo=${v[0]}&recordTime=${v[2]}">${v[2]}</a></td>
				<td>${v[1]}</td>
				<td>${v[3]}</td>
				<td>${v[4]}</td>
				<td><c:if test="${v[6]==0}">否</c:if><c:if test="${v[6]==1}">是</c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<tags:pagination page="${records}" paginationSize="10"/>
	
		<script>
		$(document).ready(function() {
			$('.input-datepick').datetimepicker( {
				startView: 2,
				minView: 2,
			    format: 'yyyy-mm-dd',
				language: "zh-CN",
 				autoclose:true
			});
		});
		</script>
</body>
</html>
