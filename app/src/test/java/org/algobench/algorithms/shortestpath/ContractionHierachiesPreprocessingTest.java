package org.algobench.algorithms.shortestpath;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.assertj.core.api.Assertions;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import net.jqwik.api.Example;
import net.jqwik.api.lifecycle.BeforeContainer;

public class ContractionHierachiesPreprocessingTest {

    static FileInputStream file;
    static EdgeWeightedGraph denmark;
    static HashMap<Long, Integer> denmarkHashes;
    static EdgeWeightedGraph degree;
    static HashMap<Long, Integer> degreeHashes;
    static EdgeWeightedGraph dist;
    static HashMap<Long, Integer> distHashes;
    static EdgeWeightedGraph sp;
    static HashMap<Long, Integer> spHashes;
    static org.algobench.algorithms.shortestpath.EdgeWeightedGraph testing;
    static HashMap<Long, Integer> testingHashes;
    static ContractionHierachiesPreprocessing ch;
    static int n;
    static int m;

    @BeforeContainer
    public static void init() throws IOException {
        file = new FileInputStream("src/test/resources/testing.graph");
        testingHashes = ParseGraph.getHashMap();
        testing = ParseGraph.parseInput(file);

        file = new FileInputStream("src/test/resources/testing.graph");
        BufferedReader reader = new BufferedReader(new InputStreamReader(file), 1 << 16);
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        n = Integer.parseInt(tokenizer.nextToken());
        m = Integer.parseInt(tokenizer.nextToken());
        ch = new ContractionHierachiesPreprocessing(testing);
    }

    @Example
    void graphHasCorrectNumberOfVertices_whenParsedFromFile() {
        Assertions.assertThat(testing.V()).isEqualTo(n);
    }

    @Example
    void graphHasCorrectNumberOfEdges_whenParsedFromFile() {
        Assertions.assertThat(testing.E()).isEqualTo(m);
    }

    @Example
    void shouldHaveShortcuts() {
        Assertions.assertThat(ch.getShortcuts().size()).isEqualTo(10);
    }

    @Example
    void rankIsCorrect() {
        // testing graph specific
        Assertions.assertThat(ch.calculateRank(0)).isEqualTo(2);
        Assertions.assertThat(ch.calculateRank(1)).isEqualTo(5);
        Assertions.assertThat(ch.calculateRank(2)).isEqualTo(3);
        Assertions.assertThat(ch.calculateRank(3)).isEqualTo(3);
        Assertions.assertThat(ch.calculateRank(4)).isEqualTo(3);
        Assertions.assertThat(ch.calculateRank(5)).isEqualTo(1);
        Assertions.assertThat(ch.calculateRank(6)).isEqualTo(1);

    }

}
