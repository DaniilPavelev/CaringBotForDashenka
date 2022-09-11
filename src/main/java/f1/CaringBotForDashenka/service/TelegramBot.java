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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    static final String aboutMeText ="Я был создан что-бы что....";
    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start"));
        listOfCommands.add(new BotCommand("/aboutme", "about me"));
        listOfCommands.add(new BotCommand("/whatweather","what weather"));
        listOfCommands.add(new BotCommand("/whattowear","what to wear"));
        listOfCommands.add(new BotCommand("/wordsofcare","words of care"));
        listOfCommands.add(new BotCommand("/settings","settings"));
        listOfCommands.add(new BotCommand("/showcat","show cat"));
        listOfCommands.add(new BotCommand("/showdog","show dog"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        }
        catch (TelegramApiException e){
            log.error("error menu list"+e.getMessage());
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
         //           srartCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    sendMessage(chatId,aboutMeText);
                    viewStartMenu(chatId);
                    break;
                case "/aboutme":
                    sendMessage(chatId, aboutMeText);
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
    private void viewStartMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выбери скорее чего ты хочешь!");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var timeButton = new InlineKeyboardButton();
        timeButton.setText("Время");
        timeButton.setCallbackData("TIME_BUTTON");
        var nameButton = new InlineKeyboardButton();
        nameButton.setText("Имя пользователя");
        nameButton.setCallbackData("NAME_BUTTON");

        rowInLine.add(timeButton);
        rowInLine.add(nameButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup((markupInLine));

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

