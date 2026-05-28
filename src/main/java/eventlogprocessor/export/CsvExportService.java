package eventlogprocessor.export;

import eventlogprocessor.dto.EventStatisticsResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

@Service
@Slf4j
public class CsvExportService {




    public void exportToCsvToResponse(EventStatisticsResponse stats, HttpServletResponse response){

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=statistics.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Event Statistics");
            writer.println("Total Valid Events,Total Invalid Lines,Total Purchase Amount,Average Purchase Amount,Largest Purchase,Most Active User");
            writer.println(stats.getTotalValidEvents() + "," +
                    stats.getTotalInvalidLines() + "," +
                    stats.getTotalPurchaseAmount() + "," +
                    stats.getAveragePurchaseAmount() + "," +
                    stats.getLargestPurchase() + "," +
                    stats.getMostActiveUser());

            writer.println();

            writer.println("Top 3 Users");
            writer.println("User ID,Event Count");
            stats.getTopUsers().forEach(user -> writer.println(escapeCsvValue(user.getUserId() + "," + user.getEventCount())));

            writer.println();

            writer.println("Event Per Action");
            writer.println("Action,Event Count");
            stats.getEventPerAction().forEach((action, count) -> writer.println(escapeCsvValue(action + "," + count)));

            writer.println();

            writer.println("Event Per User");
            writer.println("User ID,Event Count");
            stats.getEventPerUser().forEach((user, count) -> writer.println(user + "," + count));

        } catch (IOException e) {
            log.error("Error exporting statistics to CSV: {}", e.getMessage());
            System.out.println("An error occurred while exporting statistics to CSV: " + e.getMessage());

        }


    }

    private String escapeCsvValue(String value) {

        if (value == null) {
            return "";
        }

        boolean needEscape =
                value.contains(",") ||
                        value.contains("\"") ||
                        value.contains("\n");

        if (!needEscape) {
            return value;
        }

        value = value.replace("\n", " ");
        value = value.replace("\"", "\"\"");

        return "\"" + value + "\"";
    }


}

