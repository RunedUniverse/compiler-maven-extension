package net.runeduniverse.tools.maven.compiler;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceFileScanner;
import net.runeduniverse.tools.maven.compiler.api.IReferenceMap;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;

@Component(role = IReferenceScanner.class)
public class DefaultReferenceScanner implements IReferenceScanner {

	protected ICompilerRuntime runtime;
	protected IReferenceMap references;
	protected Set<IReferenceFileScanner> scanner = new HashSet<>();

	public DefaultReferenceScanner() {
	}
	
	@Override
	public IReferenceScanner inject(ICompilerRuntime runtime) {
		this.runtime = runtime;
		return this;
	}

	@Override
	public IReferenceScanner inject(IReferenceMap resultMap) {
		this.references = resultMap;
		return this;
	}

	@Override
	public IReferenceScanner inject(Set<IReferenceFileScanner> fileScanner) {
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
