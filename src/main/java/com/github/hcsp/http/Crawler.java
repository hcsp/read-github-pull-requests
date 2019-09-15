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
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String uri = MessageFormat.format("https://github.com/{0}/pulls", repo);
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String html = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(html);

        Elements issues = doc.select(".js-issue-row");
        for (Element issue : issues) {
            issue = issue.child(0).child(1);
            int number = getNumbersFromString(issue.child(0).lastElementSibling().child(0).text().split("opened")[0]);
            String title = issue.child(0).text();
            String author = issue.child(0).lastElementSibling().child(0).child(1).text();
            gitHubPullRequestList.add(new GitHubPullRequest(number, title, author));
        }
        EntityUtils.consume(entity);

        return gitHubPullRequestList;
    }

    public static int getNumbersFromString(String content) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        String numbersString = m.replaceAll("");
        return Integer.parseInt(numbersString);
    }
}
