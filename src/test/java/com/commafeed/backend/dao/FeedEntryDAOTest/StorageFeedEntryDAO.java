package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.frontend.model.Entries;

public interface StorageFeedEntryDAO {

    Entries entry(int key, Entries entry);
}
