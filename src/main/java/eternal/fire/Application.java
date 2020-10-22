package eternal.fire;

import eternal.fire.utils.Schedule;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        Schedule.uploadDataEveryTwoHours();
    }
}
