package xyz.zes.zokes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import xyz.zes.zokes.setting.ZokesSettingState;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

import static xyz.zes.zokes.setting.Constant.*;

public class Main implements StartupActivity {
    private static final Random random = new Random();

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private static final String jokeAPIURL = "https://v2.jokeapi.dev/joke/Any?lang=en&blacklistFlags=nsfw,religious,political,racist,sexist,explicit";

    private static final ObjectMapper mapper =  new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);


    @Override
    public void runActivity(@NotNull Project project) {

        ZokesSettingState state = ZokesSettingState.getInstance();
        Thread t = new Thread(()->{
            final Notification[] prev = {null};
            while (true) {
                if(!state.isGenerateJoke)continue;
                try {
                    var req = HttpRequest.newBuilder()
                            .uri(new URI(jokeAPIURL))
                            .GET()
                            .build();
                    var respFuture = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
                    respFuture.thenAccept((respStr)->{
                        try {
                            JokesApiResp resp = mapper.convertValue(mapper.readTree(respStr.body()), JokesApiResp.class);
                            String content = resp.getSetup();
                            if(content!=null && !content.isEmpty() && !content.isBlank()){
                                if(prev[0]!=null && !prev[0].isExpired()){
                                    prev[0].expire();
                                }

                                String answer = resp.getDelivery();
                                NotificationGroup notifGroup = NotificationGroupManager.getInstance()
                                        .getNotificationGroup("Zokes");

                                Notification jokesNotif = notifGroup
                                        .createNotification(resp.getSetup()+" <b>"+ answer + "</b>", NotificationType.INFORMATION);

                                String title = this.resolveTitle(resp.getCategory());
                                jokesNotif.setTitle(title);
                                jokesNotif.notify(project);

                                prev[0] = jokesNotif;
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                    });
                    Thread.sleep(state.intervalSeconds * 1000L);
                } catch (InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        t.start();
    }

    private String resolveTitle(String category) {
        String[] candidates = TITLES.getOrDefault(category.toLowerCase(),new String[]{"Jokes for your day"});
        int len = candidates.length;
        int selected = random.nextInt(len);
        return candidates[selected];
    }

}
