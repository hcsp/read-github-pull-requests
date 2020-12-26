package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        List<GitHubPullRequest> pulls = new ArrayList<>();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        request.addHeader("accept", "application/json");
        HttpResponse response = client.execute(request);
        String json = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        JSONArray array = JSONArray.parseArray(json);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            int number = object.getInteger("number");
            String title = object.getString("title");
            String author = object.getJSONObject("user").getString("login");
            GitHubPullRequest pull = new GitHubPullRequest(number, title, author);
            pulls.add(pull);
        }
        return pulls;
    }
}
