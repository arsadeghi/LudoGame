package edu.gmu.ludo.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.service.LudoService;

public class GlobalVaribaleManager extends HandlerInterceptorAdapter {

	@Autowired
	@Qualifier("ludoServiceImpl")
	private LudoService ludoService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
			User user = ludoService.getUserByUsername(authentication.getName());
			request.setAttribute("LudoUser", user);
			request.setAttribute("authN", true);
		}
		return true;
	}

}
