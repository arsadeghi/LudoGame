package edu.gmu.ludo.entity.form;

public class GameMove {

	private Integer pieceNumber;
	private Integer gameId;
	private Integer diceNumber;

	public GameMove() {
	}

	public GameMove(Integer gameId, Integer diceNumber) {
		this.gameId = gameId;
		this.diceNumber = diceNumber;
	}

	public Integer getPieceNumber() {
		return pieceNumber;
	}

	public void setPieceNumber(Integer pieceNumber) {
		this.pieceNumber = pieceNumber;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Integer getDiceNumber() {
		return diceNumber;
	}

	public void setDiceNumber(Integer diceNumber) {
		this.diceNumber = diceNumber;
	}

}
