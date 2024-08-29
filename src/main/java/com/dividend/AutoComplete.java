package com.dividend;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;

@AllArgsConstructor
public class AutoComplete {

    private Trie trie;

    public void add(String s) {
        trie.put(s, "world");
    }

    public Object get(String s) {
        return this.trie.get(s);
    }
}
