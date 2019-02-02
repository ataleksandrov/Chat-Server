package chatApp.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chatApp.clientManager.Client;

final class Room {
	private String roomName;
	private Set<Client> roomMembers;
	private Client owner;
	private File chatHistoryFile;
	private static final String ROOMS_HISTORY_FOLDER = "ChatHistory/";
	private static final String EXTENTION = ".txt";

	public Room(String roomName, Client owner) {
		this.roomName = roomName;
		this.owner = owner;
		roomMembers = new HashSet<>();
		roomMembers.add(owner);
		chatHistoryFile = new File(ROOMS_HISTORY_FOLDER + roomName + EXTENTION);
	}

	public synchronized boolean addMember(Client client) {
		if (roomMembers.add(client)) {
			sendChatHistoryToClient(client);
			return true;
		}
		return false;
	}

	public synchronized void sendMessageToFile(String msg) {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(chatHistoryFile, true)))) {
			pw.println(msg);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean delMember(Client client) {
		if (!owner.equals(client)) {
			return roomMembers.remove(client);
		}
		return false;
	}

	public synchronized boolean isClientInTheRoom(Client client) {
		return roomMembers.contains(client);
	}

	public Client getOwner() {
		return owner;
	}

	public String getRoomName() {
		return roomName;
	}

	public synchronized String getStringOfActiveClients() {
		StringBuilder sb = new StringBuilder();
		for (Client client : getActiveClients()) {
			sb.append(client.getUsername() + "\n");
		}
		return sb.toString();
	}

	public synchronized List<Client> getActiveClients() {
		List<Client> activeClients = new ArrayList<>();
		for (Client client : roomMembers) {
			if (client.getIsActive()) {
				activeClients.add(client);
			}
		}
		return activeClients;
	}

	public synchronized boolean isActiveRoom() {
		return getActiveClients().size() != 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roomName == null) ? 0 : roomName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (roomName == null) {
			if (other.roomName != null)
				return false;
		} else if (!roomName.equals(other.roomName))
			return false;
		return true;
	}

	private synchronized void sendChatHistoryToClient(Client client) {
		if (!chatHistoryFile.exists()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		client.getClientSender().sendMessage(sb.toString());
	}

}
