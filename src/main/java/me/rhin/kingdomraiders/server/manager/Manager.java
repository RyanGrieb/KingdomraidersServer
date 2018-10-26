package me.rhin.kingdomraiders.server.manager;

import me.rhin.kingdomraiders.server.manager.map.MapManager;
import me.rhin.kingdomraiders.server.manager.player.PlayerManager;
import me.rhin.kingdomraiders.server.manager.player.account.AccountManager;
import me.rhin.kingdomraiders.server.manager.player.inventory.InventoryManager;

public class Manager {

	// Player
	private PlayerManager playerManager;
	private AccountManager accountManager;
	private InventoryManager inventoryManager;

	// Map
	private MapManager mapManager;

	public Manager() {
		playerManager = new PlayerManager();
		accountManager = new AccountManager();
		inventoryManager = new InventoryManager();

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

	public MapManager getMapManager() {
		return mapManager;
	}
}
