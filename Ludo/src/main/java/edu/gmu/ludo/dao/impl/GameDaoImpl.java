package edu.gmu.ludo.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import edu.gmu.ludo.dao.GameDao;
import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.GameBoard.GameState;

@Repository
public class GameDaoImpl extends AbstractDaoImpl<GameBoard, Integer> implements GameDao {

	// SWE_681 All queries are parametric (Counter SQL Injection, provided by
	// Hibernate library)
	protected GameDaoImpl() {
		super(GameBoard.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GameBoard> getGamesByFilter(GameState state) {
		String hql = "SELECT DISTINCT game FROM GameBoard game WHERE game.gameState = :state";
		return getCurrentSession().createQuery(hql).setString("state", state.name()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GameBoard> getGamesByFilter(GameState state, User user) {
		String hql = "SELECT p.playingBoard FROM Player p WHERE p.finished = false AND p.playingBoard.gameState = :state AND p.user.username = :username";
		return getCurrentSession().createQuery(hql).setString("state", state.name()).setString("username", user.getUsername()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GameBoard> getGamesByFilter(GameState state, String playerName, String ownerName) {
		String hql = "SELECT DISTINCT g FROM  GameBoard g JOIN g.players p JOIN g.players o WHERE o.owner = true ";
		if (state != null) {
			hql += " AND g.gameState = :state";
		}
		if (playerName != null && !playerName.isEmpty()) {
			hql += " AND p.user.name = :playerName";
		}
		if (ownerName != null && !ownerName.isEmpty()) {
			hql += " AND o.user.name = :ownerName";
		}
		Query query = getCurrentSession().createQuery(hql);
		if (state != null) {
			query.setString("state", state.name());
		}
		if (playerName != null && !playerName.isEmpty()) {
			query.setString("playerName", playerName);
		}
		if (ownerName != null && !ownerName.isEmpty()) {
			query.setString("ownerName", ownerName);
		}
		return query.list();
	}

}
