package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.controller.model.GenericResponse;
import movie_master.api.dto.ReportDto;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.CreateReportRequest;
import movie_master.api.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Object> reportUser(
        HttpServletRequest httpServletRequest, @RequestBody CreateReportRequest request
    ) {
        try {
            ReportDto createdReport = reportService.createReport(request);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(createdReport);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new GenericResponse(e.getMessage()));
        }
    }
}
