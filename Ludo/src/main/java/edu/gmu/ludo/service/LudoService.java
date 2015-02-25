package edu.gmu.ludo.service;

import java.util.List;

import edu.gmu.ludo.entity.AuditTrail;
import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.Piece;
import edu.gmu.ludo.entity.Player;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.Player.Rank;

public interface LudoService {

	void registerUser(User user) throws LudoException;

	void createGame(GameBoard board) throws LudoException;

	void updateGame(GameBoard board) throws LudoException;

	List<User> getAllUsers();

	User getUserByUsername(String username);

	List<GameBoard> getJoinableGames();

	GameBoard getGameById(Integer gameId);
	
	GameBoard getResumableGame(User user);
	
	Player getPlayerById(Integer playerId);

	List<GameBoard> getFinishedGames();

	List<GameBoard> getFinishedGames(String owner, String player);

	List<AuditTrail> getAuditTrailsForGame(Integer gameId, String username);

	void saveAuditTrailForJoin(Player player);

	void saveAuditTrailForDice(Player player, Integer diceNumber);

	void saveAuditTrailForMove(Player player, Piece piece);

	void saveAuditTrailForLeave(Player player);

	void saveAuditTrailForFinish(Player player, Rank rank);
	
	void logFailedLogin(String username);

}
