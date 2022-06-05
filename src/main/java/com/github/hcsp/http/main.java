package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lilei
 * @date 2022/6/5-@19:15
 */
public class main {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/hcsp/read-github-pull-requests/pulls");
        RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        httpGet.setConfig(defaultConfig);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity1 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is=entity1.getContent();
            String s = IOUtils.toString(is, "UTF-8");
//            System.out.println(s);
            Document document = Jsoup.parse(s);
            Elements select = document.select(".js-issue-row");
            List<Crawler.GitHubPullRequest> gitHubPullRequestList=new ArrayList<>();
            for (Element e:select
            ) {

                String text = e.child(0).child(1).child(0).text();
                String id = e.child(0).child(1).child(0).attr("id").split("_")[1];
                String name = e.child(0).child(1).child(3).child(0).child(1).text();

                gitHubPullRequestList.add(new Crawler.GitHubPullRequest(Integer.parseInt(id),text,name));



            }
            System.out.println(gitHubPullRequestList);
        } finally {
            response.close();
        }
    }
}
