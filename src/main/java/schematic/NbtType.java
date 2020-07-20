package schematic;

import net.minecraft.nbt.Tag;

/**
 * Represents numerical ids of a type of {@link Tag tag}.
 *
 * <p>This is an internal copy of Fabric API's own NbtType since we do not depend on fabric api.
 */
final class NbtType {
	public static final int END = 0;
	public static final int BYTE = 1;
	public static final int SHORT = 2;
	public static final int INT = 3;
	public static final int LONG = 4;
	public static final int FLOAT = 5;
	public static final int DOUBLE = 6;
	public static final int BYTE_ARRAY = 7;
	public static final int STRING = 8;
	public static final int LIST = 9;
	public static final int COMPOUND = 10;
	public static final int INT_ARRAY = 11;
	public static final int LONG_ARRAY = 12;
	public static final int NUMBER = 99;

	private NbtType() {
	}
}
