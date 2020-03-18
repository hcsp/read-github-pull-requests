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
        //先趴网页
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient aDefault = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = aDefault.execute(httpGet);

        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String html = IOUtils.toString(content, StandardCharsets.UTF_8);
        //再解析html
        Document document = Jsoup.parse(html);
        ArrayList<Element> elements = document.select(".js-issue-row");
        for (Element ele : elements) {
            String title = ele.child(0).child(1).child(0).text();
            int value = Integer.parseInt(ele.id().replaceAll("\\D", ""));
            String author = ele.child(0).child(1).select(".muted-link").first().text();
            list.add(new GitHubPullRequest(value, title, author));
        }
        return list;
    }
}
