package me.rhin.kingdomraiders.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import me.rhin.kingdomraiders.server.entity.player.Player;
import me.rhin.kingdomraiders.server.listener.Listener;
import me.rhin.kingdomraiders.server.listener.MapListener;
import me.rhin.kingdomraiders.server.listener.PlayerListener;
import me.rhin.kingdomraiders.server.manager.Manager;
import me.rhin.kingdomraiders.server.thread.UpdateThread;

public class Main extends WebSocketServer {

	// Testing the git

	private static Main server;

	//private static final String HOST = "192.168.1.77";
	private static final String HOST = "localhost";
	private static final int PORT = 5000;

	private static int idIndex = 0;

	private UpdateThread thread;
	private Manager manager;
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	public ArrayList<Player> players = new ArrayList<Player>();

	public Main(InetSocketAddress address) {
		super(address);

		listeners.add(new PlayerListener());
		listeners.add(new MapListener());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
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
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		System.out.println("Server started successfully.");

		this.thread = new UpdateThread();
		this.manager = new Manager();
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

	public UpdateThread getUpdateThread() {
		return thread;
	}

	public Manager getManager() {
		return manager;
	}

	public int generateID() {
		return ++idIndex;
	}

	public Player getPlayerFromConn(WebSocket conn) {
		for (Player p : players)
			if (p.getConn().equals(conn))
				return p;
		return null;
	}

	public ArrayList<Player> getMPPlayers(Player toExclude) {
		ArrayList<Player> mpPlayers = new ArrayList<Player>(players);
		mpPlayers.remove(toExclude);

		// Remove players not in game.
		for (Iterator<Player> iterator = mpPlayers.iterator(); iterator.hasNext();) {
			Player p = iterator.next();
			if (!p.inGame())
				iterator.remove();
		}

		return mpPlayers;
	}
}