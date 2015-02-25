package edu.gmu.ludo.service.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException,
			ServletException {
		if (exception.getMessage().equals(LoginServiceProviderImpl.BLOCKED)) {
			super.setDefaultFailureUrl("/login.htm?login_error=2");
		} else {
			super.setDefaultFailureUrl("/login.htm?login_error=1");
		}
		super.onAuthenticationFailure(request, response, exception);
	}

}
