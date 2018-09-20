package me.rhin.kingdomraiders.server.manager.map;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;
import me.rhin.kingdomraiders.server.helper.Helper;

public class MapManager {

	private ArrayList<String> mapLines;
	private final int CHUNKSIZE = 15;

	public MapManager() {
		try {
			mapLines = new ArrayList<>(
					Files.readAllLines(Paths.get("./assets/map/gamemap.map"), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getChunkFromLocation(WebSocket conn, JSONObject jsonObj) {
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

				if (chunkX == 0)
					System.out.println((y + chunkY) + ":" + selectedLine + " ||| firstBracket: " + firstBracket
							+ ", lastbracket: " + lastBracket);

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
					if (chunkX == 0)
						System.out.print(Integer.parseInt(tileID) + " ");
					chunk[chunkY][chunkX] = Integer.parseInt(tileID);
					// Top layer is transparent...
					topLayerChunk[chunkY][chunkX] = -1;
				}
			}

		for (int chunkX = 0; chunkX < chunk.length; chunkX++) {
			for (int chunkY = 0; chunkY < chunk[chunkX].length; chunkY++)
				System.out.print(chunk[chunkX][chunkY]);

			System.out.println();
		}

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "ChunkRequest");
		jsonResponse.put("x", jsonObj.getInt("x"));
		jsonResponse.put("y", jsonObj.getInt("y"));
		jsonResponse.put("chunk", flipVertically(chunk));
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

}
