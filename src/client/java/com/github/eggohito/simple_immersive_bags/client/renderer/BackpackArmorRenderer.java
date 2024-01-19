package com.github.eggohito.simple_immersive_bags.client.renderer;

import com.github.eggohito.simple_immersive_bags.client.model.BackpackEntityModel;
import com.github.eggohito.simple_immersive_bags.content.item.DyeableBagItem;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BackpackArmorRenderer implements ArmorRenderer {

    private final Identifier baseTexture;
    private final Identifier overlayTexture;

    private final BackpackEntityModel model;

    public BackpackArmorRenderer(Identifier baseTexture, Identifier overlayTexture) {
        this.baseTexture = baseTexture;
        this.overlayTexture = overlayTexture;
        this.model = BackpackEntityModel.create();
    }

    public BackpackArmorRenderer(Identifier baseTexture) {
        this(baseTexture, null);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {

        contextModel.copyBipedStateTo(model);
        model.setAngles(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

        //  Apply a color tint and render the base texture
        VertexConsumer baseVertex = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(baseTexture), false, stack.hasGlint());
        float[] rgb = DyeableBagItem.unpackRgb(stack);

        model.render(matrices, baseVertex, light, OverlayTexture.DEFAULT_UV, rgb[0], rgb[1], rgb[2], 1.0f);

        //  If no overlay texture is specified, skip this process
        if (overlayTexture == null) {
            return;
        }

        //  Render the overlay texture
        VertexConsumer overlayVertex = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(overlayTexture), false, stack.hasNbt());
        model.render(matrices, overlayVertex, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);

    }

}
