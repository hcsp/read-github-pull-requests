package com.github.hcsp.http;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        return request(buildBody(repo));
    }

    private static String buildBody(String repo) {
        final var PAGE_SIZE = 25;
        final var query = String.join(System.getProperty("line.separator"),
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

    private static List<GitHubPullRequest> request(String body) {
        final var result = new ArrayList<GitHubPullRequest>();
        final var token = "eb75cc7cb7317d4d7e1a4cacf2a7e90036edaa7d";

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/graphql"))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("bearer %s", token))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var json = new ObjectMapper().readTree(response.body());
            var iterator = json.get("data").get("repository").get("pullRequests").get("nodes").elements();
            while (iterator.hasNext()) {
                var pull = iterator.next();
                result.add(new GitHubPullRequest(
                        pull.get("number").asInt(),
                        pull.get("title").asText(),
                        pull.get("author").get("login").asText())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
