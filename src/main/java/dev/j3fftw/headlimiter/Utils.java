package dev.j3fftw.headlimiter;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class Utils {

    private static final String BYPASS_PERMISSION = "headlimiter.bypass";

    private Utils() {
    }

    public static boolean canBypass(@Nonnull Player player) {
        return player.hasPermission(BYPASS_PERMISSION);
    }
}
