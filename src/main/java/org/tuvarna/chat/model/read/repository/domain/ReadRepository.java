package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;

import javax.sql.DataSource;
import java.util.Optional;

public interface ReadRepository<T, K> extends DataRepository<T, K> {

    DataSource getDataSource();

    @Find
    Optional<T> findById(@By("id(this)") K id);

}
