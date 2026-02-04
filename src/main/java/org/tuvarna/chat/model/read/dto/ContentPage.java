package org.tuvarna.chat.model.read.dto;

import java.util.List;

public record ContentPage<T>(List<T> content, boolean hasFollowing) {
}
