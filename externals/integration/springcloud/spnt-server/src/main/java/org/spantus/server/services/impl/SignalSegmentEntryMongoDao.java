package org.spantus.server.services.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.SignalSegmentEntryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class SignalSegmentEntryMongoDao implements SignalSegmentEntryDao {
	private static Logger LOG = LoggerFactory
			.getLogger(SignalSegmentEntryMongoDao.class);
	@Autowired
	MongoOperations mongoOperations;

	// @Autowired
	// MongoDbFactory mongoDbFactory;

	@Override
	public void updateFirstRecognizable(String id, boolean recognizable) {
		LOG.debug("[updateFirstRecognizeble] [" + id + "]; recognizable="+recognizable);
		mongoOperations.updateFirst(query(where("_id").is(new ObjectId(id))), update("recognizable", recognizable), SignalSegmentEntry.class);    
	}
	
	//
	// @Override
	// public SignalSegmentEntry findById(String id) {
	// Query query = new BasicQuery(String.format(
	// "{'_id' : { '$oid' : '%s' }}", id));
	// SignalSegmentEntry entry = mongoOperations.findOne(query,
	// SignalSegmentEntry.class);
	// return entry;
	// }

}
