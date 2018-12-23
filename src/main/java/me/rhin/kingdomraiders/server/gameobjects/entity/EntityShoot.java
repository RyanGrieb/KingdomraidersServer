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
	private int delay;
	private int currentDelay;

	private double targetX, targetY;

	public EntityShoot() {

	}

	// Player shooting method.
	public void startShooting(Player player, JSONObject jsonObj) {
		int castItem = Integer.parseInt((String) player.profile.getInventory().get(18));

		this.entity = player;
		this.projectileJSON = Main.getServer().getManager().getItemManager().getItemJson(castItem).getProjectileJson();
		this.shooting = true;
		this.delay = this.convertDexToDelay(player.profile.getDex());
		this.currentDelay = this.convertDexToDelay(player.profile.getDex());
		this.targetX = jsonObj.getDouble("targetX");
		this.targetY = jsonObj.getDouble("targetY");
	}

	// Monster Shooting methoid.
	public void startShooting(Monster entity, Entity target) {
		this.entity = entity;
		this.projectileJSON = Main.getServer().getManager().getItemManager().getItemJson(1).getProjectileJson();
		this.shooting = true;
		this.delay = this.convertDexToDelay(entity.getMonsterJSON().getDex());
		this.currentDelay = 80;
		this.targetX = target.getX();
		this.targetY = target.getY();
	}

	public void setTarget(double x, double y) {
		this.targetX = x;
		this.targetY = y;
	}

	public void stopShooting() {
		this.shooting = false;
	}

	public void shootingUpdate() {
		if (this.currentDelay >= this.delay) {
			Projectile.fire(entity, this.projectileJSON, entity.getX(), entity.getY(), this.targetX, this.targetY);

			this.currentDelay = 0;
		}

		this.currentDelay++;
	}

	public int convertDexToDelay(int dex) {
		int delay = 100;
		return delay - dex;
	}

	public boolean isShooting() {
		return this.shooting;
	}

	public int getCurrentDelay() {
		return this.currentDelay;
	}

	public void update() {
		if (this.shooting)
			shootingUpdate();

	}
}
