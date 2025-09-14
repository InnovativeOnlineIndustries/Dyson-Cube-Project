package com.buuz135.dysoncubeproject.network;

import com.buuz135.dysoncubeproject.world.ClientDysonSphere;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DysonSphereSyncMessage extends Message {

    public CompoundTag tag;

    public DysonSphereSyncMessage() {

    }

    public DysonSphereSyncMessage(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientDysonSphere.DYSON_SPHERE_PROGRESS = DysonSphereProgressSavedData.load(Minecraft.getInstance().level.registryAccess(), tag);
        });
    }
}
