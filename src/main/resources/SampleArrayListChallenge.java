import java.util.ArrayList;
class SampleArrayListChallenge {
    public static void addAtBeginningMiddleEnd(ArrayList<String> list, String s1, String s2, String s3) {
        // Implement the method here
        // EXCLUDE-START
        list.add(list.size() / 2, s2);
        list.add(0, s1);
        list.add(s3);
        // EXCLUDE-END
    }
}