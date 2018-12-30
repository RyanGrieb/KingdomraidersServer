package me.rhin.kingdomraiders.server.manager.player.commands;

import org.apache.commons.lang3.StringUtils;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;
import me.rhin.kingdomraiders.server.manager.map.Map;
import me.rhin.kingdomraiders.server.manager.map.MapManager;

public class CommandManager {

	// LOOK: THE .INDEXOF(" ") USE KEYCODE ALT+255
	public void recieveCommand(Player player, String command) {
		command = command.substring(1, command.length());

		String firstParamater = command;

		if (command.indexOf(" ") != -1)
			firstParamater = command.substring(0, command.indexOf(" "));

		// Second parameter.
		int secondEndIndex = (StringUtils.ordinalIndexOf(command, " ", 2)) == -1 ? command.length()
				: (StringUtils.ordinalIndexOf(command, " ", 2));
		String secondParamater = command.substring(command.indexOf(" ") + 1, secondEndIndex);

		// Make everything lowercase
		firstParamater = firstParamater.toLowerCase();
		secondParamater = secondParamater.toLowerCase();

		switch (firstParamater) {

		case "spawn":
			this.spawnMonsterCommand(secondParamater, player.currentMap, player.getX(), player.getY());
			break;

		case "kill":
			this.killCommand(secondParamater);
			break;

		default:
			this.sendInvalidCommand();
			break;
		}
	}

	public void sendInvalidCommand() {

	}

	public void spawnMonsterCommand(String monsterName, Map map, double x, double y) {
		Main.getServer().getManager().getMonsterManager().spawnMonster(new Monster(map, monsterName, x, y));
	}

	public void killCommand(String secondParamater) {
		if (secondParamater.equals("monsters"))
			Main.getServer().getManager().getMonsterManager().removeAllMonsters();
	}

}
