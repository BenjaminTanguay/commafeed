package com.commafeed.backend.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FEEDS")
@SuppressWarnings("serial")
@Getter
@Setter
public class Feed extends AbstractModel {

	/**
	 * The url of the feed
	 */
	@Column(length = 2048, nullable = false)
	private String url;

	/**
	 * cache the url after potential http 30x redirects
	 */
	@Column(name = "url_after_redirect", length = 2048, nullable = false)
	private String urlAfterRedirect;

	@Column(length = 2048, nullable = false)
	private String normalizedUrl;

	@Column(length = 40, nullable = false)
	private String normalizedUrlHash;

	/**
	 * The url of the website, extracted from the feed
	 */
	@Column(length = 2048)
	private String link;

	/**
	 * Last time we tried to fetch the feed
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdated;

	/**
	 * Last publishedDate value in the feed
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastPublishedDate;

	/**
	 * date of the last entry of the feed
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastEntryDate;

	/**
	 * error message while retrieving the feed
	 */
	@Column(length = 1024)
	private String message;

	/**
	 * times we failed to retrieve the feed
	 */
	private int errorCount;

	/**
	 * feed refresh is disabled until this date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date disabledUntil;

	/**
	 * http header returned by the feed
	 */
	@Column(length = 64)
	private String lastModifiedHeader;

	/**
	 * http header returned by the feed
	 */
	@Column(length = 255)
	private String etagHeader;

	/**
	 * average time between entries in the feed
	 */
	private Long averageEntryInterval;

	/**
	 * last hash of the content of the feed xml
	 */
	@Column(length = 40)
	private String lastContentHash;

	/**
	 * detected hub for pubsubhubbub
	 */
	@Column(length = 2048)
	private String pushHub;

	/**
	 * detected topic for pubsubhubbub
	 */
	@Column(length = 2048)
	private String pushTopic;

	@Column(name = "push_topic_hash", length = 2048)
	private String pushTopicHash;

	/**
	 * last time we subscribed for that topic on that hub
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date pushLastPing;

	@Override
	public String toString() {
		return "Feed{" +
				"url='" + url + '\'' +
				", urlAfterRedirect='" + urlAfterRedirect + '\'' +
				", normalizedUrl='" + normalizedUrl + '\'' +
				", normalizedUrlHash='" + normalizedUrlHash + '\'' +
				", link='" + link + '\'' +
				", lastUpdated=" + lastUpdated +
				", lastPublishedDate=" + lastPublishedDate +
				", lastEntryDate=" + lastEntryDate +
				", message='" + message + '\'' +
				", errorCount=" + errorCount +
				", disabledUntil=" + disabledUntil +
				", lastModifiedHeader='" + lastModifiedHeader + '\'' +
				", etagHeader='" + etagHeader + '\'' +
				", averageEntryInterval=" + averageEntryInterval +
				", lastContentHash='" + lastContentHash + '\'' +
				", pushHub='" + pushHub + '\'' +
				", pushTopic='" + pushTopic + '\'' +
				", pushTopicHash='" + pushTopicHash + '\'' +
				", pushLastPing=" + pushLastPing +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Feed feed = (Feed) o;
		return errorCount == feed.errorCount &&
				Objects.equals(url, feed.url) &&
				Objects.equals(urlAfterRedirect, feed.urlAfterRedirect) &&
				Objects.equals(normalizedUrl, feed.normalizedUrl) &&
				Objects.equals(normalizedUrlHash, feed.normalizedUrlHash) &&
				Objects.equals(link, feed.link) &&
				Objects.equals(lastUpdated, feed.lastUpdated) &&
				Objects.equals(lastPublishedDate, feed.lastPublishedDate) &&
				Objects.equals(lastEntryDate, feed.lastEntryDate) &&
				Objects.equals(message, feed.message) &&
				Objects.equals(disabledUntil, feed.disabledUntil) &&
				Objects.equals(lastModifiedHeader, feed.lastModifiedHeader) &&
				Objects.equals(etagHeader, feed.etagHeader) &&
				Objects.equals(averageEntryInterval, feed.averageEntryInterval) &&
				Objects.equals(lastContentHash, feed.lastContentHash) &&
				Objects.equals(pushHub, feed.pushHub) &&
				Objects.equals(pushTopic, feed.pushTopic) &&
				Objects.equals(pushTopicHash, feed.pushTopicHash) &&
				Objects.equals(pushLastPing, feed.pushLastPing);
	}

	@Override
	public int hashCode() {

		return Objects.hash(url, urlAfterRedirect, normalizedUrl, normalizedUrlHash, link, lastUpdated, lastPublishedDate, lastEntryDate, message, errorCount, disabledUntil, lastModifiedHeader, etagHeader, averageEntryInterval, lastContentHash, pushHub, pushTopic, pushTopicHash, pushLastPing);
	}

    public void setUrl(String url) {
        this.url = url;
    }

	public void setUrlAfterRedirect(String urlAfterRedirect) {
		this.urlAfterRedirect = urlAfterRedirect;
	}

	public void setNormalizedUrl(String normalizedUrl) {
		this.normalizedUrl = normalizedUrl;
	}

	public void setNormalizedUrlHash(String normalizedUrlHash) {
		this.normalizedUrlHash = normalizedUrlHash;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setLastPublishedDate(Date lastPublishedDate) {
		this.lastPublishedDate = lastPublishedDate;
	}

	public void setLastEntryDate(Date lastEntryDate) {
		this.lastEntryDate = lastEntryDate;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public void setDisabledUntil(Date disabledUntil) {
		this.disabledUntil = disabledUntil;
	}

	public void setLastModifiedHeader(String lastModifiedHeader) {
		this.lastModifiedHeader = lastModifiedHeader;
	}

	public void setEtagHeader(String etagHeader) {
		this.etagHeader = etagHeader;
	}

	public void setAverageEntryInterval(long averageEntryInterval) {
		this.averageEntryInterval = averageEntryInterval;
	}

	public void setLastContentHash(String lastContentHash) {
		this.lastContentHash = lastContentHash;
	}

	public void setPushHub(String pushHub) {
		this.pushHub = pushHub;
	}

	public void setPushTopic(String pushTopic) {
		this.pushTopic = pushTopic;
	}

	public void setPushTopicHash(String pushTopicHash) {
		this.pushTopicHash = pushTopicHash;
	}

	public void setPushLastPing(Date pushLastPing) {
		this.pushLastPing = pushLastPing;
	}
}
