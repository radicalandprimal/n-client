package fifthcolumn.n.mixins;

import fifthcolumn.n.modules.StreamerMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @ModifyArg(
        method = "sendClientSettings",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
        ))
    private Packet<?> n$forceDisableServerListing(Packet<?> packet) {
        if (StreamerMode.isStreaming()) {
            ClientOptionsC2SPacket p = (ClientOptionsC2SPacket) packet;
            return new ClientOptionsC2SPacket(new SyncedClientOptions(
                p.options().language(),
                p.options().viewDistance(),
                p.options().chatVisibility(),
                p.options().chatColorsEnabled(),
                p.options().playerModelParts(),
                p.options().mainArm(),
                p.options().filtersText(),
                false
            ));
        }
        return packet;
    }
}
