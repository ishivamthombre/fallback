package com.blockCard.fallback;

import ai.active.fulfillment.webhook.data.request.MorfeusWebhookRequest;
import ai.active.fulfillment.webhook.data.response.MorfeusWebhookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ValidationController {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  BlockCardServiceClass blockCard;

  @Autowired
  RemittanceStatusService remittance;








  //-----------------------------------------------------------------Node Fallback Validation APIs------------------------------------------------------------------------------//


  @PostMapping(path = "/card/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse validateCard(@RequestBody(required = true) String body,
                                         @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateCardInput(request);
    //morfeusWebhookResponse.setStatus(Status.SUCCESS);
//    System.out.println(body);

    return morfeusWebhookResponse;
  }


  @PostMapping(path = "/block/temp/permanent", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse typeOfBlock(@RequestBody(required = true) String body,
                                         @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateBlockType(request);
   System.out.println(body);
    return morfeusWebhookResponse;


  }

  @PostMapping(path = "/taking_confirmation/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse confirmation(@RequestBody(required = true) String body,
                                             @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    //System.out.println(body);

    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateTakingConfirmationInput(request);
    //morfeusWebhookResponse.setStatus(Status.SUCCESS);


    return morfeusWebhookResponse;
  }






  //---------------------------------DTMF NODE VALIDATION APIs------------------------------------------------------------------------------//

  @PostMapping(path = "/card/DTMF/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse validateCardDtmf(@RequestBody(required = true) String body,
                                             @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateCardDtmfInput(request);
   // System.out.println(body);
    return morfeusWebhookResponse;
  }

  @PostMapping(path = "/temporary/permanent/DTMF_node2/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse validateBlockTypeDtmf(@RequestBody(required = true) String body,
                                                 @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateBlockTypeDtmfInput(request);
   // System.out.println(body);
    return morfeusWebhookResponse;
  }

  @PostMapping(path = "/takingConfirmation/DTMF_TakingConfirmation/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse validateTakingConfirmationDtmf(@RequestBody(required = true) String body,
                                                      @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  blockCard.validateTakingConfirmationDtmfInput(request);
    //System.out.println(body);
    return morfeusWebhookResponse;
  }


 //--------------------------------------------------------Remittance status enquiry APIs----------------------------------------------------------------

  @PostMapping(path = "/remittance/referenceNumber/validation", consumes = "application/json", produces = "application/json")
  public MorfeusWebhookResponse validateReferenceNumber(@RequestBody(required = true) String body,
                                                               @RequestHeader(name = "X-Hub-Signature", required = true) String signature, HttpServletResponse response) throws Exception {
    MorfeusWebhookRequest request = objectMapper.readValue(body, MorfeusWebhookRequest.class);
    MorfeusWebhookResponse morfeusWebhookResponse =  remittance.validatingReferenceNumberInput(request);
    //System.out.println(body);
    return morfeusWebhookResponse;
  }









}
