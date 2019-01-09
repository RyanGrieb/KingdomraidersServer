package me.rhin.kingdomraiders.server.manager.map.entity.projectile;

import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.EntityShoot;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;
import me.rhin.kingdomraiders.server.manager.map.MapManager;

public class ProjectileManager {
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public void playerStartShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		player.entityShoot().startShooting(player, jsonObj);
		int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "AddShooter");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("entityType", "Player");
		jsonResponse.put("currentDelay", player.entityShoot().getCurrentDelay());
		jsonResponse.put("projectileID",
				Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjID());
		jsonResponse.put("targetX", jsonObj.get("targetX"));
		jsonResponse.put("targetY", jsonObj.get("targetY"));
		jsonResponse.put("attackdelay", player.profile.getAttackDelay());
		jsonResponse.put("time", jsonObj.get("time"));

		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

	}

	public void monsterStartShooting(Monster monster) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers(monster.currentMap)) {
			jsonObj.put("type", "AddShooter");
			jsonObj.put("id", monster.getID());
			jsonObj.put("entityType", "Monster");
			jsonObj.put("currentDelay", monster.entityShoot().getCurrentDelay());
			jsonObj.put("projectileID", Main.getServer().getManager().getItemManager().getItemJson(1).getProjID());
			jsonObj.put("targetX", monster.getTargetPlayer().getX() + monster.getTargetPlayer().getWidth() / 2);
			jsonObj.put("targetY", monster.getTargetPlayer().getY() + monster.getTargetPlayer().getHeight() / 2);
			jsonObj.put("attackdelay", monster.getMonsterJSON().getAttackDelay());
			jsonObj.put("time", System.currentTimeMillis());
			p.getConn().send(jsonObj.toString());
		}
	}

	public void playerUpdateShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().setTarget(jsonObj.getDouble("targetX"), jsonObj.getDouble("targetY"));
	}

	public void monsterUpdateShooting(Monster monster) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers(monster.currentMap)) {
			jsonObj.put("type", "ShooterUpdate");
			jsonObj.put("id", monster.getID());
			jsonObj.put("entityType", "Monster");
			jsonObj.put("targetX", monster.entityShoot().targetX);
			jsonObj.put("targetY", monster.entityShoot().targetY);
			p.getConn().send(jsonObj.toString());
		}
	}

	public void playerStopShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().stopShooting();

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "RemoveShooter");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("entityType", "Player");
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());
	}

	public void monsterStopShooting(Monster monster) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers(monster.currentMap)) {
			jsonObj.put("type", "RemoveShooter");
			jsonObj.put("id", monster.getID());
			jsonObj.put("entityType", "Monster");
			p.getConn().send(jsonObj.toString());
		}
	}
}
