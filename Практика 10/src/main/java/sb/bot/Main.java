package sb.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:./test", "sa", "");
            DatabaseManager dbManager = new DatabaseManager(connection);
            EventService eventService = new EventService(dbManager);
            MyTelegramBot bot = new MyTelegramBot(eventService);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

            System.out.println("Bot started successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}