package com.github.hcsp.http;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

        @Override
        public String toString() {
            return String.format("PullRequest[%d] [%s] by [%s]", number, title, author);
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        final String token = "b1293a5899baa10f3f678b9ffabef5c26b6ca506";
        List<GitHubPullRequest> result = new ArrayList<>();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.github.com/graphql");
        StringEntity entity = new StringEntity(buildBody(repo));
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", String.format("bearer %s", token));
        CloseableHttpResponse response = client.execute(httpPost);

        JsonNode json = new ObjectMapper().readTree(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        Iterator<JsonNode> iterator = json.get("data").get("repository").get("pullRequests").get("nodes").elements();
        while (iterator.hasNext()) {
            JsonNode pull = iterator.next();
            result.add(new GitHubPullRequest(
                    pull.get("number").asInt(),
                    pull.get("title").asText(),
                    pull.get("author").get("login").asText())
            );
        }

        response.close();
        client.close();
        return result;
    }

    private static String buildBody(String repo) {
        final int PAGE_SIZE = 25;
        final String query = String.join(System.getProperty("line.separator"),
                "query {",
                "    repository (owner: \"%s\", name: \"%s\") {",
                "        pullRequests (first: %d, states: OPEN, orderBy: { field: CREATED_AT, direction: DESC }) {",
                "            nodes { number, title, author { login } }",
                "       }",
                "    }",
                "}"
        );

        try {
            String[] params = repo.split("/");
            return new ObjectMapper().writeValueAsString(new HashMap<String, String>() {{
                put("query", String.format(query, params[0], params[1], PAGE_SIZE));
            }});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
