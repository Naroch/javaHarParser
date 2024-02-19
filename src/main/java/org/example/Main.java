package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.example.dto.ResponseJsonDto;
import org.example.dto.har.Har;
import org.example.mapper.ReviewMapper;
import org.example.model.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String RATING_URL = "https://edge.allegro.pl/ratings-api/sellers/.*";

    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().setLenient().create();

        File[] files = FileLoader.loadFiles();
        for (File file : files) {
            System.out.println("Przetwarzanie pliku: " + file.getName());
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), StandardCharsets.UTF_8));
            Har har = gson.fromJson(reader, Har.class);
            reader.close();

            Pattern pattern = Pattern.compile("(^[\\w-]+)"); // match string till space or dot character appears
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find()) {
                String sellerName = matcher.group();
                List<String> filteredResponses = getFilteredResponse(har);
                for (String response: filteredResponses) {
                    ResponseJsonDto responseJsonDto = gson.fromJson(response, ResponseJsonDto.class);
                    List<Review> reviews = responseJsonDto.getContent().stream()
                            .map((reviewsDto) -> ReviewMapper.mapToEntity(reviewsDto, sellerName))
                            .toList();
                    DatabaseHandler.saveReviewsAndProducts(reviews); //save reviews added
                }
            } else {
                System.out.println("nie można znaleźć sprzedawcy po nazwie pliku, pomijanie pliku " + file.getName());
            }
        }
    }

    private static List<String> getFilteredResponse(Har har) {
        return har.getLog().getEntries().stream()
                .filter(harEntry -> harEntry.getRequest().getUrl().matches(RATING_URL))
                .filter(harEntry -> harEntry.getResponse().getStatus() == 200)
                .map(harEntry -> harEntry.getResponse().getContent().getText())
                .toList();
    }
}