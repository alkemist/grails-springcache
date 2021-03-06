/*
 * Copyright 2009 Rob Fletcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springcache.key;

import grails.plugin.springcache.CacheKey;
import java.util.Arrays;

/**
 * Builder used to compute the hash and checksum used for an immutable cache key.
 * <p/>
 * Based on org.springmodules.cache.key.HashCodeCalculator from Spring Modules Cache (see
 * https://springmodules.dev.java.net/) by Omar Irbouh and Alex Ruiz.
 */
public class CacheKeyBuilder {

	private static final int INITIAL_HASH = 17;
	private static final int MULTIPLIER = 31;

	private int count = 0;
	private long checksum = 0L;
	private int hash = INITIAL_HASH;

	public CacheKeyBuilder append(int i) {
		hash = hash * MULTIPLIER + i;
		count++;
		checksum += (count * i);
		return this;
	}

	public CacheKeyBuilder append(Object o) {
		if (o == null) {
			appendNull();
		} else if (o.getClass().isArray()) {
			if (o instanceof Object[]) {
				append(Arrays.deepHashCode((Object[])o)); // This will fail if o contains itself
			} else if (o instanceof boolean[]) {
				append(Arrays.hashCode((boolean[])o));
			} else if (o instanceof byte[]) {
				append(Arrays.hashCode((byte[])o));
			} else if (o instanceof char[]) {
				append(Arrays.hashCode((char[])o));
			} else if (o instanceof double[]) {
				append(Arrays.hashCode((double[])o));
			} else if (o instanceof float[]) {
				append(Arrays.hashCode((float[])o));
			} else if (o instanceof int[]) {
				append(Arrays.hashCode((int[])o));
			} else if (o instanceof long[]) {
				append(Arrays.hashCode((long[])o));
			} else if (o instanceof short[]) {
				append(Arrays.hashCode((short[])o));
			} else {
				throw new UnHandleableCacheKeyComponentException(o.getClass());
			}
		} else {
			append(o.hashCode());
		}

		return this;
	}

	public CacheKeyBuilder append(Object[] oarr) {
		for (Object o : oarr) {
			append(o);
		}
		return this;
	}

	protected CacheKeyBuilder appendNull() {
		// Unsure what the best strategy is here…
		// Append 0 or just do nothing?
		
		return this;
	}

	public void leftShift(Object o) {
		append(o);
	}

	public void leftShift(Object[] oarr) {
		append(oarr);
	}

	public CacheKey toCacheKey() {
		return new CacheKey(hash, checksum);
	}

	protected static class UnHandleableCacheKeyComponentException extends IllegalStateException {
		public UnHandleableCacheKeyComponentException(Class type) {
			super("Unable to handle object of type '" + type + "'");
		}
	}
}
