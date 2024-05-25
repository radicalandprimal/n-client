package fifthcolumn.n.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fifthcolumn.n.NMod;
import fifthcolumn.n.client.ProfileCache;
import fifthcolumn.n.modules.StreamerMode;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow
    @Final
    private GameProfile profile;
    @Shadow
    @Nullable
    private String model;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void n$modifyPlayerDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!this.profile.getId().equals(MeteorClient.mc.player.getUuid())) {
            if (StreamerMode.isGenerifyNames()) {
                String fakeName = NMod.genericNames.getName(this.profile.getId());
                cir.setReturnValue(Text.of(fakeName));
            }
        }
    }

    @Redirect(method = "getSkinTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;loadTextures()V"))
    private void n$modifyPlayerSkinTexture(PlayerListEntry instance) {
        PlayerListEntryMixin playerListEntryMixin = this;
        synchronized (playerListEntryMixin) {
            if (this.profile.getId().equals(MeteorClient.mc.player.getUuid())) {
                NMod.profileCache.texture(this.profile.getId()).ifPresent(this::setSkinTexture);
            } else {
                if (StreamerMode.isGenerifyNames()) {
                    this.model = "default";
                }
            }
        }
    }

    private void setSkinTexture(ProfileCache.TextureResult textureResult) {
        this.textures.put(textureResult.type, textureResult.id);
        if (textureResult.type == MinecraftProfileTexture.Type.SKIN) {
            String modelName = textureResult.texture.getMetadata("model");
            this.model = modelName == null ? "default" : modelName;
        }
    }
}
