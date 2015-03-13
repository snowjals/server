package server;

import java.util.ArrayList;
import java.util.Date;



/* This is the User class
 * 
 * 
 */

public class User {

	private boolean online;
	private Date lastLogin;
	private String username;
	private boolean isMod = false;
	private Server server;
	private String room = "default";
	private ArrayList<User> ignoredUsers = new ArrayList<User>();
	private ArrayList<Message> privateMessages = new ArrayList<Message>();


	public User(String username, Server server) {
		if (isValidUsername(username)) {
			this.username = username;
			this.server = server;
		}
	}

	public User(String username) {
		this.username = username;
	}

	public boolean isValidUsername(String username) {
		for (int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if (! isValidTitleCharacter(c)) {
				throw new IllegalArgumentException("Invalid username");

			}

		}
		return true;
	}


	protected static boolean isValidTitleCharacter(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
	} 


	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void setOnline() {
		this.online = true;
	}

	public void setOffline() {
		this.online = false;
	}

	public String getUsername() {
		if (this.isMod) {
			return "@" + this.username;
		}
		return this.username;

	}

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public boolean isOnline() {
		return this.online;
	}

	public void setMod() {
		this.isMod = true;
	}

	public boolean isMod() {
		return this.isMod;
	}

	public boolean setOffline(User user) {
		if (user.isMod()) {
			this.online = false;
			return true;
		} else {
			return false;
		}
	}

	public Server getServer() {
		return this.server;
	}


	public boolean setRoom(String room) {
		this.room = room;
		return true;
	}

	public String getRoom() {
		return this.room;
	}

	public boolean ignoreUser(User user) {
		if ( ! ignoredUsers.contains(user)) {
			this.ignoredUsers.add(user);
			return true;
		} 
		return false;
	}

	public boolean isIgnoredByThisUser(User user) {
		if (this.ignoredUsers.contains(user)) {
			return true;
		}
		return false;
	}

	public ArrayList<Message> getPrivateMessages() {
		return this.privateMessages;
	}
	
	public void addPrivateMessage(Message message) {
		privateMessages.add(message);
	}
	
}
