package ru.yandex.practicum.filmorate.service.review;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewService {
    List<Review> getAllReviews();

    Review createReview(Review review) throws NoDataFoundException;

    Review getReviewById(Integer reviewId) throws NoDataFoundException;

    Review updateReview(Review review) throws NoDataFoundException;

    void deleteReview(Integer reviewId);

    List<Review> getReviews(Integer filmId, Integer count);

    Review addFeedback(Integer reviewId, String feedback, Integer userId) throws NoDataFoundException;

    Review removeFeedback(Integer reviewId, String feedback, Integer userId) throws NoDataFoundException;
}
