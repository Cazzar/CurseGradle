package net.cazzar.gradle.curseforge;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurseUpload extends DefaultTask {
    File artifact;

    String api_key;
    String stub;
    String name;
    String gameVersion;
    char releaseType = 'r';
    String changeLog;
    String changeLogMarkup = "plain";
    String caveats = "";
    String cevatsMarkup = "plain";

    public void artifact(String f) {
        artifact = new File(f);
    }

    public void api_key(String s) {
        api_key = s;
    }

    public void stub(String s) {
        stub = s;
    }

    public void name(String s) {
        name = s;
    }

    public void game_version(String version) {
        for (Map.Entry<Integer, LinkedTreeMap<String, String>> entry : VersionInformation.get().entrySet()) {
            if (entry.getValue().get("name").equalsIgnoreCase(version)) {
                gameVersion = String.valueOf(entry.getKey());
                return;
            }
        }

        gameVersion = version;
    }

    public void game_version(int version) {
        gameVersion = String.valueOf(version);
    }

    public void release_type(char type) {
        releaseType = type;
    }

    public void change_log(String log) {
        changeLog = log;
    }

    @TaskAction
    public void uploadToCurseForge() {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(String.format("http://minecraft.curseforge.com/mc-mods/%s/upload-file.json", stub));

//        System.out.println("VERSION: " + gameVersion);

        post.addHeader("X-API-Key", api_key);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addTextBody("name", this.name)
                .addTextBody("game_versions", gameVersion)
                .addTextBody("file_type", String.valueOf(releaseType))
                .addTextBody("change_log", changeLog)
                .addTextBody("change_log_markup_type", changeLogMarkup)
                .addTextBody("known_caveats", caveats)
                .addTextBody("caveats_markup_type", cevatsMarkup)
                .addPart("file", new FileBody(artifact))
                .build();

        post.setEntity(entity);
        try {
            HttpResponse response = client.execute(post);
            HttpEntity ent = response.getEntity();

            if (response.getStatusLine().getStatusCode() != 201) {
                Gson gson = new Gson();
                @SuppressWarnings("unchecked") Map<String, List<String>> data = gson.fromJson(EntityUtils.toString(ent), Map.class);
                StringBuilder sb = new StringBuilder();
                Iterator<Map.Entry<String, List<String>>> iter = data.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, List<String>> entry = iter.next();
                    sb.append(entry.getKey()).append(": ").append('\n');//.append(entry.getValue());

                    Iterator<String> iter2 = entry.getValue().iterator();
                    while (iter2.hasNext()) {
                        sb.append('\t').append(StringEscapeUtils.unescapeHtml(iter2.next()));

                        if (iter2.hasNext()) sb.append('\n');
                    }


                    if (iter.hasNext()) sb.append("\n\n");
                }
                throw new RuntimeException(sb.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
