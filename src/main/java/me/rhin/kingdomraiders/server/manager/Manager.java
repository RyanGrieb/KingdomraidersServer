package me.rhin.kingdomraiders.server.manager;

import me.rhin.kingdomraiders.server.manager.item.ItemManager;
import me.rhin.kingdomraiders.server.manager.map.MapManager;
import me.rhin.kingdomraiders.server.manager.map.entity.monster.MonsterManager;
import me.rhin.kingdomraiders.server.manager.map.entity.projectile.ProjectileManager;
import me.rhin.kingdomraiders.server.manager.player.PlayerManager;
import me.rhin.kingdomraiders.server.manager.player.account.AccountManager;
import me.rhin.kingdomraiders.server.manager.player.commands.CommandManager;
import me.rhin.kingdomraiders.server.manager.player.inventory.InventoryManager;
import me.rhin.kingdomraiders.server.manager.player.stats.StatsManager;

public class Manager {

	// Player
	private PlayerManager playerManager;
	private AccountManager accountManager;
	private InventoryManager inventoryManager;
	private StatsManager statsManager;
	private CommandManager commandManager;

	// Other Entities
	private MonsterManager monsterManager;
	private ProjectileManager projectileManager;

	// Items
	private ItemManager itemManager;

	// Map
	private MapManager mapManager;

	public Manager() {
		playerManager = new PlayerManager();
		accountManager = new AccountManager();
		inventoryManager = new InventoryManager();
		statsManager = new StatsManager();
		commandManager = new CommandManager();

		monsterManager = new MonsterManager();
		projectileManager = new ProjectileManager();

		itemManager = new ItemManager();

		mapManager = new MapManager();
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public StatsManager getStatsManager() {
		return statsManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public MonsterManager getMonsterManager() {
		return monsterManager;
	}

	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public MapManager getMapManager() {
		return mapManager;
	}
}
