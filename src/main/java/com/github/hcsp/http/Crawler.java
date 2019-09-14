package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        List<GitHubPullRequest> pullRequests = new ArrayList<>();
        String apiUrl = "https://api.github.com/repos/" + repo + "/pulls";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // by the connection manager.
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity.getContent();
            String pullRequestJson = IOUtils.toString(is, "UTF-8");
            JSONArray pulls = JSON.parseArray(pullRequestJson);
            for (int i = 0; i < pulls.size(); i++) {
                JSONObject pullRequest = pulls.getJSONObject(i);
                int number = pullRequest.getInteger("number");
                String title = pullRequest.getString("title");
                String author = pullRequest.getJSONObject("user").getString("login");
                pullRequests.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return pullRequests;
    }
}
