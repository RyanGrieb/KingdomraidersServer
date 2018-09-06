package me.rhin.kingdomraiders.server.entity.player;

import org.java_websocket.WebSocket;

import me.rhin.kingdomraiders.server.entity.Entity;
import me.rhin.kingdomraiders.server.entity.player.movement.PlayerMovement;

public class Player extends Entity {

	private WebSocket conn;

	public PlayerProfile profile;
	public PlayerMovement playerMovement;

	public Player(WebSocket conn) {
		this.conn = conn;

		playerMovement = new PlayerMovement();
	}

	public void joinGame() {
		//Send position update
		//Send loaded character slot
		//Send continue packet..
	}

	// For Login/Register attempts
	public void setProfile(PlayerProfile profile) {
		this.profile = profile;
	}

	// Getters

	public WebSocket getConn() {
		return conn;
	}

}
