package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Zeigt Erze (auch Deepslate-Varianten) in der Umgebung durch Wände.
 * Es werden nur Blöcke angezeigt, die der Server tatsächlich an den Client schickt.
 */
public class LenziOreEsp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgOres = settings.createGroup("Erze");

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range").description("Scan-Radius in Blöcken.").defaultValue(24).min(8).sliderMax(48).build());
    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("scan-interval").description("Ticks zwischen zwei Scans.").defaultValue(40).min(10).sliderMax(200).build());
    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").description("Wie die Boxen gezeichnet werden.").defaultValue(ShapeMode.Both).build());

    private final Setting<Boolean> diamond = ore("diamond", true);
    private final Setting<Boolean> debris = ore("ancient-debris", true);
    private final Setting<Boolean> emerald = ore("emerald", true);
    private final Setting<Boolean> gold = ore("gold", true);
    private final Setting<Boolean> iron = ore("iron", false);
    private final Setting<Boolean> redstone = ore("redstone", false);
    private final Setting<Boolean> lapis = ore("lapis", false);
    private final Setting<Boolean> copper = ore("copper", false);
    private final Setting<Boolean> coal = ore("coal", false);

    private static final Color DIAMOND = new Color(0, 255, 255);
    private static final Color DEBRIS = new Color(160, 90, 40);
    private static final Color EMERALD = new Color(0, 255, 90);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color IRON = new Color(225, 200, 180);
    private static final Color REDSTONE = new Color(255, 17, 51);
    private static final Color LAPIS = new Color(40, 80, 255);
    private static final Color COPPER = new Color(230, 120, 60);
    private static final Color COAL = new Color(120, 120, 120);

    private record Hit(BlockPos pos, Color line, Color side) {}

    private final List<Hit> hits = new ArrayList<>();
    private int timer;

    public LenziOreEsp() {
        super(LenziAddon.CATEGORY, "lenzi-ore-esp", "Erze (inkl. Deepslate) durch Wände sehen.");
    }

    private Setting<Boolean> ore(String name, boolean def) {
        return sgOres.add(new BoolSetting.Builder().name(name).description("Anzeigen: " + name).defaultValue(def).build());
    }

    @Override
    public void onActivate() {
        timer = 0;
        hits.clear();
    }

    @Override
    public void onDeactivate() {
        hits.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null || mc.player == null) return;
        if (--timer > 0) return;
        timer = interval.get();
        scan();
    }

    private void scan() {
        hits.clear();
        BlockPos c = mc.player.getBlockPos();
        int r = range.get();
        BlockPos.Mutable m = new BlockPos.Mutable();

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    m.set(c.getX() + dx, c.getY() + dy, c.getZ() + dz);
                    BlockState s = mc.world.getBlockState(m);
                    if (s.isAir()) continue;

                    Color col = colorFor(s);
                    if (col == null) continue;

                    hits.add(new Hit(m.toImmutable(), col, new Color(col.r, col.g, col.b, 40)));
                    if (hits.size() >= 3000) return;
                }
            }
        }
    }

    private Color colorFor(BlockState s) {
        if (diamond.get() && s.isIn(BlockTags.DIAMOND_ORES)) return DIAMOND;
        if (debris.get() && s.isOf(Blocks.ANCIENT_DEBRIS)) return DEBRIS;
        if (emerald.get() && s.isIn(BlockTags.EMERALD_ORES)) return EMERALD;
        if (gold.get() && s.isIn(BlockTags.GOLD_ORES)) return GOLD;
        if (iron.get() && s.isIn(BlockTags.IRON_ORES)) return IRON;
        if (redstone.get() && s.isIn(BlockTags.REDSTONE_ORES)) return REDSTONE;
        if (lapis.get() && s.isIn(BlockTags.LAPIS_ORES)) return LAPIS;
        if (copper.get() && s.isIn(BlockTags.COPPER_ORES)) return COPPER;
        if (coal.get() && s.isIn(BlockTags.COAL_ORES)) return COAL;
        return null;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        for (Hit h : hits) {
            event.renderer.box(h.pos(), h.side(), h.line(), shapeMode.get(), 0);
        }
    }
}
