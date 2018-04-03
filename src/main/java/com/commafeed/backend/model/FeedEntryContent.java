package com.commafeed.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "FEEDENTRYCONTENTS")
@SuppressWarnings("serial")
@Getter
@Setter
public class FeedEntryContent extends AbstractModel {

	@Column(length = 2048)
	public String title;

	@Column(length = 40)
	private String titleHash;

	@Lob
	@Column(length = Integer.MAX_VALUE)
	@Type(type = "org.hibernate.type.StringClobType")
	public String content;

	@Column(length = 40)
	private String contentHash;

	@Column(name = "author", length = 128)
	private String author;

	@Column(length = 2048)
	private String enclosureUrl;

	@Column(length = 255)
	private String enclosureType;

	@Column(length = 4096)
	private String categories;

	@OneToMany(mappedBy = "content")
	private Set<FeedEntry> entries;

	public String getTitle() {
		return title;
	}

    public void setTitle(String title) {
        this.title = title;
    }

	public void setTitleHash(String titleHash) {
		this.titleHash = titleHash;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setEnclosureUrl(String enclosureUrl) {
		this.enclosureUrl = enclosureUrl;
	}

	public void setEnclosureType(String enclosureType) {
		this.enclosureType = enclosureType;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public void setEntries(Set<FeedEntry> entries) {
		this.entries = entries;
	}
}
