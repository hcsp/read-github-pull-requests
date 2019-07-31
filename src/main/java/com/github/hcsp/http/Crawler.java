package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
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
        // Pull request的作者的GitHub id
        String author;


        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    static class GitHubPullResponse {
        int number;
        String title;
        User user;

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public User getUser() {
            return user;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUser(User user) {
            this.user = user;
        }

        static class User {
            String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String resonseStr = getFirstPageofPullRequestsToString(repo);
        List<GitHubPullResponse> listGitHubPullResponses = JSON.parseArray(resonseStr, GitHubPullResponse.class);
        List<GitHubPullRequest> result = new ArrayList<>();
        for (GitHubPullResponse item : listGitHubPullResponses) {
            result.add(new GitHubPullRequest(item.getNumber(), item.getTitle(), item.getUser().login));
        }
        return result;
    }

    public static String getFirstPageofPullRequestsToString(String repo) throws IOException {
        final String url = "https://api.github.com/repos/" + repo + "/pulls";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        String str = EntityUtils.toString(httpEntity, "UTF-8");
        return str;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
