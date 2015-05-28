package com.jcryptosync.sync;

import java.io.Serializable;
import java.util.*;

public class VectorTimePair implements Serializable {

    private Map<String, Integer> modificationVector;
    private Map<String, Integer> synchronizationVector;
    private int lastTime;

    public VectorTimePair() {
        modificationVector = new HashMap<>();
        synchronizationVector = new HashMap<>();
        lastTime = 0;
    }

    public void increaseModification(String clientId) {
        if(modificationVector.containsKey(clientId)) {
            modificationVector.replace(clientId, nextTime());
        } else {
            modificationVector.put(clientId, nextTime());
        }
    }

    public void increaseSynchronization(String clientId) {
        if(synchronizationVector.containsKey(clientId)) {
            synchronizationVector.replace(clientId, nextTime());
        } else {
            synchronizationVector.put(clientId, nextTime());
        }
    }

    private int nextTime() {
        lastTime += 1;
        return lastTime;
    }

    public boolean isChange(VectorTimePair vector) {
        boolean result = true;

        Set<String> keys = vector.modificationVector.keySet();

        for(String key: keys) {
            if(synchronizationVector.containsKey(key)) {
                result = result && (vector.modificationVector.get(key) <= synchronizationVector.get(key));
            } else {
                result = false;
            }
        }

        return !result;
    }

    public boolean isConflict(VectorTimePair vector) {
        boolean result = true;

        Set<String> keys = modificationVector.keySet();

        for(String key: keys) {
            if(vector.synchronizationVector.containsKey(key)) {
                result = result && (modificationVector.get(key) <= vector.synchronizationVector.get(key));
            } else {
                result = false;
            }
        }

        return !result;
    }


    public Map<String, Integer> getModificationVector() {
        return modificationVector;
    }

    public void setModificationVector(Map<String, Integer> modificationVector) {
        this.modificationVector = modificationVector;
    }

    public Map<String, Integer> getSynchronizationVector() {
        return synchronizationVector;
    }

    public void setSynchronizationVector(Map<String, Integer> synchronizationVector) {
        this.synchronizationVector = synchronizationVector;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }
}
