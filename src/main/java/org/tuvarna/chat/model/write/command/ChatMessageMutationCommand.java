package org.tuvarna.chat.model.write.command;

public sealed interface ChatMessageMutationCommand permits
        ChatMessageMutationCommand.ArchiveMessageMutation,
        ChatMessageMutationCommand.UpdateMessageMutation {

    record ArchiveMessageMutation(long messageId) implements ChatMessageMutationCommand {
    }

    record UpdateMessageMutation(long messageId, String updatedContent) implements ChatMessageMutationCommand {
    }

}
