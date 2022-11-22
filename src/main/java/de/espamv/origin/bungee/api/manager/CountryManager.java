package de.claved.origin.bungee.api.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

@Getter
public class CountryManager {

    @Getter
    private static CountryManager instance;

    public CountryManager() {
        instance = this;
    }

    public void disable() {
        instance = null;
    }

    private final LoadingCache<String, String> countryCodeCache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, String>() {
        @Override
        public String load(String ip) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://ip-api.com/json/" + ip).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json = reader.readLine();
            JsonObject object = new JsonParser().parse(json).getAsJsonObject();
            return object.get("countryCode").getAsString();
        }
    });

    public String getCountryCode(String ip) {
        try {
            return countryCodeCache.get(ip);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "en";
    }
}
