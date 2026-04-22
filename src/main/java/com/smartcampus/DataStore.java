package com.smartcampus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {

    private DataStore() {}

    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return readings.computeIfAbsent(
                sensorId,
                id -> Collections.synchronizedList(new ArrayList<>())
        );
    }
}