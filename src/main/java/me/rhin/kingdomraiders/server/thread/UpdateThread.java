package me.rhin.kingdomraiders.server.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;

public class UpdateThread {

	public UpdateThread() {
		Runnable runnable = new Runnable() {

			public void run() {
				try {
					UpdateThread.this.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(runnable, 0, 16660, TimeUnit.MICROSECONDS);
	}

	public void update() {
		for (Player p : Main.getServer().getAllPlayers())
			p.update();

		for (Monster m : Main.getServer().getManager().getMonsterManager().monsters)
			m.update();

		// Concurrently calls our projectiles.
		for (int i = 0; i < Main.getServer().getManager().getProjectileManager().projectiles.size(); i++) {
			Projectile p = Main.getServer().getManager().getProjectileManager().projectiles.get(i);
			if (p.remove)
				Main.getServer().getManager().getProjectileManager().projectiles.remove(i);
			else
				p.update();
		}
	}

}
