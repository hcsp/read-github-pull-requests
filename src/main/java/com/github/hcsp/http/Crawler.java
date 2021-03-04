package com.github.hcsp.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
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
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        httpGet.setHeader("Accept", "application/vnd.github.v3+json");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            HttpEntity responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent();
            String result = IOUtils.toString(inputStream, "UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(result);

            int number;
            String title;
            String author;
            for (int i = 0; i < node.size(); i++) {
                number = node.get(i).get("number").asInt();
                title = node.get(i).get("title").asText();
                author = node.get(i).get("user").get("login").asText();

                gitHubPullRequests.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(responseEntity);
        } finally {
            response.close();
        }
        return gitHubPullRequests;
    }
}

