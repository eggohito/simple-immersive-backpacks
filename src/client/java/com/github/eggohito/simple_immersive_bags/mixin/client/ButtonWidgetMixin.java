package com.github.eggohito.simple_immersive_bags.mixin.client;

import com.github.eggohito.simple_immersive_bags.client.duck.IdentifiableButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(ButtonWidget.class)
public abstract class ButtonWidgetMixin extends PressableWidget implements IdentifiableButtonWidget {

    private ButtonWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Unique
    private Identifier sib$id;

    @Override
    public boolean sib$equals(Identifier id) {
        return this.sib$getId()
            .map(id::equals)
            .orElse(false);
    }

    @Override
    public Optional<Identifier> sib$getId() {
        return Optional.ofNullable(sib$id);
    }

    @Override
    public void sib$setId(Identifier id) {
        this.sib$id = id;
    }

}
