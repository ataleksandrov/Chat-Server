package chatApp.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import chatApp.clientManager.Client;
import chatApp.clientManager.ClientManager;
import commandPattern.CommandPatterns;

public class Dispatcher extends Thread {
	private Queue<String> messageQueue;
	private ClientManager clientManager;
	private Map<String, Room> rooms;
	private Map<String, Client> activeClients;

	public Dispatcher() {
		messageQueue = new LinkedList<>();
		clientManager = new ClientManager();
		rooms = new HashMap<>();
		activeClients = new HashMap<>();
	}

	public ClientManager getClientManager() {
		return clientManager;
	}

	public synchronized boolean addActiveClient(Client client) {
		final String username = client.getUsername();
		if (!activeClients.containsKey(username)) {
			client.setIsActive(true);
			activeClients.put(username, client);
			return true;
		}
		return false;
	}

	public synchronized boolean removeActiveClient(Client client) {
		final String username = client.getUsername();
		if (activeClients.containsKey(username)) {
			activeClients.get(username).setIsActive(false);
			activeClients.remove(username);
			return true;
		}
		return false;
	}

	public synchronized boolean dispatchMessage(Client client, String message) {
		if (CommandPatterns.isValidRequest(message)) {
			analyzeRequest(client, message);
			return true;
		} else {
			final String invalidRequest = "Invalid request!Try again!";
			client.getClientSender().sendMessage(invalidRequest);
			return false;
		}
	}

	public void run() {
		final int constant = 1;
		final String separator = " ";
		try {
			while (!isInterrupted()) {
				String message = getNextMessageFromQueue();
				String[] tokens = message.split(separator);
				String username = tokens[0];
				sendMessage(username, message.substring(username.length() + constant));
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	private synchronized void analyzeRequest(Client client, String request) {
		String message = null;
		final String separator = " ";
		final String clientName = client.getUsername();
		String[] tokens = request.split(separator);
		final String verb = tokens[0];
		switch (verb) {
		case "list-users":
			message = listUsersCmd(tokens, client);
			break;
		case "send":
			message = sendCmd(tokens, request, client);
			break;
		case "create-room":
			message = createRoomCmd(tokens, client);
			break;
		case "delete-room":
			message = deleteRoomCmd(tokens, client);
			break;
		case "join-room":
			message = joinRoomCmd(tokens, client);
			break;
		case "leave-room":
			message = leaveRoomCmd(tokens, client);
			break;
		case "list-rooms":
			message = listRoomsCmd(tokens, client);
			break;
		case "disconnect":
			final String discMsg = "Disconnected!";
			message = clientName + separator + discMsg;
			break;
		default:
			message = client.getUsername() + " " + "default!";
			break;
		}
		messageQueue.add(message);
		notify();
	}

	private synchronized String listRoomsCmd(String[] tokens, Client client) {
		final String activeRooms = getActiveRooms();
		final String noActiveRooms = "No active rooms";
		return client.getUsername() + " " + (!activeRooms.isEmpty() ? activeRooms : noActiveRooms);
	}

	private synchronized String leaveRoomCmd(String[] tokens, Client client) {
		final String roomName = tokens[1];
		final String succLeave = "You have successfully leaved " + roomName;
		final String failLeave = "Room " + roomName + " does not exist or you are not part of in!";
		return client.getUsername() + " " + (leaveRoom(client, roomName) ? succLeave : failLeave);
	}

	private synchronized String joinRoomCmd(String[] tokens, Client client) {
		final String roomName = tokens[1];
		final String succJoin = "You have successfully joined room " + roomName;
		final String failJoin = "You can not join the room " + roomName;
		return client.getUsername() + " " + (joinRoom(client, roomName) ? succJoin : failJoin);
	}

	private synchronized String deleteRoomCmd(String[] tokens, Client client) {
		final String roomName = tokens[1];
		final String succDel = "You have successfully deleted room " + roomName;
		final String failDel = "The room " + roomName + " can not be deleted!";
		return client.getUsername() + " " + (deleteRoom(client, roomName) ? succDel : failDel);

	}

	private synchronized String createRoomCmd(String[] tokens, Client client) {
		final String roomName = tokens[1];
		final String succCre = "You have successfully created room " + roomName;
		final String failCre = "The room " + roomName + " has already exists!";
		return client.getUsername() + " " + (createRoom(client, roomName) ? succCre : failCre);
	}

	private synchronized String sendCmd(String[] tokens, String request, Client client) {
		final String reciever = tokens[1];
		final String said = " said : ";
		final String clientName = client.getUsername();
		return reciever + " " + clientName + said + request.substring(request.indexOf(tokens[2]));
	}

	private synchronized String listUsersCmd(String[] tokens, Client client) {
		final int lengthOfListUsers = 1;
		if (tokens.length == lengthOfListUsers) {
			return getStringOfActiveUsers(client);
		} else {
			final String roomName = tokens[1];
			final String activeUsers = getActiveUsersInRoom(roomName);
			final String noUsers = "No users found";
			return client.getUsername() + " " + (activeUsers == null ? noUsers : activeUsers);
		}
	}

	private synchronized String getActiveUsersInRoom(String roomName) {
		if (rooms.containsKey(roomName)) {
			return rooms.get(roomName).getStringOfActiveClients();
		}
		return null;
	}

	private synchronized boolean leaveRoom(Client client, String roomNameL) {
		if (rooms.containsKey(roomNameL)) {
			return rooms.get(roomNameL).delMember(client);
		}
		return false;
	}

	private synchronized String getActiveRooms() {
		StringBuilder sb = new StringBuilder();
		for (Room room : rooms.values()) {
			if (room.isActiveRoom()) {
				sb.append(room.getRoomName() + " ");
			}
		}
		return sb.toString();
	}

	private synchronized boolean joinRoom(Client client, String roomName) {
		if (rooms.containsKey(roomName)) {
			return rooms.get(roomName).addMember(client);
		}
		return false;
	}

	private synchronized boolean deleteRoom(Client client, String roomName) {
		if (rooms.containsKey(roomName) && client.equals(rooms.get(roomName).getOwner())) {
			rooms.remove(roomName);
			return true;
		}
		return false;
	}

	private synchronized boolean createRoom(Client client, String roomName) {
		if (rooms.containsKey(roomName)) {
			return false;
		}
		Room room = new Room(roomName, client);
		rooms.put(roomName, room);
		return true;
	}

	private synchronized String getStringOfActiveUsers(Client cl) {
		StringBuilder sb = new StringBuilder();
		sb.append(cl.getUsername() + " ");
		for (Client client : activeClients.values()) {
			sb.append(client.getUsername() + " ");
		}
		return sb.toString();
	}

	private synchronized String getNextMessageFromQueue() throws InterruptedException {
		while (messageQueue.isEmpty()) {
			wait();
		}
		String message = messageQueue.poll();
		return message;
	}

	private void sendMessage(String username, String message) {
		if (activeClients.containsKey(username)) {
			activeClients.get(username).getClientSender().sendMessage(message);
		} else if (rooms.containsKey(username)) {
			final Room room = rooms.get(username);
			final String senderUsername = message.split(" ")[0];
			final String senderPassword = "a";
			Client client = new Client(senderUsername, senderPassword);
			if (room.isClientInTheRoom(client)) {
				room.getActiveClients().stream().map(c -> c.getClientSender())
						.forEach(c -> c.sendMessage(username + " : " + message));
				room.sendMessageToFile(message);
			} else {
				final String errMsg = "Sorry you are not in the room: " + username;
				activeClients.get(senderUsername).getClientSender().sendMessage(errMsg);
			}
		} else {
			final String senderUsername = message.split(" ")[0];
			final String failSendMsg = username + " is offline now";
			activeClients.get(senderUsername).getClientSender().sendMessage(failSendMsg);
		}
	}

}