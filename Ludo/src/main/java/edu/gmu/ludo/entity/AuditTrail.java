package edu.gmu.ludo.entity;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.gmu.ludo.entity.Piece.PegNumber;
import edu.gmu.ludo.entity.Player.Rank;

@Entity
@Table(name = "audit_trail")
public class AuditTrail {

	@Id
	@GeneratedValue(strategy = AUTO)
	@Column(name = "audit_trail_id")
	private Integer id;
	@Column(name = "time", nullable = false)
	private Date time;
	@Enumerated(EnumType.STRING)
	@Column(name = "move_type", nullable = false)
	private MoveType moveType;
	@Column(name = "dice_number")
	private Integer diceNumber;
	@Enumerated(EnumType.STRING)
	@Column(name = "peg_number")
	private PegNumber pegNumber;
	@Column(name = "destination")
	private String destination;
	@Enumerated(EnumType.STRING)
	@Column(name = "rank")
	private Rank rank;
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "game_id")
	private GameBoard game;
	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "username")
	private User user;

	public AuditTrail() {
	}

	public AuditTrail(Player player, MoveType moveType, Integer diceNumber, Piece piece, Rank rank) {
		this.game = player.getPlayingBoard();
		this.user = player.getUser();
		this.moveType = moveType;
		this.diceNumber = diceNumber;
		if (piece != null){
			this.pegNumber = piece.number;
			this.destination = piece.getSpec();
		}
		this.time = Calendar.getInstance().getTime();
		this.rank = rank;
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

	public GameBoard getGame() {
		return game;
	}

	public void setGame(GameBoard game) {
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public MoveType getMoveType() {
		return moveType;
	}

	public void setMoveType(MoveType moveType) {
		this.moveType = moveType;
	}

	public Integer getDiceNumber() {
		return diceNumber;
	}

	public void setDiceNumber(Integer diceNumber) {
		this.diceNumber = diceNumber;
	}

	public PegNumber getPegNumber() {
		return pegNumber;
	}

	public void setPegNumber(PegNumber pegNumber) {
		this.pegNumber = pegNumber;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public enum MoveType {
		ROLL, MOVE, LEFT, FINISHED, JOINED;
	}

	public String getMoveDesc() {
		switch (moveType) {
		case ROLL:
			return "Rolled dice, dice number was " + diceNumber;
		case MOVE:
			return "Moved to " + destination;
		case FINISHED:
			switch (rank) {
			case R1:
				return "Won the game";
			default:
				return "Finished the game with rank " + rank.rankStr;
			}
		case LEFT:
			return "Left the game";
		case JOINED:
			return "Joined the game";
		default:
			return "N/A";
		}
	}

	public String getTdClass(){
		switch (moveType) {
		case ROLL:
			return "roll";
		case MOVE:
			return "move";
		case FINISHED:
			switch (rank) {
			case R1:
				return "gold";
			default:
				return "finished";
			}
		case LEFT:
			return "left";
		case JOINED:
			return "joined";
		default:
			return "";
		}
	}
}
