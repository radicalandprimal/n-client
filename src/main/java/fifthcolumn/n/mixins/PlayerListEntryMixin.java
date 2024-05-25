package fifthcolumn.n.mixins;

import com.mojang.authlib.GameProfile;
import fifthcolumn.n.NMod;
import fifthcolumn.n.modules.StreamerMode;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void n$modifyPlayerDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!this.profile.getId().equals(MeteorClient.mc.player.getUuid())) {
            if (StreamerMode.isGenerifyNames()) {
                String fakeName = NMod.genericNames.getName(this.profile.getId());
                cir.setReturnValue(Text.of(fakeName));
            }
        }
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void n$modifyPlayerSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        PlayerListEntryMixin playerListEntryMixin = this;
        synchronized (playerListEntryMixin) {
            SkinTextures textures = getSkinTextures(cir);
            cir.setReturnValue(textures);
        }
    }

    @Unique
    private @NotNull SkinTextures getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        boolean isCurrentPlayer = MeteorClient.mc.player != null
            && MeteorClient.mc.player.getUuid().equals(profile.getId());

        SkinTextures old = cir.getReturnValue();
        return new SkinTextures(
            old.texture(),
            old.textureUrl(),
            isCurrentPlayer ? NMod.CAPE_TEXTURE : old.capeTexture(),
            isCurrentPlayer ? NMod.CAPE_TEXTURE : old.elytraTexture(),
            StreamerMode.isGenerifyNames() ? SkinTextures.Model.WIDE : old.model(),
            old.secure()
        );
    }
}
