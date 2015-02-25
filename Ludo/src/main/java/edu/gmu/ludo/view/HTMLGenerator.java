package edu.gmu.ludo.view;

import static edu.gmu.ludo.entity.GameBoard.BOARD_ROW_CELLS_NUMBERS;

import java.util.Arrays;
import java.util.List;

import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.Piece;
import edu.gmu.ludo.entity.Player;
import edu.gmu.ludo.entity.Player.PlayerColor;

public class HTMLGenerator {

	public static final int DICE_CELL_INDEX = 61;
	public static final Integer[] NOT_BOARD_CELLS = { 3, 4, 8, 9, 14, 15, 23, 24, 25, 26, 30, 31, 32, 33, 34, 35, 36, 37, 41, 42, 43, 44, 61, 78, 79, 80, 81,
			85, 86, 87, 88, 89, 90, 91, 92, 96, 97, 98, 99, 102, 103, 107, 108, 113, 114, 118, 119 };

	public static String generateGameBoard(List<Player> players, Player thisPlayer, Player currentPlayer) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"board\" border=\"1\">\n");
		for (int row = 1; row <= BOARD_ROW_CELLS_NUMBERS; row++) {
			sb.append("\t<tr class=\"board\">\n");
			for (int col = 1; col <= BOARD_ROW_CELLS_NUMBERS; col++) {
				long cellIndex = (row - 1) * BOARD_ROW_CELLS_NUMBERS + col;
				if (cellIndex == DICE_CELL_INDEX) {
					boolean canRoll = (currentPlayer.equals(thisPlayer)) && (!currentPlayer.isFinished()) && (currentPlayer.getLastDiceNumber() == null);
					String diceNumber = currentPlayer.getLastDiceNumber() == null ? thisPlayer.getBeforeLastDiceNumber() + "" : Integer.toString(currentPlayer
							.getLastDiceNumber());
					String diceCls = "dice dic_" + (canRoll ? "null" : diceNumber);
					sb.append("\t\t<td class=\"board " + diceCls + "\" onclick=\"submitForm('roll')\">");
				} else {
					sb.append("\t\t<td class=\"board\">");
					if (isCellInGame(cellIndex)) {
						for (Player player : players) {
							for (Piece piece : player.getPieces()) {
								if (cellIndex == piece.getBoardCellNumber()) {
									String cls = "peg " + player.getColorName() + "_Peg";
									boolean moveable = player.equals(thisPlayer) && thisPlayer.equals(currentPlayer) && thisPlayer.getLastDiceNumber() != null;
									String onClick = moveable ? ("onclick=\"submitForm('move', '" + piece.getNumber().getValue() + "')\"") : "";
									sb.append("<div class=\"" + cls + "\" " + onClick + ">" + piece.getNumber().getValue() + "</div>");
								}
							}
						}
					}
				}
				sb.append("\t\t</td>\n");
			}
			sb.append("\t</tr>\n");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String generatePlayerPanel(GameBoard game, Player thisPlayer) {
		StringBuilder sb = new StringBuilder();
		for (PlayerColor color : PlayerColor.values()) {
			Player player = game.getPlayerByColor(color);
			sb.append(sb.length() == 0 ? "" : "#").append(player == null ? " " : generatePlayerPanel(player, thisPlayer, game.getCurrentPlayer()));
		}
		return sb.toString();
	}

	public static String generateJoinedPlayersList(List<Player> players) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"data\" style=\"width: 250px;\">");
		int counter = 1;
		for (Player player : players) {
			sb.append("<tr>");
			sb.append("<td>" + (counter++) + "</td>");
			sb.append("<td class='avatar " + player.getColorName() + "' align='right' style='background-image: url(/Ludo/resources/imgs/avatar/"
					+ player.getUser().getAvatar() + ".png)'>" + player.getUser().getName() + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String generateAllJoinableGameList(List<GameBoard> games) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"data\"style=\"width: 450px;\"><tr><th>&nbsp;</th><th>Game owner</th><th>Joined players</th><th></th>");
		int counter = 1;
		for (GameBoard game : games) {
			sb.append("<tr>");
			sb.append("<td class='data'>" + (counter++) + "</td>");
			sb.append("<td class='avatar " + game.getOwner().getColorName() + "' align='right' style='background-image: url(/Ludo/resources/imgs/avatar/"
					+ game.getOwner().getUser().getAvatar() + ".png)'>" + game.getOwner().getUser().getName() + "</td>");
			sb.append("<td class='data'>" + game.getJoinedPlayersNumber() + "</td>");
			sb.append("<td class='data'><a href=\"/Ludo/newGame/" + game.getId() + ".htm\">Join Now</a></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	static boolean isCellInGame(Long cellIndex) {
		return (cellIndex != null && !Arrays.asList(NOT_BOARD_CELLS).contains(cellIndex.intValue()));
	}

	static String generatePlayerPanel(Player player, Player thisPlayer, Player currentPlayer) {
		StringBuilder sb = new StringBuilder();
		if (player != null) {
			String cls = player.getColorName().toLowerCase() + (currentPlayer.equals(player) ? "_active" : "");
			sb.append("<div class=\"playerInfo " + cls + "\"><p>\n");
			String name = (player.equals(thisPlayer)) ? "You" : player.getUser().getName();
			sb.append("<img src=\"/Ludo/resources/imgs/avatar/" + player.getUser().getAvatar() + ".png\" width='45%' height='45%'/><br>");
			sb.append("\t<span>" + name + "</span><br>\n");
			sb.append("\t<span class=\"status\" id=\"red_status\">" + player.getStatus() + "</span>\n");
			sb.append("</p></div>");
		}
		return sb.toString();
	}

}
