package net.runeduniverse.tools.maven.compiler.api;

public interface CompilerProperties {
	public static String GROUP_ID = "net.runeduniverse.tools.maven.compiler";
	public static String ARTIFACT_ID = "compiler-maven-extension";
	public static String PREFIX_ID = "compiler";

	public interface LIFECYCLE {
		public interface BUILDER {
			public static String LIFECYCLE_HINT = "compiler";

			public static String EXECUTION_DEFAULT_ID = "default";
			public static String EXECUTION_TEST_ID = "test";

			public static String PARAM_SOURCE_DIR = "compiler-extension.main.source.dir";
			public static String PARAM_TEST_SOURCE_DIR = "compiler-extension.test.source.dir";
			public static String PARAM_TARGET_DIR = "compiler-extension.target.dir";
		}
	}
}
