package xyz.zes.zokes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import xyz.zes.zokes.setting.ZokesSettingState;
import xyz.zes.zokes.ui.NotificationStyleUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static xyz.zes.zokes.setting.Constant.TITLES;

public class Main implements StartupActivity {
    private static final Random random = new Random();
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // HTTP client with timeout
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // Joke API base URL
    private static final String BASE_JOKE_API_URL = "https://v2.jokeapi.dev/joke/";
    private static final String JOKE_API_BASE_PARAMS = "?lang=en&blacklistFlags=nsfw,religious,political,racist,sexist,explicit";


    @Override
    public void runActivity(@NotNull Project project) {
        // Start the joke fetching thread
        Thread jokeThread = new Thread(() -> runJokeLoop(project));
        jokeThread.setDaemon(true);
        jokeThread.start();
    }

    /**
     * Main joke fetching loop
     */
    private void runJokeLoop(Project project) {
        Notification[] previousNotification = {null};
        ActivityTracker activityTracker = ApplicationManager.getApplication().getService(ActivityTracker.class);


        while (true) {
            ZokesSettingState settings = ZokesSettingState.getInstance();

            try {
                // Check if jokes are enabled
                if (!settings.isGenerateJoke) {
                    Thread.sleep(1000); // Sleep briefly to reduce CPU usage when disabled
                    continue;
                }

                boolean shouldShowJoke = true;

                // Apply smart timing if enabled
                if (settings.useSmartTiming) {
                    // Check if user is in deep focus - if so, don't interrupt with jokes
                    if (activityTracker.isUserFocused(settings.focusThresholdSeconds)) {
                        shouldShowJoke = false;
                    }
                    // Check if user is taking a break - show joke only if prioritizing breaks
                    else if (activityTracker.isUserTakingBreak(settings.breakThresholdSeconds)) {
                        shouldShowJoke = settings.prioritizeBreaks || shouldShowJoke;
                    }
                    // Otherwise, follow normal interval timing
                }

                // Fetch and display a joke if appropriate
                if (shouldShowJoke) {
                    fetchAndDisplayJoke(project, settings, previousNotification);
                }

                // Wait for the specified interval
                Thread.sleep(settings.intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                // Thread was interrupted
                break;
            } catch (Exception e) {
                // Log error but continue running
                System.err.println("Error in joke thread: " + e.getMessage());
                try {
                    Thread.sleep(5000); // Wait a bit before retrying after an error
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
    }

    /**
     * Fetch a joke from the API and display it
     */
    private void fetchAndDisplayJoke(Project project, ZokesSettingState settings, Notification[] previousNotification) {
        try {
            // Build the API URL with parameters
            String apiUrl = buildJokeApiUrl(settings);

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .GET()
                    .build();

            // Send the request asynchronously
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> processJokeResponse(response, project, previousNotification));

        } catch (URISyntaxException e) {
            throw new RuntimeException("Error building joke API URL", e);
        }
    }

    /**
     * Process the joke API response
     */
    private void processJokeResponse(HttpResponse<String> response, Project project, Notification[] previousNotification) {
        try {
            // Parse the response
            JokesApiResp jokeResponse = mapper.convertValue(
                    mapper.readTree(response.body()),
                    JokesApiResp.class
            );

            // Check if we got a valid joke
            if (jokeResponse.isError()) {
                return;
            }

            // Handle both single and twopart joke types
            String setup = jokeResponse.getSetup();
            String delivery = jokeResponse.getDelivery();

            // If it's a single joke type
            if (setup == null || setup.isBlank()) {
                // Single type joke - use the joke content directly
                setup = jokeResponse.getJoke();
                delivery = "";
            }

            // If we have a valid joke to display
            if (setup != null && !setup.isBlank()) {
                // Expire previous notification if it exists
                if (previousNotification[0] != null) {
                    try {
                        previousNotification[0].expire();
                    } catch (Exception e) {
                        // Ignore errors from expiring notifications
                    }
                }

                // Get a title based on the joke category
                String title = resolveTitle(jokeResponse.getCategory());

                // Create and display the notification
                Notification notification = NotificationStyleUtil.createJokeNotification(
                        title, setup, delivery, project);

                // Store for later reference
                previousNotification[0] = notification;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing joke response", e);
        }
    }

    /**
     * Build the joke API URL with all parameters
     */
    private String buildJokeApiUrl(ZokesSettingState settings) {
        // Get enabled categories
        Set<String> enabledCategories = settings.enabledCategories;
        if (enabledCategories.isEmpty()) {
            // If no categories are selected, use "Any"
            enabledCategories = Set.of("Any");
        }

        // Build URL with parameters
        StringBuilder urlBuilder = new StringBuilder(BASE_JOKE_API_URL);

        // Add categories
        urlBuilder.append(String.join(",", enabledCategories));

        // Add base parameters
        urlBuilder.append(JOKE_API_BASE_PARAMS);

        // Add joke type parameter if not "Any"
        if (!"Any".equals(settings.jokeType)) {
            urlBuilder.append("&type=").append(settings.jokeType);
        }

        return urlBuilder.toString();
    }

    /**
     * Get a random title based on joke category
     */
    private String resolveTitle(String category) {
        String[] candidates = TITLES.getOrDefault(
                category == null ? "misc" : category.toLowerCase(),
                new String[]{"🎭 Jokes for your day"}
        );
        int len = candidates.length;
        int selected = random.nextInt(len);
        return candidates[selected];
    }
}