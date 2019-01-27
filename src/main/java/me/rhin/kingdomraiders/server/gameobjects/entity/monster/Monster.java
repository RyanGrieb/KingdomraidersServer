package me.rhin.kingdomraiders.server.gameobjects.entity.monster;

import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.manager.map.Map;

public class Monster extends Entity {

	private String name;

	private MonsterJson monsterJson;

	private Player targetPlayer;
	private double targetX, targetY;

	private int monsterID;

	public boolean remove;

	public Monster(Map map, String name, double x, double y) {
		super(map);

		this.name = name;
		this.monsterJson = new MonsterJson(name);
		this.setBounds(x, y, this.monsterJson.width(), this.monsterJson.height());
		this.setCollision(0, 0, this.w, this.h);

		this.stats.speed = monsterJson.getSpeed();
		this.stats.health = monsterJson.getHealth();

	}

	public String getName() {
		return this.name;
	}

	private void setNearestTarget() {

		// Get the nearest player in game.
		Player nearestPlayer = null;
		for (Player p : Main.getServer().getAllPlayers(this.currentMap)) {

			if (p.isDead())
				continue;

			if (nearestPlayer == null)
				nearestPlayer = p;

			// Gets the nearest player on the server.
			double playerDistance = Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
			double nearestDistance = Math.sqrt(
					(nearestPlayer.x - x) * (nearestPlayer.x - x) + (nearestPlayer.y - y) * (nearestPlayer.y - y));

			// If this player's distance is less than nearest dittance, then go ahead and
			// change the target.
			if (playerDistance < nearestDistance)
				nearestPlayer = p;
		}

		this.targetPlayer = nearestPlayer;

		if (this.targetPlayer == null || this.targetPlayer.isDead()) {
			this.entityShoot().stopShooting();
			return;
		}

		this.targetX = this.targetPlayer.getX();
		this.targetY = this.targetPlayer.getY();
	}

	private void trackTarget() {

		// Gives us a velocity to the player.
		double distanceX = (this.targetX - this.getX()) - this.w / 2;
		double distanceY = (this.targetY - this.getY()) - this.h / 2;
		double hypotnuse = Math.sqrt(((distanceX * distanceX) + (distanceY * distanceY)));

		// If invalid hypotnuse, stop...
		if (hypotnuse == 0)
			return;

		distanceX = (distanceX / hypotnuse);
		distanceY = (distanceY / hypotnuse);
		double velX = distanceX * this.stats.speed;
		double velY = distanceY * this.stats.speed;

		// If player is too far, or collided, stop following.
		if (hypotnuse > 550 || this.isTileCollided(velX, velY)) {
			if (this.targetPlayer != null) {
				this.targetPlayer = null;

				if (hypotnuse > 550)
					this.entityShoot().stopShooting();
			}
			return;
		}

		if (hypotnuse < -this.stats.speed || hypotnuse > this.stats.speed) {

			this.setPosition(this.getX() + velX, this.getY() + velY);
		}

	}

	public void updateShootingTarget() {
		// Updates our target..
		this.entityShoot().setTarget(this.targetX, this.targetY);
	}

	public void shootNearestPlayer() {
		if (this.targetPlayer != null) {
			if (!this.entityShoot().isShooting()) {
				// Say that were shooting to all players.
				// Main.getServer().getManager().getProjectileManager().monsterStartShooting(this);
				this.entityShoot().startShooting(this, this.targetPlayer);
			}
		}
	}

	public void sendMonsterPosition() {
		if (this.targetPlayer != null)
			Main.getServer().getManager().getMonsterManager().sendMonsterPosition(this, this.getX(), this.getY());
	}

	public void damage(int damage) {
		this.stats.health -= damage;
		if (this.stats.health <= 0) {
			Main.getServer().getManager().getMonsterManager().sendMonsterKill(this);
			this.remove = true;
			return;
		}
		Main.getServer().getManager().getMonsterManager().sendMonsterSetHealth(this, this.stats.health);
	}

	public void setID(int monsterID) {
		this.monsterID = monsterID;
	}

	public int getID() {
		return this.monsterID;
	}

	public Player getTargetPlayer() {
		return this.targetPlayer;
	}

	public MonsterJson getMonsterJSON() {
		return this.monsterJson;
	}

	public JSONObject getCastProjectile() {
		return this.monsterJson.getProjectile();
	}

	public void update() {
		super.update();
		this.trackTarget();
		this.updateShootingTarget();
		// this.shootNearestPlayer();
		// if (this.targetPlayer != null)
		// System.out.println(this.entityShoot().targetX + "," +
		// this.entityShoot().targetY);
	}

	public void slowUpdate() {
		super.slowUpdate();
		// TODO Auto-generated method stub
		this.shootNearestPlayer();
		this.setNearestTarget();
		this.sendMonsterPosition();
	}

}
