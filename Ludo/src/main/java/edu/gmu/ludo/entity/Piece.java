package edu.gmu.ludo.entity;

import edu.gmu.ludo.service.LudoException;
import static edu.gmu.ludo.entity.GameBoard.BOARD_POSITONS_TOTAL_NUMBERS;
import static edu.gmu.ludo.entity.GameBoard.BOARD_ROW_CELLS_NUMBERS;
import static edu.gmu.ludo.entity.Piece.PositionType.BOARD;
import static edu.gmu.ludo.entity.Piece.PositionType.HOME;
import static edu.gmu.ludo.entity.Piece.PositionType.OUT;

public class Piece {

	Player player;
	PegNumber number;
	PositionType positionType;
	int relativeNumber;

	public Piece(Player player, PegNumber number, String codedValue) {
		this.player = player;
		this.number = number;
		String[] codedValues = codedValue.split("_");
		switch (codedValues[0]) {
		case "H":
			this.positionType = HOME;
			break;
		case "O":
			this.positionType = OUT;
			break;
		case "B":
			this.positionType = BOARD;
			break;
		}
		this.relativeNumber = Integer.parseInt(codedValues[1]);
	}

	public Piece(Player player, int number) {
		this.player = player;
		this.positionType = OUT;
		for (PegNumber pegNumber : PegNumber.all())
			if (pegNumber.value == number)
				this.number = pegNumber;
		this.relativeNumber = number;
	}

	public void makeAMovement(int diceNumber, boolean simulate) throws LudoException {
		int prePos = relativeNumber;
		PositionType preType = positionType;
		try {
			tryToMoveFwd(diceNumber);
			checkForCollisions(simulate);
		} catch (LudoException e) {
			this.relativeNumber = prePos;
			this.positionType = preType;
			throw e;
		}
	}

	private void startToPlay() {
		this.positionType = BOARD;
		this.relativeNumber = 1;
	}

	private void throwOut() {
		this.positionType = OUT;
		this.relativeNumber = number.value;
	}

	private void welcomeHome(int steps) throws LudoException {
		this.positionType = HOME;
		this.relativeNumber = steps;
		if (this.relativeNumber > GameBoard.PLAYER_PIECES_NUMBERS)
			throw new LudoException("Cannot move the piece");
	}

	private void checkForCollisions(boolean simulate) throws LudoException {
		for (Piece peg : player.getPieces())
			if (hasCollision(peg))
				throw new LudoException("Cannot move the piece to an already occupied position by the same player.");
		if (simulate)
			return;
		for (Player competitor : player.getCompetitors())
			for (Piece other : competitor.getPieces())
				if (hasCollision(other))
					other.throwOut();
	}

	private void tryToMoveFwd(int diceNumber) throws LudoException {
		switch (positionType) {
		case OUT:
			if (diceNumber != GameBoard.DICE_NUMBER_SIX)
				throw new LudoException("Cannot move the piece from the outside");
			startToPlay();
			break;
		case BOARD:
			this.relativeNumber += diceNumber;
			if (relativeNumber > BOARD_POSITONS_TOTAL_NUMBERS)
				welcomeHome(relativeNumber - BOARD_POSITONS_TOTAL_NUMBERS);
			break;
		case HOME:
			this.relativeNumber += diceNumber;
			if (this.relativeNumber > GameBoard.PLAYER_PIECES_NUMBERS)
				throw new LudoException("Cannot move the piece");
			break;
		}
	}

	private int getAbsolutePositionNumber() {
		int abs = (relativeNumber + player.getColor().offset) % (BOARD_POSITONS_TOTAL_NUMBERS);
		return (abs == 0 ? BOARD_POSITONS_TOTAL_NUMBERS : abs);
	}

	private boolean hasCollision(Piece other) {
		if (other.player.equals(this.player) && (other.number == this.number))
			return false;
		if (this.positionType != other.positionType)
			return false;
		if (!other.player.equals(this.player) && (this.positionType != BOARD))
			return false;
		return this.getAbsolutePositionNumber() == other.getAbsolutePositionNumber();
	}

	public PegNumber getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return positionType.name().charAt(0) + "_" + relativeNumber;
	}

	public String getSpec() {
		return positionType.name().toLowerCase() + " " + relativeNumber;
	}

	public int getBoardCellNumber() {
		switch (positionType) {
		case OUT:
			return getBoardOutCellNumber();
		case BOARD:
			return getBoardInCellNumber();
		case HOME:
			return getBoardHomeCellNumber();
		}
		return 0;
	}

	private int getBoardOutCellNumber() {
		switch (player.getColor()) {
		case BLUE:
			switch (number) {
			case ONE:
				return 10;
			case TWO:
				return 11;
			case THREE:
				return 21;
			case FOUR:
				return 22;
			}
		case GREEN:
			switch (number) {
			case ONE:
				return 109;
			case TWO:
				return 110;
			case THREE:
				return 120;
			case FOUR:
				return 121;
			}
		case RED:
			switch (number) {
			case ONE:
				return 1;
			case TWO:
				return 2;
			case THREE:
				return 12;
			case FOUR:
				return 13;
			}
		case YELLOW:
			switch (number) {
			case ONE:
				return 100;
			case TWO:
				return 101;
			case THREE:
				return 111;
			case FOUR:
				return 112;
			}
		}
		return 0;
	}

	private int getBoardHomeCellNumber() {
		switch (player.getColor()) {
		case BLUE:
			return (BOARD_ROW_CELLS_NUMBERS * relativeNumber + 6);
		case GREEN:
			return 66 - relativeNumber;
		case RED:
			return 56 + relativeNumber;
		case YELLOW:
			return (BOARD_ROW_CELLS_NUMBERS * (4 - relativeNumber)) + 72;
		}
		return 0;
	}

	private int getBoardInCellNumber() {
		int abs = getAbsolutePositionNumber();
		if (abs <= 5) {
			return (abs - 1) * BOARD_ROW_CELLS_NUMBERS + 7;
		} else if (abs > 5 && abs <= 9) {
			return abs + 46;
		} else if (abs == 10) {
			return 66;
		} else if (abs > 10 && abs <= 15) {
			return 88 - abs;
		} else if (abs > 15 && abs <= 19) {
			return (abs - 15) * BOARD_ROW_CELLS_NUMBERS + 73;
		} else if (abs == 20) {
			return 116;
		} else if (abs > 20 && abs <= 25) {
			return (25 - abs) * BOARD_ROW_CELLS_NUMBERS + 71;
		} else if (abs > 25 && abs <= 29) {
			return 96 - abs;
		} else if (abs == 30) {
			return 56;
		} else if (abs > 30 && abs <= 34) {
			return abs + 14;
		} else if (abs > 34 && abs < 40) {
			return (39 - abs) * BOARD_ROW_CELLS_NUMBERS + 5;
		} else if (abs == 40) {
			return 6;
		}
		return 41;// invalid
	}

	public enum PositionType {
		HOME, BOARD, OUT;
	}

	public enum PegNumber {
		ONE(1), TWO(2), THREE(3), FOUR(4);
		private PegNumber(int value) {
			this.value = value;
		}

		static PegNumber[] all() {
			return new PegNumber[] { ONE, TWO, THREE, FOUR };
		}

		int value;

		public int getValue() {
			return value;
		}

	}
}
