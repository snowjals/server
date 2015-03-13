package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class PushMessages implements Runnable {

	
	ArrayList<Message> history = new ArrayList<Message>();
	Socket socket;
	DataOutputStream out;
	int printedMessages = 0;
	int lastPrinted = -1;
	Date loginTime;
	static boolean open = true;
	private User user;

	private int lastPrivatePrinted = -1;
	private ArrayList<Message> privateMessages = new ArrayList<Message>();

	public PushMessages(ArrayList<Message> history, Socket connectionSocket, Date loginTime, User user, ArrayList<Message> privateMessages) {
		this.history = history;
		this.socket = connectionSocket;
		this.loginTime = loginTime;
		this.user = user;
		this.privateMessages = privateMessages;
	}


	public void rig() {
		try {
			out = new DataOutputStream(socket.getOutputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
		push();
	}

	private void push() {
		/*	while (history.size() == 0 || lastPrinted == (history.size() - 1)) { 
		}*/

		if (! user.isOnline() || socket.isClosed()) {
			exit();

		} else {


			if(lastPrinted < history.size() - 1 ) {
				System.out.println("Sending [" + (lastPrinted + 2) +"][" + history.size() + "]" + "@@" + Thread.currentThread());			
				lastPrinted++;
				if (history.get(lastPrinted).getRoom().equals(user.getRoom()) &&
						(! user.isIgnoredByThisUser(history.get(lastPrinted).getOwner()) ||
								history.get(lastPrinted).getOwner().isMod())) {


					String timestamp = history.get(lastPrinted).getTimestamp();
					String sender = history.get(lastPrinted).getUsername();
					String response = "";
					if (loginTime.before(history.get(lastPrinted).getSent())) {
						response = "message";
					} else {
						response =  "history";
					}
					String content = history.get(lastPrinted).getMessage();
					String num = "";
					if (user.isMod()) {
						num = "   #[" + lastPrinted + "]";
					} 
					String pkg2 = 
							"'timestamp': " + timestamp +  "," + 
									"'sender': " + sender + "," + 
									"'response': " + response + "," +
									"'content': " + content + num;
					System.out.println(pkg2);
					try {
						System.out.println("Messages printed: " + printedMessages + " Size " + this.history.size());
						printedMessages++;

						out = new DataOutputStream(socket.getOutputStream());

						out.writeUTF("{" + pkg2 + "}");
						out.flush();


					} catch (Exception e) {
						System.out.println("err");
						exit();

					}
				}
			}
			
			
			// print private messages
			
			if (lastPrivatePrinted < privateMessages.size() - 1) {
				lastPrivatePrinted++;
				String timestamp = privateMessages.get(lastPrivatePrinted).getTimestamp();
				String sender = privateMessages.get(lastPrivatePrinted).getUsername();
				String response = "";
				if (loginTime.before(privateMessages.get(lastPrivatePrinted).getSent())) {
					response = "message";
				} else {
					response =  "history";
				}
				String content = privateMessages.get(lastPrivatePrinted).getMessage();
				String num = "";
				if (user.isMod()) {
					num = "   #[" + lastPrivatePrinted + "]";
				} 
				String pkg2 = 
						"'timestamp': " + timestamp +  "," + 
								"'sender': " + sender + "," + 
								"'response': " + response + "," +
								"'content': " + "[Private message from " + sender + "] " + content + num;
				System.out.println(pkg2);
				try {
					//System.out.println("Messages printed: " + " Size " + this.history.size());
					//printedMessages++;

					out = new DataOutputStream(socket.getOutputStream());

					out.writeUTF("{" + pkg2 + "}");
					out.flush();


				} catch (Exception e) {
					System.out.println("err");
					exit();

				}

				
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println("error");
			}

			push();
		}
	}

	@Override
	public void run() {
		rig();
	}

	public void exit() {
		System.out.println("Shuting down");
	}

	public ArrayList<Message> getMessages() {
		return this.history;
	}
	
	public ArrayList<Message> getPrivateMessages() {
		return this.privateMessages;
	}

}
