package java_packages;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        
        WebhookRequest request = new WebhookRequest(
                "Drishti Garg",          
                "22BCE2237",          
                "drishti.garg2022@vitstudent.ac.in"   
        );

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA",
                request,
                WebhookResponse.class
        );

        WebhookResponse webhookResponse = response.getBody();
        if (webhookResponse == null) {
            System.out.println("Failed to get webhook response!");
            return;
        }

        String webhookUrl = webhookResponse.getWebhook();
        String accessToken = webhookResponse.getAccessToken();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        
        String finalSQL = "SELECT p.AMOUNT AS SALARY, " +
    "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
    "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
    "d.DEPARTMENT_NAME " +
    "FROM PAYMENTS p " +
    "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
    "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
    "ORDER BY p.AMOUNT DESC LIMIT 1;";


        FinalQueryRequest sqlRequest = new FinalQueryRequest(finalSQL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(sqlRequest, headers);

        ResponseEntity<String> submitResponse = restTemplate.postForEntity(
                webhookUrl,
                entity,
                String.class
        );

        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}
