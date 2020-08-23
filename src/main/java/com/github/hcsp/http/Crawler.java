package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    public static void main(String[] args) throws IOException {
        String REQUEST_URL = "https://github.com/gradle/gradle/pulls";
        getFirstPageOfPullRequests(REQUEST_URL);
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> requestList = new ArrayList<>();
        Document document = Jsoup.connect(repo).get();
        ArrayList<Element> elementList = document.select(".js-issue-row");

        for (Element element : elementList) {
            int number = Integer.parseInt(element.select(".mt-1.text-small.text-gray").get(0).text().split(" ")[0].substring(1));
            String title = element.child(0).child(1).child(0).text();
            String author = element.select(".mt-1.text-small.text-gray").get(0).text().split(" ")[6];
            GitHubPullRequest message = new GitHubPullRequest(number, title, author);
            requestList.add(message);
            System.out.println(number + title + author);
        }
        return requestList;
    }
}
