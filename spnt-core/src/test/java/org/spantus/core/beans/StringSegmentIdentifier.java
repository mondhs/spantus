package org.spantus.core.beans;

/**
 *
 * @author mondhs
 * @since 0.3
 */
public class StringSegmentIdentifier implements SourceSegmentIdentifier<String>{
    @Override
    public String extractId(String object) {
        return object;
    }

    
}
