package com.github.hcsp.http;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

public class CrawlerTest {
    @Test
    public void test() throws IOException {
        String repo = "gradle/gradle";
        Crawler.GitHubPullRequest firstPull = Crawler.getFirstPageOfPullRequests(repo).get(0);

        GHPullRequest pull =
                GitHub.connectAnonymously().getRepository(repo).getPullRequest(firstPull.number);

        Assertions.assertEquals(pull.getTitle(), firstPull.title);
        Assertions.assertEquals(pull.getUser().getLogin(), firstPull.author);
    }
}
