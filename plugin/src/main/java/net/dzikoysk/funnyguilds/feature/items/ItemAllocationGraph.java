package net.dzikoysk.funnyguilds.feature.items;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Maximum flow from requirements through matching storage slots, bounded by each slot's stock. */
final class ItemAllocationGraph {

    private final Node source = new Node();
    private final Node sink = new Node();

    Edge addRequirement(int amount) {
        return connect(this.source, new Node(), amount);
    }

    Edge addStock(int amount) {
        return connect(new Node(), this.sink, amount);
    }

    /** Connect all matches before allocation starts; capacities capture the stock available at construction. */
    Edge connectMatch(Edge requirement, Edge stock) {
        return connect(requirement.to, stock.from, stock.remainingCapacity());
    }

    private static Edge connect(Node from, Node to, int capacity) {
        Edge forward = new Edge(from, to, capacity);
        Edge reverse = new Edge(to, from, 0);
        forward.reverse = reverse;
        reverse.reverse = forward;
        from.outgoing.add(forward);
        to.outgoing.add(reverse);
        return forward;
    }

    void allocateItems() {
        // Reverse edges let a path move earlier allocations to other slots when requirements overlap.
        // Repeating until no path remains finds a valid payment whenever one exists.
        while (true) {
            Optional<AllocationPath> path = findAllocationPath();
            if (path.isEmpty()) {
                return;
            }
            path.orElseThrow().allocate();
        }
    }

    private Optional<AllocationPath> findAllocationPath() {
        Map<Node, Edge> incomingEdges = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        ArrayDeque<Node> pending = new ArrayDeque<>();
        visited.add(this.source);
        pending.add(this.source);

        while (!pending.isEmpty()) {
            Node current = pending.remove();
            for (Edge edge : current.outgoing) {
                if (edge.remainingCapacity() > 0 && visited.add(edge.to)) {
                    incomingEdges.put(edge.to, edge);
                    if (edge.to == this.sink) {
                        return Optional.of(reconstructPath(incomingEdges));
                    }
                    pending.add(edge.to);
                }
            }
        }
        return Optional.empty();
    }

    private AllocationPath reconstructPath(Map<Node, Edge> incomingEdges) {
        List<Edge> edges = new ArrayList<>();
        Node current = this.sink;
        while (current != this.source) {
            Edge edge = incomingEdges.get(current);
            edges.add(edge);
            current = edge.from;
        }
        return new AllocationPath(List.copyOf(edges));
    }

    private static final class Node {

        private final List<Edge> outgoing = new ArrayList<>();

    }

    static final class Edge {

        private final Node from;
        private final Node to;
        private int remainingCapacity;
        private Edge reverse;

        private Edge(Node from, Node to, int capacity) {
            this.from = from;
            this.to = to;
            this.remainingCapacity = capacity;
        }

        int remainingCapacity() {
            return this.remainingCapacity;
        }

        int allocatedAmount() {
            return this.reverse.remainingCapacity;
        }

        private void allocate(int amount) {
            this.remainingCapacity -= amount;
            this.reverse.remainingCapacity += amount;
        }

    }

    private record AllocationPath(List<Edge> edges) {

        void allocate() {
            int amount = this.edges.stream().mapToInt(Edge::remainingCapacity).min().orElseThrow();
            this.edges.forEach(edge -> edge.allocate(amount));
        }

    }

}
