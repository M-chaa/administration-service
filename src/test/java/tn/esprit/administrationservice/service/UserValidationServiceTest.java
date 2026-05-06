package tn.esprit.administrationservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import tn.esprit.administrationservice.exception.BadRequestException;

class UserValidationServiceTest {

  @Test
  void validateTutorShouldThrowExceptionWhenTutorIdIsNull() {
    RestTemplate restTemplate = new RestTemplate();
    UserValidationService service = new UserValidationService(restTemplate);

    BadRequestException exception = Assertions.assertThrows(
      BadRequestException.class,
      () -> service.validateTutor(null)
    );

    Assertions.assertEquals("tutorId est obligatoire", exception.getMessage());
  }

  @Test
  void validateTutorShouldThrowExceptionWhenUserServiceUnavailable() {
    RestTemplate restTemplate = new RestTemplate();
    UserValidationService service = new UserValidationService(restTemplate);

    BadRequestException exception = Assertions.assertThrows(
      BadRequestException.class,
      () -> service.validateTutor(999L)
    );

    Assertions.assertTrue(exception.getMessage().contains("Impossible de valider le tutorId"));
  }
}
