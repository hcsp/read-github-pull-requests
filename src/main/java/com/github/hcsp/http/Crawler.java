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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
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
        List<GitHubPullRequest> list = new ArrayList<>();

        String url = "https://github.com/" + repo + "/pulls";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
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
            HttpEntity entity1 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".js-issue-row");
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            for (Element element : elements
            ) {
                String title = element.child(0).child(1).child(0).text();
                String idRaw = element.attr("id");
                Matcher m = p.matcher(idRaw);
                int id = Integer.parseInt(m.replaceAll("").trim());
                String author = element.select(".opened-by").select(".muted-link").text();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(id, title, author);
                list.add(gitHubPullRequest);
            }
        }finally {
            response.close();
        }
        return list;
    }
}
