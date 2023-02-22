package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;
import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

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
	 * @parameter property="compiler-extension.test.target.dir"
	 *            default-value="${project.build.testOutputDirectory}"
	 * @readonly
	 * @required
	 */
	private File testTargetDirectory;
	/**
	 * @parameter default-value="${mojoExecution}"
	 * @readonly
	 * @required
	 */
	private MojoExecution mojoExecution;
	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 */
	private MavenProject mvnProject;

	/**
	 * @component
	 */
	private ICompilerRuntime runtime;
	/**
	 * @component role="net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner"
	 */
	private Map<String, IRuntimeScanner> runtimeScannerMap;
	/**
	 * @component role="net.runeduniverse.tools.maven.compiler.api.IReferenceScanner"
	 */
	private Map<String, IReferenceScanner> refScanner;

	private IRuntimeScanner runtimeScanner;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// select IRuntimeScanner
		this.runtimeScanner = this.runtimeScannerMap.get(this.mojoExecution.getExecutionId());
		if (this.runtimeScanner == null)
			this.runtimeScanner = this.runtimeScannerMap.get("default");

		// initialize ICompilerRuntime
		if (this.runtimeScanner.isTestExecution()) {
			this.runtime.initialize(this.testSourceDirectory, this.testTargetDirectory);
		} else {
			this.runtime.initialize(this.sourceDirectory, this.targetDirectory);
		}

		getLog().info("mapping references of source-files");
		for (IReferenceScanner scanner : this.refScanner.values()) {
			scanner.logInfo(getLog());
		}
		// TODO collect collectors from compiler plugins and run those
		for (IReferenceScanner scanner : this.refScanner.values()) {
			scanner.logAnalisis(getLog());
		}
		getLog().info("finished mapping references of source-files");
	}
}
