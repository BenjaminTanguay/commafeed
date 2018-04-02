package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.backend.model.FeedEntry;

public interface StorageFeedEntryDAO {

    FeedEntry feedEntry(int key, FeedEntry feedEntry);
}
