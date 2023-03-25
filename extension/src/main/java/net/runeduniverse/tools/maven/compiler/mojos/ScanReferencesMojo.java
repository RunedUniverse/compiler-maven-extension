package net.runeduniverse.tools.maven.compiler.mojos;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
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
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

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
	private MavenPluginManager pluginManager;
	/**
	 * @component
	 */
	private BuildPluginManager buildPluginManager;
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

		// forceload all build plugins
		// maybe later only load plugins which are active in current lifecycle
		// execution?
		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins()) {
			try {
				PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
						this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());

				getLog().warn(descriptor.getGroupId() + ":" + descriptor.getArtifactId());
				// getLog().warn("Dependencies:");
				// for (ComponentDependency dependency : descriptor.getDependencies()) {
				// getLog().error(dependency.getGroupId()+":"+dependency.getArtifactId());
				// }
				// getLog().warn("Artifacts:");
				// if (descriptor.getIntroducedDependencyArtifacts() != null)
				// for (Artifact artifact : descriptor.getIntroducedDependencyArtifacts()) {
				// getLog().error(artifact.getGroupId() + ":" + artifact.getArtifactId());
				// }
				getLog().warn("Components:");
				for (ComponentDescriptor<?> cDescriptor : descriptor.getComponents()) {
					getLog().error(cDescriptor.getRole());
				}

				ClassRealm pluginRealm = this.buildPluginManager.getPluginRealm(this.mvnSession, descriptor);
				getLog().warn("Realm:");
				URL[] urls = pluginRealm.getURLs();
				for (int i = 0; i < urls.length; i++) {
					getLog().error(urls[i].getFile());
				}
				// if we are unable to detect if the interface <IReferenceScanner> is
				// implemented via plexus throw it all into the PackageScanner and scan it this
				// way -> it will find it as long as the file exists
			} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
					| InvalidPluginDescriptorException e) {
				e.printStackTrace();
			}
		}

		ClassRealm currentRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		currentRealm.display();
		getLog().info("");

		ClassWorld classWorld = currentRealm.getWorld();
		for (ClassRealm realm : classWorld.getRealms()) {
			if (!realm.getId()
					.startsWith("plugin"))
				continue;
			getLog().warn(realm.getId());
			// realm.display();

		}

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
}
