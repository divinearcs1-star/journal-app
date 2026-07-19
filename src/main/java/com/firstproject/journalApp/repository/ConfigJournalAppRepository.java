package com.firstproject.journalApp.repository;

import com.firstproject.journalApp.entity.ConfigJournalAppEntity;
import com.firstproject.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalAppEntity, ObjectId> {

}
