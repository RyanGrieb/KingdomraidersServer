package me.rhin.kingdomraiders.server.listener;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public interface Listener {

	public void onOpen(WebSocket conn, ClientHandshake handshake);

	public void onClose(WebSocket conn, String reason);

	public void onMessage(WebSocket conn, String message);
	
	public void interpretMessage(WebSocket conn, String message);

}
