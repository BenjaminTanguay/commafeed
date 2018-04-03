package com.commafeed.backend.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FEEDSUBSCRIPTIONS")
@SuppressWarnings("serial")
@Getter
@Setter
public class FeedSubscription extends AbstractModel {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Feed feed;

	@Column(length = 128, nullable = false)
	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	private FeedCategory category;

	@OneToMany(mappedBy = "subscription", cascade = CascadeType.REMOVE)
	private Set<FeedEntryStatus> statuses;

	private Integer position;

	@Column(length = 4096)
	private String filter;

    public Feed getFeed() {
        return feed;
    }

	public Long getId() {
		return id;
	}

    public void setUser(User user) {
        this.user = user;
    }

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCategory(FeedCategory category) {
		this.category = category;
	}

	public void setStatuses(Set<FeedEntryStatus> statuses) {
		this.statuses = statuses;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
