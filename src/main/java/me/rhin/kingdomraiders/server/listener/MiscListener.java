package me.rhin.kingdomraiders.server.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class MiscListener implements Listener {

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose(WebSocket conn, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (message.contains("ping")) {
			String time = message.substring(message.indexOf(",") + 1);
			conn.send("pong," + time);
		}

	}

	@Override
	public void interpretMessage(WebSocket conn, String message) {
		// TODO Auto-generated method stub

	}

}
