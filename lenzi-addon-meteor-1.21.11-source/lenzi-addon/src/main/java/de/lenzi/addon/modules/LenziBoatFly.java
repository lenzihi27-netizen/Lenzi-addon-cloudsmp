package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import de.lenzi.addon.util.MoveUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziBoatFly extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed").description("Horizontale Geschwindigkeit des Boots.")
        .defaultValue(0.8).min(0.1).sliderMax(3.0).build());

    private final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed").description("Steig-/Sinkgeschwindigkeit.")
        .defaultValue(0.4).min(0.1).sliderMax(2.0).build());

    private final Setting<Boolean> follow = sgGeneral.add(new BoolSetting.Builder()
        .name("follow-yaw").description("Boot dreht sich mit deiner Blickrichtung.")
        .defaultValue(true).build());

    private Entity last;

    public LenziBoatFly() {
        super(LenziAddon.CATEGORY, "lenzi-boat-fly", "Boote fliegen lassen, ohne Schwerkraft.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        Entity v = p.getVehicle();
        if (v == null || !v.getType().isIn(EntityTypeTags.BOATS)) {
            release();
            return;
        }

        last = v;
        v.setNoGravity(true);
        if (follow.get()) v.setYaw(p.getYaw());

        Vec3d h = MoveUtil.horizontal(speed.get());
        v.setVelocity(h.x, MoveUtil.vertical(verticalSpeed.get()), h.z);
        v.fallDistance = 0;
    }

    @Override
    public void onDeactivate() {
        release();
    }

    private void release() {
        if (last != null) {
            last.setNoGravity(false);
            last = null;
        }
    }
}
