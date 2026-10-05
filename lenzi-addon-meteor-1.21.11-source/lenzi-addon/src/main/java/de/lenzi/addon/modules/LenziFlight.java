package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import de.lenzi.addon.util.MoveUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziFlight extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed").description("Horizontale Geschwindigkeit (Blöcke pro Tick).")
        .defaultValue(0.6).min(0.1).sliderMax(3.0).build());

    private final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed").description("Geschwindigkeit nach oben/unten.")
        .defaultValue(0.4).min(0.1).sliderMax(2.0).build());

    private final Setting<Boolean> smooth = sgGeneral.add(new BoolSetting.Builder()
        .name("smooth").description("Weiches Beschleunigen/Bremsen statt abruptem Ruck.")
        .defaultValue(true).build());

    private final Setting<Boolean> antiKick = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-kick").description("Kurzer Absink-Impuls gegen den Vanilla-'Floating'-Kick.")
        .defaultValue(true).build());

    private int ticks;

    public LenziFlight() {
        super(LenziAddon.CATEGORY, "lenzi-flight", "Freies Fliegen. Leertaste hoch, Shift runter.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity p = mc.player;
        if (p == null || p.hasVehicle()) return;

        Vec3d target = MoveUtil.horizontal(speed.get());
        double ty = MoveUtil.vertical(verticalSpeed.get());

        double x = target.x, y = ty, z = target.z;
        if (smooth.get()) {
            Vec3d cur = p.getVelocity();
            x = MoveUtil.lerp(cur.x, target.x, 0.35);
            z = MoveUtil.lerp(cur.z, target.z, 0.35);
            y = MoveUtil.lerp(cur.y, ty, 0.35);
        }

        if (antiKick.get() && ty == 0) {
            if (++ticks >= 30) { y = -0.04; ticks = 0; }
        } else {
            ticks = 0;
        }

        p.setVelocity(x, y, z);
        p.fallDistance = 0;
    }
}
