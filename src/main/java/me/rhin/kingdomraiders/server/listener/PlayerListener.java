package me.rhin.kingdomraiders.server.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.player.Player;

public class PlayerListener implements Listener {

	public PlayerListener() {

	}

	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Main.getServer().getManager().getPlayerManager().players.add(new Player(conn));
	}

	public void onClose(WebSocket conn, String reason) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		if (player.inGame()) {
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("type", "MPLeaveGame");
			jsonResponse.put("id", player.getID());
			jsonResponse.put("name", player.profile.getName());

			Main.getServer().sendToAllMPPlayers(player, jsonResponse.toString());
		}

		Main.getServer().getManager().getPlayerManager().players.remove(player);
	}

	public void onMessage(WebSocket conn, String message) {
		interpretMessage(conn, message);
	}

	public void interpretMessage(WebSocket conn, String message) {
		JSONObject jsonObj = new JSONObject(message);

		switch (jsonObj.getString("type")) {

		case "LoginRequest":
			Main.getServer().getManager().getAccountManager().loginToAccount(conn, jsonObj);
			break;

		case "LogoutRequest":
			Main.getServer().getManager().getAccountManager().logOut(conn, jsonObj);
			break;

		case "RegisterRequest":
			Main.getServer().getManager().getAccountManager().registerAccount(conn, jsonObj);
			break;

		case "JoinGame":
			Main.getServer().getManager().getPlayerManager().joinGame(conn);
			break;

		case "LeaveGame":
			Main.getServer().getManager().getPlayerManager().leaveGame(conn);
			break;

		// Check for collision?
		case "MovementUpdate":
			Main.getServer().getManager().getPlayerManager().updatePosition(conn, jsonObj);
			break;

		case "ChatMessage":
			Main.getServer().getManager().getPlayerManager().sendMessage(conn, jsonObj);
			break;

		// Todo: put these in an entityListener?
		case "AddShooter":
			Main.getServer().getManager().getProjectileManager().playerStartShooting(conn, jsonObj);
			break;

		case "ShooterUpdate":
			Main.getServer().getManager().getProjectileManager().playerUpdateShooting(conn, jsonObj);
			break;

		case "RemoveShooter":
			Main.getServer().getManager().getProjectileManager().playerStopShooting(conn, jsonObj);
			break;

		case "RequestInventory":
			Main.getServer().getManager().getInventoryManager().requestInventory(conn, jsonObj);
			break;

		case "RequestStats":
			Main.getServer().getManager().getStatsManager().requestStats(conn, jsonObj);
			break;

		case "ModifyInventory":
			Main.getServer().getManager().getInventoryManager().modifyInventory(conn, jsonObj);
			break;

		}

		jsonObj = null;
	}

}
