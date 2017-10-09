package de.thm.arsnova.controller.v2;

import de.thm.arsnova.controller.PaginationController;
import de.thm.arsnova.entities.migration.FromV2Migrator;
import de.thm.arsnova.entities.migration.ToV2Migrator;
import de.thm.arsnova.entities.migration.v2.Room;
import de.thm.arsnova.services.RoomService;
import de.thm.arsnova.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController("v2RoomController")
@RequestMapping("/v2/session")
public class RoomController extends PaginationController {
	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private ToV2Migrator toV2Migrator;

	@Autowired
	private FromV2Migrator fromV2Migrator;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Room create(@RequestBody final Room room, final HttpServletResponse response) {
		return toV2Migrator.migrate(roomService.save(fromV2Migrator.migrate(room, null)), null);
	}

	@RequestMapping(value = "/{shortId}", method = RequestMethod.PUT)
	public Room update(
			@PathVariable final String shortId,
			@RequestBody final Room room
	) {
		return toV2Migrator.migrate(roomService.update(shortId, fromV2Migrator.migrate(room, null)), null);
	}

	@RequestMapping(value = "/{shortId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable final String shortId) {
		return String.format("forward:/v2/room/%s", shortId);
	}

	@RequestMapping(value = "/{shortId}", method = RequestMethod.GET)
	public Room join(
			@PathVariable final String shortId,
			@RequestParam(value = "admin", defaultValue = "false") final boolean admin
	) {
		if (admin) {
			return toV2Migrator.migrate(roomService.getForAdmin(shortId), null);
		} else {
			return toV2Migrator.migrate(roomService.getByKey(shortId), null);
		}
	}

	@RequestMapping(value = "/{shortId}/activeusercount", method = RequestMethod.GET)
	public int countActiveUsers(@PathVariable final String shortId) {
		return roomService.activeUsers(shortId);
	}

	@RequestMapping(value = "/{shortId}/features", method = RequestMethod.GET)
	public de.thm.arsnova.entities.Room.Settings getSessionFeatures(
			@PathVariable final String shortId,
			final HttpServletResponse response
	) {
		return roomService.getFeatures(shortId);
	}

	@RequestMapping(value = "/{shortId}/features", method = RequestMethod.PUT)
	public de.thm.arsnova.entities.Room.Settings changeSessionFeatures(
			@PathVariable final String shortId,
			@RequestBody final de.thm.arsnova.entities.Room.Settings features,
			final HttpServletResponse response
	) {
		return roomService.updateFeatures(shortId, features);
	}

	@RequestMapping(value = "/{sessionkey}/changecreator", method = RequestMethod.PUT)
	public Room changeSessionCreator(
			@PathVariable final String sessionkey,
			@RequestBody final String newCreator
	) {
		return toV2Migrator.migrate(roomService.updateCreator(sessionkey, newCreator), null);
	}

	@RequestMapping(value = "/{shortId}/lock", method = RequestMethod.POST)
	public Room lockSession(
			@PathVariable final String shortId,
			@RequestParam(required = false) final Boolean lock,
			final HttpServletResponse response
	) {
		if (lock != null) {
			return toV2Migrator.migrate(roomService.setActive(shortId, lock), null);
		}
		response.setStatus(HttpStatus.NOT_FOUND.value());

		return null;
	}

	@RequestMapping(value = "/{shortId}/lockfeedbackinput", method = RequestMethod.POST)
	public boolean lockFeedbackInput(
			@PathVariable final String shortId,
			@RequestParam(required = true) final Boolean lock,
			final HttpServletResponse response
	) {
		return roomService.lockFeedbackInput(shortId, lock);
	}

	@RequestMapping(value = "/{shortId}/flipflashcards", method = RequestMethod.POST)
	public boolean flipFlashcards(
			@PathVariable final String shortId,
			@RequestParam(required = true) final Boolean flip,
			final HttpServletResponse response
	) {
		return roomService.flipFlashcards(shortId, flip);
	}

	@RequestMapping(value = "/{sessionKey}/lecturerquestion")
	public String redirectLecturerQuestion(
			@PathVariable final String sessionKey,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/lecturerquestion/?shortId=%s", sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/lecturerquestion/{arg1}")
	public String redirectLecturerQuestionWithOneArgument(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/lecturerquestion/%s/?shortId=%s", arg1, sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/lecturerquestion/{arg1}/{arg2}")
	public String redirectLecturerQuestionWithTwoArguments(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			@PathVariable final String arg2,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/lecturerquestion/%s/%s/?shortId=%s", arg1, arg2, sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/lecturerquestion/{arg1}/{arg2}/{arg3}")
	public String redirectLecturerQuestionWithThreeArguments(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			@PathVariable final String arg2,
			@PathVariable final String arg3,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/lecturerquestion/%s/%s/%s/?shortId=%s", arg1, arg2, arg3, sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/audiencequestion")
	public String redirectAudienceQuestion(
			@PathVariable final String sessionKey,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/audiencequestion/?shortId=%s", sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/audiencequestion/{arg1}")
	public String redirectAudienceQuestionWithOneArgument(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/audiencequestion/%s/?shortId=%s", arg1, sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/audiencequestion/{arg1}/{arg2}")
	public String redirectAudienceQuestionWithTwoArguments(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			@PathVariable final String arg2,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/audiencequestion/%s/%s/?shortId=%s", arg1, arg2, sessionKey);
	}

	@RequestMapping(value = "/{sessionKey}/audiencequestion/{arg1}/{arg2}/{arg3}")
	public String redirectAudienceQuestionWithThreeArguments(
			@PathVariable final String sessionKey,
			@PathVariable final String arg1,
			@PathVariable final String arg2,
			@PathVariable final String arg3,
			final HttpServletResponse response
	) {
		response.addHeader(X_FORWARDED, "1");

		return String.format("forward:/v2/audiencequestion/%s/%s/%s/?shortId=%s", arg1, arg2, arg3, sessionKey);
	}
}
