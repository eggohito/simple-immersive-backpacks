package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.mixin.ArmorItemAccessor;
import com.github.eggohito.simple_immersive_bags.networking.s2c.OpenInventoryS2CPacket;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class BagItem extends ArmorItem implements ExtendedScreenHandlerFactory {

    protected final Identifier screenTextureId;

    protected final int initialRows;
    protected final int initialColumns;

    public BagItem(ArmorMaterial armorMaterial, Identifier screenTextureId, int initialRows, int initialColumns) {
        this(armorMaterial, screenTextureId, new Settings().maxDamage(-1), initialRows, initialColumns);
    }

    public BagItem(ArmorMaterial armorMaterial, Identifier screenTextureId, Settings settings, int initialRows, int initialColumns) {
        super(armorMaterial, Type.CHESTPLATE, settings);

        Preconditions.checkArgument(initialRows > 0, "Row argument cannot be equal or less than 0!");
        Preconditions.checkArgument(initialColumns > 0, "Column argument cannot be equal or less than 0!");

        this.screenTextureId = screenTextureId;

        this.initialRows = initialRows;
        this.initialColumns = initialColumns;

        //  Reset the armor item's attribute modifiers. This is to hide the tooltips that appear when an armor item
        //  provides protection/toughness/knockback resistance, which apparently doesn't display properly if the attribute modifiers
        //  have a 0 value...
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.builder();
        UUID uuid = ArmorItemAccessor.getModifierUuids().get(type);

        if (this.getProtection() != 0) {
            modifiers.put(
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(uuid, "Armor modifier", this.getProtection(), EntityAttributeModifier.Operation.ADDITION)
            );
        }

        if (this.getToughness() != 0) {
            modifiers.put(
                EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                new EntityAttributeModifier(uuid, "Armor toughness", this.getToughness(), EntityAttributeModifier.Operation.ADDITION)
            );
        }

        if (this.knockbackResistance != 0) {
            modifiers.put(
                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                new EntityAttributeModifier(uuid, "Armor knockback resistance", this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION)
            );
        }

        ((ArmorItemAccessor) this).setAttributeModifiers(modifiers.build());

    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        ItemStack equippedStack = player.getEquippedStack(this.getSlotType());
        if (!equippedStack.isOf(this)) {
            return null;
        }

        BagInventory bagInventory = new BagInventory(equippedStack, screenTextureId, initialRows, initialColumns);
        return new BagScreenHandler(syncId, playerInventory, player, bagInventory);

    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {

        buf.writeVarInt(initialRows);
        buf.writeVarInt(initialColumns);

        buf.writeEnumConstant(this.getSlotType());
        buf.writeIdentifier(screenTextureId);

    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Override
    public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        ((EntityBagUpdateStatus) user).sib$setStatus(BagUpdateStatus.EQUIP);
        return super.equipAndSwap(item, world, user, hand);
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

        player.onHandledScreenClosed();
        player.openHandledScreen(bagItem);

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
