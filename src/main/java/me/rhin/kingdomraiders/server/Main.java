package me.rhin.kingdomraiders.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import me.rhin.kingdomraiders.server.entity.player.Player;
import me.rhin.kingdomraiders.server.listener.Listener;
import me.rhin.kingdomraiders.server.listener.MapListener;
import me.rhin.kingdomraiders.server.listener.PlayerListener;
import me.rhin.kingdomraiders.server.manager.Manager;

public class Main extends WebSocketServer {

	// Testing the git

	private static Main server;

	private static final String HOST = "localhost";
	private static final int PORT = 5000;

	private Manager manager = new Manager();
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	public ArrayList<Player> players = new ArrayList<Player>();

	public Main(InetSocketAddress address) {
		super(address);

		listeners.add(new PlayerListener());
		listeners.add(new MapListener());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// conn.send("Welcome to the server!"); // This method sends a message to the
		// new client
		// broadcast("new connection: " + handshake.getResourceDescriptor()); // This
		// method sends a message to all clients
		// connected
		for (Listener l : listeners)
			l.onOpen(conn, handshake);

		System.out.println("new connection to " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {

		for (Listener l : listeners)
			l.onClose(conn, reason);

		System.out.println(
				"closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {

		for (Listener l : listeners)
			l.onMessage(conn, message);

		System.out.println("received message from " + conn.getRemoteSocketAddress() + ": " + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occured on connection " + conn.getRemoteSocketAddress() + ":" + ex);
	}

	@Override
	public void onStart() {
		System.out.println("Server started successfully.");
	}

	public static void main(String[] args) {
		String host = HOST;
		int port = PORT;

		server = new Main(new InetSocketAddress(host, port));
		server.run();
	}

	// Getters

	public static Main getServer() {
		return server;
	}

	public Manager getManager() {
		return manager;
	}

	public Player getPlayerFromConn(WebSocket conn) {
		for (Player p : players)
			if (p.getConn().equals(conn))
				return p;
		return null;
	}
}