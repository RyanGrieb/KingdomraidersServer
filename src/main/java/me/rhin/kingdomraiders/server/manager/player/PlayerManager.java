package me.rhin.kingdomraiders.server.manager.player;

import java.util.ArrayList;
import java.util.Iterator;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.manager.map.Map;

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

		// .. To add: send fire packet if these players are shooting. down below >>>>>>>
		this.sendExistingEntityLocations(conn);

		// .
	}

	public void sendExistingEntityLocations(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		// Add existing MPPlayers & locations
		JSONObject jsonExistingMPPlayer = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {

			// Don't load in dead players in the map
			if (p.isDead())
				continue;

			jsonExistingMPPlayer.put("type", "MPJoinGame");
			jsonExistingMPPlayer.put("id", p.getID());
			jsonExistingMPPlayer.put("name", p.profile.getName());
			jsonExistingMPPlayer.put("x", p.getX());
			jsonExistingMPPlayer.put("y", p.getY());
			conn.send(jsonExistingMPPlayer.toString());

		}
		// ..

		// Add existing Monsters (Probally need to rewrite this....).
		JSONObject jsonExistingMonster = new JSONObject();
		for (Monster m : Main.getServer().getManager().getMonsterManager().getMonsters(player.currentMap)) {
			// Send monster spawn to that specific player.
			jsonExistingMonster.put("type", "MonsterSpawn");
			jsonExistingMonster.put("name", m.getName());
			jsonExistingMonster.put("monsterID", m.getID());
			jsonExistingMonster.put("health", m.stats.health);
			jsonExistingMonster.put("x", m.getX());
			jsonExistingMonster.put("y", m.getY());
			conn.send(jsonExistingMonster.toString());

			// If some monsters have a target, send that info to the player.
			if (m.getTargetPlayer() != null) {
				// If it's following that player, it's shooting at it too.
				JSONObject jsonExistingMonsterShooter = new JSONObject();
				jsonExistingMonsterShooter.put("type", "AddShooter");
				jsonExistingMonsterShooter.put("id", m.getID());
				jsonExistingMonsterShooter.put("entityType", "Monster");
				jsonExistingMonsterShooter.put("currentDelay", m.entityShoot().getCurrentDelay());
				jsonExistingMonsterShooter.put("projectileID",
						Main.getServer().getManager().getItemManager().getItemJson(1).getProjID());
				jsonExistingMonsterShooter.put("targetX", m.entityShoot().targetX);
				jsonExistingMonsterShooter.put("targetY", m.entityShoot().targetY);
				jsonExistingMonsterShooter.put("attackdelay", m.getMonsterJSON().getAttackDelay());
				jsonExistingMonsterShooter.put("time", m.entityShoot().prevTime);
				player.getConn().send(jsonExistingMonsterShooter.toString());
			}
		}
	}

	public void leaveGame(WebSocket conn) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPLeaveGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());

		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

		// Reset the players variables back to defualt
		player.setInGame(false);
		player.setMap(Main.getServer().getManager().getMapManager().mainMap); // Set back to main world
	}

	public void respawnGame(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.setDead(false);
		player.stats.health = 100; // TODO: set it to max health retard
		player.setPosition(player.playerMovement.XSPAWN, player.playerMovement.YSPAWN);
		player.currentMap = Main.getServer().getManager().getMapManager().mainMap;

		// Reset player health
		this.sendPlayerHealth(player, player.stats.health);

		JSONObject jsonResponse = new JSONObject();
		// Resets the client chunks, also removes all mpplayers and entities ect. on
		// screen.
		jsonResponse = new JSONObject();
		jsonResponse.put("type", "ChunkReset");
		jsonResponse.put("worldtype", "Homeworld");
		jsonResponse.put("x", player.playerMovement.XSPAWN);
		jsonResponse.put("y", player.playerMovement.YSPAWN);
		player.getConn().send(jsonResponse.toString());

		// Add everything back, (mpplayers, monsters,ect.)
		Main.getServer().getManager().getPlayerManager().sendExistingEntityLocations(player.getConn());

		// Removes the player from the previous worlds

		// Send join response to other MPPlayers
		jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPJoinGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());
		jsonResponse.put("x", player.getX());
		jsonResponse.put("y", player.getY());
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

		// We need to send our infromation to existing players in the
		// dungeon!!!!!!!!!!!!!

		jsonResponse = new JSONObject();
		jsonResponse.put("type", "Respawn");
		player.getConn().send(jsonResponse.toString());
	}

	public void updatePosition(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.setPosition(jsonObj.getInt("x"), jsonObj.getInt("y"));
		// player.playerMovement.sendMovementTarget();
	}

	public void sendMessage(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		// If the message is a command
		if (jsonObj.getString("message").startsWith("/")) {
			Main.getServer().getManager().getCommandManager().recieveCommand(player, jsonObj.getString("message"));
			return;
		}

		JSONObject jsonResponse = new JSONObject();

		for (Player p : Main.getServer().getAllPlayers(player.currentMap)) {
			jsonResponse.put("type", "ChatMessage");
			jsonResponse.put("name", player.profile.getName());
			jsonResponse.put("message", jsonObj.get("message"));
			p.getConn().send(jsonResponse.toString());
		}
	}

	public void sendPlayerHealth(Player player, int health) {
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("type", "PlayerSetHealth");
		jsonObj.put("context", "Projectile");
		jsonObj.put("health", health);
		player.getConn().send(jsonObj.toString());
	}

	public void sendDeathInfo(Player player) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", player.getID());
		jsonObj.put("type", "PlayerDeath");
		for (Player p : Main.getServer().getAllPlayers(player.currentMap)) {
			p.getConn().send(jsonObj.toString());
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Player> getPlayers(Map map) {
		ArrayList<Player> playersInMap = (ArrayList<Player>) players.clone();

		for (Iterator<Player> iterator = playersInMap.iterator(); iterator.hasNext();) {
			Player p = iterator.next();
			if (!p.currentMap.equals(map))
				iterator.remove();
		}

		return playersInMap;
	}
}
