package net.runeduniverse.tools.maven.compiler;

import java.io.File;

import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;

@Component(role = ICompilerRuntime.class, hint = "default", instantiationStrategy = "singleton")
public class CompilerRuntime implements ICompilerRuntime {

	private File sourceDirectory;
	private File testSourceDirectory;
	private File targetDirectory;

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public File getTestSourceDirectory() {
		return testSourceDirectory;
	}

	public File getTargetDirectory() {
		return targetDirectory;
	}

	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setTestSourceDirectory(File testSourceDirectory) {
		this.testSourceDirectory = testSourceDirectory;
	}

	public void setTargetDirectory(File targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

}
