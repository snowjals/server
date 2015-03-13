package server;


/* This is the Server class
 * The Server object instantiated by ListenServerer is unique although 
 *  onlineUsers and history are shared
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;


public class Server implements Runnable {

	// Declaring fields
	/* SHARED FIELDS */
	private ArrayList<User> onlineUsers = new ArrayList<User>();
	private ArrayList<Message> history = new ArrayList<Message>();

	/* Per request fields */
	private String pkg2;
	boolean ready;

	/* Global fields */
	public User user;
	private boolean instanceOnline = false;
	private static final String modPW = "aleksander";
	private ListenServer listenServer;
	//private static final String SPECIAL_TITLE_CHARACTERS = " ,-";



	public void init() {
		tryLoad();
		run();
	}


	private void tryLoad() {
		// nothing yet

	}


	public Server(ArrayList<Message> history, ArrayList<User> onlineUsers, ListenServer listenServer) {
		this.history = history;
		this.onlineUsers = onlineUsers;
		this.listenServer = listenServer;
	}

	public boolean login(User user) {

		if (!(onlineUsers.contains(user)) && !instanceOnline) { // PS: BY USING THIS YOU SUCCESSFULLY BAN
			this.user = user;									// A CLIENT PR SESSION
			user.setOnline();
			onlineUsers.add(user);
			instanceOnline = true;
			ListenServer.user = user;

			return true;

		} else {

			System.out.println("Already online");
			return false;
		}

	}

	public boolean logoff(User user) {
		if (onlineUsers.contains(user)) {
			user.setOffline();
			onlineUsers.remove(user);
			System.out.println("Bye " + user.getUsername());
			instanceOnline = false;
			user.setLastLogin(new Date());
			return true;

		} else {
			throw new IllegalArgumentException("Already offline");
		}
	}

	public void showHistory() {
		// ONLY if user is logged in
		if (history.isEmpty()) {
			System.out.println("No history to display");
		}
		for (Message msg : history) {
			System.out.printf("<"+msg.getTimestamp()+">"+"<"+msg.getUsername()+">"+"<"+msg.getMessage()+">\n");
		}
	}

	public String displayOnlineUsers() {
		if (this.user == null || !this.user.isOnline()) {
			System.err.println("Not logged in");
			return "";

		} else {
			String names = "";
			int usersOnline = onlineUsers.size();
			System.out.println("Online users: [" + usersOnline + "]");
			names += "Online users: [" + usersOnline + "]" +"\n";
			for (User user : onlineUsers) {
				if (user.isOnline()) {
					System.out.println(user.getUsername());
					names += user.getUsername() + " ";
				}
			}
			return names;
		}
	}



	public void addMessage(User user, String message) {
		if (user.isOnline()) {
			// Implement sdf for more readable date format
			/*SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy@HH:mm");
			String timestamp = sdf.format(new Date());
			 */
			Message msg = new Message(user,message,(new Date()));
			this.history.add(msg);
			msg.setRoom(this.user.getRoom());

		} else {
			System.err.println("Not logged in");
		}
	}


	public ArrayList<Message> getMessages() {
		return this.history;
	}

	public String help() {
		/*
		System.out.println("Commands:");
		System.out.println("login: 'username'");
		System.out.println("\tSign in with the entered username");
		System.out.println("logout:");
		System.out.println("\tSigns off the current user");
		System.out.println("msg: 'message'");
		System.out.println("\tSends the entered message");
		System.out.println("names:");
		System.out.println("\tLists the current online users");
		System.out.println("help:");
		System.out.println("\tDisplays this text");
		 */ 
		String help = "Commands: \n "
				+ "login: 'username' \n"
				+ " \tSign in with the entered username \n "
				+ "logout: \n "
				+ "\tSigns off the current user \n "
				+ "msg: 'message' \n "
				+ "\tSends the entered message \n "
				+ "names: \n "
				+ "\tLists the current online users \n "
				+ "help: \n "
				+ "\tDisplays this text \n"
				+ "---- ADDITIONAL ---- \n"
				+ "join 'room' \n"
				+ "\tJoins the selected room \n"
				+ "ignore 'user' \n"
				+ "\t ignores the user given that he or she is not mod \n"
				+ "privatemsg 'user@message'"
				+ "\t Sends a private message"; 

		String modMessage = "---- ONLY FOR MODS ---- \n "
				+ "mod 'password \n" 
				+ "\tSet current user to mod given correct server password \n"
				+ "remove 'message number' \n"
				+ "\tRemoves the message at indicated position \n +"
				+ "edit '[edited message]@[position]' \n"
				+ "\tChanges the content at the chosen position \n"
				+ "ban 'username' \n"
				+ "\tPermanently bans the username \n"
				+ "banIP 'username' \n"
				+ "\tPermanently bans the IP associated to the username \n"
				+ "kick 'user' \n"
				+ "\t Kicks the desired user";
		if (user == null) {
			return help;
		} else if (user.isMod()) {
			return help + modMessage;
		} else {
			return help;
		}

	}

	public boolean allowMod(String password) {
		if (password.equals(modPW)) {
			return true;
		}
		return false;
	}

	public void setMod(User user) {
		user.setMod();
	}

	public String getPackage2() {

		return this.pkg2;
	}


	@Override
	public void run() {

		//backupMessages();

	}


	public boolean isLegalUsername(String username) {
		String [] split = username.split("");

		// Check legality of username - permitted: [a-z][0-9]
		for (String s : split) {
			if (s.matches("[a-z]") || s.matches("[0-9]") || s.matches("[A-Z]")) {

			}  else {
				return false;
			}
		}
		return true;
	}


	// Check legality of command
	public boolean isLegal(String command) {


		if (command.equals("help") || command.equals("login")) {
			return true;
		} else {
			if (user == null) {
				System.out.println("here");
				return false;
			} else if (! user.isOnline()) {
				return false;
			} else {
				return true;
			}
		}
	}


	public boolean ban(String username) {
		for (User user : onlineUsers) {
			if (user.getUsername().equals(username)) {
				if(user.setOffline(this.user)) {
					onlineUsers.remove(user);
					user.getServer().setInstanceOffline();
					try {
						PrintWriter writer = new PrintWriter(new FileWriter("bannedUsers.txt", true));
						writer.println(username);
						writer.close();
					} catch (Exception e) {
						return false;
					}
					//user.getServer().createPackage("0", "Server", "info", "You have been banned");
					return true;
				} else {
					return false;
				}
				//user.getSocket.close(); 
				// implement a method to set User(string, socket)
				// listener.bannedUsers.add(username)
				// listenserver(...,list-banned)
			}
		}
		return false;
	}


	public boolean join(String room) {
		this.user.setRoom(room);
		return true;
	}

	public void decomposition(String request, String content) {
		this.ready = false;

		// analyze JSON package

		if (request.equals("login")) {
			boolean isBanned = false;
			try {
				FileReader file = new FileReader("bannedUsers.txt");
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					if (scanner.nextLine().equals(content)) {
						isBanned = true;
					}
				}
				String ip = listenServer.getSocket().getInetAddress().toString();
				System.out.println(ip);
				file = new FileReader("bannedIP.txt");
				Scanner scanner2 = new Scanner(file);
				while (scanner2.hasNextLine()) {
					if (scanner2.nextLine().equals(ip)) {
						isBanned = true;
					}
				}

				scanner.close();
				scanner2.close();
			} catch (Exception e) {
				createPackage("0", "Server", "error", "Unknown error");
			}
			if (isBanned) {
				createPackage("0", "Server", "error", "This user is banned");

			} else {

				User user = null;


				if (isLegalUsername(content)) {
					user = new User(content, this); 

					System.out.println("Welcome " + user.getUsername());
					if (login(user)) {
						createPackage("0", "Server", "info", "Successfully logged in");
						ListenServer.startPushing = true;

					} else {

						createPackage("0", "Server", "error", "You are/ this user is already online");
					} 
				} else {
					createPackage("0", "Server", "error", "Invalid username");

				}
			}
		} else if (request.equals("logout")) {

			if (logoff(user)) {
				createPackage("0", user.getUsername(), "info", "Succesfully logged out");
			} else {
				createPackage("0", "Server", "error", "Unable to logoff");
			}

		} else if (request.equals("msg")) {
			addMessage(this.user,content);
			createPackage("0",user.getUsername(),"info", "Added");


		} else if (request.equals("names")) {
			createPackage("0", user.getUsername(), "info", displayOnlineUsers());



		} else if(request.equals("help")) {
			returnHelpPackage();

		} else if(request.equals("mod")) {
			if (allowMod(content)) {
				setMod(this.user);
				createPackage("0", "Server", "info", user.getUsername() + " is now mod");
			} else {
				createPackage("0", "Server", "error", "Incorrect password");

			}
		} else if(request.equals("ban")) {
			if(ban(content)) {
				createPackage("0", "Server", "info", "Sucessfully banned " + content);
			} else {
				createPackage("0", "Server", "error", "Unable to ban " + content);
			}
		} else if (request.equals("remove")) {
			int msgNum = -1;
			try {
				msgNum = Integer.parseInt(content);
			} catch (Exception e) {
				createPackage("0", "Server", "error", "Unknown error");
			}
			if (remove(msgNum) && msgNum != -1) {
				createPackage("0", "Server", "info", "Removed message number" + content);
			} else {
				createPackage("0", "Server", "error", "Unable to remove message number " + content);
			}
		} else if (request.equals("edit")) {
			String msgN = content.split("@")[1];
			String replacement = content.split("@")[0];
			int msgNum = -1;
			try {
				msgNum = Integer.parseInt(msgN);
			} catch (Exception e) {
				createPackage("0", "Server", "error", "Unknown error");
			}
			if (msgNum != -1 && edit(msgNum,replacement)) {
				createPackage("0", "Server", "info", "Edited");
			} else {
				createPackage("0", "Server", "error", "Unable to edit");
			}

		} else if (request.equals("join")) {
			if (join(content)) {
				createPackage("0", "Server", "info", user.getUsername() + " successfully joined room " + content);
			} else {
				createPackage("0", "Server", "error", "Unable to join room: " + content);
			}
		} else if (request.equals("banIP")) {
			if (banIP(content)) {
				createPackage("0", "Server", "info", "Banned IP associated to user " + content);
			} else {
				createPackage("0", "Server", "error", "Unable to ban IP associated to user: " + content);
			}
		} else if (request.equals("ignore")) {
			if (ignore(content)) {
				createPackage("0", "Server", "info", "Ignored user: " + content);
			} else {
				createPackage("0", "Server", "error", "Unable to ignore user " + content);
			}
		} else if (request.equals("kick")) {
			if (disconnect(content)) {
				createPackage("0", "Server", "info", "Kicked user " + content);
			} else {
				createPackage("0", "Server", "error", "Unable to kick user " + content);
			}

		} else if (request.equals("privmsg")) {
			// Consider to change type from "message" to "privatemessage"
			String[] split = content.split(" ", 2);
			if (sendPrivateMessage(split[0], split[1])) {
				createPackage("0", "Server", "info", "Message sent");
			} else {
				createPackage("0", "Server", "error", "Could not send message");
			}

		} else if (request.equals("list")) {
			createPackage("0", "Server", "info", list());
		} else {
			createPackage("0", "Server", "error", "Error");
		}

	}

	public boolean remove(int message) {
		if (user.isMod()) {
			history.get(message).setMessage(this.user,"[DELETED]");
			return true;
		}
		return false;
	}

	public boolean edit(int message, String replacement) {
		if (user.isMod()) {
			history.get(message).setMessage(this.user,replacement);
			return true;
		}
		return false;
	}

	public User userSearch(String username) {
		for (User user : onlineUsers) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	public boolean banIP(String username) {
		User banUser = userSearch(username);
		if(ban(username)) {
			if (banUser != null) {
				System.out.println(banUser.getUsername());
				String ip = banUser.getServer().getListenServer().getSocket().getInetAddress().toString();
				banUser.getServer().getListenServer().disconnect();
				PrintWriter writer;
				try {
					writer = new PrintWriter(new FileWriter("bannedIP.txt", true));
					writer.println(ip);
					writer.close();
					//return true;
				} catch (IOException e) {
					return false;
				}

			}
			return true;
		} else {
			return false;
		}
	}


	public void returnHelpPackage() {
		// Help packet is being constructed with a help-method due to the complexity
		String content = help();
		String username;
		if (user == null) {
			username = "Null";
		} else {
			username =  user.getUsername();
		}
		createPackage("0",username, "info", content);
	}


	public boolean ready() {
		return ready;
	}



	public void createPackage(String timestamp,String sender, String response, String content) {
		if (timestamp.equals("0")) {
			//Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy@HH:mm");
			timestamp = sdf.format(new Date()); 
		}

		this.pkg2 = 
				"'timestamp': " + "'" + timestamp + "'" +   "," + 
						"'sender': " + "'" + sender + "'" +  "," + 
						"'response': " + "'" + response + "'" +  "," +
						"'content': " + "'" + content + "'" ;

		this.ready = true;

	}

	public void setInstanceOffline() {
		this.instanceOnline = false;
	}

	public ListenServer getListenServer() {
		return this.listenServer;
	}

	public boolean ignore(String username) {
		User user = userSearch(username);
		if (user != null && ! user.isMod()) {
			return this.user.ignoreUser(user);
		}
		return false;
	}

	public boolean disconnect(String username) {
		User user = userSearch(username);
		if (user != null && this.user.isMod()) {
			user.getServer().getListenServer().disconnect();
			return true;
		}
		return false;
	}

	public boolean sendPrivateMessage(String receiver,String message) {
		User receiverUser = userSearch(receiver);
		if (receiverUser != null) {
			Message msg = new Message(this.user, message,new Date());
			receiverUser.addPrivateMessage(msg);
			return true;
		}
		return false;
	}


	public String list() {
		String rooms = "";
		for (User user : onlineUsers) {
			if (!user.getRoom().equals("default")) {
				rooms += "" + user.getRoom();
			}
		}
		return rooms;
	}

}
