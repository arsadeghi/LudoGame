package edu.gmu.ludo.dao;

import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.List;

public interface AbstractDao<E, I extends Serializable> {

    E findById(I id);
    void saveOrUpdate(E e);
    void save(E e);
    void update(E e);
    void delete(E e);
    List<E> findByCriteria(Criterion criterion);
    List<E> findAll();
}
