package me.rhin.kingdomraiders.server.manager.map.entity.monster;

import java.util.ArrayList;

import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class MonsterManager {
	public ArrayList<Monster> monsters = new ArrayList<Monster>();
	public int monsterIdIndex = 0;

	public void spawnMonster(Monster monster) {
		monster.setID(++monsterIdIndex);
		monsters.add(monster);

		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers()) {
			jsonObj.put("type", "MonsterSpawn");
			jsonObj.put("name", monster.getName());
			jsonObj.put("monsterID", monster.getID());
			jsonObj.put("health", monster.stats.health);
			jsonObj.put("x", monster.getX());
			jsonObj.put("y", monster.getY());
			p.getConn().send(jsonObj.toString());
		}

	}

	public void sendMonsterTarget(Monster monster, Player nearestPlayer) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers()) {
			jsonObj.put("type", "MonsterTarget");
			// jsonObj.put("name", monster.getName());
			jsonObj.put("monsterID", monster.getID());
			jsonObj.put("targetPlayer", nearestPlayer.getID());
			p.getConn().send(jsonObj.toString());
		}

	}

	public void sendRemoveMonsterTarget(Monster monster) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers()) {
			jsonObj.put("type", "MonsterRemoveTarget");
			// jsonObj.put("name", monster.getName());
			jsonObj.put("monsterID", monster.getID());
			jsonObj.put("x", monster.getX());
			jsonObj.put("y", monster.getY());
			p.getConn().send(jsonObj.toString());
		}

	}

	public void sendMonsterSetHealth(Monster monster, int health) {
		// Should update every 100ms.
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers()) {
			jsonObj.put("type", "MonsterSetHealth");
			jsonObj.put("monsterID", monster.getID());
			jsonObj.put("health", health);
			p.getConn().send(jsonObj.toString());
		}
	}

	public void sendMonsterKill(Monster monster) {
		JSONObject jsonObj = new JSONObject();
		for (Player p : Main.getServer().getAllPlayers()) {
			jsonObj.put("type", "MonsterKill");
			jsonObj.put("monsterID", monster.getID());
			p.getConn().send(jsonObj.toString());
		}
	}

	public void removeMonster(Monster monster) {
		monsters.remove(monster);
	}

	public void removeAllMonsters() {
		monsters.clear();
	}
}
