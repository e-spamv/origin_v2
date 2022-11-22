package de.claved.origin.utils;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

public class GameProfileBuilder {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();

	public static HashMap<UUID, CachedProfile> cache = new HashMap<>();

	public static GameProfile fromString(String string) {
		return gson.fromJson(string, GameProfile.class);
	}

	public static GameProfile fetch(UUID uuid) throws IOException {
		return fetch(uuid, false);
	}

	public static GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
		if (!forceNew && cache.containsKey(uuid) && cache.get(uuid).isValid()) {
			return cache.get(uuid).gameProfile;
		} else {
			HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid)))).openConnection();
			httpURLConnection.setReadTimeout(5000);
			if (httpURLConnection.getResponseCode() == 200) {
				String json = (new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))).readLine();
				GameProfile gameProfile = gson.fromJson(json, GameProfile.class);
				cache.put(uuid, new CachedProfile(gameProfile));
				return gameProfile;
			} else if (!forceNew && cache.containsKey(uuid)) {
				return cache.get(uuid).gameProfile;
			} else {
				JsonObject jsonObject = (JsonObject)(new JsonParser()).parse((new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()))).readLine());
				throw new IOException(jsonObject.get("error").getAsString() + ": " + jsonObject.get("errorMessage").getAsString());
			}
		}
	}

	private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

		public GameProfile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = (JsonObject) jsonElement;
			UUID uuid = jsonObject.has("id") ? (UUID) jsonDeserializationContext.deserialize(jsonObject.get("id"), UUID.class) : null;
			String name = jsonObject.has("name") ? jsonObject.getAsJsonPrimitive("name").getAsString() : null;
			GameProfile gameProfile = new GameProfile(uuid, name);

			if (jsonObject.has("properties")) {
				for (Entry<String, Property> prop : ((PropertyMap) jsonDeserializationContext.deserialize(jsonObject.get("properties"), PropertyMap.class)).entries()) {
					gameProfile.getProperties().put(prop.getKey(), prop.getValue());
				}
			}
			return gameProfile;
		}

		public JsonElement serialize(GameProfile gameProfile, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (gameProfile.getId() != null)
				jsonObject.add("id", jsonSerializationContext.serialize(gameProfile.getId()));
			if (gameProfile.getName() != null)
				jsonObject.addProperty("name", gameProfile.getName());
			if (!gameProfile.getProperties().isEmpty())
				jsonObject.add("properties", jsonSerializationContext.serialize(gameProfile.getProperties()));
			return jsonObject;
		}
	}

	public static class CachedProfile {

		public long timestamp = System.currentTimeMillis();
		public GameProfile gameProfile;

		public CachedProfile(GameProfile gameProfile) {
			this.gameProfile = gameProfile;
		}

		public boolean isValid() {
			return true;
		}
	}
}
