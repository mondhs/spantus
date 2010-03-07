package org.spantus.work.ui.cmd;

import java.util.Map;
import java.util.Set;

public interface CommandBuilderService {

	public abstract Map<String, Set<SpantusWorkCommand>> createSystem(
			CommandExecutionFacade executionFacade);

	public abstract Map<String, Set<SpantusWorkCommand>> create(
			CommandExecutionFacade executionFacade);

}