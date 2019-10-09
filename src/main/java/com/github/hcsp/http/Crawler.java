package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
        List<GitHubPullRequest> pulls = new ArrayList<GitHubPullRequest>();
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements doms = doc.select(".js-issue-row .lh-condensed");
        doms.forEach((dom) -> {
            String title = dom.child(0).text();
            String author = dom.getElementsByClass("opened-by").first().getElementsByTag("a").first().text();
            int id = Integer.parseInt(dom.child(0).attributes().get("id").split("_")[1]);

            pulls.add(new GitHubPullRequest(id, title, author));
        });
        return pulls;
    }
}
