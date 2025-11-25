package com.propertyservice.Utils;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String role = request.getHeader("X-User-Role");
        String path = request.getRequestURI();

        if (role == null) {
            response.setStatus(401);
            return false;
        }

        // ------------ USER + ADMIN allowed ------------
        if (path.contains("/api/v1/property/add-property") ||
                path.contains("/api/v1/property/search-property") ||
                path.contains("/api/v1/property/property-id") ||
                path.contains("/api/v1/property/room-id") ||
                path.contains("/api/v1/property/room-available-room-id")) {

            return true; // both USER and ADMIN can access
        }

        // ------------ ADMIN only ------------
        if (path.contains("/api/v1/property/approve") ||
                path.contains("/api/v1/property/pending-properties") ||
                path.contains("/api/v1/location") ||
                path.contains("/api/v1/property/updateRoomCount")) {

            if (role.equals("ROLE_ADMIN")) {
                return true;
            }

            response.setStatus(403);
            return false;
        }

        return true;
    }
}

