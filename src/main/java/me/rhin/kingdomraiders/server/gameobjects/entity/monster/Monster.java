package me.rhin.kingdomraiders.server.gameobjects.entity.monster;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.Entity;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class Monster extends Entity {

	private String name;
	private MonsterJson monsterJson;

	private Player targetPlayer;
	private int monsterID;

	private Timer threadTimer;

	public Monster(String name, double x, double y) {
		super();

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

	private void trackNearestPlayer() {

		// Get the nearest player in game.
		Player nearestPlayer = null;
		for (Player p : Main.getServer().getAllPlayers()) {

			if (nearestPlayer == null)
				nearestPlayer = p;

			double playerDistance = Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
			double nearestDistance = Math.sqrt(
					(nearestPlayer.x - x) * (nearestPlayer.x - x) + (nearestPlayer.y - y) * (nearestPlayer.y - y));

			// If this player's distance is less than nearest dittance, then go ahead and
			// change the target.
			if (playerDistance < nearestDistance)
				nearestPlayer = p;
		}
		if (nearestPlayer == null) {
			this.targetPlayer = null;
			this.entityShoot().stopShooting();
			return;
		}

		double distanceX = (nearestPlayer.getX() - this.getX()) - this.w / 2;
		double distanceY = (nearestPlayer.getY() - this.getY()) - this.h / 2;
		double hypotnuse = Math.sqrt(((distanceX * distanceX) + (distanceY * distanceY)));

		// If invalid hypotnuse, stop...
		if (hypotnuse == 0)
			return;

		distanceX = (distanceX / hypotnuse);
		distanceY = (distanceY / hypotnuse);
		double velX = Math.round(distanceX * this.stats.speed);
		double velY = Math.round(distanceY * this.stats.speed);

		// If monster is too far, stop following. 300
		if (hypotnuse > 300 || this.isTileCollided(velX, velY)) {
			if (this.targetPlayer != null) {
				this.targetPlayer = null;
				this.entityShoot().stopShooting();

				Main.getServer().getManager().getMonsterManager().sendRemoveMonsterTarget(this);
				// Cancel & qued target packets we want to send.
				this.threadTimer.cancel();
				this.threadTimer.purge();
			}
			return;
		}

		// Initalizes our targetPlayer every time we change our our nearest player.
		if (!nearestPlayer.equals(this.targetPlayer))
			this.updatePlayerTarget(nearestPlayer);

		if (hypotnuse < -this.stats.speed || hypotnuse > this.stats.speed) {

			this.setPosition(this.getX() + velX, this.getY() + velY);
		}
	}

	public void updatePlayerTarget(Player nearestPlayer) {
		// Variables for the delayed thread.
		Monster thisMonster = this;
		Player thisPlayer = nearestPlayer;

		TimerTask delayedThreadStartTask = new TimerTask() {

			@Override
			public void run() {

				new Thread(new Runnable() {
					@Override
					public void run() {
						Main.getServer().getManager().getMonsterManager().sendMonsterTarget(thisMonster, thisPlayer);
					}
				}).start();
			}
		};
		threadTimer = new Timer();
		threadTimer.schedule(delayedThreadStartTask, 1);
		// Main.getServer().getManager().getMonsterManager().sendMonsterTarget(this,
		// nearestPlayer);
		this.targetPlayer = nearestPlayer;
	}

	public void shootNearestPlayer() {
		if (this.targetPlayer != null) {
			if (!this.entityShoot().isShooting()) {
				this.entityShoot().startShooting(this, this.targetPlayer);

				// Say that were shooting to all players.
				Main.getServer().getManager().getProjectileManager().monsterStartShooting(this);
			}

			this.entityShoot().setTarget(this.targetPlayer.getX() + this.targetPlayer.getWidth() / 2,
					this.targetPlayer.getY() + this.targetPlayer.getHeight() / 2);
		}
	}

	public void damage(int damage) {
		this.stats.health -= damage;
		if (this.stats.health <= 0) {
			Main.getServer().getManager().getMonsterManager().sendMonsterKill(this);
			Main.getServer().getManager().getMonsterManager().removeMonster(this);
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

	public void update() {
		super.update();

		this.trackNearestPlayer();
		this.shootNearestPlayer();

		// if (this.targetPlayer != null)
		// System.out.println(this.targetPlayer.x + "," + this.targetPlayer.y);
	}

	public void slowUpdate() {
		// TODO Auto-generated method stub
	}

}
