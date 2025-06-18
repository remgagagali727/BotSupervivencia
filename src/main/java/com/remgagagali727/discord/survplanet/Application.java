package com.remgagagali727.discord.survplanet;

import com.remgagagali727.discord.survplanet.listener.BotListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.remgagagali727.discord.survplanet.listener.ReactionHandler;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		String token = System.getenv("DS_BOT");

		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		BotListener botListener = context.getBean(BotListener.class);
		ReactionHandler reactionHandler = context.getBean(ReactionHandler.class);

		JDABuilder.createDefault(token,
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.MESSAGE_CONTENT,
						GatewayIntent.GUILD_MESSAGE_REACTIONS)
				.enableIntents(
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.MESSAGE_CONTENT,
						GatewayIntent.GUILD_MESSAGE_REACTIONS)
				.addEventListeners(botListener, reactionHandler)
				.build();

	}

}
