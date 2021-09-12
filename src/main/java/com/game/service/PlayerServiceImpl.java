package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.IncorrectValueException;
import com.game.exception.NotFoundException;
import com.game.repository.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;

@Service
public class PlayerServiceImpl implements PlayerService {

    private MyRepository myRepository;

    public PlayerServiceImpl(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    @Override
    public Page<Player> getAllPlayers(Specification<Player> specification, Pageable pageable) {
        return myRepository.findAll(specification, pageable);
    }

    @Override
    public Long playersCount(Specification<Player> specification) {
        return myRepository.count(specification);
    }

    @Override
    public Player getPlayer(Long id) {
        if (id <= 0){
            throw new IncorrectValueException("Player ID is invalid");
        }
        return myRepository.findById(id).orElseThrow(() -> (
                new NotFoundException("Player with id " + id + " not found")
        ));
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getName() == null || player.getName().isEmpty() || player.getName().length() > 12){
            throw new IncorrectValueException("Player name is invalid");
        }

        if (player.getTitle() == null || player.getTitle().isEmpty() || player.getTitle().length() > 30){
            throw new IncorrectValueException("Player title is invalid");
        }

        if (player.getExperience() > 10000000 || player.getExperience() < 0){
            throw new IncorrectValueException("Player experience is invalid");
        }

        if (player.getProfession() == null){
            throw new IncorrectValueException("Player profession is invalid");
        }

        if (player.getRace() == null){
            throw new IncorrectValueException("Player race is invalid");
        }

        if (player.isBanned() == null){
            player.setBanned(false);
        }
        checkBirthday(player.getBirthday());
        player.setLevel(calculateLevel(player.getExperience()));
        player.setUntilNextLevel(calculateUntilNextLevel(player.getExperience(), player.getLevel()));

        return myRepository.saveAndFlush(player);

    }

    @Override
    public Player deletePlayer(Long id) {
        Player deletePlayer = getPlayer(id);
        myRepository.delete(deletePlayer);
        return deletePlayer;
    }

    @Override
    public Player updatePlayer(Long id, Player oldPlayer) {
        Player newPlayer = getPlayer(id);

        if (oldPlayer.getName() != null){
            if (oldPlayer.getName() == null || oldPlayer.getName().isEmpty() || oldPlayer.getName().length() > 12){
                throw new IncorrectValueException("Player name is invalid");
            }
            newPlayer.setName(oldPlayer.getName());
        }

        if (oldPlayer.getTitle() != null){
            if (oldPlayer.getTitle() == null || oldPlayer.getTitle().isEmpty() || oldPlayer.getTitle().length() > 30){
                throw new IncorrectValueException("Player title is invalid");
            }
            newPlayer.setTitle(oldPlayer.getTitle());
        }

        if (oldPlayer.getRace() != null){
            newPlayer.setRace(oldPlayer.getRace());
        }

        if (oldPlayer.getProfession() != null){
            newPlayer.setProfession(oldPlayer.getProfession());
        }

        if (oldPlayer.getExperience() != null){
            if (oldPlayer.getExperience() > 10000000 || oldPlayer.getExperience() < 0){
                throw new IncorrectValueException("Player experience is invalid");
            }
            newPlayer.setExperience(oldPlayer.getExperience());
        }

        if (oldPlayer.getBirthday() != null){
            checkBirthday(oldPlayer.getBirthday());
            newPlayer.setBirthday(oldPlayer.getBirthday());
        }

        if (oldPlayer.isBanned() != null){
            newPlayer.setBanned(oldPlayer.isBanned());
        }

        newPlayer.setLevel(calculateLevel(newPlayer.getExperience()));
        newPlayer.setUntilNextLevel(calculateUntilNextLevel(newPlayer.getExperience(), newPlayer.getLevel()));

        return myRepository.save(newPlayer);
    }

    @Override
    public Specification<Player> nameFilter(String name) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return name == null ? null : criteriaBuilder.like(root.get("name"),"%" + name + "%");
            }
        };
    }

    @Override
    public Specification<Player> titleFilter(String title) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
            }
        };
    }

    @Override
    public Specification<Player> birthdayFilter(Long after, Long before) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (after == null && before == null) return null;

                if (before == null){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"),new Date(after));
                }

                if (after == null){
                    return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"),new Date(before));
                }

                return criteriaBuilder.between(root.get("birthday"), new Date(before), new Date(after));
            }
        };
    }

    @Override
    public Specification<Player> bannedFilter(Boolean banned) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (banned == null) return null;
                if (banned == false) return criteriaBuilder.isFalse(root.get("banned"));
                return criteriaBuilder.isTrue(root.get("banned"));
            }
        };
    }

    @Override
    public Specification<Player> raceFilter(Race race) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return race == null ? null : criteriaBuilder.equal(root.get("race"),race);
            }
        };
    }

    @Override
    public Specification<Player> professionFilter(Profession profession) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
            }
        };
    }

    @Override
    public Specification<Player> experienceFilter(Integer minExperience, Integer maxExperience) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (minExperience == null && maxExperience == null) return null;

                if (minExperience == null){
                    return criteriaBuilder.lessThanOrEqualTo(root.get("experience"),maxExperience);
                }

                if (maxExperience == null){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"),minExperience);
                }

                return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
            }
        };
    }

    @Override
    public Specification<Player> levelFilter(Integer minLevel, Integer maxLevel) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (maxLevel == null && maxLevel == null) return null;

                if (maxLevel == null && minLevel != null){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("level"),minLevel);
                }

                if (maxLevel != null && minLevel == null){
                    return criteriaBuilder.lessThanOrEqualTo(root.get("level"),maxLevel);
                }

                return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
            }
        };
    }

    @Override
    public Specification<Player> UntilNextLevelFilter(Integer minUntilNextLevel, Integer maxUntilNextLevel) {

        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (maxUntilNextLevel == null && minUntilNextLevel == null) return null;

                if (minUntilNextLevel == null){
                    return criteriaBuilder.lessThanOrEqualTo(root.get("untilNextLevel"),maxUntilNextLevel);
                }

                if (maxUntilNextLevel == null){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("untilNextLevel"),minUntilNextLevel);
                }

                return criteriaBuilder.between(root.get("untilNextLevel"), minUntilNextLevel, maxUntilNextLevel);
            }
        };
    }

    public Integer calculateLevel(Integer experience) {
        Integer resultLevel;

        resultLevel = (int) Math.sqrt(2500 + 200 * experience);
        resultLevel -= 50;
        resultLevel /= 100;

        return resultLevel;
    }

    private Integer calculateUntilNextLevel(Integer experience, Integer level) {
        return 50 * (level + 1) * (level + 2) - experience;
    }

    public void checkBirthday(Date birthday) {
        if(birthday == null)
            throw new IncorrectValueException("Player birthday is invalid");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday.getTime());
        if (calendar.get(Calendar.YEAR) < 2000L || calendar.get(Calendar.YEAR) > 3000L)
            throw new IncorrectValueException("Player birthday is invalid");
    }


}
