package com.lawyer.elguennouni_dev.embedding;

import lombok.Data;

import java.util.List;

@Data
public class Law {
    public int id;
    public String text;
    public List<Double> embedding;

    public Law() {}
    public Law(int id, String text, List<Double> embedding) {
        this.id = id;
        this.text = text;
        this.embedding = embedding;
    }


    @Override
    public String toString() {
        return "Law Number : " + id + "\n" +
                "Content: " + text;
    }
}
