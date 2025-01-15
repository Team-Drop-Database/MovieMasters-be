package movie_master.api.service;

import movie_master.api.dto.ReportDto;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.mapper.UserDtoMapper;
import movie_master.api.model.Report;
import movie_master.api.model.User;
import movie_master.api.repository.ReportRepository;
import movie_master.api.repository.UserRepository;
import movie_master.api.request.CreateReportRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public ReportService(
        ReportRepository reportRepository, UserRepository userRepository, UserDtoMapper userDtoMapper
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public ReportDto createReport(CreateReportRequest request) throws UserNotFoundException {
        Optional<User> foundUser = userRepository.findById(request.userId());
        if (foundUser.isEmpty()) { throw new UserNotFoundException(request.userId()); }
        User reportedUser = foundUser.get();

        Report reportToCreate = new Report(reportedUser, request.reason());
        Report createdReport = reportRepository.save(reportToCreate);

        return new ReportDto(
            createdReport.getId(),
            userDtoMapper.apply(createdReport.getReportedUser()),
            createdReport.getReason()
        );
    }
}
