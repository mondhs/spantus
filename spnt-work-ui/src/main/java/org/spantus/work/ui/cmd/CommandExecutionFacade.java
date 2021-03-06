package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SampleChangeListener;

public interface CommandExecutionFacade extends SampleChangeListener,
		ReloadableComponent {
	public void fireEvent(Enum<?> enumCmdName);
	public void fireEvent(Enum<?> enumCmdName, Object object);
	public void fireEvent(String cmdName);
	public void fireEvent(String cmdName, Object object);
	public void showError(Throwable throwable, String message);
	public void newProject();
}
