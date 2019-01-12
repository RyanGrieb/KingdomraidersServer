package me.rhin.kingdomraiders.server.gameobjects.entity.player;

import org.java_websocket.WebSocket;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.movement.PlayerMovement;
import me.rhin.kingdomraiders.server.manager.map.Map;
import me.rhin.kingdomraiders.server.thread.UpdateThread;

public class Player extends Entity {

	private WebSocket conn;
	private int id;
	private int ping;

	public PlayerProfile profile;
	public PlayerMovement playerMovement;
	// public PlayerProjectileHandler playerProjectileHandler;

	private boolean inGame;

	public Player(WebSocket conn) {
		super(Main.getServer().getManager().getMapManager().mainMap); // Spawn in defualt map
		this.w = 42;
		this.h = 42;
		this.conn = conn;
		this.id = Main.getServer().generateID();

		playerMovement = new PlayerMovement(this);
	}

	public void joinGame() {
		this.inGame = true;
		this.initStats();
	}

	// Sets

	// For Login/Register attempts
	public void setProfile(PlayerProfile profile) {
		this.profile = profile;

		// If were not removing our profile..
		// if (this.profile != null)
		// this.initStats();

	}

	public void initStats() {
		this.stats.speed = this.profile.getSpeed();
		this.stats.damage = this.profile.getDamage();
		this.stats.health = this.profile.getHealth();
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public void setInGame(boolean b) {
		this.inGame = b;

		this.playerMovement.reset();
	}

	public void damage(int damage) {
		this.stats.health -= damage;
		if (this.stats.health <= 0) {
			System.out.println("you died!");
			Main.getServer().getManager().getPlayerManager().sendDeathInfo(this);
			return;
		}

		Main.getServer().getManager().getPlayerManager().sendPlayerHealth(this, this.stats.health);
	}

	// Getters

	public WebSocket getConn() {
		return conn;
	}

	public int getID() {
		return id;
	}

	public int getPing() {
		return this.ping;
	}

	public boolean inGame() {
		return inGame;
	}

	public void update() {
		super.update();
		// this.playerMovement.update();
	}

	public void slowUpdate() {
		super.slowUpdate();
		this.playerMovement.update();
	}

}
