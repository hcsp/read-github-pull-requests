package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=20";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            List<GitHubPullRequest> result = new ArrayList<>();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getURI());
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                JSON.parseArray(responseBody).forEach( j -> {
                    String user = ((JSONObject) j).getJSONObject("user").toString();
                    JSONObject username = JSONObject.parseObject(user);
                    result.add(new GitHubPullRequest(
                            ((JSONObject) j).getInteger("number"),
                            ((JSONObject) j).getString("title"),
                            username.getString("login")
                    ));
                });
                EntityUtils.consume(entity);
            }
            return result;
        }
    }
}
