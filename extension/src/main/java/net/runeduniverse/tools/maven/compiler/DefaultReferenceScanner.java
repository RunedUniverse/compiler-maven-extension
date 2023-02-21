package net.runeduniverse.tools.maven.compiler;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.BuilderRuntime;
import net.runeduniverse.tools.maven.compiler.api.ReferenceFileScanner;
import net.runeduniverse.tools.maven.compiler.api.ReferenceMap;
import net.runeduniverse.tools.maven.compiler.api.ReferenceScanner;

@Component(role = ReferenceScanner.class)
public class DefaultReferenceScanner implements ReferenceScanner {

	protected BuilderRuntime runtime;
	protected ReferenceMap references;
	protected Set<ReferenceFileScanner> scanner = new HashSet<>();

	public DefaultReferenceScanner() {
	}
	
	@Override
	public ReferenceScanner inject(BuilderRuntime runtime) {
		this.runtime = runtime;
		return this;
	}

	@Override
	public ReferenceScanner inject(ReferenceMap resultMap) {
		this.references = resultMap;
		return this;
	}

	@Override
	public ReferenceScanner inject(Set<ReferenceFileScanner> fileScanner) {
		this.scanner.addAll(fileScanner);
		return this;
	}

	@Override
	public boolean logInfo(Log log) {
		log.info(new StringBuilder().append("source dir")
				.append(" = ")
				.append(this.runtime.getSourceDirectory())
				.append(", ")
				.append("target dir")
				.append(" = ")
				.append(this.runtime.getTargetDirectory())
				.toString());
		return true;
	}

	@Override
	public boolean logAnalisis(Log log) {
		// TODO Auto-generated method stub
		return true;
	}

}