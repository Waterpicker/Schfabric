package schematic;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface SchematicEntity extends ExtraDataHolder {
	double getRelativeX();

	double getRelativeY();

	double getRelativeZ();

	Identifier getEntityType();

	/**
	 * Creates a minecraft entity using the schematic data.
	 *
	 * @param world the world to create the entity in
	 * @param x the x position the schematic was pasted at
	 * @param y the y position the schematic was pasted at
	 * @param z the z position the schematic was pasted at
	 * @return a new entity.
	 */
	Optional<Entity> createEntity(World world, int x, int y, int z);
}
