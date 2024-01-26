package com.github.eggohito.simple_immersive_bags.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum BagState implements StringIdentifiable {

    OPENED("opened", 1),
    CLOSED("closed", 0),
    NONE("none", -1);

    public static final Codec<BagState> CODEC = StringIdentifiable.createCodec(BagState::values);

    private final String name;
    private final int customModelFlag;

    BagState(String name, int customModelFlag) {
        this.name = name;
        this.customModelFlag = customModelFlag;
    }

    @Override
    public String asString() {
        return name;
    }

    public int getCustomModelFlag() {
        return customModelFlag;
    }

}
