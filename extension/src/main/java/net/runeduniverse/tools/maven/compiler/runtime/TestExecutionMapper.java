package net.runeduniverse.tools.maven.compiler.runtime;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IExecutionMapper;

@Component(role = IExecutionMapper.class, hint = TestExecutionMapper.HINT)
public class TestExecutionMapper extends AExecutionMapper {

	public static final String HINT = "test";

	@Override
	protected IExecutionMapper getThis() {
		return this;
	}

	@Override
	public boolean isTestExecution() {
		return true;
	}

	@Override
	public ICompilerRuntime createRuntime() {
		return new CompilerRuntime(this.testSourceDirectory, this.testTargetDirectory);
	}

}
