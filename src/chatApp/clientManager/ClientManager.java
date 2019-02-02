package chatApp.clientManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

public final class ClientManager {

	private File clientsFile;

	public ClientManager() {
		final String fileName = "./RegisteredUsers/users.txt";
		clientsFile = new File(fileName);
	}

	public synchronized boolean registerClient(String username, String password) {
		final Client client = new Client(username);
		if (checkClient(client, Client.registerComparator())) {
			return false;
		}
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(clientsFile, true)))) {
			final String delimiter = " ";
			pw.println(username + delimiter + password);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public synchronized boolean loginClient(Client client, String username, String password) {
		final Client cl = new Client(username, password);
		if (checkClient(cl, Client.loginComparator())) {
			client.setUsername(username);
			client.setPassword(password);
			return true;
		}
		System.out.println("tukaaa");
		return false;
	}

	private synchronized boolean checkClient(Client client, Comparator<Client> comparator) {
		try (BufferedReader reader = new BufferedReader(new FileReader(clientsFile))) {
			String line;
			final String separator = " ";
			while ((line = reader.readLine()) != null) {
				String tokens[] = line.split(separator);
				final String currentUsername = tokens[0];
				final String currentPassword = tokens[1];
				final Client currentClient = new Client(currentUsername, currentPassword);
				if (comparator.compare(client, currentClient) == 0) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
