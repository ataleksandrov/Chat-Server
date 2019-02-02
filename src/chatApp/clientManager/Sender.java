package chatApp.clientManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import chatApp.server.Dispatcher;

public class Sender extends Thread {
	
	private Queue<String> messageQueue;
	private Dispatcher serverDispatcher;
	private Client client;
	private PrintWriter socketPrintWriter;
	private boolean isDisconnected;

	public Sender(Client client, Dispatcher aServerDispatcher) {
		this.client = client;
		this.serverDispatcher = aServerDispatcher;
		Socket socket = this.client.getSocket();
		try {
			socketPrintWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		messageQueue = new LinkedList<>();
		isDisconnected = false;
	}

	/**
	 * Adds given message to the message queue and notifies this thread
	 * (actually getNextMessageFromQueue method) that a message is arrived.
	 */
	public synchronized void sendMessage(String aMessage) {
		messageQueue.add(aMessage);
		notify();
	}

	/**
	 * @return and deletes the next message from the message queue. If the queue
	 *         is empty, falls in sleep until notified for message arrival by
	 *         sendMessage method.
	 */
	private synchronized String getNextMessageFromQueue() throws InterruptedException {
		while (messageQueue.isEmpty()){
			wait();
		}
		final String message = messageQueue.poll();
		return message;
	}

	/**
	 * Sends given message to the client's socket.
	 */
	private void sendMessageToClient(String message) {
		socketPrintWriter.println(message);
		socketPrintWriter.flush();
		if (message.equals("Disconnected!")) {
			isDisconnected = true;
		}
	}

	/**
	 * Until interrupted or disconnected, reads messages from the message queue and sends them
	 * to the client's socket.
	 */
	public void run() {
		try {
			while (!isInterrupted() && !isDisconnected) {
				String message = getNextMessageFromQueue();
				sendMessageToClient(message);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		// Communication is broken. Interrupt both listener
		// and sender threads
		client.getClientListener().interrupt();
		serverDispatcher.removeActiveClient(client);
	}
}
