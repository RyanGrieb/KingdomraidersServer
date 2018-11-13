package me.rhin.kingdomraiders.server.entity.player.movement;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class PlayerMovement {

	public static final int XSPAWN = 16000;// 16000
	public static final int YSPAWN = 31490;// 31500 (but on sreen it's 31490?)

	Player player;

	public PlayerMovement(Player player) {
		this.player = player;

		// Set spawn position
		player.setPosition(XSPAWN, YSPAWN);
	}

	// Recived with a delay of 100ms
	// public void updatePosition(JSONObject jsonObj) {
	// player.setPosition(jsonObj.getInt("x"), jsonObj.getInt("y"));
	// }

	public void sendPositionUpdate(WebSocket conn) {

		// Sent to client
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "PositionUpdate");
		jsonResponse.put("x", player.getX());
		jsonResponse.put("y", player.getY());
		conn.send(jsonResponse.toString());

		// Sent to MPPlayers
		JSONObject jsonMPResponse = new JSONObject();
		jsonMPResponse.put("type", "MPPositionUpdate");
		jsonMPResponse.put("name", player.profile.getName());
		jsonMPResponse.put("id", player.getID());
		jsonMPResponse.put("x", player.getX());
		jsonMPResponse.put("y", player.getY());

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(jsonMPResponse.toString());

	}

	// Set the players position to have him move to it.
	public void sendMovementTarget() {

		JSONObject jsonPosUpdate = new JSONObject();
		jsonPosUpdate.put("type", "MPMovementTarget");
		jsonPosUpdate.put("id", player.getID());
		jsonPosUpdate.put("x", player.getX());
		jsonPosUpdate.put("y", player.getY());

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(jsonPosUpdate.toString());

		// jsonPosUpdate = null;
	}

	public void reset() {
		player.setPosition(XSPAWN, YSPAWN);
	}

	public void update() {
		// Set updated position packet /w delay to other players
		if (player.inGame())
			sendMovementTarget();
	}

}
