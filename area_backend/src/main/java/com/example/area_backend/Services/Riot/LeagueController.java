package com.example.area_backend.Services.Riot;

import com.example.area_backend.Services.Riot.LeagueService.Account;
import com.example.area_backend.Services.Riot.LeagueService.LeagueEntry;
import com.example.area_backend.Services.Riot.LeagueService.MatchDetail;
import com.example.area_backend.Services.Riot.LeagueService.Summoner;
// import com.example.area_backend.Services.Riot.LeagueService.MatchDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/league")
public class LeagueController {

    private final LeagueService leagueService;

    @Autowired
    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/account/{GameName}/{TagLine}")
    public ResponseEntity<Account> getAccount(@PathVariable String GameName, @PathVariable String TagLine) throws Exception {
        return ResponseEntity.ok(leagueService.getAccount(GameName, TagLine));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/summoner/{summonerPuuid}")
    public ResponseEntity<Summoner> getSummoner(@PathVariable String summonerPuuid) throws Exception {
        return ResponseEntity.ok(leagueService.getSummoner(summonerPuuid));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/summoner/{summonerId}/league-entries")
    public ResponseEntity<List<LeagueEntry>> getLeagueEntries(@PathVariable String summonerId) throws Exception {
        return ResponseEntity.ok(leagueService.getLeagueEntries(summonerId));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/account/{puuid}/matches")
    public ResponseEntity<List<String>> getMatchList(@PathVariable String puuid) throws Exception {
        return ResponseEntity.ok(leagueService.getMatchList(puuid));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/match")
    public ResponseEntity<MatchDetail> getMatchDetails(@RequestParam String matchId) {
        try {
            MatchDetail matchDetail = leagueService.getMatchDetails(matchId);
            if (matchDetail != null) {
                return ResponseEntity.ok(matchDetail);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
