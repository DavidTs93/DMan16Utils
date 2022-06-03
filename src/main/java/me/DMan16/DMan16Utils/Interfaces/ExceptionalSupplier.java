package me.DMan16.DMan16Utils.Interfaces;

@FunctionalInterface
public interface ExceptionalSupplier<V> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    V get() throws Exception;
}
