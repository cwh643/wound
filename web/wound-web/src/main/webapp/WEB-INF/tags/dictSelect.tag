<%@tag pageEncoding="UTF-8"%>

<%@ attribute name="dicmeta" type="java.util.List" required="false"%>
<%@ attribute name="name" type="java.lang.String" required="true"%>
<%@ attribute name="id" type="java.lang.String" required="false"%>
<%@ attribute name="css" type="java.lang.String" required="false"%>
<%@ attribute name="value" type="java.lang.String" required="false"%>
<%@ attribute name="items" type="java.lang.String" required="false"%>
<%@ attribute name="titleName" type="java.lang.String" required="false"%>
<%@ attribute name="titleValue" type="java.lang.String" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
if (items != null) {
	String[] itemList = items.split(",");
	request.setAttribute("itemList",itemList);
}
%>

<select id="${id}" name="${name}" class="${css}">
	<c:if test="${not empty titleName}">
		<option title="${titleName}" value="${titleValue}" >${titleName}</option>
	</c:if>
	<c:if test="${not empty itemList}">
		<c:forEach items="${itemList}" var="entry">
			<c:choose>
				<c:when test="${entry eq value }">
					<option title="${entry}" value="${entry}" selected>${entry}</option>
				</c:when>
				<c:otherwise>
					<option title="${entry}" value="${entry}">${entry}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:if>
	<c:if test="${not empty dicmeta}">
		<c:forEach items="${dicmeta}" var="entry">
			<c:choose>
				<c:when test="${entry.code eq value }">
					<option title="${entry.name}" value="${entry.code}" selected>${entry.name}</option>
				</c:when>
				<c:otherwise>
					<option title="${entry.name}" value="${entry.code}">${entry.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:if>
</select>