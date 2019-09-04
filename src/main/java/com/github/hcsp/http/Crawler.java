package com.github.hcsp.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    static class GitHubPullRequestDeserializer implements JsonDeserializer<GitHubPullRequest> {

        @Override
        public GitHubPullRequest deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String author = "";
            if (jsonObject.has("user")) {
                author = jsonObject.getAsJsonObject("user").get("login").getAsString();
            }
            return new GitHubPullRequest(
                    jsonObject.get("number").getAsInt(),
                    jsonObject.get("title").getAsString(),
                    author);
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String url = StringUtils.join("https://api.github.com/repos/", repo, "/pulls");
        String json = sendHttpGet(url);
        return parseJson(json);
    }

    private static List<GitHubPullRequest> parseJson(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(GitHubPullRequest.class, new GitHubPullRequestDeserializer())
                                        .create();

        return gson.fromJson(json, new TypeToken<ArrayList<GitHubPullRequest>>() {}.getType());

    }

    private static String sendHttpGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String gitHubPullJson = "";
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            gitHubPullJson = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gitHubPullJson;
    }

    public static void main(String[] args) throws IOException {
        List<GitHubPullRequest> list1 = getFirstPageOfPullRequests("gradle/gradle");
        for (GitHubPullRequest request : list1) {
            System.out.println(request);
        }
        System.out.println(list1.size());
    }
}
