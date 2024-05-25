package fifthcolumn.n.mixins;

import fifthcolumn.n.modules.StreamerMode;
import net.minecraft.client.network.ClientConnectionState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientConnectionState.class)
public abstract class ClientConnectionStateMixin {
    @Inject(method = "serverBrand", at = @At("HEAD"), cancellable = true)
    private void n$fakeServerBrand(CallbackInfoReturnable<String> cir) {
        String fakeBrand = StreamerMode.spoofServerBrand();
        if (!fakeBrand.isEmpty()) {
            cir.setReturnValue(fakeBrand);
        }
    }
}
