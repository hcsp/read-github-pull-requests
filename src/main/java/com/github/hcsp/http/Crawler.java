package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
        List<GitHubPullRequest> list = new ArrayList<>(50);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
//            System.out.println(response1);
            System.out.println(response1.getStatusLine());

            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            // EntityUtils.consume(entity1);
            InputStream is = entity1.getContent();
//            String theString = IOUtils.toString(is, "UTF-8");
//            System.out.println(theString);
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String html = writer.toString();//在这里拿到了html的响应
//            System.out.println(theString);
            Document doc = Jsoup.parse(html);
            //           System.out.println(doc);
            Elements issue = doc.select(".js-issue-row");
            for (Element element : issue
            ) {
//                System.out.println(element);
//                System.out.println(element.id());//issue_12466
                int number = Integer.parseInt(element.id().replaceAll("[^\\d+]", ""));
                String title = element.child(0).child(1).child(0).text();
                String author = element.child(0).child(1).select(".muted-link").first().text();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                list.add(gitHubPullRequest);
            }
        } finally {
            response1.close();
        }
        return list;
    }

//    public static void main(String[] args) throws IOException {
//        List<GitHubPullRequest> list1=getFirstPageOfPullRequests("gradle/gradle");
//        for (GitHubPullRequest a:list1
//             ) {
//            System.out.println(a.number+" "+a.author+"        "+a.title);
//        }
//    }
}
