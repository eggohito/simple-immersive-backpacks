package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.mixin.ArmorItemAccessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BagItem extends ArmorItem implements ScreenHandlerFactory {

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
        return null;    //  TODO: Implement the screen handler for general bag items
    }

}
