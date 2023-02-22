package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

@Component(role = IRuntimeScanner.class, hint = DefaultRuntimeScanner.HINT)
public class DefaultRuntimeScanner implements IRuntimeScanner {

	public static final String HINT = "default";

	@Override
	public boolean isTestExecution() {
		return false;
	}

}
