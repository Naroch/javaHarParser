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

    public static void main(String[] args) {
        Gson gson = createGson();
        for (File file : FileLoader.loadFiles()) {
            processFile(file, gson);
        }
    }

    private static Gson createGson() {
        return new GsonBuilder().setLenient().create();
    }

    private static void processFile(File file, Gson gson) {
        System.out.println("Przetwarzanie pliku: " + file.getName());
        Har har = readHar(file, gson);
        String sellerName = extractSellerName(file.getName());
        if (sellerName == null) {
            System.out.println("nie można znaleźć sprzedawcy po nazwie pliku, pomijanie pliku " + file.getName());
            return;
        }
        List<String> filteredResponses = getFilteredResponse(har);
        for (String response : filteredResponses) {
            ResponseJsonDto responseJsonDto = gson.fromJson(response, ResponseJsonDto.class);
            if (responseJsonDto != null) {
                List<Review> reviews = responseJsonDto.getContent().stream()
                        .map(reviewsDto -> ReviewMapper.mapToEntity(reviewsDto, sellerName))
                        .toList();
                DatabaseHandler.saveReviewsAndProducts(reviews);
            } else {
                System.out.println("row was empty");
            }
        }
    }

    private static Har readHar(File file, Gson gson) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), StandardCharsets.UTF_8))) {
            return gson.fromJson(reader, Har.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String extractSellerName(String fileName) {
        Pattern pattern = Pattern.compile("(^[\\w-]+)");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find() ? matcher.group() : null;
    }

    private static List<String> getFilteredResponse(Har har) {
        return har.getLog().getEntries().stream()
                .filter(harEntry -> harEntry.getRequest().getUrl().matches(RATING_URL))
                .filter(harEntry -> harEntry.getResponse().getStatus() == 200)
                .map(harEntry -> harEntry.getResponse().getContent().getText())
                .toList();
    }
}