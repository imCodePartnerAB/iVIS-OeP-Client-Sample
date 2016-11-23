package se.unlogic.hierarchy.foregroundmodules.login;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import se.unlogic.hierarchy.core.beans.User;

public class LoginEvent implements Serializable {

	private static final long serialVersionUID = -6675039911718499117L;
	
	private final User user;
	private final HttpSession session;

	public LoginEvent(User user, HttpSession session) {

		super();
		this.user = user;
		this.session = session;
	}

	public User getUser() {

		return user;
	}

	public HttpSession getSession() {

		return session;
	}
}
