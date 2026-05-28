package eventlogprocessor.controller;

import eventlogprocessor.dto.EventStatisticsResponse;
import eventlogprocessor.export.CsvExportService;
import eventlogprocessor.processing.EventProcessingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/events")
public class EventController {

    private final EventProcessingService processingService;
    private final CsvExportService csvExportService;

    @Autowired
    public EventController(EventProcessingService processingService, CsvExportService csvExportService) {
        this.processingService = processingService;
        this.csvExportService = csvExportService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventStatisticsResponse processEvents(@RequestParam("file") MultipartFile file)  throws Exception {
        return processingService.process(file.getInputStream());
    }

    @PostMapping("/export/csv")
    public void exportCsv(@RequestBody EventStatisticsResponse stats, HttpServletResponse response) {

        csvExportService.exportToCsvToResponse(stats, response);

    }
}
