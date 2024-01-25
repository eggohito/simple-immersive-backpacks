package com.github.eggohito.simple_immersive_bags.client.duck;

import net.minecraft.util.Identifier;

import java.util.Optional;

public interface IdentifiableButtonWidget {

    boolean sib$equals(Identifier id);

    Optional<Identifier> sib$getId();
    void sib$setId(Identifier id);

}
