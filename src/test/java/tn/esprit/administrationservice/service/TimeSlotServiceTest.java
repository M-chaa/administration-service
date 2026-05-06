package tn.esprit.administrationservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.administrationservice.dto.TimeSlotRequest;
import tn.esprit.administrationservice.entity.OwnerType;
import tn.esprit.administrationservice.entity.TimeSlot;
import tn.esprit.administrationservice.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class TimeSlotServiceTest {

  @Autowired
  private TimeSlotService timeSlotService;

  @Test
  void createTimeSlotSuccessfully() {

    TimeSlotRequest request = new TimeSlotRequest();

    request.setOwnerType(OwnerType.PLATFORM);
    request.setDayOfWeek(1);
    request.setStartTime(LocalTime.of(9, 0));
    request.setEndTime(LocalTime.of(11, 0));
    request.setValidFrom(LocalDate.now());
    request.setValidTo(LocalDate.now().plusDays(5));
    request.setActive(true);

    TimeSlot saved = timeSlotService.create(request);

    Assertions.assertNotNull(saved.getId());
    Assertions.assertEquals(OwnerType.PLATFORM, saved.getOwnerType());
  }

  @Test
  void shouldThrowExceptionWhenStartTimeAfterEndTime() {

    TimeSlotRequest request = new TimeSlotRequest();

    request.setOwnerType(OwnerType.PLATFORM);
    request.setDayOfWeek(1);
    request.setStartTime(LocalTime.of(14, 0));
    request.setEndTime(LocalTime.of(10, 0));

    Assertions.assertThrows(
      BadRequestException.class,
      () -> timeSlotService.create(request)
    );
  }

  @Test
  void getAllShouldReturnList() {

    List<TimeSlot> list = timeSlotService.getAll();

    Assertions.assertNotNull(list);
  }
}
