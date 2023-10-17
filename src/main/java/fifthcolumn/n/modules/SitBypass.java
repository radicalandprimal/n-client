package fifthcolumn.n.modules;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;

public class SitBypass extends Module {
    public static final Identifier VERSION_CHECK = new Identifier("sit", "version_check");

    public static final EntityType<EntityImpl> SIT_ENTITY_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("sit", "entity_sit"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityImpl::new).dimensions(EntityDimensions.fixed(0.001f, 0.001f)).build()
    );

    private final SettingGroup sgGeneral;

    public final Setting<Integer> versionSetting;

    public SitBypass() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "Sit compat", "The SIT plugin");

        this.sgGeneral = this.settings.getDefaultGroup();

        this.versionSetting = this.sgGeneral.add(new IntSetting.Builder()
            .name("version")
            .description("version of sit")
            .defaultValue(20)
            .max(100)
            .build()
        );
    }

    public static void init() {
        EntityRendererRegistry.register(SIT_ENTITY_TYPE, EmptyEntityRenderer::new);
        ClientLoginNetworking.registerGlobalReceiver(VERSION_CHECK, (client, handler, buf, listenerAdder) -> {
            SitBypass sit = Modules.get().get(SitBypass.class);
            PacketByteBuf responseBuf = PacketByteBufs.create();
            responseBuf.writeInt((sit.versionSetting.get()).intValue());
            return CompletableFuture.completedFuture(responseBuf);
        });
    }

    private static class EntityImpl extends Entity {
        public EntityImpl(EntityType<?> type, World world) {
            super(type, world);
        }

        @Override
        protected void initDataTracker() {}

        @Override
        protected void readCustomDataFromNbt(NbtCompound nbt) {}

        @Override
        protected void writeCustomDataToNbt(NbtCompound nbt) {}
    }
}
