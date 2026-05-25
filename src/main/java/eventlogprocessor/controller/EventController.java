package eventlogprocessor.controller;

import eventlogprocessor.dto.EventStatisticsResponse;
import eventlogprocessor.processing.EventProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/events")
public class EventController {

    private final EventProcessingService processingService;

    @Autowired
    public EventController(EventProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventStatisticsResponse processEvents(@RequestParam("file") MultipartFile file)  throws Exception {
        return processingService.process(file.getInputStream());
    }
}
