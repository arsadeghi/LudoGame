<%@ include file="../common/header.jsp"%>
<div class="link" style="width: 800px;">
	<h2>Game Statistics</h2>
	<br>
	<form:form modelAttribute="gameRecordFilter" action="/Ludo/statistics.htm" method="post">
		<form:label path="owner">Owner Name:</form:label>
		<form:errors path="owner" cssClass="error_field"></form:errors>
		<form:input path="owner" />
		<form:errors path="owner" />
		<form:label path="player">Player Name:</form:label>
		<form:errors path="player" cssClass="error_field"></form:errors>
		<form:input path="player" />
		<form:errors path="player" />
		<button type="submit" name="filter">Search</button>
		<button type="submit" name="all">All Records</button>
	</form:form>
	<hr>
	<table class="data">
		<tr class="data">
			<th class="data">&nbsp;</th>
			<th class="data">Owner</th>
			<th class="data">Date</th>
			<th class="data">Duration</th>
			<th class="data">Players</th>
			<th class="data">Details</th>
		</tr>
		<c:if test="${empty games}">
			<tr class="data">
				<td colspan="6" class="data">No results found!</td>
			</tr>
		</c:if>
		<c:forEach var="game" items="${games}" varStatus="counter">
			<tr class="data" style="border-bottom: medium;">
				<td class="data">${counter.count}</td>
				<td class="avatar" style="background-image: url(resources/imgs/avatar/${game.owner.user.avatar}.png)">${game.owner.user.name}</td>
				<td class="data"><fmt:formatDate value="${game.startTime}" type="DATE"></fmt:formatDate></td>
				<td class="data">${game.duration}</td>
				<td class="data" style="text-align: left;" align="left"><table style="width: 100%">${game.ranking}</table></td>
				<td class="data"><a href="/Ludo/auditTrail.htm?gameId=${game.id}">Details</a></td>
			</tr>
		</c:forEach>
	</table>
</div>
<%@ include file="../common/footer.jsp"%>
