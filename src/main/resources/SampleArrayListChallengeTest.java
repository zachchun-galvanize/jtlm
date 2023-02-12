import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SampleArrayListChallengeTest {
    @Test
    public void addAtExampleValues() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        ArrayListChallenge.addAtBeginningMiddleEnd(data, "hi", "test", "bye");
        assertEquals( Arrays.asList("hi", "a", "b", "test", "c", "d", "bye"), data);
    }

    @Test
    public void addAtOddLengthInput() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
        ArrayListChallenge.addAtBeginningMiddleEnd(data, "hi", "test", "bye");
        assertEquals(Arrays.asList("hi", "a", "b", "test", "c", "d", "e", "bye"), data);
    }
}
