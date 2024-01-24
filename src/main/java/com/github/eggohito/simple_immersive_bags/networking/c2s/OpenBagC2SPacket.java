package com.github.eggohito.simple_immersive_bags.networking.c2s;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.PacketByteBuf;

public record OpenBagC2SPacket(EquipmentSlot slotWithBag) implements FabricPacket {

    public static final PacketType<OpenBagC2SPacket> TYPE = PacketType.create(
        SimpleImmersiveBags.id("s2c/open_bag"), OpenBagC2SPacket::read
    );

    private static OpenBagC2SPacket read(PacketByteBuf buf) {
        return new OpenBagC2SPacket(buf.readEnumConstant(EquipmentSlot.class));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(slotWithBag);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
