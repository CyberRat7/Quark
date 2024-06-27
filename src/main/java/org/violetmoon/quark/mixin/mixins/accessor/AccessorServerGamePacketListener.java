package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface AccessorServerGamePacketListener {

    @Accessor("aboveGroundTickCount")
    void setAboveGroundTickCount(int t);

}
