package me.rhin.kingdomraiders.server.gameobjects.entity;

import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;

public class EntityShoot {
	private Entity entity;
	private JSONObject projectileJSON;
	private boolean shooting;
	public boolean targetUpdated;
	private int attackDelay;
	public long prevTime;

	public double targetX, targetY;

	public EntityShoot() {

	}

	// Player shooting method.
	public void startShooting(Player player, JSONObject jsonObj) {
		int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));

		this.entity = player;
		this.projectileJSON = Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjectileJson();
		this.shooting = true;
		this.attackDelay = player.profile.getAttackDelay();
		// this.prevTime = (System.currentTimeMillis() + (System.currentTimeMillis() -
		// jsonObj.getLong("time")));
		this.prevTime = jsonObj.getLong("time");
		// System.out.println("latency: " + (System.currentTimeMillis() -
		// jsonObj.getLong("time")));
		// this.currentDelay = this.convertDexToDelay(player.profile.getDex());
		this.targetX = jsonObj.getDouble("targetX");
		this.targetY = jsonObj.getDouble("targetY");
	}

	// Monster Shooting methoid.
	public void startShooting(Monster entity, Entity target) {
		this.entity = entity;
		this.projectileJSON = Main.getServer().getManager().getItemManager().getItemJson(1).getProjectileJson();
		this.shooting = true;
		this.attackDelay = entity.getMonsterJSON().getAttackDelay();
		this.prevTime = System.currentTimeMillis();
		// this.currentDelay = 80;
		this.targetX = target.getX();
		this.targetY = target.getY();
	}

	public void setTarget(double x, double y) {
		if (x == this.targetX && y == this.targetY)
			return;

		this.targetX = x;
		this.targetY = y;
		this.targetUpdated = true;
	}

	public void stopShooting() {
		this.shooting = false;
	}

	public void shootingUpdate() {
		if ((System.currentTimeMillis() - this.prevTime) >= this.attackDelay) {

			double originX = (entity.getX() + (entity.getWidth() / 2)) - 16;
			double originY = (entity.getY() + (entity.getHeight() / 2)) - 16;

			// Send a packet for each projectile

			if (this.entity instanceof Player) {
				Player player = (Player) entity;
				int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));

				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("type", "ProjShot");
				jsonResponse.put("id", player.getID());
				jsonResponse.put("entityType", "Player");
				jsonResponse.put("projID",
						Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjID());
				jsonResponse.put("targetX", this.targetX);
				jsonResponse.put("targetY", this.targetY);
				Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());

				jsonResponse = null;
			}

			if (this.entity instanceof Monster) {
				Monster monster = (Monster) entity;
				JSONObject jsonResponse = new JSONObject();
				for (Player p : Main.getServer().getAllPlayers(monster.currentMap)) {
					jsonResponse.put("type", "ProjShot");
					jsonResponse.put("id", monster.getID());
					jsonResponse.put("entityType", "Monster");
					jsonResponse.put("originX", originX);
					jsonResponse.put("originY", originY);
					jsonResponse.put("targetX", this.targetX);
					jsonResponse.put("targetY", this.targetY);
					p.getConn().send(jsonResponse.toString());
				}

				jsonResponse = null;
			}
			// ..

			// Top portion is our monsters which can have multiple projectiles
			// I just seperate for accesability.
			if (this.entity instanceof Monster) {
				Monster monster = (Monster) entity;
				double minDeg = -((monster.getMonsterJSON().getProjAmount() - 1)
						* monster.getMonsterJSON().getProjAngle()) / 2;
				double maxDeg = ((monster.getMonsterJSON().getProjAmount() - 1)
						* monster.getMonsterJSON().getProjAngle()) / 2;

				for (double degAngle = minDeg; degAngle <= maxDeg; degAngle += monster.getMonsterJSON()
						.getProjAngle()) {
					double radians = (Math.PI / 180) * (degAngle);
					double cos = Math.cos(radians);
					double sin = Math.sin(radians);
					double targetXOffset = originX + (cos * (this.targetX - originX) + sin * (this.targetY - originY));
					double targetYOffset = originY + (-sin * (this.targetX - originX) + cos * (this.targetY - originY));

					Projectile.fire(entity, this.projectileJSON, originX, originY, targetXOffset, targetYOffset);
				}
			} else
				Projectile.fire(entity, this.projectileJSON, originX, originY, this.targetX, this.targetY);

			this.prevTime = System.currentTimeMillis();
		}
	}

	public int convertDexToDelay(int dex) {
		int delay = 100;
		return delay - dex;
	}

	public boolean isShooting() {
		return this.shooting;
	}

	public int getCurrentDelay() {
		return 0;
	}

	public void update() {
		
	}

	public void fastUpdate() {
		if (this.shooting)
			this.shootingUpdate();
	}
}