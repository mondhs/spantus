package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.exception.ProcessingException;
import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.core.wav.AudioManagerFactory;

public class PlayCmd extends AbsrtactCmd {
	private SelectionDto dto;

	public PlayCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
		this.dto = new SelectionDto();
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.play.name(),
				GlobalCommands.sample.selectionChanged.name());
	}

	public String execute(SpantusWorkInfo ctx) {
		if (GlobalCommands.sample.selectionChanged.name().equals(
				getCurrentEvent().getCmd())) {
			this.dto = ((SelectionDto) getCurrentEvent().getValue());
			return null;
		}

		if (dto == null) {
			this.dto = new SelectionDto();
		}
		try {
			Float from = null;
			Float length = null;
			from = dto.getFrom() != null ? dto.getFrom().floatValue() : null;
			length = dto.getFrom() != null ? dto.getLength().floatValue()
					: null;
			AudioManagerFactory.createAudioManager()
					.play(ctx.getProject().getSample().getCurrentFile(), from,
							length);
		} catch (ProcessingException e) {
			error(e.getLocalizedMessage(), ctx, e);
		}
		return null;
	}

}
