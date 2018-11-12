package me.rhin.kingdomraiders.server.manager.item;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;

public class ItemManager {

	// TODO: should use item class
	public ItemJson getItemJson(int id) {
		ItemJson jProjectile = null;
		try {
			jProjectile = new ItemJson(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jProjectile;
	}

}