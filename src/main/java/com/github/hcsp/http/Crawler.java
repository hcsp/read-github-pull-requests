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
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        @Override
        public String toString() {
            return number + "\n" + title + "\n" + author + "\n";
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        ArrayList<Element> issues = doc.select(".js-issue-row");
        ArrayList<GitHubPullRequest> results = new ArrayList<>();
        for (Element element : issues) {
            GitHubPullRequest pr = new GitHubPullRequest(
                    Integer.parseInt(element.attr("id").substring(6)),
                    element.select(".js-navigation-open").get(0).text(),
                    element.select(".muted-link").get(0).text()
            );
            System.out.println(pr);
            results.add(pr);
        }
        return results;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("golang/go");
    }
}
