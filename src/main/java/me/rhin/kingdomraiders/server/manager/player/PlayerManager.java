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
		jsonResponse.put("x", player.getX());
		jsonResponse.put("y", player.getY());
		conn.send(jsonResponse.toString());

		// Send join response to other MPPlayers
		JSONObject jsonMPResponse = new JSONObject();
		jsonMPResponse.put("type", "MPJoinGame");
		jsonMPResponse.put("id", player.getID());
		jsonMPResponse.put("name", player.profile.getName());
		jsonMPResponse.put("x", player.getX());
		jsonMPResponse.put("y", player.getY());

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(jsonMPResponse.toString());
		// ..

		// Add existing MPPlayers & locations
		JSONObject jsonExistingMPPlayer = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {

			jsonExistingMPPlayer.put("type", "MPJoinGame");
			jsonExistingMPPlayer.put("id", p.getID());
			jsonExistingMPPlayer.put("name", p.profile.getName());
			jsonExistingMPPlayer.put("x", p.getX());
			jsonExistingMPPlayer.put("y", p.getY());
			conn.send(jsonExistingMPPlayer.toString());
		}
		// ..
	}

	public void leaveGame(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.setInGame(false);

		JSONObject jsonResponse = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {
			jsonResponse.put("type", "MPLeaveGame");
			jsonResponse.put("id", player.getID());
			jsonResponse.put("name", player.profile.getName());
			p.getConn().send(jsonResponse.toString());
		}
	}

	public void updatePosition(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.setPosition(jsonObj.getInt("x"), jsonObj.getInt("y"));

		player = null;
	}

	public void sendMessage(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();

		for (Player p : Main.getServer().players) {
			jsonResponse.put("type", "ChatMessage");
			jsonResponse.put("name", player.profile.getName());
			jsonResponse.put("message", jsonObj.get("message"));
			p.getConn().send(jsonResponse.toString());
		}
	}

	public void startShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().startShooting(conn, jsonObj);

	}

	public void updateShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {
			jsonResponse.put("type", "MPShooterUpdate");
			jsonResponse.put("id", player.getID());
			jsonResponse.put("targetX", jsonObj.get("targetX"));
			jsonResponse.put("targetY", jsonObj.get("targetY"));
			p.getConn().send(jsonResponse.toString());
		}
	}

	public void stopShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().stopShooting();

		JSONObject jsonResponse = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {
			jsonResponse.put("type", "MPRemoveShooter");
			jsonResponse.put("id", player.getID());
			p.getConn().send(jsonResponse.toString());
		}
	}

}
