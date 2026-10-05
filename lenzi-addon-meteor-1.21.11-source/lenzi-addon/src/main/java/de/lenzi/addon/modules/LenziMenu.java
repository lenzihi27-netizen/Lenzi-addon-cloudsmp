package de.lenzi.addon.modules;

import de.lenzi.addon.LenziAddon;
import de.lenzi.addon.gui.LenziScreen;
import meteordevelopment.meteorclient.systems.modules.Module;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LenziMenu extends Module {
    public LenziMenu() {
        super(LenziAddon.CATEGORY, "lenzi-menu", "Öffnet das neonrote Lenzi-addon-Menü. Lege dir eine Taste darauf.");
    }

    @Override
    public void onActivate() {
        mc.setScreen(new LenziScreen());
        toggle(); // sofort wieder aus, das Modul dient nur als Öffner
    }
}
