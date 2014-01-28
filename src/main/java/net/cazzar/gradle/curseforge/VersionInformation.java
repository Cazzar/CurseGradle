package net.cazzar.gradle.curseforge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VersionInformation {
    public static Map<Integer, LinkedTreeMap<String, String>> get() {
        Gson gson = new Gson();
        Map<Integer, LinkedTreeMap<String, String>> data = new HashMap<Integer, LinkedTreeMap<String, String>>();

        try {
            //noinspection unchecked
            data = gson.fromJson(readUrl("http://minecraft.curseforge.com/game-versions.json"), data.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }

    }
}
