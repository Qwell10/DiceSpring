package com.dice.service;

import com.dice.dto.GameState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//todo()
public class RoomsManager {

    private final Map<String, GameState> activeRooms = new ConcurrentHashMap<>();
}
