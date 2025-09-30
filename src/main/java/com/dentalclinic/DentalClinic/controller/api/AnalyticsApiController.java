package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.service.BillService;
import com.dentalclinic.DentalClinic.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsApiController {

    private final BillService billService;
    private final ExpenseService expenseService;

    public AnalyticsApiController(BillService billService, ExpenseService expenseService) {
        this.billService = billService;
        this.expenseService = expenseService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<Bill> bills = billService.findAll();
        List<Expense> expenses = expenseService.findAll();

        if (start != null) {
            bills = bills.stream().filter(b -> b.getIssuedAt() != null && !b.getIssuedAt().isBefore(start)).toList();
            expenses = expenses.stream().filter(e -> e.getDate() != null && !e.getDate().isBefore(start)).toList();
        }
        if (end != null) {
            bills = bills.stream().filter(b -> b.getIssuedAt() != null && !b.getIssuedAt().isAfter(end)).toList();
            expenses = expenses.stream().filter(e -> e.getDate() != null && !e.getDate().isAfter(end)).toList();
        }

        double totalRevenue = bills.stream().mapToDouble(Bill::getAmount).sum();
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double netBalance = totalRevenue - totalExpenses;

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        response.put("totalExpenses", totalExpenses);
        response.put("netBalance", netBalance);
        response.put("transactions", buildTransactions(bills, expenses));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/report.csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        // Recompute to CSV using services directly
        List<Bill> bills = billService.findAll();
        List<Expense> expenses = expenseService.findAll();
        if (start != null) {
            bills = bills.stream().filter(b -> b.getIssuedAt() != null && !b.getIssuedAt().isBefore(start)).toList();
            expenses = expenses.stream().filter(e -> e.getDate() != null && !e.getDate().isBefore(start)).toList();
        }
        if (end != null) {
            bills = bills.stream().filter(b -> b.getIssuedAt() != null && !b.getIssuedAt().isAfter(end)).toList();
            expenses = expenses.stream().filter(e -> e.getDate() != null && !e.getDate().isAfter(end)).toList();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8);
        writer.println("date,type,party,method,amount");
        for (Bill b : bills) {
            String party = b.getPatient() != null ? Optional.ofNullable(b.getPatient().getFullName()).orElse("Patient") : "Patient";
            writer.printf("%s,revenue,%s,%s,%.2f%n",
                    Optional.ofNullable(b.getIssuedAt()).orElse(LocalDate.now()),
                    escape(party),
                    Optional.ofNullable(b.getPaymentMethod()).orElse(""),
                    b.getAmount());
        }
        for (Expense e : expenses) {
            writer.printf("%s,expense,%s,%s,%.2f%n",
                    Optional.ofNullable(e.getDate()).orElse(LocalDate.now()),
                    escape(Optional.ofNullable(e.getDescription()).orElse("Expense")),
                    "",
                    e.getAmount());
        }
        writer.flush();

        byte[] csvBytes = baos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-report.csv");
        headers.setContentLength(csvBytes.length);
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    private List<Map<String, Object>> buildTransactions(List<Bill> bills, List<Expense> expenses) {
        List<Map<String, Object>> tx = new ArrayList<>();
        for (Bill b : bills) {
            Map<String, Object> row = new HashMap<>();
            row.put("type", "revenue");
            row.put("issuedAt", b.getIssuedAt());
            row.put("amount", b.getAmount());
            row.put("paymentMethod", b.getPaymentMethod());
            row.put("patient", b.getPatient() != null ? Map.of(
                    "id", b.getPatient().getId(),
                    "fullName", b.getPatient().getFullName()
            ) : null);
            tx.add(row);
        }
        for (Expense e : expenses) {
            Map<String, Object> row = new HashMap<>();
            row.put("type", "expense");
            row.put("date", e.getDate());
            row.put("amount", e.getAmount());
            row.put("method", "");
            row.put("description", e.getDescription());
            tx.add(row);
        }
        tx.sort(Comparator.comparing(o -> Optional.ofNullable((LocalDate) Optional.ofNullable(o.get("issuedAt")).orElse(o.get("date"))).orElse(LocalDate.now())));
        return tx;
    }

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }
}


