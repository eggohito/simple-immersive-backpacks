package com.github.eggohito.simple_immersive_bags;

import com.github.eggohito.simple_immersive_bags.content.item.DyeableBagItem;
import com.github.eggohito.simple_immersive_bags.content.item.EnderBagItem;
import com.github.eggohito.simple_immersive_bags.content.item.material.BagMaterials;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class SimpleImmersiveBags implements ModInitializer {

	public static final String MOD_NAMESPACE = "simple-immersive-bags";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	public static final String ITEM_CONTAINER_ID = id("item_container").toString();

	public static String VERSION;
	public static int[] SEMANTIC_VERSION;

	public static Item BACKPACK;
	public static Item ENDER_BACKPACK;

	@Override
	public void onInitialize() {

		FabricLoader.getInstance().getModContainer(MOD_NAMESPACE).ifPresent(simpleImmersiveBags -> {

			VERSION = simpleImmersiveBags.getMetadata().getVersion().getFriendlyString().split("[+\\-]")[0];

			String[] splitVersion = VERSION.split("\\.");
			SEMANTIC_VERSION = new int[splitVersion.length];

			for (int i = 0; i < SEMANTIC_VERSION.length; i++) {
				SEMANTIC_VERSION[i] = Integer.parseInt(splitVersion[i]);
			}

		});

		registerAllItems();

		LOGGER.info("Simple Immersive Bags {} has been initialized!", VERSION);

	}

	private static void registerAllItems() {

		//	Initialize the static item instances
		BACKPACK = register(Registries.ITEM, id("backpack"), () -> new DyeableBagItem(BagMaterials.LEATHER, id("backpack"), 3, 9));
		ENDER_BACKPACK = register(Registries.ITEM, id("ender_backpack"), () -> new EnderBagItem(BagMaterials.LEATHER, id("ender_backpack"), 3, 9));

		//	Register cauldron behaviors
		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(BACKPACK, CauldronBehavior.CLEAN_DYEABLE_ITEM);

		//	Add the items to an item group
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries ->
			entries.addAfter(Items.NETHERITE_BOOTS, BACKPACK, ENDER_BACKPACK)
		);

	}

	public static Identifier id(String path) {
		return new Identifier(MOD_NAMESPACE, path);
	}

	public static <T> T register(Registry<T> registry, Identifier id, Supplier<T> entrySupplier) {
		return Registry.register(registry, id, entrySupplier.get());
	}

}