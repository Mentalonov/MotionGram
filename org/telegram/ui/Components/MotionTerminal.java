package org.telegram.ui.Components;

import java.util.HashMap;
import java.util.Map;

public class MotionTerminal {

    private static final Map<String, Float> config = new HashMap<>();
    static {
        config.put("jelly_stiffness", 180f);
        config.put("jelly_damping", 0.45f);
        config.put("blur_radius", 15f);
    }

    public static String executeCommand(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) return "";

        // Имитируем установку тулзов через пакетный менеджер Arch
        if (inputLine.equals("pacman -S osint")) {
            return "resolving dependencies...\n" +
                   "Packages (4): sherlock-git  holehe-python  maltego-core  fanstat-mirror\n" +
                   ":: Proceed with installation? [Y/n] y\n" +
                   "(1/4) checking keys\n(2/4) installing components\n" +
                   "arch-success: OSINT subsystem initialized. Use 'sherlock [nick]' or 'holehe [email]'.";
        }

        // Вызов Шерлока
        if (inputLine.startsWith("sherlock ")) {
            String targetNick = inputLine.replace("sherlock ", "").trim();
            return MotionOsintTab.runSherlockLocal(targetNick);
        }

        // Вызов Holehe
        if (inputLine.startsWith("holehe ")) {
            String targetEmail = inputLine.replace("holehe ", "").trim();
            return MotionOsintTab.runHoleheLocal(targetEmail);
        }

        // Получение ссылок на веб-утилиты
        if (inputLine.equals("osint -links")) {
            return "--- External OSINT Platforms ---\n" +
                   "Maltego Web Search: " + MotionOsintTab.getOsintWebLink("maltego") + "\n" +
                   "Active FanStat Mirror: " + MotionOsintTab.getOsintWebLink("fanstat");
        }

        // Обновление Фанстата на лету
        if (inputLine.startsWith("motion -update-fanstat ")) {
            String newUrl = inputLine.replace("motion -update-fanstat ", "").trim();
            MotionOsintTab.updateFanstatUrl(newUrl);
            return "arch-success: FanStat mirror updated to -> " + newUrl;
        }

        // Дефолтная справка
        return "motion-shell: command not found: '" + inputLine + "'. Type 'pacman -S osint' to unlock tools.";
    }
}
