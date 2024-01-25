package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.networking.s2c.OpenInventoryS2CPacket;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public class BagItem extends Item implements Equipment, BagContainer {

    protected final Identifier screenTextureId;
    protected final EquipmentSlot equipSlot;

    private final SoundEvent equipSound;

    protected final int initialRows;
    protected final int initialColumns;

    public BagItem(Identifier screenTextureId, EquipmentSlot equipSlot, int initialRows, int initialColumns) {
        this(screenTextureId, equipSlot, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, new Settings().maxDamage(-1), initialRows, initialColumns);
    }

    public BagItem(Identifier screenTextureId, EquipmentSlot equipSlot, SoundEvent equipSound, int initialRows, int initialColumns) {
        this(screenTextureId, equipSlot, equipSound, new Settings().maxDamage(-1), initialRows, initialColumns);
    }

    public BagItem(Identifier screenTextureId, EquipmentSlot equipSlot, SoundEvent equipSound, Settings settings, int initialRows, int initialColumns) {
        super(settings);

        Preconditions.checkArgument(initialRows > 0, "Row argument cannot be equal or less than 0!");
        Preconditions.checkArgument(initialColumns > 0, "Column argument cannot be equal or less than 0!");

        SimpleImmersiveBags.ITEM_CONTAINER.registerSelf(this);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        this.initialRows = initialRows;
        this.initialColumns = initialColumns;

        this.screenTextureId = screenTextureId;
        this.equipSlot = equipSlot;
        this.equipSound = equipSound;

    }

    @Override
    public EquipmentSlot getSlotType() {
        return equipSlot;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ((EntityBagUpdateStatus) user).sib$setStatus(BagUpdateStatus.EQUIP);
        return this.equipAndSwap(this, world, user, hand);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, this.getContents(entity.getStack()).stream());
    }

    @Override
    public DefaultedList<ItemStack> getContents(ItemStack sourceStack) {

        DefaultedList<ItemStack> contents = DefaultedList.ofSize(initialRows * initialColumns, ItemStack.EMPTY);
        NbtCompound stackNbt;

        if ((stackNbt = sourceStack.getNbt()) == null || !stackNbt.contains(SimpleImmersiveBags.ITEM_CONTAINER_ID)) {
            return contents;
        }

        Inventories.readNbt(stackNbt.getCompound(SimpleImmersiveBags.ITEM_CONTAINER_ID), contents);
        return contents;

    }

    @Override
    public void setContents(ItemStack sourceStack, DefaultedList<ItemStack> contents) {
        NbtCompound itemContainerNbt = sourceStack.getOrCreateSubNbt(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        Inventories.writeNbt(itemContainerNbt, contents);
    }

    public BagInventory asDelegatedBagInventory(LivingEntity holder, ItemStack stack) {
        return this.asBagInventory(stack);
    }

    public BagInventory asBagInventory(ItemStack stack) {
        return stack.isOf(this)
            ? new BagInventory(stack, screenTextureId, initialRows, initialColumns)
            : BagInventory.EMPTY;
    }

    public static Optional<EquipmentSlot> getSlotWithBag(PlayerEntity player) {
        return Arrays.stream(EquipmentSlot.values())
            .filter(slot -> player.getEquippedStack(slot).getItem() instanceof BagItem bagItem
                         && bagItem.getSlotType() == slot)
            .findFirst();
    }

    @ApiStatus.Internal
    public static void onEquipmentUpdate(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {

        if (!(entity instanceof ServerPlayerEntity player) || ((EntityBagUpdateStatus) entity).sib$getStatus() != BagUpdateStatus.NONE) {
            return;
        }

        if (currentStack.getItem() instanceof BagItem bagItem && equipmentSlot == bagItem.getSlotType()) {
            openHandler(player, currentStack);
        }

        else if (previousStack.getItem() instanceof BagItem bagItem && equipmentSlot == bagItem.getSlotType()) {
            closeHandler(player);
        }

    }

    public static void openHandler(ServerPlayerEntity player, ItemStack bagStack) {

        if (!(bagStack.getItem() instanceof BagItem bagItem)) {
            return;
        }

        ItemStack prevCursorStack = player.currentScreenHandler.getCursorStack().copy();
        boolean shouldResetCursorStack = !prevCursorStack.isEmpty();

        if (shouldResetCursorStack) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }

        player.openHandledScreen(bagItem.asDelegatedBagInventory(player, bagStack));
        player.currentScreenHandler.sendContentUpdates();

        if (shouldResetCursorStack) {
            player.currentScreenHandler.setCursorStack(prevCursorStack);
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, player.currentScreenHandler.nextRevision(), -1, prevCursorStack));
        }

    }

    public static void closeHandler(ServerPlayerEntity player) {

        if (!(player.currentScreenHandler instanceof BagScreenHandler)) {
            player.currentScreenHandler.sendContentUpdates();
            return;
        }

        ItemStack prevCursorStack = player.currentScreenHandler.getCursorStack().copy();
        boolean shouldResetCursorStack = !prevCursorStack.isEmpty();

        if (shouldResetCursorStack) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }

        player.onHandledScreenClosed();
        player.currentScreenHandler.sendContentUpdates();

        ServerPlayNetworking.send(player, new OpenInventoryS2CPacket());

        if (shouldResetCursorStack) {
            player.currentScreenHandler.setCursorStack(prevCursorStack);
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, player.currentScreenHandler.nextRevision(), -1, prevCursorStack));
        }

    }

}
