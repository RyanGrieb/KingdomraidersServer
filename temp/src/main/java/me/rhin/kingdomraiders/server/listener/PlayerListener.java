package me.rhin.kingdomraiders.server.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class PlayerListener implements Listener {

	public PlayerListener() {

	}

	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Main.getServer().players.add(new Player(conn));
	}

	public void onClose(WebSocket conn, String reason) {
		Main.getServer().players.remove(Main.getServer().getPlayerFromConn(conn));
	}

	public void onMessage(WebSocket conn, String message) {
		interpretMessage(conn, message);
	}

	public void interpretMessage(WebSocket conn, String message) {
		JSONObject jsonObj = new JSONObject(message);

		switch (jsonObj.getString("type")) {

		case "LoginRequest":
			Main.getServer().getManager().getAccountManager().loginToAccount(conn, message);
			break;

		case "RegisterRequest":
			Main.getServer().getManager().getAccountManager().registerAccount(conn, message);
			break;

		case "JoinGame":
			Main.getServer().getManager().getPlayerManager().joinGame(conn);
			//Main.getServer().getPlayerFromConn(conn).joinGame();
		}

	}

}
