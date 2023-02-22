package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

@Component(role = IRuntimeScanner.class, hint = TestRuntimeScanner.HINT)
public class TestRuntimeScanner implements IRuntimeScanner {

	public static final String HINT = "test";

	@Override
	public boolean isTestExecution() {
		return true;
	}

}
