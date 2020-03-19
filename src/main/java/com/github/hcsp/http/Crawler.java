package com.github.hcsp.http;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
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

        @Override
        public String toString() {
            return String.format("PullRequest[%d] [%s] by [%s]", number, title, author);
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> result = new ArrayList<>();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("https://api.github.com/repos/%s/pulls", repo));
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Content-Type", "application/json");
        CloseableHttpResponse response = client.execute(httpGet);

        JsonNode json = new ObjectMapper().readTree(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        Iterator<JsonNode> iterator = json.elements();
        while (iterator.hasNext()) {
            JsonNode pull = iterator.next();
            result.add(new GitHubPullRequest(
                    pull.get("number").asInt(),
                    pull.get("title").asText(),
                    pull.get("user").get("login").asText())
            );
        }

        client.close();
        return result;
    }
}
