package com.github.hcsp.http;

import net.dongliu.requests.Requests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    static int getNumberOfOpenInfo(String openedBy) {
        String numberStr = openedBy.split("opened")[0];
        return Integer.valueOf(numberStr.split("#")[1].trim());
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        String baseUrl = "https://github.com/";
        String url = baseUrl + repo + "/pulls";

        String htmlString = Requests.get(url).send().readToText();

        Document doc = Jsoup.parse(htmlString);
        Elements issueElements = doc.select(".js-issue-row");

        ArrayList<GitHubPullRequest> pullRequestList = new ArrayList<>();

        for (Element issue : issueElements) {
            String title = issue.select("a[data-hovercard-type=\"pull_request\"]").text();
            String author = issue.select("a[data-hovercard-type=\"user\"]").text();

            int number = getNumberOfOpenInfo(issue.select(".opened-by").text());

            pullRequestList.add(new GitHubPullRequest(number, title, author));
        }

        return pullRequestList;
    }

    public static void main(String[] args) {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
