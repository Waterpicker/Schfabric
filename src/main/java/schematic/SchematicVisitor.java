package schematic;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * A visitor used to visit a schematic.
 *
 * <p>This visitor is designed for use with version two of the Sponge Schematic Specification, but is compatable with version one.
 *
 * <p>All methods in this visitor are {@code default} and new methods will be {@code default} to preserve forwards compatibility if new versions of the schematic specification are released.
 *
 * @see <a href="https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md">Sponge Schematic Specification Version Two</a>
 */
public interface SchematicVisitor {
	/**
	 * Visit the schematic version.
	 *
	 * @param version the version of the schematic
	 */
	default void visitVersion(Schematic.Version version) {
	}

	/**
	 * Visit the schematic info.
	 *
	 * @param info the schematic info
	 */
	default void visitInfo(SchematicInfo info) {
	}

	/**
	 * Visit the schematic metadata.
	 *
	 * @param metadata the schematic metadata
	 */
	default void visitMetadata(SchematicMetadata metadata) {
	}

	/**
	 * Visit a schematic block entity.
	 *
	 * @param blockEntity the block entity
	 */
	default void visitBlockEntity(SchematicBlockEntity blockEntity) {
	}

	/**
	 * Visit a schematic entity.
	 *
	 * @param entity the entity
	 */
	default void visitEntity(SchematicEntity entity) {
	}

	/**
	 * Visit a block palette.
	 *
	 * @param size the size of the palette
	 * @return a palette visitor to visit the palette with. {@code null} if this visitor does not want to visit the palette.
	 */
	@Nullable
	default BlockPaletteVisitor visitBlockPalette(int size) {
		return null;
	}

	default void visitBlockData(byte[] blockData) {
	}

	/**
	 * Visit a biome palette.
	 *
	 * @param size the size of the palette
	 * @return a palette visitor to visit the palette with. {@code null} if this visitor does not want to visit the palette.
	 */
	@Nullable
	default BiomePaletteVisitor visitBiomePalette(int size) {
		return null;
	}

	default void visitBiomeData(byte[] biomeData) {
	}

	interface BlockPaletteVisitor {
		/**
		 * Visit a palette entry
		 *
		 * @param registryId the registry id of the block
		 * @param properties the properties of the block state
		 * @param id the packed numerical id used in the schematic data.
		 */
		default void visitBlockEntry(String registryId, Map<String, String> properties, int id) {
		}
	}

	interface BiomePaletteVisitor {
		default void visitBiome(String registryId, int id) {
		}
	}
}
