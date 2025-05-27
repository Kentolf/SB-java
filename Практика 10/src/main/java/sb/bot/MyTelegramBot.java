package sb.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyTelegramBot extends TelegramLongPollingBot {
    private final EventService eventService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Map<String, UserState> userStates = new HashMap<>();

    private final Map<String, String> userTempData = new HashMap<>();

    private final Map<String, CommandInfo> textCommands = new HashMap<>();
    private final Map<UserState, BotCommandHandler> stateHandlers = new HashMap<>();

    private enum UserState {
        DEFAULT,
        AWAITING_EVENT_NAME,
        AWAITING_AVAILABILITY,
        AWAITING_SHOW_TIME_ID,
        AWAITING_DATE_FOR_AVAILABILITY,
        AWAITING_DATE_FOR_DELETION,
        AWAITING_REMINDER_TEXT
    }

    @Override
    public String getBotUsername() {
        return "CoolEventPlannerBot";
    }

    @Override
    public String getBotToken() {
        return "7848177215:AAF105GExdRmarjsQcO3FfEFrfob-rGrazU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();

                if (text.startsWith("/start join_")) {
                    String code = text.substring(12);
                    handleInviteLink(update, code);
                    return;
                }
                else if (text.equals("/start")) {
                    SendMessage welcome = new SendMessage();
                    welcome.setChatId(update.getMessage().getChatId());
                    welcome.setText("Добро пожаловать! Используйте /help для списка команд");
                    execute(welcome);
                    return;
                }

                handleMessage(update);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface BotCommandHandler {
        void handle(Update update, SendMessage message, String userId) throws TelegramApiException;
    }

    private void handleInviteLink(Update update, String code) {
        long chatId = update.getMessage().getChatId();
        String userId = update.getMessage().getFrom().getId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        Integer eventId = eventService.getEventIdByCode(code);
        if (eventId == null) {
            message.setText("Неверная ссылка приглашения. Пожалуйста, запросите новую у организатора.");
        }
        else {
            boolean success = eventService.joinEvent(eventId, userId);
            if (success) {
                String eventName = eventService.getEventName(eventId);
                message.setText("Вы успешно присоединились к событию \"" + eventName + "\"!");
                message.setReplyMarkup(createMainKeyboard());
            }
            else {
                message.setText("Произошла ошибка при присоединении к событию.");
            }
        }

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String userId = update.getCallbackQuery().getFrom().getId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (callbackData.startsWith("HOUR_")) {
            int hour = Integer.parseInt(callbackData.substring(5));
            String date = userTempData.get(userId);
            String messageId = userTempData.get(userId + "_messageId");

            if (date != null && messageId != null) {
                try {
                    String selectedHours = userTempData.getOrDefault(userId + "_hours", "");
                    List<String> hoursList = new ArrayList<>(Arrays.asList(selectedHours.split(",")));

                    hoursList.removeIf(String::isEmpty);

                    String hourStr = String.valueOf(hour);
                    if (hoursList.contains(hourStr)) {
                        hoursList.remove(hourStr);
                    }
                    else {
                        hoursList.add(hourStr);
                    }

                    String updatedHours = String.join(",", hoursList);
                    userTempData.put(userId + "_hours", updatedHours);

                    EditMessageText editMessage = new EditMessageText();
                    editMessage.setChatId(String.valueOf(chatId));
                    editMessage.setMessageId(Integer.parseInt(messageId));
                    editMessage.setText("Выберите свободные часы для " + date +
                            " (выбрано: " + hoursList.size() + ")");
                    editMessage.setReplyMarkup(createHourButtonsWithSelected(userId));

                    execute(editMessage);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        switch (callbackData) {
            case "CREATE_EVENT":
                userStates.put(userId, UserState.AWAITING_EVENT_NAME);
                message.setText("Введите название события:");
                message.setReplyMarkup(createCancelKeyboard());
                break;

            case "ADD_AVAILABILITY":
                userStates.put(userId, UserState.AWAITING_DATE_FOR_AVAILABILITY);
                message.setText("Введите дату в удобном формате:\n"
                        + "• сегодня/завтра\n"
                        + "• 06.05.2025");
                message.setReplyMarkup(createCancelKeyboard());
                break;

            case "SHOW_TIMES":
                userStates.put(userId, UserState.AWAITING_SHOW_TIME_ID);
                message.setText("Введите ID события для просмотра общего времени:");
                message.setReplyMarkup(createCancelKeyboard());
                break;

            case "CANCEL":
                userStates.put(userId, UserState.DEFAULT);
                message.setText("Действие отменено");
                message.setReplyMarkup(createMainKeyboard());
                break;

            case "DONE_HOURS":
                handleDoneHoursSelection(update, message, userId);
                break;

            case "DELETE_ALL_MY_TIME":
                handleDeleteAllTime(chatId, userId);
                break;

            case "DELETE_FOR_DATE":
                handleDeleteForDate(chatId, userId);
                break;

            case "CLEAR_ALL_HOURS":
                handleClearAllHours(update, chatId, userId);
                break;

            default:
                message.setText("Неизвестная команда");
                message.setReplyMarkup(createMainKeyboard());
        }

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userId = update.getMessage().getFrom().getId().toString();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        try {
            UserState currentState = userStates.getOrDefault(userId, UserState.DEFAULT);

            BotCommandHandler stateHandler = stateHandlers.get(currentState);
            if (stateHandler != null) {
                stateHandler.handle(update, message, userId);
                execute(message);
                return;
            }

            CommandInfo commandInfo = textCommands.get(messageText);
            if (commandInfo != null) {
                if (commandInfo.getRequiredState() == null ||
                        commandInfo.getRequiredState() == currentState) {
                    commandInfo.getHandler().handle(update, message, userId);
                    execute(message);
                    return;
                }
            }

            message.setText("Неизвестная команда. Напишите /help для списка команд.");
            message.setReplyMarkup(createMainKeyboard());
            userStates.put(userId, UserState.DEFAULT);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleMainMenu(Update update, SendMessage message, String userId) throws TelegramApiException {
        userStates.put(userId, UserState.DEFAULT);
        message.setText("Выберите действие:");
        message.setReplyMarkup(createMainKeyboard());
    }

    private void handleEventCreation(Update update, SendMessage message, String userId) throws TelegramApiException {
        String eventName = update.getMessage().getText().trim();
        if (eventName.isEmpty()) {
            message.setText("Название события не может быть пустым. Попробуйте еще раз.");
            message.setReplyMarkup(createCancelKeyboard());
            return;
        }

        int eventId = eventService.createEvent(eventName, userId);
        if (eventId != -1) {
            eventService.joinEvent(eventId, userId);
            message.setText("Событие \"" + eventName + "\" успешно создано");
        } else {
            message.setText("Произошла ошибка при создании события.");
        }
        resetUserState(message, userId);
    }

    private void handleEventCreationCommand(Update update, SendMessage message, String userId) throws TelegramApiException {
        userStates.put(userId, UserState.AWAITING_EVENT_NAME);
        message.setText("Введите название события:");
        message.setReplyMarkup(createCancelKeyboard());
    }

    private void handleAvailableCommand(Update update, SendMessage message, String userId) throws TelegramApiException {
        userStates.put(userId, UserState.AWAITING_DATE_FOR_AVAILABILITY);
        message.setText("Введите дату:");
        message.setReplyMarkup(createCancelKeyboard());
    }

    private void handleShowTimeCommand(Update update, SendMessage message, String userId) throws TelegramApiException {
        try {
            Integer eventId = eventService.getEventIdForUser(userId);

            if (eventId == null) {
                message.setText("Вы не присоединены ни к одному событию.\n" +
                        "Чтобы присоединиться, перейдите по ссылке-приглашению от организатора.");
                resetUserState(message, userId);
                return;
            }

            String eventName = eventService.getEventName(eventId);
            var commonSlots = eventService.findCommonTimeSlots(eventId);

            if (commonSlots.isEmpty()) {
                message.setText("Для события \"" + eventName + "\" пока нет пересечений свободного времени участников.");
            } else {
                StringBuilder sb = new StringBuilder("Общее свободное время для события \"" + eventName + "\":\n");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                for (var slot : commonSlots) {
                    if (slot.getStart().equals(slot.getEnd())) continue;

                    sb.append("• ")
                            .append(slot.getStart().format(DATE_TIME_FORMATTER))
                            .append(" - ")
                            .append(slot.getEnd().format(timeFormatter))
                            .append("\n");
                }
                message.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Произошла ошибка при получении данных о событии.");
        }
        resetUserState(message, userId);
    }

    private void handleShowCommonTime(Update update, SendMessage message, String userId) throws TelegramApiException {
        try {
            Integer eventId = eventService.getEventIdForUser(userId);

            if (eventId == null) {
                message.setText("Вы не присоединены ни к одному событию.\n" +
                        "Чтобы присоединиться, перейдите по ссылке-приглашению от организатора.");
                resetUserState(message, userId);
                return;
            }

            String eventName = eventService.getEventName(eventId);
            var commonSlots = eventService.findCommonTimeSlots(eventId);

            if (commonSlots.isEmpty()) {
                message.setText("Для события \"" + eventName + "\" пока нет пересечений свободного времени участников.");
            } else {
                StringBuilder sb = new StringBuilder("Общее свободное время для события \"" + eventName + "\":\n");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                for (var slot : commonSlots) {
                    if (slot.getStart().equals(slot.getEnd())) continue;

                    sb.append("• ")
                            .append(slot.getStart().format(DATE_TIME_FORMATTER))
                            .append(" - ")
                            .append(slot.getEnd().format(timeFormatter))
                            .append("\n");
                }
                message.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Произошла ошибка при получении данных о событии.");
        }
        resetUserState(message, userId);
    }

    private void handleDeleteAllTime(long chatId, String userId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        Integer eventId = eventService.getEventIdForUser(userId);
        if (eventId == null) {
            message.setText("Вы не участвуете ни в одном событии");
        }
        else {
            boolean success = eventService.deleteAllAvailabilityForUser(eventId, userId);
            message.setText(success ? "Все ваши записи удалены" : "Не удалось удалить записи");
        }

        userStates.put(userId, UserState.DEFAULT);
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteForDate(long chatId, String userId) {
        userStates.put(userId, UserState.AWAITING_DATE_FOR_DELETION);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Введите дату в формате ДД.ММ.ГГГГ");
        message.setReplyMarkup(createCancelKeyboard());

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleDateInputForAvailability(Update update, SendMessage message, String userId) throws TelegramApiException {
        String dateStr = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        message.setChatId(String.valueOf(chatId));

        try {
            LocalDate date = parseHumanReadableDate(dateStr);
            if (date == null) {
                message.setText("Неверный формат даты. Попробуйте еще раз, например:\n" +
                        "• Сегодня\n" +
                        "• Завтра\n" +
                        "• 06.05.2025");
                message.setReplyMarkup(createCancelKeyboard());
                return;
            }

            userTempData.put(userId, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

            SendMessage hourMessage = new SendMessage();
            hourMessage.setChatId(String.valueOf(chatId));
            hourMessage.setText("Выберите свободные часы:");
            hourMessage.setReplyMarkup(createHourButtons());

            Message sentMessage = execute(hourMessage);
            userTempData.put(userId + "_messageId", String.valueOf(sentMessage.getMessageId()));

            userStates.put(userId, UserState.AWAITING_AVAILABILITY);
        } catch (Exception e) {
            message.setText("Ошибка обработки даты. Попробуйте еще раз.");
            message.setReplyMarkup(createCancelKeyboard());
        }
    }

    private LocalDate parseHumanReadableDate(String input) {
        String normalized = input.trim().toLowerCase();
        if ("сегодня".equals(normalized))
            return LocalDate.now();
        if ("завтра".equals(normalized))
            return LocalDate.now().plusDays(1);

        try {

            DateTimeFormatter formatterWithYear = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
            DateTimeFormatter shortFormatterWithYear = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("ru"));

            DateTimeFormatter formatterWithoutYear = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));
            DateTimeFormatter shortFormatterWithoutYear = DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));

            DateTimeFormatter numericFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            try {
                return LocalDate.parse(normalized, numericFormatter);

            }
            catch (DateTimeParseException e1) {
                try {
                    return LocalDate.parse(normalized, formatterWithYear);
                }
                catch (DateTimeParseException e2) {
                    try {
                        return LocalDate.parse(normalized, shortFormatterWithYear);
                    }
                    catch (DateTimeParseException e3) {
                        try {
                            return LocalDate.parse(normalized + " " + LocalDate.now().getYear(),
                                    formatterWithoutYear);
                        }
                        catch (DateTimeParseException e4) {
                            return LocalDate.parse(normalized + " " + LocalDate.now().getYear(),
                                    shortFormatterWithoutYear);
                        }
                    }
                }
            }
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void handleDeleteDateInput(Update update, SendMessage message, String userId) throws TelegramApiException {
        String dateStr = update.getMessage().getText();
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            Integer eventId = eventService.getEventIdForUser(userId);

            if (eventId == null) {
                message.setText("Вы не участвуете ни в одном событии");
            } else {
                boolean success = eventService.deleteAvailabilityForDate(eventId, userId, date);
                message.setText(success ?
                        "Ваши записи за " + dateStr + " удалены" :
                        "Не найдено записей за указанную дату");
            }
        } catch (DateTimeParseException e) {
            message.setText("Неверный формат даты. Используйте ДД.ММ.ГГГГ");
        } finally {
            resetUserState(message, userId);
        }
    }


    private void handleDeleteTimeCommand(Update update, SendMessage message, String userId) throws TelegramApiException {
        userStates.put(userId, UserState.AWAITING_DATE_FOR_DELETION);
        message.setText("Выберите действие:\n" +
                "1. Удалить все мои записи\n" +
                "2. Удалить за конкретную дату\n" +
                "Или введите дату в формате ДД.ММ.ГГГГ");
        message.setReplyMarkup(createDeleteOptionsKeyboard());
    }

    private void handleClearAllHours(Update update, long chatId, String userId) {
        try {
            userTempData.remove(userId + "_hours");

            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(String.valueOf(chatId));
            editMessage.setMessageId(messageId);
            editMessage.setText("Все выбранные часы очищены. Выберите снова:");
            editMessage.setReplyMarkup(createHourButtonsWithSelected(userId));

            execute(editMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
            try {
                SendMessage errorMessage = new SendMessage();
                errorMessage.setChatId(String.valueOf(chatId));
                errorMessage.setText("Все часы очищены. Выберите снова:");
                errorMessage.setReplyMarkup(createHourButtonsWithSelected(userId));
                execute(errorMessage);
            }
            catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void handleGetInviteLink(Update update, SendMessage message, String userId) throws TelegramApiException {
        Integer eventId = eventService.getEventIdForUser(userId);
        if (eventId == null) {
            message.setText("Вы не участвуете ни в одном событии.");
            return;
        }

        String creatorId = eventService.getCreatorId(eventId);
        if (!userId.equals(creatorId)) {
            message.setText("Только создатель события может получить ссылку-приглашение.");
            return;
        }

        String inviteLink = eventService.generateInviteLink(eventId, getBotUsername());
        message.setText("Ссылка для приглашения участников:\n" + inviteLink);
    }

    private InlineKeyboardMarkup createHourButtons() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < 24; i += 4) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 4 && j < 24; j++) {
                InlineKeyboardButton hourBtn = new InlineKeyboardButton(String.format("%02d:00", j));
                hourBtn.setCallbackData("HOUR_" + j);
                row.add(hourBtn);
            }
            rows.add(row);
        }

        List<InlineKeyboardButton> actionRow = new ArrayList<>();

        InlineKeyboardButton clearBtn = new InlineKeyboardButton("Очистить все");
        clearBtn.setCallbackData("CLEAR_ALL_HOURS");
        actionRow.add(clearBtn);

        InlineKeyboardButton doneBtn = new InlineKeyboardButton("Готово");
        doneBtn.setCallbackData("DONE_HOURS");
        actionRow.add(doneBtn);

        InlineKeyboardButton cancelBtn = new InlineKeyboardButton("Отмена");
        cancelBtn.setCallbackData("CANCEL");
        actionRow.add(cancelBtn);

        rows.add(actionRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private InlineKeyboardMarkup createDeleteOptionsKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton allBtn = new InlineKeyboardButton("Удалить ВСЁ моё время");
        allBtn.setCallbackData("DELETE_ALL_MY_TIME");
        row1.add(allBtn);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton dateBtn = new InlineKeyboardButton("Удалить за дату");
        dateBtn.setCallbackData("DELETE_FOR_DATE");
        row2.add(dateBtn);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton cancelBtn = new InlineKeyboardButton("Отмена");
        cancelBtn.setCallbackData("CANCEL_DELETE");
        row3.add(cancelBtn);

        keyboard.setKeyboard(List.of(row1, row2, row3));
        return keyboard;
    }


    private void resetUserState(SendMessage message, String userId) {
        userStates.put(userId, UserState.DEFAULT);
        message.setReplyMarkup(createMainKeyboard());
    }

    private void showHelp(SendMessage message) {
        message.setText("""
                Доступные команды (но лучше используйте кнопки):
                            /create_event - Создать новое событие
                            /available - Указать своё свободное время
                            /show_time - Показать общее свободное время для вашего события
                            /show_my_time - Показать общее свободное время для вашего события
                            /delete_available - Удалить свободное время
                            /get_invite - Получить ссылку-приглашение (для создателя)
                            /help - Показать эту справку
                """);
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Создать событие");
        row1.add("Получить ссылку");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Ввести время");
        row2.add("Показать время");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Удалить время");
        row3.add("Отправить уведомление");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Помощь");
        row4.add("Главное меню");

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        keyboard.setKeyboard(rows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        return keyboard;
    }


    private InlineKeyboardMarkup createCancelKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> row = new ArrayList<>();

        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelBtn = new InlineKeyboardButton("Отмена");
        cancelBtn.setCallbackData("CANCEL");
        cancelRow.add(cancelBtn);

        row.add(cancelRow);
        keyboard.setKeyboard(row);
        return keyboard;
    }

    private InlineKeyboardMarkup createHourButtonsWithSelected(String userId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        String selectedHoursStr = userTempData.getOrDefault(userId + "_hours", "");
        List<String> selectedHours = new ArrayList<>(Arrays.asList(selectedHoursStr.split(",")));
        selectedHours.removeIf(String::isEmpty);

        for (int i = 0; i < 24; i += 4) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 4 && j < 24; j++) {
                String hourText = selectedHours.contains(String.valueOf(j)) ?
                        "✅ " + String.format("%02d:00", j) :
                        String.format("%02d:00", j);

                InlineKeyboardButton hourBtn = new InlineKeyboardButton(hourText);
                hourBtn.setCallbackData("HOUR_" + j);
                row.add(hourBtn);
            }
            rows.add(row);
        }

        List<InlineKeyboardButton> actionRow = new ArrayList<>();

        InlineKeyboardButton clearBtn = new InlineKeyboardButton("Очистить все");
        clearBtn.setCallbackData("CLEAR_ALL_HOURS");
        actionRow.add(clearBtn);

        InlineKeyboardButton doneBtn = new InlineKeyboardButton("Готово");
        doneBtn.setCallbackData("DONE_HOURS");
        actionRow.add(doneBtn);

        InlineKeyboardButton cancelBtn = new InlineKeyboardButton("Отмена");
        cancelBtn.setCallbackData("CANCEL");
        actionRow.add(cancelBtn);

        rows.add(actionRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void handleDoneHoursSelection(Update update, SendMessage message, String userId) {
        String date = userTempData.get(userId);
        String selectedHoursStr = userTempData.getOrDefault(userId + "_hours", "");

        if (selectedHoursStr.isEmpty()) {
            message.setText("Вы не выбрали ни одного часа. Попробуйте снова.");
            message.setReplyMarkup(createHourButtons());
            return;
        }

        try {
            Integer eventId = eventService.getEventIdForUser(userId);
            if (eventId == null) {
                message.setText("Вы не присоединены ни к одному событию.");
                resetUserState(message, userId);
                return;
            }


            String userName = update.getCallbackQuery().getFrom().getUserName();
            if (userName == null) {
                userName = update.getCallbackQuery().getFrom().getFirstName();
            }

            String eventName = eventService.getEventName(eventId);
            if (eventName == null) {
                eventName = "Событие ID: " + eventId;
            }

            LocalDateTime startOfDay = LocalDateTime.parse(date + " 00:00", DATE_TIME_FORMATTER);
            LocalDateTime endOfDay = LocalDateTime.parse(date + " 23:59", DATE_TIME_FORMATTER);
            eventService.deleteAvailability(eventId, userId, startOfDay, endOfDay);

            List<String> selectedIntervals = new ArrayList<>();
            String[] hoursArray = selectedHoursStr.split(",");
            Arrays.sort(hoursArray);

            for (String hourStr : hoursArray) {
                if (!hourStr.isEmpty()) {
                    int hour = Integer.parseInt(hourStr);
                    LocalDateTime start = LocalDateTime.parse(date + " " + String.format("%02d:00", hour),
                            DATE_TIME_FORMATTER);
                    LocalDateTime end = start.plusHours(1);
                    eventService.addAvailability(eventId, userId, start, end);

                    selectedIntervals.add(String.format("%02d:00-%02d:00", hour, hour + 1));
                }
            }

            List<String> mergedIntervals = mergeAdjacentIntervals(selectedIntervals);
            String intervalsText = String.join(", ", mergedIntervals);

            String creatorId = eventService.getCreatorId(eventId);
            if (creatorId != null && !creatorId.equals(userId)) {
                SendMessage notify = new SendMessage();
                notify.setChatId(creatorId);

                notify.setText("Участник " + userName + " выбрал свободное время " + date + " для события \"" + eventName + "\":\n" + intervalsText);

                try {
                    execute(notify);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            message.setText("Ваше свободное время для " + date + " успешно обновлено!");
            message.setReplyMarkup(createMainKeyboard());
            userStates.put(userId, UserState.DEFAULT);

            userTempData.remove(userId);
            userTempData.remove(userId + "_hours");
            userTempData.remove(userId + "_messageId");

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Произошла ошибка при сохранении времени. Попробуйте снова.");
            message.setReplyMarkup(createHourButtons());
        }
    }

    private List<String> mergeAdjacentIntervals(List<String> intervals) {
        if (intervals.isEmpty()) return intervals;

        List<String> merged = new ArrayList<>();
        String[] first = intervals.get(0).split("-");
        int prevEnd = Integer.parseInt(first[1].replace(":00", ""));

        for (int i = 1; i < intervals.size(); i++) {
            String[] current = intervals.get(i).split("-");
            int currStart = Integer.parseInt(current[0].replace(":00", ""));

            if (currStart == prevEnd) {
                first[1] = current[1];
                prevEnd = Integer.parseInt(current[1].replace(":00", ""));
            } else {
                merged.add(first[0] + "-" + first[1]);
                first = current;
                prevEnd = Integer.parseInt(first[1].replace(":00", ""));
            }
        }
        merged.add(first[0] + "-" + first[1]);

        return merged;
    }

    private void handleRemindParticipantsCommand(Update update, SendMessage message, String userId) throws TelegramApiException {
        Integer eventId = eventService.getEventIdForUser(userId);
        if (eventId == null) {
            message.setText("Вы не участвуете ни в одном событии.");
            return;
        }

        String creatorId = eventService.getCreatorId(eventId);
        if (!userId.equals(creatorId)) {
            message.setText("Только создатель события может отправлять уведомления.");
            return;
        }

        userStates.put(userId, UserState.AWAITING_REMINDER_TEXT);
        message.setText("Введите текст уведомления, которое получит каждый участник:");
        message.setReplyMarkup(createCancelKeyboard());
    }

    private void handleReminderTextInput(Update update, SendMessage message, String userId) throws TelegramApiException {
        String reminderText = update.getMessage().getText();
        try {
            Integer eventId = eventService.getEventIdForUser(userId);
            if (eventId == null) {
                message.setText("Ошибка: событие не найдено.");
                resetUserState(message, userId);
                return;
            }

            List<Long> participants = eventService.getParticipants(eventId);
            String creatorId = eventService.getCreatorId(eventId);

            int sentCount = 0;
            for (Long participantId : participants) {
                if (!String.valueOf(participantId).equals(creatorId)) {
                    SendMessage reminder = new SendMessage();
                    reminder.setChatId(String.valueOf(participantId));
                    reminder.setText("🔔 Уведомление:\n" + reminderText);

                    try {
                        execute(reminder);
                        sentCount++;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

            message.setText("Уведомление отправлено " + sentCount + " участникам.");
            resetUserState(message, userId);
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Произошла ошибка при отправке уведомления.");
            resetUserState(message, userId);
        }
    }

    public MyTelegramBot(EventService eventService) {
        this.eventService = eventService;
        initializeCommands();
    }

    private void initializeCommands() {
        // Обработчики для текстовых команд
        textCommands.put("/start", new CommandInfo("/start", this::handleMainMenu, UserState.DEFAULT));
        textCommands.put("Главное меню", new CommandInfo("Главное меню", this::handleMainMenu, UserState.DEFAULT));

        textCommands.put("/create_event", new CommandInfo("/create_event",
                this::handleEventCreationCommand, UserState.DEFAULT));
        textCommands.put("Создать событие", new CommandInfo("Создать событие",
                this::handleEventCreationCommand, UserState.DEFAULT));

        textCommands.put("/available", new CommandInfo("/available",
                this::handleAvailableCommand, UserState.DEFAULT));
        textCommands.put("Ввести время", new CommandInfo("Ввести время",
                this::handleAvailableCommand, UserState.DEFAULT));

        textCommands.put("/show_time", new CommandInfo("/show_time",
                this::handleShowTimeCommand, UserState.DEFAULT));
        textCommands.put("Показать время", new CommandInfo("Показать время",
                this::handleShowTimeCommand, UserState.DEFAULT));

        textCommands.put("/show_my_time", new CommandInfo("/show_my_time",
                this::handleShowCommonTime, UserState.DEFAULT));
        textCommands.put("Моё время", new CommandInfo("Моё время",
                this::handleShowCommonTime, UserState.DEFAULT));

        textCommands.put("/delete_available", new CommandInfo("/delete_available",
                this::handleDeleteTimeCommand, UserState.DEFAULT));
        textCommands.put("Удалить время", new CommandInfo("Удалить время",
                this::handleDeleteTimeCommand, UserState.DEFAULT));

        textCommands.put("/get_invite", new CommandInfo("/get_invite",
                this::handleGetInviteLink, UserState.DEFAULT));
        textCommands.put("Получить ссылку", new CommandInfo("Получить ссылку",
                this::handleGetInviteLink, UserState.DEFAULT));

        textCommands.put("/help", new CommandInfo("/help",
                (update, message, userId) -> showHelp(message), UserState.DEFAULT));
        textCommands.put("Помощь", new CommandInfo("Помощь",
                (update, message, userId) -> showHelp(message), UserState.DEFAULT));

        textCommands.put("Отправить уведомление", new CommandInfo("Отправить уведомление",
                this::handleRemindParticipantsCommand, UserState.DEFAULT));

        // Обработчики для состояний
        stateHandlers.put(UserState.AWAITING_EVENT_NAME, this::handleEventCreation);
        stateHandlers.put(UserState.AWAITING_DATE_FOR_AVAILABILITY, this::handleDateInputForAvailability);
        stateHandlers.put(UserState.AWAITING_DATE_FOR_DELETION, this::handleDeleteDateInput);
        stateHandlers.put(UserState.AWAITING_REMINDER_TEXT, this::handleReminderTextInput);
        // Removed: stateHandlers.put(UserState.AWAITING_SHOW_TIME_ID, this::handleShowTimeRequest);
    }


    class CommandInfo {
        private final String command;
        private final BotCommandHandler handler;
        private final UserState requiredState;

        public CommandInfo(String command, BotCommandHandler handler, UserState requiredState) {
            this.command = command;
            this.handler = handler;
            this.requiredState = requiredState;
        }

        public BotCommandHandler getHandler() { return handler; }
        public UserState getRequiredState() { return requiredState; }
    }
}