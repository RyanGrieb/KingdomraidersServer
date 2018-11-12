package me.rhin.kingdomraiders.server.entity;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class EntityShoot {
	private boolean shooting;

	public EntityShoot() {

	}

	public void startShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));
		String test = Main.getServer().getManager().getItemManager().getItemJson(castItem).name();

		JSONObject jsonResponse = new JSONObject();
		for (Player p : Main.getServer().getMPPlayers(player)) {
			jsonResponse.put("type", "MPAddShooter");
			jsonResponse.put("id", player.getID());
			jsonResponse.put("projectileID", Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjID());
			jsonResponse.put("targetX", jsonObj.get("targetX"));
			jsonResponse.put("targetY", jsonObj.get("targetY"));
			p.getConn().send(jsonResponse.toString());
		}

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
