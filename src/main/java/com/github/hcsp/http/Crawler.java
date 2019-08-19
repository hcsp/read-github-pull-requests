package com.github.hcsp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        int i = 0;
        int number;
        String title, author;
        List<GitHubPullRequest> requestList = new ArrayList<>();
        try {
            URL url = new URL("https://api.github.com/repos/" + repo + "/pulls");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp;

                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                String result = sbf.toString();

                result = "{1:" + result + "}";
                //不是严格的JSON数据格式，补成标准的json数据格式
                JsonParser jParser = new JsonParser();
                JsonObject jt = (JsonObject) jParser.parse(result);
                JsonArray jArray = jt.get("1").getAsJsonArray();
                for (JsonElement array : jArray
                ) {
                    JsonObject subObject = jArray.get(i).getAsJsonObject();
                    number = subObject.get("number").getAsInt();
                    author = subObject.get("user").getAsJsonObject().get("login").getAsString();
                    title = subObject.get("title").getAsString();
                    GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                    requestList.add(gitHubPullRequest);
                    ++i;
                }
                System.out.println(jArray.size());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return requestList;
    }
}
