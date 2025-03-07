package movie_master.utils;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.ObjectCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static <T> T createRandomRecord(Class<T> recordType, EasyRandom easyRandom) {
        // generate random values for record components
        RecordComponent[] recordComponents = recordType.getRecordComponents();
        Object[] randomValues = new Object[recordComponents.length];
        for (int i = 0; i < recordComponents.length; i++) {
            if (recordComponents[i].getType().isRecord()) {
                randomValues[i] = createRandomRecord(recordComponents[i].getType(), easyRandom);
            } else {
                randomValues[i] = easyRandom.nextObject(recordComponents[i].getType());
            }
        }
        // create a random instance with random values
        try {
            return getCanonicalConstructor(recordType).newInstance(randomValues);
        } catch (Exception e) {
            throw new ObjectCreationException("Unable to create a random instance of recordType " + recordType, e);

        }
    }

    public static <T> List<T> createMultipleRandomRecords(Class<T> recordType, EasyRandom easyRandom, int amount) {
        ArrayList<T> items = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            items.add(createRandomRecord(recordType, easyRandom));
        }
        return items;
    }

    private static <T> Constructor<T> getCanonicalConstructor(Class<T> recordType) {
        RecordComponent[] recordComponents = recordType.getRecordComponents();
        Class<?>[] componentTypes = new Class<?>[recordComponents.length];
        for (int i = 0; i < recordComponents.length; i++) {
            // recordComponents are ordered, see javadoc:
            // "The components are returned in the same order that they are declared in the record header"
            componentTypes[i] = recordComponents[i].getType();
        }
        try {
            return recordType.getDeclaredConstructor(componentTypes);
        } catch (NoSuchMethodException e) {
            // should not happen, from Record javadoc:
            // "A record class has the following mandated members: a public canonical constructor ,
            // whose descriptor is the same as the record descriptor;"
            throw new RuntimeException("Invalid record definition", e);
        }
    }
}
