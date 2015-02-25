package edu.gmu.ludo.dao;

import java.util.List;

import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.GameBoard.GameState;

public interface GameDao extends AbstractDao<GameBoard, Integer> {

	List<GameBoard> getGamesByFilter(GameState state);

	List<GameBoard> getGamesByFilter(GameState state, User user);

	List<GameBoard> getGamesByFilter(GameState state, String playerName, String ownerName);

}
