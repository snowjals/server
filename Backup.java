package server;

//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class Backup implements Runnable {

	// CONFIG //
	private int interval = 60; // seconds
	
	private ArrayList<Message> history = new ArrayList<Message>();

	public Backup(ArrayList<Message> history) {
		this.history = history;
		//loop();
	}

	@Override
	public void run() {
		System.out.println("Performing backup");
		//Date d = new Date();
		//long now = d.getTime();
		
		//Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy@HH:mm");
		String timestamp = sdf.format(new Date()); 
		
		try {
			PrintWriter backup = new PrintWriter(new FileWriter("backup.txt", true));
			backup.println("Backup performed: " + timestamp);
			for (Message msg : history) {
				backup.println("<"+msg.getTimestamp()+">"+"<"+msg.getUsername()+">"+"<"+msg.getMessage()+">");

			}
			backup.close();
		} catch (IOException e) {
			System.err.println("Unable to perform a backup"); 

		}
		try {
			Thread.sleep(1000*interval); //backup every 1st minute
		} catch (InterruptedException e) {

			System.out.println("Something went wrong");
			e.printStackTrace();
		}

		run();
		
	}

		

}
