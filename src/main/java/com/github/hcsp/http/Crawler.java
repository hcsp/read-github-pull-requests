package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String website = "https://github.com";
        String page = "/pulls";
        List<Crawler.GitHubPullRequest> result = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("%s/%s%s", website, repo, page));
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try {
            System.out.println(response.getStatusLine());
            //            HttpEntity是实体 body
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            String theString = IOUtils.toString(content, StandardCharsets.UTF_8);
            // 用第三方库JSoup解析HTML
            Document document = Jsoup.parse(theString);
            Elements selects_title = document.select(".js-issue-row");
            Elements selects_author = document.select(".opened-by"); // 不能用跟上面一样的selector, 因为author的位置会变化
            //   示例代码中的    EntityUtils.consume(entity1);
            for (int i = 0; i < selects_title.size(); i++) {
                int id = Integer.parseInt(selects_title.get(i).attr("id").replaceAll("\\D+", ""));
                String title = selects_title.get(i).child(0).child(1).child(0).text();
                String author = selects_author.get(i).child(1).text();
                // String href = select.child(0).child(1).child(0).attr("href");
                result.add(new Crawler.GitHubPullRequest(id, title, author));
            }
        } finally {
            response.close();
        }
        return result;
    }
}

class Test {
    public static void main(String[] args) throws IOException {
        String repo = "gradle/gradle";
        String website = "https://github.com";
        String page = "/pulls";
        List<Crawler.GitHubPullRequest> result = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("%s/%s%s", website, repo, page));
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try {
            System.out.println(response.getStatusLine());
            //            HttpEntity是实体 body
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            String theString = IOUtils.toString(content, StandardCharsets.UTF_8);
            // 用第三方库JSoup解析HTML
            Document document = Jsoup.parse(theString);
            Elements selects_title = document.select(".js-issue-row");
            Elements selects_author = document.select(".opened-by");
            //   示例代码中的    EntityUtils.consume(entity1);
            for (int i = 0; i < selects_title.size(); i++) {
                int id = Integer.parseInt(selects_title.get(i).attr("id").replaceAll("\\D+", ""));
                String title = selects_title.get(i).child(0).child(1).child(0).text();
                String author = selects_author.get(i).child(1).text();
                // String href = select.child(0).child(1).child(0).attr("href");
                result.add(new Crawler.GitHubPullRequest(id, title, author));
            }
        } finally {
            response.close();
        }
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/issues");
//        CloseableHttpResponse response = httpclient.execute(httpGet);
//        // The underlying HTTP connection is still held by the response object
//        // to allow the response content to be streamed directly from the network socket.
//        // In order to ensure correct deallocation of system resources
//        // the user MUST call CloseableHttpResponse#close() from a finally clause.
//        // Please note that if response content is not fully consumed the underlying
//        // connection cannot be safely re-used and will be shut down and discarded
//        // by the connection manager.
//        try {
//            System.out.println(response.getStatusLine());
////            HttpEntity是实体 body
//            HttpEntity entity1 = response.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            InputStream content = entity1.getContent();
//            String theString = IOUtils.toString(content, StandardCharsets.UTF_8);
//            // 用第三方库解析HTML  JSoup
//            Document document = Jsoup.parse(theString);
//            ArrayList<Element> selects = document.select(".js-issue-row");
////            Elements issues = document.select(".js-issue-row");
////            EntityUtils.consume(entity1);
//            for (Element select :
//                    selects) {
////                System.out.println("element.child(0).child(1).child(0).attr(\"href\") = " + element.child(0).child(1).child(0).attr("href"));
////                System.out.println("element.child(0).child(1).child(0).text() = " + element.child(0).child(1).child(0).text());
////                System.out.println("-----");
//                int id = Integer.parseInt(select.attr("id").replaceAll("\\D+", ""));
//                String title = select.child(0).child(1).child(0).text();
//                String href = select.child(0).child(1).child(0).attr("href");
//                System.out.println("select.child(0).child(1).child(0) = " + select.child(0).child(1).child(0));
//                Element child = select.child(0).child(1).child(0);
//            }
//        } finally {
//            response.close();
//        }

//        HttpPost httpPost = new HttpPost("http://targethost/login");
//        List <NameValuePair> nvps = new ArrayList<NameValuePair>();
//        nvps.add(new BasicNameValuePair("username", "vip"));
//        nvps.add(new BasicNameValuePair("password", "secret"));
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//        CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//        try {
//            System.out.println(response2.getStatusLine());
//            HttpEntity entity2 = response2.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity2);
//        } finally {
//            response2.close();
//        }
    }
}
