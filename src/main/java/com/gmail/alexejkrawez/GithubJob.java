package com.gmail.alexejkrawez;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GithubJob {

    private final GitHub gitHub;
    private final Gui gui = new Gui();
    private final Set<Long> allPrIds = new HashSet<>();

    public GithubJob() {
        try {
            gitHub = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();

            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = gitHub.getMyself();
        String login = myself.getLogin();

        // Получение и вывод информации
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    // Проверка на первый запуск
                    boolean notifyForNewPrs = !allPrIds.isEmpty();

                    // Список новых пуллреквестов
                    HashSet<GHPullRequest> newPrs = new HashSet<>();

                    List<RepositoryDescription> repos = myself.getAllRepositories()
                            .values()
                            .stream()
                            // Получаем список репозиториев из мапа
                            // * Желательно хранить в базе данных
                            .map(repository -> {
                                try {
                                    // Список пуллреквестов
                                    List<GHPullRequest> prs = repository.queryPullRequests()
                                            .list() // делает PagedIterable вместо листа на самом деле
                                            .toList();

                                    // Получаем список id всех пуллреквестов
                                    Set<Long> prIds = prs.stream()
                                            .map(GHPullRequest::getId)
                                            .collect(Collectors.toSet());

                                    // Удаляем все id пуллреквестов, кроме новых. В prIds остаются только новые
                                    prIds.removeAll(allPrIds);
                                    // Перезаписываем новые id пуллреквестов в список известных пуллреквестов
                                    allPrIds.addAll(prIds);

                                    // Набираем в заранее готовый список только новые пуллреквесты
                                    // на основе новых айдишников
                                    prs.forEach(pr -> {
                                        if (prIds.contains(pr.getId())) {
                                            newPrs.add(pr);
                                        }
                                    });
                                    return new RepositoryDescription(
                                            repository.getFullName(), // Содержит кроме имени
                                                                      // также владельца репозитория
                                            repository,
                                            prs);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })

                            .collect(Collectors.toList());

                            gui.setMenu(login, repos);


                    if (notifyForNewPrs) {
                        newPrs.forEach(pr -> {
                            gui.showNotification(
                                    "New PR in " + pr.getRepository().getFullName(),
                                    pr.getTitle());
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 2000); // задержка срабатывания перед первым запуском и задержка интервальная
    }

}
