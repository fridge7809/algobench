package org.algobench.algorithms.orthogonalvector;

import java.util.ArrayList;
import java.util.Scanner;

public class OrthogonalVector {

    public static final int M = 256;

    public static void readInput(ArrayList<ArrayList<Integer>> A, Scanner sc, int n) {
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> a = new ArrayList<Integer>();
	        String s = sc.nextLine();
            for (int j = 0; j < M; j++) {
                if (s.charAt(j) == '0') {
                    a.add(0);
                } else {
                    a.add(1);
                }
            }
            A.add(a);
        }
    }

    public static boolean is_orthogonal(ArrayList<Integer> u, ArrayList<Integer> v) {
        for (int i = 0; i < u.size(); i++) {
            if (u.get(i) == 1 && v.get(i) == 1) {
                return false;
            }
        }
        return true;
    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//
//        int n = Integer.parseInt(sc.nextLine());
//
//        ArrayList<ArrayList<Integer>> A = new ArrayList<ArrayList<Integer>>();
//
//        readInput(A, sc, n);
//
//        long t = System.nanoTime();
//
//        for (int i = 0; i < A.size(); i++) {
//            for (int j = i + 1; j < A.size(); j++) {
//                if (is_orthogonal(A.get(i), A.get(j))) {
//                    System.out.println("yes");
//                    System.exit(0);
//                }
//            }
//        }
//
//        System.out.println("Search took " + (System.nanoTime() - t) / 1e6 + " ms." );
//
//
//        System.out.println("no");
//    }
}
