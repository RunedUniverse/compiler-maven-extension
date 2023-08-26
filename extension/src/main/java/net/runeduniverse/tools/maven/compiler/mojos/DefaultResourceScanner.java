package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ResourceScanner;
import net.runeduniverse.tools.maven.compiler.mojos.api.ResourceCollector;
import net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Pipeline;
import net.runeduniverse.tools.maven.compiler.pipeline.api.Resource;

@Component(role = ResourceScanner.class, hint = DefaultResourceScanner.HINT)
public class DefaultResourceScanner implements ResourceScanner {

	public static final String HINT = "default";

	@Requirement
	private MavenSession mvnSession;

	@Requirement
	private Pipeline pipeline;

	protected Log log;

	@Override
	public boolean scan(CompilerRuntime runtime) {
		this.log = SessionContextUtils.loadSessionComponent(this.mvnSession, Log.class);
		this.log.info("Scanning SourceDirectory: " + runtime.getSourceDirectory());

		Path sources = runtime.getSourceDirectory()
				.toPath();
		ResourceCollector collector = new ResourceCollector(this.pipeline.getResourceIndex(this.mvnSession));

		try {
			Files.walkFileTree(sources, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, collector);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<Resource> resources = collector.getResources();
		for (Resource resource : resources) {
			this.pipeline.addResourceToNextNodeContext(this.mvnSession, null, resource);
		}

		this.log.info("  " + resources.size() + " Resources found");
		this.log.info("");
		return true;
	}

}
