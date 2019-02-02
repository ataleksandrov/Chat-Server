package chatApp.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

class TextDataTransmitter extends Thread {

	private BufferedReader reader;
	private PrintWriter writer;

	public TextDataTransmitter(BufferedReader reader, PrintWriter writer) {
		this.reader = reader;
		this.writer = writer;
	}

	/**
	 * Until interrupted reads a text line from the reader and sends it to the
	 * writer.
	 */
	public void run() {
		try {
			while (!isInterrupted()) {
				String data = reader.readLine();
				writer.println(data);
				writer.flush();
				if (data.equals("disconnect") || data.equals("Disconnected!")) {
					break;
				}
			}
		} catch (IOException ioe) {
			System.out.println("Lost connection to server.");
		}
	}
}