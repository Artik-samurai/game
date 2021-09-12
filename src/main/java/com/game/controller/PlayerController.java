package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerServiceImpl playerService;

    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    @ResponseBody
    public List<Player> getAllPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.getAllPlayers(
                Specification.where(playerService.nameFilter(name)
                        .and(playerService.titleFilter(title))
                        .and(playerService.raceFilter(race))
                        .and(playerService.professionFilter(profession))
                        .and(playerService.birthdayFilter(before,after))
                        .and(playerService.bannedFilter(banned))
                        .and(playerService.experienceFilter(minExperience, maxExperience))
                        .and(playerService.levelFilter(minLevel, maxLevel))
                ), pageable).getContent();
    }

    @GetMapping("/count")
    @ResponseBody
    public Long playersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel
    ){
        return playerService.playersCount(
                Specification.where(playerService.nameFilter(name)
                        .and(playerService.titleFilter(title))
                        .and(playerService.raceFilter(race))
                        .and(playerService.professionFilter(profession))
                        .and(playerService.birthdayFilter(after,before))
                        .and(playerService.bannedFilter(banned))
                        .and(playerService.experienceFilter(minExperience, maxExperience))
                        .and(playerService.levelFilter(minLevel, maxLevel))
                ));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity <Player> getPlayer(@PathVariable Long id){
        Player player = playerService.getPlayer(id);
        return new ResponseEntity(player, HttpStatus.OK);
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity <Player> createPlayer(@RequestBody Player player){
        Player createrPlayer = playerService.createPlayer(player);
        return new ResponseEntity<Player>(createrPlayer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id){
        Player player = playerService.deletePlayer(id);
        return new ResponseEntity<Player>(player, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    @ResponseBody
    public ResponseEntity <Player> updatePlayer(@PathVariable Long id, @RequestBody Player player){
        Player updatePlayer = playerService.updatePlayer(id, player);
        return new ResponseEntity<Player>(updatePlayer, HttpStatus.OK);
    }
}
