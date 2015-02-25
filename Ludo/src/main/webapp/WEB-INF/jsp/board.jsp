<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.0/themes/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="/Ludo/resources/css/board.css" />
<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="//code.jquery.com/ui/1.10.0/jquery-ui.js"></script>
<script type="text/javascript">
	function submitForm(actionType, pegNum) {
		document.playForm.action = "/Ludo/playGame.htm?" + actionType;
		if (!isNaN(pegNum)) {
			document.playForm.pieceNumber.value = pegNum;
		}
		document.playForm.submit();
	}
	function updateStatus() {
		$.ajax({
			type : "POST",
			url : "/Ludo/playGame.ajax",
			data : "updateStatus&gameId=" + $("#gameId").val(),
			success : function(response) {
				var states = response.split("#");
				$("#bluePanel").html(states[0]);
				$("#greenPanel").html(states[1]);
				$("#yellowPanel").html(states[2]);
				$("#redPanel").html(states[3]);
			}
		});
	}
	function updateBoard() {
		$.ajax({
			type : "POST",
			url : "/Ludo/playGame.ajax",
			data : "updateBoard&gameId=" + $("#gameId").val(),
			success : function(response) {
				$("#board_main").html(response);
			}
		});
	}

	function updateMsgs() {
		if ($("#gameId").val() != "") {
			$
					.ajax({
						type : "POST",
						url : "/Ludo/getGameStatus.ajax",
						data : "gameId=" + $("#gameId").val(),
						success : function(response) {
							if (response == "FINISHED") {
								if ($("#gameIsFinished").val() == "true") {
									$("#finishedMsg")
											.html(
													"Game is finished! You can <a href=\"/Ludo/newGame.htm\">create</a> a new game or <a href=\"/Ludo/listGames.htm\">join</a> created games.");
								} else if ($("#lastDiceNumber").val() != "") {
									$("#finishedMsg")
											.html(
													"How lucky! You got 6 and yoy have a bonus turn.");
								}
								$("#errorMsg").fadeOut("slow");
							}
						}
					});
		}
	}

	function checkGameFinished() {
	}

	$(document).ready(function() {
		updateStatus();
		updateBoard();
		setInterval(updateMsgs, 2000);
		if ($("#gameIsFinished").val() != "true") {
			setInterval(updateStatus, 2000);
			setInterval(updateBoard, 2000);
		}
	});
</script>
</head>
<body>
	<div id="wrapper">
		<%@ include file="./common/headerDiv.jsp"%>
		<form:form name="playForm" method="post" modelAttribute="gameMove">
			<form:hidden path="gameId" id="gameId" />
			<form:hidden path="pieceNumber" />
			<form:hidden path="diceNumber" id="lastDiceNumber" />
		</form:form>
		<input id="gameIsFinished" value="${isFinished}" type="hidden" />
		<div id="footerwrap">
			<div class="error">
				<c:if test="${!empty errorMsgs}">
					<c:forEach var="msg" items="${errorMsgs}">
						<p class="error" id="errorMsg">${msg}</p>
					</c:forEach>
				</c:if>
				<c:if test="${empty errorMsgs}">
					<p>&nbsp;</p>
				</c:if>
				<p align="center" id="finishedMsg"></p>
			</div>
		</div>
		<div id="leftcolumnwrap">
			<div id="redPanel">Loading player panel...</div>
			<div id="divider">&nbsp;</div>
			<div id="yellowPanel">Loading player panel...</div>
		</div>
		<div id="contentwrap">
			<div id="content">
				<div class="board" id="board_main">
					<p>Loading the game board...</p>
				</div>
			</div>
		</div>
		<div id="rightcolumnwrap">
			<div id="bluePanel">Loading player panel...</div>
			<div id="divider">&nbsp;</div>
			<div id="greenPanel">Loading player panel...</div>
		</div>

		<%@include file="./common/footerDiv.jsp"%>
	</div>
</body>
</html>