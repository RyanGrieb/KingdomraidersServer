package me.rhin.kingdomraiders.server.manager.player.stats;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class StatsManager {

	public void requestStats(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "RequestStats");
		jsonResponse.put("dex", player.profile.getDex());
		jsonResponse.put("speed", player.profile.getSpeed());
		jsonResponse.put("health", player.profile.getHealth());
		jsonResponse.put("mana", player.profile.getMana());
		conn.send(jsonResponse.toString());
	}
	
	//Update Stats, maybe?

}
