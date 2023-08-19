package com.project.chip.models;

import javax.persistence.*;

@Entity
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "solution_id")
    private Long id;
    private Long userId;
    private Long videoId;
    private String result;

    @Column(columnDefinition = "TEXT")
    private String answer;

    public Solution() {
    }

    public Solution(Long userId, Long videoId, String answer) {
        this.userId = userId;
        this.videoId = videoId;
        this.answer = answer;
    }
    public Solution(Long userId, Long videoId, String answer, String result) {
        this.userId = userId;
        this.videoId = videoId;
        this.answer = answer;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
