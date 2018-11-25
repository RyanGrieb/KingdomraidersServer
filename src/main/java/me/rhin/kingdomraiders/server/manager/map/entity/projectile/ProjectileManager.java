package me.rhin.kingdomraiders.server.manager.map.entity.projectile;

import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;

public class ProjectileManager {
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public void startShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.entityShoot().startShooting(player, jsonObj);
		int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPAddShooter");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("projectileID",
				Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjID());
		jsonResponse.put("targetX", jsonObj.get("targetX"));
		jsonResponse.put("targetY", jsonObj.get("targetY"));
		jsonResponse.put("dex", player.profile.getDex());

		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

	}

	public void updateShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().setTarget(jsonObj.getDouble("targetX"), jsonObj.getDouble("targetY"));
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPShooterUpdate");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("targetX", jsonObj.get("targetX"));
		jsonResponse.put("targetY", jsonObj.get("targetY"));
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());
	}

	public void stopShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().stopShooting();

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPRemoveShooter");
		jsonResponse.put("id", player.getID());
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());
	}
}
