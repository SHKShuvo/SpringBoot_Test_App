package com.example.test.Controller;

import com.example.test.Common.DBConnection;
import com.example.test.Common.LoggerManager;
import com.example.test.Entity.JsonIgnoreUser;
import com.example.test.Entity.UserEntity;
import com.example.test.Repository.UserRepository;
import com.example.test.Service.ServiceImpl.TestService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/gums/v1/test")
@Tag(name = "TEST Management", description = "TEST APIs")
public class TestController {

    private final DBConnection dbConn = new DBConnection();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService testService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    //  GET without RequestBody (fixed)
    @GetMapping("/get")
    @Operation(summary = "Get sample data", description = "Returns sample list of items")
    public String getList(@RequestHeader Map<String, String> headers) {

        String name = headers.get("user-name");
        String date = headers.get("user-date");

        boolean isValid = testService.isValidDate(date);
        System.out.println("Name: " + name + " | Date: " + date + " | Valid: " + isValid);

        try {
            List<Map<String, String>> data = new ArrayList<>();

            Map<String, String> item1 = Map.of(
                    "image", "2130837526",
                    "category", "Chairs",
                    "quantity", "1"
            );

            Map<String, String> item2 = Map.of(
                    "image", "2130837566",
                    "category", "Mirrors",
                    "quantity", "2"
            );

            data.add(item1);
            data.add(item2);

            return objectMapper.writeValueAsString(data);

        } catch (Exception e) {
            return "Error generating response";
        }
    }

    // JSON Parsing Example
    @GetMapping("/get2")
    @Operation(summary = "Parse JSON", description = "Reads JSON and extracts fields")
    public String getList2() {
        String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";

        try {
            JsonNode node = objectMapper.readTree(json);
            return node.get("color").asText() + "-" + node.get("type").asText();
        } catch (Exception e) {
            return "Error parsing JSON";
        }
    }

    //  DB Query Example
    @PostMapping("/getDbConnection")
    @Operation(summary = "Test DB Connection")
    public String getDBConnection() {

        LoggerManager logger = new LoggerManager();

        try (Connection connection = dbConn.getAuthDBConnection()) {

            String sql = "SELECT * FROM SHUVO.USERS WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "1");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fname = rs.getString("FIRST_NAME");
                String lname = rs.getString("LAST_NAME");

                logger.writeActivityLog("FIRST_NAME: " + fname + " | LAST_NAME: " + lname);
            }

        } catch (Exception e) {
            return "DB Error: " + e.getMessage();
        }

        return "DB Success";
    }

    //  Logging + Config Read
    @PostMapping("/api/testLog")
    @Operation(summary = "Test Logging API")
    public Map<String, Object> testLog(@RequestBody Object request) {

        LoggerManager logger = new LoggerManager();
        Map<String, Object> response = new HashMap<>();

        try {
            String json = objectMapper.writeValueAsString(request);
            logger.writeActivityLog(json);

            String requestBody = new JSONObject()
                    .put("grant_type", "jwt")
                    .put("assertion", "Hello")
                    .toString();

            String ip = InetAddress.getLocalHost().getHostAddress();

            response.put("requestBody", requestBody);
            response.put("ip", ip);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }

    //  Simple PDF
    @GetMapping("/generate-pdf")
    @Operation(summary = "Generate simple PDF")
    public ResponseEntity<byte[]> generatePdf() {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Hello, Sayed Hossain Khan!"));
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //  User PDF
    @GetMapping("/users/pdf")
    @Operation(summary = "Generate user report PDF")
    public ResponseEntity<byte[]> generateUserPdf() {

        try {
            List<UserEntity> users = userRepository.findAll();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("User Report").setBold().setTextAlignment(TextAlignment.CENTER));

            float[] cols = {30F, 70F, 70F, 120F};
            Table table = new Table(cols);

            table.addHeaderCell("ID");
            table.addHeaderCell("First Name");
            table.addHeaderCell("Last Name");
            table.addHeaderCell("Email");

            for (UserEntity user : users) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(Optional.ofNullable(user.getFirstName()).orElse(""));
                table.addCell(Optional.ofNullable(user.getLastName()).orElse(""));
                table.addCell(Optional.ofNullable(user.getEmail()).orElse(""));
            }

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //  JSON Ignore Example
    @GetMapping("/jsonIgnore")
    @Operation(summary = "Test Json Ignore")
    public JsonIgnoreUser jsonIgnore() {

        String json = "{ \"name\": \"Alice\", \"age\": 25, \"extraField\": \"ignored\" }";

        try {
            return objectMapper.readValue(json, JsonIgnoreUser.class);
        } catch (Exception e) {
            return new JsonIgnoreUser();
        }
    }

    //  SOAP Call
    @PostMapping("/getSoapGetCurrentAccount")
    @Operation(summary = "Call SOAP API")
    public String getSoap(@RequestBody Object request) {
        return testService.getSoapGetCurrentAccount("1102108444001", "", "");
    }

    //  WSO2 API
    @PostMapping("/getWSO2API")
    @Operation(summary = "Call WSO2 API")
    public String getWSO2API(@RequestBody Object request) {
        return testService.getWSO2API(request);
    }
}