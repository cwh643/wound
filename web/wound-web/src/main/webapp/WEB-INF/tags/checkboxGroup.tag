<%@tag pageEncoding="UTF-8"%>

<%@ attribute name="dicmeta" type="java.util.List" required="true"%>
<%@ attribute name="name" type="java.lang.String" required="true"%>
<%@ attribute name="id" type="java.lang.String" required="false"%>
<%@ attribute name="css" type="java.lang.String" required="false"%>
<%@ attribute name="value" type="java.lang.String" required="false"%>
<%@ attribute name="inline" type="java.lang.String" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<c:forEach items="${dicmeta}" var="entry">
	<c:choose>
		<c:when test="${fn:contains(value, entry.code)}">
			<label class="checkbox ${inline}">
		      <input type="checkbox" name="${name}" value="${entry.code}" checked="checked" /> ${entry.name}
		    </label>
		</c:when>
		<c:otherwise>
			<label class="checkbox ${inline}">
		      <input type="checkbox" name="${name}" value="${entry.code}" /> ${entry.name}
		    </label>
		</c:otherwise>
	</c:choose>
</c:forEach>