package me.rhin.kingdomraiders.server.manager.player;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.entity.player.movement.PlayerMovement;

public class PlayerManager {

	public void joinGame(WebSocket conn) {
		sendPositionUpdate(conn);
		// TODO: The player should send this packet after selecting a character slot...
		// sendCharacterSlotProfile();

		// Send back the packet when were done updating the clients information
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "JoinGame");
		conn.send(jsonResponse.toString());
	}

	public void sendPositionUpdate(WebSocket conn) {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "PositionUpdate");
		jsonResponse.put("x", PlayerMovement.XSPAWN);
		jsonResponse.put("y", PlayerMovement.YSPAWN);
		conn.send(jsonResponse.toString());
	}

}
