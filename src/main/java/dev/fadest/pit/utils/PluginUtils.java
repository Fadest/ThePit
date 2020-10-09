package dev.fadest.pit.utils;

import org.bukkit.Bukkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginUtils {

    private static final ExecutorService service = Executors.newCachedThreadPool();

    public static void runAsync(Runnable runnable) {
        if(!Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }

        service.execute(runnable);
    }

}
