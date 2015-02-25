<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.0/themes/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="/Ludo/resources/css/ludo.css" />
<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="//code.jquery.com/ui/1.10.0/jquery-ui.js"></script>
</head>
<body>
	<div id="wrapper">
		<%@ include file="headerDiv.jsp"%>
		<div id="contentwrap" style="width: 100%">
			<div id="content">
				<c:if test="${!empty errorMsgs}">
					<div class="error">
						<c:forEach var="msg" items="${errorMsgs}">
							<li>${msg}</li>
						</c:forEach>
					</div>
				</c:if>
				<c:if test="${!empty successMsgs}">
					<div class="success">
						<span>${successMsgs}</span><br />
					</div>
				</c:if>