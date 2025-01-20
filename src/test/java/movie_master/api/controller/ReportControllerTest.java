package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.controller.model.GenericResponse;
import movie_master.api.dto.ReportDto;
import movie_master.api.exception.ReportNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.CreateReportRequest;
import movie_master.api.service.ReportService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static movie_master.utils.TestUtils.createMultipleRandomRecords;
import static movie_master.utils.TestUtils.createRandomRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock private ReportService service;
    @InjectMocks private ReportController controller;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void canCreateReport() throws UserNotFoundException {
        // Arrange
        String uri = easyRandom.nextObject(String.class);
        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
        CreateReportRequest request = createRandomRecord(CreateReportRequest.class, easyRandom);
        ReportDto expectedResult = createRandomRecord(ReportDto.class, easyRandom);

        Mockito.when(service.createReport(request)).thenReturn(expectedResult);
        Mockito.when(servletRequest.getRequestURI()).thenReturn(uri);

        // Act
        ResponseEntity<Object> result = controller.reportUser(servletRequest, request);

        // Assert
        assertEquals(HttpStatusCode.valueOf(201), result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void canNotCreateReportWhenUserNotFound() throws UserNotFoundException {
        // Arrange
        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
        CreateReportRequest request = createRandomRecord(CreateReportRequest.class, easyRandom);

        Mockito.when(service.createReport(request)).thenThrow(new UserNotFoundException());

        // Act
        ResponseEntity<Object> result = controller.reportUser(servletRequest, request);

        // Assert
        assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
    }

    @Test
    void canRetrieveAllReports() {
        // Arrange
        int reportAmount = 10;
        List<ReportDto> reports = createMultipleRandomRecords(ReportDto.class, easyRandom, reportAmount);

        Mockito.when(service.getAllReports()).thenReturn(reports);

        // Act
        ResponseEntity<Object> result = controller.retrieveAllReports();

        // Assert
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(reports, result.getBody());
    }

    @Test
    void canRetrieveSingleReport() {
        // Arrange
        long reportId = easyRandom.nextLong();
        ReportDto report = createRandomRecord(ReportDto.class, easyRandom);

        Mockito.when(service.getReportById(reportId)).thenReturn(report);

        // Act
        ResponseEntity<Object> result = controller.retrieveReport(reportId);

        // Assert
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(report, result.getBody());
    }

    @Test
    void canNotRetrieveReportIfReportNotFound() {
        // Arrange
        long reportId = easyRandom.nextLong();

        Mockito.when(service.getReportById(reportId)).thenThrow(new ReportNotFoundException(reportId));

        // Act
        ResponseEntity<Object> result = controller.retrieveReport(reportId);

        // Assert
        assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
    }

    @Test
    void canDeleteReport() {
        // Arrange
        long reportId = easyRandom.nextLong();
        boolean banUser = easyRandom.nextBoolean();

        // Act
        ResponseEntity<Object> result = controller.deleteReport(reportId, banUser);

        // Assert
        Mockito.verify(service).deleteReport(reportId, banUser);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertInstanceOf(GenericResponse.class, result.getBody());
    }

    @Test
    void canNotDeleteUnfoundReport() {
        // Arrange
        long reportId = easyRandom.nextLong();
        boolean banUser = easyRandom.nextBoolean();

        Mockito.doThrow(new ReportNotFoundException(reportId)).when(service).deleteReport(reportId, banUser);

        // Act
        ResponseEntity<Object> result = controller.deleteReport(reportId, banUser);

        // Assert
        Mockito.verify(service).deleteReport(reportId, banUser);
        assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
    }
}
