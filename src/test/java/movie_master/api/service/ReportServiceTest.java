package movie_master.api.service;

import movie_master.api.dto.ReportDto;
import movie_master.api.exception.ReportNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.ReportDtoMapper;
import movie_master.api.model.Report;
import movie_master.api.model.User;
import movie_master.api.repository.ReportRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.CreateReportRequest;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static movie_master.utils.TestUtils.createMultipleRandomRecords;
import static movie_master.utils.TestUtils.createRandomRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReportDtoMapper reportDtoMapper;
    @InjectMocks ReportService service;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void canCreateReport() throws UserNotFoundException {
        // Arrange
        CreateReportRequest request = createRandomRecord(CreateReportRequest.class, easyRandom);
        User reportedUser = easyRandom.nextObject(User.class);
        Report createdReport = easyRandom.nextObject(Report.class);
        ReportDto mapped = createRandomRecord(ReportDto.class, easyRandom);

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.of(reportedUser));
        Mockito.when(reportRepository.save(Mockito.any())).thenReturn(createdReport); // any because dates suck to mock
        Mockito.when(reportDtoMapper.mapToDto(createdReport)).thenReturn(mapped);

        // Act
        ReportDto result = service.createReport(request);

        // Assert
        assertEquals(mapped, result);
    }

    @Test
    void canCreateNotReportBecauseInvalidUser() {
        // Arrange
        CreateReportRequest request = createRandomRecord(CreateReportRequest.class, easyRandom);

        Mockito.when(userRepository.findById(request.userId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> service.createReport(request));
    }

    @Test
    void canRetrieveAllReports() {
        // Arrange
        int storedAmount = 10;
        List<Report> stored = easyRandom.objects(Report.class, storedAmount).toList();
        List<ReportDto> expectedResult = createMultipleRandomRecords(ReportDto.class, easyRandom, storedAmount);

        Mockito.when(reportRepository.findAll()).thenReturn(stored);
        for (int i = 0; i < storedAmount; i++) {
            Mockito.when(reportDtoMapper.mapToDto(stored.get(i))).thenReturn(expectedResult.get(i));
        }

        // Act
        List<ReportDto> result = service.getAllReports();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void canFindById() {
        // Arrange
        long reportId = easyRandom.nextLong();
        Report report = easyRandom.nextObject(Report.class);
        ReportDto mapped = createRandomRecord(ReportDto.class, easyRandom);

        Mockito.when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        Mockito.when(reportDtoMapper.mapToDto(report)).thenReturn(mapped);

        // Act
        ReportDto result = service.getReportById(reportId);

        // Assert
        assertEquals(mapped, result);
    }

    @Test
    void cannotFindById() throws ReportNotFoundException {
        // Arrange
        long reportId = easyRandom.nextLong();

        Mockito.when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ReportNotFoundException.class, () -> service.getReportById(reportId));
    }

    @Test
    void canDeleteReportByIdWithoutBan() {
        // Arrange
        long reportId = easyRandom.nextLong();
        boolean banUser = false;

        // Act
        service.deleteReport(reportId, banUser);

        // Assert
        Mockito.verify(reportRepository).deleteById(reportId);
    }

    @Test
    void canDeleteReportByIdWithBan() {
        // Arrange
        long reportId = easyRandom.nextLong();
        boolean banUser = true;
        Report foundReport = easyRandom.nextObject(Report.class);
        User userToBan = foundReport.getReportedUser();
        userToBan.setBanned(true);

        Mockito.when(reportRepository.findById(reportId)).thenReturn(Optional.of(foundReport));

        // Act
        service.deleteReport(reportId, banUser);

        // Assert
        Mockito.verify(userRepository).save(userToBan);
        Mockito.verify(reportRepository).deleteById(reportId);
    }

    @Test
    void canNotDeleteReportByIdWithBan() throws ReportNotFoundException {
        // Arrange
        long reportId = easyRandom.nextLong();
        boolean banUser = true;

        Mockito.when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act and Assert
        Mockito.verifyNoInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(reportRepository);
        assertThrows(ReportNotFoundException.class, () -> service.deleteReport(reportId, banUser));
    }
}
