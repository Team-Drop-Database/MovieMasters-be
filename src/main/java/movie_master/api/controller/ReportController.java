package movie_master.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import movie_master.api.controller.model.GenericResponse;
import movie_master.api.dto.ReportDto;
import movie_master.api.exception.ReportNotFoundException;
import movie_master.api.exception.UserNotFoundException;
import movie_master.api.request.CreateReportRequest;
import movie_master.api.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller class for handling report-related API requests.
 * Provides endpoints to create, retrieve, and delete reports.
 */
@RestController
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * Constructs a new ReportController with the specified ReportService.
     *
     * @param reportService the service used for report operations
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Endpoint for creating a new report.
     *
     * @param httpServletRequest the HTTP request
     * @param request the request body containing report creation details
     * @return a ResponseEntity containing the created report or a not found response
     */
    @PostMapping
    public ResponseEntity<Object> reportUser(
        HttpServletRequest httpServletRequest, @RequestBody CreateReportRequest request
    ) {
        try {
            ReportDto createdReport = reportService.createReport(request);
            return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(createdReport);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for retrieving all reports.
     *
     * @return a ResponseEntity containing the list of reports
     */
    @GetMapping("all")
    public ResponseEntity<Object> retrieveAllReports() {
        List<ReportDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Endpoint for retrieving a specific report by its ID.
     *
     * @param reportId the ID of the report to retrieve
     * @return a ResponseEntity containing the requested report or a not found response
     */
    @GetMapping
    public ResponseEntity<Object> retrieveReport(@RequestParam long reportId) {
        try {
            ReportDto report = reportService.getReportById(reportId);
            return ResponseEntity.ok(report);
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for deleting a report and optionally banning the user associated with it.
     *
     * @param reportId the ID of the report to delete
     * @param banUser a boolean indicating whether to ban the user associated with the report
     * @return a ResponseEntity containing a success message or a not found response
     */
    @DeleteMapping
    public ResponseEntity<Object> deleteReport(@RequestParam long reportId, @RequestParam boolean banUser) {
        try {
            reportService.deleteReport(reportId, banUser);
            return ResponseEntity.ok(new GenericResponse("Report deleted successfully"));
        } catch (ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
