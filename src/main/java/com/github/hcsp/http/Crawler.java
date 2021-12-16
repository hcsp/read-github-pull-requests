package com.github.hcsp.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }

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
        String rul = "https://api.github.com/repos/" + repo + "/pulls";
        ArrayList<GitHubPullRequest> result = new ArrayList<>();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(rul);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity1 = response.getEntity();
                InputStream content = entity1.getContent();
                String body = IOUtils.toString(content, StandardCharsets.UTF_8);
                JSONArray bodyArray = JSON.parseArray(body);
                for (Object o : bodyArray) {
                    JSONObject bodyObject = (JSONObject) o;
                    result.add(
                            new GitHubPullRequest(
                                    bodyObject.getIntValue("number"),
                                    bodyObject.getString("title"),
                                    bodyObject.getJSONObject("user").getString("login")
                            ));

                }
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
            }

            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("gradle/gradle"));
    }
}
