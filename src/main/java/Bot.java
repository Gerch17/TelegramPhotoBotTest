import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.*;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try{
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text)
    {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        try{
            setButton(sendMessage);
            sendMessage(sendMessage);
        }catch (TelegramApiException e)
        {
            e.printStackTrace();
        }
    }


    private String readALL(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
        {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try{
            BufferedReader rd =  new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText =  readALL(rd);
            JSONObject json = new JSONObject(jsonText) ;
            return json;
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }


    public void download(String token, String id, String format, String userName, Message message)
    {
        String namePath = "";
        JSONObject json = null;
        String check = new String();
        try{
            json = readJsonFromUrl("https://api.telegram.org/bot"+token+"/getFile?file_id="+id);

            namePath = json.getJSONObject("result").getString("file_path");

            String newURL = "https://api.telegram.org/file/bot"+token+"/"+namePath;
            ParsingFormat parse = new ParsingFormat(namePath);
            System.out.println(parse.parsing());
            if(parse.parsing().equals("jpg") || parse.parsing().equals("png") || parse.parsing().equals("WebM") || parse.parsing().equals("JPG") || parse.parsing().equals("PNG") || parse.parsing().equals("JPEG")) { // Проверка на расширение
                FileUtils.copyURLToFile(new URL(newURL), new File("C:\\Users\\tambo\\IdeaProjects\\ru.Gecrh.PhotoBotTest\\src\\main\\java\\Here\\" + userName + format));
                sendMsg(message, "Твоя работа была сохранена! Спасибо за участие");
            } else
            {
                sendMsg(message, "Спасибо конечно, но нам от тебя нужны только изображения");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setButton(SendMessage sendMessage)
    {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add("useless for now");
        keyboardFirstRow.add("useless for now");

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void onUpdateReceived(Update update) {
        String user_username = update.getMessage().getChat().getUserName();
        Message message = update.getMessage();
        if(message != null && message.hasText() && !message.hasPhoto())
        {
            switch(message.getText())
            {
                case "/start":
                    sendMsg(message, "Давай начнём! Напиши /help, для большей информации");
                    break;
                case "/help":
                    sendMsg(message, "Вот некоторые правила для участия: " +
                            "\n1. Можешь присылать несколько своих работ, но учитываться будет только последняя." +
                            "\n2. Убедись, что у тебя установлено имя пользователя(username)!" +
                            " С его помощью мы сможем с тобой связаться в случае твоей победы" +
                            "\n3. Отправляй изображения как файл" +
                            "\n4. Конкурс проходит каждую неделю с четверга по пятницу");
                    break;
                default:
                    sendMsg(message, "Извините, не понял вас");
                    break;
            }
        } else if(update.getMessage().getDocument() != null)
        {
                GetDayOfWeek day = new GetDayOfWeek();
            //System.out.println(day.getDay());
                if(day.getDay() == 4 || day.getDay() == 5 || day.getDay() == 1) { // Проверка на день недели
                    String id = new String();
                    id = update.getMessage().getDocument().getFileId();
                    download(getBotToken(), id, ".jpg", user_username, message);
                }else
                {
                    sendMsg(message, "Конкурс уже закончился");
                }
        }else 
        {
            sendMsg(message, "Отправьте своё изображение как файл или пропишите /help для большей информации");
        }
    }


    public String getBotUsername() {
        return "PhotoTestbot";
    }

    public String getBotToken() {
        return "";
    }
}
