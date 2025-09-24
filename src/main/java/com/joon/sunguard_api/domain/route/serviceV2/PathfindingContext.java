package com.joon.sunguard_api.domain.route.serviceV2;

import lombok.Getter;

import java.util.*;

@Getter
public class PathfindingContext {

    private final int MAX_TRANSFER = 2;

    private final String startId;
    private final String endId;


    public List<String> allStartStopIds;
    public List<String> allEndStopIds;

    private final PriorityQueue<Node> openSet;
    private final Map<Node, Node> cameFrom;
    private final Map<Node, Double> gScore;
    private final Map<Node, Double> fScore;


    public PathfindingContext(String startId, String endId){
        this.startId = startId;
        this.endId = endId;

        this.allStartStopIds = new ArrayList<>();
        this.allEndStopIds = new ArrayList<>();

        this.openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFScore));
        this.cameFrom = new HashMap<>();
        this.gScore = new HashMap<>();
        this.fScore = new HashMap<>();

    }

}
