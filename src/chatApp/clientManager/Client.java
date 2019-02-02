package chatApp.clientManager;

import java.net.Socket;
import java.util.Comparator;

public class Client {

	private Socket socket;
	private Listener clientListener;
	private Sender clientSender;
	private String username;
	private String password;
	private boolean isActive;

	public Client() {
	}

	public Client(String username) {
		this.username = username;
	}

	public Client(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public static Comparator<Client> registerComparator() {
		return new Comparator<Client>() {

			@Override
			public int compare(Client c1, Client c2) {
				if (c1.getUsername().equals(c2.getUsername())) {
					return 0;
				}
				return -1;
			}

		};
	}

	public static Comparator<Client> loginComparator() {
		return new Comparator<Client>() {

			@Override
			public int compare(Client c1, Client c2) {
				if (c1.equals(c2)) {
					return 0;
				}
				return -1;
			}

		};
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Listener getClientListener() {
		return clientListener;
	}

	public void setClientListener(Listener clientListener) {
		this.clientListener = clientListener;
	}

	public Sender getClientSender() {
		return clientSender;
	}

	public void setClientSender(Sender clientSender) {
		this.clientSender = clientSender;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		Client other = (Client) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
