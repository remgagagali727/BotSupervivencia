package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

@Getter
@Entity
@NoArgsConstructor
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String crafting_price;
    private String sell_price;

    public void notSelleable(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("❌ Cannot Sell Item");
        embedBuilder.setDescription("The item you selected is not sellable.\nSome items may be bound, unique, or have no market value.");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setFooter("Surv Planet");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
