package com.github.eggohito.simple_immersive_bags.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public class BackpackEntityModel extends BipedEntityModel<LivingEntity> {

    protected BackpackEntityModel(ModelPart root) {
        super(root);
        this.body.visible = true;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.head.visible = false;
        this.hat.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
    }

    public static BackpackEntityModel create() {

        ModelData modelData = getModelData(Dilation.NONE, 0.0f);
        ModelPartData bodyModelPart = modelData.getRoot().getChild("body");

        bodyModelPart.addChild(
            "base",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-3.5f, 0.5f, 2.2f, 7.0f, 10.0f, 4.0f),
            ModelTransform.NONE
        );

        return new BackpackEntityModel(modelData.getRoot().createPart(64, 64));

    }

}
