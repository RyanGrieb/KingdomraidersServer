package me.rhin.kingdomraiders.server.manager.player.inventory;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class InventoryManager {

	public final String[] DEFAULT_INVENTORY = { "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
			"0", "0", "0", "0", "0", "0", "0", "0", "0" };

	public void requestInventory(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "RequestInventory");
		jsonResponse.put("inventory", player.profile.getInventory());
		conn.send(jsonResponse.toString());
	}

	public void modifyInventory(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		
		String[] inv = jsonObj.getJSONArray("inventory").join(",").split(",");
		player.profile.setInventory(inv);

	}

}
