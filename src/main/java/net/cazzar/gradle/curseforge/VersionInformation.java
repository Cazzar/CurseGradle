/*
 * Copyright (C) 2014 Cayde Dixon (Cazzar)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.cazzar.gradle.curseforge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VersionInformation {
    @SuppressWarnings("unchecked")
    public static Map<Integer, LinkedTreeMap<String, String>> get() {
        Gson gson = new Gson();
        Map<Integer, LinkedTreeMap<String, String>> data = new HashMap<Integer, LinkedTreeMap<String, String>>();

        try {
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
