package edu.gmu.ludo.service.impl;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import edu.gmu.ludo.entity.User;
import edu.gmu.ludo.service.LudoService;

public class LoginServiceProviderImpl extends DaoAuthenticationProvider implements AuthenticationProvider {

	public static final String BLOCKED = "BLOCKED";
	private LudoService ludoService;

	public LoginServiceProviderImpl(UserDetailsService userDetailsService, LudoService ludoService) {
		super.setUserDetailsService(userDetailsService);
		this.ludoService = ludoService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		//SWE_681 recored the failed attempts for login, and block the user if the number of failed attempts reaches to a threshold. 
		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		try {
			User user = ludoService.getUserByUsername(username);
			if(user != null && user.isBlocked())
				throw new BadCredentialsException(BLOCKED);
			//The original functionality of the Spring's DaoAuthenticationProvider kept here.
			Authentication authenticate = super.authenticate(authentication);
			return authenticate;
		} catch (BadCredentialsException e) {
			ludoService.logFailedLogin(username);
			throw e;
		}
	}
}
