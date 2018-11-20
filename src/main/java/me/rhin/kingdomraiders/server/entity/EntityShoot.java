package me.rhin.kingdomraiders.server.entity;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class EntityShoot {
	private boolean shooting;

	public EntityShoot() {

	}

	public void startShooting(JSONObject jsonObj) {

		this.shooting = true;
	}

	public void stopShooting() {
		this.shooting = false;
	}

	public void shootingUpdate() {
		// System.out.println("Boom");

	}

	public void update() {
		if (this.shooting) {

		}
	}
}
