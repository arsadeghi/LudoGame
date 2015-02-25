package edu.gmu.ludo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.gmu.ludo.controller.validator.GeneralValidator;
import edu.gmu.ludo.entity.form.GameRecordFilter;
import edu.gmu.ludo.service.LudoService;

@Controller
public class StatisticAndLogController {

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

	@RequestMapping(value = "/statistics.htm", params = { "all" })
	public String showAllStat(Model model) {
		model.addAttribute("gameRecordFilter", new GameRecordFilter());
		model.addAttribute("games", ludoService.getFinishedGames());
		return "gameManager/statistics";
	}

	@RequestMapping(value = "/statistics.htm", params = { "filter" })
	public String showFilteredStat(Model model, @ModelAttribute @Valid GameRecordFilter filter, BindingResult result) {
		model.addAttribute("gameRecordFilter", new GameRecordFilter());
		if (!result.hasErrors()) {
			model.addAttribute("games", ludoService.getFinishedGames(filter.getOwner(), filter.getPlayer()));
		}
		return "gameManager/statistics";
	}

	@RequestMapping(value = "/auditTrail.htm")
	public String showAuditTrail(Model model, @ModelAttribute @Valid GameRecordFilter filter, BindingResult result) {
		if (result.hasErrors()) {
			return "gameManager/statistics";
		}
		model.addAttribute("auditTrails", ludoService.getAuditTrailsForGame(filter.getGameId(), filter.getPlayer()));
		if (filter.getPlayer() != null) {
			model.addAttribute("user", ludoService.getUserByUsername(filter.getPlayer()));
		}
		return "gameManager/auditTrail";
	}
}
