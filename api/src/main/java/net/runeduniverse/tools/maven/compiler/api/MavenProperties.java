package net.runeduniverse.tools.maven.compiler.api;

public interface MavenProperties {

	public static final String DEFAULT_LIFECYCLE_ID = "default";
	public static final String DEFAULT_EXECUTION_ID = "default";

	public interface PROJECT {

		public interface BUILD {

			public static final String PARAM_SOURCE_DIR = "project.build.sourceDirectory";
			public static final String PARAM_OUTPUT_DIR = "project.build.outputDirectory";

			public static final String PARAM_TEST_SOURCE_DIR = "project.build.testSourceDirectory";
			public static final String PARAM_TEST_OUTPUT_DIR = "project.build.testOutputDirectory";

			public static final String PARAM_SCRIPT_SOURCE_DIR = "project.build.scriptSourceDirectory";

		}

	}

	public interface METAINF {

		public static final String PATH = "META-INF/";

		public interface MAVEN {

			public static final String PATH = METAINF.PATH + "maven/";
			public static final String PLUGIN_DESCRIPTOR = PATH + "plugin.xml";
			public static final String EXTENSIONS = PATH + "extension.xml";

		}

		public interface PLEXUS {

			public static final String PATH = METAINF.PATH + "plexus/";
			public static final String COMPONENTS = PATH + "components.xml";

		}

		public interface SISU {

			public static final String PATH = METAINF.PATH + "sisu/";
			public static final String NAMED_CONFIG = PATH + "javax.inject.Named";

		}

	}

}
