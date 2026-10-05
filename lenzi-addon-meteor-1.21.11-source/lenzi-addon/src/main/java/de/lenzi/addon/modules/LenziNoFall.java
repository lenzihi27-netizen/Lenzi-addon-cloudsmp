package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziNoFall extends Module {
    public LenziNoFall() {
        super(LenziAddon.CATEGORY, "lenzi-no-fall", "Kein Fallschaden.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.getNetworkHandler() == null) return;

        if (p.fallDistance > 2.5) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, p.horizontalCollision));
            p.fallDistance = 0;
        }
    }
}
