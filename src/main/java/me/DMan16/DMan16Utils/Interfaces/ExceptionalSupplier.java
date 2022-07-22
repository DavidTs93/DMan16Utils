package me.DMan16.DMan16Utils.Interfaces;

/**
 * See {@link java.util.function.Supplier}
 */
@FunctionalInterface
public interface ExceptionalSupplier<V> {
    V get() throws Exception;
}
