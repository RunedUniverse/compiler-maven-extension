package net.runeduniverse.tools.maven.compiler.mojos.api;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.eclipse.sisu.inject.Logs;

public interface SessionContextUtils {

	@SuppressWarnings("unchecked")
	public static <R> Map<String, R> getSessionContext(final MavenSession mvnSession, final Class<R> role) {
		Object obj = mvnSession.getCurrentProject()
				.getContextValue(role.getCanonicalName());
		if (obj == null)
			return null;
		try {
			return (Map<String, R>) obj;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <R> Map<String, R> putSessionContext(final MavenSession mvnSession, final Class<R> role,
			Map<String, R> context) {
		Map<String, R> oldContext = getSessionContext(mvnSession, role);
		mvnSession.getCurrentProject()
				.setContextValue(role.getCanonicalName(), context);
		return oldContext;
	}

	public static <R> void releaseSessionContext(final MavenSession mvnSession, final Class<R> role) {
		Map<String, R> context = getSessionContext(mvnSession, role);
		if (context != null)
			for (R component : context.values())
				SessionContextUtils.releaseSessionComponent(mvnSession, role, component);
		mvnSession.getCurrentProject()
				.setContextValue(role.getCanonicalName(), null);
	}

	public static <R> R lookupSessionComponent(final MavenSession mvnSession, final Class<R> role) {
		Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null)
			return null;
		R val = map.get("default");
		if (val != null)
			return val;
		for (Iterator<R> i = map.values()
				.iterator(); i.hasNext();) {
			val = i.next();
			if (val != null)
				return val;
		}
		return null;
	}

	public static <R> R lookupSessionComponent(final MavenSession mvnSession, final Class<R> role, final String hint) {
		Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null)
			return null;
		return map.get(hint);
	}

	public static <R, T extends R> void putSessionComponent(final MavenSession mvnSession, final Class<R> role,
			T component) {
		putSessionComponent(mvnSession, role, "default", component);
	}

	public static <R, T extends R> void putSessionComponent(final MavenSession mvnSession, final Class<R> role,
			final String hint, T component) {
		Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null) {
			map = new LinkedHashMap<>();
			putSessionContext(mvnSession, role, map);
		}
		map.put(hint, component);
	}

	public static <R, T extends R> void releaseSessionComponent(final MavenSession mvnSession, final Class<R> role,
			T component) {
		Map<String, R> map = getSessionContext(mvnSession, role);
		if (component != null) {
			if (component instanceof Startable)
				try {
					((Startable) component).stop();
				} catch (StoppingException e) {
					Logs.catchThrowable(e);
				}
			if (component instanceof Disposable)
				((Disposable) component).dispose();
		}
		if (map == null)
			return;
		map.values()
				.remove(component);
	}

}
