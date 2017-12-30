package com.lztimer.server.util;

import com.lztimer.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Service
public class DbTestUtil {
    @Autowired
    private List<CrudRepository> repositories;

    @Autowired
    private UserRepository userRepository;

    private void deleteAllRepositories() {
        userRepository.deleteAll();
        for (CrudRepository repo : repositories) {
            repo.deleteAll();
        }
    }

    public void resetDb() {
        deleteAllRepositories();
    }
}
