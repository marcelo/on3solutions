package br.com.on3solutions.repository;

import java.util.List;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.on3solutions.model.Doc;

public interface DocRepository extends MongoRepository<Doc, String> {
 
	
	List<Doc> findAllBy(TextCriteria criteria);
}
