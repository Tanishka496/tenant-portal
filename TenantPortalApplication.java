package chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class TenantPortalApplication 
{

    public static void main(String[] args) 
    {
        SpringApplication.run(TenantPortalApplication.class, args);
    }

    // Message model
    public static class Message 
    {
        private String sender;
        private String content;
        private String timestamp;

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    // REST Controller
    @RestController
    @RequestMapping("/api/messages")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public static class MessageController {
        private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

        @GetMapping
        public List<Message> getMessages() {
            return messages;
        }

        @PostMapping
        public Message postMessage(@RequestBody Message message) {
            message.setTimestamp(java.time.LocalDateTime.now().toString());
            messages.add(message);
            return message;
        }

        @DeleteMapping("/latest")
        public void deleteLatestMessage() {
            synchronized (messages) {
                if (!messages.isEmpty()) {
                    messages.remove(messages.size() - 1);
                }
            }
        }
    }

    // Global CORS config
    @Configuration
    public static class WebConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/messages/**")
                            .allowedOrigins("*")
                            .allowedMethods("GET", "POST", "DELETE", "OPTIONS");
                }
            };
        }
    }
}