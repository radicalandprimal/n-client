package fifthcolumn.n.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.world.GameMode;

public class GameModeNotifier extends Module {
    public GameModeNotifier() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "Gamemode Notifier", "Alerts you when someone changes gamemode");
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (this.mc.getNetworkHandler() == null) {
            return;
        }
        Packet<?> packet = event.packet;
        if (packet instanceof PlayerListS2CPacket packet2) {
            for (PlayerListS2CPacket.Entry entry : packet2.getEntries()) {
                for (PlayerListS2CPacket.Action action : packet2.getActions()) {
                    if (!action.equals(PlayerListS2CPacket.Action.UPDATE_GAME_MODE) || packet2.getPlayerAdditionEntries().contains(entry)) continue;
                    GameMode newGameMode = entry.gameMode();
                    String player = this.mc.getNetworkHandler().getPlayerListEntry(entry.profileId()).getProfile().getName();
                    super.info(player + " has switched to " + newGameMode.getName() + " mode!");
                }
            }
        }
    }
}
