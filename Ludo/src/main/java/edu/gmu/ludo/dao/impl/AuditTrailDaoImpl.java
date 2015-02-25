package edu.gmu.ludo.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import edu.gmu.ludo.dao.AuditTrailDao;
import edu.gmu.ludo.entity.AuditTrail;
import edu.gmu.ludo.entity.GameBoard.GameState;

@Repository
public class AuditTrailDaoImpl extends AbstractDaoImpl<AuditTrail, Integer> implements AuditTrailDao {

	protected AuditTrailDaoImpl() {
		super(AuditTrail.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditTrail> getAuditTrailsForGame(Integer gameId, String username) {
		String hql = "SELECT DISTINCT at FROM AuditTrail at WHERE at.game.gameState = '" + GameState.FINISHED + "'";
		if (gameId != null) {
			hql += " AND at.game.id = :gameId";
		}
		if (username != null) {
			hql += " AND at.user.username = :username";
		}
		Query query = getCurrentSession().createQuery(hql);
		if (gameId != null) {
			query.setString("gameId", gameId.toString());
		}
		if (username != null) {
			query.setString("username", username);
		}
		hql += " ORDER BY at.time";
		return query.list();
	}
}
