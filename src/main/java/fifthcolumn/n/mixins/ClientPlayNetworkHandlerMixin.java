package fifthcolumn.n.mixins;

import fifthcolumn.n.events.PlayerSpawnPositionEvent;
import fifthcolumn.n.events.SpawnPlayerEvent;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 100)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerSpawnPosition", at = @At("TAIL"))
    private void n$playerSpawnPositionEvent(PlayerSpawnPositionS2CPacket packet, CallbackInfo cinfo) {
        MeteorClient.EVENT_BUS.post(new PlayerSpawnPositionEvent(packet.getPos()));
    }

    @Inject(method = "onEntitySpawn", at = @At("TAIL"))
    private void n$playerSpawnEvent(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if (packet.getEntityType() != EntityType.PLAYER) return;
        MeteorClient.EVENT_BUS.post(new SpawnPlayerEvent(
            packet.getUuid(),
            new BlockPos((int) packet.getX(), (int) packet.getY(), (int) packet.getZ())
        ));
    }
}
