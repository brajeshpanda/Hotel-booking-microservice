package com.propertyservice.Service;

import com.propertyservice.Payload.PaginationResponse;
import com.propertyservice.Redis.PropertyNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyNotificationService {

    private static final String KEY = "PENDING_PROPERTIES";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public void savePending(PropertyNotification notification) {
        redisTemplate.opsForHash().put(KEY, String.valueOf(notification.getPropertyId()), notification);
    }


    public void updateStatus(long propertyId, String status) {
        Object obj = redisTemplate.opsForHash().get(KEY, String.valueOf(propertyId));

        if (obj != null) {
            PropertyNotification pn = (PropertyNotification) obj;
            pn.setStatus(status);
            redisTemplate.opsForHash().put(KEY, String.valueOf(propertyId), pn);
        }
    }

    public List<PropertyNotification> getAllPending() {
        return redisTemplate.opsForHash()
                .values(KEY)
                .stream()
                .map(e -> (PropertyNotification) e)
                .toList();
    }


    public PaginationResponse<PropertyNotification> getPendingPaginated(
            int pageNo, int pageSize,
            String sortBy, String sortDir
    ) {

        List<PropertyNotification> list = getAllPending();

        list = list.stream()
                .sorted((a, b) -> {
                    Comparable v1 = getSortValue(a, sortBy);
                    Comparable v2 = getSortValue(b, sortBy);
                    return sortDir.equalsIgnoreCase("asc")
                            ? v1.compareTo(v2)
                            : v2.compareTo(v1);
                })
                .toList();

        int total = list.size();
        int start = pageNo * pageSize;
        int end = Math.min(start + pageSize, total);

        List<PropertyNotification> paginated =
                start < total ? list.subList(start, end) : List.of();

        return new PaginationResponse<>(paginated, pageNo, pageSize, total);
    }


    private Comparable<?> getSortValue(PropertyNotification n, String sortBy) {
        return switch (sortBy) {
            case "propertyName" -> n.getPropertyName();
            case "gstNumber" -> n.getGstNumber();
            case "status" -> n.getStatus();
            default -> n.getPropertyId();
        };
    }
}
