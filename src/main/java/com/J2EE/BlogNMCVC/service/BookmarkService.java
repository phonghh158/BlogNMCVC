package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.response.BookmarkResponse;
import com.J2EE.BlogNMCVC.mapper.BookmarkMapper;
import com.J2EE.BlogNMCVC.model.Bookmark;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.BookmarkRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private BookmarkMapper bookmarkMapper;

    // CREATE
    public BookmarkResponse addBookmark(UUID topicId) {
        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("Topic Not Found"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .topic(topic)
                .build();

        bookmarkRepository.save(bookmark);

        return bookmarkMapper.toResponse(bookmark);
    }

    // DELETE
    public void deleteBookmark(UUID id) {
        bookmarkRepository.deleteById(id);
    }

    public BookmarkResponse findBookmarkByTopicAndMe(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            return null;
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        Bookmark bookmark = bookmarkRepository.findByTopicAndUser(topic, user);

        if (bookmark == null) {
            return null;
        }

        return bookmarkMapper.toResponse(bookmark);
    }


    public Page<BookmarkResponse> findAllByUser(int page, int size) {
        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return bookmarkRepository.findAllByUser(user, pageable).map(bookmarkMapper::toResponse);
    }

    public Page<BookmarkResponse> findAllByUserForAdmin(UUID userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return bookmarkRepository.findAllByUser(user, pageable).map(bookmarkMapper::toResponse);
    }

    public BookmarkResponse findBookmarkById(UUID id) {
        return bookmarkMapper.toResponse(bookmarkRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bookmark not found")));
    }

    public boolean existsBookmarkByTopicAndMe(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find topic with id: " + id));

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        return bookmarkRepository.existsBookmarkByTopicAndUser(topic, user);
    }
}
