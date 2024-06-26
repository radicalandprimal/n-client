package fifthcolumn.n.mixins;

import com.mojang.datafixers.DataFixer;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(HotbarStorage.class)
public abstract class HotbarStorageMixin {
    @Final
    @Shadow
    private DataFixer dataFixer;
    @Final
    @Shadow
    private HotbarStorageEntry[] entries;

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void n$loadBuiltInCreativeHotbar(CallbackInfo cb) {
        String HOTBAR_HOTBAR_NBT = "hotbar/hotbar.nbt";
        Logger LOGGER = LoggerFactory.getLogger(HotbarStorageMixin.class);

        try {
            URL hotbarResource = HotbarStorageMixin.class.getClassLoader().getResource(HOTBAR_HOTBAR_NBT);
            if (hotbarResource == null) {
                LOGGER.error("Could not find hotbar hotbar/hotbar.nbt");
                return;
            }

            Path hotbarResourcePath = Paths.get(hotbarResource.toURI());
            NbtCompound nbtComp = NbtIo.read(hotbarResourcePath);
            if (nbtComp != null) {
                if (!nbtComp.contains("DataVersion", 99)) {
                    nbtComp.putInt("DataVersion", 1343);
                }

                nbtComp = DataFixTypes.HOTBAR.update(this.dataFixer, nbtComp, nbtComp.getInt("DataVersion"));

                for (int i = 0; i < 9; i++) {
                    this.entries[i] = HotbarStorageEntry.CODEC.parse(NbtOps.INSTANCE, nbtComp.getList(String.valueOf(i), 10))
                        .result().orElseGet(HotbarStorageEntry::new);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load creative mode options", e);
        }

        cb.cancel();
    }
}
