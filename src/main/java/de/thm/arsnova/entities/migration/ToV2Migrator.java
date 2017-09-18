package de.thm.arsnova.entities.migration;

import de.thm.arsnova.entities.ChoiceQuestionContent;
import de.thm.arsnova.entities.Entity;
import de.thm.arsnova.entities.UserProfile;
import de.thm.arsnova.entities.migration.v2.Answer;
import de.thm.arsnova.entities.migration.v2.AnswerOption;
import de.thm.arsnova.entities.migration.v2.Comment;
import de.thm.arsnova.entities.migration.v2.Content;
import de.thm.arsnova.entities.migration.v2.LoggedIn;
import de.thm.arsnova.entities.migration.v2.MotdList;
import de.thm.arsnova.entities.migration.v2.Room;
import de.thm.arsnova.entities.migration.v2.VisitedRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToV2Migrator {
	private void copyCommonProperties(final Entity from, final Entity to) {
		to.setId(from.getId());
		to.setRevision(from.getRevision());
	}

	public LoggedIn migrateLoggedIn(final UserProfile from) {
		final LoggedIn to = new LoggedIn();
		copyCommonProperties(from, to);
		to.setUser(from.getLoginId());
		to.setTimestamp(from.getLastLogin());
		to.setVisitedSessions(from.getRoomHistory().stream()
				.map(entry -> new VisitedRoom())
				.collect(Collectors.toList()));

		return to;
	}

	public MotdList migrateMotdList(final UserProfile from) {
		final MotdList to = new MotdList();
		copyCommonProperties(from, to);
		to.setUsername(from.getLoginId());
		to.setMotdkeys(String.join(",", from.getAcknowledgedMotds()));

		return to;
	}

	public Room migrate(final de.thm.arsnova.entities.Room from, final UserProfile owner) {
		final Room to = new Room();
		copyCommonProperties(from, to);
		to.setKeyword(from.getShortId());
		to.setCreator(owner.getLoginId());
		to.setName(from.getName());
		to.setShortName(from.getAbbreviation());
		to.setActive(!from.isClosed());

		return to;
	}

	public Content migrate(final de.thm.arsnova.entities.Content from) {
		final Content to = new Content();
		copyCommonProperties(from, to);
		to.setSessionId(from.getRoomId());
		to.setSubject(from.getSubject());
		to.setText(from.getBody());
		to.setQuestionType(from.getFormat());
		to.setQuestionVariant(from.getGroup());

		if (from instanceof ChoiceQuestionContent) {
			final ChoiceQuestionContent fromChoiceQuestionContent = (ChoiceQuestionContent) from;
			final List<AnswerOption> toOptions = new ArrayList<>();
			to.setPossibleAnswers(toOptions);
			for (int i = 0; i < fromChoiceQuestionContent.getOptions().size(); i++) {
				AnswerOption option = new AnswerOption();
				option.setText(fromChoiceQuestionContent.getOptions().get(1).getLabel());
				option.setValue(fromChoiceQuestionContent.getOptions().get(1).getPoints());
				option.setCorrect(fromChoiceQuestionContent.getCorrectOptionIndexes().contains(i));
				toOptions.add(option);
			}
		}

		return to;
	}

	public Answer migrate(final de.thm.arsnova.entities.ChoiceAnswer from, final de.thm.arsnova.entities.ChoiceQuestionContent content, final UserProfile creator) {
		final Answer to = new Answer();
		copyCommonProperties(from, to);
		to.setQuestionId(from.getContentId());
		to.setUser(creator.getLoginId());

		List<String> answers = new ArrayList<>();
		for (int i = 0; i < content.getOptions().size(); i++) {
			answers.add(from.getSelectedChoiceIndexes().contains(i) ? "1" : "0");
		}
		to.setAnswerText(answers.stream().collect(Collectors.joining()));

		return to;
	}

	public Answer migrate(final de.thm.arsnova.entities.TextAnswer from, final de.thm.arsnova.entities.Content content, final UserProfile creator) {
		final Answer to = new Answer();
		copyCommonProperties(from, to);
		to.setQuestionId(from.getContentId());
		to.setUser(creator.getLoginId());

		to.setAnswerSubject(from.getSubject());
		to.setAnswerText(from.getBody());

		return to;
	}

	public Comment migrate(final de.thm.arsnova.entities.Comment from, final UserProfile creator) {
		final Comment to = new Comment();
		copyCommonProperties(from, to);
		to.setSessionId(from.getSessionId());
		to.setCreator(creator.getLoginId());
		to.setSubject(from.getSubject());
		to.setText(from.getBody());
		to.setTimestamp(from.getTimestamp());
		to.setRead(from.isRead());

		return to;
	}
}
