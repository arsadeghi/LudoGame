package edu.gmu.ludo.controller;

import java.security.Principal;

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
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;
import edu.gmu.ludo.view.HTMLGenerator;

@Controller
public class GameManagementContoller {

	@Autowired
	@Qualifier("ludoServiceImpl")
	private LudoService ludoService;
	@Autowired
	private GameBoardController boardController;
	// SWE_681 binding a validator to this controller
	@Autowired
	private GeneralValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "/error.htm")
	public String errorPage(Model model, Principal principal) {
		model.addAttribute("errorMsgs", "The system cannot fulfill your request.");
		return main(model, principal);
	}

	@RequestMapping(value = "/index.htm")
	public String main(Model model, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		GameBoard resumableGame = ludoService.getResumableGame(user);
		if (resumableGame != null) {
			model.addAttribute("resumableGameId", resumableGame.getId());
		}
		return "index";
	}

	@RequestMapping(value = "/newGame.htm")
	public String newGame(Model model, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		model.addAttribute("colorList", PlayerColor.values());
		model.addAttribute("owner", true);
		model.addAttribute("joined", false);
		model.addAttribute("player", new Player());
		GameBoard resumableGame = ludoService.getResumableGame(user);
		if (resumableGame != null) {
			model.addAttribute("errorMsgs", "Resume or left the game you you have already joined.</a>");
			return main(model, principal);
		}
		return "gameManager/newGame";
	}

	@RequestMapping(value = "/newGame/{gameId}.htm")
	public String laodGame(Model model, @PathVariable("gameId") Integer gameId, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		GameBoard game = ludoService.getGameById(gameId);
		if (game == null) {
			return showGamesList(model, "No game available with id " + gameId);
		}
		if (game.isUserAlreadyJoined(user)) {
			if (game.getGameState() == GameState.STARTED) {
				return boardController.refresh(model, principal, gameId);
			}
			model.addAttribute("joined", true);
		} else {
			model.addAttribute("joined", false);
		}
		if (user.equals(game.getOwner().getUser())) {
			model.addAttribute("owner", true);
		} else {
			model.addAttribute("owner", false);
		}
		model.addAttribute("player", new Player(game));
		model.addAttribute("players", game.getPlayers());
		model.addAttribute("colorList", game.getAvailableColors());
		return "gameManager/newGame";
	}

	// SWE_681 limiting method type to post
	@RequestMapping(value = "/joinGame.htm", method = RequestMethod.POST, params = { "create" })
	public String createGame(Model model, @ModelAttribute @Valid Player player, Principal principal, BindingResult result) {
		if (result.hasErrors()) {
			return newGame(model, principal);
		}
		User owner = ludoService.getUserByUsername(principal.getName());
		try {
			GameBoard game = GameBoard.createNewGame(owner, player.getColorName(), ludoService);
			ludoService.saveAuditTrailForJoin(game.getPlayerByUser(owner));
			model.addAttribute("players", game.getPlayers());
			model.addAttribute("owner", true);
			model.addAttribute("joined", true);
			model.addAttribute("successMsgs", "The game created successfully, please wait for other player(s) to join the game.");
			return laodGame(model, game.getId(), principal);
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
			return newGame(model, principal);
		}
	}

	@RequestMapping(value = "/joinGame.htm", method = RequestMethod.POST, params = { "join" })
	public String joinGame(Model model, @ModelAttribute @Valid Player player, Principal principal, BindingResult result) {
		if (result.hasErrors()) {
			return newGame(model, principal);
		}
		GameBoard game = ludoService.getGameById(player.getGameId());
		User user = ludoService.getUserByUsername(principal.getName());
		try {
			if (game == null) {
				return showGamesList(model, "No game available with id " + player.getId());
			}
			GameBoard resumableGame = ludoService.getResumableGame(user);
			if (resumableGame != null) {
				return showGamesList(model, "Resume or left the game you you have already joined.</a>");
			}
			game.addNewPlayer(user, player.getColorName(), ludoService);
			ludoService.saveAuditTrailForJoin(game.getPlayerByUser(user));
			model.addAttribute("owner", false);
			model.addAttribute("joined", true);
			model.addAttribute("players", game.getPlayers());
			model.addAttribute("successMsgs", "You join the game, please wait for the game owner to start it.");
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
		}
		return laodGame(model, game.getId(), principal);
	}

	@RequestMapping(value = "/listGames.htm", method = RequestMethod.GET)
	public String showGamesList(Model model, String error) {
		model.addAttribute("games", ludoService.getJoinableGames());
		if (error != null && !error.isEmpty()) {
			model.addAttribute("errorMsgs", error);
		}
		return "gameManager/listGames";
	}

	@RequestMapping(value = "/startGame/{gameId}.htm")
	public String startGame(Model model, @PathVariable("gameId") Integer gameId, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		try {
			GameBoard game = GameBoardController.loadGameById(ludoService, gameId);
			GameBoardController.checkGameValidity(game, user, false, true, true, new GameState[] { GameState.CREATED }, ludoService);
			game.startGame(ludoService);
			GameBoardController.setPlayers(model, game, user);
			return "board";
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
			return laodGame(model, gameId, principal);
		}
	}

	@RequestMapping(value = "/leaveGame/{gameId}.htm")
	public String leaveGame(Model model, @PathVariable("gameId") Integer gameId, Principal principal) {
		User user = ludoService.getUserByUsername(principal.getName());
		try {
			GameBoard game = GameBoardController.loadGameById(ludoService, gameId);
			GameBoardController.checkGameValidity(game, user, false, false, true, new GameState[] { GameState.STARTED }, ludoService);
			game.leaveGame(ludoService, user);
			ludoService.saveAuditTrailForLeave(game.getPlayerByUser(user));
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
		}
		return main(model, principal);
	}

	@RequestMapping(value = "/showGameList.ajax", params = { "gameId" })
	public @ResponseBody
	String showGameList(Player player) {
		try {
			GameBoard game = GameBoardController.loadGameById(ludoService, player.getGameId());
			return HTMLGenerator.generateJoinedPlayersList(game.getPlayers());
		} catch (LudoException e) {
			return "Cannot load the game!, Error: " + e.getMessage();
		}
	}

	@RequestMapping(value = "/showGameList.ajax", params = { "all" })
	public @ResponseBody
	String showGameList() {
		return HTMLGenerator.generateAllJoinableGameList(ludoService.getJoinableGames());
	}

	@RequestMapping(value = "/getGameStatus.ajax")
	public @ResponseBody
	String checkGameStarted(Player player) {
		try {
			GameBoard game = GameBoardController.loadGameById(ludoService, player.getGameId());
			if (game != null) {
				return game.getGameState().name();
			}
		} catch (LudoException e) {
		}
		return null;
	}

}
