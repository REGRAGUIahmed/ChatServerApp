package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServeurChat extends Thread {
	private boolean isActive=true;
	private int nombreClient=0;
	private List<Conversation> clients = new ArrayList<Conversation>();
	
	public static void main(String[] args) {
		new ServeurChat().start();
		//System.out.println("Suite de l'application");
	}
	@Override 
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1234);
			while(isActive) {
				Socket socket = ss.accept();
				++nombreClient;
				Conversation conversation = new Conversation(socket,nombreClient);
				clients.add(conversation);
				conversation.start();
			}
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class Conversation extends Thread{
		protected Socket socket;
		protected int nombreClient;
		public Conversation(Socket s, int nombreClient) {
			this.socket = s;
			this.nombreClient=nombreClient;
		}
		
		public void broadcastMessage(String message, Socket socket, int numero) {
			try {
				for(Conversation client : clients) {
					if(client.socket != socket) {
						if(client.nombreClient==numero) {
							PrintWriter printWriter = new PrintWriter(client.socket.getOutputStream(), true);
							printWriter.println(message);
						}
						else if( numero ==-1) {
							PrintWriter printWriter = new PrintWriter(client.socket.getOutputStream(), true);
							printWriter.println(message);
						}
					}
	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override //////////////////
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				String IP = socket.getRemoteSocketAddress().toString();
				pw.println("Bienvenu vous etes le client numero"+nombreClient);
				System.out.println("Connexion de client numero: "+nombreClient+" IP= "+IP);
				//pw.println("Deviner le nombre secret.....?");
				while(true) {
					String req = br.readLine();
					if(req.contains("=>")) {
						String[] requestParams=req.split("=>");
						if(requestParams.length==2);
						String message = requestParams[1];
						int numeroClient = Integer.parseInt(requestParams[0]);
						broadcastMessage(message, socket, numeroClient);
					}
					else {
						broadcastMessage(req, socket, -1);
					}
					
					
					
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}

