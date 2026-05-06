package tn.esprit.administrationservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.administrationservice.dto.SessionStatsDTO;
import tn.esprit.administrationservice.dto.TutorSessionStatsDTO;
import tn.esprit.administrationservice.dto.MonthlySessionStatsDTO;
import tn.esprit.administrationservice.entity.Session;
import tn.esprit.administrationservice.entity.SessionMode;
import tn.esprit.administrationservice.entity.SessionStatus;
import tn.esprit.administrationservice.repository.SessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SessionStatisticsServiceImplTest {

  @Autowired
  private SessionStatisticsService sessionStatisticsService;

  @Autowired
  private SessionRepository sessionRepository;

  private void saveSession(Long tutorId, SessionStatus status) {
    Session session = new Session();
    session.setTutorId(tutorId);
    session.setCourseId(1L);
    session.setSessionDate(LocalDate.now().plusDays(1));
    session.setStartTime(LocalTime.of(9, 0));
    session.setEndTime(LocalTime.of(10, 0));
    session.setMode(SessionMode.ONLINE);
    session.setStatus(status);
    session.setCreatedAt(LocalDateTime.now());
    session.setUpdatedAt(LocalDateTime.now());

    sessionRepository.save(session);
  }

  @Test
  void getGlobalStatisticsShouldReturnCorrectCounts() {
    saveSession(1L, SessionStatus.PLANNED);
    saveSession(1L, SessionStatus.COMPLETED);
    saveSession(2L, SessionStatus.CANCELED);

    SessionStatsDTO stats = sessionStatisticsService.getGlobalStatistics();

    Assertions.assertNotNull(stats);
    Assertions.assertEquals(3, stats.getTotalSessions());
    Assertions.assertEquals(1, stats.getPlannedSessions());
    Assertions.assertEquals(1, stats.getCompletedSessions());
    Assertions.assertEquals(1, stats.getCanceledSessions());
    Assertions.assertTrue(stats.getCompletionRate() > 0);
  }

  @Test
  void getSessionsCountByTutorShouldReturnList() {
    saveSession(1L, SessionStatus.PLANNED);
    saveSession(1L, SessionStatus.COMPLETED);
    saveSession(2L, SessionStatus.CANCELED);

    List<TutorSessionStatsDTO> result = sessionStatisticsService.getSessionsCountByTutor();

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void getSessionsCountByMonthShouldReturnList() {
    saveSession(1L, SessionStatus.PLANNED);

    List<MonthlySessionStatsDTO> result = sessionStatisticsService.getSessionsCountByMonth();

    Assertions.assertNotNull(result);
  }
}
