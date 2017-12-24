package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * User: VKoulakov
 * Date: 11.09.13
 * Time: 17:58
 */
public final class PositionRoles {
    private PositionRoles(){}
    public static String ROLE_ADMINISTRATOR = "admin";
    public static String ROLE_AUTHOR = "author";
    public static String ROLE_SUPER_AUTHOR = "super_author";
    public static String ROLE_AUDITOR = "auditor";
    public static String ROLE_INSPECTOR = "inspector";
    public static String ALL = "all";
    public static final List<String> TASK_APPLICABLE_ROLES = new ImmutableList.Builder<String>()
            .add(ROLE_ADMINISTRATOR)
            .add(ROLE_AUDITOR)
            .add(ROLE_AUTHOR)
            .build();
    //Роли допустимые в качестве автора требования
    public static final Set<String> AUTHORITY_AWARE_ROLES = ImmutableSet.<String>builder()
            .add(ROLE_ADMINISTRATOR)
            .add(ROLE_AUTHOR)
            .build();
    public static final Set<String> AUDIT_AWARE_ROLES = ImmutableSet.<String>builder()
            .add(ROLE_ADMINISTRATOR)
            .add(ROLE_AUDITOR)
            .build();

    public static final Predicate<String> IS_AUTHORITY_AWARE_PREDICATE = new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return AUTHORITY_AWARE_ROLES.contains(input);
                }
            };
    public static final Predicate<? super Position> IS_POSITION_HAS_ADMIN_PREDICATE = new Predicate<Position>() {
        @Override
        public boolean apply(@Nullable Position input) {
            return input != null && input.getRoles() != null && Iterables.any(input.getRoles(), IS_ADMIN_PREDICATE);
        }
    };

    public static final Predicate<? super Position> IS_POSITION_HAS_AUTHORITY_PREDICATE = new Predicate<Position>() {
        @Override
        public boolean apply(@Nullable Position input) {
            return input != null && input.getRoles() != null &&
                    Iterables.any(input.getRoles(), IS_AUTHORITY_AWARE_PREDICATE);
        }
    };

    public static final Predicate<String> IS_ADMIN_PREDICATE = new Predicate<String>() {
        @Override
        public boolean apply(@Nullable String input) {
            return ROLE_ADMINISTRATOR.equals(input);
        }
    };

    public static final Predicate<? super Position> IS_POSITION_HAS_AUTHOR_PREDICATE = new Predicate<Position>() {
        @Override
        public boolean apply(@Nullable Position input) {
            return input != null && input.getRoles() != null &&
                    Iterables.any(input.getRoles(), new Predicate<String>() {
                        @Override
                        public boolean apply(@Nullable String input) {
                            return ROLE_AUTHOR.equals(input);
                        }
                    });
        }
    };

}
