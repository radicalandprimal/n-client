package fifthcolumn.n.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

public class BlockPosUtils {
    public static BlockPos from(Position vec) {
        return BlockPosUtils.from(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BlockPos from(double x, double y, double z) {
        return new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public static BlockPos from(float x, float y, float z) {
        return new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }
}
