package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class ListenServer implements Runnable {

	
	// Declaring fields

	DataInputStream input;

	// Shared fields
	ArrayList<Message> history = new ArrayList<Message>();
	ArrayList<User> onlineUsers = new ArrayList<User>();
	

	// Per instance fields
	Socket connectionSocket;
	Server server;
	static User user;
	boolean isLoggedIn;
	public static boolean startPushing;
	private Listener listener;


	public ListenServer(Socket incoming, ArrayList<Message> history, ArrayList<User> onlineUsers) {
		this.connectionSocket = incoming;
		this.history = history;
		this.onlineUsers = onlineUsers;
		//this.listener = listener;
	}

	
	public void init() {
		this.server = new Server(this.history, this.onlineUsers, this);
		try {
			this.input  = new DataInputStream(connectionSocket.getInputStream());
		} catch (EOFException e) {
			System.out.println("Premature");

		} catch (IOException e) {
			System.out.println("Nothing received"); 
			e.printStackTrace();
		}
		listenServer();
	}
	


	@Override
	public void run() {
		init();
	}
	
	public void listenServer() {
		System.out.println("\n\n **Ready** \n\n");


		try {

			ArrayList<String> message = new ArrayList<String>();

			// IMPORTANT: THIS METHOD ONLY READS 
			// THE SELECTED METHOD IN CLIENT ALTHOUGH 
			// THE METHOD IS FITTED TO PARSE JSON

			while(input.available() == 0) {

			}

			// Read from input
			for (int i = 0; i < input.available(); i++) {
				message.add(input.readUTF());
			}

			// Analyze JSON packet
			String complete = "";
			String request = "";
			String content = "";
			for (String s : message) {
				complete += s;
			}

			String[] parse = parseJSON(complete);
			request = parse[0];
			content = parse[1];

			System.out.println(request + " " + content);

			// Execute the request

			if (server.isLegal(request)) {
				server.decomposition(request,content);
			} else {
				server.createPackage("0", "Server", "Error", "You are not logged in and can thereby not execute this command!");
				System.out.println("You are not logged in and can thereby not execute this command! ");
			}

			// GET the package (JSON object) that the server has prepared

			// Ensure that the server is ready
			while (! server.ready()) {

			}
			String pkg3 = server.getPackage2();

			//System.out.println(pkg3);

			// Send the package through the socket
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

			out.writeUTF("{" + pkg3  + "}\n");

			out.flush();
			

			if (startPushing) {
				Date lastOnline = user.getLastLogin();
				System.out.println("last online " + lastOnline);
				Runnable push = new PushMessages(this.history, connectionSocket, (new Date()),user, user.getPrivateMessages());
				Thread thread = new Thread(push);
				thread.start();
				startPushing = false;
			}
			System.out.println("Finished with task");
			listenServer();

		} catch (EOFException e) {
			System.out.println("Premature");

		} catch (IOException e) {
			System.out.println("Nothing received"); 
			try {
				Thread.sleep(400);
				exit();
			} catch (InterruptedException e1) {

			}
		}
	}


	private String[] parseJSON(String complete) {
		System.out.println(complete);
		String[] ss = complete.replace("{", "").replace("}", "").replace("'request'","").replace("'content'", "").replace(":","").split(",");
		for (String s : ss) {
			System.out.println(s);
		}
		String request = ss[0];
		String conte = ss[1];

		String[] ret = {"",""};	
		ret[0] = request.replace(" ", "").replace("'","");
		ret[1] = conte.replaceFirst("\\s+", "").replace("'","");
		
		/*String[] ss = complete.replace("{", "").replace("}", "").replace("'request'","").replace("'content'", "").replace(":","").split(",");
		String request = ss[0].substring(1);
		String conte = ss[1].substring(2);

		String[] ret = {"",""};	
		ret[0] = request;
		ret[1] = conte; */

		return ret;
	}

	public void exit() {
		System.out.println("Connection closed");
		try {
			connectionSocket.close();
		} catch (IOException e) {
			System.out.println("Could not close connection");
		}
	}

	public Listener getListener() {
		return this.listener;
	}
	
	public Socket getSocket() {
		return this.connectionSocket;
	}
	
	public void disconnect() {
		try {
			this.connectionSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
}
