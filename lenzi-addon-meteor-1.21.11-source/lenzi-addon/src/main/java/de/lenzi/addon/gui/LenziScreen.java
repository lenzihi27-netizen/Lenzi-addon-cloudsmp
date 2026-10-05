package de.lenzi.addon.gui;

import de.lenzi.addon.modules.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Neonrotes Menü. Linksklick schaltet ein Modul um, Einstellungen findest du im Meteor-GUI unter "Lenzi addon". */
public class LenziScreen extends Screen {

    private static final int NEON = 0xFFFF1133;
    private static final int PW = 150, ROW = 22, HEAD = 26, GAP = 16, TOP = 70;

    private record Group(String label, List<Class<? extends Module>> modules) {}

    private static final List<Group> GROUPS = List.of(
        new Group("Movement", List.of(LenziFlight.class, LenziBoatFly.class, LenziSpeed.class)),
        new Group("Player", List.of(LenziNoFall.class)),
        new Group("Render", List.of(LenziFullbright.class, LenziEsp.class, LenziOreEsp.class))
    );

    public LenziScreen() {
        super(Text.literal("Lenzi addon"));
    }

    @Override public boolean shouldPause() { return false; }

    // Vanilla-Blur abschalten, wir zeichnen den Hintergrund selbst
    @Override public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}

    private static List<Module> modulesOf(Group g) {
        return g.modules().stream()
            .map(c -> (Module) Modules.get().get(c))
            .filter(Objects::nonNull)
            .toList();
    }

    private int panelX(int i) {
        int n = GROUPS.size();
        int total = n * PW + (n - 1) * GAP;
        return (width - total) / 2 + i * (PW + GAP);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float pulse = (float) (Math.sin(System.currentTimeMillis() / 400.0) * 0.5 + 0.5);

        ctx.fill(0, 0, width, height, 0xC4070008);
        drawTitle(ctx, pulse);

        String hoverDesc = null;

        for (int i = 0; i < GROUPS.size(); i++) {
            Group g = GROUPS.get(i);
            List<Module> mods = modulesOf(g);
            int x = panelX(i), y = TOP, w = PW;
            int h = HEAD + mods.size() * ROW + 8;

            glow(ctx, x, y, x + w, y + h, pulse);
            ctx.fill(x, y, x + w, y + h, 0xEE0B0307);
            ctx.fill(x, y, x + w, y + HEAD, 0xFF1C0509);
            ctx.fill(x, y + HEAD - 1, x + w, y + HEAD, NEON);
            border(ctx, x, y, x + w, y + h, NEON);
            ctx.drawCenteredTextWithShadow(textRenderer, g.label().toUpperCase(Locale.ROOT), x + w / 2, y + 9, NEON);

            for (int r = 0; r < mods.size(); r++) {
                Module m = mods.get(r);
                int ry = y + HEAD + 4 + r * ROW;
                boolean hover = mouseX >= x && mouseX < x + w && mouseY >= ry && mouseY < ry + ROW;
                boolean on = m.isActive();

                if (on) {
                    ctx.fill(x + 1, ry, x + w - 1, ry + ROW, 0x45FF1133);
                    ctx.fill(x + 1, ry, x + 4, ry + ROW, NEON);
                } else if (hover) {
                    ctx.fill(x + 1, ry, x + w - 1, ry + ROW, 0x25FFFFFF);
                }
                if (hover) hoverDesc = m.description;

                ctx.drawTextWithShadow(textRenderer, m.title, x + 10, ry + 7, on ? 0xFFFF5470 : 0xFFB9A3A9);
            }
        }

        if (hoverDesc != null) {
            ctx.drawCenteredTextWithShadow(textRenderer, hoverDesc, width / 2, height - 34, 0xFFFFFFFF);
        }
        ctx.drawCenteredTextWithShadow(textRenderer,
            "Linksklick: an/aus   |   Einstellungen: Meteor-GUI > Lenzi addon   |   ESC: schließen",
            width / 2, height - 18, 0xFF8A6068);
    }

    private void drawTitle(DrawContext ctx, float pulse) {
        String title = "Lenzi addon";
        float scale = 2.6f;
        int tw = textRenderer.getWidth(title);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(width / 2f, 16f);
        ctx.getMatrices().scale(scale, scale);
        int glowAlpha = (int) (40 + pulse * 40);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                ctx.drawText(textRenderer, title, -tw / 2 + dx, dy, (glowAlpha << 24) | 0xFF1133, false);
            }
        }
        ctx.drawText(textRenderer, title, -tw / 2, 0, NEON, false);
        ctx.getMatrices().popMatrix();

        int lineW = (int) (tw * scale);
        ctx.fill(width / 2 - lineW / 2, 44, width / 2 + lineW / 2, 45, NEON);
    }

    private static void glow(DrawContext c, int x1, int y1, int x2, int y2, float pulse) {
        for (int i = 8; i >= 1; i--) {
            int a = Math.min(255, (int) ((6 + pulse * 6) * (9 - i) / 2.0));
            c.fill(x1 - i, y1 - i, x2 + i, y2 + i, (a << 24) | 0xFF1133);
        }
    }

    private static void border(DrawContext c, int x1, int y1, int x2, int y2, int col) {
        c.fill(x1, y1, x2, y1 + 1, col);
        c.fill(x1, y2 - 1, x2, y2, col);
        c.fill(x1, y1, x1 + 1, y2, col);
        c.fill(x2 - 1, y1, x2, y2, col);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            double mx = click.x(), my = click.y();
            for (int i = 0; i < GROUPS.size(); i++) {
                List<Module> mods = modulesOf(GROUPS.get(i));
                int x = panelX(i);
                for (int r = 0; r < mods.size(); r++) {
                    int ry = TOP + HEAD + 4 + r * ROW;
                    if (mx >= x && mx < x + PW && my >= ry && my < ry + ROW) {
                        mods.get(r).toggle();
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }
}
