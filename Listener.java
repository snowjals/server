package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Listener {

	/* CONFIG */ 
	static String tcp_ip;
	static int tcp_port = 5000;
	Socket ServerSocket;


	Socket connectionSocket;


	public Listener(Socket incoming) {
		this.connectionSocket = incoming;
	}


	public static void main(String[] args) {

		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<Message> history = new ArrayList<Message>();
		ArrayList<User> onlineUsers = new ArrayList<User>();

		// Do garbage collection of threads i.e send signal from thread that 
		// user has disconnected
		// Make main.threads referring to listener.threads (public) and remove
		// from ListenServer

		// try to load
		BackupLoader load = new BackupLoader();
		if (load.load()) {
			history = load.getReconstructedHistory();
		}
		

		// UNCOMMENT FOR BACKUP
		Runnable backup = new Backup(history);
		Thread worker = new Thread(backup);
		worker.start(); 
		// THREAD:		Backup backupThread = new Backup(server.getMessages());




		// Declaring fields	
		ArrayList<Socket> socketList = new ArrayList<Socket>();
		Socket connectionSocket = null;


		//  Create Socket
		//(created in 'declaring')


		// Open socket and listen for traffic

		System.out.println("Wating for connection");
		ServerSocket welcomeSocket = null;
		try {
			welcomeSocket = new ServerSocket(tcp_port);
		} catch (IOException e) {
			System.out.println(e);
		}

		while (true) {


			try {
				// listen for incoming traffic
				connectionSocket = welcomeSocket.accept();
				socketList.add(connectionSocket);
				System.out.println("Incoming connection from: " + connectionSocket);
				// thread incoming traffic
				Runnable connection = new ListenServer(connectionSocket,history, onlineUsers);
				Thread thread = new Thread(connection);
				thread.start();
				threads.add(thread);
				
				/*
				for (Thread t : threads) {
					if (!t.isAlive()) {
						threads.remove(t);
					}
				}
				*/

				System.out.println("proceeding");
				
				String never = "never";
				if (never.equals("always")) {
					welcomeSocket.close();
				}

			} catch (Exception e) {
				System.err.println("FATAL ERROR: Unhandled exeption."); 
			}
		}
	}
}