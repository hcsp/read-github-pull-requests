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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();
        try {
            //System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            InputStream content = entity1.getContent();
            String html = IOUtils.toString(content, StandardCharsets.UTF_8);
            //System.out.println(html);
            Document document = Jsoup.parse(html);
            Elements newsHeadlines = document.select(".js-issue-row");
            for (Element element : newsHeadlines) {

                //System.out.println(element);
                //System.out.println(element.child(0).child(1).select("a").text());
                String titleTest = element.child(0).child(1).select("a").text();
                String title = titleTest.substring(0, titleTest.lastIndexOf(" ") - 1);

                //System.out.println(title);
                //System.out.println(element.child(0).child(1).select(".text-gray").select("span").text());

                String span = element.child(0).child(1).select(".text-gray").select(".opened-by").select("span").text();
                int number = Integer.parseInt(span.substring(0, span.indexOf(" ")).replace("#", ""));

                //System.out.println(number);

                //System.out.println(element.child(0).child(1).select(".text-gray").select("span").select("a").text());

                String author = element.child(0).child(1).select(".text-gray").select(".opened-by").select("span").select("a").text();
                //System.out.println(author);

                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                gitHubPullRequestList.add(gitHubPullRequest);
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }

        return gitHubPullRequestList;
    }
}
