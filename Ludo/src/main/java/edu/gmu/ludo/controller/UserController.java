package edu.gmu.ludo.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.gmu.ludo.controller.validator.GeneralValidator;
import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.service.LudoException;
import edu.gmu.ludo.service.LudoService;

@Controller
public class UserController {

	@Autowired
	@Qualifier("ludoServiceImpl")
	private LudoService ludoService;
	// SWE_681 binding a validator to this controller
	@Autowired
	private GeneralValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "/addUser.htm", method = RequestMethod.GET)
	public String showUserForm(Model model) {
		model.addAttribute("user", new User());
		return "gameManager/addUser";
	}

	@RequestMapping(value = "/addUser.htm", method = RequestMethod.POST)
	public String saveUser(Model model, @ModelAttribute @Valid User user, BindingResult result) {
		if (result.hasErrors()) {
			return "gameManager/addUser";
		}
		try {
			ludoService.registerUser(user);
			model.addAttribute("successMsgs", "User saved successfully. Now your can login to the system.");
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("errorMsgs", "The username '" + user.getUsername() + "' is already registered.");
		}
		return "gameManager/addUser";
	}

	@RequestMapping(value = "/updateUser.htm", method = RequestMethod.GET)
	public String showUserForm(Model model, Principal principal) {
		model.addAttribute("user", ludoService.getUserByUsername(principal.getName()));
		model.addAttribute("updateMode", "true");
		return "gameManager/addUser";
	}

	@RequestMapping(value = "/updateUser.htm", method = RequestMethod.POST)
	public String updateUser(Model model, @ModelAttribute @Valid User newUser, BindingResult result, Principal principal) {
		model.addAttribute("updateMode", "true");
		if (result.hasErrors()) {
			return "gameManager/addUser";
		}
		try {
			User oldUser = ludoService.getUserByUsername(principal.getName());
			oldUser.setUser(newUser);
			ludoService.registerUser(oldUser);
			model.addAttribute("successMsgs", "User updated successfully.");
		} catch (LudoException e) {
			model.addAttribute("errorMsgs", e.getErrorMsgs());
		}
		return "gameManager/addUser";
	}
}
