package me.rhin.kingdomraiders.server.entity.player.movement;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class PlayerMovement {

	public static final JSONObject JSONPOSUPDATE = new JSONObject();
	public static final int XSPAWN = 1600;
	public static final int YSPAWN = 1600;

	Player player;

	public float playerX, playerY;
	public float velX, velY, rotationVel;
	public int rotation;

	public PlayerMovement(Player player) {
		this.player = player;

		// Set spawn position
		setPosition(XSPAWN, YSPAWN);
	}

	public void updatePosition(JSONObject jsonObj) {
		setPosition(jsonObj.getInt("x"), jsonObj.getInt("y"));
	}

	public void setPosition(int x, int y) {
		this.playerX = x;
		this.playerY = y;
	}

	// Set the players position directly
	public void sendPositionUpdate(WebSocket conn) {

		// Sent to client
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "PositionUpdate");
		jsonResponse.put("x", this.playerX);
		jsonResponse.put("y", this.playerY);
		conn.send(jsonResponse.toString());

		// Sent to MPPlayers
		JSONObject jsonMPResponse = new JSONObject();
		jsonMPResponse.put("type", "MPPositionUpdate");
		jsonMPResponse.put("name", player.profile.getName());
		jsonMPResponse.put("id", player.getID());
		jsonMPResponse.put("x", this.playerX);
		jsonMPResponse.put("y", this.playerY);

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(jsonMPResponse.toString());

	}

	// Set the players position to have him move to it.
	public void sendMovementTarget() {

		JSONPOSUPDATE.put("type", "MPMovementTarget");
		JSONPOSUPDATE.put("id", player.getID());
		JSONPOSUPDATE.put("x", playerX);
		JSONPOSUPDATE.put("y", playerY);

		for (Player p : Main.getServer().getMPPlayers(player))
			p.getConn().send(JSONPOSUPDATE.toString());
	}

	public void reset() {
		setPosition(XSPAWN, YSPAWN);
		this.rotation = 0;
	}

	public void update() {
		// Set updated position packet /w delay to other players
		if (player.inGame())
			sendMovementTarget();
	}

}
