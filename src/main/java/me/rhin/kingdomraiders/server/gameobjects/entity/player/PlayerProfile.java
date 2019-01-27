package me.rhin.kingdomraiders.server.gameobjects.entity.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlayerProfile {

	private JSONObject jsonObj;
	private String name;

	public PlayerProfile(String name) {
		this.name = name;

		try {
			initProfile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initProfile() throws IOException {
		File f = new File("./assets/profiles/" + name + ".profile");

		// If a profile already exists, init json from exisitng file.
		if (f.exists()) {
			String fileData = new String(Files.readAllBytes(f.toPath()));
			jsonObj = new JSONObject(fileData);
			return;
		}
		// If not, create a new profile
		jsonObj = new JSONObject();

		jsonObj.put("username", name);

		// Add our base stats
		JSONObject statsJson = new JSONObject();
		statsJson.put("attackdelay", 500);
		statsJson.put("speed", 5);
		statsJson.put("vitality", 1);
		statsJson.put("health", 100);
		statsJson.put("mana", 100);
		jsonObj.put("stats", statsJson);

		updateJSONFile();
	}

	public void setEmail(String email) {
		jsonObj.put("email", email);
		updateJSONFile();
	}

	public void setSalt(String salt) {
		jsonObj.put("salt", salt);
		updateJSONFile();
	}

	public void setPassword(String password) {
		jsonObj.put("password", password);
		updateJSONFile();
	}

	public void setInventory(String[] inventory) {
		JSONArray jsonArrayInv = new JSONArray(inventory);

		jsonObj.put("inventory", jsonArrayInv);
		updateJSONFile();
	}

	public void updateJSONFile() {
		try (FileWriter file = new FileWriter("./assets/profiles/" + name + ".profile")) {
			file.write(jsonObj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] getSalt() {
		String encodedString = (String) jsonObj.get("salt");
		byte[] salt = java.util.Base64.getDecoder().decode(encodedString);
		return salt;
	}

	public byte[] getHashedPassword() {
		String encodedString = (String) jsonObj.get("password");
		byte[] salt = java.util.Base64.getDecoder().decode(encodedString);
		return salt;
	}

	public JSONArray getInventory() {
		return jsonObj.getJSONArray("inventory");
	}

	public JSONObject getStats() {
		return jsonObj.getJSONObject("stats");
	}

	public String getName() {
		return name;
	}

	public int getAttackDelay() {
		return this.getStats().getInt("attackdelay");
	}

	public int getSpeed() {
		return this.getStats().getInt("speed");
	}
	
	public int getVitality() {
		return this.getStats().getInt("vitality");
	}

	public int getHealth() {
		return this.getStats().getInt("health");
	}

	public int getMana() {
		return this.getStats().getInt("mana");
	}

	public int getDamage() {
		return 1;
	}

}
