package com.game.service;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.text.html.HTMLDocument;
import java.util.Date;

public interface PlayerService {
    Page<Player> getAllPlayers(Specification <Player> specification, Pageable pageable);
    Long playersCount(Specification <Player> specification);
    Player getPlayer(Long id);
    Player createPlayer(Player player);
    Player deletePlayer(Long id);
    Player updatePlayer(Long id, Player oldPlayer);

    Specification <Player> nameFilter (String name);
    Specification <Player> titleFilter (String title);
    Specification <Player> birthdayFilter (Long after, Long before);
    Specification <Player> bannedFilter (Boolean banned);
    Specification <Player> raceFilter (Race race);
    Specification <Player> professionFilter (Profession profession);
    Specification <Player> experienceFilter (Integer minExperience, Integer maxExperience);
    Specification <Player> levelFilter (Integer minLevel, Integer maxLevel);
    Specification <Player> UntilNextLevelFilter (Integer min, Integer max);
}
