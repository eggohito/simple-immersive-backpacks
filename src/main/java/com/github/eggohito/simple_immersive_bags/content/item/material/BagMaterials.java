package com.github.eggohito.simple_immersive_bags.content.item.material;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;

public class BagMaterials {

    public static final BagMaterial LEATHER = new BagMaterial(SimpleImmersiveBags.id("leather").toString())
        .mapDurability(map -> map.put(ArmorItem.Type.CHESTPLATE, 16))
        .repairIngredient(ArmorMaterials.LEATHER::getRepairIngredient)
        .equipSound(ArmorMaterials.LEATHER.getEquipSound());

}
