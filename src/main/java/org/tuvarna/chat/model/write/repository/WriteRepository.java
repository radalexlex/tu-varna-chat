package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;

import javax.sql.DataSource;
import java.util.List;

public interface WriteRepository<T, K> extends DataRepository<T, K> {

    DataSource getDataSource();

    @Save
    <S extends T> S save(S entity);

    @Save
    <S extends T> List<S> saveAll(List<S> entities);

    @Insert
    <S extends T> S insert(S entity);

    @Insert
    <S extends T> List<S> insertAll(List<S> entities);

    @Update
    <S extends T> S update(S entity);

    @Update
    <S extends T> List<S> updateAll(List<S> entities);

    @Delete
    void delete(T entity);

    @Delete
    void deleteAll(List<? extends T> entities);

}
