package me.rhin.kingdomraiders.server.manager.item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemJson {

	private JSONObject jsonObject;

	public ItemJson(int id) throws JSONException, IOException {
		File folder = new File("./assets/config/itemconfig/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				if (Integer.parseInt(name.substring(0, name.indexOf("-"))) == id)
					jsonObject = new JSONObject(new String(Files.readAllBytes(listOfFiles[i].toPath())));

			}

		System.out.println(jsonObject);
	}

	public String name() {
		return (String) jsonObject.get("name");
	}

	public float health() {
		return Float.parseFloat(this.getProjectileJson().getString("health"));
	}

	public float speed() {
		return Float.parseFloat(this.getProjectileJson().getString("speed"));
	}

	public int getProjID() {
		return this.getProjectileJson().getInt("projID");
	}

	public JSONObject getProjectileJson() {
		JSONObject projectileObject = jsonObject.getJSONObject("Projectile");
		return projectileObject;
	}

}
