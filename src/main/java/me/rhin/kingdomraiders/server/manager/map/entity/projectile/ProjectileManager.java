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
	}

	public void playerUpdateShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().setTarget(jsonObj.getDouble("targetX"), jsonObj.getDouble("targetY"));
	}

	public void playerStopShooting(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.entityShoot().stopShooting();
	}
}
