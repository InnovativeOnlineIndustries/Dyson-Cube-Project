package com.buuz135.dysoncubeproject.network;

import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientSubscribeSphereMessage extends Message {

    public String sphereId;

    public ClientSubscribeSphereMessage() {
    }

    public ClientSubscribeSphereMessage(String sphereId) {
        this.sphereId = sphereId;
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                var level = sp.level();
                var data = DysonSphereProgressSavedData.get(level);
                if (data != null && sphereId != null) {
                    data.getSubscribedPlayers().put(sp.getStringUUID(), sphereId);
                    data.setDirty();
                }
            }
        });
    }
}
