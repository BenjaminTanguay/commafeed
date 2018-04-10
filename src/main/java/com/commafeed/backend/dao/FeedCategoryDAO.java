package com.commafeed.backend.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;

import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.QFeedCategory;
import com.commafeed.backend.model.QUser;
import com.commafeed.backend.model.User;
import com.querydsl.core.types.Predicate;

@Singleton
public class FeedCategoryDAO extends GenericDAO<FeedCategory> {

	private QFeedCategory category = QFeedCategory.feedCategory;
	private HashMap<Long, FeedCategory> longTermHashMap;

	@Inject
	public FeedCategoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public List<FeedCategory> findAll() {
		return query().selectFrom(category).fetch();
	}
	
	public void forklift(User user){
		if(MigrationToggles.isForkLiftOn()){
			List<FeedCategory> categories = findAll(user);
			for(FeedCategory category : categories){
				saveOrUpdateToStorage(category);
			}
		}
	}
	
	public List<FeedCategory> findAll(User user) {
		return query().selectFrom(category).where(category.user.eq(user)).join(category.user, QUser.user).fetchJoin().fetch();
	}

	public FeedCategory findById(User user, Long id) {
		return query().selectFrom(category).where(category.user.eq(user), category.id.eq(id)).fetchOne();
	}

	public FeedCategory findByName(User user, String name, FeedCategory parent) {
		Predicate parentPredicate = null;
		if (parent == null) {
			parentPredicate = category.parent.isNull();
		} else {
			parentPredicate = category.parent.eq(parent);
		}
		return query().selectFrom(category).where(category.user.eq(user), category.name.eq(name), parentPredicate).fetchOne();
	}

	public List<FeedCategory> findByParent(User user, FeedCategory parent) {
		Predicate parentPredicate = null;
		if (parent == null) {
			parentPredicate = category.parent.isNull();
		} else {
			parentPredicate = category.parent.eq(parent);
		}
		return query().selectFrom(category).where(category.user.eq(user), parentPredicate).fetch();
	}

	public List<FeedCategory> findAllChildrenCategories(User user, FeedCategory parent) {
		return findAll(user).stream().filter(c -> isChild(c, parent)).collect(Collectors.toList());
	}

	private boolean isChild(FeedCategory child, FeedCategory parent) {
		if (parent == null) {
			return true;
		}
		boolean isChild = false;
		while (child != null) {
			if (Objects.equals(child.getId(), parent.getId())) {
				isChild = true;
				break;
			}
			child = child.getParent();
		}
		return isChild;
	}

	public int consistencyChecker(User user) {
		int inconsistencyCounter = 0;
		if (MigrationToggles.isConsistencyCheckerOn()) {
			List<FeedCategory> categories = findAll(user);
			for(FeedCategory category: categories) {
				if (!this.storage.isModelConsistent(category)) {
					++inconsistencyCounter;
				}
			}
		}
		return inconsistencyCounter;
	}

	public void setLongTermHashMap(HashMap<Long, FeedCategory> hashMap) {
		this.longTermHashMap = hashMap;
	}
}
