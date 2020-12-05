package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {

        String SITE = "https://github.com";
        String ROUTER = "pulls";
        List<GitHubPullRequest> gitLists = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            //发送get请求
            HttpGet request = new HttpGet(String.format("%s/%s/%s", SITE, repo, ROUTER));
            HttpResponse response = httpClient.execute(request);
            //获取请求体
            HttpEntity entity = response.getEntity();

            //转换成流
            InputStream IS = entity.getContent();

            String HTML = IOUtils.toString(IS, StandardCharsets.UTF_8);
            /**读取服务器返回过来的json字符串数据 并解析成节点**/
            Document document = Jsoup.parse(HTML);

            //解析节点
            Elements titles = document.select(".js-issue-row");
            Elements authors = document.select(".opened-by");

            for (int i = 0; i < titles.size(); i++) {
                String title = titles.get(i).child(0).child(1).child(0).text();
                String author = authors.get(i).child(1).text();
                int id = Integer.parseInt(titles.get(i).attr("id").replaceAll("\\D+", ""));
                gitLists.add(new Crawler.GitHubPullRequest(id, title, author));
            }


            return gitLists;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        List<Crawler.GitHubPullRequest> result = getFirstPageOfPullRequests("gradle/gradle");
        result.forEach(item -> System.out.println(item.toString()));
    }
}
