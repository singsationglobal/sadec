package com.singsation.dto;

import com.singsation.model.Song;

public class SongDTO {
    private Long id;
    private String title;
    private String artist;
    private String url;
    private String video;
    
    // ✅ ADDED: This is the missing link
    private String coverImageUrl;
    
    public SongDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.url = song.getUrl();
        this.video = song.getVideo();
        
        // ✅ ADDED: Map the cover image from the Song entity
        this.coverImageUrl = song.getCoverImageUrl();
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getUrl() { return url; }
    public String getVideo() { return video; }
    
    // ✅ ADDED: Getter for the cover image
    public String getCoverImageUrl() { return coverImageUrl; }
}
