package me.rhin.kingdomraiders.server.gameobjects.entity;

public class EntityStats {

	private long prevTime;

	public int speed;
	public int duration;
	public int damage;
	public int vitality;
	public int health;
	public int maxHealth;

	public EntityStats() {
		this.prevTime = System.currentTimeMillis();
	}

	public void regenHealth() {
		long currentTime = System.currentTimeMillis();

		if (currentTime - this.prevTime > 666) {
			this.health += this.vitality;
			if (this.health > this.maxHealth)
				this.health = this.maxHealth;

			this.prevTime = currentTime;
		}
	}

	public void update() {
		this.regenHealth();
	}
}
