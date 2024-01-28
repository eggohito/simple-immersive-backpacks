package com.github.eggohito.simple_immersive_bags;

import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.content.item.DyeableBagItem;
import com.github.eggohito.simple_immersive_bags.content.item.EnderBagItem;
import com.github.eggohito.simple_immersive_bags.networking.SimpleImmersiveBagsC2SPackets;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandlerTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class SimpleImmersiveBags implements ModInitializer {

	public static final String MOD_NAMESPACE = "simple-immersive-bags";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	public static final String ITEM_CONTAINER_ID = id("item_container").toString();
	public static final ItemApiLookup<BagContainer, Void> ITEM_CONTAINER = ItemApiLookup.get(new Identifier(ITEM_CONTAINER_ID), BagContainer.class, Void.class);

	public static String VERSION;
	public static int[] SEMANTIC_VERSION;

	public static Item BACKPACK;
	public static Item ENDER_BACKPACK;

	public static Item TOOLBELT;

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
		BagScreenHandlerTypes.registerAll();
		SimpleImmersiveBagsC2SPackets.registerAll();

		LOGGER.info("Simple Immersive Bags {} has been initialized!", VERSION);

	}

	private static void registerAllItems() {

		//	Initialize the static item instances
		BACKPACK = registerItem(id("backpack"), () -> new DyeableBagItem(id("textures/gui/backpack.png"), EquipmentSlot.CHEST, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 3, 9));
		ENDER_BACKPACK = registerItem(id("ender_backpack"), () -> new EnderBagItem(id("textures/gui/ender_backpack.png"), EquipmentSlot.CHEST, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER));

		TOOLBELT = registerItem(id("toolbelt"), () -> new DyeableBagItem(id("textures/gui/backpack.png"), EquipmentSlot.LEGS, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 3, 3));

		//	Add the items to an item group
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries ->
			entries.addAfter(Items.NETHERITE_BOOTS, BACKPACK.getDefaultStack(), ENDER_BACKPACK.getDefaultStack(), TOOLBELT.getDefaultStack())
		);

		//	Register callback event for stuff
		ServerEntityEvents.EQUIPMENT_CHANGE.register(BagItem::onEquipmentUpdate);

	}

	public static Identifier id(String path) {
		return new Identifier(MOD_NAMESPACE, path);
	}

	public static <T extends Item> T registerItem(Identifier id, Supplier<T> entrySupplier) {
		return Registry.register(Registries.ITEM, id, entrySupplier.get());
	}

}