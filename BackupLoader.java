package server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class BackupLoader {

	private ArrayList<Message> reconstructed = new ArrayList<Message>();
	private String date;

	public boolean load() {

		FileReader reader;
		try {
			reader = new FileReader("backup.txt");
			Scanner scanner = new Scanner(reader);
			
			if (scanner.hasNextLine()) {
				date = scanner.nextLine();
			}

			String line;
			String sent;
			String username;
			String msg ;
			
			
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();

				if (line.equals("")) {
					
				} else {
				String [] content = line.replaceAll("<", "").split(">");
				System.out.println(content.length);
				for (String s : content) {
					System.out.println(s);
				}
				
				
				sent = content[0];
				username = content[1];
				msg = content[2];

				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy@HH:mm");
				Date date = sdf.parse(sent);


				User user = new User(username);
				Message message = new Message(user,msg,date); 
				reconstructed.add(message);
			}
			}

			scanner.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<Message> getReconstructedHistory() {
		return this.reconstructed;
	}

	public String getDate() {
		return this.date;
	}
	
	public static void main(String[] args) {
		BackupLoader bl = new BackupLoader();
		boolean loaded = bl.load();
		System.out.println(loaded);
		ArrayList<Message> history = bl.getReconstructedHistory();
		
		for (Message m : history) {
			m.toString();
		}
	}
	
}
	