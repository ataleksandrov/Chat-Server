package chatApp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import chatApp.clientManager.Client;
import chatApp.clientManager.Listener;
import chatApp.clientManager.Sender;

public class Server extends Thread {

	private final int LISTENING_PORT = 4444;
	private ServerSocket serverSocket;
	static private Dispatcher serverDispatcher;

	public Server() {
		bindServerSocket();
		serverDispatcher = new Dispatcher();
		serverDispatcher.start();
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	public void bindServerSocket() {
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
			System.out.println("ChatServer started on port " + LISTENING_PORT);
		} catch (IOException ioe) {
			System.err.println("Could not start listening on port " + LISTENING_PORT);
			ioe.printStackTrace();
		}
	}

	private void handleClientConnections() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				Client client = new Client();
				client.setSocket(socket);
				Listener clientListener = new Listener(client, serverDispatcher);
				Sender clientSender = new Sender(client, serverDispatcher);
				client.setClientListener(clientListener);
				clientListener.start();
				client.setClientSender(clientSender);
				clientSender.start();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		handleClientConnections();
	}
}
