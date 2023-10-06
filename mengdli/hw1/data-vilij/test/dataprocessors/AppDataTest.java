package dataprocessors;

import org.junit.Test;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AppDataTest {

    @Test
    public void saveData() {
        AppData appData = new AppData(null);
        Path path = Paths.get("data/data1.tsd");
        appData.saveData(path);
    }
}