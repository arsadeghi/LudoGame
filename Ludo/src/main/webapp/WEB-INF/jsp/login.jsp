<%@ include file="common/header.jsp"%>

<div class="link" style="width: 360px;">
	<c:if test="${param.login_error == 1}">
		<div class="error">Login Failed: Invalid username or password</div>
	</c:if>
	<c:if test="${param.login_error == 2}">
		<div class="error">Your account has been blocked</div>
	</c:if>
	<h2>Login</h2>		
	<form id="signin" action="/Ludo/j_spring_security_check" method="POST" autocomplete="off">
		<label>UserName</label> <input type="text" name="j_username" tabindex="1" /> <label>Password</label> <input
			type="password" name="j_password" tabindex="2" />
		<button type="submit">Login</button>
		<a href="/Ludo/addUser.htm"><button type="button">New User</button></a>
	</form>
</div>

<%@ include file="common/footer.jsp"%>
