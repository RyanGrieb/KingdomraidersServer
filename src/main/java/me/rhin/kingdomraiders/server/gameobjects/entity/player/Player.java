package me.rhin.kingdomraiders.server.gameobjects.entity.player;

import org.java_websocket.WebSocket;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.movement.PlayerMovement;
import me.rhin.kingdomraiders.server.thread.UpdateThread;

public class Player extends Entity {

	private WebSocket conn;
	private int id;

	public PlayerProfile profile;
	public PlayerMovement playerMovement;
	// public PlayerProjectileHandler playerProjectileHandler;

	private boolean inGame;

	public Player(WebSocket conn) {
		super();
		this.w = 42;
		this.h = 42;
		this.conn = conn;
		this.id = Main.getServer().generateID();

		playerMovement = new PlayerMovement(this);
	}

	public void joinGame() {
		this.inGame = true;
	}
	// Sets

	// For Login/Register attempts
	public void setProfile(PlayerProfile profile) {
		this.profile = profile;

		//If were not removing our profile..
		if (this.profile != null) {
			this.stats.speed = this.profile.getSpeed();
			this.stats.damage = this.profile.getDamage();
		}
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
