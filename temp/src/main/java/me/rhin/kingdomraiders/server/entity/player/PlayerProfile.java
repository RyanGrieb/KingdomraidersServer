package me.rhin.kingdomraiders.server.entity.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

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

}
