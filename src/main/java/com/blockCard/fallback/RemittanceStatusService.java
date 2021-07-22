package com.blockCard.fallback;

import ai.active.fulfillment.webhook.data.request.MorfeusWebhookRequest;
import ai.active.fulfillment.webhook.data.response.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
public class RemittanceStatusService {



  public MorfeusWebhookResponse validatingReferenceNumberInput(MorfeusWebhookRequest request) {
    MorfeusWebhookResponse morfeusWebhookResponse= new MorfeusWebhookResponse();
    String reference_number= request.getWorkflowParams().getRequestVariables().get("banking.reference-number");

    if(reference_number!=null&&(reference_number.equalsIgnoreCase("CBC2001271726591051")||reference_number.equalsIgnoreCase("CBC2001271726545852")||reference_number.equalsIgnoreCase("CBC3001471726547364")||reference_number.equalsIgnoreCase("CBC2652341326526493")||reference_number.equalsIgnoreCase("CBC2462132356532347"))
    ){
      morfeusWebhookResponse.setStatus(Status.SUCCESS);
      return morfeusWebhookResponse;
    }

    else{

      WorkflowValidationResponse workflowValidationResponse=new WorkflowValidationResponse.Builder(Status.SUCCESS).build();

      if(request.getWorkflowParams().getWorkflowVariables().containsKey("WrongAttemptsCount")){

        String wrong_input_count=request.getWorkflowParams().getWorkflowVariables().get("WrongAttemptsCount");

        if(wrong_input_count.equalsIgnoreCase("1")){
          Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
          workflowVariables.put("WrongAttemptsCount", "2");
          workflowValidationResponse.setWorkflowVariables(workflowVariables);
          workflowValidationResponse.setStatus(Status.FAILED);

          String errorMessage="My apologies but, you've again given an invalid Reference number";
          TextMessage textMessage = new TextMessage();
          textMessage.setContent(errorMessage);
          textMessage.setType("text");

          String errorMessage1="Let's try again! please enter the reference number.";
          TextMessage textMessage1 = new TextMessage();
          textMessage1.setContent(errorMessage1);
          textMessage1.setType("text");

          workflowValidationResponse.setMessages(Arrays.asList(textMessage, textMessage1));
        }

        else if(wrong_input_count.equalsIgnoreCase("2")){

          Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
          workflowVariables.put("WrongAttemptsCount", "3");
          workflowValidationResponse.setWorkflowVariables(workflowVariables);
          String errorMessage="I am extremely sorry. You've given three invalid reference numbers consecutively.  ";
          TextMessage textMessage = new TextMessage();
          textMessage.setContent(errorMessage);
          textMessage.setType("text");
          workflowValidationResponse.setMessages(Arrays.asList(textMessage));
        }

      }
      else {
        Map<String, String> workflowVariables = request.getWorkflowParams().getWorkflowVariables();
        workflowVariables.put("WrongAttemptsCount", "1");
        workflowValidationResponse.setWorkflowVariables(workflowVariables);
        workflowValidationResponse.setStatus(Status.FAILED);

        String errorMessage="Sorry, but this Reference number is invalid.";
        TextMessage textMessage = new TextMessage();
        textMessage.setContent(errorMessage);
        textMessage.setType("text");

        String errorMessage1="Let's try again! please enter the reference number.";
        TextMessage textMessage1 = new TextMessage();
        textMessage1.setContent(errorMessage1);
        textMessage1.setType("text");

        workflowValidationResponse.setMessages(Arrays.asList(textMessage, textMessage1));
      }
      return workflowValidationResponse;
    }

  }

}

