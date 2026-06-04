package com.singsation.dto;

import com.singsation.model.Song;

public class SongDTO {
    private Long id;
    private String title;
    private String artist;
    private String url;
    private String video;
    
    public SongDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.url = song.getUrl();
        this.video = song.getVideo();
    }
    
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getUrl() { return url; }
    public String getVideo() { return video; }
}