package edu.gmu.ludo.entity;

import static edu.gmu.ludo.entity.Piece.PegNumber.FOUR;
import static edu.gmu.ludo.entity.Piece.PegNumber.ONE;
import static edu.gmu.ludo.entity.Piece.PegNumber.THREE;
import static edu.gmu.ludo.entity.Piece.PegNumber.TWO;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.gmu.ludo.entity.Piece.PegNumber;
import edu.gmu.ludo.entity.Piece.PositionType;
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;

@Entity
@Table(name = "player")
public class Player implements Comparable<Player> {

	@Id
	@GeneratedValue(strategy = AUTO)
	@Column(name = "player_id")
	private Integer id;
	@Enumerated(EnumType.STRING)
	@Column(name = "rank")
	private Rank rank;
	@Column(name = "finished", nullable = false)
	private boolean finished;
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "username")
	private User user;
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "game_id")
	private GameBoard playingBoard;
	@Column(name = "owner", nullable = false)
	private boolean owner;
	@Enumerated(EnumType.STRING)
	@Column(name = "color")
	private PlayerColor color;
	@Column(name = "last_dice_number")
	private Integer lastDiceNumber;
	@Column(name = "before_last_dice_number")
	private Integer beforeLastDiceNumber;
	@Column(name = "last_move_time")
	private Date lastMoveTime;
	@Transient
	List<Piece> pieces;
	@Transient
	private Integer gameId;

	public Player() {
	}

	public Player(GameBoard game) {
		this.gameId = game.getId();
	}

	Player(User user, String color, boolean owner, GameBoard game) {
		this.owner = owner;
		this.user = user;
		this.finished = false;
		this.playingBoard = game;
		this.pieces = new ArrayList<Piece>();
		for (PlayerColor playerColor : PlayerColor.values())
			if (playerColor.colorName.equals(color))
				this.color = playerColor;
		for (int i = 1; i <= GameBoard.PLAYER_PIECES_NUMBERS; i++)
			pieces.add(new Piece(this, i));
	}

	public List<Player> getCompetitors() {
		List<Player> result = new ArrayList<Player>();
		List<Player> players = playingBoard.getPlayers();
		for (Player player : players)
			if (!player.equals(this))
				result.add(player);
		return result;
	}

	public void makeAMovement(int diceNumber, int pieceNumber, boolean simulate, LudoService ludoService) throws LudoException {
		Piece piece = getPieceByNumber(pieceNumber);
		if (piece == null)
			throw new LudoException("No valid piece is selected");
		piece.makeAMovement(diceNumber, simulate);
		if (!simulate)
			checkAndSetFinishded(ludoService);
	}

	public boolean hasAvailableMovement(int diceNumber, LudoService ludoService) {
		for (Piece piece : pieces) {
			PositionType oldType = piece.positionType;
			int oldNumber = piece.relativeNumber;
			try {
				makeAMovement(diceNumber, piece.number.value, true, ludoService);
				piece.positionType = oldType;
				piece.relativeNumber = oldNumber;
				return true;
			} catch (LudoException e) {
			}
		}
		return false;
	}

	private void checkAndSetFinishded(LudoService ludoService) {
		for (Piece piece : pieces)
			if (piece.positionType != PositionType.HOME)
				return;
		setPlayerFinished(ludoService);
	}

	public void setPlayerFinished(LudoService ludoService) {
		this.finished = true;
		int rankNumber = 1;
		for (Player competitor : getCompetitors())
			if (competitor.finished)
				rankNumber++;
		for (Rank rank : Rank.all()) {
			if (rank.rankNum == rankNumber) {
				this.rank = rank;
				break;
			}
		}
		ludoService.saveAuditTrailForFinish(this, rank);
	}

	public Piece getPieceByNumber(int number) {
		for (Piece piece : pieces)
			if (piece.number.value == number)
				return piece;
		return null;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	@Override
	public String toString() {
		return user.getUsername() + "(" + color.name().toLowerCase() + ")";
	}

	@Override
	public int compareTo(Player that) {
		return this.rank.rankNum - that.rank.rankNum;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GameBoard getPlayingBoard() {
		return playingBoard;
	}

	public void setPlayingBoard(GameBoard playingBoard) {
		this.playingBoard = playingBoard;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public String getColorName() {
		if (color == null)
			return null;
		return color.colorName;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public void setColorName(String colorName) {
		for (PlayerColor playerColor : PlayerColor.values())
			if (playerColor.colorName.equalsIgnoreCase(colorName))
				this.color = playerColor;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public PlayerColor getColor() {
		return color;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}

	public Integer getLastDiceNumber() {
		return lastDiceNumber;
	}

	public void setLastDiceNumber(Integer lastDiceNumber) {
		this.lastDiceNumber = lastDiceNumber;
	}

	public Date getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(Date lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}

	public Integer getBeforeLastDiceNumber() {
		return beforeLastDiceNumber;
	}

	public void setBeforeLastDiceNumber(Integer beforeLastDiceNumber) {
		this.beforeLastDiceNumber = beforeLastDiceNumber;
	}

	public String getStatus() {
		Player currentPlayer = playingBoard.getCurrentPlayer();
		switch (rank) {
		case PLAYING:
			if (!user.equals(currentPlayer.user)) {
				return " waiting for his/her turn";
			}
			if (currentPlayer.lastDiceNumber == null)
				return " going to roll a dice";
			if (currentPlayer.lastDiceNumber == GameBoard.DICE_NUMBER_SIX)
				return "got a six! will repeat the his/her turn";
			return "got a " + currentPlayer.lastDiceNumber + ", will move a piece";
		case LEFT:
			return " left the game.";
		default:
			if (rank == Rank.R1) {
				return " won the game!";
			}
			return " got the " + rank.rankStr;
		}
	}

	public enum Rank {
		R1(1, "Winner!"), R2(2, "Second place"), R3(3, "Third place"), R4(4, "Fourth place"), PLAYING(6, "is playing..."), LEFT(5, "Left the game");

		private Rank(int rankNum, String rankStr) {
			this.rankStr = rankStr;
			this.rankNum = rankNum;
		}

		static Rank[] all() {
			return new Rank[] { R1, R2, R3, R4 };
		}

		String rankStr;
		int rankNum;

		public String getRankStr() {
			return rankStr;
		}

		public int getRankNum() {
			return rankNum;
		}
	}

	public enum PlayerColor {
		BLUE("Blue", 0), GREEN("Green", 10), YELLOW("Yellow", 20), RED("Red", 30);

		String colorName;
		int offset;

		private PlayerColor(String colorName, int offset) {
			this.offset = offset;
			this.colorName = colorName;
		}

		public String getColorName() {
			return colorName;
		}

	}

	private String getPieceByNumber(PegNumber number) {
		if (pieces != null && pieces.size() == 4)
			return pieces.get(number.value - 1).toString();
		return null;
	}

	private void setPieceByNumber(String codedValue, PegNumber number) {
		if (pieces == null)
			pieces = new ArrayList<>();
		pieces.add(number.value - 1, new Piece(this, number, codedValue));
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "p1", nullable = false)
	public String getP1() {
		return getPieceByNumber(ONE);
	}

	public void setP1(String p1) {
		setPieceByNumber(p1, ONE);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "p2", nullable = false)
	public String getP2() {
		return getPieceByNumber(TWO);
	}

	public void setP2(String p2) {
		setPieceByNumber(p2, TWO);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "p3", nullable = false)
	public String getP3() {
		return getPieceByNumber(THREE);
	}

	public void setP3(String p3) {
		setPieceByNumber(p3, THREE);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "p4", nullable = false)
	public String getP4() {
		return getPieceByNumber(FOUR);
	}

	public void setP4(String p4) {
		setPieceByNumber(p4, FOUR);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
