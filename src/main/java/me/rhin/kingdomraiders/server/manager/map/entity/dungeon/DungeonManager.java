package me.rhin.kingdomraiders.server.manager.map.entity.dungeon;

import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.dungeon.Dungeon;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;

public class DungeonManager {

	public ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();

	public void createDungeon(Dungeon dunegon) {
		this.dungeons.add(dunegon);
	}

	public void enterDungeon(WebSocket conn, JSONObject jsonObj) {
		double x = jsonObj.getInt("x");
		double y = jsonObj.getInt("y");

		for (int i = 0; i < dungeons.size(); i++) {
			Dungeon d = dungeons.get(i);

			if (d.getX() == x && d.getY() == y) {
				System.out.println("Entering existing dungeon");
				Main.getServer().getPlayerFromConn(conn).setMapIndex(i);

				// Send a chunk reset packet to player
				JSONObject jsonReponse = new JSONObject();
				jsonReponse.put("type", "ChunkReset");
				jsonReponse.put("x", d.xSpawn());
				jsonReponse.put("y", d.ySpawn());
				conn.send(jsonReponse.toString());
				return;
			}
		}

		// If the dungeon already doesn't exist, create & enter it.
		int currentMapIndex = Main.getServer().getPlayerFromConn(conn).getMapIndex();
		TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(currentMapIndex, x, y);

		// TODO: !!!!!!!!!!! CHECK IF THE TILENAME CONTAINS HOMEWORLD, IF SO, SEND THE
		// PLAYER BACK TO MAIN WORLD AT INDEX -1!
		// !!!!!!!

		Dungeon dungeon = new Dungeon(tile.name(), x, y);
		this.createDungeon(dungeon);
		Main.getServer().getPlayerFromConn(conn).setMapIndex(dungeons.size() - 1);

		// Send a chunk reset packet to player
		JSONObject jsonReponse = new JSONObject();
		jsonReponse.put("type", "ChunkReset");
		jsonReponse.put("x", dungeon.xSpawn());
		jsonReponse.put("y", dungeon.ySpawn());
		conn.send(jsonReponse.toString());

	}

}
