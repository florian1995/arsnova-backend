package de.thm.arsnova.entities;

import com.fasterxml.jackson.annotation.JsonView;
import de.thm.arsnova.entities.serialization.View;

import java.util.Date;
import java.util.Map;

public class Content implements Entity {
	public class State {
		private int round = 1;
		private Date roundEndTimestamp;
		private boolean visible = true;
		private boolean solutionVisible = false;
		private boolean responsesEnabled = true;
		private boolean responsesVisible = false;

		@JsonView({View.Persistence.class, View.Public.class})
		public int getRound() {
			return round;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setRound(final int round) {
			this.round = round;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public Date getRoundEndTimestamp() {
			return roundEndTimestamp;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setRoundEndTimestamp(Date roundEndTimestamp) {
			this.roundEndTimestamp = roundEndTimestamp;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public boolean isVisible() {
			return visible;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public boolean isSolutionVisible() {
			return solutionVisible;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setSolutionVisible(final boolean solutionVisible) {
			this.solutionVisible = solutionVisible;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setVisible(final boolean visible) {
			this.visible = visible;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public boolean areResponsesEnabled() {
			return responsesEnabled;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setResponsesEnabled(final boolean responsesEnabled) {
			this.responsesEnabled = responsesEnabled;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public boolean areResponsesVisible() {
			return responsesVisible;
		}

		@JsonView({View.Persistence.class, View.Public.class})
		public void setResponsesVisible(final boolean responsesVisible) {
			this.responsesVisible = responsesVisible;
		}
	}

	private String id;
	private String rev;
	private Date creationTimestamp;
	private Date updateTimestamp;
	private String roomId;
	private String subject;
	private String body;
	private String format;
	private String group;
	private State state;
	private Date timestamp;
	private Map<String, Map<String, ?>> extensions;
	private Map<String, String> attachments;

	@Override
	@JsonView({View.Persistence.class, View.Public.class})
	public String getId() {
		return id;
	}

	@Override
	@JsonView({View.Persistence.class, View.Public.class})
	public void setId(final String id) {
		this.id = id;
	}

	@Override
	@JsonView({View.Persistence.class, View.Public.class})
	public String getRevision() {
		return rev;
	}

	@Override
	@JsonView({View.Persistence.class, View.Public.class})
	public void setRevision(final String rev) {
		this.rev = rev;
	}

	@Override
	@JsonView(View.Persistence.class)
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	@Override
	@JsonView(View.Persistence.class)
	public void setCreationTimestamp(final Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Override
	@JsonView(View.Persistence.class)
	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	@Override
	@JsonView(View.Persistence.class)
	public void setUpdateTimestamp(final Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public String getRoomId() {
		return roomId;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setRoomId(final String roomId) {
		this.roomId = roomId;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public String getSubject() {
		return subject;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public String getBody() {
		return body;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setBody(final String body) {
		this.body = body;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public String getFormat() {
		return format;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setFormat(final String format) {
		this.format = format;
	}

	@JsonView(View.Public.class)
	public String getGroup() {
		return group;
	}

	@JsonView(View.Public.class)
	public void setGroup(final String group) {
		this.group = group;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public State getState() {
		return state;
	}

	public void resetState() {
		this.state = new State();
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setState(final State state) {
		this.state = state;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonView(View.Persistence.class)
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public Map<String, Map<String, ?>> getExtensions() {
		return extensions;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setExtensions(Map<String, Map<String, ?>> extensions) {
		this.extensions = extensions;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public Map<String, String> getAttachments() {
		return attachments;
	}

	@JsonView({View.Persistence.class, View.Public.class})
	public void setAttachments(final Map<String, String> attachments) {
		this.attachments = attachments;
	}
}
