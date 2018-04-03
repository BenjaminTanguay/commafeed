package com.commafeed.backend.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FEEDENTRYSTATUSES")
@SuppressWarnings("serial")
@Getter
@Setter
public class FeedEntryStatus extends AbstractModel {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public FeedSubscription subscription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public FeedEntry entry;

	@Column(name = "read_status")
	public boolean read;
	public boolean starred;

	@Transient
	private boolean markable;

	@Transient
	private List<FeedEntryTag> tags = new ArrayList<>();

	/**
	 * Denormalization starts here
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	public Date entryInserted;

	@Temporal(TemporalType.TIMESTAMP)
	public Date entryUpdated;
	public Query subscription;

	public FeedEntryStatus() {

	}

	public FeedEntryStatus(User user, FeedSubscription subscription, FeedEntry entry) {
		setUser(user);
		setSubscription(subscription);
		setEntry(entry);
		setEntryInserted(entry.getInserted());
		setEntryUpdated(entry.getUpdated());
	}

	public Date getEntryUpdated() {
		return entryUpdated;
	}

	public Long getId() {
		return id;
	}

	public FeedEntry getEntry() {
		return entry;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setMarkable(boolean markable) {
		this.markable = markable;
	}

    public void setTags(List<FeedEntryTag> tags) {
        this.tags = tags;
    }

	public FeedSubscription getSubscription() {
		return subscription;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setEntryUpdated(Date entryUpdated) {
		this.entryUpdated = entryUpdated;
	}

	public void setSubscription(FeedSubscription subscription) {
		this.subscription = subscription;
	}

	public void setEntry(FeedEntry entry) {
		this.entry = entry;
	}
}
