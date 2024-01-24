package com.github.eggohito.simple_immersive_bags.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {

    @Mutable
    @Accessor
    void setSyncId(int syncId);

    @Mutable
    @Accessor
    void setType(ScreenHandlerType<?> screenHandlerType);

}
