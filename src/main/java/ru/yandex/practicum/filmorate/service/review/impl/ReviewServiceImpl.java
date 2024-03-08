package ru.yandex.practicum.filmorate.service.review.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feed;

    @Override
    public Review createReview(Review review) throws NoDataFoundException {
        Review createdReview = reviewStorage.createReview(review);
        feed.saveEvent(createdReview.getUserId(),
                FeedEventType.REVIEW,
                FeedEventOperation.ADD,
                createdReview.getReviewId());
        return createdReview;
    }

    @Override
    public Review getReviewById(Integer reviewId) throws NoDataFoundException {
        return reviewStorage.getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) throws NoDataFoundException {
        Review updReview = reviewStorage.updateReview(review);
        feed.saveEvent(updReview.getUserId(),
                FeedEventType.REVIEW,
                FeedEventOperation.UPDATE,
                updReview.getReviewId());
        return updReview;
    }

    @Override
    public void deleteReview(Integer reviewId) throws NoDataFoundException {
        Review dltReview = getReviewById(reviewId);
        feed.saveEvent(dltReview.getUserId(),
                FeedEventType.REVIEW,
                FeedEventOperation.REMOVE,
                dltReview.getFilmId());
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
