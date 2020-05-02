package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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

    static class GithubOriginPullRequest {
        private int number;
        private String title;
        private GithubOriginUser user;

        static class GithubOriginUser {
            private String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }

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

        public GithubOriginUser getUser() {
            return user;
        }

        public void setUser(GithubOriginUser user) {
            this.user = user;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        try {
            String repoResponse = createHttpRequest("https://api.github.com/repos/" + repo + "/pulls?page=1");
            List<GithubOriginPullRequest> originPulls = JSON.parseArray(repoResponse, GithubOriginPullRequest.class);
            return parseOriginPullList(originPulls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<GitHubPullRequest> parseOriginPullList(List<GithubOriginPullRequest> originPulls) {
        List<GitHubPullRequest> pulls = new ArrayList<>();
        for (GithubOriginPullRequest pull : originPulls) {
            pulls.add(new GitHubPullRequest(pull.number, pull.title, pull.user.login));
        }
        return pulls;
    }

    public static String createHttpRequest(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
        } finally {
            response.close();
        }
    }
}
