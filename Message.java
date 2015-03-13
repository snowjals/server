package server;

import java.text.SimpleDateFormat;
import java.util.Date;

/* This is the Message class
 * 
 */

public class Message  {

	private String message;
	private String timestamp;
	private User owner;
	private Date sent;
	private String room  = "default";


	public Message(User owner, String message,Date sent) {
		this.owner = owner;
		this.message = message;
		//this.timestamp = timestamp;
		this.sent = sent;

		// Implement sdf for more readable date format
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy@HH:mm");
		this.timestamp = sdf.format(sent); 

	}

	public String getMessage() {
		return message;
	}

	public String getUsername() {
		return this.owner.getUsername();
	}


	public void setMessage(User user, String message) {

		if (user.isMod()) {
			this.message = message;
		}
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {

		/* just for mods
		this.timestamp = timestamp;
		 */

	}
	public User getOwner() {
		return owner;
	}

	public Date getSent() {
		return sent;
	}

	public String getRoom() {
		return this.room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	protected void createBackup(String message, String username, Date timestamp) {

	}

}
