<%@ include file="../common/header.jsp"%>
<link rel="stylesheet" type="text/css" href="/Ludo/resources/css/carousel.css" media="screen" />
<script type="text/javascript" src="/Ludo/resources/js/jquery.tinycarousel.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#slider').tinycarousel({
			callback : function(element, index) {
				$('#avatar').val(index + 1);
			}
		});
	});
</script>
<div class="link" style="width: 560px;">
	<h2>Create User</h2>
	<form:form modelAttribute="user" action="/Ludo/${(empty updateMode)? 'addUser.htm' : 'updateUser.htm'}" method="post">
		<div id="container">
			<div style="width: 390px; float: left;">
				<table style="width: 400px;">
					<c:if test="${empty updateMode}">
						<tr>
							<td width="40%"><form:label path="username" cssStyle="display: inline;">User Name</form:label></td>
							<td width="60%"><form:errors path="username" cssClass="error_field"></form:errors><form:input path="username" cssStyle="display: inline;" /></td>
						</tr>
					</c:if>
					<c:if test="${!empty updateMode}">
						<form:hidden path="username"/>
					</c:if>
					<tr>
						<td><form:label path="name" cssStyle="display: inline;">Name</form:label></td>
						<td><form:errors path="name" cssClass="error_field"></form:errors><form:input path="name" cssStyle="display: inline;" /></td>
					</tr>
					<tr>
						<td><form:label path="password" cssStyle="display: inline;">Password</form:label></td>
						<td><form:errors path="password" cssClass="error_field"></form:errors><form:password path="password" cssStyle="display: inline;" /></td>
					</tr>
					<tr>
						<td><form:label path="confirmedPassword" cssStyle="display: inline;">Confirmed Password</form:label></td>
						<td><form:errors path="confirmedPassword" cssClass="error_field"></form:errors><form:password path="confirmedPassword" cssStyle="display: inline;" /></td>
					</tr>
				</table>
			</div>
			<div style="width: 160px; float: right;">
				<div id="slider">
					<div class="viewport">
						<ul class="overview">
							<c:forEach begin="1" end="28" var="i">
								<li><img src="resources/imgs/avatar/${i}.png" alt="avatar_${i}" id="${i}" /></li>
							</c:forEach>
						</ul>
					</div>
					<div style="text-align: center;">
						<form:hidden path="avatar" id="avatar" />
						<a class="prev" href="#"><img src="resources/imgs/left.png" class="arrow"></a> <a class="next" href="#"><img
							src="resources/imgs/right.png" class="arrow"></a>
					</div>
				</div>
			</div>
			<div style="clear: both;"></div>
		</div>
		<c:if test="${!empty updateMode}">
			<button type="submit" id="update">Update User</button>
		</c:if>
		<c:if test="${empty updateMode}">
			<button type="submit" id="save">Save User</button>
			<a href="/Ludo/login.htm"><button type="button">Login</button></a>
		</c:if>
	</form:form>
</div>

<%@ include file="../common/footer.jsp"%>
