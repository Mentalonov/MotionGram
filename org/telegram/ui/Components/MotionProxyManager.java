package org.telegram.ui.Components;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class MotionProxyManager {
    
    public static class MTMProxy {
        public String server;
        public int port;
        public String secret;
        public String name;
        public long ping = -1;

        public MTMProxy(String server, int port, String secret, String name) {
            this.server = server;
            this.port = port;
            this.secret = secret;
            this.name = name;
        }
    }

    private static boolean isMonitoring = false;
    private static MTMProxy currentActiveProxy = null;
    private static List<MTMProxy> globalProxyPool = null;

    private static void updateAndroidNotificationBar(String proxyName, long ping) {
        System.out.println("\n[ОБНОВЛЕНИЕ ШТОРКИ ANDROID]");
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│  MotionGram: Активен фоновый подбор прокси              │");
        System.out.println("│  Подключено: " + String.format("%-18s", proxyName) + " | Живой Пинг: " + String.format("%-6s", ping + " мс") + "   │");
        System.out.println("│  [ 🔄 Нажмите стрелочку для принудительного перебора ] │");
        System.out.println("└────────────────────────────────────────────────────────┘");
    }

    public static long checkProxyPing(String host, int port) {
        long startTime = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 600);
            return System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void triggerManualReSelection() {
        if (globalProxyPool == null || globalProxyPool.isEmpty()) return;
        
        System.out.println("\n[!] Юзер нажал стрелочку в шторке: Запускаю принудительный перебор...");
        
        boolean foundNew = false;
        for (MTMProxy proxy : globalProxyPool) {
            if (currentActiveProxy != null && proxy.server.equals(currentActiveProxy.server)) continue;
            
            long p = checkProxyPing(proxy.server, proxy.port);
            if (p > 0 && p <= 200) {
                currentActiveProxy = proxy;
                currentActiveProxy.ping = p;
                foundNew = true;
                updateAndroidNotificationBar(currentActiveProxy.name, currentActiveProxy.ping);
                break;
            }
        }
        
        if (!foundNew) {
            System.out.println("[MotionProxy-Daemon] Быстрых альтернатив до 200 мс сейчас нет. Держу текущий.");
        }
    }

    public static void startAutonomousWatchdog(final List<MTMProxy> proxyPool) {
        globalProxyPool = proxyPool;
        if (isMonitoring) return;
        isMonitoring = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMonitoring) {
                    try {
                        // ИСПРАВЛЕНО: Заменили питоновский None на джавовый null
                        if (currentActiveProxy == null) {
                            for (MTMProxy proxy : globalProxyPool) {
                                long p = checkProxyPing(proxy.server, proxy.port);
                                if (p > 0 && p <= 200) {
                                    currentActiveProxy = proxy;
                                    currentActiveProxy.ping = p;
                                    break;
                                }
                            }
                        } else {
                            long p = checkProxyPing(currentActiveProxy.server, currentActiveProxy.port);
                            if (p > 0) {
                                currentActiveProxy.ping = p;
                            } else {
                                currentActiveProxy = null;
                                continue;
                            }
                        }

                        if (currentActiveProxy != null) {
                            updateAndroidNotificationBar(currentActiveProxy.name, currentActiveProxy.ping);
                        }
                        
                        Thread.sleep(3000);
                        
                    } catch (Exception e) {}
                }
            }
        }).start();
    }
}
