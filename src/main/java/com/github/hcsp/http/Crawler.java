package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> result = new ArrayList<>();

        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements newsHeadlines = doc.select(".Box-row");
        for (Element headline : newsHeadlines) {
            Elements item = headline.select(".float-left");

            String title = item.select(".link-gray-dark").text();
            String numberAndAuthor = item.select("span.opened-by").text();
            Integer number = Integer.valueOf(numberAndAuthor.substring(numberAndAuthor.indexOf("#") + 1, numberAndAuthor.indexOf(" opened")));
            String author = numberAndAuthor.substring(numberAndAuthor.lastIndexOf(" ")).trim();

            result.add(new GitHubPullRequest(number, title, author));
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        List<GitHubPullRequest> firstPageOfPullRequests = getFirstPageOfPullRequests("gradle/gradle");
        System.out.println("firstPageOfPullRequests = " + firstPageOfPullRequests);
    }

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

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

}
