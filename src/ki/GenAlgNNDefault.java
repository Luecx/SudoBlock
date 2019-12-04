package ki;

import board.Board;
import core.tensor.Tensor;
import core.vector.Vector2d;
import genetic_algorithm.def.GeneticAlgorithm;
import genetic_algorithm.def.GeneticClient;
import neuralnetwork.builder.Builder;
import neuralnetwork.builder.Network;
import neuralnetwork.functions.ReLU;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.network.DenseNode;
import neuralnetwork.network.FlattenNode;
import neuralnetwork.network.InputNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenAlgNNDefault {

    public static class Client implements GeneticClient {

        private Network network;
        private Board board;
        private double score;

        public Client(Board board) {
            this.board = board;
            Builder builder = new Builder();
            builder.addNode("input1", new InputNode(1,board.getWidth(), board.getHeight()));
            builder.addNode("flatten", new FlattenNode(), "input1");
            builder.addNode("dense1", new DenseNode(50).setActivationFunction(new ReLU()), "flatten");
            builder.addNode("dense2", new DenseNode(50).setActivationFunction(new ReLU()), "dense1");
            //builder.addNode("dense3", new DenseNode(50).setActivationFunction(new ReLU()), "dense2");
            builder.addNode("dense4", new DenseNode(1).setActivationFunction(new Sigmoid()), "dense2");

            this.network = builder.build_network();

        }

        public void setNetwork(Network network) {
            this.network = network;
        }

        @Override
        public double getScore() {
            return score;
        }

        @Override
        public Network getNetwork() {
            return network;
        }


        public double rate(Board board) {
            Tensor in = new Tensor(1, board.getWidth(), board.getHeight());
            for (int i = 0; i < board.getWidth(); i++) {
                for (int n = 0; n < board.getHeight(); n++) {
                    if (board.getStones()[i][n]) {
                        in.set(1, 0, i, n);
                    }
                }
            }
            return network.calculate(in)[0].get(0, 0, 0);
        }

        public Vector2d getNextPosition(Board board) {
            Vector2d move = null;
            double rating = Double.NEGATIVE_INFINITY;
            for (Vector2d b : board.getPossiblePositions(board.getNextPiece())) {
                board.test(board.getNextPiece(), b);
                double rate = rate(board);
                if(rate >= rating){
                    move = b;
                    rating = rate;
                }
                board.untest();
            }
            return move;
        }

        public Board run() {
            Board b = new Board(board.getWidth(), board.getHeight(), board.getSubd_w(), board.getSubd_h());
            while (!b.gameOver()) {
                b.move(getNextPosition(b));
            }
            score = b.getScore();
            return b;
        }
    }

    GeneticAlgorithm algorithm = new GeneticAlgorithm();
    ArrayList<Client> clients = new ArrayList<>();

    public GenAlgNNDefault(Board board, int clients) {
        for (int i = 0; i < clients; i++) {
            this.clients.add(new Client(board));
        }
        this.algorithm = new GeneticAlgorithm();
        this.algorithm.AMOUNT_SURVIVORS = 10;
        this.algorithm.MUTATION_RATE = 0.06;
        this.algorithm.MUTATION_STENGTH = 0.07;
    }


    public void evolve(String outputNetwork) {

        double avg = 0;
        for(Client c:clients){
            c.run();
            avg += c.getScore();
        }

        //GeneticAlgorithm.printClients(clients);
        algorithm.evolve(clients);

        //GeneticAlgorithm.printClients(clients);

        double bestScore = 0;
        Client bestClient = null;
        for(Client c:clients){
            if(c.getScore() > bestScore){
                bestScore = c.getScore();
                bestClient = c;
            }
        }
        if(outputNetwork != null)
            bestClient.getNetwork().write(outputNetwork);

        System.out.println("avg: " + avg / clients.size() + "  best: " + bestScore);

    }


    public static void main(String[] args) {

        GenAlgNNDefault alg = new GenAlgNNDefault(new Board(6,6,2,2), 80);

        for(int i = 0; i < 4000; i++)
            alg.evolve("network8.net");



    }
}
