package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Message;

public class MessageObject {

    String messageId,
            senderId,
            message;

    public MessageObject(String messageId, String senderId, String message) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }
}