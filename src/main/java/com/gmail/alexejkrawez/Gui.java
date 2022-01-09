package com.gmail.alexejkrawez;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Gui {

    private TrayIcon trayIcon;

    public Gui() {
        try {
            // Получаем доступ к трэю системы (правый нижний угол экрана)
            SystemTray systemTray = SystemTray.getSystemTray();
            // Получаем иконку нашего приложения
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/logo.png"));
            trayIcon = new TrayIcon(image, "Github helper");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Github helper");

            systemTray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

    }

    // Отображение интерфейса
    public void setMenu(String login, java.util.List<RepositoryDescription> repos) {
        PopupMenu popup = new PopupMenu();

        // Создаём ссылку на аккаунт
        MenuItem accauntMI = new MenuItem(login);
        // Открывать браузер при клике
        accauntMI.addActionListener(e -> openInBrowser("https://github.com/" + login));

        // Создаём ссылку на аккаунт
        MenuItem notificationMI = new MenuItem("notifications");
        // Открывать браузер при клике
        notificationMI.addActionListener(e -> openInBrowser("https://github.com/notifications"));
        
        // Список репозиториев
        Menu repositoriesMI = new Menu("repositories");
        repos
            .forEach(repo -> {
                // Ссылка на репозиторий
                // Выпадающее меню либо один репозиторий
                String name = repo.getPrs().size() > 0
                    ? String.format("(%d) %s", repo.getPrs().size(), repo.getName())
                    : repo.getName();

                Menu repoSM = new Menu(name);

                MenuItem openInBrowser = new MenuItem("Open in browser");
                openInBrowser.addActionListener(e ->
                    openInBrowser(repo.getRepository().getHtmlUrl().toString()));

                repoSM.add(openInBrowser);

                if (repo.getPrs().size() > 0) {
                    repoSM.addSeparator();
                    
                }

                repo.getPrs()
                    .forEach(pr -> {
                        MenuItem prMI = new MenuItem(pr.getTitle());
                        openInBrowser.addActionListener(e ->
                            openInBrowser(pr.getHtmlUrl().toString()));

                        repoSM.add(prMI);
                    });

                repositoriesMI.add(repoSM);
                // Список пуллреквестов
            });


        // Новые кнопочки для перехода на гитхаб + разделители
        popup.add(accauntMI);
        popup.addSeparator();
        popup.add(notificationMI);
        popup.add(repositoriesMI);

        trayIcon.setPopupMenu(popup);

        
    }

    // Открытие в браузере
    public void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // Отображение нотификаций
    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);

    }

}
