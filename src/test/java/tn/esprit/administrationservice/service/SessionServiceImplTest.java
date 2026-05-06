package tn.esprit.administrationservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.administrationservice.entity.Session;
import tn.esprit.administrationservice.entity.SessionMode;
import tn.esprit.administrationservice.entity.SessionStatus;
import tn.esprit.administrationservice.exception.BadRequestException;
import tn.esprit.administrationservice.repository.SessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SessionServiceImplTest {

  @Autowired
  private SessionService sessionService;

  @Autowired
  private SessionRepository sessionRepository;

  private Session createSampleSession(SessionStatus status) {
    Session session = new Session();
    session.setTutorId(1L);
    session.setCourseId(1L);
    session.setSessionDate(LocalDate.now().plusDays(1));
    session.setStartTime(LocalTime.of(10, 0));
    session.setEndTime(LocalTime.of(12, 0));
    session.setMode(SessionMode.ONLINE);
    session.setStatus(status);
    session.setCreatedAt(LocalDateTime.now());
    session.setUpdatedAt(LocalDateTime.now());
    return sessionRepository.save(session);
  }

  @Test
  void getByIdShouldReturnSessionWhenExists() {
    Session saved = createSampleSession(SessionStatus.PLANNED);

    Session result = sessionService.getById(saved.getId());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(saved.getId(), result.getId());
    Assertions.assertEquals(SessionStatus.PLANNED, result.getStatus());
  }

  @Test
  void getByIdShouldThrowExceptionWhenSessionNotFound() {
    Assertions.assertThrows(RuntimeException.class, () -> sessionService.getById(999L));
  }

  @Test
  void getAllShouldReturnSessions() {
    createSampleSession(SessionStatus.PLANNED);

    List<Session> sessions = sessionService.getAll();

    Assertions.assertNotNull(sessions);
    Assertions.assertFalse(sessions.isEmpty());
  }

  @Test
  void getByTutorShouldReturnTutorSessions() {
    Session saved = createSampleSession(SessionStatus.PLANNED);

    List<Session> sessions = sessionService.getByTutor(saved.getTutorId());

    Assertions.assertNotNull(sessions);
    Assertions.assertFalse(sessions.isEmpty());
    Assertions.assertEquals(1L, sessions.get(0).getTutorId());
  }

  @Test
  void cancelShouldChangeStatusToCanceled() {
    Session saved = createSampleSession(SessionStatus.PLANNED);

    Session canceled = sessionService.cancel(saved.getId());

    Assertions.assertEquals(SessionStatus.CANCELED, canceled.getStatus());
    Assertions.assertNotNull(canceled.getUpdatedAt());
  }

  @Test
  void cancelShouldReturnSameSessionWhenAlreadyCanceled() {
    Session saved = createSampleSession(SessionStatus.CANCELED);

    Session result = sessionService.cancel(saved.getId());

    Assertions.assertEquals(SessionStatus.CANCELED, result.getStatus());
  }

  @Test
  void cancelShouldThrowExceptionWhenSessionCompleted() {
    Session saved = createSampleSession(SessionStatus.COMPLETED);

    Assertions.assertThrows(BadRequestException.class, () -> sessionService.cancel(saved.getId()));
  }

  @Test
  void completeShouldChangeStatusToCompleted() {
    Session saved = createSampleSession(SessionStatus.PLANNED);

    Session completed = sessionService.complete(saved.getId());

    Assertions.assertEquals(SessionStatus.COMPLETED, completed.getStatus());
    Assertions.assertNotNull(completed.getUpdatedAt());
  }

  @Test
  void completeShouldReturnSameSessionWhenAlreadyCompleted() {
    Session saved = createSampleSession(SessionStatus.COMPLETED);

    Session result = sessionService.complete(saved.getId());

    Assertions.assertEquals(SessionStatus.COMPLETED, result.getStatus());
  }

  @Test
  void completeShouldThrowExceptionWhenSessionCanceled() {
    Session saved = createSampleSession(SessionStatus.CANCELED);

    Assertions.assertThrows(BadRequestException.class, () -> sessionService.complete(saved.getId()));
  }

  @Test
  void deleteShouldRemoveSession() {
    Session saved = createSampleSession(SessionStatus.PLANNED);

    sessionService.delete(saved.getId());

    Assertions.assertFalse(sessionRepository.findById(saved.getId()).isPresent());
  }
}
