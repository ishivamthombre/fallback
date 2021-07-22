package com.blockCard.fallback;

import ai.active.fulfillment.webhook.data.request.MorfeusWebhookRequest;
import ai.active.fulfillment.webhook.data.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class Controller {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  BlockCardServiceClass blockCard;


  /////////////// taking confirmation node message diplaying  API.
  @PostMapping(path = "/tempBlock/permanentBlock/definition", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse blockTypeMessage(@RequestBody(required = true) String body,
                                                    @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.blockTypeInput(request);

    return morfeusWebhookResponse;
  }

  /////// taking confirmation node message diplaying API
  @PostMapping(path = "/TakingConfirmation/definition", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse TakingConfirmationMessage(@RequestBody(required = true) String body,
                                             @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.areYouSureMessage(request);

    //  System.out.println(body);

    return morfeusWebhookResponse;
  }

  /////// taking confirmation dtmf node message message diplaying api
  @PostMapping(path = "/TakingConfirmation/DTMF/definition", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse TakingConfirmationDTMFMessage(@RequestBody(required = true) String body,
                                                          @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.areYouSureDTMFMessage(request);

    //  System.out.println(body);

    return morfeusWebhookResponse;
  }


  /////////////// the card is successfully blocked message displaying API.
  @PostMapping(path = "/message_node/definition", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse confirmationMessage(@RequestBody(required = true) String body,
                                                    @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.displaySuccessfullBlockMessage(request);

    //  System.out.println(body);

    return morfeusWebhookResponse;
  }



  @PostMapping(path = "/fund/available", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse selectingCardValidation(@RequestBody(required = true) String body,
                                                @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Throwable{
//    List<Object> ListOfButtons= new ArrayList<>();
//
//    Button button = new Button();
//    button.setTitle("1. Signature card ending with 4785");
//    button.setIntent("txn-productclosure");
//    button.setPayload("{\"data\":{\"banking_product_card_number\":\"4785\"}, \"intent\": \"txn-productclosure\"}");
//    button.setType("postback");
//    ListOfButtons.add(button);
//
//    Button button1 = new Button();
//    button1.setTitle("2. Platinum Card ending with 6587");
//    button1.setIntent("txn-productclosure");
//    button1.setPayload("{\"data\":{\"banking_product_card_number\":\"6587\"}, \"intent\": \"txn-productclosure\"}");
//    button1.setType("postback");
//    ListOfButtons.add(button1);
//
//
//    Content content = new Content();
//    content.setTitle("Select the card.");
//    content.setButtons(ListOfButtons);
//    AbstractMessage buttonMessage= new ButtonMessage();
//    buttonMessage.setType("button");
//    ((ButtonMessage) buttonMessage).setContent(content);

   // MorfeusWebhookResponse morfeusWebhookResponse = new MorfeusWebhookResponse();
   // morfeusWebhookResponse.setMessages(Arrays.asList(buttonMessage));

    //System.out.println(body);
    return null;
  }
}
