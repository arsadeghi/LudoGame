package edu.gmu.ludo.service;

import java.util.ArrayList;
import java.util.List;

public class LudoException extends Exception {

	private static final long serialVersionUID = 1L;

	private List<String> errorMsgs = new ArrayList<String>();
	private String returnPage;

	public LudoException(String message) {
		super();
		this.errorMsgs.add(message);
	}

	public LudoException(String message, String returnPage) {
		super();
		this.errorMsgs.add(message);
		this.returnPage = returnPage;
	}

	public LudoException(List<String> messages) {
		super();
		this.errorMsgs.addAll(messages);
	}

	public List<String> getErrorMsgs() {
		return errorMsgs;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		for (String msg : errorMsgs) {
			sb.append(msg + "\n");
		}
		return sb.toString();
	}

	public void printErrors() {
		for (String msg : errorMsgs) {
			System.err.println(msg);
		}
	}

	public String getReturnPage() {
		return returnPage;
	}
	
}
