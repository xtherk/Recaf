package me.coley.recaf.workspace;

import com.google.common.base.MoreObjects;
import me.coley.recaf.util.ClasspathUtil;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 * Importable instrumentation resource.
 *
 * @author Matt
 */
public class InstrumentationResource extends JavaResource {
	private final Instrumentation instrumentation;

	/**
	 * Constructs an instrumentation resource.
	 *
	 * @param instrumentation
	 * 		Instrumentation instance provided by the tools api.
	 */
	public InstrumentationResource(Instrumentation instrumentation) {
		super(ResourceKind.INSTRUMENTATION);
		this.instrumentation = instrumentation;
	}

	@Override
	protected Map<String, byte[]> loadClasses() throws IOException {
		Map<String, byte[]> classes = new HashMap<>();
		// iterate over loaded classes
		for(Class<?> c : instrumentation.getAllLoadedClasses()) {
			String name = Type.getInternalName(c);
			// skip specified prefixes
			if(shouldSkip(name))
				continue;
			String path = name.concat(".class");
			ClassLoader loader = MoreObjects.firstNonNull(c.getClassLoader(), ClasspathUtil.scl);
			try(InputStream in = loader.getResourceAsStream(path)) {
				if(in != null) {
					classes.put(name, IOUtils.toByteArray(in));
				}
			}
		}
		return classes;
	}

	@Override
	protected Map<String, byte[]> loadResources() {
		return Collections.emptyMap();
	}
}