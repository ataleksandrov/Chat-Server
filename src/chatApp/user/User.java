package chatApp.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class User extends Thread {

	private Socket socket;
	private BufferedReader consoleReader;
	private PrintWriter consoleWriter;
	private BufferedReader socketReader;
	private PrintWriter socketWriter;

	public User() {
		consoleReader = new BufferedReader(new InputStreamReader(System.in));
		consoleWriter = new PrintWriter(new OutputStreamWriter(System.out));
		socket = new Socket();
		connect(consoleReader);
		try {
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Connected to server " + socket.getInetAddress() + ":" + socket.getPort());
	}

	@Override
	public void run() {
		// Start socket --> console transmitter thread
		TextDataTransmitter socketToConsoleTransmitter = new TextDataTransmitter(socketReader, consoleWriter);
		socketToConsoleTransmitter.start();

		// Start console --> socket transmitter thread
		TextDataTransmitter consoleToSocketTransmitter = new TextDataTransmitter(consoleReader, socketWriter);
		consoleToSocketTransmitter.start();
	}

	private void connect(BufferedReader consoleReader) {
		String host = null;
		int port = 0;
		do {
			try {
				System.out.println("Please insert a host: ");
				host = consoleReader.readLine();
				System.out.println("Please insert a port: ");
				port = Integer.parseInt(consoleReader.readLine());
			} catch (NumberFormatException nFe) {
				nFe.printStackTrace();
			} catch (IOException IOe) {
				IOe.printStackTrace();
			}
			openConnection(host, port);
		} while (!isConnected());
	}

	private boolean isConnected() {
		return socket.isConnected();
	}

	private void openConnection(String host, int port) {
		try {
			socket.connect(new InetSocketAddress(host, port));
		} catch (IOException IOe) {
			final String failureMsg = "Couldn't connect to " + host + " " + port;
			System.out.println(failureMsg);
		}
	}

}
