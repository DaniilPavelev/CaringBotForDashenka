package f1.CaringBotForDashenka.service;

import f1.CaringBotForDashenka.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начни получать свою заботу..."));
        listOfCommands.add(new BotCommand("/aboutMe", "Немного о том для кого и зачем я был создан"));
        listOfCommands.add(new BotCommand("/whatWeather","Расскажу какая погода в твоём городе"));
        listOfCommands.add(new BotCommand("/whatToWear","Расскажу тебе как одеться по погоде, чтоб не заболеть"));
        listOfCommands.add(new BotCommand("/wordsOfCare","Всем хочется получить немного заботы"));
        listOfCommands.add(new BotCommand("/settings","подкрутить штуки"));
        listOfCommands.add(new BotCommand("/showCat","Котички!"));
        listOfCommands.add(new BotCommand("/showDog","Собачки!"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        }
        catch (TelegramApiException e){
            log.error("Error menu list"+e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()&&update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    srartCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default: sendMessage(chatId, "Sorry)");
            }
        }

    }

    private void srartCommandReceived(long chatId, String name){
        String answer = "Hi, " + name+ ", nice man";
        log.info("Replied to user " + name );
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occured: "+e.getMessage());
        }
    }

}

