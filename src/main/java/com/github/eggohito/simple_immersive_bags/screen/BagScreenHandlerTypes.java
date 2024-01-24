package com.github.eggohito.simple_immersive_bags.screen;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class BagScreenHandlerTypes {

    public static ScreenHandlerType<BagScreenHandler> GENERIC_BAG;

    public static void registerAll() {
        GENERIC_BAG = registerExtended(SimpleImmersiveBags.id("generic_bag"), BagScreenHandler::create);
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> registerExtended(Identifier id, ExtendedScreenHandlerType.ExtendedFactory<T> extendedFactory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ExtendedScreenHandlerType<>(extendedFactory));
    }

}
