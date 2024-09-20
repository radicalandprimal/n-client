package fifthcolumn.n.origins;

import net.minecraft.util.Identifier;

public class TMOPackets {
    public static final String MODID = "toomanyorigins";
    public static final Identifier HANDSHAKE = TMOPackets.identifier("handshake");

    public static Identifier identifier(String path) {
        return Identifier.of(MODID, path);
    }
}
