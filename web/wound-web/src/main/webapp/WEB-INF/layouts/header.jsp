<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <div class="navbar  navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a href="/" class="brand nav-block"><img src="${ctx}/static/images/ic_launcher.png" alt="榕创医疗" style="margin-right: 10px;"><span>榕创医疗</span></a>
          <!-- <a class="brand" href="${ctx}">榕创医疗</a> -->
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li <c:if test ="${menu eq 1}">class="active"</c:if>><a href="${ctx}/archivesRecord">创伤信息</a></li>
              <li <c:if test ="${menu eq 2}">class="active"</c:if>><a href="${ctx}/admin/user">用户管理</a></li>
              <%-- <li><a href="${ctx}/api">API</a></li>--%>
            </ul>
          </div><!--/.nav-collapse -->
	    <shiro:user>
			<ul class="nav pull-right">
			  <li class="dropdown">
			    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
			      <i class="icon-user"></i> <shiro:principal property="name"/>
			      <b class="caret"></b>
			    </a>
			    <ul class="dropdown-menu">
					<li><a href="${ctx}/profile">修改密码</a></li>
					<li><a href="${ctx}/logout">退出</a></li>
			    </ul>
			  </li>
			</ul>
		</shiro:user>
        </div>
      </div>
    </div>