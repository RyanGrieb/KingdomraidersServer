package me.rhin.kingdomraiders.server.manager.map;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;
import me.rhin.kingdomraiders.server.helper.Helper;

public class MapUpdater {

	private Map mainMap;

	public MapUpdater(Map mainMap) {
		this.mainMap = mainMap;
		this.checkOutdatedTileTypes();
	}

	// Checks for outdated tiletypes in .map when we add new tile IDs.
	private void checkOutdatedTileTypes() {
		// this.modifyMapIDs(1, 0);

		File f = new File("./assets/map/tiletypes.json");
		String fileData = null;
		try {
			fileData = new String(Files.readAllBytes(f.toPath()));
		} catch (IOException e) {
		}

		JSONObject jsonObj = new JSONObject(fileData);

		JSONArray jsonTileTypes = jsonObj.getJSONArray("tileTypes");
		JSONArray newJsonTileTypes = new JSONArray(jsonTileTypes.toList());

		// Find out which tiles we added
		for (int i = 0; i < TileType.values().length; i++) {
			String serverTileType = TileType.values()[i].name();

			if (!this.JSONTypesContains(newJsonTileTypes, serverTileType)) {
				System.out.println("Added " + serverTileType);
				newJsonTileTypes = this.addToJSON(newJsonTileTypes, i, serverTileType);

				this.modifyMapIDs(1, i);
			}
		}

		// Find out which tiles we removed
		for (int i = 0; i < jsonTileTypes.length(); i++) {
			String jsonTileType = jsonTileTypes.getString(i);

			if (!this.ServerTypesContains(jsonTileType)) {
				System.out.println("Removed: " + jsonTileType);
				this.removeString(newJsonTileTypes, jsonTileType);

				this.modifyMapIDs(-1, i);
			}
		}

		jsonObj.put("tileTypes", newJsonTileTypes);

		// Update json file
		try (FileWriter file = new FileWriter("./assets/map/tiletypes.json")) {
			file.write(jsonObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void modifyMapIDs(int amount, int indexFrom) {
		// to (indexFrom)
		ArrayList<String> lines = this.mainMap.mapLines;
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			int widthAmount = StringUtils.countMatches(line, "[");

			int x = 0;
			for (int w = 1; w < widthAmount + 1; w++) {

				int firstBracket = Helper.findIndexAt(line, "[", w);
				int lastBracket = Helper.findIndexAt(line, "]", w);

				StringBuffer buf = new StringBuffer(line);

				int start = firstBracket + 1;
				int end = lastBracket;

				if (start == -1 || end == -1)
					return;

				String containerID = line.substring(start, end);

				// If there is only one tile
				if (!containerID.contains(",")) {

					int id = (Integer.parseInt(containerID));
					if (id >= indexFrom) {
						//Replace the removed tile with grass
						if (id == indexFrom && amount == -1)
							buf.replace(start, end, "0");
						else //If it's not that tile or were adding a tile, do it normally.
							buf.replace(start, end, ((id + amount) + ""));
						line = buf.toString();
						this.mainMap.mapLines.set((int) y, line);
					}

				} else {

					String[] ids = containerID.split(",");

					for (int i = 0; i < ids.length; i++) {
						int id = Integer.parseInt(ids[i]);

						if (id >= indexFrom) {
							id += amount;
							ids[i] = id + "";
						}

						// If were on our selected id & were removing it, set it to grass.
						if (id == indexFrom && amount == -1) {
							ids[i] = "0";
						}
					}

					buf.replace(start, end, ids[0] + "," + ids[1]);
					line = buf.toString();
					this.mainMap.mapLines.set((int) y, line);
				}

			}

			try {
				Files.write(Paths.get("assets/map/gamemap.map"), this.mainMap.mapLines, StandardCharsets.UTF_8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(line);
		}
	}

	// TODO: probally should move these helper methods somewhere else
	private boolean JSONTypesContains(JSONArray json, String name) {
		for (int i = 0; i < json.length(); i++)
			if (json.getString(i).equals(name))
				return true;

		return false;
	}

	private boolean ServerTypesContains(String name) {
		for (int i = 0; i < TileType.values().length; i++) {
			if (TileType.values()[i].name().equals(name))
				return true;
		}

		return false;
	}

	// (Adds values at indexes without removing them)
	private JSONArray addToJSON(JSONArray array, int i, String string) {
		// TODO Auto-generated method stub
		List<Object> list = array.toList();
		list.add(i, string);
		return new JSONArray(list);
	}

	private void removeString(JSONArray array, String string) {
		for (int i = 0; i < array.length(); i++) {
			if (array.getString(i).equals(string))
				array.remove(i);
		}
	}
}
