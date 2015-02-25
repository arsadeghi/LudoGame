package edu.gmu.ludo.dao.impl;

import org.springframework.stereotype.Repository;

import edu.gmu.ludo.dao.PlayerDao;
import edu.gmu.ludo.entity.Player;

@Repository
public class PlayerDaoImpl extends AbstractDaoImpl<Player, Integer> implements PlayerDao {

	protected PlayerDaoImpl() {
		super(Player.class);
	}

}
