package eternal.fire.utils;

import eternal.fire.entity.Covid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.TimerTask;

public class CovidTimerTask extends TimerTask {
    private final Covid covid;

    public CovidTimerTask() throws IOException {
        this.covid = new Covid();
    }

    @Override
    public void run() {
        try {
            this.covid.uploadData();
        } catch (InterruptedException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
