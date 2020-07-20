package schematic;

final class UnimplementedException extends RuntimeException {
	UnimplementedException(String className, String method) {
		super("An implementation of " + className + " with method " + method + " has not been implemented");
	}
}
