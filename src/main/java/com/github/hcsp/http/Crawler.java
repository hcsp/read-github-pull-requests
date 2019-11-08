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
import java.io.IOException;
import java.io.InputStream;
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

        public String getTitle() {
            return this.title;
        }

        public String getAuthor() {
            return this.author;
        }

        public int getNumber() {
            return this.number;
        }

        @Override
        public String toString() {
            return getNumber() + " #### " + getTitle() + " #### " + getAuthor() + "\n";
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息

    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {

        List<GitHubPullRequest> information = new ArrayList<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();

            String html = IOUtils.toString(is, "UTF-8");

            Document doc = Jsoup.parse(html);

            ArrayList<Element> issues = doc.select(".js-issue-row");

            String author;
            String title;
            String numberS;
            int numberI;
            String str = "";
            List<GitHubPullRequest> string = new ArrayList<>();
            for (Element element : issues) {


                numberS = element.child(0).child(1).child(3).child(0).text();
                for (String part : numberS.split(" |", 2)) {
                    str = part;
                }
                for (String part : str.split(" ", 2)) {
                    str = part;
                    break;
                }

                numberI = Integer.parseInt(str);
                title = element.child(0).child(1).child(0).text();
                author = element.child(0).child(1).child(3).child(0).child(1).text();

                GitHubPullRequest temp = new GitHubPullRequest(numberI, title, author);
                information.add(temp);
            }

            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return information;
    }


    public static void main(String[] args) throws IOException {
        for (GitHubPullRequest member : getFirstPageOfPullRequests("gradle/gradle")) {
            System.out.println(member);
        }
    }
}
