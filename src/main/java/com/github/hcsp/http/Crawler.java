package com.github.hcsp.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }

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

    static class GitHubListPullRequestResponse {
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

        public User getUser() {
            return user;
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

        // see https://developer.github.com/v3/pulls/#list-pull-requests
        final String RepoURL = "https://api.github.com/repos/" + repo + "/pulls";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(RepoURL);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");


        List<GitHubListPullRequestResponse> gitHubListPullRequestResponses = JSON.parseArray(responseJson, GitHubListPullRequestResponse.class);


        return gitHubListPullRequestResponses
                .stream()
                .map(x -> new GitHubPullRequest(x.getNumber(), x.getTitle(), x.getUser().getLogin()))
                .collect(Collectors.toList());
    }

}
