package com.cinereserve.catalog.security;

public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> email = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();

    public static void set(Long id, String mail, String r) {
        userId.set(id);
        email.set(mail);
        role.set(r);
    }

    public static Long getUserId() { return userId.get(); }
    public static String getEmail() { return email.get(); }
    public static String getRole() { return role.get(); }

    public static void clear() {
        userId.remove();
        email.remove();
        role.remove();
    }
}
