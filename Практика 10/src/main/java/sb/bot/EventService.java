package sb.bot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class EventService {
    private final DatabaseManager dbManager;
    private final Map<String, Integer> userEventMap = new HashMap<>();


    public EventService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public int createEvent(String eventName, String creatorId) {
        int eventId = dbManager.createEvent(eventName, creatorId);
        if (eventId != -1) {
            userEventMap.put(creatorId, eventId);
        }
        return eventId;
    }

    public boolean joinEvent(int eventId, String userId) {
        try {
            long chatId = Long.parseLong(userId);
            dbManager.joinEvent(eventId, chatId);
            userEventMap.put(userId, eventId);
            return true;
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Integer getEventIdForUser(String userId) {
        return userEventMap.get(userId);
    }

    public void addAvailability(int eventId, String userId, LocalDateTime start, LocalDateTime end) {
        dbManager.saveAvailability(eventId, Long.parseLong(userId), start, end);
    }

    public List<TimeSlot> findCommonTimeSlots(int eventId) {
        Map<String, List<TimeSlot>> userSlots = dbManager.getAvailabilitiesByEvent(eventId);

        if (userSlots == null || userSlots.isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeSlot> commonSlots = null;

        for (List<TimeSlot> slots : userSlots.values()) {
            if (commonSlots == null) {
                commonSlots = new ArrayList<>(slots);
            } else {
                commonSlots = intersect(commonSlots, slots);
                if (commonSlots.isEmpty()) {
                    return Collections.emptyList();
                }
            }
        }

        if (commonSlots != null) {
            commonSlots = mergeConsecutiveSlots(commonSlots);
        }

        return commonSlots != null ? commonSlots : Collections.emptyList();
    }

    private List<TimeSlot> mergeConsecutiveSlots(List<TimeSlot> slots) {
        if (slots.isEmpty()) {
            return slots;
        }

        slots.sort(Comparator.comparing(TimeSlot::getStart));

        List<TimeSlot> merged = new ArrayList<>();
        TimeSlot current = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            TimeSlot next = slots.get(i);

            if (!next.getStart().isAfter(current.getEnd())) {
                LocalDateTime newEnd = current.getEnd().isAfter(next.getEnd())
                        ? current.getEnd()
                        : next.getEnd();
                current = new TimeSlot(current.getStart(), newEnd);
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return merged;
    }

    public boolean deleteAllAvailabilityForUser(int eventId, String userId) {
        try {
            long chatId = Long.parseLong(userId);
            return dbManager.deleteAllAvailabilityForUser(eventId, chatId);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAvailabilityForDate(int eventId, String userId, LocalDate date) {
        try {
            long chatId = Long.parseLong(userId);
            return dbManager.deleteAvailabilityForDate(eventId, chatId, date);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAvailability(int eventId, String userId, LocalDateTime start, LocalDateTime end) {
        try {
            long chatId = Long.parseLong(userId);
            return dbManager.deleteAvailability(eventId, chatId, start, end);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserJoinedToEvent(int eventId, String userId) {
        Integer cachedEventId = userEventMap.get(userId);
        if (cachedEventId != null) {
            return cachedEventId == eventId;
        }

        try {
            long chatId = Long.parseLong(userId);
            List<Long> participants = dbManager.getParticipants(eventId);
            boolean isJoined = participants.contains(chatId);
            if (isJoined) {
                userEventMap.put(userId, eventId); // кэшируем
            }
            return isJoined;
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }


    private List<TimeSlot> intersect(List<TimeSlot> list1, List<TimeSlot> list2) {
        List<TimeSlot> result = new ArrayList<>();

        for (TimeSlot slot1 : list1) {
            for (TimeSlot slot2 : list2) {
                LocalDateTime maxStart = slot1.getStart().isAfter(slot2.getStart()) ? slot1.getStart() : slot2.getStart();
                LocalDateTime minEnd = slot1.getEnd().isBefore(slot2.getEnd()) ? slot1.getEnd() : slot2.getEnd();

                if (!maxStart.isAfter(minEnd)) {
                    result.add(new TimeSlot(maxStart, minEnd));
                }
            }
        }
        return result;
    }

    public String generateInviteLink(int eventId, String botUsername) {
        String code = generateRandomCode(8);
        dbManager.setInviteCode(eventId, code);
        return "https://t.me/" + botUsername + "?start=join_" + code;
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public Integer getEventIdByCode(String code) {
        return dbManager.getEventIdByCode(code);
    }

    public String getCreatorId(int eventId) {
        return dbManager.getCreatorIdByEventId(eventId);
    }

    public String getEventName(int eventId) {
        return dbManager.getEventName(eventId);
    }

    public List<Long> getParticipants(int eventId) {
        return dbManager.getParticipants(eventId);
    }

}