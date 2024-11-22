package com.example.area_backend.Services.osu;

import com.example.area_backend.Services.osu.OsuService.User;
import com.example.area_backend.Services.osu.OsuService.Beatmapset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/osu")
public class OsuController {

    private final OsuService osuService;

    @Autowired
    public OsuController(OsuService osuService) {
        this.osuService = osuService;
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping(value = {"/user/{userId}", "/user/{userId}/"})
    public User getUser(@PathVariable String userId) throws Exception {
        return osuService.getUserData(userId);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping(value = {"/user/{userId}/beatmaps", "/user/{userId}/beatmaps/"})
    public ResponseEntity<List<Beatmapset>> getUserBeatmaps(
            @PathVariable long userId,
            @RequestParam(value = "type", defaultValue = "most_played") String type,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "1") int offset) throws Exception {

        List<Beatmapset> beatmaps = osuService.getUserBeatmaps(userId, type, limit, offset);
        return new ResponseEntity<>(beatmaps, HttpStatus.OK);
    }
}
