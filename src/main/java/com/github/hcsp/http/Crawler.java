package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        ArrayList<GitHubPullRequest> resultList = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?state=all&page=1");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            String content = EntityUtils.toString(entity1, "UTF-8");
            content = "{ \"content\" :" + content + '}';
            JSONObject jsonContent = new JSONObject(content);
            JSONArray result = (JSONArray) jsonContent.get("content");
            for (int i = 0; i < ((JSONArray) jsonContent.get("content")).length(); i++) {
                JSONObject item = (JSONObject) result.get(i);
                String title = (String) item.get("title");
                int number = (int) item.get("number");
                JSONObject user = (JSONObject) item.get("user");
                String userName = (String) user.get("login");
                resultList.add(new GitHubPullRequest(number, title, userName));
            }
        } finally {
            response1.close();
        }
        return resultList;
    }
}
