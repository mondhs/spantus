package org.spantus.core.beans;

/**
 *
 * @author mondhs
 */
public interface SourceSegmentIdentifier<T> {
    public String extractId(T object);
}
