package com.OnlineConsultancyApp;

import com.OnlineConsultancyApp.exceptions.NoSuchUserException;
import com.OnlineConsultancyApp.exceptions.UserAlreadyExistsException;
import com.OnlineConsultancyApp.models.Users.Consultant;
import com.OnlineConsultancyApp.repositories.ConsultantRepository;
import com.OnlineConsultancyApp.services.ConsultantService;
import com.OnlineConsultancyApp.services.RedisCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootTest
public class ConsultantServiceTests {

    @Mock
    ConsultantRepository consultantRepository;
    @Mock
    RedisCacheService redisCacheService;
    @InjectMocks
    ConsultantService consultantService;

    @Test
    void testRegisterConsultantAlreadyExists() throws SQLException, JsonProcessingException {
        // Arrange
        Consultant consultant = new Consultant();
        consultant.setEmail("consultant@gmail.com");

        Mockito.doReturn(new Consultant()).when(consultantRepository).getConsultant(consultant.getEmail());

        // Act & Assert
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> consultantService.registerConsultant(consultant));

        Mockito.verify(consultantRepository, Mockito.times(1)).getConsultant(consultant.getEmail());
        Mockito.verifyNoInteractions(redisCacheService);
    }

    @Test
    void testRegisterConsultantSuccess() throws SQLException, IOException, ClassNotFoundException, IOException {
        // Arrange
        Consultant consultant = new Consultant();
        consultant.setEmail("consultant@gmail.com");
        consultant.setPassword("plainPassword");

        Mockito.doThrow(new NoSuchUserException()).when(consultantRepository).getConsultant(consultant.getEmail());
        Mockito.doReturn(1L).when(consultantRepository).registerConsultant(Mockito.any(Consultant.class));

        // Act
        consultantService.registerConsultant(consultant);

        // Assert
        Mockito.verify(consultantRepository, Mockito.times(1)).getConsultant(consultant.getEmail());
        Mockito.verify(consultantRepository, Mockito.times(1)).registerConsultant(Mockito.any(Consultant.class));
        Mockito.verify(redisCacheService, Mockito.times(1)).put(Mockito.any(Consultant.class));
    }
}
