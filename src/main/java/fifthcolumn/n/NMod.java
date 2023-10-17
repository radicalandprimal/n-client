package fifthcolumn.n;

import fifthcolumn.n.client.ProfileCache;
import fifthcolumn.n.client.ui.copenheimer.servers.CopeMultiplayerScreen;
import fifthcolumn.n.collar.CollarLogin;
import fifthcolumn.n.copenheimer.CopeService;
import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NameGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class NMod implements ModInitializer {
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?<!<@)[&\u00a7](?i)[0-9a-fklmnorx]");

    private static NMod INSTANCE;

    public static final CopeService copeService = new CopeService();

    public static final Identifier CAPE_TEXTURE = new Identifier("nc:cape.png");
    public static final Identifier cockSound = new Identifier("nc:cock");
    public static final Identifier shotgunSound = new Identifier("nc:shot");

    public static SoundEvent shotgunSoundEvent = SoundEvent.of(shotgunSound);
    public static SoundEvent cockSoundEvent = SoundEvent.of(cockSound);

    public static ProfileCache profileCache = new ProfileCache();
    public static GenericNames genericNames = new GenericNames();

    private CopeMultiplayerScreen multiplayerScreen;

    @Override
    public void onInitialize() {
        MinecraftClient mc = MinecraftClient.getInstance();

        INSTANCE = new NMod();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            copeService.clearTranslations();
            copeService.startUpdating();
            copeService.setLastServerInfo(mc.getCurrentServerEntry());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            copeService.clearTranslations();
            copeService.stopUpdating();
            copeService.setDefaultSession();
            genericNames.clear();
        });
        copeService.setDefaultSession(mc.getSession());

        CollarLogin.refreshSession();

        Registry.register(Registries.SOUND_EVENT, shotgunSound, shotgunSoundEvent);
        Registry.register(Registries.SOUND_EVENT, cockSound, cockSoundEvent);
    }

    public static CopeService getCopeService() {
        return copeService;
    }

    public static CopeMultiplayerScreen getMultiplayerScreen() {
        return NMod.INSTANCE.multiplayerScreen;
    }

    public static CopeMultiplayerScreen getOrCreateMultiplayerScreen(Screen parent) {
        if (NMod.INSTANCE.multiplayerScreen == null) {
            NMod.INSTANCE.multiplayerScreen = new CopeMultiplayerScreen(parent, copeService);
        }
        return NMod.INSTANCE.multiplayerScreen;
    }

    public static void setMultiplayerScreen(CopeMultiplayerScreen multiplayerScreen) {
        NMod.INSTANCE.multiplayerScreen = multiplayerScreen;
    }

    public static boolean is2b2t() {
        ServerInfo serverEntry = MeteorClient.mc.getCurrentServerEntry();
        if (serverEntry == null) {
            return false;
        }
        return serverEntry.address.contains("2b2t.org");
    }

    public static class GenericNames {
        private final Map<UUID, String> names = new HashMap<>();

        public String getName(UUID uuid) {
            this.names.computeIfAbsent(uuid, k -> NameGenerator.name(uuid));
            return this.names.get(uuid);
        }

        public void clear() {
            this.names.clear();
        }
    }
}
