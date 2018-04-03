package com.commafeed.backend.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Expression;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FEEDENTRIES")
@SuppressWarnings("serial")
@Getter
@Setter
public class FeedEntry extends AbstractModel {

	@Column(length = 2048, nullable = false)
	private String guid;

	@Column(length = 40, nullable = false)
	private String guidHash;

	@ManyToOne(fetch = FetchType.LAZY)
	public Feed feed;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false, updatable = false)
	public FeedEntryContent content;

	@Column(length = 2048)
	private String url;

	@Temporal(TemporalType.TIMESTAMP)
	public Date inserted;

	@Temporal(TemporalType.TIMESTAMP)
	public Date updated;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.REMOVE)
	public Set<FeedEntryStatus> statuses;

	@OneToMany(mappedBy = "entry", cascade = CascadeType.REMOVE)
	public Set<FeedEntryTag> tags;
    public Query feed;

    public FeedEntryContent getContent() {
        return content;
    }

	public Date getUpdated() {
		return updated;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public void setContent(FeedEntryContent content) {
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public Expression<?> count() {
		return null;
	}
}
