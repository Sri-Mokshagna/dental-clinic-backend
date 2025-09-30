package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.BillItem;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.service.BillService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.PdfService;
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;
import com.dentalclinic.DentalClinic.util.WhatsAppTemplates;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "http://localhost:3000")
public class BillApiController {

    private final BillService billService;
    private final PatientService patientService;
    private final UserService userService;
    private final WhatsAppSender whatsAppSender;
    private final WhatsAppTemplates whatsAppTemplates;
    private final PdfService pdfService;

    public BillApiController(BillService billService, PatientService patientService, UserService userService, 
                           WhatsAppSender whatsAppSender, WhatsAppTemplates whatsAppTemplates,PdfService pdfService) {
        this.billService = billService;
        this.patientService = patientService;
        this.userService = userService;
        this.whatsAppSender = whatsAppSender;
        this.whatsAppTemplates = whatsAppTemplates;
        this.pdfService = pdfService;
    }

    private Map<String, Object> toResponse(Bill bill) {
        Map<String, Object> res = new HashMap<>();
        res.put("id", bill.getId());
        res.put("amount", bill.getAmount());
        res.put("status", bill.getStatus());
        res.put("issuedAt", bill.getIssuedAt());
        if (bill.getPaymentDate() != null) res.put("paymentDate", bill.getPaymentDate());
        if (bill.getPaymentMethod() != null) res.put("paymentMethod", bill.getPaymentMethod());

        // patient minimal
        if (bill.getPatient() != null) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", bill.getPatient().getId());
            p.put("fullName", bill.getPatient().getFullName());
            p.put("phoneNumber", bill.getPatient().getPhoneNumber());
            res.put("patient", p);
        }

        // items
        List<Map<String, Object>> items = new ArrayList<>();
        if (bill.getItems() != null) {
            for (var it : bill.getItems()) {
                Map<String, Object> i = new HashMap<>();
                i.put("description", it.getDescription());
                i.put("cost", it.getCost());
                items.add(i);
            }
        }
        res.put("items", items);

        // created/processed by minimal
        if (bill.getCreatedBy() != null) {
            Map<String, Object> u = new HashMap<>();
            u.put("id", bill.getCreatedBy().getId());
            u.put("username", bill.getCreatedBy().getUsername());
            res.put("createdBy", u);
        }
        if (bill.getProcessedBy() != null) {
            Map<String, Object> u = new HashMap<>();
            u.put("id", bill.getProcessedBy().getId());
            u.put("username", bill.getProcessedBy().getUsername());
            res.put("processedBy", u);
        }
        return res;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Bill> bills = billService.findAll();
        return ResponseEntity.ok(bills.stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req) {
        try {
            Long patientId = req.get("patientId") != null ? ((Number) req.get("patientId")).longValue() : null;
            if (patientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "patientId is required"));
            }
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }

            Bill bill = new Bill();
            bill.setPatient(patientOpt.get());
            bill.setIssuedAt(LocalDate.now());
            bill.setStatus("UNPAID");

            if (req.get("amount") != null) {
                bill.setAmount(((Number) req.get("amount")).doubleValue());
            }

            // createdBy username (optional)
            if (req.get("createdBy") != null) {
                String username = String.valueOf(req.get("createdBy"));
                var uOpt = userService.findByUsernameOrPhone(username);
                if (uOpt != null && uOpt.isPresent()) {
                    bill.setCreatedBy((com.dentalclinic.DentalClinic.model.User) uOpt.get());
                }
            }

            // items (optional)
            Object itemsObj = req.get("items");
            if (itemsObj instanceof List) {
                List<?> list = (List<?>) itemsObj;
                for (Object obj : list) {
                    if (obj instanceof Map) {
                        Map<?, ?> it = (Map<?, ?>) obj;
                        BillItem bi = new BillItem();
                        bi.setBill(bill);
                        Object descObj = it.get("description");
                        bi.setDescription(descObj != null ? String.valueOf(descObj) : "");
                        Object costObj = it.get("cost");
                        double cost = 0;
                        if (costObj instanceof Number) cost = ((Number) costObj).doubleValue();
                        bi.setCost(cost);
                        bill.getItems().add(bi);
                    }
                }
            }

            Bill saved = billService.save(bill);
            return ResponseEntity.ok(toResponse(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOpt = patientService.findById(patientId);
        if (patientOpt.isEmpty()) return ResponseEntity.notFound().build();
        List<Bill> bills = billService.findByPatient(patientOpt.get());
        return ResponseEntity.ok(bills.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return billService.findById(id)
                .map(b -> ResponseEntity.ok(toResponse(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/record-payment")
    public ResponseEntity<?> recordPayment(@RequestBody Map<String, Object> req) {
        try {
            Long billId = ((Number) req.get("billId")).longValue();
            String paymentMethod = (String) req.get("paymentMethod");
            Double amount = ((Number) req.get("amount")).doubleValue();
            
            Optional<Bill> billOpt = billService.findById(billId);
            if (billOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Bill not found"));
            }
            
            Bill bill = billOpt.get();
            bill.setStatus("PAID");
            bill.setPaymentMethod(paymentMethod);
            bill.setPaymentDate(LocalDate.now());
            
            Bill saved = billService.save(bill);
            
            // Send WhatsApp notification
            try {
                Patient patient = bill.getPatient();
                String message = whatsAppTemplates.getBillPaymentTemplate(
                    patient.getFullName(),
                    bill.getId().toString(),
                    amount.toString(),
                    paymentMethod,
                    LocalDate.now().toString()
                );
                
                whatsAppSender.sendMessage(patient.getPhoneNumber(), message, patient.getFullName());
            } catch (Exception e) {
                System.err.println("Failed to send payment notification: " + e.getMessage());
            }
            
            return ResponseEntity.ok(toResponse(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{billId}/send-to-whatsapp")
public ResponseEntity<?> sendBillToWhatsApp(@PathVariable Long billId) {
    try {
        Bill bill = billService.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        Patient patient = bill.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Patient phone number not available"));
        }

        // 1. Generate PDF
        String fileName = "Bill-" + bill.getId() + ".pdf";
        Path pdfPath = pdfService.generateBillPdf(bill, fileName);

        // 2. Upload or expose URL (for now local path converted to URL)
        String fileUrl = "http://localhost:8080/files/" + fileName;

        // 3. Send WhatsApp message using template
        Map<String, String> vars = Map.of(
            "1", patient.getFullName(),
            "2", "Bill",
            "3", fileName,
            "4", "Bill"
        );

        whatsAppSender.sendTemplateWithMedia(
            patient.getPhoneNumber(),
            "HX230b4b964d4436ce0fb7c001bac63f6b", // your fileShare template SID
            vars,
            fileUrl
        );

        return ResponseEntity.ok(Map.of("status", "success", "message", "Bill sent to WhatsApp"));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

    // Backward-compatible alias
    @PostMapping(path = "/payment")
    public ResponseEntity<?> paymentAlias(@RequestBody Map<String, Object> req) {
        return recordPayment(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Optional<Bill> billOpt = billService.findById(id);
        if (billOpt.isEmpty()) return ResponseEntity.notFound().build();
        Bill bill = billOpt.get();

        if (req.get("amount") != null) bill.setAmount(((Number) req.get("amount")).doubleValue());
        if (req.get("status") != null) bill.setStatus((String) req.get("status"));
        if (req.get("paymentMethod") != null) bill.setPaymentMethod((String) req.get("paymentMethod"));
        if (req.get("paymentDate") != null) bill.setPaymentDate(LocalDate.parse((String) req.get("paymentDate")));

        // replace items if provided
        if (req.get("items") != null) {
            bill.getItems().clear();
            List<Map<String, Object>> items = (List<Map<String, Object>>) req.get("items");
            for (Map<String, Object> it : items) {
                BillItem bi = new BillItem();
                bi.setBill(bill);
                bi.setDescription((String) it.get("description"));
                bi.setCost(((Number) it.getOrDefault("cost", 0)).doubleValue());
                bill.getItems().add(bi);
            }
        }

        return ResponseEntity.ok(toResponse(billService.save(bill)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        billService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Bill deleted"));
    }
}


