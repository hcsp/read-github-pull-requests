package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            String html = IOUtils.toString(is, StandardCharsets.UTF_8);

            Document document = Jsoup.parse(html);

            ArrayList<Element> issues = document.select(".js-issue-row");

            for (Element element :
                    issues) {
                String numberTempe = element.child(0).select(".col-8").get(0).select(".mt-1").get(0).select(".opened-by").get(0).getElementsByTag("span").get(0).text();
                int number = Integer.parseInt(numberTempe.split(" ")[0].substring(1));

                String title = element.child(0).select(".col-8").get(0).select(".link-gray-dark").get(0).text();
                String author = element.child(0).select(".col-8").get(0).select(".mt-1").get(0).getElementsByTag("a").get(0).text();

                gitHubPullRequestList.add(new GitHubPullRequest(number, title, author));
            }

        }

        return gitHubPullRequestList;
    }
}
