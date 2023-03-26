package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.util.Map;
import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;
import net.runeduniverse.tools.maven.compiler.api.IRuntimeScanner;

import static net.runeduniverse.tools.maven.compiler.api.mojos.ContextUtils.hasComponent;
import static net.runeduniverse.tools.maven.compiler.api.mojos.ContextUtils.lookupMap;

/**
 * Maps out all references of the source files to later be able to compile
 * source files in order
 * 
 * @author VenaNocta
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
	 * @parameter default-value="${session}"
	 * @readonly
	 */
	private MavenSession mvnSession;
	/**
	 * @parameter default-value="${project}"
	 * @readonly
	 */
	private MavenProject mvnProject;

	/**
	 * @component
	 */
	private PlexusContainer container;
	/**
	 * @component
	 */
	private ClassRealmManager classRealmManager;
	/**
	 * @component
	 */
	private MavenPluginManager pluginManager;
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

		this.analyzeScanner();

		getLog().info("");

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

	private void analyzeScanner() {
		// forceload all build plugins
		// maybe later only load plugins which are active in current lifecycle
		// execution?
		ClassRealm apiRealm = this.classRealmManager.getMavenApiRealm();

		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins())
			try {

				PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
						this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());

				this.pluginManager.setupPluginRealm(descriptor, this.mvnSession, null, null, null);
				ClassRealm pluginRealm = descriptor.getClassRealm();

				if (!hasComponent(container, pluginRealm, IReferenceScanner.class, apiRealm))
					continue;

				this.refScanner.putAll(lookupMap(this.container, pluginRealm, IReferenceScanner.class));

			} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
					| InvalidPluginDescriptorException | ComponentLookupException e) {
				e.printStackTrace();
			}

	}
}
