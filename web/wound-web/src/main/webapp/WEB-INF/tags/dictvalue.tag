<%@tag pageEncoding="UTF-8"%>

<%@ attribute name="dicmeta" type="java.util.List" required="true"%>
<%@ attribute name="value" type="java.lang.String" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<c:forEach items="${dicmeta}" var="entry">
		<c:if test="${entry.code eq value }">${entry.name}</c:if>
	</c:forEach>