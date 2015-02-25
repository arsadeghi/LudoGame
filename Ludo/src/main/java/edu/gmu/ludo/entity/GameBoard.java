package edu.gmu.ludo.entity;

import static edu.gmu.ludo.entity.Player.Rank.LEFT;
import static edu.gmu.ludo.entity.Player.Rank.PLAYING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.gmu.ludo.entity.Player.PlayerColor;
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;

@Entity
@Table(name = "game")
public class GameBoard {

	// SWE_681 5 minutes is maximum wait time
	public static final int MAX_WAIT_TIME = 300000;
	public static final int BOARD_ROW_CELLS_NUMBERS = 11;
	public static final int BOARD_POSITONS_TOTAL_NUMBERS = 40;
	public static final int PLAYER_PIECES_NUMBERS = 4;
	public static final int MAX_PLAYERS = 4;
	public static final int DICE_NUMBER_SIX = 6;

	@Id
	@GeneratedValue(strategy = AUTO)
	@Column(name = "game_id")
	private Integer id;
	@Column(name = "start_time")
	private Date startTime;
	@Column(name = "end_time")
	private Date endTime;
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	private GameState gameState;
	@OneToMany(mappedBy = "playingBoard", fetch = EAGER)
	private List<Player> players;
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "current_player_id")
	private Player currentPlayer;

	public GameBoard() {
	}

	GameBoard(User owner, String color) {
		this.gameState = GameState.CREATED;
		this.players = new ArrayList<Player>();
		Player ownerPlayer = new Player(owner, color, true, this);
		this.players.add(ownerPlayer);
	}

	public static GameBoard createNewGame(User owner, String color, LudoService ludoService) throws LudoException {
		GameBoard game = new GameBoard(owner, color);
		ludoService.createGame(game);
		return game;
	}

	public synchronized void addNewPlayer(User user, String color, LudoService ludoService) throws LudoException {
		// SWE_681 check if a user tries to manipulate the URL to join an
		// already started/finished game
		if (gameState != GameState.CREATED)
			throw new LudoException("Cannot join the game, because it is already " + gameState.name().toLowerCase() + ".");
		// SWE_681 check the capacity of the at server side to prevent the race
		// condition (when two or more users trying to simultaneously join a
		// game that leads creation a game with over-capacity)
		if (players.size() >= MAX_PLAYERS)
			throw new LudoException("Cannot join the game, because game is full.");
		Player newPlayer = new Player(user, color, false, this);
		players.add(newPlayer);
		ludoService.updateGame(this);
	}

	public enum GameState {
		CREATED, STARTED, FINISHED;
	}

	public List<PlayerColor> getAvailableColors() {
		ArrayList<PlayerColor> result = new ArrayList<PlayerColor>();
		ArrayList<PlayerColor> used = new ArrayList<PlayerColor>();
		for (Player player : players)
			used.add(player.getColor());
		for (PlayerColor color : PlayerColor.values())
			if (!used.contains(color))
				result.add(color);
		return result;
	}

	public Player switchToNextPlayer() {
		int totalPlayers = players.size();
		do {
			int currentPlayerIndex = players.indexOf(currentPlayer);
			int nextPlayerIndex = (currentPlayerIndex + 1) % totalPlayers;
			this.currentPlayer = players.get(nextPlayerIndex);
		} while (currentPlayer.isFinished() && !isFinished());
		return currentPlayer;
	}

	public void startGame(LudoService ludoService) throws LudoException {
		if (players.size() < 2)
			throw new LudoException("Game could be started with at least two players");
		for (Player player : players) {
			player.setRank(PLAYING);
			player.setLastMoveTime(Calendar.getInstance().getTime());
		}
		startTime = Calendar.getInstance().getTime();
		gameState = GameState.STARTED;
		if (currentPlayer == null)
			this.currentPlayer = players.get(0);
		ludoService.updateGame(this);
	}

	public void playForCurrentPlayer(int diceNumber, int pieceNumber, LudoService ludoService) throws LudoException {
		currentPlayer.makeAMovement(diceNumber, pieceNumber, false, ludoService);
		checkGameFinished(ludoService);
	}

	public static Integer rollDice() {
		// SWE_681 Random number generator
		return new SecureRandom().nextInt(DICE_NUMBER_SIX) + 1;
	}

	private void checkGameFinished(LudoService ludoService) {
		int finishedNumber = 1;
		for (Player player : players)
			if (player.isFinished())
				finishedNumber++;
		if (finishedNumber >= players.size()) {
			for (Player player : players)
				if (!player.isFinished())
					player.setPlayerFinished(ludoService);
			this.gameState = GameState.FINISHED;
			this.endTime = Calendar.getInstance().getTime();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Game state is " + gameState.toString().toLowerCase() + "\n");
		for (Player player : players) {
			sb.append(player + " (");
			for (Piece piece : player.getPieces()) {
				sb.append(piece + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(")\n");
		}
		return sb.toString();
	}

	public String getRanking() {
		StringBuilder sb = new StringBuilder();
		Collections.sort(players);
		for (Player player : players) {
			sb.append("<tr><td class='avatar " + player.getColorName() + "' align='right' style='background-image: url(resources/imgs/avatar/"
					+ player.getUser().getAvatar() + ".png)'>" + player.getRank().rankStr + ": " + player.getUser().getName() + "</td></tr>");
		}
		return sb.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Player getOwner() {
		for (Player player : players) {
			if (player.isOwner()) {
				return player;
			}
		}
		return null;
	}

	public boolean isUserAlreadyJoined(User user) {
		return (getPlayerByUser(user) != null);
	}

	public Player getPlayerByUser(User user) {
		for (Player player : players) {
			if (player.getUser().equals(user)) {
				return player;
			}
		}
		return null;
	}

	public Player getPlayerByColor(PlayerColor color) {
		for (Player player : players) {
			if (player.getColor() == color) {
				return player;
			}
		}
		return null;
	}

	public int getJoinedPlayersNumber() {
		return players.size() - 1;
	}

	public void playGame(LudoService ludoService, Integer pieceNumber, Integer diceNumber) throws LudoException {
		if (pieceNumber != null) {
			movePiece(ludoService, pieceNumber, false, null);
		}
		if (diceNumber != null) {
			rollDice(ludoService, diceNumber);
		}
	}

	private void movePiece(LudoService ludoService, Integer pieceNumber, boolean skip, Integer diceNumber) throws LudoException {
		Integer lastDiceNumber = currentPlayer.getLastDiceNumber();
		// SWE_681 white-box checking of the selected piece number
		if (!skip) {
			if (pieceNumber >= 1 && pieceNumber <= 4) {
				playForCurrentPlayer(lastDiceNumber, pieceNumber, ludoService);
			} else {
				throw new LudoException("You should select a valid piece to move");
			}
		}
		currentPlayer.setBeforeLastDiceNumber(lastDiceNumber);
		currentPlayer.setLastDiceNumber(null);
		if (!skip) {
			ludoService.saveAuditTrailForMove(currentPlayer, currentPlayer.getPieceByNumber(pieceNumber));
		}
		if (lastDiceNumber != GameBoard.DICE_NUMBER_SIX || currentPlayer.isFinished()) {
			switchToNextPlayer();
		}
		ludoService.updateGame(this);
		if (skip) {
			throw new LudoException("You can not have make any movement with dice number " + diceNumber);
		}
	}

	private void rollDice(LudoService ludoService, Integer diceNumber) throws LudoException {
		// SWE_681 check if the player has not already rolled a dice, otherwise
		// a player can re-submit the page form (with roll parameter) to roll a
		// new
		// dice
		if (currentPlayer.getLastDiceNumber() != null)
			return;
		// SWE_681 Permanently saving the dice number (into the DB), immediately
		// after rolling the dice. This prevents a possible cheat (when the dice
		// number saved in the session) that a user
		// disconnects/connects the game after rolling a dice and its number is
		// not desirable, and he/she could roll the dice again.
		currentPlayer.setLastDiceNumber(diceNumber);
		// SWE_681 Reset the last played time for all player after any player
		// roll a dice. It should be done for all player (and not just the
		// current player), because a player who is wasting his/her time should
		// not also consumes the other times.
		for (Player player : players) {
			player.setLastMoveTime(Calendar.getInstance().getTime());
		}
		if (!getCurrentPlayer().hasAvailableMovement(diceNumber, ludoService)) {
			movePiece(ludoService, null, true, diceNumber);
		}
		ludoService.saveAuditTrailForDice(currentPlayer, diceNumber);
		ludoService.updateGame(this);
	}

	public void checkPlayersAreAlive(LudoService ludoService) throws LudoException {
		// SWE_681 Expels every player who is idle a long time (MAX_WAIT_TIME)
		long currentTime = Calendar.getInstance().getTime().getTime();
		for (Player player : players) {
			long lastPlayedTime = player.getLastMoveTime() == null ? currentTime : player.getLastMoveTime().getTime();
			if ((currentTime - lastPlayedTime) > MAX_WAIT_TIME) {
				player.setRank(LEFT);
				player.setFinished(true);
			}
		}
		// SWE_681 if all players have left the game, the game should be
		// considered as finished.
		if (gameState == GameState.STARTED)
			checkGameFinished(ludoService);
		ludoService.updateGame(this);
	}

	public void leaveGame(LudoService ludoService, User user) throws LudoException {
		Player player = getPlayerByUser(user);
		player.setRank(LEFT);
		player.setFinished(true);
		if (gameState == GameState.STARTED)
			checkGameFinished(ludoService);
		ludoService.updateGame(this);
	}

	public boolean isFinished() {
		return gameState == GameState.FINISHED;
	}

	public String getDuration() {
		if (!isFinished() || startTime == null || endTime == null)
			return "N/A";
		return Long.toString((endTime.getTime() - startTime.getTime()) / (60000)) + " mins";
	}
}
