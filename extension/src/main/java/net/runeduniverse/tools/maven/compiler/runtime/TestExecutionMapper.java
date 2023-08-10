package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ExecutionMapper;

@Component(role = ExecutionMapper.class, hint = TestExecutionMapper.HINT)
public class TestExecutionMapper extends AExecutionMapper {

	public static final String HINT = "test";

	@Override
	protected ExecutionMapper getThis() {
		return this;
	}

	@Override
	public boolean isTestExecution() {
		return true;
	}

	@Override
	public CompilerRuntime createRuntime() {
		return new DefaultCompilerRuntime(this.testSourceDirectory, this.testTargetDirectory);
	}

}
