package org.spantus.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spantus.dao.GenericDao;
import org.spantus.domain.Identifiable;
import org.springframework.transaction.annotation.Transactional;

public abstract class GenericJpaDao<T extends Identifiable> 
		implements GenericDao<T> {
	Log log = LogFactory.getLog(this.getClass());
	
	@PersistenceContext
	private EntityManager entityManager;

	protected Class<T> type;

	public GenericJpaDao() {
		@SuppressWarnings("unchecked")
		Class<T> aType = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		this.type = aType;

	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	
	public List<T> findAll() {
		javax.persistence.Query q = getEntityManager().createQuery("select e from " + type.getSimpleName() + " e");
		return q.getResultList();
	}

	
	
	@Transactional
	public void delete(T entity) {
		getEntityManager().remove(entity);
	}

	@Transactional(readOnly=true)
	public T findById(Long id) {
		return (T) getEntityManager().find(
				type, id);
	}


	
	@Transactional(readOnly=false)
	public T store(T entity) {
		if (entity.getId() == null) {
			try {
				getEntityManager().persist(entity);
			} catch (Exception ex) {
				log.error("Exception on inserting", ex);
				entity = null;
			}

		} else {
			entity = getEntityManager().merge(entity);
		}
		return entity;

	}
	public void setEntityManager(EntityManager entityManager)
	{
	    this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

}
