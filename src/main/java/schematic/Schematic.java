package schematic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.mojang.serialization.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

public final class Schematic implements SchematicVisitor {
	private Version version;
	private SchematicInfo info;
	private SchematicMetadata metadata = EmptySchematicMetadata.INSTANCE;
	private byte[] blockData = new byte[0];
	private Int2ObjectBiMap<BlockState> blockPalette = new Int2ObjectBiMap<>(64); // Allocate a sane default for now
	private List<SchematicBlockEntity> blockEntities = new ArrayList<>();

	// Version 2 additions
	private byte[] biomeData = new byte[0];
	private Int2ObjectBiMap<Biome> biomePalette = new Int2ObjectBiMap<>(64); // Allocate a sane default for now

	private List<SchematicEntity> entities = new ArrayList<>();

	public static Schematic create(Schematic.Version version) {
		return new Schematic(Objects.requireNonNull(version, "Schematic version cannot be null"));
	}

	/**
	 * Loads a schematic
	 *
	 * @param path the path of the schematic
	 * @return a schematic
	 * @throws IOException if the schematic could not be read
	 * @throws SchematicReader.SchematicReadException if any issues occur while reading the schematic
	 */
	public static Schematic load(Path path) throws IOException, SchematicReader.SchematicReadException {
		final Schematic schematic = new Schematic();
		SchematicReader.accept(path, schematic);

		return schematic;
	}

	// Internal constructor
	private Schematic() {
	}

	private Schematic(Version version) {
		this.version = version;
	}

	public Schematic.Version getVersion() {
		return this.version;
	}

	public SchematicInfo getInfo() {
		return this.info;
	}

	public SchematicMetadata getMetadata() {
		return this.metadata;
	}

	public BlockState getBlockState(int x, int y, int z) {
		this.validateLocation(x, y, z);
		final byte blockDatum = this.blockData[this.wrap3DLocation(x, y, z)];

		return this.blockPalette.get(blockDatum);
	}

	public Biome getBiome(int x, int z) {
		if (this.supports(Capability.BIOMES)) {
			// Version 2 does not support 3d biomes. That will likely be in version 3
			this.validateLocation(x, 0, z);
			final byte biomeDatum = this.biomeData[this.wrap2DLocation(x, z)];

			return this.biomePalette.get(biomeDatum);
		}

		throw new UnsupportedOperationException("Biomes are not supported in this schematic!");
	}

	public List<SchematicEntity> getEntities() {
		if (this.supports(Capability.ENTITIES)) {
			return Collections.unmodifiableList(this.entities);
		}

		return Collections.emptyList();
	}

	public List<SchematicBlockEntity> getBlockEntities() {
		return Collections.unmodifiableList(this.blockEntities);
	}

	public boolean supports(Capability capability) {
		return this.version.supports(capability);
	}

	// VISITOR IMPLEMENTATION

	@Override
	public void visitVersion(Version version) {
		this.version = version;
	}

	@Override
	public void visitInfo(SchematicInfo info) {
		this.info = info;
	}

	@Override
	public void visitMetadata(SchematicMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public void visitBlockEntity(SchematicBlockEntity blockEntity) {
		this.blockEntities.add(blockEntity);
	}

	@Override
	public void visitEntity(SchematicEntity entity) {
		this.entities.add(entity);
	}

	@Override
	public BlockPaletteVisitor visitBlockPalette(int size) {
		this.blockPalette.clear(); // Prepare the palette
		return new BlockPaletteVisitorImpl();
	}

	@Override
	public void visitBlockData(byte[] blockData) {
		this.blockData = blockData;
	}

	@Override
	public BiomePaletteVisitor visitBiomePalette(int size) {
		this.biomePalette.clear(); // Prepare the palette
		return new BiomePaletteVisitorImpl();
	}

	@Override
	public void visitBiomeData(byte[] biomeData) {
		this.biomeData = biomeData;
	}

	// Util Methods

	// TODO: Explain each error
	private void validateLocation(int x, int y, int z) {
		final SchematicInfo info = this.info;

		if (x > info.getWidth() || 0 > x) {
			throw new IllegalArgumentException();
		}

		if (y > info.getHeight() || 0 > y) {
			throw new IllegalArgumentException();
		}

		if (z > info.getLength() || 0 > z) {
			throw new IllegalArgumentException();
		}
	}

	private int wrap3DLocation(int x, int y, int z) {
		// x + (z * Width) + (y * Width * Length)
		return x + ((z) * this.info.getWidth()) + (y * this.info.getWidth() * this.info.getLength());
	}

	private int wrap2DLocation(int x, int z) {
		// x + (z * Width)
		return x + (z * this.info.getWidth());
	}

	public enum Version {
		ONE(1),
		TWO(2, Capability.ENTITIES, Capability.BIOMES);

		private final int version;
		private final EnumSet<Capability> capabilities;

		Version(int version, Capability... capabilities) {
			this.version = version;

			if (capabilities.length == 0) {
				this.capabilities = EnumSet.noneOf(Capability.class);
			} else {
				this.capabilities = EnumSet.copyOf(Arrays.asList(capabilities));
			}
		}

		public boolean supports(Capability capability) {
			return this.capabilities.contains(capability);
		}

		public int getNumericalVersion() {
			return this.version;
		}
	}

	public enum Capability {
		/**
		 * The schematic supports entities.
		 *
		 * <p>Added by version two of the schematic specification.
		 */
		ENTITIES,
		/**
		 * The schematic supports biomes.
		 *
		 * <p>Added by version two of the schematic specification.
		 */
		BIOMES;
	}

	private class BlockPaletteVisitorImpl implements BlockPaletteVisitor {
		@Override
		public void visitBlockEntry(String registryId, Map<String, String> properties, int id) {
			// Assemble a rudimentary block state in tag form
			final CompoundTag tag = new CompoundTag();
			final CompoundTag propertiesTag = new CompoundTag();

			tag.putString("Name", registryId);

			for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
				propertiesTag.putString(propertyEntry.getKey(), propertyEntry.getValue());
			}

			tag.put("Properties", propertiesTag);

			// Data fix the block state to latest version
			final CompoundTag fixedState = (CompoundTag) SchematicReader.fixBlockState(Schemas.getFixer(), new Dynamic<>(NbtOps.INSTANCE, tag), Schematic.this.getInfo().getDataVersion()).getValue();
			Schematic.this.blockPalette.put(NbtHelper.toBlockState(fixedState), id);
		}
	}

	private class BiomePaletteVisitorImpl implements BiomePaletteVisitor {
		@Override
		public void visitBiome(String registryId, int id) {
			// TODO: Data fix biome names
			Schematic.this.biomePalette.put(BuiltinRegistries.BIOME.get(new Identifier(registryId)), id);
		}
	}
}
