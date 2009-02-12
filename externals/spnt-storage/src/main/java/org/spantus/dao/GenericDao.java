package org.spantus.dao;

import java.util.List;

import org.spantus.domain.Identifiable;

public interface GenericDao<T extends Identifiable> {
	public List<T> findAll();
	public T findById(Long id);
	public T store(T entity);
	public void delete(T entity);

}
