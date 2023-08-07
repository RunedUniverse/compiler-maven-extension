package net.runeduniverse.tools.maven.compiler;

import net.runeduniverse.tools.maven.compiler.api.CompilerProperties;
import net.runeduniverse.tools.maven.compiler.api.MavenProperties;

public interface Properties extends MavenProperties, CompilerProperties {

	public static String VAL_PREFIX = "${";
	public static String VAL_POSTFIX = "}";

	public static String PLUGIN_KEY = GROUP_ID + ':' + ARTIFACT_ID;

	public interface PROJECT {
		public interface BUILD extends MavenProperties.PROJECT.BUILD {
		}
	}

	public interface LIFECYCLE {
		public interface COMPILER extends CompilerProperties.LIFECYCLE.COMPILER {
			public static String DEFAULT_VAL_SOURCE_DIR = VAL_PREFIX + Properties.PROJECT.BUILD.PARAM_SOURCE_DIR
					+ VAL_POSTFIX;
			public static String DEFAULT_VAL_TEST_SOURCE_DIR = VAL_PREFIX
					+ Properties.PROJECT.BUILD.PARAM_TEST_SOURCE_DIR + VAL_POSTFIX;
			public static String DEFAULT_VAL_TARGET_DIR = VAL_PREFIX + Properties.PROJECT.BUILD.PARAM_OUTPUT_DIR
					+ VAL_POSTFIX;
		}
	}

	public interface METAINF extends MavenProperties.METAINF {
		public interface MAVEN extends MavenProperties.METAINF.MAVEN {
		}

		public interface PLEXUS extends MavenProperties.METAINF.PLEXUS {
		}

		public interface SISU extends MavenProperties.METAINF.SISU {
		}
	}

}
