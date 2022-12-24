package com.diozero.aoc.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.tinylog.Logger;

public class InputDownloader {
	public static void main(final String[] args) {
		final HttpClient http_client = HttpClient.newHttpClient();
		final String session_cookie = System.getProperty("cookie");
		if (session_cookie == null) {
			Logger.error("You must set the session cookie system property (-Dcookie=abc)");
			System.exit(1);
		}

		IntStream.rangeClosed(2015, 2022).boxed()
				.flatMap(year -> IntStream.rangeClosed(1, 25).mapToObj(day -> new int[] { year.intValue(), day }))
				.forEach(year_day -> download(year_day, http_client, session_cookie));
	}

	private static void download(final int[] yearAndDay, final HttpClient httpClient, final String sessionCookie) {
		final int year = yearAndDay[0];
		final int day = yearAndDay[1];

		final Path dest = Path.of(
				String.format("src/main/resources/input/%d/day%d.txt", Integer.valueOf(year), Integer.valueOf(day)));

		final String source = String.format("https://adventofcode.com/%d/day/%d/input", Integer.valueOf(year),
				Integer.valueOf(day));

		try {
			if (dest.toFile().exists()) {
				Logger.debug("Already downloaded input for {}-{}", Integer.valueOf(year), Integer.valueOf(day));
			} else {
				final HttpRequest request = HttpRequest.newBuilder().uri(new URI(source)).GET()
						.header("Cookie", "session=" + sessionCookie).build();
				dest.getParent().toFile().mkdirs();

				Logger.debug("Downloading input from {} and writing to {}", source, dest);
				final HttpResponse<Path> response = httpClient.send(request, BodyHandlers.ofFile(dest));
				if (response.statusCode() != 200) {
					Logger.error("Failed to download from {}: {}", source, Integer.valueOf(response.statusCode()));
					dest.toFile().delete();
				} else {
					dest.getParent().getParent().resolve(year + "_samples").toFile().mkdirs();

					final Path answers_file = dest.getParent().resolve("day" + day + "_answers.txt");
					if (!answers_file.toFile().exists()) {
						answers_file.toFile().createNewFile();
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e, "Error downloading from {}: {}", source, e);
		}
	}
}
