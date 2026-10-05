package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziFullbright extends Module {
    public LenziFullbright() {
        super(LenziAddon.CATEGORY, "lenzi-fullbright", "Immer volle Helligkeit (clientseitiger Nachtsicht-Effekt).");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
