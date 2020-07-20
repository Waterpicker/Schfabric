package schematic.test;

import java.nio.file.Path;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import schematic.Schematic;

public final class SchematicTests implements ModInitializer {
	private static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer("schfabric-testmod").orElseThrow(() -> {
		return new RuntimeException("Failed to get the test mod's mod container! This is a fabric loader issue.");
	});

	@Override
	public void onInitialize() {
		final Path test = SchematicTests.getFile("test.schem");
		Schematic load = null;

		try {
			load = Schematic.load(test);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (load != null) {
			System.out.println(load.getVersion().getNumericalVersion());
		}
	}

	private static Path getFile(String path) {
		return MOD_CONTAINER.getPath(path);
	}
}
