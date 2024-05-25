package fifthcolumn.n.origins;

import fifthcolumn.n.modules.OriginsModule;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;

public class ModPacketsC2S {
    public static void register() {
        ServerLoginConnectionEvents.QUERY_START.register(ModPacketsC2S::handshake);
    }

    private static void handshake(
        ServerLoginNetworkHandler serverLoginNetworkHandler,
        MinecraftServer minecraftServer,
        LoginPacketSender packetSender,
        ServerLoginNetworking.LoginSynchronizer loginSync
    ) {
        if (Modules.get().isActive(OriginsModule.class)) {
            packetSender.sendPacket(ModPackets.HANDSHAKE, PacketByteBufs.empty());
        }
    }
}
