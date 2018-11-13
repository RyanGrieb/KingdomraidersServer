package me.rhin.kingdomraiders.server.manager.player.account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Base64;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.entity.player.Player;
import me.rhin.kingdomraiders.server.entity.player.PlayerProfile;

public class AccountManager {

	// For password salting..
	private static final Random RANDOM = new SecureRandom();

	public void loginToAccount(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "LoginRequest");

		// If username/email doesn't exist
		if (!emailTaken(jsonObj.getString("email"))) {
			// Send rejected json..
			jsonResponse.put("response", "rejected");
			jsonResponse.put("reason", "Incorrect password");
			conn.send(jsonResponse.toString());
			return;
		}

		String name = getNameFromEmail(jsonObj.getString("email"));
		PlayerProfile requestedProfile = new PlayerProfile(name);

		// If incorrect password
		if (!isExpectedPassword(jsonObj.getString("password"), requestedProfile.getSalt(),
				requestedProfile.getHashedPassword())) {
			// Send rejected json..
			jsonResponse.put("response", "rejected");
			jsonResponse.put("reason", "Incorrect password");
			conn.send(jsonResponse.toString());
			return;
		}
		// If correct password
		player.setProfile(requestedProfile);

		jsonResponse.put("response", "accepted");
		jsonResponse.put("name", requestedProfile.getName());
		conn.send(jsonResponse.toString());

	}

	public void logOut(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);
		player.setProfile(null);
	}

	public void registerAccount(WebSocket conn, JSONObject jsonObj) {
		Player player = Main.getServer().getPlayerFromConn(conn);

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("type", "RegisterRequest");

		if (usernameTaken(jsonObj.getString("username"))) {
			// Send rejected json..
			jsonResponse.put("response", "rejected");
			jsonResponse.put("reason", "Username taken");
			conn.send(jsonResponse.toString());
			return;
		}

		if (emailTaken(jsonObj.getString("email"))) {

			// Send rejected json..
			jsonResponse.put("response", "rejected");
			jsonResponse.put("reason", "Email taken");
			conn.send(jsonResponse.toString());
			return;
		}

		// All good, set the profile...

		player.setProfile(new PlayerProfile(jsonObj.getString("username")));

		// Set Email
		player.profile.setEmail(jsonObj.getString("email"));

		// Set Password
		byte[] salt = getNextSalt();
		String password = Base64.toBase64String(hash(jsonObj.getString("password"), salt));
		player.profile.setSalt(Base64.toBase64String(salt));
		player.profile.setPassword(password);
		
		//Give default inventory
		player.profile.setInventory(Main.getServer().getManager().getInventoryManager().DEFAULT_INVENTORY);

		// Respond with success json...
		jsonResponse.put("response", "accepted");
		conn.send(jsonResponse.toString());
	}

	private boolean usernameTaken(String username) {
		try {
			File folder = new File("./assets/profiles/");
			File[] listOfFiles = folder.listFiles();

			// Store created accounts as username.profile...
			for (int i = 0; i < listOfFiles.length; i++)
				if (listOfFiles[i].isFile()) {
					String fileData = new String(Files.readAllBytes(listOfFiles[i].toPath()));
					JSONObject jsonObj = new JSONObject(fileData);

					if (jsonObj.getString("username").equals(username))
						return true;
				}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean emailTaken(String email) {
		try {
			File folder = new File("./assets/profiles/");
			File[] listOfFiles = folder.listFiles();

			// Store created accounts as username.profile...
			for (int i = 0; i < listOfFiles.length; i++)
				if (listOfFiles[i].isFile()) {
					String fileData = new String(Files.readAllBytes(listOfFiles[i].toPath()));
					JSONObject jsonObj = new JSONObject(fileData);

					if (jsonObj.getString("email").equals(email))
						return true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getNameFromEmail(String email) {
		File folder = new File("./assets/profiles/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].isFile())
				try {
					String fileData = new String(Files.readAllBytes(listOfFiles[i].toPath()));
					JSONObject jsonObj = new JSONObject(fileData);
					if (email.equals(jsonObj.getString("email")))
						return listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf('.'));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		return null;
	}

	// Encryption Methods

	public byte[] getNextSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return salt;
	}

	public byte[] hash(String password, byte[] salt) {

		try {
			final byte[] passwordBytes = password.getBytes("UTF-8");
			final byte[] all = ArrayUtils.addAll(passwordBytes, salt);
			SHA3.DigestSHA3 md = new SHA3.Digest512();
			md.update(all);
			return md.digest();
		} catch (UnsupportedEncodingException e) {
			final String message = String.format("Caught UnsupportedEncodingException e: <%s>", e.getMessage());
		}
		return new byte[0];
	}

	public boolean isExpectedPassword(final String password, final byte[] salt, final byte[] hash) {

		try {
			final byte[] passwordBytes = password.getBytes("UTF-8");
			final byte[] all = ArrayUtils.addAll(passwordBytes, salt);

			SHA3.DigestSHA3 md = new SHA3.Digest512();
			md.update(all);
			final byte[] digest = md.digest();
			return Arrays.equals(digest, hash);
		} catch (UnsupportedEncodingException e) {
			final String message = String.format("Caught UnsupportedEncodingException e: <%s>", e.getMessage());
		}
		return false;

	}

}
