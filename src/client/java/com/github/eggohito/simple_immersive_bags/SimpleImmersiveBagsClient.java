package com.github.eggohito.simple_immersive_bags;

import com.github.eggohito.simple_immersive_bags.client.renderer.BackpackArmorRenderer;
import com.github.eggohito.simple_immersive_bags.client.screen.BagScreen;
import com.github.eggohito.simple_immersive_bags.networking.SimpleImmersiveBagsS2CPackets;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.DyeableItem;
import net.minecraft.util.Identifier;

public class SimpleImmersiveBagsClient implements ClientModInitializer {

	public static final Identifier VANILLA_RECIPE_BOOK_WIDGET_ID = new Identifier("minecraft:widgets/recipe_book");

	public static final BackpackArmorRenderer BACKPACK_ARMOR_RENDERER = new BackpackArmorRenderer(SimpleImmersiveBags.id("textures/armor/backpack.png"));
	public static final BackpackArmorRenderer ENDER_BACKPACK_ARMOR_RENDERER = new BackpackArmorRenderer(SimpleImmersiveBags.id("textures/armor/ender_backpack.png"));

	@Override
	public void onInitializeClient() {

		ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
			tintIndex == 0 && stack.getItem() instanceof DyeableItem dyeableItem ? dyeableItem.getColor(stack) : 0xFFFFFFF,
			SimpleImmersiveBags.BACKPACK
		);

		ArmorRenderer.register(BACKPACK_ARMOR_RENDERER, SimpleImmersiveBags.BACKPACK);
		ArmorRenderer.register(ENDER_BACKPACK_ARMOR_RENDERER, SimpleImmersiveBags.ENDER_BACKPACK);

		HandledScreens.register(BagScreenHandlerTypes.GENERIC_BAG, BagScreen::new);
		SimpleImmersiveBagsS2CPackets.registerAll();

	}

}