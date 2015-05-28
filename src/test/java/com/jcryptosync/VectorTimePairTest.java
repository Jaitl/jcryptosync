package com.jcryptosync;

import com.jcryptosync.sync.VectorTimePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class VectorTimePairTest {
    @Test
    public void addTest() {
        String clientA = "A";
        String clientB = "B";
        String clientC = "C";

        VectorTimePair vectorTimePair = new VectorTimePair();

        vectorTimePair.increaseModification(clientA);
        vectorTimePair.increaseModification(clientB);
        vectorTimePair.increaseModification(clientC);

        Map<String, Integer> vectorMod = vectorTimePair.getModificationVector();

        Assert.assertEquals(vectorMod.size(), 3);

        Assert.assertEquals((int) vectorMod.get(clientA), 1);
        Assert.assertEquals((int) vectorMod.get(clientB), 2);
        Assert.assertEquals((int) vectorMod.get(clientC), 3);

        vectorTimePair.increaseModification(clientA);

        vectorTimePair.increaseModification(clientC);

        Assert.assertEquals((int) vectorMod.get(clientA), 4);
        Assert.assertEquals((int) vectorMod.get(clientC), 5);

        vectorTimePair.increaseSynchronization(clientA);
        vectorTimePair.increaseSynchronization(clientB);

        Map<String, Integer> vectorSync = vectorTimePair.getSynchronizationVector();

        Assert.assertEquals((int) vectorSync.get(clientA), 6);
        Assert.assertEquals((int) vectorSync.get(clientB), 7);

        vectorTimePair.increaseSynchronization(clientA);
        Assert.assertEquals((int) vectorSync.get(clientA), 8);
    }

    @Test
    public void testCompare_1() {
        VectorTimePair fileA = new VectorTimePair();
        Map<String , Integer> mapModA = new HashMap<>();
        mapModA.put("A", 1);
        Map<String, Integer> mapSyncA = new HashMap<>();
        mapSyncA.put("A", 2);
        fileA.setModificationVector(mapModA);
        fileA.setSynchronizationVector(mapSyncA);

        VectorTimePair fileB = new VectorTimePair();
        Map<String, Integer> mapModB = new HashMap<>();
        mapModB.put("A", 1);
        mapModB.put("B", 3);
        Map<String, Integer> mapSyncB = new HashMap<>();
        mapSyncB.put("A", 1);
        mapSyncB.put("B", 2);
        fileB.setModificationVector(mapModB);
        fileB.setSynchronizationVector(mapSyncB);

        boolean isChange = fileA.isChange(fileB);

        Assert.assertEquals(isChange, true);

        boolean isConflict = fileA.isConflict(fileB);

        Assert.assertEquals(isConflict, false);
    }

    @Test
    public void testCompare_2() {
        VectorTimePair fileA = new VectorTimePair();
        Map<String , Integer> mapModA = new HashMap<>();
        mapModA.put("A", 1);
        Map<String, Integer> mapSyncA = new HashMap<>();
        mapSyncA.put("A", 2);
        fileA.setModificationVector(mapModA);
        fileA.setSynchronizationVector(mapSyncA);

        VectorTimePair fileB = new VectorTimePair();
        Map<String, Integer> mapModB = new HashMap<>();
        mapModB.put("A", 1);
        Map<String, Integer> mapSyncB = new HashMap<>();
        mapSyncB.put("A", 1);
        mapSyncB.put("B", 2);
        fileB.setModificationVector(mapModB);
        fileB.setSynchronizationVector(mapSyncB);

        boolean isChange = fileA.isChange(fileB);

        Assert.assertEquals(isChange, false);
    }

    @Test
    public void testCompare_3() {
        VectorTimePair fileA = new VectorTimePair();
        Map<String , Integer> mapModA = new HashMap<>();
        mapModA.put("A", 3);
        Map<String, Integer> mapSyncA = new HashMap<>();
        mapSyncA.put("A", 2);
        fileA.setModificationVector(mapModA);
        fileA.setSynchronizationVector(mapSyncA);

        VectorTimePair fileB = new VectorTimePair();
        Map<String, Integer> mapModB = new HashMap<>();
        mapModB.put("A", 1);
        mapModB.put("B", 3);
        Map<String, Integer> mapSyncB = new HashMap<>();
        mapSyncB.put("A", 1);
        mapSyncB.put("B", 2);
        fileB.setModificationVector(mapModB);
        fileB.setSynchronizationVector(mapSyncB);

        boolean isChange = fileA.isChange(fileB);

        Assert.assertEquals(isChange, true);

        boolean isNotConflict = fileA.isConflict(fileB);

        Assert.assertEquals(isNotConflict, true);
    }

    @Test
    public void testCompare_4() {
        VectorTimePair fileB = new VectorTimePair();
        Map<String , Integer> mapModB = new HashMap<>();
        mapModB.put("A", 1);
        mapModB.put("B", 3);
        Map<String, Integer> mapSyncB = new HashMap<>();
        mapSyncB.put("A", 2);
        mapSyncB.put("B", 4);
        fileB.setModificationVector(mapModB);
        fileB.setSynchronizationVector(mapSyncB);

        VectorTimePair fileC = new VectorTimePair();
        Map<String, Integer> mapModC = new HashMap<>();
        mapModC.put("A", 1);
        mapModC.put("B", 3);
        mapModC.put("C", 5);
        Map<String, Integer> mapSyncC = new HashMap<>();
        mapSyncC.put("A", 2);
        mapSyncC.put("B", 4);
        fileC.setModificationVector(mapModC);
        fileC.setSynchronizationVector(mapSyncC);

        boolean isChange = fileB.isChange(fileC);

        Assert.assertEquals(isChange, true);

        boolean isConflict = fileB.isConflict(fileC);

        Assert.assertEquals(isConflict, false);
    }
}
