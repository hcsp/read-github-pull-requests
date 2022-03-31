package com.github.hcsp.http;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(html);
        Elements issues = document.select(".js-issue-row");

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

    public static void main(String[] args) throws IOException {
        setProxy();
        System.out.println(getFirstPageOfPullRequests("gradle/gradle"));
    }

    private static void setProxy() {
        String proxyHost = "127.0.0.1";
        String proxyPort = "7890";
        // 对 http 开启代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对 https 也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
    }

    public static int getNumbersFromString(String content) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        String numbersString = m.replaceAll("");
        return Integer.parseInt(numbersString);
    }
}
