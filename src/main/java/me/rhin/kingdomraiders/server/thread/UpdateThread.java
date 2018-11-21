package me.rhin.kingdomraiders.server.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.monster.Monster;
import me.rhin.kingdomraiders.server.entity.player.Player;

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
	}

}
