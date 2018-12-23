package me.rhin.kingdomraiders.server.gameobjects.entity.dungeon;

import java.util.ArrayList;
import java.util.Collection;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;

public class Dungeon {

	ArrayList<String> map = new ArrayList<String>();

	private String name;
	private double x, y;

	public Dungeon(String name, double x, double y) {
		super();

		this.name = name;
		this.x = x;
		this.y = y;

		// Create tileupdate.
		this.generateTiles();
	}

	private void generateTiles() {
		for (int y = 0; y < 10; y++) {
			String mapLine = "";
			for (int x = 0; x < 10; x++)
				mapLine += "[0]";
			map.add(mapLine);
		}

	}

	public ArrayList<String> getMap() {
		return this.map;
	}

	public String getName() {
		return this.name;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double xSpawn() {
		return 0;
	}

	public double ySpawn() {
		return 0;
	}

}
