/*
 * This file is part of ARSnova Backend.
 * Copyright (C) 2012-2017 The ARSnova Team
 *
 * ARSnova Backend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ARSnova Backend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.thm.arsnova.controller;

import de.thm.arsnova.entities.Room;
import de.thm.arsnova.entities.transport.ImportExportSession;
import de.thm.arsnova.entities.transport.ScoreStatistics;
import de.thm.arsnova.exceptions.UnauthorizedException;
import de.thm.arsnova.services.RoomService;
import de.thm.arsnova.services.RoomServiceImpl.SessionNameComparator;
import de.thm.arsnova.services.RoomServiceImpl.SessionShortNameComparator;
import de.thm.arsnova.services.UserService;
import de.thm.arsnova.web.DeprecatedApi;
import de.thm.arsnova.web.Pagination;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles requests related to ARSnova rooms.
 */
@RestController
@RequestMapping("/room")
@Api(value = "/room", description = "the Room Controller API")
public class RoomController extends PaginationController {
	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@ApiOperation(value = "join a session",
			nickname = "joinRoom")
	@DeprecatedApi
	@Deprecated
	@RequestMapping(value = "/{shortId}", method = RequestMethod.GET)
	public Room get(
			@ApiParam(value = "Room-Key from current session", required = true) @PathVariable final String shortId,
			@ApiParam(value = "Adminflag", required = false) @RequestParam(value = "admin", defaultValue = "false")	final boolean admin
			) {
		if (admin) {
			return roomService.getForAdmin(shortId);
		} else {
			return roomService.getByKey(shortId);
		}
	}

	@ApiOperation(value = "deletes a session",
			nickname = "delete")
	@RequestMapping(value = "/{shortId}", method = RequestMethod.DELETE)
	public void delete(@ApiParam(value = "Room-Key from current session", required = true) @PathVariable final String shortId) {
		Room room = roomService.getByKey(shortId);
		roomService.deleteCascading(room);
	}

	@ApiOperation(value = "count active users",
			nickname = "countActiveUsers")
	@DeprecatedApi
	@Deprecated
	@RequestMapping(value = "/{shortId}/activeusercount", method = RequestMethod.GET)
	public int countActiveUsers(@ApiParam(value = "Room-Key from current session", required = true) @PathVariable final String shortId) {
		return roomService.activeUsers(shortId);
	}

	@ApiOperation(value = "Creates a new Room and returns the Room's data",
			nickname = "create")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = HTML_STATUS_201),
		@ApiResponse(code = 503, message = HTML_STATUS_503)
	})
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Room create(@ApiParam(value = "current session", required = true) @RequestBody final Room room, final HttpServletResponse response) {
		/* FIXME: migrate LMS course support
		if (room != null && room.isCourseSession()) {
			final List<Course> courses = new ArrayList<>();
			final Course course = new Course();
			course.setId(room.getCourseId());
			courses.add(course);
			final int sessionCount = roomService.countSessionsByCourses(courses);
			if (sessionCount > 0) {
				final String appendix = " (" + (sessionCount + 1) + ")";
				room.setName(room.getName() + appendix);
				room.setAbbreviation(room.getAbbreviation() + appendix);
			}
		}
		*/

		roomService.save(room);

		return room;
	}

	@ApiOperation(value = "updates a session",
			nickname = "create")
	@RequestMapping(value = "/{shortId}", method = RequestMethod.PUT)
	public Room update(
			@ApiParam(value = "session-key from current session", required = true) @PathVariable final String shortId,
			@ApiParam(value = "current session", required = true) @RequestBody final Room room
			) {
		return roomService.update(shortId, room);
	}

	@ApiOperation(value = "Retrieves a list of Sessions",
			nickname = "getAll")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = HTML_STATUS_204),
		@ApiResponse(code = 501, message = HTML_STATUS_501)
	})
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@Pagination
	public List<Room> getAll(
			@ApiParam(value = "ownedOnly", required = true) @RequestParam(value = "ownedonly", defaultValue = "false") final boolean ownedOnly,
			@ApiParam(value = "visitedOnly", required = true) @RequestParam(value = "visitedonly", defaultValue = "false") final boolean visitedOnly,
			@ApiParam(value = "sortby", required = true) @RequestParam(value = "sortby", defaultValue = "name") final String sortby,
			@ApiParam(value = "for a given username. admin rights needed", required = false) @RequestParam(value =
					"username", defaultValue = "") final String username,
			final HttpServletResponse response
			) {
		List<Room> rooms;

		if (!"".equals(username)) {
			try {
				if (ownedOnly && !visitedOnly) {
					rooms = roomService.getUserSessions(username);
				} else if (visitedOnly && !ownedOnly) {
					rooms = roomService.getUserVisitedSessions(username);
				} else {
					response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
					return null;
				}
			} catch (final AccessDeniedException e) {
				throw new UnauthorizedException();
			}
		} else {
			/* TODO implement all parameter combinations, implement use of user parameter */
			try {
				if (ownedOnly && !visitedOnly) {
					rooms = roomService.getMySessions(offset, limit);
				} else if (visitedOnly && !ownedOnly) {
					rooms = roomService.getMyVisitedSessions(offset, limit);
				} else {
					response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
					return null;
				}
			} catch (final AccessDeniedException e) {
				throw new UnauthorizedException();
			}
		}

		if (rooms == null || rooms.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return null;
		}

		if ("shortname".equals(sortby)) {
			Collections.sort(rooms, new SessionShortNameComparator());
		} else {
			Collections.sort(rooms, new SessionNameComparator());
		}

		return rooms;
	}

	/**
	 * Returns a list of my own sessions with only the necessary information like name, keyword, or counters.
	 */
	@ApiOperation(value = "Retrieves a Room",
			nickname = "getMySessions")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = HTML_STATUS_204)
	})
	@RequestMapping(value = "/", method = RequestMethod.GET, params = "statusonly=true")
	@Pagination
	public List<Room> getMySessions(
			@ApiParam(value = "visitedOnly", required = true) @RequestParam(value = "visitedonly", defaultValue = "false") final boolean visitedOnly,
			@ApiParam(value = "sort by", required = false) @RequestParam(value = "sortby", defaultValue = "name") final String sortby,
			final HttpServletResponse response
			) {
		List<Room> rooms;
		if (!visitedOnly) {
			rooms = roomService.getMySessionsInfo(offset, limit);
		} else {
			rooms = roomService.getMyVisitedSessionsInfo(offset, limit);
		}

		if (rooms == null || rooms.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return null;
		}

		if ("shortname".equals(sortby)) {
			Collections.sort(rooms, new SessionShortNameComparator());
		} else {
			Collections.sort(rooms, new SessionNameComparator());
		}
		return rooms;
	}

	@ApiOperation(value = "Retrieves all public pool sessions for the current user",
			nickname = "getMyPublicPoolSessions")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = HTML_STATUS_204)
	})
	@RequestMapping(value = "/publicpool", method = RequestMethod.GET, params = "statusonly=true")
	public List<Room> getMyPublicPoolSessions(
			final HttpServletResponse response
			) {
		List<Room> sessions = roomService.getMyPublicPoolSessionsInfo();

		if (sessions == null || sessions.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return null;
		}

		return sessions;
	}

	@ApiOperation(value = "Retrieves all public pool sessions",
			nickname = "getMyPublicPoolSessions")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = HTML_STATUS_204)
	})
	@RequestMapping(value = "/publicpool", method = RequestMethod.GET)
	public List<Room> getPublicPoolSessions(
			final HttpServletResponse response
			) {
		List<Room> rooms = roomService.getPublicPoolSessionsInfo();

		if (rooms == null || rooms.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return null;
		}

		return rooms;
	}

	@ApiOperation(value = "imports a session",
			nickname = "importSession")
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public Room importSession(
			@ApiParam(value = "current session", required = true) @RequestBody final ImportExportSession session,
			final HttpServletResponse response
			) {
		return roomService.importSession(session);
	}

	@ApiOperation(value = "export sessions", nickname = "exportSession")
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public List<ImportExportSession> getExport(
			@ApiParam(value = "shortId", required = true) @RequestParam(value = "shortId", defaultValue = "") final List<String> shortId,
			@ApiParam(value = "wether statistics shall be exported", required = true) @RequestParam(value = "withAnswerStatistics", defaultValue = "false") final Boolean withAnswerStatistics,
			@ApiParam(value = "wether comments shall be exported", required = true) @RequestParam(value = "withFeedbackQuestions", defaultValue = "false") final Boolean withFeedbackQuestions,
			final HttpServletResponse response
		) {
		List<ImportExportSession> sessions = new ArrayList<>();
		ImportExportSession temp;
		for (String key : shortId) {
			roomService.setActive(key, false);
			temp = roomService.exportSession(key, withAnswerStatistics, withFeedbackQuestions);
			if (temp != null) {
				sessions.add(temp);
			}
			roomService.setActive(key, true);
		}
		return sessions;
	}

	@ApiOperation(value = "copy a session to the public pool if enabled")
	@RequestMapping(value = "/{shortId}/copytopublicpool", method = RequestMethod.POST)
	public Room copyToPublicPool(
			@ApiParam(value = "session-key from current session", required = true) @PathVariable final String shortId,
			@ApiParam(value = "public pool attributes for session", required = true) @RequestBody final de.thm.arsnova.entities.transport.ImportExportSession.PublicPool publicPool
			) {
		roomService.setActive(shortId, false);
		Room roomInfo = roomService.copySessionToPublicPool(shortId, publicPool);
		roomService.setActive(shortId, true);
		return roomInfo;
	}

	@ApiOperation(value = "retrieves a value for the score",
			nickname = "getLearningProgress")
	@RequestMapping(value = "/{shortId}/learningprogress", method = RequestMethod.GET)
	public ScoreStatistics getLearningProgress(
			@ApiParam(value = "session-key from current session", required = true) @PathVariable final String shortId,
			@ApiParam(value = "type", required = false) @RequestParam(value = "type", defaultValue = "questions") final String type,
			@ApiParam(value = "question variant", required = false) @RequestParam(value = "questionVariant", required = false) final String questionVariant,
			final HttpServletResponse response
			) {
		return roomService.getLearningProgress(shortId, type, questionVariant);
	}

	@ApiOperation(value = "retrieves a value for the learning progress for the current user",
			nickname = "getMyLearningProgress")
	@RequestMapping(value = "/{shortId}/mylearningprogress", method = RequestMethod.GET)
	public ScoreStatistics getMyLearningProgress(
			@ApiParam(value = "session-key from current session", required = true) @PathVariable final String shortId,
			@RequestParam(value = "type", defaultValue = "questions") final String type,
			@RequestParam(value = "questionVariant", required = false) final String questionVariant,
			final HttpServletResponse response
			) {
		return roomService.getMyLearningProgress(shortId, type, questionVariant);
	}
}
