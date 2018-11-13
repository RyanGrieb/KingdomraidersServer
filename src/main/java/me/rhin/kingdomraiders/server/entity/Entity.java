package me.rhin.kingdomraiders.server.entity;

public class Entity {

	public float x, y;

	private EntityShoot entityShoot;

	public Entity() {
		this.entityShoot = new EntityShoot();
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public EntityShoot entityShoot() {
		return this.entityShoot;
	}

	public void update() {
		this.entityShoot.update();
	}
}
