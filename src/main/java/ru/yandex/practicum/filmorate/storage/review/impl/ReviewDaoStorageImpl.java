package ru.yandex.practicum.filmorate.storage.review.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Repository
@RequiredArgsConstructor
public class ReviewDaoStorageImpl implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAllReviews() {
        String sqlString = "SELECT * FROM review " +
                "ORDER BY useful DESC";
        return jdbcTemplate.query(sqlString, this::reviewMapper);
    }

    @Override
    public Review createReview(Review review) throws NoDataFoundException {
        String create = "INSERT INTO review (content, is_positive, film_id, user_id, useful) "
                + "SELECT ?, ?, ?, ?, ? FROM dual WHERE EXISTS (SELECT 1 FROM users WHERE user_id = ?) "
                + "AND EXISTS (SELECT 1 FROM films WHERE film_id = ?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(create, new String[] { "review_id" });
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getFilmId());
            stmt.setInt(4, review.getUserId());
            stmt.setInt(5, 0);
            stmt.setInt(6, review.getUserId());
            stmt.setInt(7, review.getFilmId());
            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new NoDataFoundException(
                    "Can't create review with userID = " + review.getUserId() + " and filmId = " + review.getFilmId());
        }
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return review;
    }

    @Override
    public Review getReviewById(Integer reviewId) throws NoDataFoundException {
        String sqlString = "SELECT * FROM review WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlString, this::reviewMapper, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoDataFoundException("Review with id = " + reviewId + " not found");
        }
    }

    @Override
    public Review updateReview(Review review) throws NoDataFoundException {
        String sqlString = "UPDATE review SET content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        int updateResult = jdbcTemplate.update(sqlString,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (updateResult == 0) {
            throw new NoDataFoundException("Review with id = " + review.getReviewId() + " not found");
        }
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Integer reviewId) {
        String sqlString = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sqlString, reviewId);
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId == null) {
            String sqlString = "SELECT * FROM review " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sqlString, this::reviewMapper, count);
        }

        String sqlString = "SELECT * FROM review " +
                "WHERE film_id = ?" +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlString, this::reviewMapper, filmId, count);
    }

    @Override
    public Review addFeedback(Integer reviewId, Boolean isFeedbackPositive, Integer userId)
            throws NoDataFoundException {
        String sqlString = "MERGE INTO " +
                "review_likes (review_id, user_id, is_useful) " +
                "VALUES (?, ? ,?)";
        jdbcTemplate.update(sqlString, reviewId, userId, isFeedbackPositive);
        updateUseful(reviewId, isFeedbackPositive);
        return getReviewById(reviewId);
    }

    @Override
    public Review removeFeedback(Integer reviewId, Boolean isFeedbackPositive, Integer userId)
            throws NoDataFoundException {
        String sqlString = "DELETE FROM review WHERE review_id = ? AND userId = ?";
        jdbcTemplate.update(sqlString, reviewId, userId);
        updateUseful(reviewId, !isFeedbackPositive);
        return getReviewById(reviewId);
    }

    private Review reviewMapper(ResultSet resultSet, int row) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }

    private void updateUseful(Integer reviewId, Boolean isFeedbackPositive) throws NoDataFoundException {
        String sqlString;
        if (isFeedbackPositive) {
            sqlString = "UPDATE review " +
                    "SET useful = useful + 1 " +
                    "WHERE review_id = ?";
        } else {
            sqlString = "UPDATE review " +
                    "SET useful = useful - 1 " +
                    "WHERE review_id = ?";
        }
        int updateResult = jdbcTemplate.update(sqlString, reviewId);
        if (updateResult == 0) {
            throw new NoDataFoundException("Review with id = " + reviewId + " not found");
        }
    }
}
