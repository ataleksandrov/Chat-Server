package commandPattern;

import java.util.regex.Pattern;

public final class CommandPatterns {
	private static final String SEPARATOR = " ";
	private static final String WORD = "[a-zA-Z0-9]*";
	private static final String WORDS = "([a-zA-Z0-9]* [a-zA-Z0-9]*)*";
	private static final String CONNECT = "connect" + SEPARATOR + WORD + SEPARATOR + WORD;
	private static final String REGISTER = "register" + SEPARATOR + WORD + SEPARATOR + WORD;
	private static final String LOGIN = "login" + SEPARATOR + WORD + SEPARATOR + WORD;
	private static final String DISCONNECT = "disconnect";
	private static final String LIST_USERS = "list-users";
	private static final String SEND = "send" + SEPARATOR + WORD + SEPARATOR + WORDS;
	private static final String SEND_W = "send" + SEPARATOR + WORD + SEPARATOR + WORD;
	private static final String SEND_FILE = "send-file" + SEPARATOR + WORD + SEPARATOR + WORD;
	private static final String CREATE_ROOM = "create-room" + SEPARATOR + WORD;
	private static final String DELETE_ROOM = "delete-room" + SEPARATOR + WORD;
	private static final String JOIN_ROOM = "join-room" + SEPARATOR + WORD;
	private static final String LEAVE_ROOM = "leave-room" + SEPARATOR + WORD;
	private static final String LIST_ROOMS = "list-rooms";
	private static final String LIST_USERS_ROOM = "list-users" + SEPARATOR + WORD;

	private static final String[] VALID_PATTERNS = { CONNECT, REGISTER, LOGIN, DISCONNECT, LIST_USERS, SEND, SEND_W,
			SEND_FILE, CREATE_ROOM, DELETE_ROOM, JOIN_ROOM, LEAVE_ROOM, LIST_ROOMS, LIST_USERS_ROOM };

	public static boolean isValidRequest(String request) {
		for (String pattern : VALID_PATTERNS) {
			if (Pattern.matches(pattern, request)) {
				return true;
			}
		}
		return false;
	}
}
