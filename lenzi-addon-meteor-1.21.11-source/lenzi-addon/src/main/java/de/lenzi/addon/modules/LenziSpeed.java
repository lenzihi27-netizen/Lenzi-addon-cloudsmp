package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import de.lenzi.addon.util.MoveUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziSpeed extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed").description("Geschwindigkeit am Boden (Blöcke pro Tick). Sprinten ist ca. 0.28.")
        .defaultValue(0.32).min(0.1).sliderMax(1.5).build());

    public LenziSpeed() {
        super(LenziAddon.CATEGORY, "lenzi-speed", "Schneller am Boden laufen.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity p = mc.player;
        if (p == null || p.hasVehicle() || Modules.get().isActive(LenziFlight.class)) return;

        Vec3d h = MoveUtil.horizontal(speed.get());
        if (h.lengthSquared() == 0) return;
        p.setVelocity(h.x, p.getVelocity().y, h.z);
    }
}
