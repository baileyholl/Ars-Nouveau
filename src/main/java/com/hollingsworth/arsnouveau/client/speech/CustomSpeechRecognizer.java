package com.hollingsworth.arsnouveau.client.speech;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.IOException;

public class CustomSpeechRecognizer extends StreamSpeechRecognizer {

    public CustomSpeechRecognizer(Configuration configuration) throws IOException {
        super(configuration);
    }

    public void cancelRecognition() {
        context.setSpeechSource(null);
    }
}