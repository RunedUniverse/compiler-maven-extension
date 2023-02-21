package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceFileScanner;
import net.runeduniverse.tools.maven.compiler.api.IReferenceMap;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;

/**
 * Maps out all references of the source files to later be able to compile
 * source files in order
 * 
 * @author Pl4yingNight
 * @phase scan-references
 * @goal scan-references
 */
public class ScanReferencesMojo extends AbstractMojo {
	// https://maven.apache.org/developers/mojo-api-specification.html
	// https://maven.apache.org/plugin-tools/maven-plugin-tools-java/index.html

	/**
	 * @parameter property="compiler-extension.main.source.dir"
	 *            default-value="${project.build.sourceDirectory}"
	 * @readonly
	 * @required
	 */
	private File sourceDirectory;
	/**
	 * @parameter property="compiler-extension.test.source.dir"
	 *            default-value="${project.build.testSourceDirectory}"
	 * @readonly
	 * @required
	 */
	private File testSourceDirectory;
	/**
	 * @parameter property="compiler-extension.target.dir"
	 *            default-value="${project.build.outputDirectory}"
	 * @readonly
	 * @required
	 */
	private File targetDirectory;
	/**
	 * @parameter default-value="${mojoExecution}"
	 * @readonly
	 * @required
	 */
	private MojoExecution mojoExecution;

	/**
	 * @component
	 */
	private IReferenceScanner scanner;
	/**
	 * @component
	 */
	private Map<String, IReferenceFileScanner> fileScanner;
	/**
	 * @component
	 */
	private ICompilerRuntime runtime;

	/**
	 * @component
	 */
	private IReferenceMap references;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.getLog()
				.info("MojoExecution: " + this.mojoExecution);

		if (this.runtime == null) {
			this.getLog()
					.warn("ICompilerRuntime is null");
		} else {
			this.runtime.setSourceDirectory(this.sourceDirectory);
			this.runtime.setTestSourceDirectory(this.testSourceDirectory);
			this.runtime.setTargetDirectory(this.targetDirectory);

			this.getLog()
					.info("ICompilerRuntime is <" + this.runtime.getClass()
							.getCanonicalName() + ">");
			this.getLog()
					.info("SourceDirectory: " + this.runtime.getSourceDirectory());
			this.getLog()
					.info("TestSourceDirectory: " + this.runtime.getTestSourceDirectory());
			this.getLog()
					.info("TargetDirectory: " + this.runtime.getTargetDirectory());
		}

		/*
		 * getLog().info((this.runtime==null)+""); scanner.inject(this.runtime)
		 * .inject(this.fileScanner) .inject(this.references);
		 */

		getLog().info("mapping references of source-files");
		// scanner.logInfo(getLog());
		// TODO collect collectors from compiler plugins and run those
		// scanner.logAnalisis(getLog());
		getLog().info("finished mapping references of source-files");
	}
}
