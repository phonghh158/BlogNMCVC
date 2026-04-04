package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.repository.CollectionRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private TopicRepository topicRepository;

    // Top 3 collection nhiều bài viết nhất
    public List<Collection> getFeaturedCollections() {
        return collectionRepository.findTopCollectionsByTopicCount(
                TopicStatus.PUBLISHED,
                PageRequest.of(0, 3)
        );
    }

    // Top 6 topic nhiều LOVE nhất
    public List<Topic> getFeaturedTopics() {
        return topicRepository.findTopTopicsByReactionType(
                TopicStatus.PUBLISHED,
                ReactionType.LOVE,
                PageRequest.of(0, 6)
        );
    }
}