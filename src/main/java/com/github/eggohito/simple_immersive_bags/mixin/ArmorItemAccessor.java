package com.github.eggohito.simple_immersive_bags.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

    @Mutable
    @Accessor
    void setAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers);

    @Accessor("MODIFIERS")
    static EnumMap<ArmorItem.Type, UUID> getModifierUuids() {
        throw new AssertionError();
    }

}
