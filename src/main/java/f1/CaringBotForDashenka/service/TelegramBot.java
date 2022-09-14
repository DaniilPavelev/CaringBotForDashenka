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
                    viewAboutMeMenu(chatId);
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
            if(callbackData.equals(TehnicString.aboutMe)){
                sendMessage(chatId,Answers.aboutMeText);
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.whatWeather)){
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText(giveWeatherData());
                executeMessage(message);
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.whatToWear)){

            }
            else if(callbackData.equals(TehnicString.wordsOfCare)){

            }
            else if(callbackData.equals(TehnicString.settings)){

            }
            else if(callbackData.equals(TehnicString.showCat)){

            }
            else if(callbackData.equals(TehnicString.showDog)){

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
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine4 = new ArrayList<>();

        var aboutMeButton = new InlineKeyboardButton();
        aboutMeButton.setText(Answers.aboutMe);
        aboutMeButton.setCallbackData(TehnicString.aboutMe);

        var wearButton = new InlineKeyboardButton();
        wearButton.setText(Answers.whatToWear);
        wearButton.setCallbackData(TehnicString.whatToWear);

        var weatherButton = new InlineKeyboardButton();
        weatherButton.setText(Answers.whatWeather);
        weatherButton.setCallbackData(TehnicString.whatWeather);

        var wordsOfCareButton = new InlineKeyboardButton();
        wordsOfCareButton.setText(Answers.wordsOfCare);
        wordsOfCareButton.setCallbackData(TehnicString.wordsOfCare);

        var settingsButton = new InlineKeyboardButton();
        settingsButton.setText(Answers.settings);
        settingsButton.setCallbackData(TehnicString.settings);

        var showCatButton = new InlineKeyboardButton();
        showCatButton.setText(Answers.showCat);
        showCatButton.setCallbackData(TehnicString.showCat);

        var showDogsButton = new InlineKeyboardButton();
        showDogsButton.setText(Answers.showDog);
        showDogsButton.setCallbackData(TehnicString.showDog);




        rowInLine1.add(aboutMeButton);
        rowInLine1.add(weatherButton);

        rowInLine2.add(wearButton);
        rowInLine2.add(wordsOfCareButton);

        rowInLine3.add(settingsButton);
        rowInLine3.add(showDogsButton);

        rowInLine4.add(showCatButton);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        rowsInLine.add(rowInLine3);
        rowsInLine.add(rowInLine4);

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

