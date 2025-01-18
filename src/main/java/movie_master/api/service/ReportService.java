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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing reports in the system.
 * Handles business logic related to creating, retrieving, and deleting reports.
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportDtoMapper reportDtoMapper;

    /**
     * Constructs a new ReportService with the specified repositories and DTO mapper.
     *
     * @param reportRepository the repository for report data access
     * @param userRepository the repository for user data access
     * @param reportDtoMapper the mapper for converting reports to ReportDto objects
     */
    public ReportService(
        ReportRepository reportRepository, UserRepository userRepository, ReportDtoMapper reportDtoMapper
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportDtoMapper = reportDtoMapper;
    }

    /**
     * Creates a new report based on the provided request.
     *
     * @param request the details of the report to create
     * @return a ReportDto representing the created report
     * @throws UserNotFoundException if the user to be reported cannot be found
     */
    public ReportDto createReport(CreateReportRequest request) throws UserNotFoundException {
        Optional<User> foundUser = userRepository.findById(request.userId());
        if (foundUser.isEmpty()) { throw new UserNotFoundException(request.userId()); }
        User reportedUser = foundUser.get();

        Report reportToCreate = new Report(reportedUser, request.reason());
        Report createdReport = reportRepository.save(reportToCreate);

        return reportDtoMapper.mapToDto(createdReport);
    }

    /**
     * Retrieves all reports from the repository.
     *
     * @return a list of ReportDto objects representing all reports
     */
    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream().map(reportDtoMapper::mapToDto).toList();
    }

    /**
     * Retrieves a specific report by its ID.
     *
     * @param reportId the ID of the report to retrieve
     * @return a ReportDto representing the requested report
     * @throws ReportNotFoundException if the report with the specified ID cannot be found
     */
    public ReportDto getReportById(long reportId) throws ReportNotFoundException {
        Optional<Report> foundReport = reportRepository.findById(reportId);
        if (foundReport.isEmpty()) { throw new ReportNotFoundException(reportId); }
        Report report = foundReport.get();

        return reportDtoMapper.mapToDto(report);
    }

    /**
     * Deletes a report by its ID and optionally bans the user associated with the report.
     *
     * @param reportId the ID of the report to delete
     * @param banUser a boolean indicating whether to ban the user associated with the report
     * @throws ReportNotFoundException if the report with the specified ID cannot be found
     */
    public void deleteReport(long reportId, boolean banUser) throws ReportNotFoundException {
        if (banUser) {
            Optional<Report> foundReport = reportRepository.findById(reportId);
            if (foundReport.isEmpty()) { throw new ReportNotFoundException(reportId); }
            Report report = foundReport.get();
            User userToBan = report.getReportedUser();
            userToBan.setBanned(true);
            userRepository.save(userToBan);
        }

        reportRepository.deleteById(reportId);
    }
}
