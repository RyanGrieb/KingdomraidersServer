package me.rhin.kingdomraiders.server.manager.map;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;
import me.rhin.kingdomraiders.server.helper.Helper;

public class MapManager {

	public Map mainMap;
	private final int CHUNKSIZE = 15;

	public MapManager() {
		// Adds the mainworld to maps index of 0.
		try {
			mainMap = (new Map("DEFAULT_WORLD", new ArrayList<>(
					Files.readAllLines(Paths.get("./assets/map/gamemap.map"), StandardCharsets.UTF_8))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new MapUpdater(mainMap);
	}

	public void sendChunkFromLocation(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		ArrayList<String> mapLines = player.currentMap.mapLines;

		int x = jsonObj.getInt("x");
		int y = jsonObj.getInt("y");

		// I add stuff to w/h to prevent overlap & fit them
		int[][] chunk = null;
		int[][] topLayerChunk = null;

		chunk = init2DChunkArray(chunk);
		topLayerChunk = init2DChunkArray(topLayerChunk);

		// assume location is from top left.. init 2D array
		for (int chunkY = 0; chunkY < chunk.length; chunkY++)
			for (int chunkX = 0; chunkX < chunk[chunkY].length; chunkX++) {

				// CHECK IF Y IS OUT OF BOUNDS..
				if (y + chunkY >= mapLines.size() || y + chunkY < 0)
					continue;

				String selectedLine = mapLines.get(y + chunkY);

				int firstBracket = Helper.findIndexAt(selectedLine, "[", (int) (x + chunkX) + 1);
				int lastBracket = Helper.findIndexAt(selectedLine, "]", (int) (x + chunkX) + 1);

				// CHECK IF X IS OUT OF BOUNDS..
				if (firstBracket == -1 || lastBracket == -1 || x + chunkX < 0)
					continue;

				String tileID = selectedLine.substring(firstBracket + 1, lastBracket);

				// If we have a stacked tile
				if (tileID.contains(",")) {
					String[] array = tileID.split(",", -1);

					// Put chunk layers in respective 2D arrays
					chunk[chunkY][chunkX] = Integer.parseInt(array[0]);
					topLayerChunk[chunkY][chunkX] = Integer.parseInt(array[1]);

				} else {
					chunk[chunkY][chunkX] = Integer.parseInt(tileID);
					// Top layer is transparent...
					topLayerChunk[chunkY][chunkX] = -1;
				}
			}

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "ChunkRequest");
		jsonResponse.put("x", jsonObj.getInt("x"));
		jsonResponse.put("y", jsonObj.getInt("y"));
		jsonResponse.put("chunk", chunk);
		jsonResponse.put("topchunk", topLayerChunk);
		conn.send(jsonResponse.toString());
	}

	private int[][] init2DChunkArray(int[][] chunk) {
		chunk = new int[CHUNKSIZE][CHUNKSIZE];

		for (int chunkX = 0; chunkX < chunk.length; chunkX++)
			for (int chunkY = 0; chunkY < chunk[chunkX].length; chunkY++)
				chunk[chunkX][chunkY] = -1;

		return chunk;
	}

	// We flip the chunk vertically since for some reason json reverses our 2D
	// array.. ):
	private int[][] flipVertically(int[][] theArray) {
		for (int i = 0; i < (theArray.length / 2); i++) {
			int[] temp = theArray[i];
			theArray[i] = theArray[theArray.length - i - 1];
			theArray[theArray.length - i - 1] = temp;
		}

		return theArray;
	}

	public void build(WebSocket conn, int inputX, int inputY, int id, boolean replace) {
		Map map = Main.getServer().getPlayerFromConn(conn).currentMap;
		// We need to scale down.
		int x = inputX / 32;
		int y = inputY / 32;

		map = this.replaceTile(map, id, x, y, replace);

		// If were in the main map, modify the file
		if (map.name.equals("DEFAULT_WORLD")) {
			try {
				Files.write(Paths.get("assets/map/gamemap.map"), this.mainMap.mapLines, StandardCharsets.UTF_8);
			} catch (IOException e) {
			}

		}

		// Send chunk update, makes client re-request the chunk from that location
		JSONObject jsonPacket = new JSONObject();
		jsonPacket.put("type", "ChunkUpdate");
		jsonPacket.put("x", inputX);
		jsonPacket.put("y", inputY);

		if (conn != null) {
			Player player = Main.getServer().getPlayerFromConn(conn);

			// Send this to everyone
			conn.send(jsonPacket.toString());
			Main.getServer().sendToAllMPPlayers(player, jsonPacket.toString());
		} else
			Main.getServer().sendToAllMPPlayers(jsonPacket.toString());
	}

	public Map replaceTile(Map map, int id, int x, int y, boolean replace) {
		// If building outside of map
		if (y >= map.mapLines.size() || y < 0)
			return map;

		String selectedLine = map.mapLines.get((int) y);

		int firstBracket = Helper.findIndexAt(selectedLine, "[", (int) x + 1);
		int lastBracket = Helper.findIndexAt(selectedLine, "]", (int) x + 1);

		StringBuffer buf = new StringBuffer(selectedLine);

		int start = firstBracket + 1;
		int end = lastBracket;

		if (start == -1 || end == -1)
			return map;

		// If were just replacing the tile
		if (replace) {
			buf.replace(start, end, id + "");

			selectedLine = buf.toString();
			map.mapLines.set((int) y, selectedLine);
		}

		// If were adding a tree or something..
		if (!replace) {
			String newIds = selectedLine.substring(start, lastBracket) + "," + id + "";
			if (!selectedLine.substring(start, lastBracket).contains(id + "")) {

				buf.replace(start, end, newIds);
				selectedLine = buf.toString();

				map.mapLines.set((int) y, selectedLine);
			}
		}

		return map;
	}

	public TileType getTileTypeFromLocation(Map map, double inputX, double inputY) {
		ArrayList<String> mapLines = map.mapLines;

		int x = ((int) Math.round(inputX)) / 32;
		int y = ((int) Math.round(inputY)) / 32;

		if (y >= mapLines.size() || y < 0)
			return null;

		String selectedLine = mapLines.get(y);

		int firstBracket = Helper.findIndexAt(selectedLine, "[", (int) (x) + 1);
		int lastBracket = Helper.findIndexAt(selectedLine, "]", (int) (x) + 1);

		// CHECK IF X IS OUT OF BOUNDS..
		if (firstBracket == -1 || lastBracket == -1)
			return null;

		String tileID = selectedLine.substring(firstBracket + 1, lastBracket);
		int id = -1;
		if (tileID.contains(",")) {
			String[] array = tileID.split(",", -1);
			id = Integer.parseInt(array[1]);

		} else
			id = Integer.parseInt(tileID);

		return TileType.getTileTypeFromID(id);
	}
}
