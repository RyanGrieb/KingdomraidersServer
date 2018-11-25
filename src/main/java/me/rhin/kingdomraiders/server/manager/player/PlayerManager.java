package me.rhin.kingdomraiders.server.manager.player;

import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class PlayerManager {

	public ArrayList<Player> players = new ArrayList<Player>();

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

		Main.getServer().sendToAllMPPlayers(player, jsonMPResponse.toString());

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

		// Add existing Monsters
		JSONObject jsonExistingMonster = new JSONObject();
		for (Monster m : Main.getServer().getManager().getMonsterManager().monsters) {
			jsonExistingMonster.put("type", "MonsterSpawn");
			jsonExistingMonster.put("name", m.getName());
			jsonExistingMonster.put("monsterID", m.getID());
			jsonExistingMonster.put("health", m.stats.health);
			jsonExistingMonster.put("x", m.getX());
			jsonExistingMonster.put("y", m.getY());
			conn.send(jsonExistingMonster.toString());
		}
		// .
	}

	public void leaveGame(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.setInGame(false);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPLeaveGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());

		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());
	}

	public void updatePosition(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.setPosition(jsonObj.getInt("x"), jsonObj.getInt("y"));

		player = null;
	}

	public void sendMessage(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		// If the message is a command
		if (jsonObj.getString("message").startsWith("/")) {
			Main.getServer().getManager().getCommandManager().recieveCommand(player, jsonObj.getString("message"));
			return;
		}

		JSONObject jsonResponse = new JSONObject();

		for (Player p : Main.getServer().getAllPlayers()) {
			jsonResponse.put("type", "ChatMessage");
			jsonResponse.put("name", player.profile.getName());
			jsonResponse.put("message", jsonObj.get("message"));
			p.getConn().send(jsonResponse.toString());
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

}
