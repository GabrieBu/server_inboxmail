package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.*;

public class Server{
    static int currentIdMail;

    private String readConfig() {
        String line = "-1";
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Storage/config.txt"))) {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return line;
    }

    public Server(){
        currentIdMail = Integer.parseInt(readConfig());
    }

    public static class Mail{
        private final int idMail;
        private String from;
        private List<String> to;
        private String subject;
        private String body;
        private final LocalDateTime date_time;

        // to delete
        public Mail() {
            idMail = -1;
            date_time = LocalDateTime.now();
        }

        public Mail(String from, List<String> to, String subject, String body) {
            this.idMail = currentIdMail;
            this.from = from;
            this.to = to; //to_check
            this.subject = subject;
            this.body = body;
            this.date_time = LocalDateTime.now();
            currentIdMail++;
        }

        public int getId() {
            return idMail;
        }

        public String getFrom() {
            return from;
        }

        public List<String> getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public LocalDateTime getDate() {
            return date_time;
        }

        public String getDateFormatted() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return date_time.format(formatter);
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public void setTo(List<String> to) {
            this.to = to; //to_check
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setBody(String body) {
            this.body = body;
        }
    } //TO CHECK WHETHER PUBLIC IS CORRECT
}
