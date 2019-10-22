package com.spandan.dextro.spandan.Util;

import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

public class PrefixTree<Type> {

    private int                 size;
    private PrefixNode          root;
    private ArrayList<Type>     result;
    private OnQueryResult<Type> onQueryResultDelegate;

    private class PrefixNode {

        private HashMap<Character, PrefixNode> prefixMap;
        private boolean isEndOfWord;
        private Type token;

        private PrefixNode() {
            prefixMap   = new HashMap<>();
            isEndOfWord = false;
        }
    }

    public PrefixTree(OnQueryResult<Type> delegate) {

        onQueryResultDelegate = delegate;
        root = new PrefixNode();
        size = 0;
    }

    public int size() { return size; }

    public void insert(String key,Type token) {

        key = key.toLowerCase();
        PrefixNode root = this.root;
        for(int i = 0; i < key.length(); i++) {
            char value = key.charAt(i);
            if ( i + 1 == key.length()) {
                // last letter of string
                if (root.prefixMap.isEmpty()) {
                    PrefixNode node  = new PrefixNode();
                    root.prefixMap.put(value, node);
                    node.isEndOfWord = true;
                    node.token       = token;
                }
                else {
                    if(root.prefixMap.containsKey(value)) {
                        PrefixNode node  = root.prefixMap.get(value);
                        node.isEndOfWord = true;
                        node.token       = token;
                    }
                    else {
                        PrefixNode node  = new PrefixNode();
                        root.prefixMap.put(value, node);
                        node.isEndOfWord = true;
                        node.token       = token;
                    }
                }
            }
            else {
                // not the last letter of string

                if (root.prefixMap.containsKey(value)) {
                    root = root.prefixMap.get(value);
                }
                else {
                    PrefixNode node = new PrefixNode();
                    root.prefixMap.put(value,node);
                    root = node;
                }
            }
        }
    }

    public boolean query(String key) {

        PrefixNode node;

        result = new ArrayList<>();
        node   = this.root;

        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (node.prefixMap.isEmpty()) {
                return false;
            }
            else {
                if (node.prefixMap.containsKey(ch)) {
                    node = node.prefixMap.get(ch);
                }
                else {
                    return false;
                }
            }
        }

        query(node);
        onQueryResultDelegate.onResult(result);
        return true;
    }

    public void query(PrefixNode node) {

        Set<Character> set;

        if (node.isEndOfWord) {
            if (node.prefixMap.isEmpty()) {
                result.add(node.token);
                return;
            } else {

                set = node.prefixMap.keySet();

                for (char ch : set) {
                    PrefixNode prefixNode = node.prefixMap.get(ch);
                    query(prefixNode);
                }
                return;
            }
        }

        set = node.prefixMap.keySet();

        for (char ch : set) {
            PrefixNode prefixNode = node.prefixMap.get(ch);
            query(prefixNode);
        }
    }

//    public void query(PrefixNode node) {
//
//        Set<Character> set;
//
//        if (node.isEndOfWord) {
//            if (node.prefixMap.isEmpty()) {
//                System.out.println();
//                return;
//            } else {
//
//                System.out.println();
//
//                set = node.prefixMap.keySet();
//
//                for (char ch : set) {
//                    System.out.print(ch);
//                    PrefixNode prefixNode = node.prefixMap.get(ch);
//                    query(prefixNode);
//                }
//                return;
//            }
//        }
//
//        set = node.prefixMap.keySet();
//
//        for (char ch : set) {
//            System.out.print(ch);
//            PrefixNode prefixNode = node.prefixMap.get(ch);
//            query(prefixNode);
//        }
//    }

//        Collection<PrefixNode> nodes = node.prefixMap.values();
//        for (PrefixNode prefixNode: nodes) {
//            if (prefixNode.isEndOfWord) {
//                if (prefixNode.prefixMap.isEmpty()) {
//                    System.out.println(prefixNode.prefixMap);
//                }
//                else {
//                    // show word
//                }
//            }
//            else {
//                //continue;
//            }
//        }

    public static void main(String... args) {

//        PrefixTree tree = new PrefixTree();
//
//        tree.insert("car", "car");
//        tree.insert("done", "done");
//        tree.insert("try", "try");
//        tree.insert("cat", "cat");
//        tree.insert("trie", "trie");
//        tree.insert("do", "do");
//
//        //tree.query(tree.root);
//
//        tree.query("tr");
    }
}