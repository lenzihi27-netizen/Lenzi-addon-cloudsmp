package de.lenzi.addon.util;

import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public final class MoveUtil {
    private MoveUtil() {}

    /** Horizontale Bewegung in Blickrichtung (WASD), Länge = speed. */
    public static Vec3d horizontal(double speed) {
        double f = (mc.options.forwardKey.isPressed() ? 1 : 0) - (mc.options.backKey.isPressed() ? 1 : 0);
        double s = (mc.options.leftKey.isPressed() ? 1 : 0) - (mc.options.rightKey.isPressed() ? 1 : 0);
        if (f == 0 && s == 0) return Vec3d.ZERO;

        double rad = Math.toRadians(mc.player.getYaw());
        double sin = Math.sin(rad), cos = Math.cos(rad);
        double x = -sin * f + cos * s;
        double z = cos * f + sin * s;
        double len = Math.sqrt(x * x + z * z);
        return new Vec3d(x / len * speed, 0, z / len * speed);
    }

    /** Leertaste = hoch, Shift = runter. */
    public static double vertical(double speed) {
        double y = 0;
        if (mc.options.jumpKey.isPressed()) y += speed;
        if (mc.options.sneakKey.isPressed()) y -= speed;
        return y;
    }

    public static double lerp(double from, double to, double t) {
        return from + (to - from) * t;
    }
}
