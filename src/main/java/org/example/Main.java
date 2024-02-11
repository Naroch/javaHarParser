package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.example.dto.EntriesJsonDto;
import org.example.dto.ReviewDto;
import org.example.dto.har.Har;
import org.example.mapper.ReviewMapper;
import org.example.model.Review;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    private static final String RATING_URL = "https://edge.allegro.pl/ratings-api/sellers/";

    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().setLenient().create();

        File[] files = FileLoader.loadFiles();
        for (File file : files) {
            System.out.println("Przetwarzanie pliku: " + file.getName());
            JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
            Har har = gson.fromJson(reader, Har.class);
            reader.close();

            List<String> filteredEntries = getFilteredEntries(har);
            for (String entry: filteredEntries) {
                EntriesJsonDto entriesJsonDto = gson.fromJson(entry, EntriesJsonDto.class);
                List<Review> reviews = entriesJsonDto.getContent().stream()
                        .map(ReviewMapper::mapToEntity)
                        .toList();

            }
        }
    }

    private static List<String> getFilteredEntries(Har har) {
        return har.getLog().getEntries().stream()
                .filter(harEntry -> harEntry.getRequest().getUrl().matches(RATING_URL))
                .filter(harEntry -> harEntry.getResponse().getStatus() == 200)
                .map(harEntry -> harEntry.getResponse().getContent().getText())
//                .map(entry -> entry.replace("\\", "")) unneeded with gson
                .toList();
    }
}