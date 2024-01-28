package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.networking.s2c.OpenInventoryS2CPacket;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagState;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.util.BagUtil;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

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
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {

        if (!BagUtil.withinEquipmentBounds(player, slot) || clickType == ClickType.LEFT) {
            return false;
        }

        if (player.getWorld().isClient) {
            return true;
        }

        return switch (this.getState(stack)) {
            case OPENED -> {
                this.setState(stack, BagState.CLOSED);
                yield true;
            }
            case CLOSED -> {
                this.setState(stack, BagState.OPENED);
                yield true;
            }
            default ->
                false;
        };

    }

    @Override
    public ItemStack getDefaultStack() {

        ItemStack stack = super.getDefaultStack();
        this.setState(stack, BagState.CLOSED);

        return stack;

    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        this.setState(stack, BagState.CLOSED);
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

    @Override
    public BagState getState(ItemStack sourceStack) {

        NbtCompound stackNbt;
        if ((stackNbt = sourceStack.getNbt()) == null || !stackNbt.contains(SimpleImmersiveBags.ITEM_CONTAINER_ID)) {
            return BagState.NONE;
        }

        NbtCompound itemContainerNbt = stackNbt.getCompound(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        return BagState.CODEC
            .decode(NbtOps.INSTANCE, itemContainerNbt.get("State"))
            .result()
            .map(Pair::getFirst)
            .orElse(BagState.NONE);

    }

    @Override
    public void setState(ItemStack sourceStack, BagState state) {
        NbtCompound itemContainerNbt = sourceStack.getOrCreateSubNbt(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        itemContainerNbt.putString("State", state.asString());
    }

    @Override
    public BagInventory asDelegatedBagInventory(LivingEntity holder, ItemStack stack) {
        return this.asBagInventory(stack);
    }

    @Override
    public BagInventory asBagInventory(ItemStack stack) {
        return stack.isOf(this)
            ? new BagInventory(stack, screenTextureId, initialRows, initialColumns)
            : BagInventory.EMPTY;
    }

    public static Optional<EquipmentSlot> getFirstOpenedBag(PlayerEntity player) {

        ItemStack stack;

        for (EquipmentSlot slot : EquipmentSlot.values()) {

            stack = player.getEquippedStack(slot);
            if (!(stack.getItem() instanceof BagItem bagItem)) {
                continue;
            }

            if (bagItem.getSlotType() == slot && bagItem.getState(stack) == BagState.OPENED) {
                return Optional.of(slot);
            }

        }

        return Optional.empty();

    }

    @ApiStatus.Internal
    public static void onEquipmentUpdate(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {

        if (!(entity instanceof ServerPlayerEntity player) || ((EntityBagUpdateStatus) entity).sib$getStatus() != BagUpdateStatus.NONE) {
            return;
        }

        if (ItemStack.areEqual(previousStack, currentStack)) {
            return;
        }

        if (previousStack.getItem() instanceof BagItem bagItem && equipmentSlot == bagItem.getSlotType()) {
            closeHandler(player);
        }

        if (currentStack.getItem() instanceof BagItem bagItem && equipmentSlot == bagItem.getSlotType()) {
            openHandler(player, currentStack);
        }

    }

    private static void openHandler(PlayerEntity player, ItemStack bagStack) {

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(bagStack, null);
        if (bagContainer == null || bagContainer.getState(bagStack) == BagState.CLOSED) {
            return;
        }

        if (player.currentScreenHandler instanceof BagScreenHandler prevBagScreenHandler) {

            ItemStack prevBagStack = prevBagScreenHandler.getSourceStack();
            BagContainer prevBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(prevBagStack, null);

            if (prevBagContainer != null) {
                prevBagScreenHandler.getBagInventory().onClose(player);
                prevBagContainer.setState(prevBagStack, BagState.CLOSED);
            }

        }

        ItemStack prevCursorStack = serverPlayer.currentScreenHandler.getCursorStack().copy();
        serverPlayer.currentScreenHandler.setCursorStack(ItemStack.EMPTY);

        serverPlayer.openHandledScreen(bagContainer.asDelegatedBagInventory(serverPlayer, bagStack));

        serverPlayer.currentScreenHandler.setCursorStack(prevCursorStack);
        serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, serverPlayer.currentScreenHandler.nextRevision(), -1, prevCursorStack));

    }

    private static void closeHandler(PlayerEntity player) {

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (!(serverPlayer.currentScreenHandler instanceof BagScreenHandler bagScreenHandler)) {
            return;
        }

        ItemStack bagStack = bagScreenHandler.getSourceStack();
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(bagStack, null);

        if (bagContainer != null && bagContainer.getState(bagStack) == BagState.OPENED) {
            return;
        }

        ItemStack prevCursorStack = player.currentScreenHandler.getCursorStack().copy();
        serverPlayer.currentScreenHandler.setCursorStack(ItemStack.EMPTY);

        serverPlayer.onHandledScreenClosed();
        ServerPlayNetworking.send(serverPlayer, new OpenInventoryS2CPacket());

        serverPlayer.currentScreenHandler.setCursorStack(prevCursorStack);
        serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, serverPlayer.currentScreenHandler.nextRevision(), -1, prevCursorStack));

    }

}
