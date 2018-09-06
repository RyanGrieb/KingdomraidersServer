package me.rhin.kingdomraiders.server.manager;

import me.rhin.kingdomraiders.server.manager.map.MapManager;
import me.rhin.kingdomraiders.server.manager.player.PlayerManager;
import me.rhin.kingdomraiders.server.manager.player.account.AccountManager;

public class Manager {

	private PlayerManager playerManager;
	private AccountManager accountManager;

	// Map
	private MapManager mapManager;

	public Manager() {
		playerManager = new PlayerManager();
		accountManager = new AccountManager();

		mapManager = new MapManager();
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public MapManager getMapManager() {
		return mapManager;
	}
}
