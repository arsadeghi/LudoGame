<%@ include file="../common/header.jsp"%>

<script type="text/javascript">
	function updateJoinedPlayers() {
		if ($("#gameId").val() != "") {
			$.ajax({
				type : "POST",
				url : "/Ludo/showGameList.ajax",
				data : "gameId=" + $("#gameId").val(),
				success : function(response) {
					$("#joinedPlayer").html(response);
				}
			});
		}
	}

	function checkGameStarted() {
		if ($("#gameId").val() != "") {
			$.ajax({
				type : "POST",
				url : "/Ludo/getGameStatus.ajax",
				data : "gameId=" + $("#gameId").val(),
				success : function(response) {
					if (response == "STARTED") {
						$("#gameStart").html(
								"Now you can <a href='/Ludo/newGame/"
										+ $("#gameId").val()
										+ ".htm'>start</a> the game");
					}
				}
			});
		}
	}

	$(document).ready(function() {
		updateJoinedPlayers();
		checkGameStarted();
		setInterval(updateJoinedPlayers, 5000);
		setInterval(checkGameStarted, 5000);
	});
</script>
<div class="link">
	<c:if test="${!joined}">
		<c:if test="${owner}">
			<h2>Create new game:</h2>
		</c:if>
		<c:if test="${!owner}">
			<h2>Join this game:</h2>
		</c:if>
		<br>
		<form:form modelAttribute="player" action="/Ludo/joinGame.htm" method="post">
			<form:hidden path="gameId" />
			<form:label path="colorName">Select your color</form:label>
			<form:select path="colorName" items="${colorList}" itemLabel="colorName" itemValue="colorName"></form:select>
			<c:if test="${owner}">
				<button type="submit" name="create">Create</button>
			</c:if>
			<c:if test="${!owner}">
				<button type="submit" name="join">Join</button>
			</c:if>
		</form:form>
	</c:if>
	<c:if test="${joined}">
		<form:form modelAttribute="player" name="gameInfo">
			<form:hidden path="gameId" />
		</form:form>
	</c:if>
	<h2>Joined players:</h2>
	<br>
	<div id="joinedPlayer">Loading joined players ...</div>
	<br>
	<c:if test="${owner and joined}">
		<a href="/Ludo/startGame/${player.gameId}.htm"><button type="button">Start</button></a>
	</c:if>
	<c:if test="${!owner and joined}">
		<div id="gameStart">Waiting for the game owner to start the game ...</div>
	</c:if>
</div>

<%@ include file="../common/footer.jsp"%>
