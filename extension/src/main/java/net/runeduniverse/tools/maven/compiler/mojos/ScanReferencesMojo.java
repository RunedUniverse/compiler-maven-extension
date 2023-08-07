package net.runeduniverse.tools.maven.compiler.mojos;

import static net.runeduniverse.tools.maven.compiler.mojos.api.ContextUtils.getComponentDescriptorMap;
import static net.runeduniverse.tools.maven.compiler.mojos.api.ContextUtils.loadComponent;
import static net.runeduniverse.tools.maven.compiler.mojos.api.CurrentContextUtils.addComponent;
import static net.runeduniverse.tools.maven.compiler.mojos.api.CurrentContextUtils.getContext;
import static net.runeduniverse.tools.maven.compiler.mojos.api.CurrentContextUtils.putContext;
import static net.runeduniverse.tools.maven.compiler.mojos.api.CurrentContextUtils.releaseComponent;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.extension.internal.CoreExports;
import org.apache.maven.extension.internal.CoreExtensionEntry;
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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;
import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceScanner;
import net.runeduniverse.tools.maven.compiler.api.IExecutionMapper;

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

	// RUNTIME CONTEXT

	private Map<String, ICompilerRuntime> compilerRuntimeContext = new LinkedHashMap<>(0);

	// RUNTIME PLEXUS COMPONENTS

	private Map<String, ComponentDescriptor<IExecutionMapper>> executionMapperDescriptors = new LinkedHashMap<>(2);

	private Map<String, ComponentDescriptor<IReferenceScanner>> referenceScannerDescriptors = new LinkedHashMap<>();

	// RUNTIME VALUES

	private IExecutionMapper executionMapper;

	private ICompilerRuntime runtime;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		addComponent(mvnSession, Log.class, "default", getLog());

		// load context
		compilerRuntimeContext = getContext(this.mvnSession, ICompilerRuntime.class);
		if (compilerRuntimeContext == null)
			compilerRuntimeContext = new LinkedHashMap<String, ICompilerRuntime>();
		putContext(this.mvnSession, ICompilerRuntime.class, compilerRuntimeContext);

		// seed with defaults
		executionMapperDescriptors
				.putAll(this.container.getComponentDescriptorMap(null, IExecutionMapper.class.getCanonicalName()));
		// seed with build plugins
		this.scanBuildPlugins();

		// select IExecutionMapper
		ComponentDescriptor<IExecutionMapper> executionMapperDescriptor = this.executionMapperDescriptors
				.get(this.mojoExecution.getExecutionId());
		if (executionMapperDescriptor == null)
			executionMapperDescriptor = executionMapperDescriptors.get("default");

		try {
			loadComponent(this.container, executionMapperDescriptor, (context, executionMapper) -> {
				ScanReferencesMojo.this.executionMapper = executionMapper;
			});
		} catch (ComponentLookupException e) {
			throw new MojoExecutionException(
					"IExecutionMapper<" + executionMapperDescriptor.getImplementation() + "> failed to load!", e);
		}

		// seed mapper
		this.executionMapper.setSourceDirectory(this.sourceDirectory)
				.setTargetDirectory(this.targetDirectory)
				.setTestSourceDirectory(this.testSourceDirectory)
				.setTestTargetDirectory(this.testTargetDirectory);

		// initialize ICompilerRuntime
		this.runtime = this.executionMapper.createRuntime();
		releaseComponent(this.mvnSession, ICompilerRuntime.class,
				this.compilerRuntimeContext.put(this.runtime.getHint(), this.runtime));

		// identifyNodes
		getLog().info("identifying lifecycle process nodes");
		for (ComponentDescriptor<IReferenceScanner> descriptor : this.referenceScannerDescriptors.values()) {
			try {
				loadComponent(this.container, descriptor, (c, scanner) -> {
					scanner.identifyNodes();
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		getLog().info("");
		// scan
		getLog().info("mapping references of source-files");
		for (ComponentDescriptor<IReferenceScanner> descriptor : this.referenceScannerDescriptors.values()) {
			try {
				loadComponent(this.container, descriptor, (c, scanner) -> {
					scanner.scan();
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		getLog().info("");
		// TODO collect collectors from compiler plugins and run those
		for (ComponentDescriptor<IReferenceScanner> descriptor : this.referenceScannerDescriptors.values()) {
			try {
				loadComponent(this.container, descriptor, (c, scanner) -> {
					scanner.logAnalisis(getLog());
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		getLog().info("finished mapping references of source-files");

		// debug:
		// this.buildRealm();
	}

	private void scanBuildPlugins() {
		ClassRealm apiRealm = this.classRealmManager.getMavenApiRealm();

		for (Plugin mvnPlugin : this.mvnProject.getBuildPlugins())
			try {
				PluginDescriptor descriptor = this.pluginManager.getPluginDescriptor(mvnPlugin,
						this.mvnProject.getRemotePluginRepositories(), this.mvnSession.getRepositorySession());
				// forceload all build plugins
				this.pluginManager.setupPluginRealm(descriptor, this.mvnSession, null, null, null);
				ClassRealm pluginRealm = descriptor.getClassRealm();

				this.referenceScannerDescriptors.putAll(getComponentDescriptorMap(this.container, pluginRealm, null,
						IReferenceScanner.class.getCanonicalName()));

				this.executionMapperDescriptors.putAll(getComponentDescriptorMap(this.container, pluginRealm, null,
						IExecutionMapper.class.getCanonicalName()));

			} catch (PluginResolutionException | PluginManagerException | PluginDescriptorParsingException
					| InvalidPluginDescriptorException e) {
				getLog().error(e);
			}
	}

	private static CompoundTree toTree(ClassWorld world) {
		Map<ClassRealm, CompoundTree> treeMap = new LinkedHashMap<>();
		Set<ClassRealm> rootlessRealms = new LinkedHashSet<>();

		for (ClassRealm realm : world.getRealms()) {
			CompoundTree t = new CompoundTree(realm.getId());
			treeMap.put(realm, t);
			rootlessRealms.add(realm);
		}
		for (ClassRealm r : treeMap.keySet()) {
			CompoundTree t = treeMap.get(r.getParentClassLoader());
			if (t == null)
				continue;
			t.append(treeMap.get(r));
			rootlessRealms.remove(r);
		}
		CompoundTree loadedRealmsTree = new CompoundTree("ClassWorld");
		for (ClassRealm r : rootlessRealms)
			loadedRealmsTree.append(treeMap.get(r));
		return loadedRealmsTree;
	}

	private ClassRealm buildCompilerBuildRealm(final ClassWorld world, final ClassRealm parentRealm) {
		String ID = "build>net.runeduniverse.tools.maven.compiler:compiler-maven-extension";
		ClassRealm compilerBuildRealm;
		try {
			compilerBuildRealm = world.newRealm(ID, null);
		} catch (DuplicateRealmException e) {
			return world.getClassRealm(ID);
		}

		compilerBuildRealm.setParentRealm(parentRealm);

		CoreExports exports = new CoreExports(CoreExtensionEntry.discoverFrom(world.getClassRealm("plexus.core")));
		for (Entry<String, ClassLoader> entry : exports.getExportedPackages()
				.entrySet()) {
			getLog().warn("ID: " + entry.getKey() + "\tCL: " + entry.getValue());
		}

		return compilerBuildRealm;
	}

	private void buildRealm() {
		getLog().warn("rebuilding REALM");

		ClassRealm curRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		ClassWorld world = curRealm.getWorld();

		getLog().info(toTree(world).toString());

		ClassRealm compilerBuildRealm = this.buildCompilerBuildRealm(world, curRealm);

		getLog().info(toTree(world).toString());
	}

}
