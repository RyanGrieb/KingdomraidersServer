package me.rhin.kingdomraiders.server.entity.player;

import java.util.Collection;

import org.java_websocket.WebSocket;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.Entity;
import me.rhin.kingdomraiders.server.entity.player.movement.PlayerMovement;

public class Player extends Entity {

	private WebSocket conn;
	private int id;

	public PlayerProfile profile;
	public PlayerMovement playerMovement;

	private boolean inGame;

	public Player(WebSocket conn) {
		this.conn = conn;
		this.id = Main.getServer().generateID();

		playerMovement = new PlayerMovement(this);
	}

	public void joinGame() {
		// Send position update
		// Send loaded character slot
		// Send continue packet..

		this.inGame = true;

		//this.playerMovement.sendPositionUpdate(conn);
	}

	// Setts

	// For Login/Register attempts
	public void setProfile(PlayerProfile profile) {
		this.profile = profile;
	}

	public void setInGame(boolean b) {
		this.inGame = b;
		
		this.playerMovement.reset();
	}

	// Getters

	public WebSocket getConn() {
		return conn;
	}

	public int getID() {
		return id;
	}

	public boolean inGame() {
		return inGame;
	}

	public void update() {
		super.update();

		this.playerMovement.update();
	}

}
