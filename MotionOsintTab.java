package org.telegram.ui.Components;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MotionOsintTab {
    private static String fanstatUrl = "https://t.me"; 
    private static final String TERMUX_BIN_PATH = "/data/data/com.termux/files/usr/bin/";

    public static String runSherlockLocal(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9_.-]+$")) {
            return "error: invalid username syntax.";
        }

        StringBuilder output = new StringBuilder("arch-osint: [sherlock] scanning '" + username + "'...\n");
        
        List<String> command = new ArrayList<>();
        command.add(TERMUX_BIN_PATH + "python");
        command.add("-m");
        command.add("sherlock");
        command.add(username);
        command.add("--timeout");
        command.add("1");

        return executeOsintProcess(command, output);
    }

    public static String runHoleheLocal(String email) {
        if (email == null || !email.contains("@") || email.contains(" ") || email.contains(";")) {
            return "error: invalid email syntax.";
        }

        StringBuilder output = new StringBuilder("arch-osint: [holehe] checking email: " + email + "\n");
        
        List<String> command = new ArrayList<>();
        command.add(TERMUX_BIN_PATH + "holehe");
        command.add(email);
        command.add("--only-used");

        return executeOsintProcess(command, output);
    }

    private static String executeOsintProcess(List<String> command, StringBuilder output) {
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); 
            
            process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            output.append("\n[PROCESS FINISHED WITH CODE: ").append(exitCode).append("]");
            
        } catch (Exception e) { 
            return "error: process execution failed. Reason: " + e.getMessage(); 
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output.toString();
    }

    public static String getOsintWebLink(String t) { 
        return t.equalsIgnoreCase("maltego") ? "https://maltego.com" : fanstatUrl; 
    }
    
    public static void updateFanstatUrl(String url) { 
        if (url != null && url.startsWith("http")) fanstatUrl = url; 
    }
}
