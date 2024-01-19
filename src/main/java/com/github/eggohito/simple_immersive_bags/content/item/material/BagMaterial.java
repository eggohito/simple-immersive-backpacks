package com.github.eggohito.simple_immersive_bags.content.item.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.EnumMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BagMaterial implements ArmorMaterial {

    private final EnumMap<ArmorItem.Type, Integer> durabilityByType = new EnumMap<>(ArmorItem.Type.class);
    private final EnumMap<ArmorItem.Type, Integer> protectionByType = new EnumMap<>(ArmorItem.Type.class);

    private final String name;

    private Supplier<Ingredient> repairIngredientSupplier = () -> Ingredient.EMPTY;
    private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;

    private int enchantability;
    private int toughness;
    private int knockbackResistance;

    public BagMaterial(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public BagMaterial mapDurability(Consumer<EnumMap<ArmorItem.Type, Integer>> durabilityMapConsumer) {
        return map(durabilityMapConsumer, this.durabilityByType);
    }

    public BagMaterial mapProtection(Consumer<EnumMap<ArmorItem.Type, Integer>> protectionMapConsumer) {
        return map(protectionMapConsumer, this.protectionByType);
    }

    public BagMaterial repairIngredient(Supplier<Ingredient> ingredientSupplier) {
        this.repairIngredientSupplier = ingredientSupplier;
        return this;
    }

    public BagMaterial equipSound(SoundEvent equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public BagMaterial enchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public BagMaterial toughness(int toughness) {
        this.toughness = toughness;
        return this;
    }

    public BagMaterial knockbackResistance(int knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    protected <E extends Enum<E>, I, M extends EnumMap<E, I>> BagMaterial map(Consumer<M> mapConsumer, M map) {
        mapConsumer.accept(map);
        return this;
    }

    @Override
    public int getDurability(ArmorItem.Type type) {
        return durabilityByType.getOrDefault(type, 0);
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return protectionByType.getOrDefault(type, 0);
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredientSupplier.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

}
