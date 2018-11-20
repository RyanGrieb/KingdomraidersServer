package me.rhin.kingdomraiders.server.entity.monster;

import java.util.Timer;
import java.util.TimerTask;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.Entity;
import me.rhin.kingdomraiders.server.entity.player.Player;

public class Monster extends Entity {

	private String name;
	private MonsterJson monsterJson;
	private int speed;

	private Player targetPlayer;
	private int monsterID;

	private Timer threadTimer = new Timer();

	public Monster(String name, double x, double y) {
		super();

		this.name = name;
		this.monsterJson = new MonsterJson(name);
		this.setBounds(x, y, this.monsterJson.width(), this.monsterJson.height());
		this.speed = monsterJson.speed();
	}

	public String getName() {
		return this.name;
	}

	private void trackNearestPlayer() {
		// Maybe need to make this more effiecient.

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
			return;
		}

		double distanceX = (nearestPlayer.getX() - this.getX()) - this.w / 2;
		double distanceY = (nearestPlayer.getY() - this.getY()) - this.h / 2;
		double hypotnuse = Math.sqrt(((distanceX * distanceX) + (distanceY * distanceY)));

		// If monster is too far, stop following.
		// !!!!!!!!!!!!!!! SEND STOP
		// PACKET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if (hypotnuse > 300) {
			if (this.targetPlayer != null) {
				this.targetPlayer = null;
				Main.getServer().getManager().getMonsterManager().sendRemoveMonsterTarget(this);
			}
			return;
		}

		// If the monster is too close, stop!!!.
		// if (hypotnuse < 200)
		// return;

		// Initalizes our targetPlayer every time we change our our nearest player.

		// error is target player equals nearest player
		if (!nearestPlayer.equals(this.targetPlayer)) {

			// Variables for the delayed thread.
			Monster thisMonster = this;
			Player thisPlayer = nearestPlayer;
			TimerTask delayedThreadStartTask = new TimerTask() {

				@Override
				public void run() {

					new Thread(new Runnable() {
						@Override
						public void run() {
							Main.getServer().getManager().getMonsterManager().sendMonsterTarget(thisMonster,
									thisPlayer);
						}
					}).start();
				}
			};

			threadTimer.schedule(delayedThreadStartTask, 100);
			// Main.getServer().getManager().getMonsterManager().sendMonsterTarget(this,
			// nearestPlayer);
			this.targetPlayer = nearestPlayer;
		}

		// If invalid hypotnuse, stop...
		if (hypotnuse == 0)
			return;

		distanceX = (distanceX / hypotnuse);
		distanceY = (distanceY / hypotnuse);

		if (hypotnuse < -speed || hypotnuse > speed) {
			double velX = Math.round(distanceX * speed);
			double velY = Math.round(distanceY * speed);

			this.setPosition(this.getX() + velX, this.getY() + velY);
		}

		// System.out.println(this.getX() + "," + this.getY());
		// System.out.println("target: " + nearestPlayer.profile.getName());

	}

	public void setID(int monsterID) {
		this.monsterID = monsterID;
	}

	public int getID() {
		return this.monsterID;
	}

	public void update() {
		super.update();

		this.trackNearestPlayer();

		// System.out.println(this.targetPlayer);
		// System.out.println(this.getX() + "," + this.getY());
	}

}
