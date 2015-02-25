package edu.gmu.ludo.controller.validator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.gmu.ludo.entity.Player;
import edu.gmu.ludo.entity.Player.PlayerColor;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.entity.form.GameMove;
import edu.gmu.ludo.entity.form.GameRecordFilter;

@Component
public class GeneralValidator implements Validator {

	@Override
	public boolean supports(Class<?> cls) {
		// SWE_681 validates the input data encapsulated in the following
		// classes. All validations are based on whitelist approach.
		return (User.class.isAssignableFrom(cls) || Player.class.isAssignableFrom(cls) || GameMove.class.isAssignableFrom(cls) || GameRecordFilter.class
				.isAssignableFrom(cls));
	}

	@Override
	public void validate(Object target, Errors errors) {
		Map<String, String> errorMap = new HashMap<>();
		if (target instanceof User) {
			errorMap = validateUser((User) target);
		} else if (target instanceof Player) {
			errorMap = validatePlayer((Player) target);
		} else if (target instanceof GameMove) {
			errorMap = validateGameMove((GameMove) target);
		} else if (target instanceof GameRecordFilter) {
			errorMap = validateGameRecordFilter((GameRecordFilter) target);
		}
		for (String errorField : errorMap.keySet()) {
			errors.rejectValue(errorField, errorMap.get(errorField));
		}
	}

	public static Map<String, String> validateUser(User user) {
		// SWE_681 input validation for user
		Map<String, String> errors = new HashMap<>();
		if (user.getUsername() == null || user.getUsername().isEmpty())
			errors.put("username", "user.username.empty");
		else if (!user.getUsername().matches("^([a-zA-Z_0-9]){5,}$"))
			errors.put("username", "user.username.invalid");
		if (user.getPassword() == null || user.getPassword().isEmpty() || user.getConfirmedPassword() == null || user.getConfirmedPassword().isEmpty())
			errors.put("password", "user.password.empty");
		else if (!user.getPassword().equals(user.getConfirmedPassword()))
			errors.put("confirmedPassword", "user.password.notMatch");
		else if (!user.getPassword().matches("^(?=.*[A-Z])(?=.*[0-9]).{8,}$"))
			errors.put("password", "user.password.invalid");
		if (user.getName() == null || user.getName().isEmpty())
			errors.put("name", "user.name.empty");
		else if (!user.getName().matches("^(\\w+\\s?){3,}$"))
			errors.put("name", "user.name.invalid");
		return errors;
	}

	public static Map<String, String> validatePlayer(Player player) {
		// SWE_681 input validation for player
		Map<String, String> errors = new HashMap<>();
//		if (player.getGameId() == null) {
//			errors.put("gameId", "player.game.empty");
//		}
		if (player.getColorName() == null || player.getColorName().isEmpty()) {
			errors.put("colorName", "player.colorname.empty");
		} else {
			boolean validColor = false;
			for (PlayerColor color : PlayerColor.values()) {
				if (color.getColorName().equals(player.getColorName())) {
					validColor = true;
					break;
				}
			}
			if (!validColor) {
				errors.put("colorName", "player.colorname.invalid");
			}
		}
		return errors;
	}

	public static Map<String, String> validateGameMove(GameMove move) {
		// SWE_681 input validation for player
		Map<String, String> errors = new HashMap<>();
		if (move.getGameId() == null) {
			errors.put("gameId", "move.game.empty");
		}
		if (move.getPieceNumber() != null && !(move.getPieceNumber() >= 1 && move.getPieceNumber() <= 4)) {
			errors.put("pieceNumber", "move.pieceNumber.invalid");
		}
		if (move.getDiceNumber() != null && !(move.getDiceNumber() >= 1 && move.getDiceNumber() <= 6)) {
			errors.put("diceNumber", "move.diceNumber.invalid");
		}
		return errors;
	}

	public static Map<String, String> validateGameRecordFilter(GameRecordFilter filter) {
		// SWE_681 input validation for player
		Map<String, String> errors = new HashMap<>();
		if (filter.getOwner() != null && !filter.getOwner().matches("^(\\w+\\s?)*$")) {
			errors.put("owner", "recordFilter.owner.name.invalid");
		}
		if (filter.getPlayer() != null && !filter.getPlayer().matches("^(\\w+\\s?)*$")) {
			errors.put("player", "recordFilter.player.name.invalid");
		}
		return errors;
	}
}
