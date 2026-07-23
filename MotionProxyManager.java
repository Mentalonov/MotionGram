package org.telegram.ui.Components;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MotionProxyManager {
    
    public static class MTMProxy {
        public final String server;
        public final int port;
        public final String secret;
        public final String name;
        public volatile long ping = -1;

        public MTMProxy(String server, int port, String secret, String name) {
            this.server = server; 
            this.port = port; 
            this.secret = secret; 
            this.name = name;
        }
    }

    private static final AtomicBoolean isMonitoring = new AtomicBoolean(false);
    private static final Object lock = new Object();
    
    private static volatile MTMProxy currentActiveProxy = null;
    private static final List<MTMProxy> globalProxyPool = new CopyOnWriteArrayList<>();
    private static Thread watchdogThread = null;

    private static void updateAndroidNotificationBar(String name, long ping) {
        System.out.println("\n[ШТОРКА] Подключено: " + name + " | Пинг: " + ping + " мс");
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (globalProxyPool.isEmpty()) return;
                    MTMProxy localActive = currentActiveProxy;
                    
                    for (MTMProxy proxy : globalProxyPool) {
                        if (localActive != null && proxy.server.equals(localActive.server)) continue;
                        long p = checkProxyPing(proxy.server, proxy.port);
                        if (p > 0 && p <= 200) {
                            proxy.ping = p;
                            currentActiveProxy = proxy;
                            updateAndroidNotificationBar(proxy.name, p);
                            break;
                        }
                    }
                }
            }
        }).start();
    }

    public static void stopAutonomousWatchdog() {
        isMonitoring.set(false);
        if (watchdogThread != null) {
            watchdogThread.interrupt();
            watchdogThread = null;
        }
    }

    public static void startAutonomousWatchdog(final List<MTMProxy> proxyPool) {
        if (proxyPool == null) return;
        
        globalProxyPool.clear();
        globalProxyPool.addAll(proxyPool);

        if (!isMonitoring.compareAndSet(false, true)) return;

        watchdogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMonitoring.get()) {
                    try {
                        MTMProxy localActive = currentActiveProxy;

                        if (localActive == null) {
                            for (MTMProxy proxy : globalProxyPool) {
                                long p = checkProxyPing(proxy.server, proxy.port);
                                if (p > 0 && p <= 200) { 
                                    proxy.ping = p;
                                    currentActiveProxy = proxy; 
                                    break; 
                                }
                            }
                        } else {
                            long p = checkProxyPing(localActive.server, localActive.port);
                            if (p > 0) {
                                localActive.ping = p;
                            } else { 
                                localActive.ping = -1;
                                currentActiveProxy = null; 
                            }
                        }

                        MTMProxy updatedActive = currentActiveProxy;
                        if (updatedActive != null) {
                            updateAndroidNotificationBar(updatedActive.name, updatedActive.ping);
                        }

                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        // Защита рантайма мессенджера
                    }
                }
            }
        }, "MotionWatchdog-Thread");
        
        watchdogThread.start();
    }
}
