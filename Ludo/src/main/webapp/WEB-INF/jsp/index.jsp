<%@ include file="./common/header.jsp"%>
<div class="link">
	<h2>Welcome To Ludo</h2>
	<table style="border: 0; width: 100%">
		<tr>
			<td align="right"><a href="/Ludo/newGame.htm"><img src="/Ludo/resources/imgs/create.png" alt="Create Game"
					title="Create Game"></a></td>
			<td align="left"><b>Create a New Game</b></td>
			<td align="right"><a href="/Ludo/statistics.htm?all"><img src="/Ludo/resources/imgs/stat.png"
					alt="Statistics" title="Game Statistics"></a></td>
			<td align="left" valign="middle"><b>View Game Statistics</b></td>
		</tr>
		<tr>
			<td align="right"><a href="/Ludo/listGames.htm"><img src="/Ludo/resources/imgs/join.png" alt="Join Game"
					title="Join Game"></a></td>
			<td align="left"><b>Join a Created Game</b></td>
			<td colspan="2"></td>
		</tr>
		<c:if test="${!empty resumableGameId}">
			<tr>
				<td align="right"><a href="/Ludo/playGame/${resumableGameId}.htm"><img
						src="/Ludo/resources/imgs/resume.png" alt="Resume Game" title="Resume Game"></a></td>
				<td align="left"><b>Resume your current Game</b></td>
				<td align="right"><a href="/Ludo/leaveGame/${resumableGameId}.htm"><img
						src="/Ludo/resources/imgs/leave.png" alt="Leave Game" title="Leave Game"></a></td>
				<td align="left"><b>Leave your current Game</b></td>
			</tr>
		</c:if>
	</table>
</div>
<%@ include file="./common/footer.jsp"%>
