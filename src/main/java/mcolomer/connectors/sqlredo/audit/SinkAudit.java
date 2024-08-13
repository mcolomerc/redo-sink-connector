package mcolomer.connectors.sqlredo.audit;

import java.sql.Timestamp;

public class SinkAudit {
    String sentence;
    Timestamp sourceTimestamp;
    Timestamp destinationTimestamp;
    long latency; //ms

    public SinkAudit(String sentence, Timestamp sourceTimestamp, Timestamp destinationTimestamp,  long latency) {
        this.sentence = sentence;
        this.sourceTimestamp = sourceTimestamp;
        this.destinationTimestamp = destinationTimestamp;
        this.latency = latency;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Timestamp getDestinationTimestamp() {
        return destinationTimestamp;
    }

    public void setDestinationTimestamp(Timestamp destinationTimestamp) {
        this.destinationTimestamp = destinationTimestamp;
    }

    @Override
    public String toString() {
        return "SinkAudit{" +
                "sentence='" + sentence + '\'' +
                ", sourceTimestamp=" + sourceTimestamp +
                ", destinationTimestamp=" + destinationTimestamp +
                ", latency=" + latency +
                '}';
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }


    public Timestamp getSourceTimestamp() {
        return sourceTimestamp;
    }

    public void setSourceTimestamp(Timestamp sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }
}
