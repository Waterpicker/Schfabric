package schematic;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

final class SchematicEntityImpl implements SchematicEntity {
	private final SchematicInfo info;
	private final double x;
	private final double y;
	private final double z;
	private final Identifier id;
	private final CompoundTag extraData;

	SchematicEntityImpl(SchematicInfo info, double x, double y, double z, String id, CompoundTag extraData) {
		this.info = info;
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = new Identifier(id);
		this.extraData = extraData;
	}

	@Override
	public double getRelativeX() {
		return this.x;
	}

	@Override
	public double getRelativeY() {
		return this.y;
	}

	@Override
	public double getRelativeZ() {
		return this.z;
	}

	@Override
	public Identifier getEntityType() {
		return this.id;
	}

	@Override
	public Optional<Entity> createEntity(World world, int x, int y, int z) {
		final CompoundTag entityTag = this.getExtraData();

		// Write the entity type id
		entityTag.putString("id", this.id.toString());

		// Write the entity's position
		final ListTag pos = new ListTag();
		pos.add(0, DoubleTag.of(this.info.getWidth() + this.info.getXOffset() + this.getRelativeX() + x));
		pos.add(1, DoubleTag.of(this.info.getHeight() + this.info.getYOffset() + this.getRelativeY() + y));
		pos.add(2, DoubleTag.of(this.info.getLength() + this.info.getZOffset() + this.getRelativeZ() + z));
		entityTag.put("Pos", pos);

		return EntityType.getEntityFromTag(entityTag, world);
	}

	@Override
	public CompoundTag getExtraData() {
		return this.extraData.copy(); // Provide a copy
	}
}
