package com.github.hcsp.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo)
        throws IOException {
        ArrayList<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String requestUrl = "https://api.github.com/repos/" + repo + "/pulls";

        HttpGet httpGet = new HttpGet(requestUrl);


        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            StringBuilder sb = new StringBuilder();
            for (int ch; (ch = entity1.getContent().read()) != -1; ) {
                sb.append((char) ch);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(sb.toString());
            StringBuilder header = new StringBuilder("number, title, userName\n");


            for (int i = 0; i < actualObj.size(); i++) {
                int number1 = Integer.parseInt(actualObj.get(i).get("number").toString());
                String title1 = actualObj.get(i).get("title").textValue();
                String author = actualObj.get(i).get("user").get("login").textValue();
                GitHubPullRequest item = new GitHubPullRequest(number1, title1, author);
                String content = number1 + "," + title1 + "," + author + '\n';
                header.append(content);
                list.add(item);
            }



            FileOutputStream outputStream = new FileOutputStream("/Users/raojj/Desktop/test-java-jrg/read-github-pull-requests/src/main/java/com/github/hcsp/http/test.csv");
            String resuitt = header.toString();
            byte[] fff = resuitt.getBytes();
            outputStream.write(fff);

            outputStream.close();


            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("fdsfdas");

        getFirstPageOfPullRequests("gradle/gradle");


    }
}
