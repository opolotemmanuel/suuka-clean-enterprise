package com.suuka.cleaning.messages.dto;

import com.suuka.cleaning.messages.entity.Conversation;
import com.suuka.cleaning.messages.entity.Message;

import java.util.List;

public record ConversationDetail(Conversation conversation, List<Message> messages) {
}
