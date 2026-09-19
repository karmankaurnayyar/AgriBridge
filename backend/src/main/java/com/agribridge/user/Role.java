package com.agribridge.user;

/**
 * Platform roles. FPO/Coordinator is planned for a later phase (see
 * docs/roadmap.md); the Week 2 foundation supports the three roles below,
 * which are sufficient for the Farm Management prototype and for exercising
 * role-based access control end-to-end.
 */
public enum Role {
    FARMER,
    BUYER,
    ADMIN
}
