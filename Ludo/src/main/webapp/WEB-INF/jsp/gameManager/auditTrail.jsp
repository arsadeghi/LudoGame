<%@ include file="../common/header.jsp"%>
<div class="link" style="width: 800px;">
	<h2>Game Statistics</h2>
	<br>
	<c:if test="${!empty player }">
		Audit Trail for player <b><c:out value="${player.user.name}"></c:out></b>
	</c:if>
	<hr>
	<table class="data">
		<tr class="data">
			<th class="data">#</th>
			<c:if test="${empty player }">
				<th class="data">Player</th>
			</c:if>
			<th class="data">Time</th>
			<th class="data">Movement Description</th>
		</tr>
		<c:if test="${empty auditTrails}">
			<tr class="data">
				<td colspan="3" class="data">No results found!</td>
			</tr>
		</c:if>
		<c:forEach var="auditTrail" items="${auditTrails}" varStatus="counter">
			<tr class="data">
				<td class="data">${counter.count}</td>
				<c:if test="${empty player }">
					<td class="report" style="background-image: url(resources/imgs/avatar/${auditTrail.user.avatar}.png)">${auditTrail.user.name}</td>
				</c:if>
				<td class="data"><fmt:formatDate value="${auditTrail.time}" type="TIME"></fmt:formatDate></td>
				<td class="report_small ${auditTrail.tdClass}">${auditTrail.moveDesc}</td>
			</tr>
		</c:forEach>
	</table>
</div>
<%@ include file="../common/footer.jsp"%>
