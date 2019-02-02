package server;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

import chatApp.clientManager.Client;
import chatApp.clientManager.ClientManager;
import chatApp.clientManager.Listener;
import chatApp.clientManager.Sender;
import chatApp.server.Dispatcher;
import chatApp.server.Server;

public class ServerTest {
	static ClientManager cm;
	static Dispatcher dispatcher;
	static Client client;

	@BeforeClass
	public static void initialization() {
		Server s = new Server();
		cm = new ClientManager();
		dispatcher = new Dispatcher();
		client = new Client("ivan", "password");
	}

	@Test
	public void loginTestAssertFalse() {
		final String username = "p";
		final String password = "p";
		assertFalse(cm.loginClient(new Client(username, password), username, password));
	}

	@Test
	public void registerTestAssertTrue() {
		final String username = "p1";
		final String password = "p1";
		assertTrue(cm.registerClient(username, password));
	}

	@Test
	public void loginTestAssertTrue() {
		final String username = "angel";
		final String password = "a";
		assertTrue(cm.loginClient(new Client(username, password), username, password));
	}

	@Test
	public void dispatcherAddClientTestAssertTrue() {
		assertTrue(dispatcher.addActiveClient(new Client("petur", "parola")));
	}

	@Test
	public void dispatcherRemoveClientTestAssertFalse() {
		assertFalse(dispatcher.removeActiveClient(new Client("georgi", "parola")));
	}

	@Test
	public void dispatchSendMessageTest() {
		final String msg = "send angel zdravei";
		assertTrue(dispatcher.dispatchMessage(client, msg));
	}

	@Test
	public void dispatchlistUsersMessageTest() {
		final String msg = "list-users";
		assertTrue(dispatcher.dispatchMessage(client, msg));
	}

	@Test
	public void dispatchCreateRoomsMessageTest() {
		final String msg = "create-room myroom";
		assertTrue(dispatcher.dispatchMessage(client, msg));
	}

	@Test
	public void dispatchDeleteRoomMessageTest() {
		final String msg = "delete-room myroom";
		assertTrue(dispatcher.dispatchMessage(client, msg));
	}

	@Test
	public void dispatchJoinRoomMessageTest() {
		final String msg = "join-room myroom";
		assertTrue(dispatcher.dispatchMessage(client, msg));
	}

	@Test
	public void listenerTest() {
		client.setIsActive(true);
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("localhost", 4444));
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.setSocket(socket);
		final Listener listener = new Listener(client, dispatcher);
		assertNotNull(listener);
	}

	@Test
	public void senderTest() {
		client.setIsActive(true);
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("localhost", 4444));
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.setSocket(socket);
		Sender sender = new Sender(client, dispatcher);

		assertNotNull(sender);
	}

}
