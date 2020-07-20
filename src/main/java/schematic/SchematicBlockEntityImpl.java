package schematic;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;

final class SchematicBlockEntityImpl implements SchematicBlockEntity {
	private final SchematicInfo info;
	private final int x;
	private final int y;
	private final int z;
	private final Identifier id;
	private final CompoundTag extraData;

	SchematicBlockEntityImpl(SchematicInfo info, int x, int y, int z, String id, CompoundTag extraData) {
		this.info = info;
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = new Identifier(id);
		this.extraData = extraData;
	}

	@Override
	public int getRelativeX() {
		return this.x;
	}

	@Override
	public int getRelativeY() {
		return this.y;
	}

	@Override
	public int getRelativeZ() {
		return this.z;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public BlockEntity createBlockEntity(BlockState state, int x, int y, int z) {
		// Copy the extra data and write the id and real position to the block entity.
		final CompoundTag blockEntityTag = this.getExtraData();

		// Apply position of real block entity
		blockEntityTag.putInt("x", this.info.getWidth() + this.info.getXOffset() + this.getRelativeX() + x);
		blockEntityTag.putInt("y", this.info.getHeight() + this.info.getYOffset() + this.getRelativeY() + y);
		blockEntityTag.putInt("z", this.info.getLength() + this.info.getZOffset() + this.getRelativeZ() + z);

		blockEntityTag.putString("id", this.id.toString());

		return BlockEntity.createFromTag(state, blockEntityTag);
	}

	@Override
	public CompoundTag getExtraData() {
		return this.extraData.copy(); // Provide a copy
	}
}
