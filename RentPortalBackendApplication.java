package payment;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class RentPortalBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(RentPortalBackendApplication.class, args);
	}

	// --- CORS (single-file) ---
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*"); // Use addAllowedOriginPattern for Spring Boot 2.4+
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	// --- Model ---
	public static class Payment {
		public enum Status { CREATED, SUCCESS, FAILED }

		private final String id = UUID.randomUUID().toString();
		private final Instant createdAt = Instant.now();
		private Status status = Status.CREATED;
		private String upiUrl;
		private String qrUrl;
		private String amount;
		private String upiId;
		private String payeeName;
		private String note;

		public String getId() { return id; }
		public Instant getCreatedAt() { return createdAt; }
		public Status getStatus() { return status; }
		public void setStatus(Status status) { this.status = status; }
		public String getUpiUrl() { return upiUrl; }
		public void setUpiUrl(String upiUrl) { this.upiUrl = upiUrl; }
		public String getQrUrl() { return qrUrl; }
		public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }
		public String getAmount() { return amount; }
		public void setAmount(String amount) { this.amount = amount; }
		public String getUpiId() { return upiId; }
		public void setUpiId(String upiId) { this.upiId = upiId; }
		public String getPayeeName() { return payeeName; }
		public void setPayeeName(String payeeName) { this.payeeName = payeeName; }
		public String getNote() { return note; }
		public void setNote(String note) { this.note = note; }
	}

	// --- Service ---
	@org.springframework.stereotype.Service
	public static class PaymentService {
		@Value("${rent.upiId}")
		private String upiId;
		@Value("${rent.payeeName}")
		private String payeeName;
		@Value("${rent.amountINR}")
		private String amountINR;
		@Value("${rent.note}")
		private String note;

		private final Map<String, Payment> store = new ConcurrentHashMap<>();

		public Payment initiate() {
			Payment p = new Payment();
			p.setAmount(amountINR);
			p.setUpiId(upiId);
			p.setPayeeName(payeeName);
			p.setNote(note);
			String upiUrl = buildUpiUrl(upiId, payeeName, amountINR, note);
			p.setUpiUrl(upiUrl);
			p.setQrUrl(buildQrUrl(upiUrl));
			store.put(p.getId(), p);
			return p;
		}

		public Optional<Payment> find(String id) { return Optional.ofNullable(store.get(id)); }
		public Optional<Payment> markSuccess(String id) { return updateStatus(id, Payment.Status.SUCCESS); }
		public Optional<Payment> markFailed(String id) { return updateStatus(id, Payment.Status.FAILED); }

		private Optional<Payment> updateStatus(String id, Payment.Status s) {
			Payment p = store.get(id);
			if (p == null) return Optional.empty();
			p.setStatus(s);
			return Optional.of(p);
		}

		private static String buildUpiUrl(String pa, String pn, String am, String tn) {
			String qp = "pa=" + enc(pa) + "&pn=" + enc(pn) + "&am=" + enc(am) + "&cu=INR&tn=" + enc(tn);
			return "upi://pay?" + qp;
		}

		private static String buildQrUrl(String data) {
			return "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=" + enc(data);
		}

		private static String enc(String v) { return URLEncoder.encode(v, StandardCharsets.UTF_8); }
	}

	// --- Controller ---
	@RestController
	@RequestMapping("/api")
	@Validated
	public static class PaymentController {
		private final PaymentService paymentService;

		public PaymentController(PaymentService paymentService) {
			this.paymentService = paymentService;
		}

		@Value("${rent.ownerName}")
		private String ownerName;
		@Value("${rent.payeeName}")
		private String payeeName;
		@Value("${rent.upiId}")
		private String upiId;
		@Value("${rent.amountINR}")
		private String amountINR;
		@Value("${rent.note}")
		private String note;

		@GetMapping("/config")
		public Map<String, String> config() {
			return Map.of(
				"ownerName", ownerName,
				"payeeName", payeeName,
				"upiId", upiId,
				"amountINR", amountINR,
				"note", note
			);
		}

		@PostMapping("/payments")
		public Payment initiate() {
			return paymentService.initiate();
		}

		@GetMapping("/payments/{id}")
		public ResponseEntity<Payment> get(@PathVariable String id) {
			return paymentService.find(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		}

		@PostMapping("/payments/{id}/success")
		public ResponseEntity<Payment> success(@PathVariable String id) {
			return paymentService.markSuccess(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		}

		@PostMapping("/payments/{id}/failed")
		public ResponseEntity<Payment> failed(@PathVariable String id) {
			return paymentService.markFailed(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		}
	}
}
