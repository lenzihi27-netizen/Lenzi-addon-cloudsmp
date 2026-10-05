package de.lenzi.addon;

import de.lenzi.addon.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LenziAddon extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("Lenzi addon");
    public static final Category CATEGORY = new Category("Lenzi addon");

    @Override
    public void onInitialize() {
        LOG.info("Lenzi addon geladen");

        Modules.get().add(new LenziMenu());
        Modules.get().add(new LenziFlight());
        Modules.get().add(new LenziBoatFly());
        Modules.get().add(new LenziSpeed());
        Modules.get().add(new LenziNoFall());
        Modules.get().add(new LenziFullbright());
        Modules.get().add(new LenziEsp());
        Modules.get().add(new LenziOreEsp());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "de.lenzi.addon";
    }
}
