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
    }

    //     给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36 Edg/80.0.361.69");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            //System.out.println(response.getStatusLine());
            HttpEntity entity1 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            //EntityUtils.consume(entity1);
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document doc = Jsoup.parse(html);
            ArrayList<Element> pr = doc.select(".Box-row--focus-gray");
            ArrayList<GitHubPullRequest> list = new ArrayList<>();
            for (Element element : pr){
                String title = element.select(".lh-condensed").select(".link-gray-dark").eachText().toString();   //title
                int num =Integer.parseInt(element.select(".lh-condensed").select(".opened-by").get(0).childNode(0).toString().split(" ")[1].split("#")[1]);
                String author = element.select(".lh-condensed").select(".opened-by").select(".muted-link").eachText().toString(); //by
                list.add(new GitHubPullRequest(num, title, author));
//              mvn clean verify  System.out.println(num);
//                System.out.println(Integer.parseInt(element.select(".lh-condensed").select(".opened-by").get(0).childNode(0).toString().split(" ")[1].split("#")[1]));

            }
            return list;
        } finally {
            response.close();
        }
    }
}
