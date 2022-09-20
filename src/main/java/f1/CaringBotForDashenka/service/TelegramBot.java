package f1.CaringBotForDashenka.service;

import f1.CaringBotForDashenka.Data.Answers;
import f1.CaringBotForDashenka.Data.TehnicString;
import f1.CaringBotForDashenka.config.BotConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static f1.CaringBotForDashenka.Data.Answers.*;
import static f1.CaringBotForDashenka.service.AnimalPhotoIMG.giveURLforCats;
import static f1.CaringBotForDashenka.service.AnimalPhotoIMG.giveURLforDogs;
import static f1.CaringBotForDashenka.service.Helper.returnClosestTime;
import static f1.CaringBotForDashenka.service.RandomPhrasesOfCare.giveRandomPhrasesOfCare;
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

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()&&update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    srartCommandReceived(chatId);
                    viewAboutMeMenu(chatId);
                    break;
                case "/aboutme":
                    sendMessage(chatId, aboutMeText);
                    viewAboutMeMenu(chatId);
                    break;
                default:
                    sendMessage(chatId, "Sorry)");
            }
        }
        else if(update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(callbackData.equals(TehnicString.aboutMe)){
                sendMessage(chatId,Answers.aboutMeText);
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.whatWeather)){
                sendMessage(chatId, giveWeatherData());
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.whatToWear)){
                sendMessage(chatId, recommendWhatToWear());
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.wordsOfCare)){
                sendMessage(chatId, giveRandomPhrasesOfCare());
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.showCat)){
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(giveURLforCats()));
                execute(sendPhoto);
                viewAboutMeMenu(chatId);
            }
            else if(callbackData.equals(TehnicString.showDog)){
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(giveURLforDogs()));
                execute(sendPhoto);
                viewAboutMeMenu(chatId);
            }
        }

    }

    private String recommendWhatToWear() {
        boolean isRain=false, isSnow = false;
        int counter=0;
        double averageTemp = 0;
        HashMap<String,String> dataMap;
        try {
            dataMap = GiveClear5DaysWeatherString();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        StringBuilder str = new StringBuilder();
        for(int i=returnClosestTime();i<24&&i!=-1;i+=3){
            str.append("Температура воздуха в "+i+":00: "+dataMap.get("temp"+i)+" градуса"+"\n");
            averageTemp =averageTemp + Double.parseDouble(dataMap.get("temp"+i));
            counter++;
            if (dataMap.get("main"+i) =="Rain") isRain=true;
            if (dataMap.get("main"+i) =="Snow") isSnow=true;
        }
        averageTemp = (double) Math.round((averageTemp*100 / counter))/100;
        str.append("Средняя температура " + averageTemp+" градуса" +"\n\n");

        if(isSnow)
            str.append(textSnowyWeather);
        else if (isRain&&averageTemp<=25 && averageTemp>=18)
            str.append(textBasicAndRainyWeather);
        else if(!isRain&&averageTemp<=25 && averageTemp>=18)
            str.append(textBasicAndSunnyWeather);
        else if(averageTemp<18&&averageTemp>=10)
            str.append(textSomeColdWeather);
        else if(averageTemp<10)
            str.append(textColdWeather);
        else if(averageTemp>25)
            str.append(textHotWeather);
        str.append("\n\n"+textFinalToWear);

        return str.toString();
    }

    private  String giveWeatherData(){
        String str;
        HashMap<String,String> dataMap;
        try {
            dataMap = GiveClearCurrentWeatherString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        str = "Погоды значит следующие:\n\nТемпературы: " + dataMap.get("temp")+" градусов.\n\nОщущается они как: "+ dataMap.get("feels_like")+" градусов.\n\nВетер "+dataMap.get("speed")+" метров в секунду"
        +".\n\nНа улице вообще "+dataMap.get("description")+".\n\nВот такие погоды, блин!";
        return str;
    }
    private void srartCommandReceived(long chatId){
        sendMessage(chatId, startText);
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

}

