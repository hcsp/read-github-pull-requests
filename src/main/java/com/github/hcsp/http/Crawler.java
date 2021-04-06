package com.github.hcsp.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
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
    List<GitHubPullRequest> list = new ArrayList<>();
    String uri = "https://github.com/" + repo + "/pulls";
    Document doc = Jsoup.connect(uri).get();
    System.out.println(doc.title());
    Elements prList = doc.select(".js-issue-row");
    for (Element prRow : prList) {
      GitHubPullRequest pr = new GitHubPullRequest(
          Integer.parseInt(prRow.attr("id").split("_")[1]),
          prRow.select(".markdown-title").text(),
          prRow.select("[data-hovercard-type=\"user\"]").text()
      );
      list.add(pr);
    }
    return list;
  }
}
