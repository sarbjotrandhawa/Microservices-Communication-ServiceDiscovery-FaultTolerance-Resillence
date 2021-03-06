package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.CatalogItem;
import com.example.demo.model.Movie;
import com.example.demo.model.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
public class MovieInfo {

	@Autowired
	RestTemplate restTemplate;

	 @HystrixCommand(
	            fallbackMethod = "getFallbackCatalogItem",
	            threadPoolKey = "movieInfoPool",
	            threadPoolProperties = {
	                    @HystrixProperty(name = "coreSize", value = "20"),
	                    @HystrixProperty(name = "maxQueueSize", value = "10")
	            })
	public CatalogItem getCatalogItem(Rating rating) {
		Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
		return new CatalogItem(movie.getMovieName(), movie.getMovieDescription(), rating.getRating() + "");
	}

	public CatalogItem getFallbackCatalogItem(Rating rating) {

		return new CatalogItem("No Movie", "", rating.getRating() + "");
	}

}
