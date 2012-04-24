package org.spantus.server.servlet.repository;

import org.bson.types.ObjectId;
import org.spantus.server.dto.CorporaEntry;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface CorporaEntryRepository extends CrudRepository<CorporaEntry, ObjectId>, QueryDslPredicateExecutor<CorporaEntry> {


}
