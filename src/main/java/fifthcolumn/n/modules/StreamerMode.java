package fifthcolumn.n.modules;

import fifthcolumn.n.NMod;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StreamerMode extends Module {
    private final SettingGroup sgGeneral;

    private final Setting<Boolean> hideServerInfo;
    private final Setting<Boolean> hideAccount;
    private final Setting<Boolean> generifyPlayerNames;
    private final Setting<Integer> addFakePlayers;
    private final Setting<String> spoofServerBrand;
    public final Setting<Boolean> useRandomIpOffset;

    public StreamerMode() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "Streamer mode", "Hides sensitive info from stream viewers");

        this.sgGeneral = this.settings.getDefaultGroup();

        this.hideServerInfo = this.sgGeneral.add(new BoolSetting.Builder()
            .name("hide server info")
            .defaultValue(true)
            .build()
        );

        this.hideAccount = this.sgGeneral.add(new BoolSetting.Builder()
            .name("hide Logged in account text")
            .defaultValue(true)
            .build()
        );

        this.generifyPlayerNames = this.sgGeneral.add(new BoolSetting.Builder()
            .name("use a generic name for non-griefers")
            .defaultValue(true)
            .build()
        );

        this.addFakePlayers = this.sgGeneral.add(new IntSetting.Builder()
            .name("add fake players to tablist")
            .sliderRange(0, 10)
            .defaultValue(5)
            .build()
        );

        this.spoofServerBrand = this.sgGeneral.add(new StringSetting.Builder()
            .name("spoof server brand")
            .description("Change server brand label in F3, blank to disable.")
            .defaultValue("Paper devs are ops")
            .build()
        );


        this.useRandomIpOffset = this.sgGeneral.add(new BoolSetting.Builder()
            .name("use a random ip header offset")
            .defaultValue(true)
            .build()
        );
    }

    @EventHandler
    public void onMessage(PacketEvent.Receive event) {
        GameMessageS2CPacket packet;
        Packet<?> packet2 = event.packet;
        if (packet2 instanceof GameMessageS2CPacket && (packet = (GameMessageS2CPacket)packet2).content().getString().contains("join") && StreamerMode.isGenerifyNames()) {
            event.cancel();
            MutableText text = Text.literal("A player has joined the game").formatted(Formatting.YELLOW);
            this.mc.inGameHud.getChatHud().addMessage(text);
        }
    }

    public static boolean isStreaming() {
        return Modules.get().get(StreamerMode.class).isActive();
    }

    public static boolean isHideServerInfoEnabled() {
        StreamerMode streamerMode = Modules.get().get(StreamerMode.class);
        return streamerMode != null && streamerMode.isActive() && streamerMode.hideServerInfo.get() != false;
    }

    public static boolean isHideAccountEnabled() {
        StreamerMode streamerMode = Modules.get().get(StreamerMode.class);
        return streamerMode != null && streamerMode.isActive() && streamerMode.hideAccount.get() != false;
    }

    public static boolean isGenerifyNames() {
        StreamerMode streamerMode = Modules.get().get(StreamerMode.class);
        return streamerMode != null && streamerMode.isActive() && streamerMode.generifyPlayerNames.get() != false;
    }

    public static int addFakePlayers() {
        StreamerMode streamerMode = Modules.get().get(StreamerMode.class);
        if (streamerMode != null && streamerMode.isActive()) {
            return streamerMode.addFakePlayers.get();
        }
        return 0;
    }

    public static String spoofServerBrand() {
        return Modules.get().get(StreamerMode.class).spoofServerBrand.get();
    }

    @Nullable
    public static String anonymizePlayerNameInstances(String text) {
        if (MeteorClient.mc != null && MeteorClient.mc.player != null && MeteorClient.mc.getNetworkHandler() != null && StreamerMode.isGenerifyNames()) {
            for (PlayerListEntry player : MeteorClient.mc.getNetworkHandler().getPlayerList()) {
                if (player.getProfile() == null) continue;
                String fakeName = NMod.genericNames.getName(player.getProfile().getId());
                text = StringUtils.replace(text, player.getProfile().getName(), fakeName);
                if (player.getDisplayName() == null) continue;
                text = StringUtils.replace(text, player.getDisplayName().getString(), fakeName);
            }
        }
        return text;
    }

    public static Optional<String> getPlayerEntityName(PlayerEntity player) {
        if (StreamerMode.isGenerifyNames() && MeteorClient.mc.getNetworkHandler() != null
            && !player.getGameProfile().getId().equals(MeteorClient.mc.player.getUuid())
            && MeteorClient.mc.getNetworkHandler() != null
            && StreamerMode.isGenerifyNames()
        ) {
            return Optional.of(NMod.genericNames.getName(player.getGameProfile().getId()));
        }
        return Optional.empty();
    }
}
