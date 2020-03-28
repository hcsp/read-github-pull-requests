package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

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
        List<GitHubPullRequest> pullRequestList = new ArrayList<>();
        // 例子：https://api.github.com/repos/golang/go/pulls
        String pullRequests = "https://api.github.com/repos/" + repo + "/pulls";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("http.socket.timeout", 0);
        context.setAttribute("http.connection.stalecheck", Boolean.TRUE);
        HttpGet httpGet = new HttpGet(pullRequests);
        CloseableHttpResponse response1 = httpclient.execute(httpGet, context);
        try {
            int statusCode = response1.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            if (200 != statusCode) {
                // 状态码不对需要进一步处理
                return pullRequestList;
            }
            HttpEntity entity1 = response1.getEntity();
            InputStream contentStream = entity1.getContent();
            try {
                String content = IOUtils.toString(contentStream, "UTF-8");
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int number = jsonObject.getInt("number");
                    String title = jsonObject.getString("title");
                    String author = jsonObject.getJSONObject("user").getString("login");
                    GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                    pullRequestList.add(gitHubPullRequest);
                }

            } finally {
                IOUtils.closeQuietly(contentStream);
            }
        } finally {
            response1.close();
        }
        return pullRequestList;
    }

}
