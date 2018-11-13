package me.rhin.kingdomraiders.server.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;

public class MapListener implements Listener {

	public void onOpen(WebSocket conn, ClientHandshake handshake) {
	}

	public void onClose(WebSocket conn, String reason) {
	}

	public void onMessage(WebSocket conn, String message) {
		interpretMessage(conn, message);
	}

	public void interpretMessage(WebSocket conn, String message) {
		// TODO Auto-generated method stub
		JSONObject jsonObj = new JSONObject(message);

		switch (jsonObj.getString("type")) {

		case "ChunkRequest":
			Main.getServer().getManager().getMapManager().sendChunkFromLocation(conn, jsonObj);
			break;

		case "BuildRequest":
			Main.getServer().getManager().getMapManager().build(conn, jsonObj);
			break;

		}

	}

}
