package com.singsation.service;

import com.singsation.model.Song;
import com.singsation.repository.SongRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {
    
    @Autowired
    private SongRepository songRepository;
    
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
    
    // NEW: Get only active songs for karaoke app
    public List<Song> getVisibleSongs() {
        return songRepository.findByIsActiveTrue();
    }
    
    public List<Song> searchSongs(String query) {
        return songRepository.searchSongs(query);
    }
    
    public Song getSongById(@NonNull Long id) {
        return songRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Song not found"));
    }
    
    @PostConstruct
    public void initializeSongs() {
        if (songRepository.count() == 0) {
            saveSong("One and Only", "Adele", 
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsAdele%20-%20One%20and%20Only.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosAdele%20-%20One%20and%20Only.mp4");
            
            saveSong("Summer Of 69", "Bryan Adams",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsBryan%20Adams%20-Summer%20Of%2069.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosBryan%20Adams%20-Summer%20Of%2069.mp4");
            
            saveSong("Have You Ever Seen The Rain", "CCR",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsCreedence%20Clearwater%20Revival%20-%20Have%20You%20Ever%20Seen%20The%20Rain.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosCreedence%20Clearwater%20Revival%20-%20Have%20You%20Ever%20Seen%20The%20Rain.mp4");
            
            saveSong("Wie Se Kind Is Jy", "Dr Victor & Theuns Jordaan",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsDr%20Victor%20%26%20Theuns%20Jordaan%20-%20Wie%20Se%20Kind%20Is%20Jy.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosDr%20Victor%20%26%20Theuns%20Jordaan%20-%20Wie%20Se%20Kind%20Is%20Jy.mp4");
            
            saveSong("Black Coffee", "Lacy J Dalton",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsLacy%20J%20Dalton%20-%20Black%20Coffee.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosLacy%20J%20Dalton%20-%20Black%20Coffee.mp4");
            
            saveSong("Simple Man", "Lynyrd Skynyrd",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsLynyrd%20Skynyrd%20-%20Simple%20Man.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosLynyrd%20Skynyrd%20-%20Simple%20Man.mp4");
            
            saveSong("Bitch", "Meredith Brooks",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsMeredith%20Brooks%20-%20Bitch.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosMeredith%20Brooks%20-%20Bitch.mp4");
            
            saveSong("Sweet Caroline", "Neil Diamond",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsNeil%20Diamond%20-%20Sweet%20Caroline.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosNeil%20Diamond%20-%20Sweet%20Caroline.mp4");
            
            saveSong("Shake It Off", "Taylor Swift",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsTaylor%20Swift%20-%20Shake%20It%20Off.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosTaylor%20Swift%20-%20Shake%20It%20Off.mp4");
            
            saveSong("Lose Control", "Teddy Swims",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsTeddy%20Swims%20-%20Lose%20Control.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosTeddy%20Swims%20-%20Lose%20Control.mp4");
            
            saveSong("House Of The Rising Sun", "The Animals",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsThe%20Animals%20-%20House%20Of%20The%20Rising%20Sun.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosThe%20Animals%20-%20House%20Of%20The%20Rising%20Sun.mp4");
            
            saveSong("Wondering Why", "The Red Clay Strays",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokesongs/o/karaokesongsThe%20Red%20Clay%20Strays%20-%20Wondering%20Why.mp3",
                "https://axcbefxpjvzm.objectstorage.af-johannesburg-1.oci.customer-oci.com/n/axcbefxpjvzm/b/karaokevideos/o/karaokevideosThe%20Red%20Clay%20Strays%20-%20Wondering%20Why.mp4");
        }
    }
    
    private void saveSong(String title, String artist, String url, String video) {
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setUrl(url);
        song.setVideo(video);
        song.setActive(true);
        songRepository.save(song);
    }
}