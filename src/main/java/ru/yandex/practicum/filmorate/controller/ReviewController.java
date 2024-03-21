package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) throws NoDataFoundException {
        log.info("Request to add Review {}", review);
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) throws NoDataFoundException {
        log.info("Request to update review id = {}", review.getReviewId());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) throws NoDataFoundException {
        log.info("Request to delete review id = {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) throws NoDataFoundException {
        log.info("Get review with id = {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "10") @Positive Integer count,
            @RequestParam(required = false) Integer filmId) {
        log.info("Get {} reviews for film id = {}", count, filmId);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/{feedback}/{userId}")
    public Review addFeedback(@PathVariable Integer reviewId, @PathVariable String feedback,
            @PathVariable Integer userId) throws NoDataFoundException {
        log.info("Add feedback for review id = {}, from user id = {}", reviewId, userId);
        return reviewService.addFeedback(reviewId, feedback, userId);
    }

    @DeleteMapping("/{reviewId}/{feedback}/{userId}")
    public Review removeFeedback(@PathVariable Integer reviewId, @PathVariable String feedback,
            @PathVariable Integer userId) throws NoDataFoundException {
        log.info("Remove feedback for review id = {}, from user id = {}", reviewId, userId);
        return reviewService.removeFeedback(reviewId, feedback, userId);
    }

}
