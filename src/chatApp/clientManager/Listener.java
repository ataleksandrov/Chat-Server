package chatApp.clientManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import chatApp.server.Dispatcher;
import commandPattern.CommandPatterns;

public class Listener extends Thread {
	private Dispatcher serverDispatcher;
	private Client client;
	private BufferedReader socketReader;

	public Listener(Client client, Dispatcher serverDispatcher) {
		this.client = client;
		this.serverDispatcher = serverDispatcher;
		Socket socket = this.client.getSocket();
		try {
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Until interrupted, reads messages from the client socket, forwards them
	 * to the server dispatcher's queue and notifies the server dispatcher.
	 */
	public void run() {
		try {
			authentication();

			while (!isInterrupted()) {
				String message = socketReader.readLine();
				if (message == null)
					break;
				serverDispatcher.dispatchMessage(client, message);
			}
		} catch (IOException ioe) {
			System.out.println("Client has left.");
			// Problem reading from socket (broken connection)
		}

		// Communication is broken. Interrupt both listener and
		// sender threads
		client.getClientSender().interrupt();
		serverDispatcher.removeActiveClient(client);
	}

	private void authentication() throws IOException {
		boolean isLoggedIn = false;
		do {
			String request;
			final String validRequest = "Please inset a register or login request!";
			do {
				client.getClientSender().sendMessage(validRequest);
				request = socketReader.readLine();
			} while (!CommandPatterns.isValidRequest(request));
			String[] tokens = request.split(" ");
			final String verb = tokens[0];
			final String username = tokens[1];
			final String password = tokens[2];
			final String register = "register";
			final String login = "login";
			String response;
			if (verb.equals(register) && serverDispatcher.getClientManager().registerClient(username, password)) {
				response = "You have successfully registered!";
			} else
				if (verb.equals(login) && serverDispatcher.getClientManager().loginClient(client, username, password)
						&& serverDispatcher.addActiveClient(client)) {
				response = "Welcome " + username;
				isLoggedIn = true;
			} else {
				response = "Invalid username or password!";
			}
			client.getClientSender().sendMessage(response);
		} while (!isLoggedIn);
	}
}