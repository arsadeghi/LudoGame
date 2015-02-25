<style>
td.user {
	text-align: right;
	vertical-align: middle;
	background-size: 50px 75px;
	background-repeat: no-repeat;
	height: 65px;
	padding-right: 65px;
	background-position: right center;
}
</style>
<div id="headerwrap">
	<div id="header">
		<table width="100%">
			<tr>
				<c:if test="${empty authN}">
					<td width="100%" colspan="3"><p align="center" style="font-size: x-large; font-weight: bolder;">Ludo</p></td>
				</c:if>
				<c:if test="${!empty authN}">
					<td width="10%" align="center" valign="bottom">
					<div style="margin-top: 20px;"><a href="/Ludo/index.htm"><img title="Home" src="/Ludo/resources/imgs/home.png"></a>&nbsp;<a
						href="/Ludo/j_spring_security_logout"><img title="Logout" src="/Ludo/resources/imgs/logout.png"></a>&nbsp;<a
						href="/Ludo/updateUser.htm"><img title="Update User" src="/Ludo/resources/imgs/user_edit.png" width="26px;" height="26px;"></a>
					<c:if test="${!empty playringGameId}">
					&nbsp;<a
						href="/Ludo/leaveGame/"><img title="Logout" src="/Ludo/resources/imgs/logout.png">${playringGameId}</a>
					</c:if>	
					</div>
						
					</td>
					<td width="80%"><p align="center" style="font-size: x-large; font-weight: bolder;">Ludo</p></td>
					<td width="10%" align="center" class="user"
						style="background-image: url(/Ludo/resources/imgs/avatar/${LudoUser.avatar}.png)">
						<div></div> <c:out value="${LudoUser.name}"></c:out>
					</td>
				</c:if>
			</tr>

		</table>
	</div>
</div>