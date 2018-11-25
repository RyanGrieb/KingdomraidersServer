package me.rhin.kingdomraiders.server.gameobjects.entity.monster;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;

public class MonsterJson {

	private JSONObject jsonObj;

	public MonsterJson(String name) {
		File f = new File("./assets/config/monsterconfig/" + name + ".json");
		String fileData = null;
		try {
			fileData = new String(Files.readAllBytes(f.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		jsonObj = new JSONObject(fileData);
	}

	public int getSpeed() {
		return jsonObj.getInt("speed");
	}

	public int getHealth() {
		return jsonObj.getInt("health");
	}

	public int width() {
		return jsonObj.getInt("width");
	}

	public int height() {
		return jsonObj.getInt("height");
	}

}
