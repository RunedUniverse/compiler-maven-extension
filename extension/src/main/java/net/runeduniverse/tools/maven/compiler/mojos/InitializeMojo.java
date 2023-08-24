package net.runeduniverse.tools.maven.compiler.mojos;

import static net.runeduniverse.tools.maven.compiler.mojos.api.PlexusContextUtils.getPlexusComponentDescriptorMap;
import static net.runeduniverse.tools.maven.compiler.mojos.api.PlexusContextUtils.loadPlexusComponent;
import static net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils.getSessionContext;
import static net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils.putSessionComponent;
import static net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils.putSessionContext;
import static net.runeduniverse.tools.maven.compiler.mojos.api.SessionContextUtils.releaseSessionComponent;

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
import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.ExecutionMapper;
import net.runeduniverse.tools.maven.compiler.api.PipelineInitializer;

/**
 * Maps out all references of the source files to later be able to compile
 * source files in order
 *
 * @author VenaNocta
 * @phase initialize-compiler
 * @goal initialize
 */
public class InitializeMojo extends AbstractMojo {
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

	private Map<String, CompilerRuntime> compilerRuntimeContext = new LinkedHashMap<>(0);

	// RUNTIME PLEXUS COMPONENTS

	private Map<String, ComponentDescriptor<ExecutionMapper>> executionMapperDescriptors = new LinkedHashMap<>(2);

	private Map<String, ComponentDescriptor<PipelineInitializer>> referenceScannerDescriptors = new LinkedHashMap<>();

	// RUNTIME VALUES

	private ExecutionMapper executionMapper;

	private CompilerRuntime runtime;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		putSessionComponent(mvnSession, Log.class, getLog());

		// load context
		compilerRuntimeContext = getSessionContext(this.mvnSession, CompilerRuntime.class);
		if (compilerRuntimeContext == null)
			compilerRuntimeContext = new LinkedHashMap<>();
		putSessionContext(this.mvnSession, CompilerRuntime.class, compilerRuntimeContext);

		// seed with defaults
		executionMapperDescriptors
				.putAll(this.container.getComponentDescriptorMap(null, ExecutionMapper.class.getCanonicalName()));
		// seed with build plugins
		this.scanBuildPlugins();

		// select IExecutionMapper
		ComponentDescriptor<ExecutionMapper> executionMapperDescriptor = this.executionMapperDescriptors
				.get(this.mojoExecution.getExecutionId());
		if (executionMapperDescriptor == null)
			executionMapperDescriptor = executionMapperDescriptors.get("default");

		try {
			loadPlexusComponent(this.container, executionMapperDescriptor, (context, executionMapper) -> {
				InitializeMojo.this.executionMapper = executionMapper;
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
		releaseSessionComponent(this.mvnSession, CompilerRuntime.class,
				this.compilerRuntimeContext.put(this.runtime.getHint(), this.runtime));

		// identifyNodes
		getLog().info("identifying lifecycle process nodes");
		for (ComponentDescriptor<PipelineInitializer> descriptor : this.referenceScannerDescriptors.values()) {
			try {
				loadPlexusComponent(this.container, descriptor, (c, scanner) -> {
					scanner.initialize();
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}
		// scan
		getLog().info("scanning source-files");
		for (ComponentDescriptor<PipelineInitializer> descriptor : this.referenceScannerDescriptors.values()) {
			try {
				loadPlexusComponent(this.container, descriptor, (c, scanner) -> {
					scanner.scan();
				});
			} catch (ComponentLookupException e) {
				getLog().error(e);
			}
		}

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

				this.referenceScannerDescriptors.putAll(getPlexusComponentDescriptorMap(this.container, pluginRealm,
						null, PipelineInitializer.class.getCanonicalName()));

				this.executionMapperDescriptors.putAll(getPlexusComponentDescriptorMap(this.container, pluginRealm,
						null, ExecutionMapper.class.getCanonicalName()));

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
