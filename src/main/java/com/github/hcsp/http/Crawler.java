package com.github.hcsp.http;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Crawler {

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        private Map<String, Object> user = new HashMap<>();
        String author;

        public int getNumber() {
            return Integer.parseInt(String.valueOf(number));
        }

        public String getTitle() {
            return title;
        }

        public String getLogin() {
            author = (String) user.get("login");
            return (String) user.get("login");
        }


        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        //用httpclient发起请求
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            List<GitHubPullRequest> list = new ArrayList<>();
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            String data = IOUtils.toString(is, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type datasetListType = new TypeToken<Collection<GitHubPullRequest>>() {
            }.getType();
            List<GitHubPullRequest> gitHubPullRequests = gson.fromJson(data, datasetListType);

            for (GitHubPullRequest pr : gitHubPullRequests) {
                int number = pr.getNumber();
                String title = pr.getTitle();
                String author = pr.getLogin();
                list.add(new GitHubPullRequest(number, title, author));
            }


            return list;

        }




    }

    public static void main(String[] args) throws IOException {
        String repo = "gradle/gradle";
        GitHubPullRequest firstPull = Crawler.getFirstPageOfPullRequests(repo).get(0);
        System.out.println(firstPull);


    }
}
