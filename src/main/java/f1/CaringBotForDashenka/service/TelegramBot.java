package f1.CaringBotForDashenka.service;

import f1.CaringBotForDashenka.Data.Answers;
import f1.CaringBotForDashenka.Data.TehnicString;
import f1.CaringBotForDashenka.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static f1.CaringBotForDashenka.service.WeatherGiver.GiveClear5DaysWeatherString;
import static f1.CaringBotForDashenka.service.WeatherGiver.GiveClearCurrentWeatherString;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
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

        if(update.hasMessage()&&update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    srartCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/aboutme":
                    viewAboutMeMenu(chatId);
                    break;
                case "/whatweather":
                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText(giveWeatherData());
                    executeMessage(message);
                    viewAboutMeMenu(chatId);
                    break;
                case "/whattowear":
                    viewAboutMeMenu(chatId);
                    break;
                default:
                    sendMessage(chatId, "Sorry)");
            }
        }
        else if(update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(callbackData.equals(TehnicString.time)){
                Date date=new Date();
                String text = date.toString();
                EditMessageText message = new EditMessageText();
                message.setChatId(chatId);
                message.setText(text);
                message.setMessageId((int)messageId);
                executeMessage(message);
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.nameUser)){
                String text = update.getCallbackQuery().getMessage().getChat().getFirstName() ;
                EditMessageText message = new EditMessageText();
                message.setChatId(chatId);
                message.setText(text);
                message.setMessageId((int)messageId);
                executeMessage(message);
                viewAboutMeMenu(chatId);
            }

        }

    }

    private  String giveWeatherData(){
        String str;
        HashMap<String,String> dataMap = null;
        try {
            dataMap = GiveClearCurrentWeatherString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        str = "погода:\nТемпература: " + dataMap.get("temp")+"\nОщущается как: "+ dataMap.get("feels_like");
        return str;
    }

    private void viewWeatherData(long chatId) {
    }

    private void srartCommandReceived(long chatId, String name){
        String answer = "Hi, " + name+ ", nice man";
        log.info("Replied to user " + name );
        sendMessage(chatId, answer);
    }
    private void viewAboutMeMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выбери скорее чего ты хочешь!");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var timeButton = new InlineKeyboardButton();
        timeButton.setText(Answers.time);
        timeButton.setCallbackData(TehnicString.time);
        var nameButton = new InlineKeyboardButton();
        nameButton.setText(Answers.nameUser);
        nameButton.setCallbackData(TehnicString.nameUser);

        rowInLine.add(timeButton);
        rowInLine.add(nameButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup((markupInLine));

        executeMessage(message);
    }


    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message){
        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occured: "+e.getMessage());
        }
    }

    private void executeMessage(EditMessageText message){
        try{
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occured: "+e.getMessage());
        }
    }


}

