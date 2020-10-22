package eternal.fire.utils;

import java.io.IOException;
import java.util.Timer;

public class Schedule {
    public static void uploadDataEveryTwoHours() throws IOException {
        Timer timer = new Timer();
        timer.schedule(new CovidTimerTask(), 0, 1000 * 60 * 60 * 2);
    }
}
