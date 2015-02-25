package edu.gmu.ludo.service.impl;

import static edu.gmu.ludo.entity.AuditTrail.MoveType.LEFT;
import static edu.gmu.ludo.entity.AuditTrail.MoveType.MOVE;
import static edu.gmu.ludo.entity.AuditTrail.MoveType.ROLL;
import static edu.gmu.ludo.entity.GameBoard.GameState.CREATED;
import static edu.gmu.ludo.entity.GameBoard.GameState.FINISHED;
import static edu.gmu.ludo.entity.GameBoard.GameState.STARTED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.gmu.ludo.controller.validator.GeneralValidator;
import edu.gmu.ludo.dao.AuditTrailDao;
import edu.gmu.ludo.dao.GameDao;
import edu.gmu.ludo.dao.PlayerDao;
import edu.gmu.ludo.dao.UserDao;
import edu.gmu.ludo.entity.AuditTrail;
import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.Piece;
import edu.gmu.ludo.entity.Player;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.AuditTrail.MoveType;
import edu.gmu.ludo.entity.Player.Rank;
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;

@Service
@Transactional
public class LudoServiceImpl implements LudoService, UserDetailsService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private AuditTrailDao auditTrailDao;
	@Autowired
	private ShaPasswordEncoder passwordEncoder;

	@Override
	public void registerUser(User user) throws LudoException {
		List<String> errorList = validateUser(user);
		if (!errorList.isEmpty()) {
			throw new LudoException(errorList);
		}
		user.encodePassword(passwordEncoder);
		userDao.saveOrUpdate(user);
	}

	public List<String> validateUser(User user) {
		//SWE_681 second layer validation in service layer. 
		Map<String, String> validatationResult = GeneralValidator.validateUser(user);
		return new ArrayList<>(validatationResult.values());
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if(user == null)
			throw new UsernameNotFoundException("Username is invalid");
		return user;
	}

	@Override
	public User getUserByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public List<GameBoard> getJoinableGames() {
		return gameDao.getGamesByFilter(CREATED);
	}

	@Override
	public GameBoard getGameById(Integer gameId) {
		return gameDao.findById(gameId);
	}

	@Override
	public void createGame(GameBoard game) throws LudoException {
		Player owner = game.getOwner();
		List<GameBoard> joinableGames = getJoinableGames();
		for (GameBoard joinableGame : joinableGames) {
			if (joinableGame.getOwner().getUser().equals(owner.getUser())) {
				throw new LudoException("You have already created a game, please join <a href='./newGame/" + joinableGame.getId() + ".htm'>that game</a>");
			}
		}
		gameDao.save(game);
		playerDao.save(owner);
	}

	@Override
	public void updateGame(GameBoard game) throws LudoException {
		gameDao.update(game);
		for (Player player : game.getPlayers()) {
			playerDao.saveOrUpdate(player);
		}
	}

	@Override
	public GameBoard getResumableGame(User user) {
		List<GameBoard> games = gameDao.getGamesByFilter(STARTED, user);
		if (games.isEmpty())
			return null;
		return games.get(0);
	}

	@Override
	public List<GameBoard> getFinishedGames() {
		return gameDao.getGamesByFilter(FINISHED);
	}

	@Override
	public List<GameBoard> getFinishedGames(String owner, String player) {
		return gameDao.getGamesByFilter(FINISHED, player, owner);
	}

	@Override
	public List<AuditTrail> getAuditTrailsForGame(Integer gameId, String username) {
		return auditTrailDao.getAuditTrailsForGame(gameId, username);
	}

	@Override
	public Player getPlayerById(Integer playerId) {
		return playerDao.findById(playerId);
	}

	private void saveAuditTrail(AuditTrail at) {
		auditTrailDao.save(at);
	}

	@Override
	public void saveAuditTrailForJoin(Player player) {
		saveAuditTrail(new AuditTrail(player, MoveType.JOINED, null, null, null));
	}

	@Override
	public void saveAuditTrailForDice(Player player, Integer diceNumber) {
		saveAuditTrail(new AuditTrail(player, ROLL, diceNumber, null, null));
	}

	@Override
	public void saveAuditTrailForMove(Player player, Piece piece) {
		saveAuditTrail(new AuditTrail(player, MOVE, null, piece, null));
	}

	@Override
	public void saveAuditTrailForLeave(Player player) {
		saveAuditTrail(new AuditTrail(player, LEFT, null, null, null));
	}

	@Override
	public void saveAuditTrailForFinish(Player player, Rank rank) {
		saveAuditTrail(new AuditTrail(player, MoveType.FINISHED, null, null, rank));
	}

	@Override
	public void logFailedLogin(String username) {
		User user = userDao.findByUsername(username);
		if(user == null)
			return;
		user.addFailedAttempt();
		userDao.saveOrUpdate(user);
	}


}
