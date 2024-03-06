package ru.yandex.practicum.filmorate.service.review.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;

    @Override
    public Review createReview(Review review) throws NoDataFoundException {
        Review interimReview = reviewStorage.createReview(review);
        return interimReview;
    }

    @Override
    public Review getReviewById(Integer reviewId) throws NoDataFoundException {
        return reviewStorage.getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) throws NoDataFoundException {
        return reviewStorage.updateReview(review);
    }

    @Override
    public void deleteReview(Integer reviewId) {
        reviewStorage.deleteReview(reviewId);
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Override
    public Review addFeedback(Integer reviewId, String feedback, Integer userId) throws NoDataFoundException {
        reviewStorage.addFeedback(reviewId, feedbackToBoolean(feedback), userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review removeFeedback(Integer reviewId, String feedback, Integer userId) throws NoDataFoundException {
        reviewStorage.removeFeedback(reviewId, feedbackToBoolean(feedback), userId);
        return getReviewById(reviewId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    private Boolean feedbackToBoolean(String feedback) {
        if (feedback.equals("like")) {
            return true;
        } else if (feedback.equals("dislike")) {
            return false;
        }
        return false;
    }
}
