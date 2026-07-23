package org.telegram.ui.Components;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MotionOsintTab {
    private static String fanstatUrl = "https://t.me"; 

    public static String runSherlockLocal(String username) {
        if (username == null || username.trim().isEmpty()) return "error: username required";
        StringBuilder output = new StringBuilder("arch-osint: [sherlock] scanning '" + username + "'...\n");
        try {
            Process process = Runtime.getRuntime().exec("python -m sherlock " + username + " --timeout 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[+]")) {
                    output.append(line).append("\n");
                    count++;
                }
            }
            if (count == 0) output.append("No active profiles found.");
        } catch (Exception e) {
            return "error: sherlock execution failed. make sure 'pip install sherlock' is done in termux.";
        }
        return output.toString();
    }

    public static String runHoleheLocal(String email) {
        if (email == null || !email.contains("@")) return "error: valid email required";
        StringBuilder output = new StringBuilder("arch-osint: [holehe] checking email: " + email + "\n");
        try {
            Process process = Runtime.getRuntime().exec("holehe " + email + " --only-used");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            return "error: holehe failed. verify 'pip install holehe' in terminal.";
        }
        return output.toString();
    }

    public static String getOsintWebLink(String toolType) {
        if (toolType.equalsIgnoreCase("maltego")) {
            return "https://maltego.com";
        } else if (toolType.equalsIgnoreCase("fanstat")) {
            return fanstatUrl;
        }
        return "unknown tool";
    }

    public static void updateFanstatUrl(String newUrl) {
        if (newUrl != null && newUrl.startsWith("http")) {
            fanstatUrl = newUrl;
        }
    }
}
