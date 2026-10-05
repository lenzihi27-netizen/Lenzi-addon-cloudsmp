package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziEsp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Farben");

    private final Setting<Boolean> players = sgGeneral.add(new BoolSetting.Builder()
        .name("players").description("Spieler anzeigen.").defaultValue(true).build());
    private final Setting<Boolean> mobs = sgGeneral.add(new BoolSetting.Builder()
        .name("mobs").description("Mobs und Tiere anzeigen.").defaultValue(false).build());
    private final Setting<Boolean> items = sgGeneral.add(new BoolSetting.Builder()
        .name("items").description("Gedroppte Items anzeigen.").defaultValue(false).build());
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range").description("Maximale Entfernung in Blöcken.").defaultValue(128).min(8).sliderMax(256).build());
    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").description("Wie die Boxen gezeichnet werden.").defaultValue(ShapeMode.Both).build());

    private final Setting<SettingColor> playerColor = sgColors.add(new ColorSetting.Builder()
        .name("player-color").description("Farbe für Spieler.").defaultValue(new SettingColor(255, 17, 51, 255)).build());
    private final Setting<SettingColor> mobColor = sgColors.add(new ColorSetting.Builder()
        .name("mob-color").description("Farbe für Mobs.").defaultValue(new SettingColor(255, 170, 0, 255)).build());
    private final Setting<SettingColor> itemColor = sgColors.add(new ColorSetting.Builder()
        .name("item-color").description("Farbe für Items.").defaultValue(new SettingColor(0, 255, 255, 255)).build());

    public LenziEsp() {
        super(LenziAddon.CATEGORY, "lenzi-esp", "Spieler, Mobs und Items als Boxen durch Wände.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;

            SettingColor c = pick(e);
            if (c == null) continue;
            if (mc.player.distanceTo(e) > range.get()) continue;

            double x = MathHelper.lerp(event.tickDelta, e.lastRenderX, e.getX()) - e.getX();
            double y = MathHelper.lerp(event.tickDelta, e.lastRenderY, e.getY()) - e.getY();
            double z = MathHelper.lerp(event.tickDelta, e.lastRenderZ, e.getZ()) - e.getZ();

            Box b = e.getBoundingBox();
            Color side = new Color(c.r, c.g, c.b, 40);
            event.renderer.box(x + b.minX, y + b.minY, z + b.minZ,
                               x + b.maxX, y + b.maxY, z + b.maxZ,
                               side, c, shapeMode.get(), 0);
        }
    }

    private SettingColor pick(Entity e) {
        if (e instanceof PlayerEntity) return players.get() ? playerColor.get() : null;
        if (e instanceof MobEntity) return mobs.get() ? mobColor.get() : null;
        if (e instanceof ItemEntity) return items.get() ? itemColor.get() : null;
        return null;
    }
}
