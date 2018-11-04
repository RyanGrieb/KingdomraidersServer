package me.rhin.kingdomraiders.server.manager.player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;
import me.rhin.kingdomraiders.server.helper.Helper;

public class PlayerManager {

	public void joinGame(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		if (player.profile == null)
			return;

		// Handle player-class based join methods
		player.joinGame();

		// Client-Side response
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "JoinGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());
		jsonResponse.put("x", player.playerMovement.playerX);
		jsonResponse.put("y", player.playerMovement.playerY);
		conn.send(jsonResponse.toString());

		// Send join response to other MPPlayers
		JSONObject jsonMPResponse = new JSONObject();
		jsonMPResponse.put("type", "MPJoinGame");
		jsonMPResponse.put("id", player.getID());
		jsonMPResponse.put("name", player.profile.getName());
		jsonMPResponse.put("x", player.playerMovement.playerX);
		jsonMPResponse.put("y", player.playerMovement.playerY);

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(jsonMPResponse.toString());
		// ..

		// Add existing MPPlayers & locations
		for (Player p : Main.getServer().getMPPlayers(player)) {
			JSONObject jsonExistingMPPlayer = new JSONObject();

			jsonExistingMPPlayer.put("type", "MPJoinGame");
			jsonExistingMPPlayer.put("id", p.getID());
			jsonExistingMPPlayer.put("name", p.profile.getName());
			jsonExistingMPPlayer.put("x", p.playerMovement.playerX);
			jsonExistingMPPlayer.put("y", p.playerMovement.playerY);
			conn.send(jsonExistingMPPlayer.toString());
		}
		// ..
	}

	public void leaveGame(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.setInGame(false);

		for (Player p : Main.getServer().getMPPlayers(player)) {
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("type", "MPLeaveGame");
			jsonResponse.put("id", player.getID());
			jsonResponse.put("name", player.profile.getName());
			p.getConn().send(jsonResponse.toString());
		}
	}

	public void updatePosition(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.playerMovement.updatePosition(jsonObj);
	}

}
