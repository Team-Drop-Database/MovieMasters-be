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

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportDtoMapper reportDtoMapper;

    public ReportService(
        ReportRepository reportRepository, UserRepository userRepository, ReportDtoMapper reportDtoMapper
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportDtoMapper = reportDtoMapper;
    }

    public ReportDto createReport(CreateReportRequest request) throws UserNotFoundException {
        Optional<User> foundUser = userRepository.findById(request.userId());
        if (foundUser.isEmpty()) { throw new UserNotFoundException(request.userId()); }
        User reportedUser = foundUser.get();

        Report reportToCreate = new Report(reportedUser, request.reason());
        Report createdReport = reportRepository.save(reportToCreate);

        return reportDtoMapper.mapToDto(createdReport);
    }

    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream().map(reportDtoMapper::mapToDto).toList();
    }

    public ReportDto getReportById(long reportId) throws ReportNotFoundException {
        Optional<Report> foundReport = reportRepository.findById(reportId);
        if (foundReport.isEmpty()) { throw new ReportNotFoundException(reportId); }
        Report report = foundReport.get();

        return reportDtoMapper.mapToDto(report);
    }

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
