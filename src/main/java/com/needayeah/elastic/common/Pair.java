package com.needayeah.elastic.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pair<L,R> {

    private L left;
    private R right;

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair(left, right);
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
