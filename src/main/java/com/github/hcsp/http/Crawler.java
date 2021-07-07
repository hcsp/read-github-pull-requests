package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");

        RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        httpGet.setConfig(defaultConfig);        // 这两行修改请求中的cookie策略(CookieSpecs) 来避免invalid cookies header 异常

        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> result = new ArrayList<>();
        try {
            System.out.println(response1.getStatusLine());    // 打印 http 状态码
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String theString = IOUtils.toString(entity1.getContent(), StandardCharsets.UTF_8);   // 将实体转 String

            String html = "<html><head><title>First parse</title></head>"             // 开始解析 html
                    + "<body><p>Parsed HTML into a doc.</p></body></html>";
            Document doc = Jsoup.parse(theString);
            ArrayList<Element> selectResult = doc.select(".Box-row");    // 注意类名前面有个点！！！！

            for (Element element : selectResult) {
                String title = element.child(0).child(1).child(0).text();
                int number = Integer.parseInt(element.child(0).child(1).select(".mt-1").get(0).child(0).text().substring(1, 6));
                // 此处有坑，child 位置不一样，需要再次调用select筛选
                String author = element.child(0).child(1).select(".mt-1").get(0).child(0).child(1).text();
                result.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();        // 这边都 close了，所以代码要放上面
        }
        return result;
    }

    public static void main(String[] args) throws IOException {      // 调试用
        List result = getFirstPageOfPullRequests("gradle/gradle");
    }
}
