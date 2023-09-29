package fifthcolumn.n.modules;

import com.mojang.authlib.GameProfile;
import fifthcolumn.n.NMod;
import fifthcolumn.n.copenheimer.CopeService;
import java.util.Optional;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class LarpModule extends Module {
    private final SettingGroup sgGeneral;
    public final Setting<String> alias;
    public final Setting<String> aliasName;

    public LarpModule() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "Larping", "Make all griefers larp as another player");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.alias = this.sgGeneral.add(new StringSetting.Builder().name("player uid").description("player uuid to larp as").defaultValue("24f7eb09-ad9e-4e09-b58b-2b59259f171d").build());
        this.aliasName = this.sgGeneral.add(new StringSetting.Builder().name("player name").description("player name to larp as").defaultValue("Joey_Coconut").build());
    }

    @Nullable
    public static String modifyPlayerNameInstances(String text) {
        LarpModule larpModule;
        for (CopeService.Griefer entity : NMod.getCopeService().griefers()) {
            Optional<GameProfile> profile;
            if (entity.playerNameAlias == null || !(profile = NMod.profileCache.findPlayerName(entity.playerNameAlias)).isPresent()) continue;
            text = StringUtils.replace(text, entity.playerName, entity.playerNameAlias);
        }
        if (MeteorClient.mc != null && MeteorClient.mc.player != null && (larpModule = Modules.get().get(LarpModule.class)).isActive()) {
            String aliasName = larpModule.aliasName.get();
            text = StringUtils.replace(text, MeteorClient.mc.player.getEntityName(), aliasName);
        }
        return text;
    }

    public static Optional<String> getPlayerEntityName(PlayerEntity player) {
        if (MeteorClient.mc.player != null && player.getGameProfile().getId().equals(MeteorClient.mc.player.getUuid())) {
            UUID uuid;
            Optional<GameProfile> profile;
            LarpModule larpModule = Modules.get().get(LarpModule.class);
            if (larpModule.isActive() && (profile = NMod.profileCache.findByUUID(uuid = UUID.fromString(larpModule.alias.get()))).isPresent()) {
                return profile.map(GameProfile::getName);
            }
        } else {
            for (CopeService.Griefer griefer : NMod.getCopeService().griefers()) {
                Optional<GameProfile> profile;
                if (!player.getGameProfile().getId().equals(griefer.playerId) || !(profile = NMod.profileCache.findByUUID(griefer.playerId)).isPresent()) continue;
                return profile.map(GameProfile::getName);
            }
        }
        return Optional.empty();
    }
}
