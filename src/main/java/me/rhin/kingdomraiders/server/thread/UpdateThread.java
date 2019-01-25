package me.rhin.kingdomraiders.server.thread;

import java.util.Iterator;
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

		Runnable fastRunnable = new Runnable() {

			public void run() {
				try {
					UpdateThread.this.fastUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		ScheduledExecutorService fastExecutor = Executors.newScheduledThreadPool(1);
		fastExecutor.scheduleAtFixedRate(fastRunnable, 0, 1, TimeUnit.MILLISECONDS);

		Runnable slowRunnable = new Runnable() {

			public void run() {
				try {
					UpdateThread.this.slowUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		ScheduledExecutorService slowExecutor = Executors.newScheduledThreadPool(1);
		slowExecutor.scheduleAtFixedRate(slowRunnable, 0, 100, TimeUnit.MILLISECONDS);
	}

	public void update() {
		for (Iterator<Player> iterator = Main.getServer().getAllPlayers().iterator(); iterator.hasNext();) {
			Player p = iterator.next();
			p.update();
		}

		for (int i = 0; i < Main.getServer().getManager().getMonsterManager().monsters.size(); i++) {
			Monster m = Main.getServer().getManager().getMonsterManager().monsters.get(i);
			if (m.remove)
				Main.getServer().getManager().getMonsterManager().monsters.remove(i);
			else
				m.update();
		}

		// Concurrently calls our projectiles.
		for (int i = 0; i < Main.getServer().getManager().getProjectileManager().projectiles.size(); i++) {
			Projectile p = Main.getServer().getManager().getProjectileManager().projectiles.get(i);
			if (p.remove)
				Main.getServer().getManager().getProjectileManager().projectiles.remove(i);
			else
				p.update();
		}
	}

	// Called every 1ms
	public void fastUpdate() {
		for (Iterator<Player> iterator = Main.getServer().getAllPlayers().iterator(); iterator.hasNext();) {
			Player p = iterator.next();
			p.fastUpdate();
		}

		for (Iterator<Monster> iterator = Main.getServer().getManager().getMonsterManager().monsters
				.iterator(); iterator.hasNext();) {
			Monster m = iterator.next();
			m.fastUpdate();
		}
	}

	// Called every 333ms.
	public void slowUpdate() {
		for (Iterator<Player> iterator = Main.getServer().getAllPlayers().iterator(); iterator.hasNext();) {
			Player p = iterator.next();
			p.slowUpdate();
		}

		for (Iterator<Monster> iterator = Main.getServer().getManager().getMonsterManager().monsters
				.iterator(); iterator.hasNext();) {
			Monster m = iterator.next();
			m.slowUpdate();
		}
	}

}
