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
//import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
//import java.net.URL;
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
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();

//        URL url = new URL("https://github.com/" + repo + "/pulls");
//        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//        conn.setInstanceFollowRedirects(false);
//        conn.connect();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
//            InputStream is = conn.getInputStream();
            String html = IOUtils.toString(is, "UTF-8");
            Document doc = Jsoup.parse(html);
            ArrayList<Element> issues = doc.select(".js-issue-row");
            for (Element element : issues) {
                String[] authorAndNumber = element.select(".opened-by").text().split(" ");
                String number = authorAndNumber[0].replace("#", "");
                String author = authorAndNumber[authorAndNumber.length - 1];
                String title = element.child(0).child(1).child(0).text();

                gitHubPullRequests.add(new GitHubPullRequest(Integer.parseInt(number), title, author));
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return gitHubPullRequests;
    }
}
