package schematic;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;

public interface SchematicBlockEntity extends ExtraDataHolder {
	int getRelativeX();

	int getRelativeY();

	int getRelativeZ();

	Identifier getId();

	/**
	 * Creates a block entity.
	 *
	 * @param state the block state
	 * @param x the x position the schematic was pasted at
	 * @param y the y position the schematic was pasted at
	 * @param z the z position the schematic was pasted at
	 * @return a new block entity.
	 */
	BlockEntity createBlockEntity(BlockState state, int x, int y, int z);
}
