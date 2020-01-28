package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
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

    //     给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            //System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();
            String result = IOUtils.toString(content, StandardCharsets.UTF_8);
            JSONArray JsonArray = new JSONArray(result);
            for (int i = 0; i < JsonArray.length(); ++i) {
                JSONObject obj = JsonArray.getJSONObject(i);
                GitHubPullRequest pullRequest = new GitHubPullRequest(obj.getInt("number"), obj.getString("title"), obj.getJSONObject("user").getString("login"));
                list.add(pullRequest);
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;

    }
}
