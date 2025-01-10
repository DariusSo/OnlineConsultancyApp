package com.OnlineConsultancyApp;

import com.OnlineConsultancyApp.models.Users.Client;
import com.OnlineConsultancyApp.repositories.ClientRepository;
import com.OnlineConsultancyApp.services.AppointmentService;
import com.OnlineConsultancyApp.services.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class OnlineConsultancyAppApplicationTests {

	@Mock
	private ClientRepository clientRepository; // Mocked repository

	@InjectMocks
	private ClientService clientService; // Service to test

	@Test
	void testGetClientById() throws SQLException, JsonProcessingException {
		// Arrange
		MockitoAnnotations.openMocks(this); // Initialize mocks
		long clientId = 1L;
		Client expectedClient = new Client(); // Example Client object

		when(clientRepository.getClientById(clientId)).thenReturn(expectedClient);

		// Act
		Client actualClient = clientService.getClientById(clientId);

		// Assert
		assertEquals(expectedClient, actualClient); // Validate the result
		verify(clientRepository, times(1)).getClientById(clientId); // Verify the repository method was called once
	}

	@Test
	void getConsultantsByDate(){

	}

}
