package com.remgagagali727.discord.survplanet.listener;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.repository.ItemRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.remgagagali727.discord.survplanet.listener.ReactionHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;


import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class ReactionHandler extends ListenerAdapter {

    @Autowired
    private ItemRepository itemRepository;

    private final Map<Long, PaginationData> paginationMessages = new HashMap<>();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        Long messageId = event.getMessageIdLong();
        if (paginationMessages.containsKey(messageId)) {
            PaginationData data = paginationMessages.get(messageId);

            String emoji = event.getEmoji().getName();
            if (emoji.equals("➡️") && data.currentPage < data.maxPages - 1) {
                // Next page
                data.currentPage++;
                updatePaginationMessage(event, data);
            } else if (emoji.equals("⬅️") && data.currentPage > 0) {
                // Previous page
                data.currentPage--;
                updatePaginationMessage(event, data);
            }

            // Remove user reaction so they can click again
            event.retrieveMessage().queue(message ->
                    message.removeReaction(event.getEmoji(), event.getUser()).queue());
        }
    }

    private void updatePaginationMessage(MessageReactionAddEvent event, PaginationData data) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📋 Items List");
        embed.setColor(Color.BLUE);

        List<Item> items = data.items;
        int page = data.currentPage;

        StringBuilder itemsList = new StringBuilder();
        for (int i = page * 10; i < Math.min((page + 1) * 10, items.size()); i++) {
            Item item = items.get(i);
            itemsList.append("`").append(item.getId()).append("` **").append(item.getName()).append("**\n");
        }

        embed.setDescription(itemsList.toString());
        embed.setFooter("Page " + (page + 1) + "/" + data.maxPages);

        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
    }


    public void registerPaginationMessage(long messageId, List<Item> items, int startPage) {
        int maxPages = (int) Math.ceil(items.size() / 10.0);
        paginationMessages.put(messageId, new PaginationData(startPage, maxPages, items));
    }


    private static class PaginationData {
        int currentPage;
        int maxPages;
        List<Item> items;

        public PaginationData(int currentPage, int maxPages, List<Item> items) {
            this.currentPage = currentPage;
            this.maxPages = maxPages;
            this.items = items;
        }
    }
}