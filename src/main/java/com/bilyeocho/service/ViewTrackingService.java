package com.bilyeocho.service;

import com.bilyeocho.domain.Item;
import com.bilyeocho.dto.response.ItemSearchResponse;
import com.bilyeocho.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewTrackingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ItemRepository itemRepository;
    private static final String ITEM_VIEW_PREFIX = "item:view:";


    public void incrementViewCount(Long itemId) {
        String key = ITEM_VIEW_PREFIX + itemId;
        redisTemplate.opsForValue().increment(key, 1);
    }


    public Long getViewCount(Long itemId) {
        String key = ITEM_VIEW_PREFIX + itemId;
        String viewCount = redisTemplate.opsForValue().get(key);
        return (viewCount == null) ? 0L : Long.parseLong(viewCount);
    }

    public List<ItemSearchResponse> getTop3ItemsByViews() {

        Set<String> keys = redisTemplate.keys(ITEM_VIEW_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }


        return keys.stream()
                .map(key -> {
                    try {
                        Long itemId = Long.parseLong(key.split(":")[2]);
                        Long viewCount = getViewCount(itemId);

                        Item item = itemRepository.findById(itemId).orElse(null);
                        if (item != null) {
                            ItemSearchResponse response = new ItemSearchResponse(item);
                            response.setViewCount(viewCount);
                            return response;
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Long.compare(b.getViewCount(), a.getViewCount()))
                .limit(3)
                .collect(Collectors.toList());
    }
}
