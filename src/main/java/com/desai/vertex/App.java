package com.desai.vertex;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class App {
    public static void main(String[] args) {
        try (HttpClient client = HttpClient.newBuilder().build()) {
            String body = String.format("""
                    {
                        "contents": [
                            {
                                "role": "user",
                                "parts": [
                                    {
                                        "text": "%s"
                                    },
                                ]
                            }
                        ],
                        "generationConfig": %s
                    }
                    """, getContentValue(), getParameters());
            HttpRequest request = getHttpRequest(body);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("request => {}", request.toString());
            log.info("response => {}", response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Failed", e);
        }
    }

    private static HttpRequest getHttpRequest(String body) throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://%s/v1/projects/%s/locations/%s/publishers/%s/models/%s:streamGenerateContent",
                        System.getenv("API_ENDPOINT"),
                        System.getenv("PROJECT_ID"),
                        System.getenv("LOCATION_ID"),
                        System.getenv("PUBLISHER"),
                        System.getenv("MODEL_ID")
                )))
                .header("Content-Type", "application/json")
                .header("authorization", String.format("Bearer %s", getAccessToken()))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public static String getContentValue() throws IOException {
        String context = System.getenv("CONTEXT");
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("examples.json")) {
            if (inputStream == null) {
                throw new IOException("Resource not found: examples.json");
            }
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                Gson gson = new Gson();
                Map<String, Object>[] examples = gson.fromJson(reader, TypeToken.of(Map[].class).getType());
                StringBuilder inputOutputInstances = new StringBuilder();
                for (Map<String, Object> example : examples) {
                    String inputValue = (String) example.get("input");
                    String outputValue = (String) example.get("output");
                    inputOutputInstances.append(String.format("input: %s\noutput: %s\n\n", inputValue, outputValue));
                }
                String content = String.format("%s\n\n%sinput: %s\noutput:\n", context, inputOutputInstances, getPrompt());
                //log.info("content => {}", content);
                return content;
            }
        }
    }

    private static String getParameters() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("candidateCount", Integer.parseInt(System.getenv("CANDIDATE_COUNT")));
        parameterMap.put("maxOutputTokens", Integer.parseInt(System.getenv("MAX_OUTPUT_TOKENS")));
        parameterMap.put("temperature", Double.parseDouble(System.getenv("TEMPERATURE")));
        parameterMap.put("topP", Double.parseDouble(System.getenv("TOP-P")));
        parameterMap.put("topK", Integer.parseInt(System.getenv("TOP-K")));
        return new Gson().toJson(parameterMap);
    }

    private static String getAccessToken() throws IOException {
        try (InputStream serviceAccountStream = App.class.getClassLoader().getResourceAsStream(
                System.getenv("SERVICE_ACCOUNT_FILE_PATH"))) {
            if (serviceAccountStream == null) {
                throw new FileNotFoundException("Service account file not found");
            }
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(serviceAccountStream)
                    .createScoped(Lists.newArrayList(System.getenv("SCOPES")));
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        }
    }

    private static String getPrompt() {
        System.out.println("Type a prompt:");
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }
}
