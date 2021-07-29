package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

        ArrayList<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();
        ArrayList<Integer> pullRequestNumber = new ArrayList<>();
        ArrayList<String> pullRequestTitle = new ArrayList<>();
        ArrayList<String> pullRequestAuthor = new ArrayList<>();

        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build()) {
            HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
                HttpEntity entity = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                //EntityUtils.consume(entity1);
                String html = EntityUtils.toString(entity);
                Document doc = Jsoup.parse(html);
                Elements pulls = doc.select(".js-issue-row");
                Elements authors = doc.select(".opened-by");

                for (Element element : authors) {
                    String[] parsingResults = element.text().split("\\s+");
                    pullRequestNumber.add(Integer.parseInt(parsingResults[0].substring(1)));
                    pullRequestAuthor.add(parsingResults[parsingResults.length - 1]);
                    System.out.println(Integer.parseInt(parsingResults[0].substring(1)));
                }

                for (Element element : pulls) {
                    pullRequestTitle.add(element.child(0).child(1).child(0).text());
                    //System.out.println(element.child(0).child(1).child(0).text());

                }

                if (pullRequestNumber.size() == pullRequestTitle.size()) {
                    for (int i = 0; i < pullRequestNumber.size(); i++) {
                        gitHubPullRequestList.add(new GitHubPullRequest(pullRequestNumber.get(i),
                                pullRequestTitle.get(i),
                                pullRequestAuthor.get(i)));
                    }
                }
            }
        }
        return gitHubPullRequestList;
    }
}
