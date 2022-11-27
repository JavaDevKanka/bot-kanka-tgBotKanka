package io.proj3ct.kankaBot.dao.abstracts;

import io.proj3ct.kankaBot.models.Ads;
import org.springframework.data.repository.CrudRepository;

public interface AdsRepository extends CrudRepository<Ads, Long> {
}
