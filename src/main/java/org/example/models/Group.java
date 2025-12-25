package org.example.models;

import java.util.Date;
import java.util.List;

public class Group {
    private final String groupId;
    private final String name;
    private final List<User> members;
    private final Date createdAt;

    public Group(String groupId, String name, List<User> members, Date createdAt) {
        this.groupId = groupId;
        this.name = name;
        this.members = members;
        this.createdAt = createdAt;
    }
}
