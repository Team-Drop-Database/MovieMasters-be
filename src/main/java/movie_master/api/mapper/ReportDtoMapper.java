package movie_master.api.mapper;

import movie_master.api.dto.ReportDto;
import movie_master.api.model.Report;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportDtoMapper {

    private final UserDtoMapper userDtoMapper;

    public ReportDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    public ReportDto mapToDto(Report report) {
        return new ReportDto(
            report.getId(),
            userDtoMapper.apply(report.getReportedUser()),
            report.getReason()
        );
    }

    public List<ReportDto> mapToDto(List<Report> reports) {
        return reports.stream().map(this::mapToDto).toList();
    }
}
