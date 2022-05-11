package com.blockCard.fallback;

import ai.active.fulfillment.webhook.data.request.MorfeusWebhookRequest;
import ai.active.fulfillment.webhook.data.request.NlpV1;
import ai.active.fulfillment.webhook.data.response.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class BlockCardServiceClass {

  AbstractMessage textMessage = new TextMessage();

  private static final String Hi = "hi";






//---------------------------------------------fallback service methods for other nodes-------------------------------------------------------
  public MorfeusWebhookResponse validateCardInput(MorfeusWebhookRequest request) {
    String card_number= request.getWorkflowParams().getRequestVariables().get("banking_product_card_number");
    String card_name=request.getWorkflowParams().getRequestVariables().get("banking_product_name");

    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();

    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    if((card_number!=null&&(card_number.equalsIgnoreCase("4785")||card_number.equalsIgnoreCase("6587")))||
      (card_name!=null&&(card_name.equalsIgnoreCase("signature")||card_name.equalsIgnoreCase("platinum"))))
    {
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }

    else{
      WorkflowValidationResponse workflowValidationResponse = new WorkflowValidationResponse.Builder(Status.SUCCESS).build();
      if(request.getWorkflowParams().getWorkflowVariables().containsKey("countOfWrongAttemptsForCard")){
        Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
        // text message: you have selected the wrong card twice, please enter the option from your keyPad.
        String errorMessage;
        if(langCode.equalsIgnoreCase("en")) {
          errorMessage = "Sorry, you have selected the wrong card twice. Now, please enter the option from your phone's keypad.";
          textMessage.setSpeechResponse("My apologies. Still, I am unable to hear you clearly. I request you to please open your phone's keypad . ");
        }
        else{
          errorMessage = "क्षमा करें, आपने दो बार गलत कार्ड चुना है। अब, कृपया अपने फोन के कीपैड से विकल्प दर्ज करें।";
          textMessage.setSpeechResponse("माफ़ कीजिये, मैं अभी भी आपको स्पष्ट रूप से नहीं सुन पा रही हूं। मेरा आपसे अनुरोध है कि कृपया अपने फोन का कीपैड खोलें ");
        }
        ((TextMessage) textMessage).setContent(errorMessage);
        textMessage.setType("text");
        workflowValidationResponse.setMessages(Arrays.asList(textMessage));
        workflowVariables.put("nodeId", "DTMF_node1");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        return workflowValidationResponse;
      }
      else {

        Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
        workflowVariables.put("countOfWrongAttemptsForCard", "1");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        workflowValidationResponse.setStatus(Status.FAILED);


        // text message: you have selected the wrong card. please select form the given options.
        String errorMessage;
        if(langCode.equalsIgnoreCase("en")) {
          errorMessage = "Sorry, you have given a wrong input. Please, try again.";
          textMessage.setSpeechResponse("Sorry, I am not able to hear you properly. Can you speak slowly like. Signature Card. or, Card ending with 8 6 5 4.");
        }
        else{
          errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया पुन: प्रयास करें।";
          textMessage.setSpeechResponse("क्षमा करें, मैं आपको ठीक से सुन नहीं पा रही हूं। कृपया धीरे धीरे दोबारा बोलें। जैस।  सिग्नेचर कार्ड। या, 8 6 5 4 से समाप्त होने वाला कार्ड।");
        }
        ((TextMessage) textMessage).setContent(errorMessage);
        textMessage.setType("text");

        List<Object> ListOfButtons1= new ArrayList<>();

        Button button = new Button();
        if(langCode.equalsIgnoreCase("en")) {
          button.setTitle("select");
        }
        else{
          button.setTitle("4785 के साथ समाप्त होने वाला सिग्नेचर कार्ड");
        }
        button.setIntent("txn-productclosure");
        //button.setPayload("{\"data\":{\"banking_product_card_number\":\"4785\"}, \"intent\": \"txn-productclosure\"}");
        button.setPayload("{\"data\":{\"banking_product_card_number\":\"4785\",\"banking_product_name\": \"signature\"}, \"intent\": \"txn-productclosure\"}");
        button.setType("postback");
        ListOfButtons1.add(button);

        List<Object> ListOfButtons2= new ArrayList<>();
        Button button1 = new Button();
        if(langCode.equalsIgnoreCase("en")){
          button1.setTitle("Platinum Card ending with 6587");
        }
        else{
          button1.setTitle("प्लेटिनम कार्ड जो 6587 के साथ समाप्त होता है");
        }

        button1.setIntent("txn-productclosure");
      //  button1.setPayload("{\"data\":{\"banking_product_card_number\":\"6587\"}, \"intent\": \"txn-productclosure\"}");
        button1.setPayload("{\"data\":{\"banking_product_card_number\":\"6587\",\"banking_product_name\": \"platinum\"}, \"intent\": \"txn-productclosure\"}");
        button1.setType("postback");
        ListOfButtons2.add(button1);


        ListContent listContent = new ListContent();
        List<Content> contentList = new ArrayList<>();
        Content content1 =  new Content();
        if(langCode.equalsIgnoreCase("en")){
          content1.setTitle("Signature - xxxx 4785");
          content1.setButtons(ListOfButtons1);
        }
        else{
          content1.setTitle("आप किस कार्ड को ब्लॉक करना चाहते हैं?");
        }

        Content content2 =  new Content();
        if(langCode.equalsIgnoreCase("en")){
          content2.setTitle("Platinum - xxxx 6587");
          content2.setButtons(ListOfButtons1);
        }
        else{
          content2.setTitle("आप किस कार्ड को ब्लॉक करना चाहते हैं?");
        }

        ListMessage listMessage= new ListMessage();
        if(langCode.equalsIgnoreCase("en")){
          listMessage.setSpeechResponse("Please let me know, if you wish to block, your Signature card ending with 4 7 8 5, or your Platinum Card ending with 6 5 8 7");
        }
        else{
          listMessage.setSpeechResponse("कृपया मुझे बतायें कि आप किस कार्ड को ब्लॉक करना चाहते हैं।  4 7 8 5 के साथ समाप्त होने वाला सिग्नेचर कार्ड। या। प्लेटिनम कार्ड जो 6 5 8 7 के साथ समाप्त होता है?");
        }
        listMessage.setType("list");
        contentList.add(content1);
        contentList.add(content2);
        listContent.setList(contentList);
        listMessage.setContent(listContent);
        workflowValidationResponse.setMessages(Arrays.asList(textMessage,listMessage));
        return workflowValidationResponse;
      }

    }
  }

  public MorfeusWebhookResponse validateBlockType(MorfeusWebhookRequest request) {
    String block_type= request.getWorkflowParams().getWorkflowVariables().get("banking.transaction-description");
    String block_type1=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
    String block_type2=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description_Start");
    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();
    if(block_type!=null&&(block_type.equalsIgnoreCase("temporary")||block_type.equalsIgnoreCase("permanent"))){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else if(block_type1!=null&&(block_type1.equalsIgnoreCase("temporary")||block_type1.equalsIgnoreCase("permanent"))){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else if(block_type2!=null&&(block_type2.equalsIgnoreCase("temporary")||block_type2.equalsIgnoreCase("permanent"))){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else{
      WorkflowValidationResponse workflowValidationResponse = new WorkflowValidationResponse.Builder(Status.SUCCESS).build();
      if(request.getWorkflowParams().getWorkflowVariables().containsKey("countOfWrongAttemptsForBlockType")){
        Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
        // text message: you have selected the wrong card twice, please enter the option from your keyPad.
        String errorMessage;
        if(langCode.equalsIgnoreCase("en")) {
          errorMessage = "Sorry, you have again given a wrong input. Now, please enter the option from your phone's keypad.";
          textMessage.setSpeechResponse("My apologies. Still, I am unable to hear you clearly. I request you to please open your phone's keypad . ");
        }
        else{
          errorMessage = "क्षमा करें, आपने फिर से गलत इनपुट दिया है। अब, कृपया अपने फोन के कीपैड से विकल्प दर्ज करें।";
          textMessage.setSpeechResponse("माफ़ कीजिये, मैं अभी भी आपको स्पष्ट रूप से नहीं सुन पा रही हूं। मेरा आपसे अनुरोध है कि कृपया अपने फोन का कीपैड खोलें ");
        }
        ((TextMessage) textMessage).setContent(errorMessage);

        textMessage.setType("text");

        workflowValidationResponse.setMessages(Arrays.asList(textMessage));

        workflowVariables.put("nodeId1", "DTMF_node2");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        return workflowValidationResponse;
      }
      else {

        Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
        workflowVariables.put("countOfWrongAttemptsForBlockType", "1");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        workflowValidationResponse.setStatus(Status.FAILED);
        String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
        String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");

        // if the a direct utterance just has the card number
        if(card_name==null){
          if(card_number.equalsIgnoreCase("4785"))
            card_name="signature";
          else
            card_name="platinum";
        }

        String errorMessage;
        if(langCode.equalsIgnoreCase("en")){
          errorMessage = "Sorry, you have given a wrong input. Please, try again.";
          textMessage.setSpeechResponse("Sorry, I am unable to hear you properly. Can you speak slowly like. permanently. or. temporarily.");
        }
        else{
          errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया पुन: प्रयास करें।";
          textMessage.setSpeechResponse("क्षमा करें, मैं आपको ठीक से सुन नहीं पा रही हूं। कृपया धीरे धीरे दोबारा बोलें। जैस। अस्थायी ब्लॉक। या। स्थायी ब्लॉक।");
        }
        ((TextMessage) textMessage).setContent(errorMessage);

        textMessage.setType("text");

        List<Object> ListOfButtons= new ArrayList<>();

        Button button = new Button();
        Button button1 = new Button();
        Content content = new Content();
        if(langCode.equalsIgnoreCase("en")){
          button.setTitle("Temporary block");
          button1.setTitle("Permanent block");
          content.setTitle("Temporary or a permanent block?");
        }
        else{
          button.setTitle("अस्थायी ब्लॉक");
          button1.setTitle("स्थायी ब्लॉक");
          content.setTitle("आप अपने "+card_name+" कार्ड पर किस प्रकार का ब्लॉक लगाना पसंद करेंग, अस्थायी ब्लॉक या स्थायी ब्लॉक?");
        }

        button.setIntent("txn-productclosure");
        button.setPayload("{\"data\":{\"banking_transaction_description\":\"Temporary\"}, \"intent\": \"txn-productclosure\"}");
        button.setType("postback");
        ListOfButtons.add(button);

        button1.setIntent("txn-productclosure");
        button1.setPayload("{\"data\":{\"banking_transaction_description\":\"Permanent\"}, \"intent\": \"txn-productclosure\"}");
        button1.setType("postback");
        ListOfButtons.add(button1);

        content.setButtons(ListOfButtons);
        AbstractMessage buttonMessage= new ButtonMessage();
        buttonMessage.setType("button");
        ((ButtonMessage) buttonMessage).setContent(content);

        if(langCode.equalsIgnoreCase("en")) {
          buttonMessage.setSpeechResponse("Please let me know, if you want to block your " + card_name + " card temporarily or permanently?");
        }
        else {
          buttonMessage.setSpeechResponse("कृपया मुझे बताएं कि आप अपने "+card_name+" कार्ड पर किस प्रकार का ब्लॉक लगाना पसंद करेंगे। अस्थायी ब्लॉक, या, स्थायी ब्लॉक?");
        }
        workflowValidationResponse.setMessages(Arrays.asList(textMessage,buttonMessage));
        return workflowValidationResponse;

      }

    }

  }


  public MorfeusWebhookResponse validateTakingConfirmationInput(MorfeusWebhookRequest request) {
    String ccdEntity= request.getWorkflowParams().getRequestVariables().get("ccdEntity");
    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();

    if(ccdEntity!=null&&(ccdEntity.equalsIgnoreCase("sys.confirmation")||ccdEntity.equalsIgnoreCase("sys.cancellation")||ccdEntity.equalsIgnoreCase("sys.negation"))
      ){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else{


      WorkflowValidationResponse workflowValidationResponse=new WorkflowValidationResponse.Builder(Status.SUCCESS).build();


      if(request.getWorkflowParams().getWorkflowVariables().containsKey("countOfWrongAttemptsForTakingConfimation")){
        Map<String,String> workflowVariables= request.getWorkflowParams().getWorkflowVariables();
        workflowVariables.put("nodeId2","DTMF_TakingConfirmation");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        String errorMessage;
        if(langCode.equalsIgnoreCase("en")){
          errorMessage = "Sorry, you have again given a wrong input. Now, please enter the option from your phone's keypad.";
          textMessage.setSpeechResponse("My apologies. Still, I am unable to hear you clearly. I request you to please open your phone's keypad . ");
        }
       else{
          errorMessage = "क्षमा करें, आपने फिर से गलत इनपुट दिया है। अब, कृपया अपने फोन के कीपैड से विकल्प दर्ज करें।";
          textMessage.setSpeechResponse("माफ़ कीजिये, मैं अभी भी आपको स्पष्ट रूप से नहीं सुन पा रही हूं। मेरा आपसे अनुरोध है कि कृपया अपने फोन का कीपैड खोलें");
        }
        ((TextMessage) textMessage).setContent(errorMessage);
        textMessage.setType("text");
        workflowValidationResponse.setMessages(Arrays.asList(textMessage));
        return workflowValidationResponse;
      }

      else{
        Map<String,String> workflowVariables= request.getWorkflowParams().getWorkflowVariables();
        workflowVariables.put("countOfWrongAttemptsForTakingConfimation","1");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        workflowValidationResponse.setStatus(Status.FAILED);

        String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
        String block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
        String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");
        // for handling direct utterance
        if(card_number==null){
          if(card_name.equalsIgnoreCase("signature"))
            card_number="4785";
          else
            card_number="6587";
        }

        if(card_name==null){
          if(card_number.equalsIgnoreCase("4785"))
            card_name="signature";
          else
            card_name="platinum";
        }

        if(block_type.equalsIgnoreCase("temporary")){
          if(langCode.equalsIgnoreCase("en")) {
            block_type = "temporarily";
          }
          else{
            block_type = "अस्थायी";
          }
        }
        else{
          if(langCode.equalsIgnoreCase("en")){
            block_type="permanently";
          }
          else{
            block_type = "स्थायी";
          }
        }

        String errorMessage;
        Button button = new Button();
        Button button1 = new Button();
        Content content = new Content();
        if(langCode.equalsIgnoreCase("en")){
          errorMessage = "Sorry, you have given a wrong input. Please, try again.";
          textMessage.setSpeechResponse("Sorry, I am unable to hear you properly. Can you please speak slowly like. yes. or, go ahead.");
          button.setTitle("Confirm");
          button1.setTitle("Cancel");
          content.setTitle("Please confirm, if you wish to "+block_type+" block your "+card_name+" card ending with xxxx"+card_number+"?");
        }
        else{
          errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया, पुन: प्रयास करें।";
          textMessage.setSpeechResponse("क्षमा करें, मैं आपको ठीक से सुन नहीं पा रही हूं। कृपया धीरे धीरे दोबारा बोलें। जैस। हाँ। या। नही।");
          button.setTitle("हाँ, कर सकती हैं ");
          button1.setTitle("नहीं, धन्यवाद");
          content.setTitle("क्या मैं xxxx"+card_number+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");
        }

        ((TextMessage) textMessage).setContent(errorMessage);
        textMessage.setType("text");
        List<Object> ListOfButtons= new ArrayList<>();

        button.setIntent("txn-productclosure");
        button.setPayload("{\"data\":{\"ccdEntity\":\"sys.confirmation\"}, \"intent\": \"txn-productclosure\"}");
        button.setType("postback");
        ListOfButtons.add(button);

        button1.setIntent("txn-productclosure");
        button1.setPayload("{\"data\":{\"ccdEntity\":\"sys.cancellation\"}, \"intent\": \"txn-productclosure\"}");
        button1.setType("postback");
        ListOfButtons.add(button1);

        content.setButtons(ListOfButtons);

        AbstractMessage buttonMessage= new ButtonMessage();
        //buttonMessage.setSpeechResponse("");
        buttonMessage.setType("button");
        ((ButtonMessage) buttonMessage).setContent(content);
        if(langCode.equalsIgnoreCase("en")){
          buttonMessage.setSpeechResponse("Please let me know, if i should go ahead and block your, "+card_name+" card,"+block_type+"?");
        }
        else{
          buttonMessage.setSpeechResponse("कृपया मुझे बताएं, क्या मैं "+card_number.replaceAll(".(?=.)", "$0 ")+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");

        }
        workflowValidationResponse.setMessages(Arrays.asList(textMessage,buttonMessage));
        return workflowValidationResponse;
      }

    }

  }












//---------------------------------------------------------------DTMF NODE INPUT VALIDATION SERVICE METHODS----------------------------------------------//



  public MorfeusWebhookResponse validateCardDtmfInput(MorfeusWebhookRequest request) {
    WorkflowValidationResponse workflowValidationResponse = new WorkflowValidationResponse.Builder(Status.SUCCESS).build();
    String card_number=request.getWorkflowParams().getRequestVariables().get("banking_product_card_number");
    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();

    if(card_number!=null&&(card_number.equalsIgnoreCase("4785")||card_number.equalsIgnoreCase("6587"))){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else{
      String errorMessage;
      if(langCode.equalsIgnoreCase("en")) {
        errorMessage = "Sorry, you have given a wrong input. Please, try again.";
        textMessage.setSpeechResponse("Sorry but that's a wrong input. lets try again. ");
      }
      else{
        errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया पुन: प्रयास करें।";
        textMessage.setSpeechResponse("क्षमा करें, लेकिन यह गलत इनपुट है। फिर से कोशिश करते है।");
      }
      ((TextMessage) textMessage).setContent(errorMessage);
      textMessage.setType("text");


      List<Object> ListOfButtons= new ArrayList<>();

      Button button = new Button();
      if(langCode.equalsIgnoreCase("en")) {
        button.setTitle("1. Signature card ending with 4785");
      }
      else{
        button.setTitle("1. 4785 के साथ समाप्त होने वाला सिग्नेचर कार्ड");
      }
      button.setIntent("txn-productclosure");
     // button.setPayload("{\"data\":{\"banking_product_card_number\":\"4785\"}, \"intent\": \"txn-productclosure\"}");
      button.setPayload("{\"data\":{\"banking_product_card_number\":\"4785\",\"banking_product_name\": \"signature\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);

      Button button1 = new Button();
      if(langCode.equalsIgnoreCase("en")) {
        button1.setTitle("2. Platinum Card ending with 6587");
      }
      else{
        button1.setTitle("2. प्लेटिनम कार्ड जो 6587 के साथ समाप्त होता है");
      }
      button1.setIntent("txn-productclosure");
      //button1.setPayload("{\"data\":{\"banking_product_card_number\":\"6587\"}, \"intent\": \"txn-productclosure\"}");
      button1.setPayload("{\"data\":{\"banking_product_card_number\":\"6587\",\"banking_product_name\": \"platinum\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);


      Content content = new Content();
      if(langCode.equalsIgnoreCase("en")) {
        content.setTitle("Please, select from.");
      }
      else{
        content.setTitle("कृपया इन विकल्पों में से चुनें।");
      }
      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage= new ButtonMessage();
      //buttonMessage.setSpeechResponse("");
      buttonMessage.setType("button");
      if(langCode.equalsIgnoreCase("en")){
        buttonMessage.setSpeechResponse("please, press 1 to select your signature card ending with 4 7 8 5. or press 2 to select your platinum card ending with 6 5 8 7. followed by # key");
      }
      else{
        buttonMessage.setSpeechResponse("4 7 8 5 से समाप्त होने वाले अपने सिग्नेचर कार्ड का चयन करने के लिए कृपया 1 दबाएं। या। 6 5 8 7 से समाप्त होने वाले अपने प्लैटिनम कार्ड का चयन करने के लिए कृपया 2 दबाएं, और उसके बाद हैश दबाएं।");
      }

      ((ButtonMessage) buttonMessage).setContent(content);



      workflowValidationResponse.setMessages(Arrays.asList(textMessage,buttonMessage));
      workflowValidationResponse.setStatus(Status.FAILED);
      return workflowValidationResponse;
    }
  }

  public MorfeusWebhookResponse validateBlockTypeDtmfInput(MorfeusWebhookRequest request) {
    String block_type= request.getWorkflowParams().getRequestVariables().get("banking_transaction_description");
    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();
    if(block_type!=null&&(block_type.equalsIgnoreCase("temporary")||block_type.equalsIgnoreCase("permanent"))){
      MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else{
      Button button = new Button();
      Button button1 = new Button();
      Content content = new Content();
      String errorMessage;
      if(langCode.equalsIgnoreCase("en")){
        errorMessage = "Sorry, you have given a wrong input. Please, try again.";
        textMessage.setSpeechResponse("Sorry but that's a wrong input. lets try again. ");
        button.setTitle("1. Temporary block");
        button1.setTitle("2. Permanent block");
        content.setTitle("Temporary or a permanent block?");
      }
      else{
        errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया पुन: प्रयास करें।";
        textMessage.setSpeechResponse("क्षमा करें, लेकिन यह गलत इनपुट है। फिर से कोशिश करते है।");
        button.setTitle("1. अस्थायी ब्लॉक");
        button1.setTitle("2. स्थायी ब्लॉक");
        content.setTitle("अस्थायी ब्लॉक या स्थायी ब्लॉक?");
      }
      ((TextMessage) textMessage).setContent(errorMessage);
      textMessage.setType("text");

      List<Object> ListOfButtons= new ArrayList<>();

      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"banking_transaction_description\":\"Temporary\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);

      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"banking_transaction_description\":\"Permanent\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);

      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage= new ButtonMessage();
      //buttonMessage.setSpeechResponse("");
      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      if(langCode.equalsIgnoreCase("en")) {
        buttonMessage.setSpeechResponse("please,press 1 to opt for a temporary block. or press 2 to block your card, permanently.");
      }
      else{
        buttonMessage.setSpeechResponse("अस्थायी ब्लॉक। चुनने के लिए कृपया 1 दबाएं। या। स्थायी ब्लॉक। चुनने के लिए कृपया 2 दबाएं, उसके बाद हैश दबाएं।");
      }
      WorkflowValidationResponse workflowValidationResponse = new WorkflowValidationResponse.Builder(Status.FAILED).build();
      workflowValidationResponse.setMessages(Arrays.asList(textMessage,buttonMessage));
      return workflowValidationResponse;
    }
  }


  public MorfeusWebhookResponse validateTakingConfirmationDtmfInput(MorfeusWebhookRequest request) {
    String ccdEntity= request.getWorkflowParams().getRequestVariables().get("ccdEntity");
    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();
    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    if(ccdEntity!=null&&(ccdEntity.equalsIgnoreCase("sys.confirmation")||ccdEntity.equalsIgnoreCase("sys.cancellation")||ccdEntity.equalsIgnoreCase("sys.negation"))
    ){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }
    else{

      String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
      String block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
      String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");

      // for handling direct utterance
      if(card_number==null){
        if(card_name.equalsIgnoreCase("signature"))
          card_number="4785";
        else
          card_number="6587";
      }

      if(card_name==null){
        if(card_number.equalsIgnoreCase("4785"))
          card_name="signature";
        else
          card_name="platinum";
      }
      if(block_type.equalsIgnoreCase("temporary")){
        if(langCode.equalsIgnoreCase("en")) {
          block_type = "temporarily";
        }
        else{
          block_type = "अस्थायी";
        }
      }
      else{
        if(langCode.equalsIgnoreCase("en")){
          block_type="permanently";
        }
        else{
          block_type = "स्थायी";
        }
      }

      String errorMessage;
      Button button = new Button();
      Button button1 = new Button();
      Content content = new Content();
      if(langCode.equalsIgnoreCase("en")){
        errorMessage = "Sorry, you have given a wrong input. Please, try again.";
        textMessage.setSpeechResponse("Sorry but that seems like an invalid input. lets try again. ");
        button.setTitle("1. Confirm");
        button1.setTitle("2. Cancel");
        content.setTitle("Please confirm, if you wish to "+block_type+" block your "+card_name+" card ending with xxxx"+card_number+"?");
      }
      else{
        errorMessage = "क्षमा करें, आपने गलत इनपुट दिया है। कृपया पुन: प्रयास करें।";
        textMessage.setSpeechResponse("क्षमा करें, लेकिन यह एक अमान्य इनपुट की तरह लगता है। फिर से कोशिश करते है।");
        button.setTitle("1. हाँ, कर सकती हैं");
        button1.setTitle("2. नहीं, धन्यवाद");
        content.setTitle("क्या मैं xxxx"+card_number+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");
      }

      ((TextMessage) textMessage).setContent(errorMessage);
      textMessage.setType("text");

      List<Object> ListOfButtons= new ArrayList<>();

      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"ccdEntity\":\"sys.confirmation\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);

      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"ccdEntity\":\"sys.cancellation\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);

      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage= new ButtonMessage();
      //buttonMessage.setSpeechResponse("");
      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      if(langCode.equalsIgnoreCase("en")){
        buttonMessage.setSpeechResponse("please, press 1 followed by # key, to confirm a, "+block_type+" block on your, "+card_name+" card ending with "+card_number.replaceAll(".(?=.)", "$0 ")+"  , or,  press 2 followed by # key, to cancel your request.");
      }
      else{
        buttonMessage.setSpeechResponse("अपने "+card_name+" कार्ड पर "+block_type+" ब्लॉक लगाने के लिए कृपया 1 दबाए।  या। अपने अनुरोध को रद्द करने के लिए कृपया 2 दबाए, और अपना विकल्प दर्ज करने के बाद हैश दबाए।");
      }
      WorkflowValidationResponse workflowValidationResponse =new WorkflowValidationResponse.Builder(Status.FAILED).build();
      workflowValidationResponse.setMessages(Arrays.asList(textMessage,buttonMessage));
      return workflowValidationResponse;
    }
  }










  //---------------------------------------------Message node's card successfully blocked message-----------------------------------------//
  public MorfeusWebhookResponse displaySuccessfullBlockMessage(MorfeusWebhookRequest request) {


    WorkflowValidationResponse workflowValidationResponse = new WorkflowValidationResponse.Builder(Status.SUCCESS).build();


    Content content=new Content();
    content.setImage("https://i.ibb.co/GRzCZpp/success.gif");
    content.setTitle("Card is blocked successfully.");
    content.setSubtitle("Please save the reference id 5zMbyvHX for future reference.");

//    ImageMessage imageMessage=new ImageMessage();
//    imageMessage.setType("image");
//    imageMessage.setContent(content);

   // workflowValidationResponse.setMessages(Arrays.asList(imageMessage));

    CarouselMessage carouselMessage=new CarouselMessage();
    carouselMessage.setType("carousel");
    List<Content> ListOfcontents= new ArrayList<>();
    ListOfcontents.add(content);
    carouselMessage.setContent(ListOfcontents);
    workflowValidationResponse.setMessages(Arrays.asList(carouselMessage));
    return workflowValidationResponse;


  }

//    String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
//    String block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
//    String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");
//
//    NlpV1 nlpV1 = (NlpV1) request.getNlp();
//    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();
//
//    // for handling direct utterance
//    if(card_number==null){
//      if(card_name.equalsIgnoreCase("signature"))
//        card_number="4785";
//      else
//        card_number="6587";
//    }
//
//    if(card_name==null){
//      if(card_number.equalsIgnoreCase("4785"))
//        card_name="signature";
//      else
//        card_name="platinum";
//    }
//
//
//    if(langCode.equalsIgnoreCase("en")) {
//      String message = "Thanks! for your confirmation.";
//      TextMessage textMessage = new TextMessage();
//      textMessage.setContent(message);
//      textMessage.setType("text");
//      textMessage.setSpeechResponse("Thanks! for your confirmation.");
//
//      String message1 = "The request for a " + block_type + " block on your " + card_name + " card ending with xxxx" + card_number + " has been successfully processed.";
//      TextMessage textMessage1 = new TextMessage();
//      textMessage1.setContent(message1);
//      textMessage1.setType("text");
//      textMessage1.setSpeechResponse("As per your request, I have put a " + block_type + " block on your " + card_name + " card.");
//
//      TextMessage textMessage2 = new TextMessage();
//      textMessage2.setType("text");
//
//      if (block_type.equalsIgnoreCase("permanent")) {
//        String message2 = "You will be receiving a replacement card on your mailing address. i.e. 57 , Begum Sahib St, Greams Rd, Bengaluru";
//        textMessage2.setContent(message2);
//        textMessage2.setSpeechResponse("You will be receiving a replacement card on your mailing address. that is, 57 , Begum Sahib Street, Greams Road, Bengaluru.");
//      } else {
//        String message2 = "You can call us anytime and unblock your " + card_name + " card.";
//        textMessage2.setContent(message2);
//        textMessage2.setSpeechResponse("You can call us anytime and unblock your " + card_name + " card.");
//      }
//
//      String message3 = "How else can I help you? today.";
//      TextMessage textMessage3 = new TextMessage();
//      textMessage3.setType("text");
//      textMessage3.setContent(message3);
//      textMessage3.setSpeechResponse("Apart from this, is there anything at all that i can help you with, today?");
//      workflowValidationResponse.setMessages(Arrays.asList(textMessage, textMessage1, textMessage2, textMessage3));
//    }
//
//    else if(langCode.equalsIgnoreCase(Hi)){
//      String message = "आपकी पुष्टि के लिए, धन्यवाद!";
//      TextMessage textMessage = new TextMessage();
//      textMessage.setContent(message);
//      textMessage.setType("text");
//      textMessage.setSpeechResponse("आपकी पुष्टि के लिए, धन्यवाद!");
//
//      if (block_type.equalsIgnoreCase("temporary")) {
//        block_type = "अस्थायी";
//      } else {
//        block_type = "स्थायी";
//      }
//
//      String message1 = "xxxx"+card_number+" से समाप्त होने वाले आपके "+card_name+" को "+block_type+" रूप से ब्लॉक कर दिया गया है।";
//      TextMessage textMessage1 = new TextMessage();
//      textMessage1.setContent(message1);
//      textMessage1.setType("text");
//      textMessage1.setSpeechResponse("आपके अनुरोध के अनुसार, आपके "+card_name+" को "+block_type+" रूप से ब्लॉक कर दिया गया है।");
//
//      TextMessage textMessage2 = new TextMessage();
//      textMessage2.setType("text");
//
//      if (block_type.equalsIgnoreCase("स्थायी")) {
//        String message2 = "आपका रिप्लेसमेंट कार्ड आपके पते पर भेज दिया जाएगा। आपका रजिस्टर्ड पता: 57, बेगम साहिब स्ट्रीट, ग्रीम्स रोड, बेंगलुरु।";
//        textMessage2.setContent(message2);
//        textMessage2.setSpeechResponse("आपका रिप्लेसमेंट कार्ड आपके पते पर भेज दिया जाएगा। आपका रजिस्टर्ड पता, 57, बेगम साहिब स्ट्रीट, ग्रीम्स रोड, बेंगलुरु, है।");
//      } else {
//        String message2 = "आप अपने "+ card_name +" कार्ड को अनब्लॉक करने के लिए आप हमें कभी भी कॉल कर सकते हैं। ";
//        textMessage2.setContent(message2);
//        textMessage2.setSpeechResponse("आप अपने "+ card_name +" कार्ड को अनब्लॉक करने के लिए आप हमें कभी भी कॉल कर सकते हैं। ");
//      }
//
//      String message3 = "मैं आपकी और किस प्रकार से मदद कर सकती हूं?";
//      TextMessage textMessage3 = new TextMessage();
//      textMessage3.setType("text");
//      textMessage3.setContent(message3);
//      textMessage3.setSpeechResponse("इसके अलावा, मैं आपकी और किस प्रकार से मदद कर सकती हूं?");
//      workflowValidationResponse.setMessages(Arrays.asList(textMessage, textMessage1, textMessage2, textMessage3));
//
//    }
//
//    return workflowValidationResponse;
//  }

  public MorfeusWebhookResponse areYouSureMessage(MorfeusWebhookRequest request) {
    WorkflowValidationResponse workflowValidationResponse=new WorkflowValidationResponse.Builder(Status.SUCCESS).build();

    String card_name="";
    if(StringUtils.isNotEmpty(request.getWorkflowParams().getWorkflowVariables().get("banking_product_name"))){
      card_name = request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
    }else{
      card_name = request.getWorkflowParams().getWorkflowVariables().get("banking_product_name_Start");
    }
    if (!CollectionUtils.isEmpty(request.getWorkflowParams().getRequestVariables()) && StringUtils.isNotEmpty(
        request.getWorkflowParams().getRequestVariables().get("banking_product_name")) && !card_name.equalsIgnoreCase(
        request.getWorkflowParams().getRequestVariables().get("banking_product_name"))) {
      card_name = request.getWorkflowParams().getRequestVariables().get("banking_product_name");
    }

    String block_type ="";
    if(StringUtils.isNotEmpty(request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description"))){
      block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
    }
    else{
      block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description_Start");
    }
    if (!CollectionUtils.isEmpty(request.getWorkflowParams().getRequestVariables()) && StringUtils.isNotEmpty(
        request.getWorkflowParams().getRequestVariables().get("banking_transaction_description")) && !block_type.equalsIgnoreCase(
        request.getWorkflowParams().getRequestVariables().get("banking_transaction_description"))) {
      block_type = request.getWorkflowParams().getRequestVariables().get("banking_transaction_description");
    }


    String card_number="";
    if(StringUtils.isNotEmpty(request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number"))){
      card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");
    }
    else{
      card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number_Start");
    }
    if (!CollectionUtils.isEmpty(request.getWorkflowParams().getRequestVariables()) && StringUtils.isNotEmpty(
        request.getWorkflowParams().getRequestVariables().get("banking_product_card_number")) && !card_number.equalsIgnoreCase(
        request.getWorkflowParams().getRequestVariables().get("banking_product_card_number"))) {
      card_number = request.getWorkflowParams().getRequestVariables().get("banking_product_card_number");
    }

    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();


    // for handling direct utterance
    if(card_number==null){
      if(card_name.equalsIgnoreCase("signature"))
        card_number="4785";
      else
        card_number="6587";
    }

    if(card_name==null){
      if(card_number.equalsIgnoreCase("4785"))
        card_name="signature";
      else
        card_name="platinum";
    }

    List<Object> ListOfButtons= new ArrayList<>();

    if(langCode.equalsIgnoreCase("en")) {
      Button button = new Button();
      button.setTitle("Confirm");
      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"ccdEntity\":\"sys.confirmation\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);

      Button button1 = new Button();
      button1.setTitle("Cancel");
      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"ccdEntity\":\"sys.cancellation\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);

      if (block_type.equalsIgnoreCase("temporary")) {
        block_type = "temporarily";
      } else {
        block_type = "permanently";
      }

      Content content = new Content();
      content.setTitle("Please confirm, if you wish to " + block_type + " block your " + card_name + " card ending with xxxx" + card_number + "?");


      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage = new ButtonMessage();
      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      buttonMessage.setSpeechResponse("Please tell me, if i should go ahead and block your, " + card_name + " card," + block_type + "?");
      workflowValidationResponse.setMessages(Arrays.asList(buttonMessage));
    }

    else if(langCode.equalsIgnoreCase(Hi)){
      Button button = new Button();
      button.setTitle("हाँ, कर सकती हैं ");
      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"ccdEntity\":\"sys.confirmation\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);

      Button button1 = new Button();
      button1.setTitle("नहीं, धन्यवाद");
      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"ccdEntity\":\"sys.cancellation\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);

      if (block_type.equalsIgnoreCase("temporary")) {
        block_type = "अस्थायी";
      } else {
        block_type = "स्थायी";
      }

      Content content = new Content();
      content.setTitle("क्या मैं xxxx"+card_number+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");


      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage = new ButtonMessage();
      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      buttonMessage.setSpeechResponse("कृपया मुझे बताएं, क्या मैं "+card_number.replaceAll(".(?=.)", "$0 ")+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");
      workflowValidationResponse.setMessages(Arrays.asList(buttonMessage));

    }



    return workflowValidationResponse;
  }

  public MorfeusWebhookResponse areYouSureDTMFMessage(MorfeusWebhookRequest request) {

    NlpV1 nlpV1 = (NlpV1) request.getNlp();
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();
    String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
    String block_type=request.getWorkflowParams().getWorkflowVariables().get("banking_transaction_description");
    String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");


    // for handling direct utterance
    if(card_number==null){
      if(card_name.equalsIgnoreCase("signature"))
        card_number="4785";
      else
        card_number="6587";
    }

    if(card_name==null){
      if(card_number.equalsIgnoreCase("4785"))
        card_name="signature";
      else
        card_name="platinum";
    }

    List<Object> ListOfButtons= new ArrayList<>();

    Button button = new Button();
    Button button1 = new Button();
    Content content = new Content();
    if(langCode.equalsIgnoreCase("en")){
      button.setTitle("1. Confirm");
      button1.setTitle("2. Cancel");
      content.setTitle("Please confirm, if you wish to put a "+block_type+" block on your "+card_name+" card ending with xxxx"+card_number+"?");
    }
    else{
      button.setTitle("1. हाँ, कर सकती हैं");
      button1.setTitle("2. नहीं, धन्यवाद");
      if (block_type.equalsIgnoreCase("temporary")) {
        block_type = "अस्थायी";
      } else {
        block_type = "स्थायी";
      }
      content.setTitle("क्या मैं xxxx"+card_number+" से समाप्त होने वाले आपके "+card_name+" कार्ड को "+block_type+" रूप से ब्लॉक कर सकती हूं?");
    }

    button.setIntent("txn-productclosure");
    button.setPayload("{\"data\":{\"ccdEntity\":\"sys.confirmation\"}, \"intent\": \"txn-productclosure\"}");
    button.setType("postback");
    ListOfButtons.add(button);

    button1.setIntent("txn-productclosure");
    button1.setPayload("{\"data\":{\"ccdEntity\":\"sys.cancellation\"}, \"intent\": \"txn-productclosure\"}");
    button1.setType("postback");
    ListOfButtons.add(button1);

    content.setButtons(ListOfButtons);

    AbstractMessage buttonMessage= new ButtonMessage();
    //buttonMessage.setSpeechResponse("");
    buttonMessage.setType("button");
    ((ButtonMessage) buttonMessage).setContent(content);
    if(langCode.equalsIgnoreCase("en")){
      buttonMessage.setSpeechResponse("and, please, press 1 followed by # key, to confirm a, "+block_type+" block on your, "+card_name+" card ending with "+card_number.replaceAll(".(?=.)", "$0 ")+"  , or,  press 2 followed by # key, to cancel your request.");
    }
    else{
      if (block_type.equalsIgnoreCase("temporary")) {
        block_type = "अस्थायी";
      } else {
        block_type = "स्थायी";
      }

      buttonMessage.setSpeechResponse("और, अपने "+card_name+" कार्ड पर "+block_type+" ब्लॉक लगाने के लिए 1 दबाए।  या। अपने अनुरोध को रद्द करने के लिए 2 दबाए, और अपना विकल्प दर्ज करने के बाद हैश दबाए। ");
    }
    WorkflowValidationResponse workflowValidationResponse =new WorkflowValidationResponse.Builder(Status.FAILED).build();
    workflowValidationResponse.setMessages(Arrays.asList(buttonMessage));
    return workflowValidationResponse;
  }

  public MorfeusWebhookResponse blockTypeInput(MorfeusWebhookRequest request) {
    WorkflowValidationResponse workflowValidationResponse=new WorkflowValidationResponse.Builder(Status.SUCCESS).build();

    String card_name=request.getWorkflowParams().getWorkflowVariables().get("banking_product_name");
    String card_number=request.getWorkflowParams().getWorkflowVariables().get("banking_product_card_number");

    NlpV1 nlpV1 = (NlpV1) request.getNlp();
   // String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : "en";
    String langCode = nlpV1.getData().has("langCode") ? nlpV1.getData().get("langCode").asText() : request.getBot().getLanguageCode();


    //for handling direct uttrances with just the card number and no card name.
    if(card_name==null){
      if(card_number.equalsIgnoreCase("4785"))
        card_name="signature";
      else
        card_name="platinum";
    }

    List<Object> ListOfButtons= new ArrayList<>();
    Button button = new Button();
    Button button1 = new Button();
    Content content = new Content();

    if(langCode.equalsIgnoreCase("en")) {
      button.setTitle("Temporary block");
      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"banking_transaction_description\":\"Temporary\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);


      button1.setTitle("Permanent block");
      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"banking_transaction_description\":\"Permanent\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);


      content.setTitle("Temporary or a permanent block?");
      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage = new ButtonMessage();

      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      buttonMessage.setSpeechResponse("Please let me know, if you want to block your " + card_name + " card, temporarily or permanently?");
      workflowValidationResponse.setMessages(Arrays.asList(buttonMessage));
    }

    else if(langCode.equalsIgnoreCase(Hi)){

      button.setTitle("अस्थायी रूप से");
      button.setIntent("txn-productclosure");
      button.setPayload("{\"data\":{\"banking_transaction_description\":\"Temporary\"}, \"intent\": \"txn-productclosure\"}");
      button.setType("postback");
      ListOfButtons.add(button);


      button1.setTitle("स्थायी रूप से");
      button1.setIntent("txn-productclosure");
      button1.setPayload("{\"data\":{\"banking_transaction_description\":\"Permanent\"}, \"intent\": \"txn-productclosure\"}");
      button1.setType("postback");
      ListOfButtons.add(button1);


      content.setTitle("आप अपने "+card_name+" कार्ड को किस प्रकार से ब्लॉक करना चाहते हैं, अस्थायी रूप से या स्थायी रूप से?");
      content.setButtons(ListOfButtons);

      AbstractMessage buttonMessage = new ButtonMessage();

      buttonMessage.setType("button");
      ((ButtonMessage) buttonMessage).setContent(content);
      buttonMessage.setSpeechResponse("कृपया मुझे बताएं कि आप अपने "+card_name+" कार्ड को किस प्रकार से ब्लॉक करना चाहते हैं अस्थायी रूप से, या स्थायी रूप से?");
      workflowValidationResponse.setMessages(Arrays.asList(buttonMessage));



    }

    return workflowValidationResponse;
  }


  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
