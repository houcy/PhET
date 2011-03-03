// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

/**
 * @author Sam Reid
 */
public abstract class Option<T> {
    public abstract T get();

    public abstract boolean isSome();

    public boolean isNone() {
        return !isSome();
    }

    public static class None<T> extends Option<T> {
        public T get() {
            throw new UnsupportedOperationException( "Cannot get value on none." );
        }

        public boolean isSome() {
            return false;
        }
    }

    public static class Some<T> extends Option<T> {
        private final T value;

        public Some( T value ) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public boolean isSome() {
            return true;
        }
    }
}
