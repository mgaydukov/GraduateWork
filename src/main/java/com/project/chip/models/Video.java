package com.project.chip.models;

import com.project.chip.repos.VideoRepo;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JoinColumn(name = "video_id")
    private Long id;
    private String name;
    @Column(unique=true)
    private String link;
    @Column(unique=true)
    private String youtubeId;
    private String subject;
    @Column(columnDefinition = "character varying(1024)")
    private String description;
    private String datePublication;
    private String cover;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User publisher;

    @Column(columnDefinition = "TEXT")
    private String fullText;

    public Video(){
    }

    public Video(String name, String link, User publisher, String subject, String description) {
        this.name = name;
        this.link = link;
        this.publisher = publisher;
        this.subject = subject;
        this.description = description;
        datePublication = String.valueOf(LocalDate.now());
        youtubeId = extractYoutubeId();
        cover = "https://i.ytimg.com/vi/"+ youtubeId +"/hq720.jpg";
    }

    public String extractYoutubeId(){
        String text = link;
        Pattern pattern = Pattern.compile("((?=)\\w{11}).*?");
        Matcher matcher = pattern.matcher(text);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(text.substring(matcher.start(), matcher.end()));
            if(list.get(0) != null)
                break;
        }
        return youtubeId = list.get(0);
    }

    public String getPublisherName(){
        return publisher !=null ? publisher.getUsername() : "<none>";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public User getPublisher() { return publisher; }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        this.datePublication = datePublication;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public boolean isPublisher(User user){
        if (publisher.getId() == user.getId())
            return true;
        return false;
    }
}
