package me.rhin.kingdomraiders.server.gameobjects.tile;

public enum TileType {

	GRASS(false), 
	GRASS2(false), 
	FLOOR(false), 
	WOODFLOOR(false), 
	PATH(false), 
	LIGHTPATH(false),

	WATER(true, false), 
	SAND(false),

	TREE(true), 
	BIGTREE(true, 128, 128, new TileCollider(-24, -26, 32, 26)),
	BIGTREE2(true, 128, 128, new TileCollider(-24, -26, 32, 26)),
	STATUE(true, 64,64, new TileCollider(-30, -32 , 47, 47)),
	CHAIR(true),
	TABLE(true),
	BIRDBATH(true),
	FOUNTIAN(true),
	BRUSH(false),
	YELLOWFLOWER(false),
	BLUEFLOWER(false),
	
	WALLBOTTOM(true),
	WALLBOTTOMLEFT(true),
	WALLBOTTOMRIGHT(true),
	WALLLEFT(true),
	WALLRIGHT(true),
	WALLTOP(true),
	WALLTOPLEFT(true),
	WALLTOPRIGHT(true),
	WALLFULL(true),
	
	WOODWALLBOTTOM(true),
	WOODWALLBOTTOMLEFT(true),
	WOODWALLBOTTOMEIGHT(true),
	WOODWALLTOPLEFT(true),
	WOODWALLTOPRIGHT(true),
	WOODWALLSIDE(true),
	
	ERROR(false);
	

	public boolean entityCollision, projectileCollision;
	public TileCollider tileCollider;
	public int w, h;

	TileType(boolean collision) {
		this.entityCollision = collision;
		this.projectileCollision = collision;
	}

	TileType(boolean entityCollision, boolean projectileCollision) {
		this.entityCollision = entityCollision;
		this.projectileCollision = projectileCollision;
	}

	TileType(boolean collision, int w, int h, TileCollider tileCollider) {
		this.entityCollision = collision;
		this.projectileCollision = collision;
		this.tileCollider = tileCollider;
		this.w = w;
		this.h = h;
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
