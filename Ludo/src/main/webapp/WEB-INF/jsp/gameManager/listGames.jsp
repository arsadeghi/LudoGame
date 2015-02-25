<%@ include file="../common/header.jsp"%>
<script type="text/javascript">
	function updateGameList() {
		$.ajax({
			type : "POST",
			url : "/Ludo/showGameList.ajax",
			data : "all",
			success : function(response) {
				$("#gameList").html(response);
			}
		});
	}

	$(document).ready(function() {
		updateGameList();
		setInterval(updateGameList, 5000);
	});
</script>
<div class="link">
	<h2>Create User</h2>
	<c:if test="${empty games}">
		<p align="center">
			There is no game, <a href="/Ludo/newGame.htm">create</a> a new one.
		</p>
	</c:if>
	<div id="gameList">
		Loading available game list ...
	</div>
</div>
<%@ include file="../common/footer.jsp"%>
