package me.rhin.kingdomraiders.server.gameobjects.entity.dungeon;

import java.util.ArrayList;
import java.util.Random;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.manager.map.Map;

public class Dungeon {

	Map map;
	private String name;
	private double x, y;
	private int w, h;

	public Dungeon(String name, double x, double y) {
		super();

		this.name = name;
		this.x = x;
		this.y = y;
		this.w = 100;
		this.h = 100;

		this.generateTiles(4, this.w, this.h);
	}

	// TODO: should probally have a seperate dungeon thread.
	private void generateTiles(int floorID, int width, int height) {
		Random rnd = new Random();

		// Generate the floor
		ArrayList<String> mapLines = new ArrayList<String>();
		for (int y = 0; y < height; y++) {
			String mapLine = "";
			for (int x = 0; x < width; x++)
				mapLine += "[" + floorID + "]";
			mapLines.add(mapLine);
		}

		this.map = new Map("DUNGEON_DEFAULT", mapLines);

		// Generate the rooms
		int roomAmount = 10;
		int roomChanceY = 8; // (Higher == less common)
		int roomChanceX = 20;
		int roomWidth = 30;
		int roomHeight = 30;
		ArrayList<Room> rooms = new ArrayList<Room>();

		for (int i = 0; i < roomAmount; i++) {
			int rW = rnd.nextInt(roomWidth - 20) + 20;
			int rH = rnd.nextInt(roomHeight - 20) + 20;
			int x = rnd.nextInt(this.w - rW - 1) + 1;
			int y = rnd.nextInt(this.h - rH - 1) + 1;
			Room room = new Room(x, y, rW, rH);

			if (!room.isOverlapping(rooms, x, y)) {
				rooms.add(room);
				room.generate();
			} else
				room = null;
		}

		this.map.createDisplayWindow();
	}

	public Map getMap() {
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

	public class Room {
		public int x, y, w, h;

		public Room(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public void generate() {
			for (int y = 0; y < this.h; y++)
				for (int x = 0; x < this.w; x++)
					if (y == 0 || y == this.h - 1 || x == 0 || x == this.w - 1)
						Main.getServer().getManager().getMapManager().replaceTile(map, 5, this.x + x, this.y + y, true);
		}

		public boolean isOverlapping(ArrayList<Room> rooms, int x, int y) {
			for (Room r : rooms) {
				if (this.x <= (r.x + r.w) && (this.x + this.w) >= r.x)
					if (this.y <= (r.y + r.h) && (this.y + this.h) >= r.y)
						return true;
			}

			return false;
		}
	}

}
