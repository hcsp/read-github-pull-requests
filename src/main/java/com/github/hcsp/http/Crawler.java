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
import java.util.ArrayList;
import java.util.List;

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
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
    CloseableHttpResponse response1 = httpclient.execute(httpGet);

    List<GitHubPullRequest> list = new ArrayList<>();
    try {
      System.out.println(response1.getStatusLine());
      HttpEntity entity1 = response1.getEntity();
      String html = IOUtils.toString(entity1.getContent(), "UTF-8");
      System.out.println(html);
      Document document = Jsoup.parse(html);
      Elements issues = document.select(".Box-row");
      for (Element e : issues) {
        String title = e.select(".lh-condensed").select("a").get(0).text();
        String[] issueMsg = e.select(".opened-by").get(0).text().split(" ");
        String number = issueMsg[0].substring(1);
        String author = issueMsg[issueMsg.length - 1];
        list.add(new GitHubPullRequest(Integer.parseInt(number), title, author));
      }
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity1);
    } finally {
      response1.close();
    }

    return list;
  }

  public static void main(String[] args) {
    try {
      getFirstPageOfPullRequests("gradle/gradle");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
