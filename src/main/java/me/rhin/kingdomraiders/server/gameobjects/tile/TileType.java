package me.rhin.kingdomraiders.server.gameobjects.tile;

public enum TileType {

	GRASS(false), //0
	GRASS2(false), //1
	FLOOR(false), //2
	WOODFLOOR(false), //3 
	PATH(false), 
	LIGHTPATH(false),

	WATER(true, false), 
	SAND(false),
	VOID(true),

	TREE(true), 
	BIGTREE(true, new TileCollider(-1, 0, 34, 30)), 
	BIGTREE2(true, new TileCollider(-1, 0, 34, 30)),
	STATUE(true, new TileCollider(0, 0, 64, 64)), 
	CHAIR(true), 
	TABLE(true), 
	BIRDBATH(true), 
	FOUNTIAN(true),
	BRUSH(false), 
	YELLOWFLOWER(false), 
	BLUEFLOWER(false),

	WALLBOTTOM(true), WALLBOTTOMLEFT(true), WALLBOTTOMRIGHT(true), WALLLEFT(true), WALLRIGHT(true), WALLTOP(true),
	WALLTOPLEFT(true), WALLTOPRIGHT(true), WALLFULL(true),

	WOODWALLBOTTOM(true), WOODWALLBOTTOMLEFT(true), WOODWALLBOTTOMEIGHT(true), WOODWALLTOPLEFT(true),
	WOODWALLTOPRIGHT(true), WOODWALLSIDE(true),
	
	DUNGEON_DEFAULT(true, new TileCollider(0, 0, 64, 64)),
	DUNGEON_HOMEWORLD(true, new TileCollider(0, 0, 64, 64)),

	ERROR(false);

	public boolean entityCollision, projectileCollision;
	public TileCollider tileCollider;

	TileType(boolean collision) {
		this.entityCollision = collision;
		this.projectileCollision = collision;
		this.tileCollider = new TileCollider(0, 0, 32, 32);
	}

	TileType(boolean entityCollision, boolean projectileCollision) {
		this.entityCollision = entityCollision;
		this.projectileCollision = projectileCollision;
		this.tileCollider = new TileCollider(0, 0, 32, 32);
	}

	TileType(boolean collision, TileCollider tileCollider) {
		this.entityCollision = collision;
		this.projectileCollision = collision;
		this.tileCollider = tileCollider;
	}

	public static TileType getTileTypeFromID(int id) {

		for (int i = 0; i < TileType.values().length; i++)
			if (i == id)
				return TileType.values()[i];

		return TileType.WATER;
	}

	// Nested Collider class for organization
	public static class TileCollider {
		public int x, y, w, h;

		public TileCollider(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
}
