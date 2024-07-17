package com.onlinecommunity.domain;

public enum ScoreStatus {
    SCORE_LIKE, SCORE_DISLIKE;

    public static ScoreStatus changeScoreStatus(ScoreStatus scoreStatus, boolean isLike) {
        if(isLike) {
            if(scoreStatus == ScoreStatus.SCORE_LIKE) {
                throw new RuntimeException("이미 좋아요를 누른 상태입니다.");
            }
            return ScoreStatus.SCORE_LIKE;
        } else {
            if(scoreStatus == ScoreStatus.SCORE_DISLIKE) {
                throw new RuntimeException("이미 싫어요를 누른 상태입니다.");
            }
            return ScoreStatus.SCORE_DISLIKE;
        }
    }

    private static int valueForAddOrMinus = 1;

    public static int plusScore(int score) {
        return score + valueForAddOrMinus;
    }

    public static int minusScore(int score) {
        return score - valueForAddOrMinus;
    }
}
