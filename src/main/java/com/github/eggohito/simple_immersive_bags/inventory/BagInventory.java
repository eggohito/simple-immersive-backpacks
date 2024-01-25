package com.github.eggohito.simple_immersive_bags.inventory;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BagInventory extends GridInventory implements ExtendedScreenHandlerFactory {

    public static final BagInventory EMPTY = new BagInventory(ItemStack.EMPTY, SimpleImmersiveBags.id("textures/gui/backpack.png"), false, 0, 0) {

        @Override
        public void load() {

        }

        @Override
        public void save() {

        }

    };

    private final Identifier screenTextureId;
    private final ItemStack sourceStack;

    private final boolean save;
    private boolean dirty;

    public BagInventory(ItemStack sourceStack, Identifier screenTextureId, boolean shouldSave, int rows, int columns) {
        super(rows, columns);
        this.screenTextureId = screenTextureId;
        this.sourceStack = sourceStack;
        this.save = shouldSave;
    }

    public BagInventory(ItemStack sourceStack, Identifier screenTextureId, int rows, int columns) {
        super(rows, columns);
        this.screenTextureId = screenTextureId;
        this.sourceStack = sourceStack;
        this.save = true;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        this.write(buf);
    }

    @Override
    public Text getDisplayName() {
        return sourceStack.getItem() instanceof BagItem
            ? sourceStack.getName()
            : Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return sourceStack.getItem() instanceof BagItem
            ? new BagScreenHandler(syncId, playerInventory, player, this)
            : null;
    }

    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.load();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.getWorld().isClient && dirty && save) {
            ((EntityBagUpdateStatus) player).sib$setStatus(BagUpdateStatus.SAVE);
            this.save();
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    public Identifier getScreenTextureId() {
        return screenTextureId;
    }

    public ItemStack getSourceStack() {
        return sourceStack;
    }

    public boolean saveable() {
        return save;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void write(PacketByteBuf buf) {

        if (!(this.getSourceStack().getItem() instanceof BagItem bagItem)) {
            buf.writeBoolean(true);
            return;
        }

        buf.writeBoolean(false);

        buf.writeIdentifier(this.getScreenTextureId());
        buf.writeEnumConstant(bagItem.getSlotType());

        buf.writeVarInt(this.getRows());
        buf.writeVarInt(this.getColumns());

        buf.writeBoolean(this.saveable());

    }

    public static BagInventory receive(PlayerEntity player, PacketByteBuf buf) {

        if (buf.readBoolean()) {
            return EMPTY;
        }

        Identifier screenTextureId = buf.readIdentifier();
        EquipmentSlot bagSlot = buf.readEnumConstant(EquipmentSlot.class);

        int rows = buf.readVarInt();
        int columns = buf.readVarInt();

        boolean saveable = buf.readBoolean();
        return new BagInventory(player.getEquippedStack(bagSlot), screenTextureId, saveable, rows, columns);

    }

    public void load() {

        ItemStack sourceStack = this.getSourceStack();
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(sourceStack, null);

        if (bagContainer == null) {
            SimpleImmersiveBags.LOGGER.error("Tried loading the bag inventory contents of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        DefaultedList<ItemStack> contents = bagContainer.getContents(sourceStack);
        for (int i = 0; i < this.size(); i++) {
            this.getHeldStacks().set(i, contents.get(i));
        }

    }

    public void save() {

        ItemStack sourceStack = this.getSourceStack();
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(sourceStack, null);

        if (bagContainer == null) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the bag inventory contents of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        if (!save) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the bag inventory contents of item {}, which can't be saved!", sourceStack);
            return;
        }

        bagContainer.setContents(sourceStack, this.getHeldStacks());
        this.dirty = false;

    }

}
