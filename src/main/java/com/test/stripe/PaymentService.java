package com.test.stripe;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import org.springframework.util.StringUtils;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/***
 * https://stripe.com/docs/api/java
 */
@Named
public class PaymentService {

    public static Map<String,String> customerMap = new HashMap<String, String>();

    private void doPaymentOneTime(String token, String amount, String stripeEmail) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Stripe.apiKey = "sk_test_g30izId666d6tEDTl5GAm3cn";
        Map<String,Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("source", token);
        chargeParams.put("currency", "usd");
        chargeParams.put("description", "Charge for test@example.com");
        if(stripeEmail != null)
            chargeParams.put("receipt_email", stripeEmail);         //https://stripe.com/blog/email-receipts (The emails will only be sent for payments in the live environment.)
        Charge charge = Charge.create(chargeParams);
        System.out.println(charge.toString());
    }

    public void doPaymentAndSaveCustomer(String token, String amount, String stripeEmail, String saveuser) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        amount = "" + (Integer.parseInt(amount) * 100);       // convert to cents
        if(StringUtils.isEmpty(saveuser)) {
            System.out.println("Doing one time payment");
            doPaymentOneTime(token, amount, stripeEmail);
            return;
        }
        System.out.println("Doing payment and saving customer");
        Stripe.apiKey = "sk_test_g30izId666d6tEDTl5GAm3cn";
        Map<String,Object> customerParams = new HashMap<String, Object>();
        customerParams.put("source", token);
        customerParams.put("description", stripeEmail);
        Customer savedCustomer = Customer.create(customerParams);

        Map<String,Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", savedCustomer.getId());
        chargeParams.put("description", "Charge for test@example.com");
        if(stripeEmail != null)
            chargeParams.put("receipt_email", stripeEmail);         //https://stripe.com/blog/email-receipts (The emails will only be sent for payments in the live environment.)
        Charge charge = Charge.create(chargeParams);
        System.out.println(charge.toString());
        // save customer in database,
        customerMap.put(stripeEmail, savedCustomer.getId());
    }

    public String doPaymentByCustomer(String amount, String customerEmail) throws Exception {
        amount = "" + (Integer.parseInt(amount) * 100);       // convert to cents
        String customerId = customerMap.get(customerEmail);
        if(customerId == null) {
            return ("User not found, Payment Failed");
        }
        Map<String,Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customerId);
        chargeParams.put("description", "Charge for " + customerEmail);

        try {
            Charge charge = Charge.create(chargeParams);
        } catch (Exception e) {
            return e.getMessage() + "Payment Failed";
        }
        return "Payment successful";
    }
}
