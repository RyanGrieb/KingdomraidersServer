package me.rhin.kingdomraiders.server.entity;

public class Entity {

	public double x, y;
	public int w, h;

	private EntityShoot entityShoot;

	public Entity() {
		this.entityShoot = new EntityShoot();
	}

	public void setBounds(double x, double y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public EntityShoot entityShoot() {
		return this.entityShoot;
	}

	public void update() {
		this.entityShoot.update();
	}
}
