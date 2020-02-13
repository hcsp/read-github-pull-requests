# HTTP实战: 获取指定仓库中的Pull request信息

请完成[`Crawler`](https://github.com/hcsp/read-github-pull-requests/blob/master/src/main/java/com/github/hcsp/http/Crawler.java)中的程序，爬取给定的GitHub仓库中的Pull request信息：

- 编号
- 标题
- 作者的GitHub ID

如图所示：

![1](https://github.com/hcsp/read-github-pull-requests/blob/master/pull-screenshot.png)

你可以自由选择你喜欢的任何第三方库，我们也不限制你使用GitHub的API。完成一件事情的做法有很多种，我们非常鼓励你尝试各种不同的方案。你可以从以下方法中挑选一种完成挑战：

- 按照课上所学，直接获取页面的HTML，解析之。
- 自行阅读GitHub的API文档，调用API获取并解析JSON。
- 自行搜索并使用第三方的GitHub SDK。

我们希望能锻炼你自己搜索、阅读文档的能力——这对一个工程师是必不可少的技能，因此我们没有列出所有的细节。但是，我们仍然希望在你遇到解决不了的问题时能多思考、多问。祝你好运！

在提交Pull Request之前，你应当在本地确保所有代码已经编译通过，并且通过了测试(`mvn clean verify`)

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/main/java/com/github/hcsp/http/Crawler.java](https://github.com/hcsp/read-github-pull-requests/blob/master/src/main/java/com/github/hcsp/http/Crawler.java)
- [pom.xml](https://github.com/hcsp/read-github-pull-requests/blob/master/pom.xml)
-----


完成题目有困难？不妨来看看[写代码啦的相应课程](https://xiedaimala.com/tasks/661cd7ab-7fea-47d0-8e11-555d6fca751d)吧！

回到[写代码啦的题目](https://xiedaimala.com/tasks/661cd7ab-7fea-47d0-8e11-555d6fca751d/quizzes/6c87ef57-7f06-4af2-9112-86dd27ff099d)，继续挑战！
