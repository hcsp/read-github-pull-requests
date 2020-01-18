package com.github.hcsp.http;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.commons.io.IOUtils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.alibaba.fastjson.JSON;


public class Crawler {
    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("gradle/gradle"));
    }

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

    static class GitHubListPullRequestResponse {
        static class User {
            String login;

            public String getLogin() {
                return login;
            }
        }

        int number;
        String title;
        User user;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public GitHubListPullRequestResponse.User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {

        String PullURL = "https://api.github.com/repos/" + repo + "/pulls";
        //创建HttpClient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //创建HttpGet对象
        HttpGet httpGet = new HttpGet(PullURL);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity PullEntity = response.getEntity();
        String responseJson = IOUtils.toString(PullEntity.getContent(), "UTF-8");

        List<GitHubListPullRequestResponse> gitHubListPullRequestResponses =
                JSON.parseArray(responseJson, GitHubListPullRequestResponse.class);

        return gitHubListPullRequestResponses.stream().map(y -> new GitHubPullRequest(y.getNumber(), y.getTitle(),
                y.getUser().getLogin())).collect(Collectors.toList());
    }
}
