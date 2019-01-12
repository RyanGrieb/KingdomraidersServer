package me.rhin.kingdomraiders.server.gameobjects.entity.dungeon;

import java.util.ArrayList;
import java.util.Random;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;
import me.rhin.kingdomraiders.server.helper.Helper;
import me.rhin.kingdomraiders.server.manager.map.Map;

//Leaf Generation Credit: Timothy Hely @ 
//https://gamedevelopment.tutsplus.com/tutorials/how-to-use-bsp-trees-to-generate-game-maps--gamedev-12268
//https://eskerda.com/bsp-dungeon-generation/

public class Dungeon {

	private Map portalLocationMap;
	private Map map;
	private String name;
	private double x, y, spawnX, spawnY;
	private int w, h;

	private double widthRatio = 0.45;
	private double heightRatio = 0.45;

	public Dungeon(String name, Map portalLocationMap, double x, double y) {
		this.portalLocationMap = portalLocationMap;

		this.name = name;
		this.x = x;
		this.y = y;
		this.spawnX = -1;
		this.spawnY = -1;
		this.w = 150;
		this.h = 150;

		this.generateTiles(TileType.getIDFromName("VOID"), this.w, this.h);
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

		int numIterations = 4; // 3
		Container mainContainer = new Container(0, 0, this.w, this.h);
		Leaf rootLeaf = this.splitContainer(mainContainer, numIterations);
		this.map.rootLeaf = rootLeaf;

		// Create paths
		this.drawPaths(rootLeaf);

		// Create rooms
		for (Leaf l : rootLeaf.getLeafs())
			l.room = new Rectangle(l.container);

		// Place the player in a random room
		ArrayList<Leaf> leafs = new ArrayList<Leaf>();
		leafs = rootLeaf.getLeafs();
		int randomNum = rnd.nextInt(leafs.size());
		Leaf spawnLeaf = leafs.get(randomNum);
		this.spawnX = spawnLeaf.room.centerX * 32;
		this.spawnY = spawnLeaf.room.centerY * 32;

		// Place homeworld portal behind the player
		Main.getServer().getManager().getMapManager().replaceTile(map, TileType.getIDFromName("DUNGEON_HOMEWORLD"),
				spawnLeaf.room.centerX, (spawnLeaf.room.centerY + 3), false);

		this.map.createDisplayWindow();
	}

	private Leaf splitContainer(Container mainContainer, int iterations) {
		Leaf root = new Leaf(mainContainer);

		if (iterations != 0) {
			Container[] sr = this.randomSplit(mainContainer);
			root.leftChild = this.splitContainer(sr[0], iterations - 1);
			root.rightChild = this.splitContainer(sr[1], iterations - 1);
		}

		return root;
	}

	private Container[] randomSplit(Container container) {
		Container[] containers = new Container[2];
		Container r1 = null;
		Container r2 = null;

		if (Helper.RandomNum(0, 2) == 0) {
			// Vertical
			r1 = new Container(container.x, container.y, Helper.RandomNum(1, container.w), container.h);
			r2 = new Container(container.x + r1.w, container.y, container.w - r1.w, container.h);

			// Ratio splitting
			double r1WidthRatio = (double) r1.w / (double) r1.h;
			double r2WidthRatio = (double) r2.w / (double) r2.h;

			if (r1WidthRatio < this.widthRatio || r2WidthRatio < this.widthRatio)
				return this.randomSplit(container);

		} else {
			// Horizontal
			r1 = new Container(container.x, container.y, container.w, Helper.RandomNum(1, container.h));
			r2 = new Container(container.x, container.y + r1.h, container.w, container.h - r1.h);

			// Ratio Splitting
			double r1HeightRatio = (double) r1.h / (double) r1.w;
			double r2HeightRatio = (double) r2.h / (double) r2.w;

			// System.out.println(r1.w);
			if (r1HeightRatio < this.heightRatio || r2HeightRatio < this.heightRatio)
				return this.randomSplit(container);
		}

		containers[0] = r1;
		containers[1] = r2;

		return containers;
	}

	private void drawPaths(Leaf leaf) {
		if (leaf.leftChild == null || leaf.rightChild == null)
			return;

		leaf.leftChild.container.createPath(leaf.rightChild.container);
		this.drawPaths(leaf.leftChild);
		this.drawPaths(leaf.rightChild);
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
		return this.spawnX;
	}

	public double ySpawn() {
		return this.spawnY;
	}

	public Map getPortalMap() {
		return this.portalLocationMap;
	}

	public class Leaf {

		private Random rnd;

		public Container container;
		public Rectangle room;
		public Leaf leftChild, rightChild;

		public Leaf(Container container) {
			rnd = new Random();
			this.container = container;
			this.leftChild = null;
			this.rightChild = null;
		}

		public ArrayList<Leaf> getLeafs() {
			ArrayList<Leaf> leafs = new ArrayList<Leaf>();
			if (this.leftChild == null && this.rightChild == null) {
				leafs.add(this);
				return leafs;
			}
			ArrayList<Leaf> leftChildLeafs = this.leftChild.getLeafs();
			ArrayList<Leaf> rightChildLeafs = this.rightChild.getLeafs();

			ArrayList<Leaf> combinedLeafs = new ArrayList<Leaf>();
			combinedLeafs.addAll(leftChildLeafs);
			combinedLeafs.addAll(rightChildLeafs);
			return combinedLeafs;
		}
	}

	public class Container {
		public int x, y, w, h;
		public int centerX, centerY;

		public Container(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;

			this.centerX = (this.x + this.w / 2);
			this.centerY = (this.y + this.h / 2);
		}

		public void createPath(Container container) {
			// First determine if it's horizontal line or vertial

			double startX = this.centerX;
			double startY = this.centerY;
			double targetX = container.centerX;
			double targetY = container.centerY;

			while (startX < targetX || startY < targetY) {
				double distanceX = (container.centerX - this.centerX);
				double distanceY = (container.centerY - this.centerY);

				double hypotnuse = Math.sqrt(((distanceX * distanceX) + (distanceY * distanceY)));

				distanceX = (distanceX / hypotnuse);
				distanceY = (distanceY / hypotnuse);

				// Start the line at THIS.CENTER
				// THEN MOVE line to CONTAINER.CENTER
				startX += distanceX;
				startY += distanceY;

				// Include thinkness of our corridors
				for (int i = 0; i < 3; i++) {
					int xThinkness = (startY == targetY) ? 0 : i;
					int yThinkness = (startX == targetX) ? 0 : i;
					Main.getServer().getManager().getMapManager().replaceTile(map, TileType.getIDFromName("PATH"),
							(int) (startX + xThinkness), (int) (startY + yThinkness), true);
				}
			}
		}
	}

	public class Rectangle {
		public int x, y, w, h;
		public int centerX, centerY;

		public Rectangle(Container container) {

			this.createRandomRectalge(container);
			this.drawTiles();

			this.centerX = (this.x + this.w / 2);
			this.centerY = (this.y + this.h / 2);
		}

		public Rectangle(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;

			this.centerX = (this.x + this.w / 2);
			this.centerY = (this.y + this.h / 2);
		}

		private void createRandomRectalge(Container container) {
			// TODO: add a check to see if the rectangle is inside the center of the
			// container
			// if not recursivley re-attempt.

			try {
				this.x = container.x + Helper.RandomNum(1, (int) Math.floor(container.w / 3) - 1);
				this.y = container.y + Helper.RandomNum(1, (int) Math.floor(container.h / 3) - 1);
			} catch (IllegalArgumentException e) { // Just doesn't create the room instead.
				return;
			}
			this.w = container.w - (this.x - container.x) - 1;
			this.h = container.h - (this.y - container.y) - 1;

			this.w -= Helper.RandomNum(0, this.w / 3); // Try not to make this so extreme.!!!!
			this.h -= Helper.RandomNum(0, this.w / 3);

			// If the point lies inside the center of container

			// Bottom left corner
			int x1 = this.x;
			int y1 = this.y;
			// Top right corner
			int x2 = (this.x + this.w);
			int y2 = (this.y + this.h);

			boolean isCentered = false;
			// If the rectangle is inside the container, were peachy!
			if (container.centerX > x1 && container.centerX < x2 && container.centerY > y1 && container.centerY < y2)
				isCentered = true;

			// If not, recursivley try again
			if (!isCentered)
				this.createRandomRectalge(container);
		}

		private void drawTiles() {
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++)
					Main.getServer().getManager().getMapManager().replaceTile(map, TileType.getIDFromName("PATH"),
							this.x + x, this.y + y, true);
		}
	}

}
