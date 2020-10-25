package com.github.hcsp.http;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String requestUrl = "https://api.github.com/repos/" + repo + "/pulls";
        List<GitHubPullRequest> result = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(requestUrl);

            CloseableHttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            String responseBodyString = EntityUtils.toString(entity);
            JSONArray responseBodyJson = new JSONArray(responseBodyString);

            Gson gson = new Gson();

            for (int i = 0; i < responseBodyJson.length(); i++) {
                String currDataString = responseBodyJson.get(i).toString();
                ResponseData currDataJson = gson.fromJson(currDataString, ResponseData.class);

                int number = currDataJson.number;
                String title = currDataJson.title;
                String author = currDataJson.user.get("login").toString();

                result.add(new GitHubPullRequest(number, title, author));
            }
        } finally {
            httpClient.close();
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        List<GitHubPullRequest> test = getFirstPageOfPullRequests("gradle");
    }

    private static class ResponseData {
        Integer number;
        String title;
        Map user = new HashMap();
    }
}
