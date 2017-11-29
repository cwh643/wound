<%@tag pageEncoding="UTF-8"%>

<%@ attribute name="dicmeta" type="java.util.List" required="true"%>
<%@ attribute name="value" type="java.lang.String" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<% 
String[] valueArray = value.split(","); 
request.setAttribute("valueArray",valueArray);
%>
<c:forEach items="${valueArray}" var="val" varStatus="status">
	<c:forEach items="${dicmeta}" var="entry">
		<c:if test="${entry.code eq val }">${entry.name}
			<c:if test="${!status.last}" >，</c:if>
		</c:if>
	</c:forEach>
</c:forEach>