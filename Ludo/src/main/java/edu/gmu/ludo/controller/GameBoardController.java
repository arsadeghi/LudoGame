package edu.gmu.ludo.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.gmu.ludo.controller.validator.GeneralValidator;
import edu.gmu.ludo.entity.GameBoard;
import edu.gmu.ludo.entity.Player;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.GameBoard.GameState;
import edu.gmu.ludo.entity.Player.PlayerColor;
import edu.gmu.ludo.entity.form.GameMove;
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;
import edu.gmu.ludo.view.HTMLGenerator;

@Controller
public class GameBoardController {

	public static final String RETURN_PAGE_LIST = "listGames";

	@Autowired
	@Qualifier("ludoServiceImpl")
	private LudoService ludoService;
	@Autowired
	private GameManagementContoller managementContoller;
	// SWE_681 binding a validator to this controller
	@Autowired
	private GeneralValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "/playGame.htm")
	public String invalid(Model model) {
		return managementContoller.showGamesList(model, "Please join a game to play.");
	}

	@RequestMapping(value = "/playGame.htm", method = RequestMethod.POST, params = { "move" })
	public String move(Model model, @ModelAttribute @Valid GameMove move, Principal principal, BindingResult result) {
		return playGame(model, principal, move.getPieceNumber(), null, move.getGameId(), true, result);
	}

	@RequestMapping(value = "/playGame.htm", method = RequestMethod.POST, params = { "roll" })
	public String roll(Model model, @ModelAttribute @Valid GameMove move, Principal principal, BindingResult result) {
		return playGame(model, principal, null, GameBoard.rollDice(), move.getGameId(), true, result);
	}

	@RequestMapping(value = "/playGame/{gameId}.htm", method = RequestMethod.GET)
	public String refresh(Model model, Principal principal, @PathVariable("gameId") Integer gameId) {
		return playGame(model, principal, null, null, gameId, false, null);
	}

	public String playGame(Model model, Principal principal, Integer pieceNumber, Integer diceNumber, Integer gameId, boolean currentPlayerValidAction,
			BindingResult result) {
		User user = ludoService.getUserByUsername(principal.getName());
		GameBoard game = new GameBoard();
		try {
			game = loadGameById(ludoService, gameId);
			checkGameValidity(game, user, currentPlayerValidAction, false, true, new GameState[] { GameState.STARTED }, ludoService);
			if (result == null || !result.hasErrors()) {
				game.playGame(ludoService, pieceNumber, diceNumber);
			}
			setPlayers(model, game, user);
			return "board";
		} catch (LudoException e) {
			if (RETURN_PAGE_LIST.equals(e.getReturnPage())) {
				return managementContoller.showGamesList(model, e.getMessage());
			}
			model.addAttribute("errorMsgs", e.getErrorMsgs());
			setPlayers(model, game, user);
			return "board";
		}
	}

	public static void setPlayers(Model model, GameBoard game, User user) {
		Player thisPlayer = game.getPlayerByUser(user);
		model.addAttribute("thisPlayer", thisPlayer);
		model.addAttribute("currentPlayer", game.getCurrentPlayer());
		List<Player> players = new ArrayList<>();
		for (PlayerColor color : PlayerColor.values()) {
			Player player = game.getPlayerByColor(color);
			if (player != null) {
				model.addAttribute(color.name() + "_player", player);
				players.add(player);
			}
		}
		model.addAttribute("players", players);
		model.addAttribute("gameMove", new GameMove(game.getId(), thisPlayer.getLastDiceNumber()));
		model.addAttribute("isFinished", game.isFinished());
	}

	public static GameBoard loadGameById(LudoService ludoService, Integer gameId) throws LudoException {
		// SWE_681 Check if an injected (invalid) gameId added to URL
		GameBoard game = ludoService.getGameById(gameId);
		if (game == null) {
			throw new LudoException("No game available with id " + gameId, RETURN_PAGE_LIST);
		}
		return game;
	}

	public static void checkGameValidity(GameBoard game, User user, boolean currentPlayerValidAction, boolean ownerValidAction, boolean alivePlayerValidAction,
			GameState[] validState, LudoService ludoService) throws LudoException {
		// SWE_681 Rejects if an unauthorized player (who is not in the game)
		// tries to join an started game
		if (!game.isUserAlreadyJoined(user)) {
			throw new LudoException("You have not joined this game!", RETURN_PAGE_LIST);
		}
		// SWE_681 Make sure if all players are alive and expels the players who
		// have left the game.
		game.checkPlayersAreAlive(ludoService);
		// SWE_681 Rejects if the user tries to do any action that is not valid
		// according to the game state, for example only "created" game could be
		// started, or the "started" game could be played.
		if (!Arrays.asList(validState).contains(game.getGameState())) {
			throw new LudoException("This action is not valid according to the game state (" + game.getGameState().name() + ")");
		}
		// SWE_681 Rejects if an finished player (who has leaved or won) tries
		// to make a move
		if (alivePlayerValidAction && game.getPlayerByUser(user).isFinished()) {
			throw new LudoException("You have finished or left the game.");
		}
		// SWE_681 Rejects if an unauthorized (who is not the game's owner)
		// tries to do some action that is just valid for the game's owner, for
		// example starting the game.
		if (ownerValidAction && !game.getOwner().getUser().equals(user)) {
			throw new LudoException("Just the games owner is authorized to do this action.");
		}
		// SWE_681 Rejects if a players tries to inject form fields to play out
		// of his/her turn.
		if (currentPlayerValidAction && !game.getCurrentPlayer().getUser().equals(user)) {
			throw new LudoException("It is not your turn, please wait!");
		}
	}

	@RequestMapping(value = "/playGame.ajax", params = { "updateStatus" })
	public @ResponseBody
	String getUserStatus(GameMove move, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		try {
			GameBoard game = loadGameById(ludoService, move.getGameId());
			checkGameValidity(game, user, false, false, false, new GameState[] { GameState.STARTED, GameState.FINISHED }, ludoService);
			return HTMLGenerator.generatePlayerPanel(game, game.getPlayerByUser(user));
		} catch (LudoException e) {
			return "";
		}
	}

	@RequestMapping(value = "/playGame.ajax", params = { "updateBoard" })
	public @ResponseBody
	String getBoard(GameMove move, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		try {
			GameBoard game = loadGameById(ludoService, move.getGameId());
			checkGameValidity(game, user, false, false, false, new GameState[] { GameState.STARTED, GameState.FINISHED }, ludoService);
			return HTMLGenerator.generateGameBoard(game.getPlayers(), game.getPlayerByUser(user), game.getCurrentPlayer());
		} catch (LudoException e) {
			return "Cannot load the game!, Error: " + e.getMessage();
		}
	}

}
