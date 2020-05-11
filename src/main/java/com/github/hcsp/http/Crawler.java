package com.github.hcsp.http;

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
import java.util.LinkedList;
import java.util.List;
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
        String repoUrl = "https://github.com/" + repo + "/pulls";
        List<GitHubPullRequest> result = new LinkedList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(repoUrl);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                HttpEntity entity = response.getEntity();
                Document document = Jsoup.parse(EntityUtils.toString(entity), repoUrl);
                Elements elements = document.getElementsByAttributeValue("data-hovercard-type", "pull_request");
                for (Element element : elements
                     ) {
                    Element parent = element.parent();
                    String title = element.text();
                    String content = parent.select(".opened-by").text();
                    String pattern = "^#([0-9]*).*by\\s+(.*)$";

                    // 创建 Pattern 对象
                    Pattern r = Pattern.compile(pattern);

                    // 现在创建 matcher 对象
                    Matcher m = r.matcher(content);
                    if (m.find()) {
                        String author = m.group(2);
                        int number = Integer.parseInt(m.group(1), 10);
                        GitHubPullRequest item = new GitHubPullRequest(number, title, author);
                        result.add(item);
                    }
                }
                EntityUtils.consume(entity);
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("gradle/gradle").get(0));
    }
}
