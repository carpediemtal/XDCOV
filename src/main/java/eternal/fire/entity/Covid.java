package eternal.fire.entity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Covid {
    private final static String LOGIN_URL = "https://xxcapp.xidian.edu.cn/uc/wap/login/check";
    private final static String UPLOAD_URL = "https://xxcapp.xidian.edu.cn/xisuncov/wap/open-report/save";

    private final static HttpClient httpClient = HttpClient.newBuilder().build();

    private final String username;
    private final String password;
    private final ObjectMapper mapper;


    public Covid(String username, String password) {
        this.username = username;
        this.password = password;
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Covid() throws IOException {
        var account = getAccountFromFile();
        this.username = account[0];
        this.password = account[1];
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void uploadData() throws InterruptedException, IOException, URISyntaxException {
        String cookie;
        try {
            cookie = getCookieByLogIn();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }
        String data = getContentFromFile();
        HttpRequest request = HttpRequest.newBuilder(new URI(UPLOAD_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .headers("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Message message = mapper.readValue(response.body(), Message.class);
        if (message.getE() == 0) {
            System.out.println("上报成功");
        } else {
            System.out.println("上报失败：" + message.getM());
        }
    }

    private String getCookieByLogIn() throws IOException, InterruptedException, URISyntaxException {
        String body = String.format("username=%s&password=%s", username, password);
        HttpRequest request = HttpRequest.newBuilder(new URI(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        Message message = mapper.readValue(response.body(), Message.class);
        if (message.getE() == 0) {
            var cookies = response.headers().allValues("set-cookie");
            StringBuilder ans = new StringBuilder();
            for (var cookie : cookies) {
                ans.append(purifyCookie(cookie));
            }
            return ans.toString();
        }
        throw new RuntimeException("登录失败，请检查用户名和密码是否正确");
    }

    private String purifyCookie(String cookie) {
        int index = cookie.indexOf(';');
        return cookie.substring(0, index + 2);
    }

    private String getContentFromFile() throws IOException {
        InputStreamReader reader = new InputStreamReader(Covid.class.getResourceAsStream("/data.json"));
        StringBuilder data = new StringBuilder();
        char[] buffer = new char[1024];
        if (reader.read(buffer) != -1) {
            for (var ch : buffer) {
                data.append(ch);
            }
        }
        return data.toString();
    }

    public String[] getAccountFromFile() throws IOException {
        Properties properties = new Properties();
        properties.load(Covid.class.getResourceAsStream("/account.properties"));
        String[] ans = new String[2];
        ans[0] = properties.getProperty("username");
        ans[1] = properties.getProperty("password");
        return ans;
    }
}
