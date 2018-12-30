package me.rhin.kingdomraiders.server.manager.map.dungeon;

import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.dungeon.Dungeon;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;

public class DungeonManager {

	public ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();

	public void createDungeon(Dungeon dunegon) {
		this.dungeons.add(dunegon);
	}

	public void enterDungeon(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		double x = jsonObj.getInt("x");
		double y = jsonObj.getInt("y");

		// Check if the portal actually exists
		TileType type = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(player.currentMap, x, y);
		if (!type.name().contains("DUNGEON"))
			return;
		
		// Send leave packet to players in the players previous world.
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPLeaveGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

		for (int i = 0; i < dungeons.size(); i++) {
			Dungeon d = dungeons.get(i);

			if (d.getPortalMap().equals(player.currentMap))
				if (d.getX() == x && d.getY() == y) {

					// Checks if the player is already entering dongeon
					if (player.currentMap.equals(d.getMap()))
						return;

					// Send a chunk reset packet to player
					player.currentMap = d.getMap();
					this.sendDungeonJoinPackets(player, d.xSpawn(), d.ySpawn());
					return;
				}
		}

		// If the dungeon already doesn't exist, create & enter it.
		TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(player.currentMap, x, y);

		// Teleport player back to main world.
		if (tile.name().contains("HOMEWORLD")) {
			player.currentMap = Main.getServer().getManager().getMapManager().mainMap;
			this.sendDungeonJoinPackets(player, player.playerMovement.XSPAWN, player.playerMovement.YSPAWN);
			return;
		}

		Dungeon dungeon = new Dungeon(tile.name(), player.currentMap, x, y);
		this.createDungeon(dungeon);

		// Send a chunk reset packet to player
		player.currentMap = dungeon.getMap();
		this.sendDungeonJoinPackets(player, dungeon.xSpawn(), dungeon.ySpawn());
	}

	public void sendDungeonJoinPackets(Player player, double xSpawn, double ySpawn) {
		player.setPosition(xSpawn, ySpawn);

		JSONObject jsonResponse = new JSONObject();

		// Resets the client chunks, also removes all mpplayers and entities ect. on
		// screen.
		jsonResponse = new JSONObject();
		jsonResponse.put("type", "ChunkReset");
		jsonResponse.put("x", xSpawn);
		jsonResponse.put("y", ySpawn);
		player.getConn().send(jsonResponse.toString());

		// Add everything back, (mpplayers, monsters,ect.)
		Main.getServer().getManager().getPlayerManager().sendExistingEntityLocations(player.getConn());

		// Removes the player from the previous worlds

		// Send join response to other MPPlayers
		jsonResponse = new JSONObject();
		jsonResponse.put("type", "MPJoinGame");
		jsonResponse.put("id", player.getID());
		jsonResponse.put("name", player.profile.getName());
		jsonResponse.put("x", player.getX());
		jsonResponse.put("y", player.getY());
		Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

		// We need to send our infromation to existing players in the
		// dungeon!!!!!!!!!!!!!

	}

}
