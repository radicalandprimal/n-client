package fifthcolumn.n.origins;

import fifthcolumn.n.modules.OriginsModule;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;

public class TMOPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientLoginNetworking.registerGlobalReceiver(TMOPackets.HANDSHAKE, TMOPacketsS2C::handleHandshake);
    }

    @Environment(EnvType.CLIENT)
    private static CompletableFuture<PacketByteBuf> handleHandshake(
        MinecraftClient minecraftClient,
        ClientLoginNetworkHandler clientLoginNetworkHandler,
        PacketByteBuf packetByteBuf,
        Consumer<PacketCallbacks> genericFutureListenerConsumer
    ) {
        if (minecraftClient.isIntegratedServerRunning()) {
            return CompletableFuture.completedFuture(PacketByteBufs.empty());
        }
        if (!Modules.get().isActive(OriginsModule.class)) {
            return CompletableFuture.failedFuture(new Throwable("Origins module needs to be enabled and version set"));
        }
        OriginsModule originsModule = Modules.get().get(OriginsModule.class);
        int[] semVer = originsModule.getSemVer();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(semVer.length);
        for (int j : semVer) {
            buf.writeInt(j);
        }
        return CompletableFuture.completedFuture(buf);
    }
}

