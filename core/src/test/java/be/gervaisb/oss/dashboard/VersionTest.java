package be.gervaisb.oss.dashboard;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

public class VersionTest {

    @Test @Ignore
    public void should_be_ordered_naturally() {
	Version[] versions = shuffle(new Version[]{
		new Version(1, 0, 1),
		new Version(1, 0, 0),
		new Version(1, 0, 12),
		new Version(1, 1, 1),
		new Version(1, 12, 0),
		new Version(11, 0, 0),
		new Version(12, 0, 0)});

	Arrays.sort(versions);

	assertEquals(versions[0].toString(), "1.0.0");
	assertEquals(versions[1].toString(), "1.0.1");
	assertEquals(versions[2].toString(), "1.0.12");
	assertEquals(versions[3].toString(), "1.1.1");
	assertEquals(versions[4].toString(), "1.12.0");
	assertEquals(versions[5].toString(), "11.0.0");
	assertEquals(versions[5].toString(), "12.0.0");
    }

    @Test
    public void snapshots_should_be_older_than_release() {
	Version[] versions = new Version[]{
		new Version(1, 0, 0, true),
		new Version(1, 0, 0)};

	Arrays.sort(versions);

	assertEquals(versions[0].toString(), "1.0.0-SNAPSHOT");
	assertEquals(versions[1].toString(), "1.0.0");
    }

    private static <T> T[] shuffle(final T[] array) {
	Random random = new Random();
	for (int i=(int)(Math.random()*10); i>-1; i--) {
	    int pos = random.nextInt(array.length-1);
	    T temp = array[pos];
	    array[pos] = array[pos+1];
	    array[pos+1] = temp;
	}
	return array;
    }

}
