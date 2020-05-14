package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private static final String GITHUBURL = "https://github.com";


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
        List<GitHubPullRequest> list = new ArrayList<>();
        String url = getGithubPollsUrl(repo);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream ins = entity.getContent();
            String content = IOUtils.toString(ins, "utf-8");
            Document doc = Jsoup.parse(content);
            Elements elements = doc.select(".js-issue-row");
            for (Element element : elements) {
                Element aLink = element.child(0).child(1).child(0);
                Element author = element.child(0).child(1).select(".text-gray").first().child(0).child(1);
                String href = aLink.attr("href");
                String number = href.substring(href.lastIndexOf('/') + 1);

                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(Integer.parseInt(number), aLink.text(), author.text());
                list.add(gitHubPullRequest);
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return list;
    }

    private static String getGithubPollsUrl(String repo) {
        return GITHUBURL + '/' + repo + "/pulls";
    }

}
