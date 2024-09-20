package fifthcolumn.n;

import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
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

    public static final Identifier CAPE_TEXTURE = Identifier.of("nc:cape.png");
    public static final Identifier cockSound = Identifier.of("nc:cock");
    public static final Identifier shotgunSound = Identifier.of("nc:shot");

    public static SoundEvent shotgunSoundEvent = SoundEvent.of(shotgunSound);
    public static SoundEvent cockSoundEvent = SoundEvent.of(cockSound);

    public static GenericNames genericNames = new GenericNames();

    @Override
    public void onInitialize() {
        INSTANCE = new NMod();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            genericNames.clear();
        });

        Registry.register(Registries.SOUND_EVENT, shotgunSound, shotgunSoundEvent);
        Registry.register(Registries.SOUND_EVENT, cockSound, cockSoundEvent);
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
